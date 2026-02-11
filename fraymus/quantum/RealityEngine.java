package fraymus.quantum;

/**
 * REALITY ENGINE: Timeline Transformation Protocol
 * 
 * Each implementation applies a different simulation strategy to a Timeline:
 * - Alpha (Deterministic): Reduces entropy using hard logic
 * - Beta (Stochastic): Increases complexity using Quantum Randomness
 * - Gamma (Heuristic): Optimizes coherence using the Golden Ratio
 */
public interface RealityEngine {
    
    /**
     * Simulate the Timeline and modify its state
     * 
     * @param timeline The Timeline to transform
     */
    void simulate(Timeline timeline);
    
    /**
     * Get the engine type identifier
     */
    String getEngineType();
}
