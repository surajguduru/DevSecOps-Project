package com.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import com.example.imageeditor.ImageProcessor;

public class ImageEditorServer {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);

        // Serve the HTML page
        server.createContext("/", new StaticFileHandler("index.html"));

        // Handle image processing
        server.createContext("/process", new ProcessImageHandler());

        server.start();
        System.out.println("Image Editor server running on 0.0.0.0:8080");
        System.out.println("Accessible at http://localhost:8080 or http://YOUR_IP:8080");
        System.out.println("For Kubernetes/external access, ensure port 8080 is exposed");
    }

    static class StaticFileHandler implements HttpHandler {
        private final String filename;

        public StaticFileHandler(String filename) {
            this.filename = filename;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = "src/main/resources/" + filename;
            File file = new File(path);
            if (file.exists()) {
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, file.length());
                try (FileInputStream fis = new FileInputStream(file);
                     OutputStream os = exchange.getResponseBody()) {
                    fis.transferTo(os);
                }
            } else {
                String response = "File not found";
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }

    static class ProcessImageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            // Read the request body
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[1024];
            int length;
            while ((length = isr.read(buffer)) != -1) {
                sb.append(buffer, 0, length);
            }
            String requestBody = sb.toString();

            try {
                // Parse JSON
                String imageBase64 = extractStringValue(requestBody, "image");
                String editType = extractStringValue(requestBody, "editType");
                int brightnessPercent = 0;
                String brightnessStr = extractStringValue(requestBody, "percentage");
                if (brightnessStr != null) {
                    brightnessPercent = Integer.parseInt(brightnessStr);
                }

                if (imageBase64 == null || editType == null) {
                    sendError(exchange, "Invalid request");
                    return;
                }

                // Decode base64 to BufferedImage
                byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
                ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                BufferedImage inputImage = ImageIO.read(bais);
                if (inputImage == null) {
                    sendError(exchange, "Invalid image");
                    return;
                }

                // Process the image
                BufferedImage outputImage = null;
                switch (editType) {
                    case "grayscale":
                        outputImage = ImageProcessor.toGrayscale(inputImage);
                        break;
                    case "invert":
                        outputImage = ImageProcessor.invertRGB(inputImage);
                        break;
                    case "brightness":
                        outputImage = ImageProcessor.alterBrightness(inputImage, brightnessPercent);
                        break;
                    case "rotate90":
                        outputImage = ImageProcessor.rotate90(inputImage);
                        break;
                    case "rotate180":
                        outputImage = ImageProcessor.rotate180(inputImage);
                        break;
                    case "mirrorRight":
                        outputImage = ImageProcessor.mirrorRight(inputImage);
                        break;
                    case "mirrorBottom":
                        outputImage = ImageProcessor.mirrorBottom(inputImage);
                        break;
                    default:
                        sendError(exchange, "Unknown edit type");
                        return;
                }

                // Encode output image to base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(outputImage, "png", baos);
                String outputBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());

                // Send response
                String response = "{\"editedImage\":\"" + outputBase64 + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }

            } catch (Exception e) {
                e.printStackTrace();
                sendError(exchange, "Processing error: " + e.getMessage());
            }
        }

        private String extractStringValue(String json, String key) {
            // Simple JSON parsing - find "key":"value" or "key":value
            String keyPattern = "\"" + key + "\":";
            int keyIndex = json.indexOf(keyPattern);
            if (keyIndex == -1) return null;

            int valueStart = keyIndex + keyPattern.length();

            // Skip whitespace
            while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) valueStart++;

            if (valueStart >= json.length()) return null;

            char firstChar = json.charAt(valueStart);
            if (firstChar == '"') {
                // String value
                valueStart++; // Skip opening quote
                int valueEnd = json.indexOf('"', valueStart);
                if (valueEnd == -1) return null;
                return json.substring(valueStart, valueEnd);
            } else {
                // Number or boolean value - find next comma or closing brace
                int valueEnd = valueStart;
                while (valueEnd < json.length() && json.charAt(valueEnd) != ',' && json.charAt(valueEnd) != '}') valueEnd++;
                String value = json.substring(valueStart, valueEnd).trim();
                return value;
            }
        }

        private void sendError(HttpExchange exchange, String message) throws IOException {
            String response = "{\"error\":\"" + message.replace("\"", "\\\"") + "\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}