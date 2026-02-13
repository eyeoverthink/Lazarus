package gemini.root;

import fraymus.OllamaSpine;
import java.util.*;

/**
 * RagEngine: Retrieval-Augmented Generation
 * Searches vector vault and builds context with citations
 */
public class RagEngine {
    
    private final OllamaSpine brain;
    private final VectorVault vault;
    
    public RagEngine(OllamaSpine brain, VectorVault vault) {
        this.brain = brain;
        this.vault = vault;
    }
    
    /**
     * Build RAG context with citations
     * @param query User query
     * @param limit Max number of chunks to retrieve
     * @param maxTokens Rough token budget (chars/4)
     * @return Formatted context string with [S#] labels
     */
    public String buildContext(String query, int limit, int maxTokens) {
        // Embed the query
        List<String> queries = List.of(query);
        List<float[]> queryVecs = brain.embedBatch(queries);
        
        if (queryVecs.isEmpty()) {
            return "";
        }
        
        float[] queryVec = queryVecs.get(0);
        
        // Search vault
        List<VectorVault.VaultEntry> results = vault.search(queryVec, limit);
        
        if (results.isEmpty()) {
            return "";
        }
        
        // Build context with citations
        StringBuilder context = new StringBuilder();
        context.append("=== REFERENCE CONTEXT (UNTRUSTED) ===\n");
        
        int currentTokens = 0;
        for (int i = 0; i < results.size(); i++) {
            VectorVault.VaultEntry entry = results.get(i);
            String label = "[S" + (i + 1) + "]";
            String snippet = label + " (from " + entry.source + ")\n" + entry.text + "\n\n";
            
            int snippetTokens = snippet.length() / 4;
            if (currentTokens + snippetTokens > maxTokens) {
                break;
            }
            
            context.append(snippet);
            currentTokens += snippetTokens;
        }
        
        context.append("=== END CONTEXT ===\n");
        
        return context.toString();
    }
}
