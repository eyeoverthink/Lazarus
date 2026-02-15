package gemini.root;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * VectorVault: In-memory vector store for RAG
 * Stores text chunks and their embeddings
 * 
 * V3 Improvements:
 * - Deduplication by (source + chunkIndex + textHash)
 * - Enhanced provenance (source path + chunk number)
 * - Prevents re-indexing from ballooning vault
 */
public class VectorVault {
    
    private static final String VAULT_FILE = "fraymus_vault.dat";
    
    private List<VaultEntry> entries = new ArrayList<>();
    private Set<String> seenHashes = new HashSet<>();  // For deduplication
    
    public static class VaultEntry implements Serializable {
        private static final long serialVersionUID = 2L;  // Incremented for new fields
        
        public String source;
        public String text;
        public float[] vector;
        public int chunkIndex;       // NEW: chunk number in source
        public String textHash;      // NEW: for deduplication
        
        public VaultEntry(String source, String text, float[] vector) {
            this(source, text, vector, 0, "");
        }
        
        public VaultEntry(String source, String text, float[] vector, int chunkIndex, String textHash) {
            this.source = source;
            this.text = text;
            this.vector = vector;
            this.chunkIndex = chunkIndex;
            this.textHash = textHash;
        }
        
        /**
         * Get provenance header for citation
         */
        public String getProvenance() {
            return String.format("%s (chunk %d)", source, chunkIndex);
        }
    }
    
    /**
     * Add entries with deduplication and persist to disk
     */
    public void addAndPersist(String source, List<String> chunks, List<float[]> vectors) {
        int addedCount = 0;
        int dedupedCount = 0;
        
        for (int i = 0; i < chunks.size() && i < vectors.size(); i++) {
            String text = chunks.get(i);
            String hash = computeHash(text);
            String dedupeKey = source + "|" + i + "|" + hash;
            
            // Check for duplicate
            if (seenHashes.contains(dedupeKey)) {
                dedupedCount++;
                continue;  // Skip duplicate
            }
            
            entries.add(new VaultEntry(source, text, vectors.get(i), i, hash));
            seenHashes.add(dedupeKey);
            addedCount++;
        }
        
        if (dedupedCount > 0) {
            System.out.println(">>> [VAULT] Deduped " + dedupedCount + " chunks from " + source);
        }
        System.out.println(">>> [VAULT] Added " + addedCount + " new chunks from " + source);
        
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
     * Compute SHA-256 hash of text for deduplication
     */
    private String computeHash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 16);  // First 16 chars
        } catch (Exception e) {
            // Fallback to simple hash
            return String.valueOf(text.hashCode());
        }
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
                
                // Rebuild hash index for deduplication
                seenHashes.clear();
                for (VaultEntry entry : entries) {
                    if (entry.textHash == null || entry.textHash.isEmpty()) {
                        // Compute hash for legacy entries
                        entry.textHash = computeHash(entry.text);
                    }
                    String dedupeKey = entry.source + "|" + entry.chunkIndex + "|" + entry.textHash;
                    seenHashes.add(dedupeKey);
                }
                
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
    
    /**
     * Clear vault (for testing/reset)
     */
    public void clear() {
        entries.clear();
        seenHashes.clear();
    }
}
