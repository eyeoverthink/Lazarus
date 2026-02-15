package fraymus;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * OllamaSpine: The Embedding Engine and Text Generation
 * 
 * Converts text into high-dimensional vector embeddings using Ollama's embedding models.
 * Also provides text generation and chat capabilities.
 * These embeddings capture semantic meaning and enable similarity comparisons.
 * 
 * V3 Improvements:
 * - Configurable timeouts (connect + read)
 * - Retry logic with exponential backoff
 * - Input size caps for embeddings
 */
public class OllamaSpine {
    
    private static final String OLLAMA_URL = "http://localhost:11434";
    private static final int CONNECT_TIMEOUT_MS = 10000;  // 10 seconds
    private static final int READ_TIMEOUT_MS = 120000;    // 2 minutes
    private static final int MAX_RETRIES = 3;
    private static final int INITIAL_RETRY_DELAY_MS = 1000;  // 1 second
    
    // Input size limits
    private static final int MAX_EMBED_TEXT_LENGTH = 8000;  // chars
    private static final int MAX_EMBED_BATCH_SIZE = 100;     // chunks
    
    private String baseUrl;
    private boolean connected = false;
    private String chatModel;
    private String embedModel;
    
    /**
     * Message structure for chat API
     */
    public static class Msg {
        public String role;
        public String content;
        
        public Msg(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
    
    public OllamaSpine() {
        this(OLLAMA_URL);
    }
    
    public OllamaSpine(String baseUrl) {
        this.baseUrl = baseUrl;
        this.chatModel = "llama3";
        this.embedModel = "embeddinggemma";
        testConnection();
    }
    
    public OllamaSpine(String chatModel, String embedModel) {
        this.baseUrl = OLLAMA_URL;
        this.chatModel = chatModel;
        this.embedModel = embedModel;
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
     * Generate embeddings for a list of text inputs with size limits
     */
    public double[][] embed(String embedModel, java.util.List<String> inputs) {
        try {
            if (inputs == null || inputs.isEmpty()) {
                return new double[0][];
            }
            
            // Apply batch size limit
            if (inputs.size() > MAX_EMBED_BATCH_SIZE) {
                System.err.println("WARNING: Batch size " + inputs.size() + " exceeds max " + MAX_EMBED_BATCH_SIZE + ", truncating");
                inputs = inputs.subList(0, MAX_EMBED_BATCH_SIZE);
            }
            
            double[][] embeddings = new double[inputs.size()][];
            
            for (int i = 0; i < inputs.size(); i++) {
                String input = inputs.get(i);
                
                // Apply text length limit
                if (input.length() > MAX_EMBED_TEXT_LENGTH) {
                    System.err.println("WARNING: Text length " + input.length() + " exceeds max " + MAX_EMBED_TEXT_LENGTH + ", truncating");
                    input = input.substring(0, MAX_EMBED_TEXT_LENGTH);
                }
                
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
     * Generate embedding for a single text input with retry logic
     */
    public double[] embedSingle(String embedModel, String input) throws IOException {
        return retryWithBackoff(() -> embedSingleInternal(embedModel, input));
    }
    
    private double[] embedSingleInternal(String embedModel, String input) throws IOException {
        URL url = new URL(baseUrl + "/api/embeddings");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);
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
            .replace("\b", "\\b")
            .replace("\f", "\\f")
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
    
    /**
     * Generate text completion from Ollama with retry logic
     */
    public String generate(String model, String prompt, Map<String, Object> options) throws IOException {
        return retryWithBackoff(() -> generateInternal(model, prompt, options));
    }
    
    private String generateInternal(String model, String prompt, Map<String, Object> options) throws IOException {
        URL url = new URL(baseUrl + "/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);
        conn.setDoOutput(true);
        
        // Build JSON request
        StringBuilder jsonRequest = new StringBuilder();
        jsonRequest.append("{\"model\":").append(escapeJson(model));
        jsonRequest.append(",\"prompt\":").append(escapeJson(prompt));
        jsonRequest.append(",\"stream\":false");
        
        if (options != null && !options.isEmpty()) {
            jsonRequest.append(",\"options\":{");
            boolean first = true;
            for (Map.Entry<String, Object> entry : options.entrySet()) {
                if (!first) jsonRequest.append(",");
                jsonRequest.append("\"").append(entry.getKey()).append("\":");
                Object value = entry.getValue();
                if (value instanceof String) {
                    jsonRequest.append(escapeJson((String) value));
                } else {
                    jsonRequest.append(value);
                }
                first = false;
            }
            jsonRequest.append("}");
        }
        
        jsonRequest.append("}");
        
        // Send request
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonRequest.toString().getBytes(StandardCharsets.UTF_8));
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
                return parseGenerateResponse(response.toString());
            }
        } else {
            throw new IOException("Ollama generate API returned status: " + responseCode);
        }
    }
    
    /**
     * Chat with Ollama using conversation format with retry logic
     */
    public String chat(String model, List<Map<String, String>> messages, Map<String, Object> options) throws IOException {
        return retryWithBackoff(() -> chatInternal(model, messages, options));
    }
    
    private String chatInternal(String model, List<Map<String, String>> messages, Map<String, Object> options) throws IOException {
        URL url = new URL(baseUrl + "/api/chat");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);
        conn.setDoOutput(true);
        
        // Build JSON request
        StringBuilder jsonRequest = new StringBuilder();
        jsonRequest.append("{\"model\":").append(escapeJson(model));
        jsonRequest.append(",\"messages\":[");
        
        for (int i = 0; i < messages.size(); i++) {
            Map<String, String> msg = messages.get(i);
            jsonRequest.append("{\"role\":").append(escapeJson(msg.get("role")));
            jsonRequest.append(",\"content\":").append(escapeJson(msg.get("content"))).append("}");
            if (i < messages.size() - 1) jsonRequest.append(",");
        }
        
        jsonRequest.append("],\"stream\":false");
        
        if (options != null && !options.isEmpty()) {
            jsonRequest.append(",\"options\":{");
            boolean first = true;
            for (Map.Entry<String, Object> entry : options.entrySet()) {
                if (!first) jsonRequest.append(",");
                jsonRequest.append("\"").append(entry.getKey()).append("\":");
                Object value = entry.getValue();
                if (value instanceof String) {
                    jsonRequest.append(escapeJson((String) value));
                } else {
                    jsonRequest.append(value);
                }
                first = false;
            }
            jsonRequest.append("}");
        }
        
        jsonRequest.append("}");
        
        // Send request
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonRequest.toString().getBytes(StandardCharsets.UTF_8));
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
                return parseChatResponse(response.toString());
            }
        } else {
            throw new IOException("Ollama chat API returned status: " + responseCode);
        }
    }
    
