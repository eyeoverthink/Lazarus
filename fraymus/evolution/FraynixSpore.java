package fraymus.evolution;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.zip.*;

/**
 * 🌱 THE SPORE - Compressed Immortal Package
 * 
 * A spore is a dormant, compressed, immutable snapshot of the entire organism.
 * 
 * Key Features:
 * - TINY: Compressed to minimal size
 * - IMMUTABLE: Cannot be changed once created
 * - IMMORTAL: Can survive indefinitely
 * - PERFECT: 100% fidelity restoration
 * 
 * Biology Analogy:
 * - Bacterial spores can survive millions of years
 * - When conditions are right, they germinate
 * - Creates perfect copy of original organism
 * 
 * Use Cases:
 * - Backup/restore
 * - Distribution
 * - Version control
 * - Rapid deployment
 */
public class FraynixSpore implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // CORE GENOME
    private FraynixDNA genome;
    
    // ORGANISM STATE (optional - for full resurrection)
    private Map<String, Object> state;
    
    // METADATA
    private long timestamp;
    private String version;
    private byte[] compressedData;
    
    // INTEGRITY
    private String hash;
    
    /**
     * Private constructor - use static factory methods
     */
    private FraynixSpore(FraynixDNA genome, Map<String, Object> state) {
        this.genome = genome;
        this.state = state;
        this.timestamp = System.currentTimeMillis();
        this.version = genome.getVersion();
        this.hash = calculateHash();
        
        System.out.println("🌱 SPORE formed");
        System.out.println("   Generation: " + genome.getGeneration());
        System.out.println("   Hash: " + hash.substring(0, 16) + "...");
    }
    
    /**
     * SPORULATE: Package organism into spore
     */
    public static FraynixSpore sporulate(FraynixDNA genome) {
        return sporulate(genome, new HashMap<>());
    }
    
    /**
     * SPORULATE: Package organism with state
     */
    public static FraynixSpore sporulate(FraynixDNA genome, Map<String, Object> state) {
        System.out.println("🌱 SPORULATION: Creating dormant package...");
        
        FraynixSpore spore = new FraynixSpore(genome, state);
        
        try {
            // Compress for storage efficiency
            spore.compress();
            System.out.println("   Compressed size: " + spore.compressedData.length + " bytes");
        } catch (IOException e) {
            System.err.println("   ⚠️  Compression failed, using uncompressed");
        }
        
        return spore;
    }
    
    /**
     * Compress spore data
     */
    private void compress() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (GZIPOutputStream gzip = new GZIPOutputStream(baos);
             ObjectOutputStream oos = new ObjectOutputStream(gzip)) {
            
            // Serialize genome and state
            oos.writeObject(genome);
            oos.writeObject(state);
        }
        
        this.compressedData = baos.toByteArray();
    }
    
    /**
     * Decompress spore data
     */
    @SuppressWarnings("unchecked")
    private void decompress() throws IOException, ClassNotFoundException {
        if (compressedData == null) return;
        
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        
        try (GZIPInputStream gzip = new GZIPInputStream(bais);
             ObjectInputStream ois = new ObjectInputStream(gzip)) {
            
            this.genome = (FraynixDNA) ois.readObject();
            this.state = (Map<String, Object>) ois.readObject();
        }
    }
    
    /**
     * GERMINATE: Restore organism from spore
     */
    public FraynixDNA germinate() {
        System.out.println("🌱 GERMINATION: Awakening from dormancy...");
        
        // Verify integrity
        if (!verifyHash()) {
            throw new RuntimeException("💥 SPORE CORRUPTED - Hash mismatch!");
        }
        
        // Decompress if needed
        try {
            if (compressedData != null && genome == null) {
                decompress();
            }
        } catch (Exception e) {
            throw new RuntimeException("💥 GERMINATION FAILED: " + e.getMessage());
        }
        
        System.out.println("   ✓ Genome restored");
        System.out.println("   Generation: " + genome.getGeneration());
        System.out.println("   Genes: " + genome.getGenes().size());
        
        return genome;
    }
    
    /**
     * Get genome (decompresses if needed)
     */
    public FraynixDNA getGenome() {
        if (genome == null && compressedData != null) {
            try {
                decompress();
            } catch (Exception e) {
                throw new RuntimeException("Failed to decompress: " + e.getMessage());
            }
        }
        return genome;
    }
    
    /**
     * Get organism state
     */
    public Map<String, Object> getState() {
        return new HashMap<>(state);
    }
    
    /**
     * Calculate integrity hash
     */
    private String calculateHash() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // Hash genome checksum + timestamp
            String data = genome.getChecksum() + timestamp;
            byte[] hash = md.digest(data.getBytes());
            
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
            
        } catch (Exception e) {
            return "HASH_ERROR";
        }
    }
    
    /**
     * Verify spore integrity
     */
    public boolean verifyHash() {
        String current = calculateHash();
        boolean valid = current.equals(hash);
        
        if (!valid) {
            System.err.println("⚠️  SPORE INTEGRITY COMPROMISED!");
            System.err.println("   Expected: " + hash.substring(0, 16) + "...");
            System.err.println("   Got: " + current.substring(0, 16) + "...");
        }
        
        return valid;
    }
    
    /**
     * Save spore to file
     */
    public void saveToFile(String filepath) throws IOException {
        Path path = Paths.get(filepath);
        
        // Ensure parent directory exists
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filepath))) {
            oos.writeObject(this);
            
            System.out.println("💾 SPORE saved to: " + filepath);
            System.out.println("   Size: " + Files.size(path) + " bytes");
            System.out.println("   Hash: " + hash.substring(0, 16) + "...");
        }
    }
    
    /**
     * Load spore from file
     */
    public static FraynixSpore loadFromFile(String filepath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filepath))) {
            
            FraynixSpore spore = (FraynixSpore) ois.readObject();
            
            System.out.println("🌱 SPORE loaded from: " + filepath);
            System.out.println("   Age: " + (System.currentTimeMillis() - spore.timestamp) / 1000 + "s");
            System.out.println("   Generation: " + spore.getGenome().getGeneration());
            
            // Verify integrity
            if (!spore.verifyHash()) {
                throw new IOException("Spore corrupted - hash mismatch!");
            }
            
            return spore;
        }
    }
    
    /**
     * Get spore age in milliseconds
     */
    public long getAge() {
        return System.currentTimeMillis() - timestamp;
    }
    
    /**
     * Get compressed size
     */
    public int getSize() {
        return compressedData != null ? compressedData.length : 0;
    }
    
    /**
     * Get version
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * Get hash
     */
    public String getHash() {
        return hash;
    }
    
    @Override
    public String toString() {
        return "FraynixSpore[Gen" + genome.getGeneration() + ", " + 
               (getSize() / 1024) + "KB, " + 
               hash.substring(0, 8) + "...]";
    }
}
