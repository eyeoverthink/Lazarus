package fraymus.evolution;

import fraymus.limbs.ClawIO;
import fraymus.limbs.ClawConnector;
import fraymus.physics.*;

/**
 * 🧬 FRAYNIX EVOLVER: The Ouroboros Loop
 * 
 * This is the self-improvement engine. It follows this cycle:
 * 1. READ own source code (Introspection)
 * 2. ANALYZE for inefficiencies (Entropy calculation)
 * 3. FUSE with optimization strategies (Physics simulation)
 * 4. GENERATE improved version (via OpenClaw AI)
 * 5. REWRITE self (Mutation)
 * 6. RECOMPILE (Metamorphosis)
 * 
 * The system literally improves itself through physics-based evolution.
 */
public class FraynixEvolver {

    private final ClawIO surgeon;
    private final ClawConnector nerve;
    private final GravityEngine brain;
    
    // Evolution statistics
    private int evolutionCount = 0;
    private int successfulMutations = 0;

    /**
     * Initialize the evolution engine
     */
    public FraynixEvolver(GravityEngine brain) {
        this.brain = brain;
        this.surgeon = new ClawIO();
        this.nerve = new ClawConnector();
        
        System.out.println("🧬 FRAYNIX EVOLVER initialized");
        System.out.println("   Self-improvement capability: ACTIVE");
    }

    /**
     * ANALYZE: Examine a specific class for optimization opportunities
     * 
     * @param className Class to analyze
     * @return Analysis report
     */
    public String analyzeSelf(String className) {
        System.out.println("\n🔍 INTROSPECTION: Analyzing " + className + "...");
        evolutionCount++;

        // 1. READ (Ingest self)
        String sourceCode = surgeon.readSource(className);
        if (sourceCode.startsWith("ERROR")) {
            System.err.println(sourceCode);
            return sourceCode;
        }

        // 2. PHYSICS SIMULATION (Calculate Entropy)
        double entropy = calculateEntropy(sourceCode);
        System.out.println("   📊 Entropy Level: " + String.format("%.2f", entropy) + " / 100.0");
        
        // 3. DECISION: Should we evolve?
        if (entropy > 50.0) {
            System.out.println("   ⚡ HIGH ENTROPY DETECTED");
            System.out.println("   🧬 Initiating evolution sequence...");
            return triggerEvolution(className, sourceCode, entropy);
        } else {
            System.out.println("   ✅ Code quality acceptable (entropy < 50)");
            return "ANALYSIS COMPLETE: " + className + " is optimized (entropy " + String.format("%.2f", entropy) + ")";
        }
    }
    
    /**
     * EVOLVE: Generate and apply an improved version
     * 
     * @param className Class to evolve
     * @param sourceCode Current source
     * @param entropy Current entropy level
     * @return Evolution result
     */
    private String triggerEvolution(String className, String sourceCode, double entropy) {
        
        // Create particles for physics-based optimization
        // The SOURCE particle contains current code
        PhiSuit<String> sourceParticle = new PhiSuit<>(sourceCode, 0, 0, 0);
        sourceParticle.label = "SOURCE_" + className;
        sourceParticle.amplitude = entropy; // Entropy becomes mass
        sourceParticle.heat = 80.0; // High energy
        
        // The OPTIMIZATION particle contains improvement strategy
        String strategy = "Optimize for: performance, readability, φ-harmonic structure";
        PhiSuit<String> optimizationParticle = new PhiSuit<>(strategy, 10, 10, 10);
        optimizationParticle.label = "OPTIMIZATION_STRATEGY";
        optimizationParticle.amplitude = 90.0; // High importance
        optimizationParticle.heat = 90.0;
        
        // Register in physics engine
        brain.register(sourceParticle);
        brain.register(optimizationParticle);
        
        // Simulate fusion (run physics for a few ticks)
        System.out.println("   ⚛️  Simulating particle fusion...");
        for (int i = 0; i < 50; i++) {
            brain.tick();
            
            // Check if fusion occurred
            double distance = sourceParticle.distanceTo(optimizationParticle);
            if (distance < 2.0) {
                System.out.println("   💥 FUSION ACHIEVED at tick " + i);
                break;
            }
        }
        
        // Generate improved code via OpenClaw (if connected)
        System.out.println("   🤖 Requesting evolution from OpenClaw...");
        String evolutionPrompt = buildEvolutionPrompt(className, sourceCode, entropy);
        String result = nerve.dispatch(evolutionPrompt, "EVOLUTION_REQUEST");
        
        // Clean up particles
        brain.unregister(sourceParticle);
        brain.unregister(optimizationParticle);
        
        if (result.contains("SEVERED LINK")) {
            System.out.println("   ⚠️  OpenClaw not available - evolution simulated only");
            return "EVOLUTION SIMULATED: OpenClaw needed for actual code generation";
        }
        
        System.out.println("   ✨ Evolution proposal received");
        return result;
    }
    
