package fraymus.run;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * RunConfig: Immutable configuration for reproducible scientific runs.
 * 
 * Contains all parameters needed to reproduce a computational experiment:
 * - Random seed for deterministic behavior
 * - Simulation parameters (steps, population)
 * - Physics constants (gravity, fusion thresholds)
 * - Output configuration (directory, formats)
 * 
 * Used by: CancerResearch, DrugDiscovery, ProteinFolding, and all physics engines.
 */
public class RunConfig {
    
    // Reproducibility
    public final long seed;
    
    // Simulation parameters
    public final int steps;
    public final int populationSize;
    
    // Physics constants
    public final double gravityConstant;
    public final double fusionDistance;
    public final double energyThreshold;
    
    // Output configuration
    public final Path outDir;
    public final boolean prettyConsole;
    public final boolean jsonl;
    
    private RunConfig(Builder builder) {
        this.seed = builder.seed;
        this.steps = builder.steps;
        this.populationSize = builder.populationSize;
        this.gravityConstant = builder.gravityConstant;
        this.fusionDistance = builder.fusionDistance;
        this.energyThreshold = builder.energyThreshold;
        this.outDir = builder.outDir;
        this.prettyConsole = builder.prettyConsole;
        this.jsonl = builder.jsonl;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private long seed = System.nanoTime();
        private int steps = 1000;
        private int populationSize = 100;
        private double gravityConstant = 1.618033988749895; // φ
        private double fusionDistance = 2.0;
        private double energyThreshold = 80.0;
        private Path outDir = Paths.get("./runs");
        private boolean prettyConsole = true;
        private boolean jsonl = true;
        
        public Builder seed(long seed) {
            this.seed = seed;
            return this;
        }
        
        public Builder steps(int steps) {
            this.steps = steps;
            return this;
        }
        
        public Builder populationSize(int populationSize) {
            this.populationSize = populationSize;
            return this;
        }
        
        public Builder gravityConstant(double gravityConstant) {
            this.gravityConstant = gravityConstant;
            return this;
        }
        
        public Builder fusionDistance(double fusionDistance) {
            this.fusionDistance = fusionDistance;
            return this;
        }
        
        public Builder energyThreshold(double energyThreshold) {
            this.energyThreshold = energyThreshold;
            return this;
        }
        
        public Builder outDir(Path outDir) {
            this.outDir = outDir;
            return this;
        }
        
        public Builder outDir(String outDir) {
            this.outDir = Paths.get(outDir);
            return this;
        }
        
        public Builder prettyConsole(boolean prettyConsole) {
            this.prettyConsole = prettyConsole;
            return this;
        }
        
        public Builder jsonl(boolean jsonl) {
            this.jsonl = jsonl;
            return this;
        }
        
        public RunConfig build() {
            return new RunConfig(this);
        }
    }
    
    @Override
    public String toString() {
        return String.format(
            "RunConfig{seed=%d, steps=%d, pop=%d, g=%.4f, fusionD=%.2f, energyT=%.2f, out=%s}",
            seed, steps, populationSize, gravityConstant, fusionDistance, energyThreshold, outDir
        );
    }
}
