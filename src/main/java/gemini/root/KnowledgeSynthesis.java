package gemini.root;

import fraymus.OllamaSpine;
import com.google.gson.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * KNOWLEDGE SYNTHESIS ENGINE
 * 
 * Discovers novel insights by connecting information across documents.
 * Goes beyond retrieval to create NEW knowledge.
 * 
 * Capabilities:
 * 1. Cross-document pattern detection
 * 2. Contradiction identification and resolution
 * 3. Hypothesis generation
 * 4. Causal chain inference
 * 5. Analogical reasoning
 * 6. Emergence detection (whole > parts)
 */
public class KnowledgeSynthesis {
    
    private final OllamaSpine brain;
    private final VectorVault vault;
    
    public KnowledgeSynthesis(OllamaSpine brain, VectorVault vault) {
        this.brain = brain;
        this.vault = vault;
    }
    
    /**
     * Discovered insight
     */
    public static class DiscoveredInsight {
        String type;              // "connection", "contradiction", "pattern", "hypothesis"
        String description;       // What was discovered
        List<String> sources;     // Which documents involved
        double confidence;        // How confident (0-1)
        String reasoning;         // Why this insight emerged
        
        public DiscoveredInsight(String type, String description, List<String> sources, 
                                double confidence, String reasoning) {
            this.type = type;
            this.description = description;
            this.sources = sources;
            this.confidence = confidence;
            this.reasoning = reasoning;
        }
    }
    
