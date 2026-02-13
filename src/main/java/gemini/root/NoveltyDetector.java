package gemini.root;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import com.google.gson.*;

/**
 * NOVELTY DETECTOR
 * 
 * Determines what is genuinely NEW vs already known.
 * Enables curiosity-driven exploration and learning.
 * 
 * Core idea: The system should know what it knows.
 * 
 * Capabilities:
 * 1. Novelty scoring for queries and information
 * 2. Knowledge gap identification
 * 3. Exploration recommendations
 * 4. Semantic deduplication
 * 5. Learning progress tracking
 */
public class NoveltyDetector {
    
    private final OllamaSpine brain;
    private final VectorVault vault;
    private final Gson gson = new GsonBuilder().create();
    
    // Track what we've seen before
    private final Path seenQueriesPath = Paths.get("memory/seen_queries.jsonl");
    private final Set<String> queryFingerprints = new HashSet<>();
    
    public NoveltyDetector(OllamaSpine brain, VectorVault vault) {
        this.brain = brain;
        this.vault = vault;
        loadSeenQueries();
    }
    
    /**
     * Novelty assessment
     */
    public static class NoveltyScore {
        double queryNovelty;      // How novel is the query? (0=seen before, 1=completely new)
        double domainNovelty;     // How novel is the domain? (0=well-known, 1=new territory)
        double answerNovelty;     // How novel is the answer? (0=cached, 1=new insight)
        List<String> knownAspects;    // What we already know
        List<String> novelAspects;    // What's new
        List<String> gaps;            // What we don't know but should explore
        
        public double overall() {
            return (queryNovelty + domainNovelty + answerNovelty) / 3.0;
        }
    }
    
    /**
     * Assess novelty of a query
     */
    public NoveltyScore assessNovelty(String query, String context) {
        
        NoveltyScore score = new NoveltyScore();
        score.knownAspects = new ArrayList<>();
        score.novelAspects = new ArrayList<>();
        score.gaps = new ArrayList<>();
        
        // 1. Query novelty: Have we seen this before?
        String fingerprint = getQueryFingerprint(query);
        score.queryNovelty = queryFingerprints.contains(fingerprint) ? 0.0 : 1.0;
        
        // 2. Domain novelty: How much do we know about this topic?
        score.domainNovelty = assessDomainNovelty(query, context);
        
        // 3. Identify known vs novel aspects
        categorizeAspects(query, context, score);
        
        // 4. Identify knowledge gaps
        score.gaps = identifyGaps(query, context);
        
        // Record this query as seen
        queryFingerprints.add(fingerprint);
        persistQuery(query, fingerprint);
        
        return score;
    }
    
    /**
     * Get semantic fingerprint of query (for dedup)
     */
    private String getQueryFingerprint(String query) {
        // Normalize: lowercase, remove punctuation, sort words
        String normalized = query.toLowerCase()
            .replaceAll("[^a-z0-9\\s]", "")
            .trim();
        
        String[] words = normalized.split("\\s+");
        Arrays.sort(words);
        
        // Take significant words (skip common words)
        Set<String> significant = new HashSet<>();
        Set<String> stopwords = new HashSet<>(Arrays.asList(
            "the", "a", "an", "is", "are", "was", "were", "what", "how", "why", "when", "where"
        ));
        
        for (String word : words) {
            if (word.length() > 3 && !stopwords.contains(word)) {
                significant.add(word);
            }
        }
        
        return String.join("_", significant);
    }
    
    /**
     * Assess how well we know this domain
     */
    private double assessDomainNovelty(String query, String context) {
        
        // If we have rich context, domain is well-known
        if (context.contains("[S3]") || context.length() > 2000) {
            return 0.2; // Well-known domain
        }
        
        if (context.contains("[S1]") || context.length() > 500) {
            return 0.5; // Some knowledge
        }
        
        // No context = novel domain
        return 0.9;
    }
    
    /**
     * Categorize query aspects into known vs novel
     */
    private void categorizeAspects(String query, String context, NoveltyScore score) {
        
        String prompt = String.format("""
            Query: %s
            
            Available context: %s
            
            Categorize aspects of this query:
            1. What aspects are KNOWN (covered by context)?
            2. What aspects are NOVEL (not in context)?
            
            Return JSON:
            {
              "known": ["aspect 1", "aspect 2"],
              "novel": ["aspect 3", "aspect 4"]
            }
            """, query, context.substring(0, Math.min(1000, context.length())));
        
        List<OllamaSpine.Msg> msgs = List.of(
            new OllamaSpine.Msg("system", "You categorize known vs novel information."),
            new OllamaSpine.Msg("user", prompt)
        );
        
        try {
            String result = brain.chatOnce(msgs, null, Map.of("temperature", 0.1));
            JsonObject obj = JsonParser.parseString(result).getAsJsonObject();
            
            JsonArray known = obj.getAsJsonArray("known");
            JsonArray novel = obj.getAsJsonArray("novel");
            
            for (int i = 0; i < known.size(); i++) {
                score.knownAspects.add(known.get(i).getAsString());
            }
            
            for (int i = 0; i < novel.size(); i++) {
                score.novelAspects.add(novel.get(i).getAsString());
            }
            
            // Answer novelty based on proportion of novel aspects
            int total = score.knownAspects.size() + score.novelAspects.size();
            score.answerNovelty = total > 0 ? 
                (double) score.novelAspects.size() / total : 0.5;
            
        } catch (Exception e) {
            // Fallback: if no context, everything is novel
            score.answerNovelty = context.length() < 100 ? 0.9 : 0.3;
        }
    }
    
