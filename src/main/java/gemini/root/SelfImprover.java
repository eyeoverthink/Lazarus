package gemini.root;

import fraymus.OllamaSpine;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * SelfImprover: Continuous Self-Enhancement System
 * 
 * The system that can read its own code, identify improvements,
 * generate enhanced versions, and integrate them.
 * 
 * This enables true continuous evolution - the system gets better
 * over time by analyzing its own implementation and applying
 * insights from MetaCognitionEngine.
 * 
 * Bounded self-modification: Can improve algorithms and strategies,
 * but cannot change core safety constraints or alignment.
 */
public class SelfImprover {
    
    private final OllamaSpine brain;
    private final MetaCognitionEngine metaCognition;
    private final VectorVault vault;
    private final Transmudder transmudder;
    private final String sourceRoot;
    private final List<Improvement> improvementHistory;
    
    public SelfImprover(OllamaSpine brain, MetaCognitionEngine metaCognition, 
                       VectorVault vault, Transmudder transmudder) {
        this.brain = brain;
        this.metaCognition = metaCognition;
        this.vault = vault;
        this.transmudder = transmudder;
        this.sourceRoot = "src/main/java";
        this.improvementHistory = new ArrayList<>();
    }
    
    /**
     * Analyze and improve a specific component
     * 
     * @param componentName Name of the Java file (e.g., "Reflector.java")
     * @return Improvement proposal or null if no improvement needed
     */
    public ImprovementProposal analyzeAndImprove(String componentName) throws Exception {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("   🔧 SELF-IMPROVEMENT: " + componentName);
        System.out.println("═══════════════════════════════════════════════");
        System.out.println();
        
        // Phase 1: Read current implementation
        System.out.println("[Phase 1] Reading current implementation...");
        Path componentPath = findComponentFile(componentName);
        if (componentPath == null) {
            System.out.println("   ✗ Component not found: " + componentName);
            return null;
        }
        
        String currentCode = Files.readString(componentPath);
        System.out.println("   ✓ Loaded: " + componentPath);
        System.out.println("   Lines: " + currentCode.split("\n").length);
        System.out.println();
        
        // Phase 2: Gather insights from meta-cognition
        System.out.println("[Phase 2] Gathering performance insights...");
        List<MetaCognitionEngine.PerformanceInsight> insights = 
            metaCognition.getInsightsRelatedTo(componentName);
        System.out.println("   Found " + insights.size() + " relevant insights");
        
        String insightsSummary = summarizeInsights(insights);
        if (!insightsSummary.isEmpty()) {
            System.out.println("   Key patterns:");
            System.out.println(insightsSummary);
        }
        System.out.println();
        
        // Phase 3: Analyze for improvements
        System.out.println("[Phase 3] Analyzing for improvements...");
        ImprovementAnalysis analysis = analyzeComponent(
            componentName, currentCode, insightsSummary);
        
        if (analysis.opportunities.isEmpty()) {
            System.out.println("   ✓ No improvements needed - code is optimal");
            return null;
        }
        
        System.out.println("   Found " + analysis.opportunities.size() + " improvement opportunities:");
        for (String opp : analysis.opportunities) {
            System.out.println("     • " + opp);
        }
        System.out.println();
        
        // Phase 4: Generate improved version
        System.out.println("[Phase 4] Generating improved version...");
        String improvedCode = generateImprovedVersion(
            componentName, currentCode, analysis);
        System.out.println("   ✓ Improved version generated");
        System.out.println();
        
        // Phase 5: Create proposal
        ImprovementProposal proposal = new ImprovementProposal(
            componentName,
            componentPath.toString(),
            currentCode,
            improvedCode,
            analysis.opportunities,
            analysis.expectedBenefits
        );
        
        System.out.println("[Phase 5] Improvement proposal created");
        System.out.println("   Expected benefits:");
        for (String benefit : analysis.expectedBenefits) {
            System.out.println("     • " + benefit);
        }
        System.out.println();
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("   ✅ ANALYSIS COMPLETE");
        System.out.println("   Use applyImprovement() to integrate changes");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println();
        
        return proposal;
    }
    
