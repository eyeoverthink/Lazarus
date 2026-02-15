package fraymus.limbs;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * 🖐️ CLAW CONNECTOR: The Nerve Ending
 * 
 * Connects Fraynix (The Brain) to OpenClaw (The Body).
 * This is the HTTP bridge that sends thoughts to physical actions.
 * 
 * When a task particle collides with the OpenClaw particle,
 * this connector transmits the neural impulse.
 */
public class ClawConnector {

    private final String agentUrl;
    private final HttpClient client;

    /**
     * Default OpenClaw Gateway
     */
    public ClawConnector() {
        this("http://127.0.0.1:18789"); 
    }

    /**
     * Custom gateway URL
     */
    public ClawConnector(String url) {
        this.agentUrl = url;
        this.client = HttpClient.newBuilder()
            .timeout(Duration.ofSeconds(30))
            .build();
    }

    /**
     * DISPATCH TASK: Sends a thought from Fraynix to OpenClaw's hands.
     * 
     * @param intent The task description (what to do)
     * @param context Additional context or priority
     * @return Result message from OpenClaw
     */
    public String dispatch(String intent, String context) {
        System.out.println("⚡ NERVE IMPULSE: Sending task to OpenClaw -> " + intent);
        
        String jsonPayload = String.format(
            "{\"task\": \"%s\", \"context\": \"%s\", \"autonomy\": \"high\"}", 
            escape(intent), escape(context)
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(agentUrl + "/api/v1/agent/execute"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

            HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return "✅ CLAW ACTION STARTED: " + response.body();
            } else if (response.statusCode() == 404) {
                return "⚠️ ENDPOINT NOT FOUND: OpenClaw may use different API";
            } else {
                return "❌ NERVE DAMAGE: Claw refused task (HTTP " + response.statusCode() + ")";
            }
        } catch (java.net.ConnectException e) {
            return "🔌 SEVERED LINK: OpenClaw not running on " + agentUrl;
        } catch (Exception e) {
            return "⚠️ COMMUNICATION ERROR: " + e.getMessage();
        }
    }
    
    /**
     * TEST CONNECTION: Ping OpenClaw to see if it's alive
     */
    public boolean isConnected() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(agentUrl + "/health"))
                .GET()
                .build();
                
            HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
            
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get the gateway URL
     */
    public String getUrl() {
        return agentUrl;
    }

    /**
     * Escape JSON strings
     */
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("\t", " ");
    }
}
