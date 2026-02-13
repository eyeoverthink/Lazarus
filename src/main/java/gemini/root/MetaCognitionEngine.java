package gemini.root;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import com.google.gson.*;

/**
 * META-COGNITION ENGINE
 * 
 * System-3: Beyond reflection - learns from experience.
 * 
 * Capabilities:
 * 1. Pattern recognition across conversations
 * 2. Strategy adaptation based on success/failure
 * 3. Insight extraction and generalization
 * 4. Self-assessment of reasoning quality
 * 5. Continuous improvement through feedback
 * 
 * Lifecycle:
 *   Query → Answer → Self-Assess → Learn → Update Strategies
 */
public class MetaCognitionEngine {
    
    private final OllamaSpine brain;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    // Learned insights storage
    private final Path insightsPath = Paths.get("memory/insights.jsonl");
    private final List<Insight> insights = new ArrayList<>();
    
    // Performance tracking
    private final Map<String, StrategyPerformance> strategyMetrics = new HashMap<>();
    
    public MetaCognitionEngine(OllamaSpine brain) {
        this.brain = brain;
        loadInsights();
    }
    
    /**
     * Learned insight
     */
    public static class Insight {
        String pattern;          // What situation/pattern
        String strategy;         // What worked
        double successRate;      // How well it worked
        int occurrences;         // How many times seen
        String example;          // Example case
        long timestamp;
        
