package fraymus.bio;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

/**
 * NeuroQuant - Vector Identity for Planetary Cortex Nodes
 * 
 * Represents a node's unique semantic signature in 10,000-dimensional space.
 * Used for semantic routing and peer discovery.
 */
public class NeuroQuant implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public final String id;
    private final double[] vector;
    private final int dimension;
    
    /**
     * Create a new NeuroQuant with random vector
     */
    public NeuroQuant() {
        this(10000, System.currentTimeMillis());
    }
    
    /**
     * Create a new NeuroQuant with specified dimension and seed
     */
    public NeuroQuant(int dimension, long seed) {
        this.id = "NODE-" + UUID.randomUUID().toString();
        this.dimension = dimension;
        this.vector = new double[dimension];
        
        Random random = new Random(seed);
        for (int i = 0; i < dimension; i++) {
            this.vector[i] = random.nextGaussian();
        }
        
        normalize();
    }
    
    /**
     * Normalize the vector to unit length
     */
    private void normalize() {
        double magnitude = 0.0;
        for (double v : vector) {
            magnitude += v * v;
        }
        magnitude = Math.sqrt(magnitude);
        
        if (magnitude > 0) {
            for (int i = 0; i < dimension; i++) {
                vector[i] /= magnitude;
            }
        }
    }
    
    /**
     * Calculate cosine similarity with another NeuroQuant
     * Returns value between -1.0 and 1.0
     * 1.0 = identical, 0.0 = orthogonal, -1.0 = opposite
     */
    public double similarity(NeuroQuant other) {
        if (other.dimension != this.dimension) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        
        double dotProduct = 0.0;
        for (int i = 0; i < dimension; i++) {
            dotProduct += this.vector[i] * other.vector[i];
        }
        
        return dotProduct;
    }
    
    /**
     * Get the vector
     */
    public double[] getVector() {
        return vector.clone();
    }
    
    /**
     * Get dimension
     */
    public int getDimension() {
        return dimension;
    }
    
    @Override
    public String toString() {
        return String.format("NeuroQuant[id=%s, dim=%d]", id, dimension);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NeuroQuant)) return false;
        NeuroQuant other = (NeuroQuant) obj;
        return this.id.equals(other.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