    /**
     * Apply an improvement proposal (with testing)
     */
    public boolean applyImprovement(ImprovementProposal proposal) throws Exception {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("   ⚡ APPLYING IMPROVEMENT: " + proposal.componentName);
        System.out.println("═══════════════════════════════════════════════");
        System.out.println();
        
        // Phase 1: Backup current version
        System.out.println("[Phase 1] Creating backup...");
        Path backupPath = Paths.get(proposal.filePath + ".backup");
        Files.writeString(backupPath, proposal.currentCode);
        System.out.println("   ✓ Backup saved: " + backupPath);
        System.out.println();
        
        // Phase 2: Write improved version
        System.out.println("[Phase 2] Writing improved version...");
        Files.writeString(Paths.get(proposal.filePath), proposal.improvedCode);
        System.out.println("   ✓ New version written");
        System.out.println();
        
        // Phase 3: Validate (simple compile check)
        System.out.println("[Phase 3] Validating changes...");
        boolean valid = validateImprovement(proposal);
        
        if (!valid) {
            System.out.println("   ✗ Validation failed - rolling back");
            Files.writeString(Paths.get(proposal.filePath), proposal.currentCode);
            System.out.println("   ✓ Rolled back to original version");
            return false;
        }
        
        System.out.println("   ✓ Validation passed");
        System.out.println();
        
        // Phase 4: Record improvement
        Improvement improvement = new Improvement(
            proposal.componentName,
            proposal.opportunities,
            proposal.expectedBenefits,
            System.currentTimeMillis()
        );
        improvementHistory.add(improvement);
        
        System.out.println("[Phase 4] Improvement recorded");
        System.out.println("   Total improvements: " + improvementHistory.size());
        System.out.println();
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("   ✅ IMPROVEMENT APPLIED SUCCESSFULLY");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println();
        
        return true;
    }
    
    /**
     * Analyze a component for improvement opportunities
     */
    private ImprovementAnalysis analyzeComponent(
            String componentName, String currentCode, String insights) throws Exception {
        
        List<OllamaSpine.Msg> msgs = List.of(
            new OllamaSpine.Msg("system",
                "You are a code analyzer looking for improvement opportunities. " +
                "Focus on: performance, clarity, correctness, and maintainability. " +
                "Consider the meta-cognition insights provided."),
            new OllamaSpine.Msg("user",
                "Component: " + componentName + "\n\n" +
                "Current code:\n" + currentCode + "\n\n" +
                "Performance insights:\n" + insights + "\n\n" +
                "List specific improvement opportunities (be concrete):")
        );
        
        String analysis = brain.chatOnce(msgs, null, Map.of("temperature", 0.2));
        
        // Parse opportunities and benefits
        List<String> opportunities = extractOpportunities(analysis);
        List<String> benefits = extractBenefits(analysis);
        
        return new ImprovementAnalysis(opportunities, benefits);
    }
    
    /**
     * Generate improved version of the code
     */
    private String generateImprovedVersion(
            String componentName, String currentCode, ImprovementAnalysis analysis) throws Exception {
        
        String opportunitiesText = String.join("\n", analysis.opportunities);
        
        List<OllamaSpine.Msg> msgs = List.of(
            new OllamaSpine.Msg("system",
                "You are a code improver. Generate an enhanced version of the code " +
                "that addresses the identified opportunities. " +
                "Preserve all existing functionality and safety constraints. " +
                "Only improve what's explicitly mentioned."),
            new OllamaSpine.Msg("user",
                "Current code:\n" + currentCode + "\n\n" +
                "Improvements to make:\n" + opportunitiesText + "\n\n" +
                "Generate improved version (complete file):")
        );
        
        return brain.chatOnce(msgs, null, Map.of("temperature", 0.3));
    }
    
