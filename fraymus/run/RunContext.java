package fraymus.run;

import java.util.SplittableRandom;

/**
 * RunContext: Shared context for reproducible scientific runs.
 * 
 * Contains everything needed for a reproducible computational experiment:
 * - RunConfig: Immutable configuration
 * - SplittableRandom: Deterministic RNG (seeded from config)
 * - EventLogger: JSONL logging
 * - RunClock: Timing information
 * 
 * CRITICAL RNG RULES:
 * - NO new Random() anywhere in your code
 * - NO Math.random() anywhere in your code
 * - ALL randomness comes from ctx.rng
 * 
 * This ensures:
 * - Same seed → Same output (reproducibility)
 * - All randomness is tracked and logged
 * - Results are defensible and verifiable
 * 
 * Usage:
 * 
 *   RunConfig cfg = RunConfig.builder()
 *       .seed(12345)
 *       .steps(1000)
 *       .build();
 *   
 *   RunContext ctx = RunContext.create(cfg, "my-experiment");
 *   
 *   // Use ctx.rng for ALL random numbers
 *   double x = ctx.rng.nextDouble();
 *   int i = ctx.rng.nextInt(100);
 *   
 *   // Log events
 *   ctx.log.step(i, Map.of("metric", 42.0));
 *   
 *   // Cleanup
 *   ctx.close();
 */
public class RunContext {
    
    public final RunConfig cfg;
    public final SplittableRandom rng;
    public final EventLogger log;
    public final RunClock clock;
    
    private RunContext(RunConfig cfg, EventLogger log, RunClock clock) {
        this.cfg = cfg;
        this.rng = new SplittableRandom(cfg.seed);
        this.log = log;
        this.clock = clock;
    }
    
    /**
     * Create a new RunContext with the given config and run name.
     * 
     * @param cfg Configuration for this run
     * @param runName Name for this run (used in file naming)
     * @return A new RunContext ready to use
     */
    public static RunContext create(RunConfig cfg, String runName) {
        try {
            RunClock clock = new RunClock();
            EventLogger log = new EventLogger(cfg, clock, runName);
            return new RunContext(cfg, log, clock);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create RunContext: " + e.getMessage(), e);
        }
    }
    
    /**
     * Split the RNG for parallel processing.
     * Returns a new independent RNG that won't interfere with this one.
     */
    public SplittableRandom splitRng() {
        return rng.split();
    }
    
    /**
     * Close this context and flush all logs.
     */
    public void close() {
        log.close();
    }
    
    /**
     * Convenience: nextDouble from [0, 1)
     */
    public double nextDouble() {
        return rng.nextDouble();
    }
    
    /**
     * Convenience: nextInt from [0, bound)
     */
    public int nextInt(int bound) {
        return rng.nextInt(bound);
    }
    
    /**
     * Convenience: nextGaussian (mean=0, std=1)
     */
    public double nextGaussian() {
        return rng.nextGaussian();
    }
    
    /**
     * Convenience: nextBoolean
     */
    public boolean nextBoolean() {
        return rng.nextBoolean();
    }
    
    @Override
    public String toString() {
        return String.format("RunContext{cfg=%s, elapsed=%s}", cfg, clock.elapsed());
    }
}
