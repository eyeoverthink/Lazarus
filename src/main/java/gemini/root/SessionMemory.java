package gemini.root;

import org.java_websocket.WebSocket;
import fraymus.OllamaSpine;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SessionMemory: Gemini-style session continuity
 * Maintains per-connection chat history with token budgeting
 */
public class SessionMemory {
    
    private final int maxMessages;
    private final int maxTokens;
    private final ConcurrentHashMap<WebSocket, Deque<OllamaSpine.Msg>> sessions;
    
    public SessionMemory(int maxMessages, int maxTokens) {
        this.maxMessages = maxMessages;
        this.maxTokens = maxTokens;
        this.sessions = new ConcurrentHashMap<>();
    }
    
    /**
     * Initialize a new session for a connection
     */
    public void init(WebSocket conn) {
        sessions.put(conn, new ArrayDeque<>());
    }
    
    /**
     * Add a message to the session history
     */
    public void push(WebSocket conn, String role, String content) {
        Deque<OllamaSpine.Msg> history = sessions.get(conn);
        if (history == null) {
            history = new ArrayDeque<>();
            sessions.put(conn, history);
        }
        
        history.addLast(new OllamaSpine.Msg(role, content));
        
        // Trim if too many messages
        while (history.size() > maxMessages) {
            history.removeFirst();
        }
        
        // Trim if token budget exceeded (rough estimate: 4 chars = 1 token)
        int estimatedTokens = 0;
        for (OllamaSpine.Msg msg : history) {
            estimatedTokens += msg.content.length() / 4;
        }
        
        while (estimatedTokens > maxTokens && history.size() > 2) {
            OllamaSpine.Msg removed = history.removeFirst();
            estimatedTokens -= removed.content.length() / 4;
        }
    }
    
    /**
     * Get a snapshot of the session history
     */
    public Deque<OllamaSpine.Msg> snapshot(WebSocket conn) {
        Deque<OllamaSpine.Msg> history = sessions.get(conn);
        if (history == null) {
            return new ArrayDeque<>();
        }
        return new ArrayDeque<>(history);
    }
    
    /**
     * Clear session history for a connection
     */
    public void clear(WebSocket conn) {
        Deque<OllamaSpine.Msg> history = sessions.get(conn);
        if (history != null) {
            history.clear();
        }
    }
    
    /**
     * Remove session when connection closes
     */
    public void drop(WebSocket conn) {
        sessions.remove(conn);
    }
}
