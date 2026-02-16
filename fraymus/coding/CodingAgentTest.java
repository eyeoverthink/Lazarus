package fraymus.coding;

/**
 * Test the Coding Agent system
 */
public class CodingAgentTest {
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("🤖 CODING AGENT TEST");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        try {
            // Initialize components
            System.out.println("Initializing Coding Agent...");
            fraymus.OllamaSpine ollama = new fraymus.OllamaSpine();
            fraymus.LivingCodeGenerator codeGen = new fraymus.LivingCodeGenerator();
            
            CodingAgent agent = new CodingAgent(null, codeGen, ollama);
            CodingPrompt prompt = new CodingPrompt(agent);
            
            System.out.println("✓ Coding Agent initialized\n");
            
            // Test 1: Help command
            System.out.println("Test 1: Help Command");
            System.out.println("--------------------");
            String help = prompt.processCommand("help");
            System.out.println(help);
            System.out.println();
            
            // Test 2: Generate simple Python code
            System.out.println("\nTest 2: Generate Python Code");
            System.out.println("-----------------------------");
            String result = prompt.processCommand("code: write a simple fibonacci function");
            System.out.println(result);
            
            // Test 3: Show statistics
            System.out.println("\nTest 3: Statistics");
            System.out.println("------------------");
            String stats = prompt.processCommand("stats");
            System.out.println(stats);
            
            // Test 4: Show last code
            System.out.println("\nTest 4: Show Last Code");
            System.out.println("----------------------");
            String show = prompt.processCommand("show");
            System.out.println(show);
            
            System.out.println("\n═══════════════════════════════════════════════════");
            System.out.println("✅ CODING AGENT TEST COMPLETE");
            System.out.println("═══════════════════════════════════════════════════");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
