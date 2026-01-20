package com.example;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ImageEditorServer {

    public static void main(String[] args) throws Exception {
        // Fixed port 8080
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

        // Dummy root endpoint
        server.createContext("/", exchange -> {
            try (InputStream is = ImageEditorServer.class.getClassLoader().getResourceAsStream("index.html")) {
                if (is == null) {
                    String response = "File not found";
                    exchange.getResponseHeaders().set("Content-Type", "text/plain");
                    exchange.sendResponseHeaders(404, response.getBytes(StandardCharsets.UTF_8).length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                    return;
                }
                byte[] content = is.readAllBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/html");
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
        });

        server.start();
        System.out.println("Server running on port 8080");
    }
}