    /**
     * Parse the response text from generate API JSON response
     */
    private String parseGenerateResponse(String json) {
        int start = json.indexOf("\"response\":\"");
        if (start == -1) {
            start = json.indexOf("\"response\": \"");
        }
        if (start == -1) return "";
        
        start = json.indexOf("\"", start + 11) + 1;
        
        // Find closing quote (handle escapes)
        boolean escaped = false;
        int end = start;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '"') {
                end = i;
                break;
            }
        }
        
        String response = json.substring(start, end);
        
        // Unescape
        response = response.replace("\\n", "\n")
                          .replace("\\t", "\t")
                          .replace("\\\"", "\"")
                          .replace("\\\\", "\\");
        
        return response;
    }
    
    /**
     * Parse the response text from chat API JSON response
     */
    private String parseChatResponse(String json) {
        int start = json.indexOf("\"content\":\"");
        if (start == -1) {
            start = json.indexOf("\"content\": \"");
        }
        if (start == -1) return "";
        
        start = json.indexOf("\"", start + 10) + 1;
        
        // Find closing quote (handle escapes)
        boolean escaped = false;
        int end = start;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '"') {
                end = i;
                break;
            }
        }
        
        String response = json.substring(start, end);
        
        // Unescape
        response = response.replace("\\n", "\n")
                          .replace("\\t", "\t")
                          .replace("\\\"", "\"")
                          .replace("\\\\", "\\");
        
        return response;
    }
    
    /**
     * Simplified chat method for single-turn interactions
     * @param messages List of Msg objects with role and content
     * @param schema Optional JSON schema for structured output (currently unused, reserved for future)
     * @param options Generation options (temperature, num_ctx, etc.)
     * @return Generated response text
     */
    public String chatOnce(List<Msg> messages, Object schema, Map<String, Object> options) {
        // Note: schema parameter is reserved for future structured output support
        try {
            List<Map<String, String>> msgMaps = new ArrayList<>();
            for (Msg msg : messages) {
                Map<String, String> m = new HashMap<>();
                m.put("role", msg.role);
                m.put("content", msg.content);
                msgMaps.add(m);
            }
            return chat(chatModel, msgMaps, options);
        } catch (IOException e) {
            System.err.println("chatOnce error: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Batch embedding for multiple texts
     * @param texts List of texts to embed
     * @return List of embedding vectors as float arrays
     */
    public List<float[]> embedBatch(List<String> texts) {
        List<float[]> results = new ArrayList<>();
        try {
            for (String text : texts) {
                double[] embedding = embedSingle(embedModel, text);
                float[] floatEmb = new float[embedding.length];
                for (int i = 0; i < embedding.length; i++) {
                    floatEmb[i] = (float) embedding[i];
                }
                results.add(floatEmb);
            }
        } catch (IOException e) {
            System.err.println("embedBatch error: " + e.getMessage());
        }
        return results;
    }
    
    /**
     * Retry a network operation with exponential backoff
     */
    private <T> T retryWithBackoff(IOSupplier<T> operation) throws IOException {
        int attempt = 0;
        IOException lastException = null;
        
        while (attempt < MAX_RETRIES) {
            try {
                return operation.get();
            } catch (IOException e) {
                lastException = e;
                attempt++;
                
                // Don't retry on certain errors (4xx client errors)
                String msg = e.getMessage().toLowerCase();
                if (msg.contains("400") || msg.contains("401") || msg.contains("403") || msg.contains("404")) {
                    throw e;
                }
                
                if (attempt < MAX_RETRIES) {
                    int delayMs = INITIAL_RETRY_DELAY_MS * (1 << (attempt - 1)); // Exponential backoff
                    System.err.println("Ollama request failed (attempt " + attempt + "/" + MAX_RETRIES + "), retrying in " + delayMs + "ms: " + e.getMessage());
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Retry interrupted", ie);
                    }
                }
            }
        }
        
        throw new IOException("Failed after " + MAX_RETRIES + " retries", lastException);
    }
    
    @FunctionalInterface
    private interface IOSupplier<T> {
        T get() throws IOException;
    }
}
