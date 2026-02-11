package fraymus.cortex;

/**
 * 🧬 CORTEX ADAPTER
 * 
 * Bridges the Topological Cortex with the existing Akashic Record system.
 * 
 * This allows legacy code using the simple Map-based AkashicRecord
 * to seamlessly upgrade to the 3D spatial cortex system.
 */
public class CortexAdapter {
    
    private TopologicalCortex cortex;
    
    // Auto-tick configuration
    private boolean autoTick = true;
    private int tickInterval = 10; // Tick physics every N operations
    private int operationCount = 0;
    
    public CortexAdapter() {
        this.cortex = new TopologicalCortex();
    }
    
    /**
     * ADD - Add data to the cortex (compatible with AkashicRecord API)
     */
    public String addBlock(String category, String content) {
        PhiNode node = cortex.add(category, content);
        
        // Auto-tick the physics simulation
        if (autoTick) {
            operationCount++;
            if (operationCount >= tickInterval) {
                cortex.tick(1, 0.1);
                operationCount = 0;
            }
        }
        
        return node.id;
    }
    
    /**
     * QUERY - Search for content (compatible with AkashicRecord API)
     * Returns formatted strings instead of nodes
     */
    public java.util.List<String> query(String searchTerm) {
        java.util.List<PhiNode> nodes = cortex.query(searchTerm);
        java.util.List<String> results = new java.util.ArrayList<>();
        
        for (PhiNode node : nodes) {
            String result = String.format("[Resonance: %.3f] %s", 
                node.resonance, node.data.toString());
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * QUERY BY CATEGORY - Get all data in a category
     */
    public java.util.List<String> queryCategory(String category) {
        java.util.List<PhiNode> nodes = cortex.queryCategory(category);
        java.util.List<String> results = new java.util.ArrayList<>();
        
        for (PhiNode node : nodes) {
            results.add(node.data.toString());
        }
        
        return results;
    }
    
    /**
     * GET RAW CORTEX - Access the underlying cortex for advanced operations
     */
    public TopologicalCortex getCortex() {
        return cortex;
    }
    
    /**
     * TICK PHYSICS - Manually advance the simulation
     */
    public void tick(int steps) {
        cortex.tick(steps, 0.1);
    }
    
    /**
     * VISUALIZE - Show the cortex structure
     */
    public void visualize() {
        cortex.visualize();
    }
    
    /**
     * PRINT STATS
     */
    public void printStats() {
        cortex.printStats();
    }
    
    /**
     * Enable/disable auto-tick
     */
    public void setAutoTick(boolean enabled) {
        this.autoTick = enabled;
    }
    
    /**
     * Set tick interval
     */
    public void setTickInterval(int interval) {
        this.tickInterval = interval;
    }
    
    /**
     * DEMO - Show how to use the adapter
     */
    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ CORTEX ADAPTER DEMONSTRATION                              ║");
        System.out.println("║ Bridge between AkashicRecord API and Topological Cortex   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        CortexAdapter adapter = new CortexAdapter();
        
        // Add knowledge using familiar API
        System.out.println(">> Adding knowledge (AkashicRecord-compatible API)...\n");
        adapter.addBlock("MATH", "Fibonacci: 1, 1, 2, 3, 5, 8, 13, 21...");
        adapter.addBlock("MATH", "Golden Ratio PHI = 1.618033988749895");
        adapter.addBlock("PHYSICS", "E = mc²");
        adapter.addBlock("PHYSICS", "F = ma");
        adapter.addBlock("BIOLOGY", "Mitochondria is the powerhouse of the cell");
        
        // Run some physics ticks
        System.out.println("\n>> Running physics simulation...\n");
        adapter.tick(50);
        
        // Query
        System.out.println(">> Querying for 'PHI'...\n");
        java.util.List<String> results = adapter.query("PHI");
        for (String result : results) {
            System.out.println("   " + result);
        }
        
        // Visualize the cortex
        adapter.visualize();
        
        // Stats
        adapter.printStats();
        
        System.out.println("\n   ✓ Cortex Adapter demo complete.\n");
    }
}