        public Insight(String pattern, String strategy, String example) {
            this.pattern = pattern;
            this.strategy = strategy;
            this.successRate = 0.5;
            this.occurrences = 1;
            this.example = example;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    /**
     * Strategy performance metrics
     */
    public static class StrategyPerformance {
        String strategy;
        int successes = 0;
        int failures = 0;
        double avgLatency = 0;
        
        public double getSuccessRate() {
            int total = successes + failures;
            return total > 0 ? (double) successes / total : 0.5;
        }
    }
    
    /**
     * Self-assess the quality of reasoning
     * 
     * @param query Original query
     * @param context Context used
     * @param answer Generated answer
     * @param strategy Strategy used (e.g., "multi-agent", "reflection", "fast")
     * @return Quality score 0-1
     */
    public double assessQuality(String query, String context, String answer, String strategy) {
        
        String assessPrompt = String.format("""
            Assess the quality of this reasoning:
            
            QUERY: %s
            
            CONTEXT AVAILABLE:
            %s
            
            ANSWER PRODUCED:
            %s
            
            Rate the answer on these dimensions (0-1 scale):
            1. Accuracy: Are facts correct and properly cited?
            2. Completeness: Does it fully address the query?
            3. Clarity: Is it well-structured and understandable?
            4. Appropriate uncertainty: Does it acknowledge what it doesn't know?
            
            Return ONLY a JSON object:
            {
              "accuracy": 0.0-1.0,
              "completeness": 0.0-1.0,
              "clarity": 0.0-1.0,
              "uncertainty_handling": 0.0-1.0,
              "overall": 0.0-1.0,
              "reasoning": "brief explanation"
            }
            """, query, context.substring(0, Math.min(500, context.length())), answer);
        
        List<OllamaSpine.Msg> msgs = List.of(
            new OllamaSpine.Msg("system", "You are a rigorous quality assessor."),
            new OllamaSpine.Msg("user", assessPrompt)
        );
        
        try {
            String result = brain.chatOnce(msgs, null, Map.of("temperature", 0.1));
            JsonObject assessment = JsonParser.parseString(result).getAsJsonObject();
            
            double quality = assessment.get("overall").getAsDouble();
            
            // Record performance
            recordPerformance(strategy, quality > 0.7, 0);
            
            return quality;
            
        } catch (Exception e) {
            // Fallback: simple heuristic
            return simpleQualityHeuristic(answer);
        }
    }
    
    /**
     * Simple quality heuristic (fallback)
     */
    private double simpleQualityHeuristic(String answer) {
        if (answer == null || answer.trim().isEmpty()) return 0.0;
        
        double score = 0.5;
        
        // Has citations
        if (answer.contains("[S") && answer.contains("]")) score += 0.15;
        
        // Reasonable length
        int len = answer.length();
        if (len > 100 && len < 3000) score += 0.15;
        
        // Contains reasoning
        if (answer.toLowerCase().contains("because") || 
            answer.toLowerCase().contains("therefore")) score += 0.1;
        
        // Acknowledges limits
        if (answer.toLowerCase().contains("not found") || 
            answer.toLowerCase().contains("unclear")) score += 0.1;
        
        return Math.min(1.0, score);
    }
    
    /**
     * Extract insights from successful interactions
     */
    public void extractInsights(String query, String answer, double quality) {
        
        if (quality < 0.7) return; // Only learn from good examples
        
        String extractPrompt = String.format("""
            Analyze this successful interaction and extract a generalizable insight:
            
            QUERY: %s
            ANSWER (quality: %.2f): %s
            
            What pattern or strategy made this successful?
            What can we learn and apply to future queries?
            
            Return JSON:
            {
              "pattern": "description of query type or situation",
              "strategy": "what approach worked well",
              "generalization": "how to apply this broadly"
            }
            """, query, quality, answer.substring(0, Math.min(500, answer.length())));
        
        List<OllamaSpine.Msg> msgs = List.of(
            new OllamaSpine.Msg("system", "You are a meta-learning system extracting insights."),
            new OllamaSpine.Msg("user", extractPrompt)
        );
        
        try {
            String result = brain.chatOnce(msgs, null, Map.of("temperature", 0.2));
            JsonObject insight = JsonParser.parseString(result).getAsJsonObject();
            
            String pattern = insight.get("pattern").getAsString();
            String strategy = insight.get("strategy").getAsString();
            
            // Store insight
            Insight newInsight = new Insight(pattern, strategy, query);
            insights.add(newInsight);
            persistInsight(newInsight);
            
        } catch (Exception e) {
            // Silent fail - insights are enhancement, not critical
        }
    }
    
    /**
     * Get relevant insights for a query
     */
    public List<Insight> getRelevantInsights(String query) {
        // Simple relevance: keyword matching
        // In production, would use embedding similarity
        
        List<Insight> relevant = new ArrayList<>();
        String queryLower = query.toLowerCase();
        
        for (Insight insight : insights) {
            String patternLower = insight.pattern.toLowerCase();
            
            // Check for keyword overlap
            String[] queryWords = queryLower.split("\\s+");
            String[] patternWords = patternLower.split("\\s+");
            
            int overlap = 0;
            for (String qw : queryWords) {
                for (String pw : patternWords) {
                    if (qw.length() > 3 && pw.contains(qw)) {
                        overlap++;
                    }
                }
            }
            
            if (overlap > 1 || insight.successRate > 0.8) {
                relevant.add(insight);
            }
        }
        
        // Return top insights by success rate
        relevant.sort((a, b) -> Double.compare(b.successRate, a.successRate));
        return relevant.subList(0, Math.min(3, relevant.size()));
    }
    
    /**
     * Recommend best strategy for a query
     */
    public String recommendStrategy(String query, String context) {
        
        // Get relevant past insights
        List<Insight> relevant = getRelevantInsights(query);
        
        if (!relevant.isEmpty()) {
            // Use learned strategy
            Insight best = relevant.get(0);
            if (best.successRate > 0.75) {
                return best.strategy;
            }
        }
        
        // Fallback to heuristics
        if (query.length() < 30 && !context.contains("[S1]")) {
            return "fast"; // Simple query, no RAG
        }
        
        if (context.length() > 5000 || context.contains("[S3]")) {
            return "multi-agent"; // Complex context, use ensemble
        }
        
        return "reflection"; // Default to System-2
    }
    
    /**
     * Record strategy performance
     */
    private void recordPerformance(String strategy, boolean success, long latency) {
        StrategyPerformance perf = strategyMetrics.computeIfAbsent(
            strategy, k -> new StrategyPerformance());
        
        perf.strategy = strategy;
        if (success) perf.successes++;
        else perf.failures++;
        
        // Update average latency
        int total = perf.successes + perf.failures;
        perf.avgLatency = (perf.avgLatency * (total - 1) + latency) / total;
    }
    
    /**
     * Get performance report
     */
    public String getPerformanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== META-COGNITION PERFORMANCE ===\n\n");
        
        report.append("Strategy Performance:\n");
        for (StrategyPerformance perf : strategyMetrics.values()) {
            report.append(String.format("  %s: %.1f%% success (%d/%d), avg %.1fs\n",
                perf.strategy,
                perf.getSuccessRate() * 100,
                perf.successes,
                perf.successes + perf.failures,
                perf.avgLatency / 1000.0));
        }
        
        report.append(String.format("\nInsights Learned: %d\n", insights.size()));
        
        return report.toString();
    }
    
    /**
     * Load insights from disk
     */
    private void loadInsights() {
        try {
            if (!Files.exists(insightsPath)) {
                Files.createDirectories(insightsPath.getParent());
                return;
            }
            
            List<String> lines = Files.readAllLines(insightsPath);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                Insight insight = gson.fromJson(line, Insight.class);
                insights.add(insight);
            }
            
        } catch (Exception e) {
            System.err.println("Failed to load insights: " + e.getMessage());
        }
    }
    
    /**
     * Persist single insight
     */
    private void persistInsight(Insight insight) {
        try {
            Files.createDirectories(insightsPath.getParent());
            String json = gson.toJson(insight);
            Files.write(insightsPath, (json + "\n").getBytes(), 
                       StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.err.println("Failed to persist insight: " + e.getMessage());
        }
    }
}
