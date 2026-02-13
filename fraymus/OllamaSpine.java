package fraymus;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * OllamaSpine: The Embedding Engine
 * 
 * Converts text into high-dimensional vector embeddings using Ollama's embedding models.
 * These embeddings capture semantic meaning and enable similarity comparisons.
 */
public class OllamaSpine {
    
    private static final String OLLAMA_URL = "http://localhost:11434";
    private static final int TIMEOUT_MS = 120000;  // 2 minutes
    
    private String baseUrl;
    private boolean connected = false;
    
    public OllamaSpine() {
        this(OLLAMA_URL);
    }
    
    public OllamaSpine(String baseUrl) {
        this.baseUrl = baseUrl;
        testConnection();
    }
    
    private void testConnection() {
        try {
            URL url = new URL(baseUrl + "/api/tags");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                connected = true;
            }
            conn.disconnect();
            
        } catch (Exception e) {
            connected = false;
        }
    }
    
    /**
     * Generate embeddings for a list of text inputs
     * 
     * @param embedModel The embedding model to use (e.g., "embeddinggemma", "nomic-embed-text")
     * @param inputs List of text strings to embed
     * @return 2D array where each row is an embedding vector for the corresponding input
     */
    public double[][] embed(String embedModel, java.util.List<String> inputs) {
        try {
            if (inputs == null || inputs.isEmpty()) {
                return new double[0][];
            }
            
            double[][] embeddings = new double[inputs.size()][];
            
            for (int i = 0; i < inputs.size(); i++) {
                String input = inputs.get(i);
                double[] embedding = embedSingle(embedModel, input);
                embeddings[i] = embedding;
            }
            
            return embeddings;
            
        } catch (Exception e) {
            System.err.println("Error generating embeddings: " + e.getMessage());
            e.printStackTrace();
            return new double[inputs.size()][];
        }
    }
    
    /**
     * Generate embedding for a single text input
     * 
     * @param embedModel The embedding model to use
     * @param input The text to embed
     * @return The embedding vector
     */
    public double[] embedSingle(String embedModel, String input) throws IOException {
        URL url = new URL(baseUrl + "/api/embeddings");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        conn.setDoOutput(true);
        
        // Build JSON request
        String jsonRequest = String.format(
            "{\"model\":\"%s\",\"prompt\":%s}",
            embedModel,
            escapeJson(input)
        );
        
        // Send request
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
        }
        
        // Read response
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return parseEmbeddingResponse(response.toString());
            }
        } else {
            throw new IOException("Ollama embedding API returned status: " + responseCode);
        }
    }
    
    /**
     * Parse the embedding array from Ollama's JSON response
     */
    private double[] parseEmbeddingResponse(String json) {
        // Find the "embedding" array in the JSON response
        int start = json.indexOf("\"embedding\":");
        if (start == -1) {
            return new double[0];
        }
        
        start = json.indexOf("[", start);
        int end = json.indexOf("]", start);
        
        if (start == -1 || end == -1) {
            return new double[0];
        }
        
        String arrayContent = json.substring(start + 1, end);
        String[] values = arrayContent.split(",");
        
        double[] embedding = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            embedding[i] = Double.parseDouble(values[i].trim());
        }
        
        return embedding;
    }
    
    /**
     * Escape a string for JSON
     */
    private String escapeJson(String text) {
        if (text == null) return "\"\"";
        return "\"" + text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            + "\"";
    }
    
    /**
     * Calculate cosine similarity between two embedding vectors
     */
    public static double cosineSimilarity(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vectors must have the same length");
        }
        
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    
    public boolean isConnected() {
        return connected;
    }
}