    /**
     * Identify knowledge gaps worth exploring
     */
    private List<String> identifyGaps(String query, String context) {
        
        String prompt = String.format("""
            Query: %s
            Context: %s
            
            What important related topics are NOT covered that would enhance understanding?
            List 2-3 knowledge gaps worth exploring.
            
            Return JSON array: ["gap 1", "gap 2", "gap 3"]
            """, query, context.substring(0, Math.min(1000, context.length())));
        
        List<OllamaSpine.Msg> msgs = List.of(
            new OllamaSpine.Msg("system", "You identify knowledge gaps."),
            new OllamaSpine.Msg("user", prompt)
        );
        
        try {
            String result = brain.chatOnce(msgs, null, Map.of("temperature", 0.3));
            JsonArray arr = JsonParser.parseString(result).getAsJsonArray();
            
            List<String> gaps = new ArrayList<>();
            for (int i = 0; i < Math.min(5, arr.size()); i++) {
                gaps.add(arr.get(i).getAsString());
            }
            return gaps;
            
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Recommend exploration directions based on novelty
     */
    public List<String> recommendExploration(NoveltyScore novelty) {
        
        List<String> recommendations = new ArrayList<>();
        
        if (novelty.overall() > 0.7) {
            recommendations.add("High novelty detected - consider indexing new sources on: " + 
                String.join(", ", novelty.novelAspects));
        }
        
        if (!novelty.gaps.isEmpty()) {
            recommendations.add("Knowledge gaps identified: " + 
                String.join(", ", novelty.gaps));
        }
        
        if (novelty.domainNovelty > 0.8) {
            recommendations.add("New domain - build foundational knowledge");
        }
        
        if (novelty.queryNovelty < 0.3 && novelty.answerNovelty < 0.3) {
            recommendations.add("Repeated query with known answer - consider caching");
        }
        
        return recommendations;
    }
    
    /**
     * Format novelty report
     */
    public String formatReport(NoveltyScore novelty) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== NOVELTY ASSESSMENT ===\n\n");
        sb.append(String.format("Overall Novelty: %.0f%%\n", novelty.overall() * 100));
        sb.append(String.format("  Query: %.0f%% | Domain: %.0f%% | Answer: %.0f%%\n\n",
            novelty.queryNovelty * 100,
            novelty.domainNovelty * 100,
            novelty.answerNovelty * 100));
        
        if (!novelty.knownAspects.isEmpty()) {
            sb.append("Known aspects:\n");
            for (String aspect : novelty.knownAspects) {
                sb.append("  ✓ ").append(aspect).append("\n");
            }
            sb.append("\n");
        }
        
        if (!novelty.novelAspects.isEmpty()) {
            sb.append("Novel aspects:\n");
            for (String aspect : novelty.novelAspects) {
                sb.append("  ★ ").append(aspect).append("\n");
            }
            sb.append("\n");
        }
        
        if (!novelty.gaps.isEmpty()) {
            sb.append("Knowledge gaps:\n");
            for (String gap : novelty.gaps) {
                sb.append("  ? ").append(gap).append("\n");
            }
            sb.append("\n");
        }
        
        List<String> recs = recommendExploration(novelty);
        if (!recs.isEmpty()) {
            sb.append("Recommendations:\n");
            for (String rec : recs) {
                sb.append("  → ").append(rec).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Load seen queries
     */
    private void loadSeenQueries() {
        try {
            if (!Files.exists(seenQueriesPath)) {
                Files.createDirectories(seenQueriesPath.getParent());
                return;
            }
            
            List<String> lines = Files.readAllLines(seenQueriesPath);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                JsonObject obj = JsonParser.parseString(line).getAsJsonObject();
                queryFingerprints.add(obj.get("fingerprint").getAsString());
            }
            
        } catch (Exception e) {
            System.err.println("Failed to load seen queries: " + e.getMessage());
        }
    }
    
    /**
     * Persist query as seen
     */
    private void persistQuery(String query, String fingerprint) {
        try {
            Files.createDirectories(seenQueriesPath.getParent());
            
            JsonObject obj = new JsonObject();
            obj.addProperty("query", query);
            obj.addProperty("fingerprint", fingerprint);
            obj.addProperty("timestamp", System.currentTimeMillis());
            
            String json = gson.toJson(obj);
            Files.write(seenQueriesPath, (json + "\n").getBytes(),
                       StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {
            // Silent fail - not critical
        }
    }
}
