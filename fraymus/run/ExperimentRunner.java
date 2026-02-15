package fraymus.run;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * ExperimentRunner - Orchestrates multiple scientific computing experiments
 * 
 * Features:
 * - Parameter sweeps
 * - Statistical analysis
 * - Reproducibility verification
 * - Comparative benchmarking
 */
public class ExperimentRunner {
    
    /**
     * Run a single experiment with given configuration
     */
    public static ExperimentResult runExperiment(
            String engineName,
            RunConfig config,
            ExperimentEngine engine) throws IOException {
        
        long startTime = System.currentTimeMillis();
        RunContext ctx = RunContext.create(config, engineName);
        
        try {
            engine.run(ctx);
            long elapsedMs = System.currentTimeMillis() - startTime;
            
            return new ExperimentResult(
                engineName,
                config.seed,
                elapsedMs,
                ctx.log.getOutputPath(),
                true,
                null
            );
        } catch (Exception e) {
            long elapsedMs = System.currentTimeMillis() - startTime;
            return new ExperimentResult(
                engineName,
                config.seed,
                elapsedMs,
                null,
                false,
                e.getMessage()
            );
        } finally {
            ctx.close();
        }
    }
    
    /**
     * Run parameter sweep across multiple seed values
     */
    public static List<ExperimentResult> runSeedSweep(
            String engineName,
            RunConfig baseConfig,
            ExperimentEngine engine,
            long[] seeds) throws IOException {
        
        List<ExperimentResult> results = new ArrayList<>();
        
        System.out.println("=== Seed Sweep: " + engineName + " ===");
        System.out.println("Seeds: " + seeds.length);
        System.out.println();
        
        for (int i = 0; i < seeds.length; i++) {
            long seed = seeds[i];
            System.out.printf("[%d/%d] Running seed %d...\n", i + 1, seeds.length, seed);
            
            RunConfig config = RunConfig.builder()
                .from(baseConfig)
                .seed(seed)
                .build();
            
            ExperimentResult result = runExperiment(engineName + "-seed" + seed, config, engine);
            results.add(result);
            
            if (result.success) {
                System.out.printf("  ✓ Completed in %dms\n", result.elapsedMs);
            } else {
                System.out.printf("  ✗ Failed: %s\n", result.error);
            }
        }
        
        System.out.println();
        printSummary(results);
        
        return results;
    }
    
    /**
     * Run parameter sweep across population sizes
     */
    public static List<ExperimentResult> runPopulationSweep(
            String engineName,
            RunConfig baseConfig,
            ExperimentEngine engine,
            int[] populations) throws IOException {
        
        List<ExperimentResult> results = new ArrayList<>();
        
        System.out.println("=== Population Sweep: " + engineName + " ===");
        System.out.println("Populations: " + Arrays.toString(populations));
        System.out.println();
        
        for (int i = 0; i < populations.length; i++) {
            int pop = populations[i];
            System.out.printf("[%d/%d] Running population %d...\n", i + 1, populations.length, pop);
            
            RunConfig config = RunConfig.builder()
                .from(baseConfig)
                .populationSize(pop)
                .build();
            
            ExperimentResult result = runExperiment(engineName + "-pop" + pop, config, engine);
            results.add(result);
            
            if (result.success) {
                System.out.printf("  ✓ Completed in %dms (%.2f ms/entity)\n", 
                    result.elapsedMs, (double) result.elapsedMs / pop);
            } else {
                System.out.printf("  ✗ Failed: %s\n", result.error);
            }
        }
        
        System.out.println();
        printSummary(results);
        
        return results;
    }
    
    /**
     * Verify reproducibility by running same seed multiple times
     */
    public static boolean verifyReproducibility(
            String engineName,
            RunConfig config,
            ExperimentEngine engine,
            int iterations) throws IOException {
        
        System.out.println("=== Reproducibility Test: " + engineName + " ===");
        System.out.println("Seed: " + config.seed);
        System.out.println("Iterations: " + iterations);
        System.out.println();
        
        List<String> checksums = new ArrayList<>();
        
        for (int i = 0; i < iterations; i++) {
            System.out.printf("[%d/%d] Running iteration...\n", i + 1, iterations);
            
            ExperimentResult result = runExperiment(
                engineName + "-repro" + i, config, engine);
            
            if (!result.success) {
                System.out.println("  ✗ Failed: " + result.error);
                return false;
            }
            
            // Calculate checksum of output file
            String checksum = calculateChecksum(result.outputPath);
            checksums.add(checksum);
            
            System.out.printf("  Checksum: %s\n", checksum);
        }
        
        // All checksums should be identical
        boolean reproducible = checksums.stream().distinct().count() == 1;
        
        System.out.println();
        if (reproducible) {
            System.out.println("✓ REPRODUCIBLE: All runs identical");
        } else {
            System.out.println("✗ NOT REPRODUCIBLE: Different outputs");
        }
        
        return reproducible;
    }
    
