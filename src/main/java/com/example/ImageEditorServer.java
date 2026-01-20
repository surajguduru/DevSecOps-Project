package com.example;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ImageEditorServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Health endpoint
        server.createContext("/health", exchange -> {
            String response = "The server is healthy!";
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        });

        // Root endpoint serves index.html
        server.createContext("/", exchange -> {
            serveFile(exchange, "index.html", "text/html");
        });

        // Serve static assets (JS, CSS, images)
        server.createContext("/static", exchange -> {
            String path = exchange.getRequestURI().getPath().replaceFirst("/static/", "");
            String contentType = guessContentType(path);
            serveFile(exchange, path, contentType);
        });

        server.start();
        System.out.println("Server running on port 8080");
    }

    // Helper to serve files from resources
    private static void serveFile(HttpExchange exchange, String filePath, String contentType) throws IOException {
        try (InputStream is = ImageEditorServer.class.getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                String response = "File not found: " + filePath;
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.sendResponseHeaders(404, response.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
                return;
            }

            byte[] content = is.readAllBytes();
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, content.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(content);
            }
        } catch (IOException e) {
            String response = "Internal server error";
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(500, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    // Simple MIME type guesser
    private static String guessContentType(String filePath) {
        Map<String, String> map = new HashMap<>();
        map.put(".html", "text/html");
        map.put(".js", "application/javascript");
        map.put(".css", "text/css");
        map.put(".png", "image/png");
        map.put(".jpg", "image/jpeg");
        map.put(".jpeg", "image/jpeg");
        map.put(".gif", "image/gif");

        for (Map.Entry<String, String> e : map.entrySet()) {
            if (filePath.endsWith(e.getKey())) return e.getValue();
        }
        return "application/octet-stream";
    }
}
