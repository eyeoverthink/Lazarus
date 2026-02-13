package gemini.root;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 🧠 THE OLLAMA SPINE
 * Role: Connects the Java Nervous System to your Local LLM.
 * Abstraction: "Thought" is just an API call to localhost:11434.
 * 
 * Generation: 195 - The Universal Interface
 */
public class OllamaSpine {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private String modelName; // e.g., "llama3" or "mistral"

    public OllamaSpine(String modelName) {
        this.modelName = modelName;
    }

    /**
     * TRANSMIT THOUGHT
     * Sends the prompt + context (Transmudder data) to the AI.
     */
    public String think(String prompt, String context) {
        try {
            URL url = new URL(OLLAMA_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Construct JSON Payload (The Abstraction)
            // We feed the "Transmudder" context as a System Prompt.
            String jsonInput = String.format(
                "{\"model\": \"%s\", \"prompt\": \"[CONTEXT: %s] USER: %s\", \"stream\": false}",
                escapeJson(modelName), escapeJson(context), escapeJson(prompt)
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the Response (The Voice)
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            // Simple Parsing (Abstracting the JSON response)
            return extractResponseText(response.toString());

        } catch (Exception e) {
            return "[OLLAMA DISCONNECTED] " + e.getMessage();
        }
    }

    private String extractResponseText(String json) {
        // Quick & Dirty extraction to avoid libraries
        int startIndex = json.indexOf("\"response\":\"") + 12;
        int endIndex = json.indexOf("\",\"done\"");
        if (startIndex > 12 && endIndex > startIndex) {
            return json.substring(startIndex, endIndex)
                      .replace("\\n", "\n")
                      .replace("\\\"", "\"")
                      .replace("\\t", "\t");
        }
        return json; // Fallback
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
