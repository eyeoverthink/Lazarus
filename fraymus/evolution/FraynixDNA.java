package fraymus.evolution;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * 🧬 THE GENOME - Immutable Source of Truth
 * 
 * This is NOT code - it's GENETIC INFORMATION.
 * The organism is just a temporary expression of this DNA.
 * 
 * Key Concept:
 * - DNA (Genotype): What the system CAN be
 * - Organism (Phenotype): What it currently IS
 * 
 * This separation makes Fraynix antifragile - the organism can die,
 * but the genome survives and creates a better version.
 */
public class FraynixDNA implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // GENETIC CODE: Trait -> Expression
    private Map<String, String> genes;
    
    // VERSION TRACKING
    private String version;
    private int generation;
    private long timestamp;
    
    // LINEAGE
    private String parentGenome;
    private List<String> mutations;
    
    // INTEGRITY
    private String checksum;
    
    /**
     * Create default genome with baseline genes
     */
    public FraynixDNA() {
        this.genes = new HashMap<>();
        this.version = "1.0.0";
        this.generation = 0;
        this.timestamp = System.currentTimeMillis();
        this.parentGenome = null;
        this.mutations = new ArrayList<>();
        
        // Initialize with default genes
        initializeDefaultGenes();
        
        // Calculate integrity checksum
        this.checksum = calculateChecksum();
        
        System.out.println("🧬 NEW GENOME created");
        System.out.println("   Generation: " + generation);
        System.out.println("   Genes: " + genes.size());
    }
    
    /**
     * Clone constructor for creating offspring
     */
    private FraynixDNA(FraynixDNA parent) {
        this.genes = new HashMap<>(parent.genes);
        this.version = parent.version;
        this.generation = parent.generation + 1;
        this.timestamp = System.currentTimeMillis();
        this.parentGenome = parent.checksum;
        this.mutations = new ArrayList<>(parent.mutations);
        this.checksum = calculateChecksum();
    }
    
    /**
     * Initialize baseline genetic code
     */
    private void initializeDefaultGenes() {
        // ARCHITECTURE GENES
        genes.put("MemoryArchitecture", "VectorDatabase");
        genes.put("BrainStructure", "4DTesseract");
        genes.put("PhysicsEngine", "HebianGravity");
        genes.put("StorageFormat", "JSONL");
        
        // BEHAVIOR GENES
        genes.put("Personality", "StoicEngineer");
        genes.put("LearningRate", "Adaptive");
        genes.put("DecisionStrategy", "PhysicsBased");
        genes.put("CreativityLevel", "Balanced");
        
        // EVOLUTION GENES
        genes.put("MutationRate", "0.01");
        genes.put("FitnessMetric", "Entropy");
        genes.put("SelectionPressure", "Survival");
        genes.put("AdaptationSpeed", "Moderate");
        
        // SAFETY GENES
        genes.put("NullSafety", "Enabled");
        genes.put("MemoryLimit", "Moderate");
        genes.put("TimeoutDuration", "Standard");
        genes.put("ErrorRecovery", "Aggressive");
        
        // OPTIMIZATION GENES
        genes.put("CacheStrategy", "LRU");
        genes.put("CompressionLevel", "Medium");
        genes.put("ParallelismDegree", "Auto");
        genes.put("ResourceUsage", "Efficient");
    }
    
    /**
     * Set or update a gene
     */
    public void setGene(String trait, String expression) {
        genes.put(trait, expression);
        checksum = calculateChecksum();
    }
    
    /**
     * Get gene expression
     */
    public String getGene(String trait) {
        return genes.getOrDefault(trait, "Unknown");
    }
    
    /**
     * Get all genes
     */
    public Map<String, String> getGenes() {
        return new HashMap<>(genes);
    }
    
    /**
     * Mutate a specific gene
     */
    public void mutateGene(String trait) {
        String current = genes.get(trait);
        if (current == null) return;
        
        // Generate mutation (simple example - could be more sophisticated)
        String mutated = mutateValue(current);
        genes.put(trait, mutated);
        
        // Record mutation
        mutations.add("Gen" + generation + ": Mutated " + trait + " (" + current + " → " + mutated + ")");
        checksum = calculateChecksum();
    }
    
    /**
     * Mutate value (simple string manipulation)
     */
    private String mutateValue(String value) {
        // This is a simplified mutation - in reality would be more intelligent
        if (value.matches("\\d+(\\.\\d+)?")) {
            // Numeric value - adjust slightly
            double num = Double.parseDouble(value);
            double mutation = (Math.random() - 0.5) * 0.1 * num;
            return String.format("%.2f", num + mutation);
        } else {
            // String value - keep for now (could use AI to suggest alternatives)
            return value;
        }
    }
    
    /**
     * Create a mutated clone
     */
    public FraynixDNA clone() {
        return new FraynixDNA(this);
    }
    
    /**
     * Mutate entire genome at specified rate
     */
    public FraynixDNA mutate(double rate) {
        FraynixDNA mutated = this.clone();
        
        for (String trait : genes.keySet()) {
            if (Math.random() < rate) {
                mutated.mutateGene(trait);
            }
        }
        
        return mutated;
    }
    
    /**
     * Mutate based on crash analysis
     */
    public FraynixDNA mutateFromCrash(String crashType, String crashMessage) {
        FraynixDNA mutated = this.clone();
        
        // Intelligent mutation based on crash type
        if (crashType.contains("NullPointer")) {
            mutated.setGene("NullSafety", "Strict");
            mutated.recordMutation("CrashRecovery: Enhanced null safety");
        }
        
        if (crashType.contains("OutOfMemory")) {
            mutated.setGene("MemoryLimit", "Conservative");
            mutated.recordMutation("CrashRecovery: Reduced memory usage");
        }
        
        if (crashType.contains("Timeout")) {
            mutated.setGene("TimeoutDuration", "Extended");
            mutated.recordMutation("CrashRecovery: Increased timeout");
        }
        
        if (crashType.contains("StackOverflow")) {
            mutated.setGene("RecursionDepth", "Limited");
            mutated.recordMutation("CrashRecovery: Limited recursion");
        }
        
        // General mutation to explore solution space
        mutated = mutated.mutate(0.01);
        
        return mutated;
    }
    
    /**
     * Record a mutation in lineage
     */
    public void recordMutation(String description) {
        mutations.add("Gen" + generation + ": " + description);
    }
    
    /**
     * Increment generation counter
     */
    public void incrementGeneration() {
        generation++;
        timestamp = System.currentTimeMillis();
        checksum = calculateChecksum();
    }
    
    /**
     * Calculate checksum for integrity verification
     */
    public String calculateChecksum() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // Combine all genetic information
            StringBuilder data = new StringBuilder();
            for (Map.Entry<String, String> gene : genes.entrySet()) {
                data.append(gene.getKey()).append(":").append(gene.getValue()).append(";");
            }
            
            byte[] hash = md.digest(data.toString().getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
            
        } catch (Exception e) {
            return "CHECKSUM_ERROR";
        }
    }
    
    /**
     * Verify genome integrity
     */
    public boolean verify() {
        String current = calculateChecksum();
        return current.equals(checksum);
    }
    
    /**
     * Get generation number
     */
    public int getGeneration() {
        return generation;
    }
    
    /**
     * Get version string
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * Get lineage (mutation history)
     */
    public List<String> getLineage() {
        return new ArrayList<>(mutations);
    }
    
    /**
     * Get parent genome checksum
     */
    public String getParentGenome() {
        return parentGenome;
    }
    
    /**
     * Get checksum
     */
    public String getChecksum() {
        return checksum;
    }
    
    /**
     * Save genome to disk
     */
    public void save(String filepath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filepath))) {
            oos.writeObject(this);
            System.out.println("💾 GENOME saved to: " + filepath);
            System.out.println("   Generation: " + generation);
            System.out.println("   Checksum: " + checksum.substring(0, 16) + "...");
        }
    }
    
    /**
     * Load genome from disk
     */
    public static FraynixDNA load(String filepath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filepath))) {
            FraynixDNA dna = (FraynixDNA) ois.readObject();
            
            // Verify integrity
            if (!dna.verify()) {
                throw new IOException("Genome corrupted - checksum mismatch!");
            }
            
            System.out.println("🧬 GENOME loaded from: " + filepath);
            System.out.println("   Generation: " + dna.generation);
            System.out.println("   Genes: " + dna.genes.size());
            
            return dna;
        }
    }
    
    /**
     * Create default genome file if it doesn't exist
     */
    public static FraynixDNA createDefault() {
        return new FraynixDNA();
    }
    
    /**
     * Express genome into organism
     * This is where genotype becomes phenotype
     */
    public String express() {
        StringBuilder expression = new StringBuilder();
        expression.append("🧬 GENOME EXPRESSION\n");
        expression.append("Generation: ").append(generation).append("\n");
        expression.append("Version: ").append(version).append("\n");
        expression.append("\nGENETIC CODE:\n");
        
        for (Map.Entry<String, String> gene : genes.entrySet()) {
            expression.append("  ").append(gene.getKey())
                     .append(" → ").append(gene.getValue()).append("\n");
        }
        
        if (!mutations.isEmpty()) {
            expression.append("\nLINEAGE:\n");
            for (String mutation : mutations) {
                expression.append("  ").append(mutation).append("\n");
            }
        }
        
        return expression.toString();
    }
    
    @Override
    public String toString() {
        return "FraynixDNA[Gen" + generation + ", " + genes.size() + " genes, " + 
               checksum.substring(0, 8) + "...]";
    }
}