    /**
     * Calculate code entropy (complexity/inefficiency metric)
     * 
     * Higher entropy = more need for refactoring
     * Range: 0-100
     */
    private double calculateEntropy(String sourceCode) {
        double entropy = 0.0;
        
        // Factor 1: Length (longer code = higher entropy)
        double lengthFactor = Math.min(sourceCode.length() / 100.0, 30.0);
        entropy += lengthFactor;
        
        // Factor 2: Nesting depth (count braces)
        long braceCount = sourceCode.chars().filter(ch -> ch == '{').count();
        double nestingFactor = Math.min(braceCount / 5.0, 20.0);
        entropy += nestingFactor;
        
        // Factor 3: Cyclomatic complexity (count if/for/while)
        long ifCount = countOccurrences(sourceCode, "if ");
        long forCount = countOccurrences(sourceCode, "for ");
        long whileCount = countOccurrences(sourceCode, "while ");
        double complexityFactor = Math.min((ifCount + forCount + whileCount) * 2, 25.0);
        entropy += complexityFactor;
        
        // Factor 4: Lack of comments
        long commentCount = countOccurrences(sourceCode, "//");
        double commentFactor = Math.max(0, 15.0 - commentCount);
        entropy += commentFactor;
        
        // Factor 5: φ-deviation (how far from golden ratio proportions)
        double phiFactor = calculatePhiDeviation(sourceCode);
        entropy += phiFactor;
        
        return Math.min(entropy, 100.0);
    }
    
    /**
     * Calculate how far code structure deviates from φ-harmonic proportions
     */
    private double calculatePhiDeviation(String sourceCode) {
        String[] lines = sourceCode.split("\n");
        int totalLines = lines.length;
        
        // Count logic lines vs comment lines
        long logicLines = 0;
        long commentLines = 0;
        
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("//") || trimmed.startsWith("/*") || trimmed.startsWith("*")) {
                commentLines++;
            } else if (!trimmed.isEmpty()) {
                logicLines++;
            }
        }
        
        if (commentLines == 0) return 10.0; // No comments = high deviation
        
        // Ideal ratio: logic / comments ≈ φ (1.618)
        double actualRatio = (double) logicLines / commentLines;
        double idealRatio = 1.618033988749895;
        double deviation = Math.abs(actualRatio - idealRatio);
        
        return Math.min(deviation * 5.0, 10.0);
    }
    
    /**
     * Count occurrences of substring
     */
    private long countOccurrences(String text, String substring) {
        return (text.length() - text.replace(substring, "").length()) / substring.length();
    }
    
    /**
     * Build evolution prompt for OpenClaw
     */
    private String buildEvolutionPrompt(String className, String sourceCode, double entropy) {
        return String.format(
            "EVOLUTION REQUEST:\n" +
            "Class: %s\n" +
            "Current Entropy: %.2f\n" +
            "Task: Refactor and optimize this code. " +
            "Reduce complexity, improve readability, add φ-harmonic structure.\n\n" +
            "Current Code:\n%s\n\n" +
            "Generate: Improved version with same functionality but lower entropy.",
            className, entropy, sourceCode
        );
    }
    
    /**
     * DANGER: Apply evolution (actually rewrite source code)
     * 
     * This requires safety to be disabled on ClawIO
     */
    public String applyEvolution(String className, String newCode) {
        System.out.println("\n💉 APPLYING EVOLUTION to " + className);
        
        if (surgeon.isSafetyEnabled()) {
            surgeon.disableSafety();
        }
        
        String result = surgeon.writeSource(className, newCode);
        successfulMutations++;
        
        surgeon.enableSafety(); // Re-enable immediately
        
        return result;
    }
    
    /**
     * Get evolution statistics
     */
    public String getStatistics() {
        return String.format(
            "Evolution Statistics:\n" +
            "  Analyses performed: %d\n" +
            "  Successful mutations: %d\n" +
            "  Success rate: %.1f%%",
            evolutionCount, successfulMutations,
            evolutionCount > 0 ? (100.0 * successfulMutations / evolutionCount) : 0.0
        );
    }
}
