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

        // Health endpoint
        server.createContext("/health", new HealthHandler());

        // Serve UI
        server.createContext("/", new StaticFileHandler("index.html"));

        // Image processing API
        server.createContext("/process", new ProcessImageHandler());

        server.start();
        System.out.println("Server running on 0.0.0.0:8080");
    }

    // ---------- HEALTH ----------
    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "The server is healthy!";
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    // ---------- STATIC FILE ----------
    static class StaticFileHandler implements HttpHandler {
        private final String filename;

        public StaticFileHandler(String filename) {
            this.filename = filename;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = "src/main/resources/" + filename;
            File file = new File(path);

            if (!file.exists()) {
                String response = "File not found";
                exchange.sendResponseHeaders(404, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
                return;
            }

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, file.length());
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = exchange.getResponseBody()) {
                fis.transferTo(os);
            }
        }
    }

    // ---------- IMAGE PROCESS ----------
    static class ProcessImageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                String imageBase64 = extract(requestBody, "image");
                String editType = extract(requestBody, "editType");
                int brightness = Integer.parseInt(extract(requestBody, "percentage", "0"));

                BufferedImage input = ImageIO.read(
                        new ByteArrayInputStream(Base64.getDecoder().decode(imageBase64)));

                BufferedImage output;
                switch (editType) {
                    case "grayscale": output = ImageProcessor.toGrayscale(input); break;
                    case "invert": output = ImageProcessor.invertRGB(input); break;
                    case "brightness": output = ImageProcessor.alterBrightness(input, brightness); break;
                    case "rotate90": output = ImageProcessor.rotate90(input); break;
                    case "rotate180": output = ImageProcessor.rotate180(input); break;
                    case "mirrorRight": output = ImageProcessor.mirrorRight(input); break;
                    case "mirrorBottom": output = ImageProcessor.mirrorBottom(input); break;
                    default: throw new RuntimeException("Unknown edit type");
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(output, "png", baos);

                String response = "{\"editedImage\":\"" +
                        Base64.getEncoder().encodeToString(baos.toByteArray()) + "\"}";

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());

            } catch (Exception e) {
                String error = "{\"error\":\"" + e.getMessage() + "\"}";
                exchange.sendResponseHeaders(400, error.length());
                exchange.getResponseBody().write(error.getBytes());
            }
        }

        private String extract(String json, String key) {
            return extract(json, key, null);
        }

        private String extract(String json, String key, String def) {
            String p = "\"" + key + "\":";
            int i = json.indexOf(p);
            if (i == -1) return def;
            i += p.length();
            while (i < json.length() && json.charAt(i) == ' ') i++;
            if (json.charAt(i) == '"') {
                int e = json.indexOf('"', i + 1);
                return json.substring(i + 1, e);
            }
            int e = json.indexOf(",", i);
            return json.substring(i, e == -1 ? json.length() : e).trim();
        }
    }
}
