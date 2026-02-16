package fraymus;

import java.util.Arrays;
import java.util.List;

/**
 * Test for OllamaSpine embedding functionality
 */
public class OllamaSpineTest {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   OLLAMA SPINE TEST - Embedding Engine                ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Create OllamaSpine instance
        OllamaSpine spine = new OllamaSpine();
        
        // Test 1: Connection
        System.out.println("TEST 1: Connection Test");
        System.out.println("─────────────────────────────────────────────────────────");
        System.out.println("Connected to Ollama: " + (spine.isConnected() ? "✓ YES" : "✗ NO"));
        System.out.println();
        
        if (!spine.isConnected()) {
            System.out.println("⚠ Ollama is not running. Start it with: ollama serve");
            System.out.println("⚠ Then run: ollama pull embeddinggemma");
            System.out.println();
            System.out.println("Skipping embedding tests...");
            return;
        }
        
        // Test 2: Single embedding
        System.out.println("TEST 2: Single Embedding");
        System.out.println("─────────────────────────────────────────────────────────");
        try {
            String embedModel = System.getenv("FRAYMUS_EMBED_MODEL");
            if (embedModel == null || embedModel.isEmpty()) {
                embedModel = "embeddinggemma";
            }
            System.out.println("Using model: " + embedModel);
            
            double[] embedding = spine.embedSingle(embedModel, "What is consciousness?");
            System.out.println("Embedding dimension: " + embedding.length);
            System.out.println("First 5 values: " + Arrays.toString(Arrays.copyOf(embedding, Math.min(5, embedding.length))));
            System.out.println("✓ Single embedding successful");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
        
        // Test 3: Batch embeddings
        System.out.println("TEST 3: Batch Embeddings");
        System.out.println("─────────────────────────────────────────────────────────");
        try {
            String embedModel = System.getenv("FRAYMUS_EMBED_MODEL");
            if (embedModel == null || embedModel.isEmpty()) {
                embedModel = "embeddinggemma";
            }
            
            List<String> inputs = Arrays.asList(
                "What is consciousness?",
                "The nature of reality",
                "Quantum mechanics"
            );
            
            System.out.println("Embedding " + inputs.size() + " texts...");
            double[][] embeddings = spine.embed(embedModel, inputs);
            
            System.out.println("Number of embeddings: " + embeddings.length);
            for (int i = 0; i < embeddings.length; i++) {
                System.out.println("  [" + i + "] dimension: " + embeddings[i].length + 
                    ", first value: " + embeddings[i][0]);
            }
            System.out.println("✓ Batch embeddings successful");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
        
        // Test 4: Similarity calculation
        System.out.println("TEST 4: Cosine Similarity");
        System.out.println("─────────────────────────────────────────────────────────");
        try {
            String embedModel = System.getenv("FRAYMUS_EMBED_MODEL");
            if (embedModel == null || embedModel.isEmpty()) {
                embedModel = "embeddinggemma";
            }
            
            double[] emb1 = spine.embedSingle(embedModel, "cat");
            double[] emb2 = spine.embedSingle(embedModel, "dog");
            double[] emb3 = spine.embedSingle(embedModel, "computer");
            
            double sim12 = OllamaSpine.cosineSimilarity(emb1, emb2);
            double sim13 = OllamaSpine.cosineSimilarity(emb1, emb3);
            double sim23 = OllamaSpine.cosineSimilarity(emb2, emb3);
            
            System.out.println("Similarity between 'cat' and 'dog': " + String.format("%.4f", sim12));
            System.out.println("Similarity between 'cat' and 'computer': " + String.format("%.4f", sim13));
            System.out.println("Similarity between 'dog' and 'computer': " + String.format("%.4f", sim23));
            
            if (sim12 > sim13) {
                System.out.println("✓ Semantic similarity works! (cat-dog > cat-computer)");
            } else {
                System.out.println("⚠ Unexpected similarity ordering");
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
        
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   TEST COMPLETE                                        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
    }
}
