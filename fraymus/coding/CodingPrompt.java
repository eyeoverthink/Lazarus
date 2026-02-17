package fraymus.coding;

import fraymus.coding.CodingAgent.*;
import java.util.Map;

/**
 * 🎯 CODING PROMPT - Natural Language Code Interface
 * 
 * Command-line interface for the Coding Agent.
 * Processes natural language requests and displays results.
 * 
 * Commands:
 * - code: <request>        - Generate code from natural language
 * - code python: <request> - Generate Python code
 * - code java: <request>   - Generate Java code
 * - code show              - Show last generated code
 * - code stats             - Show statistics
 * - code history           - Show request history
 * - code help              - Show help
 */
public class CodingPrompt {
    
    private final CodingAgent agent;
    private CodingResult lastResult = null;
    
    public CodingPrompt(CodingAgent agent) {
        this.agent = agent;
    }
    
    /**
     * Process a command and return formatted output
     */
    public String processCommand(String command) {
        // Remove "code" prefix if present
        String cmd = command.trim();
        if (cmd.startsWith("code")) {
            cmd = cmd.substring(4).trim();
        }
        
        // Handle empty command
        if (cmd.isEmpty()) {
            return "Usage: code: <request>\nType 'code help' for more info";
        }
        
        // Handle sub-commands
        if (cmd.equals("help")) {
            return showHelp();
        } else if (cmd.equals("show")) {
            return showLastCode();
        } else if (cmd.equals("stats")) {
            return showStats();
        } else if (cmd.equals("history")) {
            return showHistory();
        } else {
            // Generate code
            return generateCode(cmd);
        }
    }
    
    /**
     * Generate code from natural language
     */
    private String generateCode(String prompt) {
        StringBuilder output = new StringBuilder();
        
        // Display request header
        output.append("🤖 CODING AGENT REQUEST #").append(agent.getTotalRequests() + 1).append("\n");
        output.append("   Prompt: ").append(prompt).append("\n\n");
        
        try {
            // Generate code
            long startTime = System.currentTimeMillis();
            CodingResult result = agent.generate(prompt);
            lastResult = result;
            
            // Display result
            output.append("   Language: ").append(result.request.language).append("\n");
            output.append("   Task: ").append(result.request.task).append("\n\n");
            
            if (result.examples != null && !result.examples.isEmpty()) {
                output.append("   Found ").append(result.examples.size());
                output.append(" examples in knowledge base\n\n");
            }
            
            output.append("   ✓ Code generated in ").append(result.generationTime).append("ms\n");
            output.append("   ✓ Validation: ").append(result.success ? "PASSED" : "FAILED").append("\n\n");
            
            // Display validation steps
            for (String step : result.validation.steps) {
                output.append("   ").append(step).append("\n");
            }
            output.append("\n");
            
            // Display code
            output.append("🤖 GENERATED CODE:\n\n");
            output.append("```").append(result.request.language).append("\n");
            output.append(result.code).append("\n");
            output.append("```\n\n");
            
            output.append("Type 'code show' to see this again\n");
            output.append("Type 'code stats' for statistics\n");
            
        } catch (Exception e) {
            output.append("❌ ERROR: ").append(e.getMessage()).append("\n");
        }
        
        return output.toString();
    }
    
    /**
     * Show last generated code
     */
    private String showLastCode() {
        if (lastResult == null) {
            return "No code has been generated yet.\nType 'code: <request>' to generate code.";
        }
        
        StringBuilder output = new StringBuilder();
        output.append("🤖 LAST GENERATED CODE\n\n");
        output.append("Language: ").append(lastResult.request.language).append("\n");
        output.append("Task: ").append(lastResult.request.task).append("\n");
        output.append("Generated: ").append(lastResult.generationTime).append("ms ago\n\n");
        output.append("```").append(lastResult.request.language).append("\n");
        output.append(lastResult.code).append("\n");
        output.append("```\n");
        
        return output.toString();
    }
    
    /**
     * Show statistics
     */
    private String showStats() {
        StringBuilder output = new StringBuilder();
        
        output.append("🤖 CODING AGENT STATS\n\n");
        output.append("   Total Requests: ").append(agent.getTotalRequests()).append("\n");
        output.append("   Success Rate: ").append(String.format("%.1f%%", agent.getSuccessRate())).append("\n");
        output.append("   Avg Time: ").append(agent.getAverageTime()).append("ms\n\n");
        
        Map<String, Integer> langStats = agent.getLanguageStats();
        if (!langStats.isEmpty()) {
            output.append("   Languages:\n");
            for (Map.Entry<String, Integer> entry : langStats.entrySet()) {
                output.append("     ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        
        return output.toString();
    }
    
    /**
     * Show request history
     */
    private String showHistory() {
        java.util.List<CodingRequest> history = agent.getHistory();
        
        if (history.isEmpty()) {
            return "No requests in history yet.";
        }
        
        StringBuilder output = new StringBuilder();
        output.append("🤖 CODING AGENT HISTORY\n\n");
        
        int start = Math.max(0, history.size() - 10);
        for (int i = start; i < history.size(); i++) {
            CodingRequest req = history.get(i);
            output.append(String.format("   %d. [%s] %s\n", 
                i + 1, req.language, req.task));
        }
        
        if (history.size() > 10) {
            output.append("\n   (Showing last 10 of ").append(history.size()).append(" requests)\n");
        }
        
        return output.toString();
    }
    
    /**
     * Show help
     */
    private String showHelp() {
        StringBuilder output = new StringBuilder();
        
        output.append("🤖 CODING AGENT - Natural Language to Code\n\n");
        output.append("COMMANDS:\n");
        output.append("  code: <request>              - Generate code from natural language\n");
        output.append("  code python: <request>       - Specify language explicitly\n");
        output.append("  code java: <request>         - Generate Java code\n");
        output.append("  code javascript: <request>   - Generate JavaScript code\n");
        output.append("  code show                    - Show last generated code\n");
        output.append("  code stats                   - Show statistics\n");
        output.append("  code history                 - Show request history\n");
        output.append("  code help                    - Show this help\n\n");
        
        output.append("SUPPORTED LANGUAGES:\n");
        output.append("  ✅ Python       ✅ Java         ✅ JavaScript\n");
        output.append("  ✅ TypeScript   ✅ C++          ✅ Rust\n");
        output.append("  ✅ Go           ✅ C#           ✅ Swift\n\n");
        
        output.append("EXAMPLES:\n");
        output.append("  code: write a function to calculate fibonacci\n");
        output.append("  code java: create a binary search tree class\n");
        output.append("  code python: implement quicksort algorithm\n");
        output.append("  code rust: create a hashmap with custom hash\n\n");
        
        output.append("FEATURES:\n");
        output.append("  🧠 Language understanding (Ollama/KAI)\n");
        output.append("  📚 Knowledge-based examples (no hallucination)\n");
        output.append("  🔄 Chain of Density refinement\n");
        output.append("  ✓  Process Reward Model validation\n");
        output.append("  🎯 Dynamic sampling (context-aware)\n");
        output.append("  φ  Phi-optimized generation\n\n");
        
        return output.toString();
    }
}