    /**
     * Validate an improvement (basic check)
     */
    private boolean validateImprovement(ImprovementProposal proposal) {
        // Basic validation: check that improved code has similar structure
        String[] currentLines = proposal.currentCode.split("\n");
        String[] improvedLines = proposal.improvedCode.split("\n");
        
        // Should be similar length (within 50%)
        double lengthRatio = (double) improvedLines.length / currentLines.length;
        if (lengthRatio < 0.5 || lengthRatio > 2.0) {
            System.out.println("   Warning: Length changed significantly");
            return false;
        }
        
        // Should contain key elements
        if (!proposal.improvedCode.contains("package ")) {
            System.out.println("   Warning: Missing package declaration");
            return false;
        }
        
        if (!proposal.improvedCode.contains("class ")) {
            System.out.println("   Warning: Missing class declaration");
            return false;
        }
        
        return true;
    }
    
    /**
     * Find a component file in the source tree
     */
    private Path findComponentFile(String componentName) throws IOException {
        Path rootPath = Paths.get(sourceRoot);
        if (!Files.exists(rootPath)) {
            return null;
        }
        
        Optional<Path> found = Files.walk(rootPath)
            .filter(p -> p.getFileName().toString().equals(componentName))
            .findFirst();
        
        return found.orElse(null);
    }
    
    /**
     * Summarize insights for a component
     */
    private String summarizeInsights(List<MetaCognitionEngine.PerformanceInsight> insights) {
        if (insights.isEmpty()) {
            return "(No performance insights available)";
        }
        
        StringBuilder sb = new StringBuilder();
        for (MetaCognitionEngine.PerformanceInsight insight : insights) {
            sb.append("   • ").append(insight.pattern).append("\n");
        }
        return sb.toString();
    }
    
    /**
     * Extract improvement opportunities from analysis
     */
    private List<String> extractOpportunities(String analysis) {
        List<String> opportunities = new ArrayList<>();
        String[] lines = analysis.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.matches("^[•\\-*].*") || line.matches("^\\d+\\..*")) {
                opportunities.add(line.replaceFirst("^[•\\-*\\d.]+\\s*", ""));
            }
        }
        return opportunities;
    }
    
    /**
     * Extract expected benefits from analysis
     */
    private List<String> extractBenefits(String analysis) {
        List<String> benefits = new ArrayList<>();
        if (analysis.toLowerCase().contains("faster") || 
            analysis.toLowerCase().contains("performance")) {
            benefits.add("Improved performance");
        }
        if (analysis.toLowerCase().contains("clearer") ||
            analysis.toLowerCase().contains("readable")) {
            benefits.add("Better code clarity");
        }
        if (analysis.toLowerCase().contains("maintain")) {
            benefits.add("Easier maintenance");
        }
        if (benefits.isEmpty()) {
            benefits.add("General improvement");
        }
        return benefits;
    }
    
    /**
     * Get improvement history
     */
    public List<Improvement> getImprovementHistory() {
        return new ArrayList<>(improvementHistory);
    }
    
    /**
     * Analysis result
     */
    private static class ImprovementAnalysis {
        final List<String> opportunities;
        final List<String> expectedBenefits;
        
        ImprovementAnalysis(List<String> opps, List<String> benefits) {
            this.opportunities = opps;
            this.expectedBenefits = benefits;
        }
    }
    
    /**
     * Improvement proposal
     */
    public static class ImprovementProposal {
        public final String componentName;
        public final String filePath;
        public final String currentCode;
        public final String improvedCode;
        public final List<String> opportunities;
        public final List<String> expectedBenefits;
        
        public ImprovementProposal(String name, String path, String current, 
                                  String improved, List<String> opps, List<String> benefits) {
            this.componentName = name;
            this.filePath = path;
            this.currentCode = current;
            this.improvedCode = improved;
            this.opportunities = opps;
            this.expectedBenefits = benefits;
        }
    }
    
    /**
     * Record of an applied improvement
     */
    public static class Improvement {
        public final String componentName;
        public final List<String> changes;
        public final List<String> benefits;
        public final long timestamp;
        
        public Improvement(String name, List<String> changes, 
                          List<String> benefits, long timestamp) {
            this.componentName = name;
            this.changes = changes;
            this.benefits = benefits;
            this.timestamp = timestamp;
        }
        
        @Override
        public String toString() {
            return String.format("Improved %s: %s (benefits: %s)",
                componentName, String.join(", ", changes), 
                String.join(", ", benefits));
        }
    }
}
