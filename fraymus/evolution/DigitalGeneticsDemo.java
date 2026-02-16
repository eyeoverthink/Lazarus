package fraymus.evolution;

import java.io.IOException;

/**
 * 🧬 DIGITAL GENETICS DEMO
 * 
 * Demonstrates the complete antifragile system:
 * - Genome creation
 * - Spore packaging
 * - Phoenix resurrection
 * - Genetic evolution
 * 
 * Run this to see Digital Genetics in action!
 */
public class DigitalGeneticsDemo {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         DIGITAL GENETICS DEMONSTRATION                    ║");
        System.out.println("║         Making Fraynix Antifragile                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");
        
        // Demo 1: Basic Genome
        demoGenome();
        
        // Demo 2: Spore Formation
        demoSpore();
        
        // Demo 3: Phoenix Protocol
        demoPhoenix();
        
        // Demo 4: Genetic Evolution
        demoEvolution();
        
        System.out.println("\n✓ All demonstrations complete!");
        System.out.println("  Fraynix is now ANTIFRAGILE!");
    }
    
    /**
     * Demo 1: Create and manipulate genome
     */
    private static void demoGenome() {
        System.out.println("═══ DEMO 1: THE GENOME ═══\n");
        
        // Create genome
        FraynixDNA genome = new FraynixDNA();
        
        // Show initial state
        System.out.println(genome.express());
        
        // Mutate a gene
        System.out.println("\nMutating LearningRate gene...");
        genome.mutateGene("LearningRate");
        
        // Clone and mutate
        FraynixDNA offspring = genome.clone();
        offspring = offspring.mutate(0.05);
        offspring.incrementGeneration();
        
        System.out.println("\nOffspring created:");
        System.out.println("  Parent generation: " + genome.getGeneration());
        System.out.println("  Child generation: " + offspring.getGeneration());
        
        // Save to disk
        try {
            genome.save("demo_genome.dna");
            FraynixDNA loaded = FraynixDNA.load("demo_genome.dna");
            System.out.println("\n✓ Genome saved and loaded successfully");
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Demo 2: Create and restore spore
     */
    private static void demoSpore() {
        System.out.println("═══ DEMO 2: THE SPORE ═══\n");
        
        // Create genome
        FraynixDNA genome = new FraynixDNA();
        genome.setGene("TestGene", "TestValue");
        
        // Sporulate
        FraynixSpore spore = FraynixSpore.sporulate(genome);
        
        System.out.println("Spore info:");
        System.out.println("  Size: " + spore.getSize() + " bytes");
        System.out.println("  Version: " + spore.getVersion());
        System.out.println("  Hash: " + spore.getHash().substring(0, 16) + "...");
        
        // Save to file
        try {
            spore.saveToFile("demo_spore.spore");
            
            // Load and germinate
            FraynixSpore loaded = FraynixSpore.loadFromFile("demo_spore.spore");
            FraynixDNA restored = loaded.germinate();
            
            System.out.println("\n✓ Spore saved and germinated successfully");
            System.out.println("  Restored genome generation: " + restored.getGeneration());
            System.out.println("  Test gene value: " + restored.getGene("TestGene"));
            
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Demo 3: Phoenix resurrection from crash
     */
    private static void demoPhoenix() {
        System.out.println("═══ DEMO 3: THE PHOENIX PROTOCOL ═══\n");
        
        // Initialize system
        PhoenixProtocol.initializeSystem();
        
        // Create phoenix
        PhoenixProtocol phoenix = new PhoenixProtocol();
        
        // Simulate crash
        Exception simulatedCrash = new NullPointerException("Simulated crash for demo");
        
        System.out.println("Simulating crash...");
        phoenix.handleCrash(simulatedCrash);
        
        System.out.println("\nPhoenix stats:");
        System.out.println("  Resurrections: " + phoenix.getResurrectionCount());
        System.out.println("  Crash history: " + phoenix.getCrashHistory().size());
        
        System.out.println();
    }
    
    /**
     * Demo 4: Genetic evolution
     */
    private static void demoEvolution() {
        System.out.println("═══ DEMO 4: GENETIC EVOLUTION ═══\n");
        
        // Create base genome
        FraynixDNA baseGenome = new FraynixDNA();
        
        // Create evolver
        GeneticEvolver evolver = new GeneticEvolver(
            20,    // population size
            0.05,  // mutation rate (5%)
            0.2    // selection pressure (top 20%)
        );
        
        // Initialize gene pool
        evolver.initializeGenePool(baseGenome);
        
        // Evolve for 30 generations
        FraynixDNA best = evolver.evolve(30);
        
        System.out.println("\nBest genome found:");
        System.out.println("  Generation: " + best.getGeneration());
        System.out.println("  Fitness: " + String.format("%.2f", evolver.getBestFitness()));
        System.out.println("  Genes: " + best.getGenes().size());
        
        System.out.println("\nEvolution stats:");
        System.out.println("  " + evolver.getStats());
        
        System.out.println();
    }
}
