package gemini.root;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * AGENT ORCHESTRATOR
 * 
 * Multi-agent ensemble reasoning system.
 * Coordinates multiple independent reasoning agents to achieve:
 * - Diverse perspectives
 * - Error correction through voting
 * - Emergent insights from agent debate
 * - Robustness against single-model failures
 * 
 * Architecture:
 *   Query → [Agent1, Agent2, Agent3] → Synthesis → Superior Answer
 * 
 * Agents have different:
 * - Temperatures (creative vs precise)
 * - System prompts (skeptic vs optimist vs neutral)
 * - Contexts (different RAG retrievals)
 */
public class AgentOrchestrator {
    
    private final OllamaSpine brain;
    private final ExecutorService executor;
    
    public AgentOrchestrator(OllamaSpine brain) {
        this.brain = brain;
        this.executor = Executors.newFixedThreadPool(3);
    }
    
    /**
     * Agent persona configuration
     */
    public static class AgentPersona {
        String name;
        String systemPrompt;
        double temperature;
        
        public AgentPersona(String name, String systemPrompt, double temperature) {
            this.name = name;
            this.systemPrompt = systemPrompt;
            this.temperature = temperature;
        }
    }
    
    /**
     * Agent response with metadata
     */
    public static class AgentResponse {
        String agentName;
        String answer;
        double confidence;
        long latencyMs;
        
        public AgentResponse(String agentName, String answer, double confidence, long latencyMs) {
            this.agentName = agentName;
            this.answer = answer;
            this.confidence = confidence;
            this.latencyMs = latencyMs;
        }
    }
    
    /**
     * Multi-agent reasoning with parallel execution
     * 
     * @param query User question
     * @param context Shared context packet (RAG + tools)
     * @param history Session history
     * @return Synthesized best answer
     */
    public String orchestrate(String query, String context, List<OllamaSpine.Msg> history) {
        
        // Define three distinct agent personas
        List<AgentPersona> personas = Arrays.asList(
            new AgentPersona("Skeptic", """
                You are a rigorous skeptic.
                Question assumptions, demand evidence, point out logical flaws.
                Be precise and conservative. Prefer "I don't know" to guessing.
                """, 0.1),
            
            new AgentPersona("Explorer", """
                You are a creative explorer.
                Make novel connections, suggest alternatives, think laterally.
                Be bold but clearly mark speculations vs facts.
                """, 0.8),
            
            new AgentPersona("Synthesizer", """
                You are a balanced synthesizer.
                Integrate multiple perspectives, find common ground, be comprehensive.
                Balance creativity with rigor.
                """, 0.4)
        );
        
        // Execute agents in parallel
        List<CompletableFuture<AgentResponse>> futures = personas.stream()
            .map(persona -> CompletableFuture.supplyAsync(() -> 
                executeAgent(persona, query, context, history), executor))
            .collect(Collectors.toList());
        
        // Wait for all agents
        List<AgentResponse> responses = futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
        
        // Synthesize the best answer
        return synthesize(query, context, responses);
    }
    
    /**
     * Execute single agent
     */
    private AgentResponse executeAgent(AgentPersona persona, String query, 
                                       String context, List<OllamaSpine.Msg> history) {
        long start = System.currentTimeMillis();
        
        List<OllamaSpine.Msg> msgs = new ArrayList<>();
        msgs.add(new OllamaSpine.Msg("system", persona.systemPrompt));
        if (history != null && !history.isEmpty()) {
            msgs.addAll(history);
        }
        msgs.add(new OllamaSpine.Msg("user", context + "\n\nQUERY: " + query));
        
        String answer = brain.chatOnce(msgs, null, Map.of(
            "temperature", persona.temperature,
            "num_ctx", 8192
        ));
        
        long latency = System.currentTimeMillis() - start;
        
        // Simple confidence heuristic: longer, cited answers = higher confidence
        double confidence = calculateConfidence(answer);
        
        return new AgentResponse(persona.name, answer, confidence, latency);
    }
    
    /**
     * Synthesize multiple agent responses into best answer
     */
    private String synthesize(String query, String context, List<AgentResponse> responses) {
        
        // Build synthesis prompt
        StringBuilder synthPrompt = new StringBuilder();
        synthPrompt.append("ORIGINAL QUERY:\n").append(query).append("\n\n");
        synthPrompt.append("CONTEXT:\n").append(context).append("\n\n");
        synthPrompt.append("AGENT RESPONSES:\n\n");
        
        for (AgentResponse resp : responses) {
            synthPrompt.append("--- ").append(resp.agentName)
                       .append(" (confidence: ").append(String.format("%.2f", resp.confidence))
                       .append(") ---\n")
                       .append(resp.answer).append("\n\n");
        }
        
        synthPrompt.append("""
            Your task: Synthesize the BEST answer by:
            1. Taking the strongest points from each agent
            2. Resolving contradictions by evaluating evidence
            3. Preserving all citations [S#]
            4. Noting where agents disagree (if significant)
            5. Producing a superior answer better than any single agent
            
            Rules:
            - If agents agree: reinforce with confidence
            - If agents disagree on facts: favor the one with citations
            - If agents disagree on interpretation: present both views
            - Don't just concatenate; truly synthesize
            - Be concise but complete
            """);
        
        List<OllamaSpine.Msg> synthMsgs = List.of(
            new OllamaSpine.Msg("system", "You are a master synthesizer. Create superior answers."),
            new OllamaSpine.Msg("user", synthPrompt.toString())
        );
        
        return brain.chatOnce(synthMsgs, null, Map.of(
            "temperature", 0.3,
            "num_ctx", 12288
        ));
    }
    
    /**
     * Calculate confidence score for an answer
     */
    private double calculateConfidence(String answer) {
        if (answer == null || answer.isEmpty()) return 0.0;
        
        double score = 0.5; // baseline
        
        // Has citations
        if (answer.contains("[S1]") || answer.contains("[S2]")) {
            score += 0.2;
        }
        
        // Substantial length (not too short, not too verbose)
        int len = answer.length();
        if (len > 200 && len < 2000) {
            score += 0.15;
        }
        
        // Contains reasoning markers
        if (answer.contains("because") || answer.contains("therefore") || answer.contains("however")) {
            score += 0.1;
        }
        
        // Acknowledges uncertainty when appropriate
        if (answer.contains("not found") || answer.contains("don't have") || answer.contains("unclear")) {
            score += 0.05;
        }
        
        return Math.min(1.0, score);
    }
    
    /**
     * Cleanup resources
     */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
