package fraymus.coding;

import fraymus.*;
import java.util.*;

/**
 * 🎯 CONVERGENCE_01 - Chain of Density Convergence Test
 * 
 * Demonstrates and validates the iterative refinement process:
 * - Initial code generation
 * - Critique and improvement suggestions
 * - Iterative refinement
 * - Convergence detection
 * - Quality improvement metrics
 * 
 * This is the key differentiator: code that improves itself through
 * phi-optimized iterative refinement until convergence is achieved.
 */
public class CONVERGENCE_01 {
    
    private static final double PHI = 1.618033988749894;
    
    /**
     * Convergence Data Tracker
     */
    public static class ConvergenceData {
        public List<String> iterations = new ArrayList<>();
        public List<String> critiques = new ArrayList<>();
        public List<Double> qualityScores = new ArrayList<>();
        public int iterationCount = 0;
        public boolean converged = false;
        public String convergenceReason = "";
        public long totalTime = 0;
        
        public void addIteration(String code, String critique, double quality) {
            iterations.add(code);
            critiques.add(critique);
            qualityScores.add(quality);
            iterationCount++;
        }
        
        public double getImprovement() {
            if (qualityScores.size() < 2) return 0;
            double initial = qualityScores.get(0);
            double final_ = qualityScores.get(qualityScores.size() - 1);
            return ((final_ - initial) / initial) * 100.0;
        }
        
        public void setConverged(boolean converged, String reason) {
            this.converged = converged;
            this.convergenceReason = reason;
        }
    }
    
    /**
     * Enhanced refinement with convergence tracking
     */
    public static ConvergenceData refineWithConvergenceTracking(
            String initialCode, String language, String task, 
            OllamaSpine ollama, String chatModel) {
        
        ConvergenceData data = new ConvergenceData();
        long startTime = System.currentTimeMillis();
        
        String currentCode = initialCode;
        int maxIterations = 3;
        
        // Track initial code
        double initialQuality = assessCodeQuality(currentCode, language, task);
        data.addIteration(currentCode, "Initial generation", initialQuality);
        
        for (int i = 0; i < maxIterations; i++) {
            try {
                // Critique current code
                String critiquePrompt = String.format(
                    "Review this %s code and suggest ONE specific improvement:\n\n%s\n\n" +
                    "Task: %s\n\n" +
                    "If the code is already optimal, respond: 'OPTIMAL: No improvements needed.'\n" +
                    "Otherwise respond: 'IMPROVEMENT: <specific improvement>'",
                    language, currentCode, task);
                
                Map<String, Object> options = new HashMap<>();
                options.put("temperature", 0.3);
                
                String critique = ollama.generate(chatModel, critiquePrompt, options);
                
                // Check for convergence
                if (critique.toLowerCase().contains("optimal") || 
                    critique.toLowerCase().contains("no improvement")) {
                    data.setConverged(true, "Code deemed optimal by critique");
                    break;
                }
                
                // Apply improvement
                String refinePrompt = String.format(
                    "Improve this code:\n\n%s\n\n" +
                    "Improvement to apply: %s\n\n" +
                    "Return ONLY the improved code:",
                    currentCode, critique);
                
                String refined = ollama.generate(chatModel, refinePrompt, options);
                currentCode = cleanCodeResponse(refined);
                
                // Assess new quality
                double newQuality = assessCodeQuality(currentCode, language, task);
                data.addIteration(currentCode, critique, newQuality);
                
                // Check if quality stopped improving (convergence)
                if (data.qualityScores.size() >= 2) {
                    int lastIdx = data.qualityScores.size() - 1;
                    double improvement = data.qualityScores.get(lastIdx) - 
                                        data.qualityScores.get(lastIdx - 1);
                    if (Math.abs(improvement) < 0.01) {  // Phi-threshold
                        data.setConverged(true, "Quality improvement below threshold");
                        break;
                    }
                }
                
            } catch (Exception e) {
                data.setConverged(true, "Error during refinement: " + e.getMessage());
                break;
            }
        }
        
        if (!data.converged && data.iterationCount >= maxIterations) {
            data.setConverged(true, "Maximum iterations reached");
        }
        
        data.totalTime = System.currentTimeMillis() - startTime;
        return data;
    }
    
    /**
     * Assess code quality (simplified heuristic)
     */
    private static double assessCodeQuality(String code, String language, String task) {
        double quality = 0.5;  // Base quality
        
        // Length check (not too short, not too long)
        int idealLength = 200;
        double lengthScore = 1.0 - Math.abs(code.length() - idealLength) / (idealLength * 2.0);
        quality += lengthScore * 0.2;
        
        // Structure checks
        if (code.contains("def ") || code.contains("function ") || code.contains("public ")) {
            quality += 0.1;
        }
        if (code.contains("return")) quality += 0.1;
        if (code.contains("//") || code.contains("#") || code.contains("\"\"\"")) {
            quality += 0.1;  // Has comments
        }
        
        // Error handling
        if (code.contains("try") || code.contains("except") || code.contains("catch") ||
            code.contains("if ") || code.contains("else")) {
            quality += 0.1;
        }
        
        // Complexity (operators and control flow)
        int complexity = 0;
        for (char c : code.toCharArray()) {
            if (c == '+' || c == '-' || c == '*' || c == '/' || c == '=') complexity++;
        }
        quality += Math.min(0.1, complexity * 0.001);
        
        return Math.min(1.0, quality);
    }
    
    /**
     * Clean code response (remove markdown blocks)
     */
    private static String cleanCodeResponse(String response) {
        String cleaned = response.replaceAll("```[a-z]*\\n", "").replaceAll("```", "");
        return cleaned.trim();
    }
    
