package com.example;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import com.example.imageeditor.ImageProcessor;

public class ImageEditorServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 8080);

        server.createContext("/health", exchange -> {
            String response = "The server is healthy!";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });

        server.createContext("/", new StaticFileHandler("index.html"));
        server.createContext("/process", new ProcessImageHandler());

        server.start();
        System.out.println("Server running on port 8080");
    }

    static class StaticFileHandler implements HttpHandler {
        private final String filename;

        StaticFileHandler(String filename) {
            this.filename = filename;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
            if (is == null) {
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
                return;
            }
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, 0);
            is.transferTo(exchange.getResponseBody());
            exchange.close();
        }
    }

    static class ProcessImageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                String imageBase64 = extract(body, "image");
                String editType = extract(body, "editType");
                int percent = Integer.parseInt(extract(body, "percentage", "0"));

                BufferedImage input = ImageIO.read(
                        new ByteArrayInputStream(Base64.getDecoder().decode(imageBase64)));

                BufferedImage output;
                switch (editType) {
                    case "grayscale" -> output = ImageProcessor.toGrayscale(input);
                    case "invert" -> output = ImageProcessor.invertRGB(input);
                    case "brightness" -> output = ImageProcessor.alterBrightness(input, percent);
                    case "rotate90" -> output = ImageProcessor.rotate90(input);
                    case "rotate180" -> output = ImageProcessor.rotate180(input);
                    case "mirrorRight" -> output = ImageProcessor.mirrorRight(input);
                    case "mirrorBottom" -> output = ImageProcessor.mirrorBottom(input);
                    default -> throw new RuntimeException("Unknown edit");
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(output, "png", baos);

                String response = "{\"editedImage\":\"" +
                        Base64.getEncoder().encodeToString(baos.toByteArray()) + "\"}";

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();

            } catch (Exception e) {
                exchange.sendResponseHeaders(500, 0);
                exchange.close();
            }
        }

        private String extract(String json, String key) {
            return extract(json, key, null);
        }

        private String extract(String json, String key, String def) {
            int i = json.indexOf("\"" + key + "\":");
            if (i == -1) return def;
            i = json.indexOf(":", i) + 1;
            while (json.charAt(i) == ' ' || json.charAt(i) == '"') i++;
            int j = i;
            while (j < json.length() && json.charAt(j) != '"' && json.charAt(j) != ',' && json.charAt(j) != '}') j++;
            return json.substring(i, j);
        }
    }
}
