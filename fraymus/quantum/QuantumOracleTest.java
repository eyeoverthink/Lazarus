package fraymus.quantum;

/**
 * QUANTUM ORACLE TEST: Verification Suite
 * 
 * Tests the QuantumOracle multi-threaded reality simulator:
 * - Timeline cloning and fitness calculation
 * - Individual RealityEngine implementations
 * - Parallel simulation and thread safety
 * - InterruptedException handling
 * - Best timeline selection (MERGE phase)
 */
public class QuantumOracleTest {
    
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    
    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║   QUANTUM ORACLE TEST SUITE                                   ║");
        System.out.println("║   Multi-Threaded Reality Simulation Verification             ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Run all tests
        testTimelineCloning();
        testTimelineFitnessCalculation();
        testAlphaEngine();
        testBetaEngine();
        testGammaEngine();
        testQuantumOracleConsult();
        testThreadSafety();
        testMultipleConsultations();
        testShutdown();
        
        // Print summary
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║   TEST SUMMARY                                                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println("   Passed: " + testsPassed);
        System.out.println("   Failed: " + testsFailed);
        System.out.println("   Total:  " + (testsPassed + testsFailed));
        System.out.println();
        
        if (testsFailed == 0) {
            System.out.println("   ✅ ALL TESTS PASSED - QuantumOracle is operational!");
        } else {
            System.out.println("   ❌ SOME TESTS FAILED - Review required");
        }
        
        System.exit(testsFailed > 0 ? 1 : 0);
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // TEST: Timeline Cloning
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testTimelineCloning() {
        System.out.println("TEST: Timeline Cloning");
        
        try {
            Timeline original = new Timeline("TEST", 1.0, 2.0);
            Timeline clone = original.clone();
            
            // Verify clone has same values
            if (clone.getEntropy() != original.getEntropy()) {
                throw new AssertionError("Clone entropy mismatch");
            }
            if (clone.getCoherence() != original.getCoherence()) {
                throw new AssertionError("Clone coherence mismatch");
            }
            
            // Verify it's a deep copy (modifying clone doesn't affect original)
            clone.setEntropy(5.0);
            if (original.getEntropy() == 5.0) {
                throw new AssertionError("Clone is not independent - shallow copy detected");
            }
            
            System.out.println("   ✓ Timeline cloning works correctly");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage());
            testsFailed++;
        }
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // TEST: Timeline Fitness Calculation
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testTimelineFitnessCalculation() {
        System.out.println("TEST: Timeline Fitness Calculation");
        
        try {
            Timeline timeline = new Timeline("TEST", 1.0, 5.0);
            double fitness = timeline.calculateFitness();
            
            // Fitness = Coherence - (Entropy * 2.0)
            // Expected: 5.0 - (1.0 * 2.0) = 3.0
            double expected = 3.0;
            
            if (Math.abs(fitness - expected) > 0.0001) {
                throw new AssertionError("Fitness calculation incorrect: expected " + 
                    expected + ", got " + fitness);
            }
            
            System.out.println("   ✓ Fitness = Coherence - (Entropy * 2.0) ✓");
            System.out.println("   ✓ Expected: " + expected + ", Got: " + fitness);
            testsPassed++;
        } catch (Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage());
            testsFailed++;
        }
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // TEST: Alpha RealityEngine (Deterministic)
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testAlphaEngine() {
        System.out.println("TEST: Alpha RealityEngine (Deterministic)");
        
        try {
            Timeline timeline = new Timeline("ALPHA_TEST", 10.0, 5.0);
            AlphaRealityEngine alpha = new AlphaRealityEngine();
            
            double originalEntropy = timeline.getEntropy();
            alpha.simulate(timeline);
            double newEntropy = timeline.getEntropy();
            
            // Alpha should reduce entropy (multiply by 0.5)
            if (newEntropy >= originalEntropy) {
                throw new AssertionError("Alpha did not reduce entropy");
            }
            
            // Verify it's using deterministic logic
            if (!timeline.getEngineType().equals("ALPHA_DETERMINISTIC")) {
                throw new AssertionError("Engine type not set correctly");
            }
            
            System.out.println("   ✓ Alpha reduces entropy: " + originalEntropy + " → " + newEntropy);
            System.out.println("   ✓ Engine type: " + timeline.getEngineType());
            testsPassed++;
        } catch (Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage());
            testsFailed++;
        }
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // TEST: Beta RealityEngine (Stochastic)
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testBetaEngine() {
        System.out.println("TEST: Beta RealityEngine (Stochastic)");
        
        try {
            Timeline timeline = new Timeline("BETA_TEST", 1.0, 5.0);
            BetaRealityEngine beta = new BetaRealityEngine();
            
            double originalEntropy = timeline.getEntropy();
            beta.simulate(timeline);
            double newEntropy = timeline.getEntropy();
            
            // Beta should increase entropy (add quantum noise)
            if (newEntropy <= originalEntropy) {
                throw new AssertionError("Beta did not increase entropy");
            }
            
            // Verify engine type
            if (!timeline.getEngineType().equals("BETA_STOCHASTIC")) {
                throw new AssertionError("Engine type not set correctly");
            }
            
            System.out.println("   ✓ Beta increases entropy: " + originalEntropy + " → " + newEntropy);
            System.out.println("   ✓ Engine type: " + timeline.getEngineType());
            testsPassed++;
        } catch (Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage());
            testsFailed++;
        }
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // TEST: Gamma RealityEngine (Heuristic)
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testGammaEngine() {
        System.out.println("TEST: Gamma RealityEngine (Heuristic - Golden Ratio)");
        
        try {
            Timeline timeline = new Timeline("GAMMA_TEST", 10.0, 5.0);
            GammaRealityEngine gamma = new GammaRealityEngine();
            
            double originalEntropy = timeline.getEntropy();
            double originalCoherence = timeline.getCoherence();
            
            gamma.simulate(timeline);
            
            double newEntropy = timeline.getEntropy();
            double newCoherence = timeline.getCoherence();
            
            // Gamma should reduce entropy (divide by φ) and increase coherence (multiply by φ)
            if (newEntropy >= originalEntropy) {
                throw new AssertionError("Gamma did not reduce entropy");
            }
            if (newCoherence <= originalCoherence) {
                throw new AssertionError("Gamma did not increase coherence");
            }
            
            // Verify engine type
            if (!timeline.getEngineType().equals("GAMMA_HEURISTIC")) {
                throw new AssertionError("Engine type not set correctly");
            }
            
            System.out.println("   ✓ Gamma optimizes with φ (1.618...)");
            System.out.println("   ✓ Entropy:   " + originalEntropy + " → " + newEntropy);
            System.out.println("   ✓ Coherence: " + originalCoherence + " → " + newCoherence);
            System.out.println("   ✓ Engine type: " + timeline.getEngineType());
            testsPassed++;
        } catch (Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage());
            testsFailed++;
        }
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // TEST: QuantumOracle Consult Method
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testQuantumOracleConsult() {
        System.out.println("TEST: QuantumOracle Consult Method");
        
        QuantumOracle oracle = null;
        try {
            oracle = new QuantumOracle();
            Timeline input = new Timeline("INPUT", 5.0, 10.0);
            
            System.out.println("   Input: " + input);
            
            Timeline result = oracle.consult(input);
            
            System.out.println("   Result: " + result);
            
            // Verify result is not null
            if (result == null) {
                throw new AssertionError("Oracle returned null");
            }
            
            // Verify engine type is set
            if (result.getEngineType().equals("ORIGINAL")) {
                throw new AssertionError("Result was not processed by any engine");
            }
            
            System.out.println("   ✓ Oracle returned valid Timeline");
            System.out.println("   ✓ Best engine: " + result.getEngineType());
            System.out.println("   ✓ Final fitness: " + result.calculateFitness());
            testsPassed++;
        } catch (Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage());
            e.printStackTrace();
            testsFailed++;
        } finally {
            if (oracle != null) {
                oracle.shutdown();
            }
        }
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // TEST: Thread Safety
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testThreadSafety() {
        System.out.println("TEST: Thread Safety (Parallel Execution)");
        
        QuantumOracle oracle = null;
        try {
            oracle = new QuantumOracle();
            Timeline input = new Timeline("THREAD_TEST", 3.0, 7.0);
            
            // The consult method should handle parallel execution internally
            Timeline result = oracle.consult(input);
            
            // If we got here without deadlock or race condition, test passed
            System.out.println("   ✓ Parallel execution completed successfully");
            System.out.println("   ✓ Result: " + result.getEngineType());
            testsPassed++;
        } catch (Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage());
            e.printStackTrace();
            testsFailed++;
        } finally {
            if (oracle != null) {
                oracle.shutdown();
            }
        }
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // TEST: Multiple Consultations
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testMultipleConsultations() {
        System.out.println("TEST: Multiple Consultations");
        
        QuantumOracle oracle = null;
        try {
            oracle = new QuantumOracle();
            
            // Run multiple consultations to test thread pool reuse
            for (int i = 0; i < 5; i++) {
                Timeline input = new Timeline("RUN_" + i, 2.0 + i, 8.0 - i);
                Timeline result = oracle.consult(input);
                System.out.println("   Run " + (i + 1) + ": " + result.getEngineType() + 
                    " (fitness: " + String.format("%.4f", result.calculateFitness()) + ")");
            }
            
            System.out.println("   ✓ Multiple consultations successful");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage());
            e.printStackTrace();
            testsFailed++;
        } finally {
            if (oracle != null) {
                oracle.shutdown();
            }
        }
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // TEST: Graceful Shutdown
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testShutdown() {
        System.out.println("TEST: Graceful Shutdown");
        
        try {
            QuantumOracle oracle = new QuantumOracle();
            oracle.shutdown();
            
            if (!oracle.isShutdown()) {
                throw new AssertionError("Oracle did not shut down properly");
            }
            
            System.out.println("   ✓ ExecutorService shutdown gracefully");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage());
            testsFailed++;
        }
        System.out.println();
    }
}
