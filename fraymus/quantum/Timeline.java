package fraymus.quantum;

/**
 * TIMELINE: A Reality State Container
 * 
 * Represents a branching path in the multiverse with measurable properties:
 * - Entropy: The chaos/disorder level in this timeline
 * - Coherence: The logical consistency/stability of this timeline
 * 
 * Used by QuantumOracle to simulate parallel reality paths.
 */
public class Timeline implements Cloneable {
    
    private double entropy;
    private double coherence;
    private String id;
    private String engineType;
    
    /**
     * Create a new Timeline with initial state
     */
    public Timeline(String id, double entropy, double coherence) {
        this.id = id;
        this.entropy = entropy;
        this.coherence = coherence;
        this.engineType = "ORIGINAL";
    }
    
    /**
     * Copy constructor for cloning
     */
    private Timeline(String id, double entropy, double coherence, String engineType) {
        this.id = id;
        this.entropy = entropy;
        this.coherence = coherence;
        this.engineType = engineType;
    }
    
    /**
     * Create a deep copy of this Timeline
     */
    @Override
    public Timeline clone() {
        return new Timeline(this.id, this.entropy, this.coherence, this.engineType);
    }
    
    /**
     * Calculate fitness using the formula: Fitness = Coherence - (Entropy * 2.0)
     */
    public double calculateFitness() {
        return coherence - (entropy * 2.0);
    }
    
    // Getters and Setters
    
    public double getEntropy() {
        return entropy;
    }
    
    public void setEntropy(double entropy) {
        this.entropy = entropy;
    }
    
    public double getCoherence() {
        return coherence;
    }
    
    public void setCoherence(double coherence) {
        this.coherence = coherence;
    }
    
    public String getId() {
        return id;
    }
    
    public String getEngineType() {
        return engineType;
    }
    
    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }
    
    @Override
    public String toString() {
        return String.format("Timeline[id=%s, engine=%s, entropy=%.4f, coherence=%.4f, fitness=%.4f]",
                id, engineType, entropy, coherence, calculateFitness());
    }
}
