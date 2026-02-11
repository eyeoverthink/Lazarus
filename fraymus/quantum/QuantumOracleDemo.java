package fraymus.quantum;

/**
 * QUANTUM ORACLE DEMONSTRATION
 * 
 * Shows the QuantumOracle in action with detailed output
 * demonstrating the FORK-SIMULATE-COLLAPSE-MERGE protocol.
 */
public class QuantumOracleDemo {
    
    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║   QUANTUM ORACLE: Multi-Threaded Reality Simulator           ║");
        System.out.println("║   FRAYMUS ARCHITECT Implementation                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        QuantumOracle oracle = new QuantumOracle();
        
        try {
            // Create initial Timeline
            Timeline input = new Timeline("REALITY_ALPHA", 8.0, 5.0);
            
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("   INITIAL STATE");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("   " + input);
            System.out.println("   Initial Fitness: " + String.format("%.4f", input.calculateFitness()));
            System.out.println();
            
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("   PROTOCOL: FORK → SIMULATE → COLLAPSE → MERGE");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("   1. FORK:      Cloning Timeline into 3 parallel paths");
            System.out.println("   2. SIMULATE:  Running through RealityEngines:");
            System.out.println("      • Alpha (Deterministic)  - Reduces entropy via hard logic");
            System.out.println("      • Beta (Stochastic)      - Adds quantum randomness");
            System.out.println("      • Gamma (Heuristic)      - Optimizes via Golden Ratio φ");
            System.out.println("   3. COLLAPSE:  Waiting for all threads to complete");
            System.out.println("   4. MERGE:     Selecting Timeline with highest Fitness");
            System.out.println();
            
            System.out.println(">> Consulting Oracle...");
            System.out.println();
            
            Timeline result = oracle.consult(input);
            
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("   RESULT: OPTIMAL TIMELINE");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("   " + result);
            System.out.println("   Selected Engine: " + result.getEngineType());
            System.out.println("   Final Fitness:   " + String.format("%.4f", result.calculateFitness()));
            System.out.println();
            
            // Demonstrate with different initial states
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("   MULTIPLE SIMULATIONS");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println();
            
            Timeline[] testCases = {
                new Timeline("LOW_ENTROPY", 2.0, 10.0),
                new Timeline("HIGH_ENTROPY", 10.0, 10.0),
                new Timeline("LOW_COHERENCE", 5.0, 3.0),
                new Timeline("BALANCED", 5.0, 10.0)
            };
            
            for (Timeline test : testCases) {
                System.out.println("   Input:  " + test);
                Timeline output = oracle.consult(test);
                System.out.println("   Output: " + output);
                System.out.println("   Engine: " + output.getEngineType() + 
                    " | Fitness Improvement: " + 
                    String.format("%.4f → %.4f", test.calculateFitness(), output.calculateFitness()));
                System.out.println();
            }
            
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("   MATHEMATICAL VERIFICATION");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("   Fitness Formula: Coherence - (Entropy × 2.0)");
            System.out.println();
            System.out.println("   Alpha Engine:  entropy *= 0.5  (Deterministic reduction)");
            System.out.println("   Beta Engine:   entropy += rand (Quantum noise)");
            System.out.println("   Gamma Engine:  entropy /= φ, coherence *= φ (Golden Ratio)");
            System.out.println("   where φ = 1.618033988749895...");
            System.out.println();
            
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("   THREAD POOL STATUS");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("   Pool Size: " + oracle.getThreadPoolSize() + " threads");
            System.out.println("   Shutdown:  " + (oracle.isShutdown() ? "Yes" : "No"));
            System.out.println();
            
        } catch (InterruptedException e) {
            System.err.println("ERROR: Simulation interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            System.out.println(">> Shutting down Oracle...");
            oracle.shutdown();
            System.out.println(">> Oracle shutdown complete");
            System.out.println();
            System.out.println("╔═══════════════════════════════════════════════════════════════╗");
            System.out.println("║   QUANTUM ORACLE DEMONSTRATION COMPLETE                       ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        }
    }
}