    /**
     * Detect contradictions in retrieved context
     * 
     * Contradictions indicate:
     * - Different perspectives (good to note)
     * - Errors in sources (flag for review)
     * - Evolution of knowledge (temporal context)
     */
    public List<DiscoveredInsight> detectContradictions(String context) {
        
        String prompt = String.format("""
            Analyze this context for contradictions or conflicting claims:
            
            %s
            
            Look for:
            1. Direct contradictions (A says X, B says not-X)
            2. Incompatible claims (both can't be true)
            3. Different perspectives on same topic
            
            For each contradiction found, return JSON:
            {
              "contradictions": [
                {
                  "claim1": "first claim",
                  "claim2": "conflicting claim",
                  "source1": "document reference",
                  "source2": "document reference",
                  "severity": "high|medium|low",
                  "resolution": "how to resolve or why both might be valid"
                }
              ]
            }
            
            If no contradictions, return {"contradictions": []}
            """, context);
        
        List<OllamaSpine.Msg> msgs = List.of(
            new OllamaSpine.Msg("system", "You are a logical analyzer detecting contradictions."),
            new OllamaSpine.Msg("user", prompt)
        );
        
        try {
            String result = brain.chatOnce(msgs, null, Map.of("temperature", 0.1));
            JsonObject obj = JsonParser.parseString(result).getAsJsonObject();
            JsonArray arr = obj.getAsJsonArray("contradictions");
            
            List<DiscoveredInsight> insights = new ArrayList<>();
            for (int i = 0; i < arr.size(); i++) {
                JsonObject c = arr.get(i).getAsJsonObject();
                
                String desc = String.format("Contradiction: '%s' vs '%s'", 
                    c.get("claim1").getAsString(),
                    c.get("claim2").getAsString());
                
                List<String> sources = Arrays.asList(
                    c.get("source1").getAsString(),
                    c.get("source2").getAsString()
                );
                
                double conf = c.get("severity").getAsString().equals("high") ? 0.9 : 0.7;
                String reasoning = c.get("resolution").getAsString();
                
                insights.add(new DiscoveredInsight("contradiction", desc, sources, conf, reasoning));
            }
            
            return insights;
            
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Find novel connections across documents
     * 
     * Connections that weren't explicitly stated but emerge from synthesis
     */
    public List<DiscoveredInsight> discoverConnections(String topic, List<String> documents) {
        
        if (documents.size() < 2) return new ArrayList<>();
        
        String prompt = String.format("""
            Topic: %s
            
            Documents:
            %s
            
            Find NOVEL connections that emerge when considering these documents together.
            Look for:
            1. Unstated relationships between concepts
            2. Patterns that appear across documents
            3. Cause-and-effect chains
            4. Shared underlying principles
            
            Return JSON:
            {
              "connections": [
                {
                  "description": "what connects",
                  "documents": ["doc1", "doc2"],
                  "novelty": "why this isn't obvious from single document",
                  "strength": "strong|medium|weak"
                }
              ]
            }
            """, topic, String.join("\n\n---\n\n", documents.subList(0, Math.min(5, documents.size()))));
        
        List<OllamaSpine.Msg> msgs = List.of(
            new OllamaSpine.Msg("system", "You discover hidden connections across information."),
            new OllamaSpine.Msg("user", prompt)
        );
        
        try {
            String result = brain.chatOnce(msgs, null, Map.of("temperature", 0.4));
            JsonObject obj = JsonParser.parseString(result).getAsJsonObject();
            JsonArray arr = obj.getAsJsonArray("connections");
            
            List<DiscoveredInsight> insights = new ArrayList<>();
            for (int i = 0; i < arr.size(); i++) {
                JsonObject c = arr.get(i).getAsJsonObject();
                
                String desc = c.get("description").getAsString();
                String novelty = c.get("novelty").getAsString();
                String strength = c.get("strength").getAsString();
                
                JsonArray docArr = c.getAsJsonArray("documents");
                List<String> sources = new ArrayList<>();
                for (int j = 0; j < docArr.size(); j++) {
                    sources.add(docArr.get(j).getAsString());
                }
                
                double conf = strength.equals("strong") ? 0.8 : (strength.equals("medium") ? 0.6 : 0.4);
                
                insights.add(new DiscoveredInsight("connection", desc, sources, conf, novelty));
            }
            
            return insights;
            
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Generate hypotheses from available knowledge
     * 
     * What might be true based on what we know?
     * What questions should we explore?
     */
    public List<DiscoveredInsight> generateHypotheses(String domain, String context) {
        
        String prompt = String.format("""
            Domain: %s
            
            Current knowledge:
            %s
            
            Based on this knowledge, generate 2-3 testable hypotheses:
            1. What might explain observed patterns?
            2. What predictions follow from this knowledge?
            3. What gaps suggest interesting questions?
            
            Return JSON:
            {
              "hypotheses": [
                {
                  "hypothesis": "clear statement",
                  "reasoning": "why this follows from evidence",
                  "testable": "how to test/verify",
                  "confidence": 0.0-1.0
                }
              ]
            }
            """, domain, context.substring(0, Math.min(2000, context.length())));
        
        List<OllamaSpine.Msg> msgs = List.of(
            new OllamaSpine.Msg("system", "You generate scientific hypotheses from evidence."),
            new OllamaSpine.Msg("user", prompt)
        );
        
        try {
            String result = brain.chatOnce(msgs, null, Map.of("temperature", 0.5));
            JsonObject obj = JsonParser.parseString(result).getAsJsonObject();
            JsonArray arr = obj.getAsJsonArray("hypotheses");
            
            List<DiscoveredInsight> insights = new ArrayList<>();
            for (int i = 0; i < arr.size(); i++) {
                JsonObject h = arr.get(i).getAsJsonObject();
                
                String hyp = h.get("hypothesis").getAsString();
                String reasoning = h.get("reasoning").getAsString();
                String testable = h.get("testable").getAsString();
                double conf = h.get("confidence").getAsDouble();
                
                String fullDesc = String.format("Hypothesis: %s (Test: %s)", hyp, testable);
                
                insights.add(new DiscoveredInsight("hypothesis", fullDesc, 
                    Arrays.asList("synthetic"), conf, reasoning));
            }
            
            return insights;
            
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Detect emergent patterns (whole > parts)
     * 
     * Patterns that only appear when looking at the big picture
     */
    public String detectEmergentPatterns(List<String> retrievedChunks) {
        
        if (retrievedChunks.size() < 3) {
            return "Insufficient data for emergence detection (need 3+ sources)";
        }
        
        String combined = retrievedChunks.stream()
            .limit(10)
            .collect(Collectors.joining("\n\n---\n\n"));
        
        String prompt = String.format("""
            Analyze these information fragments for EMERGENT patterns:
            
            %s
            
            Look for:
            1. Higher-order patterns not visible in individual pieces
            2. System-level behaviors
            3. Recurring themes across different contexts
            4. Meta-patterns (patterns of patterns)
            
            Describe what emerges that isn't explicitly stated.
            """, combined);
        
        List<OllamaSpine.Msg> msgs = List.of(
            new OllamaSpine.Msg("system", "You detect emergent patterns in complex systems."),
            new OllamaSpine.Msg("user", prompt)
        );
        
        return brain.chatOnce(msgs, null, Map.of("temperature", 0.6));
    }
    
    /**
     * Analogical reasoning: find similar patterns in different domains
     */
    public String findAnalogies(String concept, String targetDomain) {
        
        String prompt = String.format("""
            Concept: %s
            Target domain: %s
            
            Find meaningful analogies:
            1. What in the target domain is structurally similar?
            2. What insights transfer?
            3. Where does the analogy break down?
            
            Example:
            - Concept: "Natural selection" in biology
            - Target: Economics
            - Analogy: "Market selection" - companies that fit environment survive
            - Insight: Competition drives adaptation
            - Limitation: Markets have intentional actors, nature doesn't
            
            Provide 2-3 analogies with similar analysis.
            """, concept, targetDomain);
        
        List<OllamaSpine.Msg> msgs = List.of(
            new OllamaSpine.Msg("system", "You make insightful cross-domain analogies."),
            new OllamaSpine.Msg("user", prompt)
        );
        
        return brain.chatOnce(msgs, null, Map.of("temperature", 0.7));
    }
    
    /**
     * Comprehensive synthesis: all discovery methods on a topic
     */
    public String synthesize(String query, String context, List<String> documents) {
        
        List<DiscoveredInsight> allInsights = new ArrayList<>();
        
        // Run all discovery methods
        allInsights.addAll(detectContradictions(context));
        allInsights.addAll(discoverConnections(query, documents));
        allInsights.addAll(generateHypotheses(query, context));
        
        if (allInsights.isEmpty()) {
            return "No novel insights discovered beyond retrieved context.";
        }
        
        // Format insights
        StringBuilder sb = new StringBuilder();
        sb.append("=== KNOWLEDGE SYNTHESIS ===\n\n");
        
        Map<String, List<DiscoveredInsight>> byType = allInsights.stream()
            .collect(Collectors.groupingBy(i -> i.type));
        
        for (Map.Entry<String, List<DiscoveredInsight>> entry : byType.entrySet()) {
            sb.append(entry.getKey().toUpperCase()).append("S:\n");
            for (DiscoveredInsight insight : entry.getValue()) {
                sb.append(String.format("  • %s (confidence: %.0f%%)\n", 
                    insight.description, insight.confidence * 100));
                sb.append(String.format("    Reasoning: %s\n", insight.reasoning));
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
}
