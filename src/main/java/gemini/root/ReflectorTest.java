package gemini.root;

import fraymus.OllamaSpine;
import java.util.*;

/**
 * Test and demonstration of the Reflector metacognition module
 */
public class ReflectorTest {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   REFLECTOR TEST - Metacognition Module               ║");
        System.out.println("║   Draft → Critique → Refine                           ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Create OllamaSpine instance
        OllamaSpine brain = new OllamaSpine();
        
        // Test connection
        System.out.println("TEST 1: Connection Test");
        System.out.println("─────────────────────────────────────────────────────────");
        System.out.println("Connected to Ollama: " + (brain.isConnected() ? "✓ YES" : "✗ NO"));
        System.out.println();
        
        if (!brain.isConnected()) {
            System.out.println("⚠ Ollama is not running. Start it with: ollama serve");
            System.out.println("⚠ Then run: ollama pull llama3");
            System.out.println();
            System.out.println("Skipping reflection tests...");
            return;
        }
        
        // Create Reflector
        Reflector reflector = new Reflector(brain);
        
        // Test 2: Simple reflection without RAG context
        System.out.println("TEST 2: Simple Reflection (No Context)");
        System.out.println("─────────────────────────────────────────────────────────");
        try {
            String query = "What is the capital of France?";
            System.out.println("Query: " + query);
            System.out.println();
            
            String answer = reflector.reflect(query, "");
            System.out.println("Answer:");
            System.out.println(answer);
            System.out.println();
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Test 3: Reflection with RAG context
        System.out.println("TEST 3: Reflection with RAG Context");
        System.out.println("─────────────────────────────────────────────────────────");
        try {
            String query = "What are the main features of quantum computing?";
            String context = """
            [S1] Quantum computing uses quantum bits (qubits) that can exist in superposition.
            [S2] Quantum entanglement allows qubits to be correlated in ways classical bits cannot.
            [S3] Quantum computers can solve certain problems exponentially faster than classical computers.
            """;
            
            System.out.println("Query: " + query);
            System.out.println();
            System.out.println("Context provided:");
            System.out.println(context);
            System.out.println();
            
            String answer = reflector.reflect(query, context);
            System.out.println("Answer:");
            System.out.println(answer);
            System.out.println();
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Test 4: Reflection with custom options
        System.out.println("TEST 4: Reflection with Custom Options");
        System.out.println("─────────────────────────────────────────────────────────");
        try {
            String query = "Explain machine learning in simple terms.";
            
            Map<String, Object> options = new HashMap<>();
            options.put("temperature", 0.7);
            options.put("num_ctx", 4096);
            options.put("model", "llama3");
            
            System.out.println("Query: " + query);
            System.out.println("Options: " + options);
            System.out.println();
            
            String answer = reflector.reflect(query, "", options);
            System.out.println("Answer:");
            System.out.println(answer);
            System.out.println();
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Test 5: Full reflection with history
        System.out.println("TEST 5: Reflection with Conversation History");
        System.out.println("─────────────────────────────────────────────────────────");
        try {
            List<Map<String, String>> history = new ArrayList<>();
            
            Map<String, String> msg1 = new HashMap<>();
            msg1.put("role", "user");
            msg1.put("content", "What is AI?");
            history.add(msg1);
            
            Map<String, String> msg2 = new HashMap<>();
            msg2.put("role", "assistant");
            msg2.put("content", "AI stands for Artificial Intelligence, which refers to computer systems that can perform tasks that typically require human intelligence.");
            history.add(msg2);
            
            String query = "Can you give me an example?";
            
            System.out.println("Previous conversation:");
            for (Map<String, String> msg : history) {
                System.out.println("  " + msg.get("role") + ": " + msg.get("content"));
            }
            System.out.println();
            System.out.println("Current query: " + query);
            System.out.println();
            
            String answer = reflector.reflect(query, "", "", history, new HashMap<>());
            System.out.println("Answer:");
            System.out.println(answer);
            System.out.println();
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   TEST COMPLETE                                        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
    }
}