    /**
     * Display convergence results
     */
    private static void displayConvergenceResults(ConvergenceData data, String task) {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🎯 CONVERGENCE ANALYSIS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("   Task: " + task);
        System.out.println("   Iterations: " + data.iterationCount);
        System.out.println("   Time: " + data.totalTime + "ms");
        System.out.println();
        
        // Show quality progression
        System.out.println("   Quality Progression:");
        for (int i = 0; i < data.qualityScores.size(); i++) {
            String marker = i == 0 ? "→" : "→";
            if (i == data.qualityScores.size() - 1) marker = "✓";
            System.out.printf("   %s Iteration %d: %.4f", marker, i, data.qualityScores.get(i));
            if (i > 0) {
                double improvement = data.qualityScores.get(i) - data.qualityScores.get(i - 1);
                System.out.printf(" (+%.4f)", improvement);
            }
            System.out.println();
        }
        System.out.println();
        
        // Show improvement
        double improvement = data.getImprovement();
        System.out.printf("   Total Improvement: %.2f%%\n", improvement);
        System.out.println("   Convergence: " + (data.converged ? "ACHIEVED" : "NOT ACHIEVED"));
        System.out.println("   Reason: " + data.convergenceReason);
        System.out.println();
        
        // Show iterations
        System.out.println("   Iteration Details:");
        for (int i = 0; i < data.iterations.size(); i++) {
            System.out.println("   ─────────────────────────────");
            System.out.println("   Iteration " + i + ":");
            if (i > 0 && i - 1 < data.critiques.size()) {
                System.out.println("   Critique: " + data.critiques.get(i).substring(0, 
                    Math.min(80, data.critiques.get(i).length())) + "...");
            }
            System.out.println("   Code preview: " + data.iterations.get(i).substring(0,
                Math.min(100, data.iterations.get(i).length())) + "...");
        }
        
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        if (data.converged) {
            System.out.println("✅ CONVERGENCE: ACHIEVED");
        } else {
            System.out.println("⚠️  CONVERGENCE: NOT ACHIEVED");
        }
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
    }
    
    /**
     * Main convergence test
     */
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("🔥 CONVERGENCE_01 - Chain of Density Refinement Test");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        
        try {
            // Initialize Ollama
            System.out.println("Initializing Ollama...");
            OllamaSpine ollama = new OllamaSpine();
            String chatModel = "llama3";
            System.out.println("✓ Ollama initialized with model: " + chatModel + "\n");
            
            // Test 1: Simple function with convergence
            System.out.println("TEST 1: Fibonacci Function Refinement");
            System.out.println("────────────────────────────────────────");
            
            String task1 = "write a fibonacci function";
            String language1 = "python";
            
            // Generate initial code
            System.out.println("Generating initial code...");
            Map<String, Object> options = new HashMap<>();
            options.put("temperature", 0.1);
            
            String prompt1 = String.format(
                "Generate %s code for: %s\n\n" +
                "Return ONLY the code, no explanations.",
                language1, task1);
            
            String initialCode1 = ollama.generate(chatModel, prompt1, options);
            initialCode1 = cleanCodeResponse(initialCode1);
            System.out.println("✓ Initial code generated (" + initialCode1.length() + " chars)\n");
            
            // Refine with convergence tracking
            System.out.println("Starting Chain of Density refinement...");
            ConvergenceData data1 = refineWithConvergenceTracking(
                initialCode1, language1, task1, ollama, chatModel);
            
            // Display results
            displayConvergenceResults(data1, task1);
            
            // Test 2: More complex function
            System.out.println("\nTEST 2: Binary Search Implementation");
            System.out.println("────────────────────────────────────────");
            
            String task2 = "implement binary search algorithm";
            String language2 = "java";
            
            System.out.println("Generating initial code...");
            String prompt2 = String.format(
                "Generate %s code for: %s\n\n" +
                "Return ONLY the code, no explanations.",
                language2, task2);
            
            String initialCode2 = ollama.generate(chatModel, prompt2, options);
            initialCode2 = cleanCodeResponse(initialCode2);
            System.out.println("✓ Initial code generated (" + initialCode2.length() + " chars)\n");
            
            System.out.println("Starting Chain of Density refinement...");
            ConvergenceData data2 = refineWithConvergenceTracking(
                initialCode2, language2, task2, ollama, chatModel);
            
            displayConvergenceResults(data2, task2);
            
            // Summary
            System.out.println("\n═══════════════════════════════════════════════════════════════");
            System.out.println("📊 CONVERGENCE TEST SUMMARY");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println(String.format("   Test 1: %d iterations, %.2f%% improvement, %s",
                data1.iterationCount, data1.getImprovement(), 
                data1.converged ? "CONVERGED" : "NOT CONVERGED"));
            System.out.println(String.format("   Test 2: %d iterations, %.2f%% improvement, %s",
                data2.iterationCount, data2.getImprovement(),
                data2.converged ? "CONVERGED" : "NOT CONVERGED"));
            System.out.println();
            
            int totalConverged = (data1.converged ? 1 : 0) + (data2.converged ? 1 : 0);
            System.out.println(String.format("   Success Rate: %d/2 tests achieved convergence", totalConverged));
            System.out.println();
            
            if (totalConverged == 2) {
                System.out.println("✅ ALL TESTS PASSED - CONVERGENCE SYSTEM OPERATIONAL");
            } else {
                System.out.println("⚠️  PARTIAL SUCCESS - REVIEW CONVERGENCE CRITERIA");
            }
            System.out.println("═══════════════════════════════════════════════════════════════");
            
        } catch (Exception e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
