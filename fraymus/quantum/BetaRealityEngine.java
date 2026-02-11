package fraymus.quantum;

import java.security.SecureRandom;

/**
 * BETA REALITY ENGINE: Stochastic Quantum Processor
 * 
 * Uses Quantum Randomness (Bit flips/Hadamard gates) to increase complexity.
 * 
 * The stochastic approach:
 * - Entropy increases due to quantum randomness
 * - Coherence fluctuates based on quantum bit flips
 * 
 * Simulation: 
 * - entropy += quantum_randomness (0.0 to 0.5)
 * - coherence modified by Hadamard-like transformation
 */
public class BetaRealityEngine implements RealityEngine {
    
    private static final SecureRandom quantum = new SecureRandom();
    private static final double MAX_QUANTUM_NOISE = 0.5;
    private static final double HADAMARD_SCALE = 1.0 / Math.sqrt(2.0); // ~0.707
    
    @Override
    public void simulate(Timeline timeline) {
        double currentEntropy = timeline.getEntropy();
        double currentCoherence = timeline.getCoherence();
        
        // Quantum randomness: bit flips add entropy
        double quantumNoise = quantum.nextDouble() * MAX_QUANTUM_NOISE;
        double newEntropy = currentEntropy + quantumNoise;
        
        // Hadamard gate effect: creates superposition, affects coherence
        // Simulates quantum interference pattern
        double hadamardFactor = HADAMARD_SCALE * (1.0 + quantum.nextDouble() - 0.5);
        double newCoherence = currentCoherence * hadamardFactor;
        
        // Ensure coherence doesn't go negative
        if (newCoherence < 0) {
            newCoherence = 0;
        }
        
        timeline.setEntropy(newEntropy);
        timeline.setCoherence(newCoherence);
        timeline.setEngineType("BETA_STOCHASTIC");
    }
    
    @Override
    public String getEngineType() {
        return "BETA_STOCHASTIC";
    }
}
