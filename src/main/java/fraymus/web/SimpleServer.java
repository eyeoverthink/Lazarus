package fraymus.web;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Simple HTTP server - NO PROMISES, JUST WORKING CODE
 * Run it, curl it, it responds. That's it.
 */
public class SimpleServer {
    
    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Simple hello endpoint
        server.createContext("/hello", new HelloHandler());
        
        // Status endpoint
        server.createContext("/status", new StatusHandler());
        
        server.setExecutor(null); // default executor
        server.start();
        
        System.out.println("✅ Simple HTTP Server running on port " + port);
        System.out.println("   Try: curl http://localhost:" + port + "/hello");
        System.out.println("   Try: curl http://localhost:" + port + "/status");
    }
    
    static class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Hello! This server actually works. No promises - just code.";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Status: RUNNING\nReal code, not documentation.";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