    /**
     * Compare multiple engines on same problem
     */
    public static void compareEngines(
            Map<String, ExperimentEngine> engines,
            RunConfig config) throws IOException {
        
        System.out.println("=== Engine Comparison ===");
        System.out.println("Engines: " + engines.size());
        System.out.println("Config: seed=" + config.seed + ", steps=" + config.steps);
        System.out.println();
        
        List<ExperimentResult> results = new ArrayList<>();
        
        for (Map.Entry<String, ExperimentEngine> entry : engines.entrySet()) {
            String name = entry.getKey();
            ExperimentEngine engine = entry.getValue();
            
            System.out.println("Running: " + name);
            ExperimentResult result = runExperiment(name, config, engine);
            results.add(result);
            
            if (result.success) {
                System.out.printf("  ✓ Completed in %dms\n", result.elapsedMs);
            } else {
                System.out.printf("  ✗ Failed: %s\n", result.error);
            }
        }
        
        System.out.println();
        printSummary(results);
    }
    
    private static void printSummary(List<ExperimentResult> results) {
        int success = (int) results.stream().filter(r -> r.success).count();
        int failed = results.size() - success;
        
        System.out.println("=== Summary ===");
        System.out.printf("Total: %d, Success: %d, Failed: %d\n", 
            results.size(), success, failed);
        
        if (success > 0) {
            long totalTime = results.stream()
                .filter(r -> r.success)
                .mapToLong(r -> r.elapsedMs)
                .sum();
            long avgTime = totalTime / success;
            long minTime = results.stream()
                .filter(r -> r.success)
                .mapToLong(r -> r.elapsedMs)
                .min()
                .orElse(0);
            long maxTime = results.stream()
                .filter(r -> r.success)
                .mapToLong(r -> r.elapsedMs)
                .max()
                .orElse(0);
            
            System.out.printf("Time: avg=%dms, min=%dms, max=%dms\n", 
                avgTime, minTime, maxTime);
        }
    }
    
    private static String calculateChecksum(Path path) throws IOException {
        if (path == null || !Files.exists(path)) {
            return "NO_FILE";
        }
        
        byte[] bytes = Files.readAllBytes(path);
        int hash = Arrays.hashCode(bytes);
        return String.format("%08x", hash);
    }
    
    /**
     * Functional interface for experiment engines
     */
    @FunctionalInterface
    public interface ExperimentEngine {
        void run(RunContext ctx) throws Exception;
    }
    
    /**
     * Result of a single experiment
     */
    public static class ExperimentResult {
        public final String engineName;
        public final long seed;
        public final long elapsedMs;
        public final Path outputPath;
        public final boolean success;
        public final String error;
        
        public ExperimentResult(String engineName, long seed, long elapsedMs,
                              Path outputPath, boolean success, String error) {
            this.engineName = engineName;
            this.seed = seed;
            this.elapsedMs = elapsedMs;
            this.outputPath = outputPath;
            this.success = success;
            this.error = error;
        }
    }
    
    /**
     * Demo: Run all examples
     */
    public static void main(String[] args) throws IOException {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║   Fraymus Experiment Runner - Demo        ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println();
        
        // Example 1: Seed sweep
        RunConfig baseConfig = RunConfig.builder()
            .steps(50)
            .populationSize(30)
            .prettyConsole(false)
            .build();
        
        long[] seeds = {12345, 54321, 99999};
        
        runSeedSweep("cancer-sweep", baseConfig,
            ctx -> new CancerResearchEngine().run(ctx), seeds);
        
        System.out.println();
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println();
        
        // Example 2: Population sweep
        int[] populations = {10, 30, 50, 100};
        
        runPopulationSweep("protein-sweep", baseConfig,
            ctx -> new ProteinFoldingEngine().run(ctx), populations);
        
        System.out.println();
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println();
        
        // Example 3: Reproducibility test
        RunConfig reproConfig = RunConfig.builder()
            .seed(12345)
            .steps(100)
            .populationSize(50)
            .build();
        
        boolean reproducible = verifyReproducibility("drug-repro", reproConfig,
            ctx -> new DrugDiscoveryEngine().run(ctx), 3);
        
        System.out.println();
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println();
        
        // Example 4: Engine comparison
        Map<String, ExperimentEngine> engines = new LinkedHashMap<>();
        engines.put("cancer", ctx -> new CancerResearchEngine().run(ctx));
        engines.put("drug", ctx -> new DrugDiscoveryEngine().run(ctx));
        engines.put("protein", ctx -> new ProteinFoldingEngine().run(ctx));
        
        RunConfig compareConfig = RunConfig.builder()
            .seed(42)
            .steps(100)
            .populationSize(50)
            .build();
        
        compareEngines(engines, compareConfig);
        
        System.out.println();
        System.out.println("✓ All experiments complete!");
    }
}
