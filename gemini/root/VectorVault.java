package gemini.root;

import java.util.*;

/**
 * THE VECTOR VAULT
 * Role: Semantic storage and retrieval via embeddings.
 * Function: Store chunks with vectors, search by cosine similarity.
 */
public class VectorVault {

    public static class Chunk {
        public String text;
        public double[] embedding;
        public String source;  // file path
        public int index;      // chunk number

        public Chunk(String text, double[] embedding, String source, int index) {
            this.text = text;
            this.embedding = embedding;
            this.source = source;
            this.index = index;
        }
    }

    private List<Chunk> chunks = new ArrayList<>();
    private int topK = 3;  // Return top 3 by default

    /**
     * ADD CHUNK
     * Stores a text chunk with its embedding vector.
     */
    public void addChunk(String text, double[] embedding, String source, int index) {
        chunks.add(new Chunk(text, embedding, source, index));
    }

    /**
     * SEARCH SIMILAR
     * Find top-K chunks most similar to query embedding.
     * Uses cosine similarity.
     */
    public List<Chunk> searchSimilar(double[] queryEmbedding, int k) {
        if (chunks.isEmpty()) return new ArrayList<>();

        // Calculate similarity for each chunk
        List<Map.Entry<Chunk, Double>> scored = new ArrayList<>();
        for (Chunk chunk : chunks) {
            double sim = cosineSimilarity(queryEmbedding, chunk.embedding);
            scored.add(new AbstractMap.SimpleEntry<>(chunk, sim));
        }

        // Sort by similarity (descending)
        scored.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Return top K
        List<Chunk> results = new ArrayList<>();
        for (int i = 0; i < Math.min(k, scored.size()); i++) {
            results.add(scored.get(i).getKey());
        }

        return results;
    }

    /**
     * COSINE SIMILARITY
     * Measures angle between two vectors.
     * Returns value between -1 and 1 (1 = identical direction).
     */
    private double cosineSimilarity(double[] a, double[] b) {
        if (a.length != b.length) return 0.0;

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0.0 || normB == 0.0) return 0.0;

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * SEARCH SIMILAR (with default topK)
     */
    public List<Chunk> searchSimilar(double[] queryEmbedding) {
        return searchSimilar(queryEmbedding, topK);
    }

    /**
     * GET ALL CHUNKS
     */
    public List<Chunk> getAllChunks() {
        return new ArrayList<>(chunks);
    }

    /**
     * GET STATS
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_chunks", chunks.size());
        
        Set<String> sources = new HashSet<>();
        for (Chunk c : chunks) sources.add(c.source);
        stats.put("unique_sources", sources.size());
        
        if (!chunks.isEmpty()) {
            stats.put("embedding_dim", chunks.get(0).embedding.length);
        }
        
        return stats;
    }

    /**
     * CLEAR ALL
     */
    public void clear() {
        chunks.clear();
    }

    /**
     * SET TOP K
     */
    public void setTopK(int k) {
        this.topK = k;
    }
}
