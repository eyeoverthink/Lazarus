package fraymus.quantum;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * QUANTUM ORACLE: Multi-Threaded Reality Simulator
 * 
 * Patent: FRAYMUS ARCHITECT Implementation
 * 
 * The Oracle manages parallel reality simulations:
 * 1. FORK: Clone input Timeline into 3 separate paths
 * 2. SIMULATE: Run each path through a different RealityEngine in parallel:
 *    - Alpha (Deterministic): Reduces entropy using hard logic
 *    - Beta (Stochastic): Increases complexity using Quantum Randomness
 *    - Gamma (Heuristic): Optimizes coherence using Golden Ratio (φ = 1.618)
 * 3. COLLAPSE: Wait for all threads, calculate Fitness = Coherence - (Entropy * 2.0)
 * 4. MERGE: Return the Timeline with the highest Fitness
 * 
 * Thread-safe and handles InterruptedException gracefully.
 */
public class QuantumOracle {
    
    private final ExecutorService executorService;
    private final RealityEngine alphaEngine;
    private final RealityEngine betaEngine;
    private final RealityEngine gammaEngine;
    private final int threadPoolSize;
    
    /**
     * Create a QuantumOracle with default thread pool size (3)
     */
    public QuantumOracle() {
        this(3);
    }
    
    /**
     * Create a QuantumOracle with specified thread pool size
     * 
     * @param threadPoolSize Number of threads in the pool
     */
    public QuantumOracle(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.alphaEngine = new AlphaRealityEngine();
        this.betaEngine = new BetaRealityEngine();
        this.gammaEngine = new GammaRealityEngine();
    }
    
    /**
     * Consult the Oracle: Simulate 3 parallel timelines and return the best one
     * 
     * Protocol:
     * 1. FORK: Clone input Timeline into 3 paths
     * 2. SIMULATE: Pass each to a specific RealityEngine in parallel
     * 3. COLLAPSE: Wait for all threads, calculate Fitness
     * 4. MERGE: Return Timeline with highest Fitness
     * 
     * @param input The Timeline to simulate
     * @return The Timeline with the highest fitness after simulation
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public Timeline consult(Timeline input) throws InterruptedException {
        // FORK: Clone the input Timeline into 3 separate paths
        Timeline alphaPath = input.clone();
        Timeline betaPath = input.clone();
        Timeline gammaPath = input.clone();
        
        // Create simulation tasks for each engine
        Callable<Timeline> alphaTask = () -> {
            alphaEngine.simulate(alphaPath);
            return alphaPath;
        };
        
        Callable<Timeline> betaTask = () -> {
            betaEngine.simulate(betaPath);
            return betaPath;
        };
        
        Callable<Timeline> gammaTask = () -> {
            gammaEngine.simulate(gammaPath);
            return gammaPath;
        };
        
        // SIMULATE: Submit all tasks to run in parallel
        List<Future<Timeline>> futures = new ArrayList<>();
        futures.add(executorService.submit(alphaTask));
        futures.add(executorService.submit(betaTask));
        futures.add(executorService.submit(gammaTask));
        
        // COLLAPSE: Wait for all threads to complete and collect results
        List<Timeline> results = new ArrayList<>();
        for (Future<Timeline> future : futures) {
            try {
                Timeline result = future.get(); // Blocks until the computation is complete
                results.add(result);
            } catch (ExecutionException e) {
                // Handle execution exceptions gracefully
                System.err.println("ERROR: Simulation failed: " + e.getMessage());
                throw new RuntimeException("Timeline simulation failed", e.getCause());
            }
        }
        
        // MERGE: Calculate Fitness for each and return the best one
        Timeline bestTimeline = null;
        double bestFitness = Double.NEGATIVE_INFINITY;
        
        for (Timeline timeline : results) {
            double fitness = timeline.calculateFitness();
            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestTimeline = timeline;
            }
        }
        
        return bestTimeline;
    }
    
    /**
     * Shutdown the executor service gracefully
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("ERROR: ExecutorService did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Get the number of threads in the pool
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }
    
    /**
     * Check if the executor service is shut down
     */
    public boolean isShutdown() {
        return executorService.isShutdown();
    }
}
