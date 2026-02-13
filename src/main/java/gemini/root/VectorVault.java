package gemini.root;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * VectorVault: In-memory vector store for RAG
 * Stores text chunks and their embeddings
 */
public class VectorVault {
    
    private static final String VAULT_FILE = "fraymus_vault.dat";
    
    private List<VaultEntry> entries = new ArrayList<>();
    
    public static class VaultEntry implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public String source;
        public String text;
        public float[] vector;
        
        public VaultEntry(String source, String text, float[] vector) {
            this.source = source;
            this.text = text;
            this.vector = vector;
        }
    }
    
    /**
     * Add entries and persist to disk
     */
    public void addAndPersist(String source, List<String> chunks, List<float[]> vectors) {
        for (int i = 0; i < chunks.size() && i < vectors.size(); i++) {
            entries.add(new VaultEntry(source, chunks.get(i), vectors[i]));
        }
        save();
    }
    
    /**
     * Search for similar entries
     */
    public List<VaultEntry> search(float[] queryVector, int limit) {
        List<VaultEntry> results = new ArrayList<>(entries);
        
        // Sort by cosine similarity
        results.sort((a, b) -> {
            double simA = cosineSimilarity(queryVector, a.vector);
            double simB = cosineSimilarity(queryVector, b.vector);
            return Double.compare(simB, simA);
        });
        
        return results.subList(0, Math.min(limit, results.size()));
    }
    
    private double cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) return 0.0;
        
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    
    /**
     * Load vault from disk
     * 
     * WARNING: Uses Java serialization which has security risks.
     * For production, consider JSON-based storage or add validation.
     */
    public void load() {
        File f = new File(VAULT_FILE);
        if (!f.exists()) {
            System.out.println(">>> [VAULT] No prior vault found");
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            @SuppressWarnings("unchecked")
            List<VaultEntry> loaded = (List<VaultEntry>) ois.readObject();
            // Basic validation
            if (loaded != null) {
                entries = loaded;
                System.out.println(">>> [VAULT] Loaded " + entries.size() + " entries");
            }
        } catch (Exception e) {
            System.err.println(">>> [VAULT] Load failed: " + e.getMessage());
        }
    }
    
    /**
     * Save vault to disk
     */
    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(VAULT_FILE))) {
            oos.writeObject(entries);
        } catch (IOException e) {
            System.err.println(">>> [VAULT] Save failed: " + e.getMessage());
        }
    }
    
    public int size() {
        return entries.size();
    }
}
