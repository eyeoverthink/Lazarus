package fraymus.evolution;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 🧬 GENETIC EVOLVER - Controlled Evolution Engine
 * 
 * Manages genetic evolution through:
 * - Fitness evaluation
 * - Selection pressure
 * - Mutation strategies
 * - Gene pool management
 * - Convergence tracking
 * 
 * This is DARWINIAN evolution for AI:
 * - Population of genomes
 * - Survival of the fittest
 * - Continuous improvement
 * - Emergent optimization
 */
public class GeneticEvolver {
    
    private List<FraynixDNA> genePool;
    private final int populationSize;
    private final double mutationRate;
    private final double selectionPressure;
    
    private int currentGeneration = 0;
    private double bestFitness = 0.0;
    private FraynixDNA bestGenome = null;
    
    /**
     * Initialize evolution engine
     */
    public GeneticEvolver(int populationSize, double mutationRate, double selectionPressure) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.selectionPressure = selectionPressure;
        this.genePool = new ArrayList<>();
        
        System.out.println("🧬 GENETIC EVOLVER initialized");
        System.out.println("   Population: " + populationSize);
        System.out.println("   Mutation rate: " + String.format("%.2f%%", mutationRate * 100));
        System.out.println("   Selection pressure: " + String.format("%.2f%%", selectionPressure * 100));
    }
    
    /**
     * Initialize gene pool with variations
     */
    public void initializeGenePool(FraynixDNA baseGenome) {
        System.out.println("\n🧬 Initializing gene pool...");
        
        genePool.clear();
        
        // Add base genome
        genePool.add(baseGenome);
        
        // Create variations
        for (int i = 1; i < populationSize; i++) {
            FraynixDNA variant = baseGenome.clone();
            variant = variant.mutate(mutationRate * 2); // Higher initial diversity
            genePool.add(variant);
        }
        
        System.out.println("   ✓ Created " + genePool.size() + " initial genomes");
    }
    
    /**
     * Evolve for specified number of generations
     */
    public FraynixDNA evolve(int generations) {
        System.out.println("\n🧬 EVOLUTION STARTED");
        System.out.println("   Generations: " + generations);
        
        for (int gen = 0; gen < generations; gen++) {
            currentGeneration = gen;
            
            // 1. Evaluate fitness
            Map<FraynixDNA, Double> scores = evaluateFitness();
            
            // 2. Select best
            List<FraynixDNA> selected = select(scores, selectionPressure);
            
            // 3. Reproduce with mutations
            List<FraynixDNA> offspring = reproduce(selected);
            
            // 4. Replace population
            genePool = offspring;
            
            // 5. Track best
            updateBest(scores);
            
            // 6. Report progress
            if (gen % 10 == 0 || gen == generations - 1) {
                reportProgress(gen, generations);
            }
        }
        
        System.out.println("\n✓ EVOLUTION COMPLETE");
        System.out.println("   Best fitness: " + String.format("%.2f", bestFitness));
        System.out.println("   Final generation: " + currentGeneration);
        
        return bestGenome;
    }
    
    /**
     * Evaluate fitness of all genomes
     */
    private Map<FraynixDNA, Double> evaluateFitness() {
        Map<FraynixDNA, Double> scores = new HashMap<>();
        
        for (FraynixDNA genome : genePool) {
            double fitness = calculateFitness(genome);
            scores.put(genome, fitness);
        }
        
        return scores;
    }
    
    /**
     * Calculate fitness score for a genome
     */
    private double calculateFitness(FraynixDNA genome) {
        double fitness = 0.0;
        
        // Fitness based on multiple factors
        Map<String, String> genes = genome.getGenes();
        
        // 1. Gene diversity (more genes = higher fitness)
        fitness += genes.size() * 0.1;
        
        // 2. Prefer certain traits
        if ("Adaptive".equals(genes.get("LearningRate"))) {
            fitness += 10.0;
        }
        
        if ("PhysicsBased".equals(genes.get("DecisionStrategy"))) {
            fitness += 10.0;
        }
        
        if ("Enabled".equals(genes.get("NullSafety"))) {
            fitness += 5.0;
        }
        
        if ("Aggressive".equals(genes.get("ErrorRecovery"))) {
            fitness += 5.0;
        }
        
        // 3. Numeric gene values (parse and evaluate)
        String mutationRate = genes.get("MutationRate");
        if (mutationRate != null && mutationRate.matches("\\d+(\\.\\d+)?")) {
            double rate = Double.parseDouble(mutationRate);
            // Prefer moderate mutation rates
            fitness += Math.max(0, 10 - Math.abs(rate - 0.05) * 100);
        }
        
        // 4. Penalize extreme values
        if ("Conservative".equals(genes.get("MemoryLimit"))) {
            fitness -= 2.0; // Prefer moderate or efficient
        }
        
        // 5. Bonus for recent generation (encourages evolution)
        fitness += genome.getGeneration() * 0.1;
        
        return fitness;
    }
    
    /**
     * Select top genomes based on fitness
     */
    private List<FraynixDNA> select(Map<FraynixDNA, Double> scores, double fraction) {
        // Sort by fitness (descending)
        List<Map.Entry<FraynixDNA, Double>> sorted = scores.entrySet().stream()
            .sorted(Map.Entry.<FraynixDNA, Double>comparingByValue().reversed())
            .collect(Collectors.toList());
        
        // Select top fraction
        int count = (int) Math.max(2, sorted.size() * fraction);
        
        return sorted.subList(0, Math.min(count, sorted.size())).stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    /**
     * Reproduce selected genomes with mutations
     */
    private List<FraynixDNA> reproduce(List<FraynixDNA> parents) {
        List<FraynixDNA> offspring = new ArrayList<>();
        
        // Keep best parent
        offspring.add(parents.get(0));
        
        // Create offspring through mutation
        Random rand = new Random();
        while (offspring.size() < populationSize) {
            // Select random parent
            FraynixDNA parent = parents.get(rand.nextInt(parents.size()));
            
            // Create mutated child
            FraynixDNA child = parent.clone();
            child = child.mutate(mutationRate);
            child.incrementGeneration();
            
            offspring.add(child);
        }
        
        return offspring;
    }
    
    /**
     * Update best genome tracker
     */
    private void updateBest(Map<FraynixDNA, Double> scores) {
        for (Map.Entry<FraynixDNA, Double> entry : scores.entrySet()) {
            if (entry.getValue() > bestFitness) {
                bestFitness = entry.getValue();
                bestGenome = entry.getKey();
            }
        }
    }
    
    /**
     * Report evolution progress
     */
    private void reportProgress(int gen, int total) {
        double progress = (gen + 1.0) / total * 100.0;
        
        System.out.println(String.format(
            "   Gen %d/%d (%.0f%%) | Best fitness: %.2f | Avg generation: %.1f",
            gen + 1, total, progress, bestFitness, getAverageGeneration()
        ));
    }
    
    /**
     * Get average generation in population
     */
    private double getAverageGeneration() {
        return genePool.stream()
            .mapToInt(FraynixDNA::getGeneration)
            .average()
            .orElse(0.0);
    }
    
    /**
     * Get best genome
     */
    public FraynixDNA getBest() {
        return bestGenome;
    }
    
    /**
     * Get best fitness
     */
    public double getBestFitness() {
        return bestFitness;
    }
    
    /**
     * Get current gene pool
     */
    public List<FraynixDNA> getGenePool() {
        return new ArrayList<>(genePool);
    }
    
    /**
     * Get population statistics
     */
    public String getStats() {
        if (genePool.isEmpty()) {
            return "Gene pool empty";
        }
        
        double avgFitness = genePool.stream()
            .mapToDouble(this::calculateFitness)
            .average()
            .orElse(0.0);
        
        double avgGen = getAverageGeneration();
        
        return String.format(
            "Population: %d | Best fitness: %.2f | Avg fitness: %.2f | Avg generation: %.1f",
            genePool.size(), bestFitness, avgFitness, avgGen
        );
    }
    
    @Override
    public String toString() {
        return "GeneticEvolver[pop=" + populationSize + 
               ", mutation=" + String.format("%.2f%%", mutationRate * 100) +
               ", best=" + String.format("%.2f", bestFitness) + "]";
    }
}
