package fraymus.cortex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🧬 THE TOPOLOGICAL CORTEX
 * 
 * The 3D Graph-Based Memory System.
 * 
 * Instead of storing data in a flat list or map, the Topological Cortex
 * stores data as nodes in 3D space with geometric relationships.
 * 
 * Key Concepts:
 * - Data is stored as PhiNodes with (x, y, z) coordinates
 * - Related concepts form Hebbian synaptic connections
 * - The manifold applies spring forces - connected nodes pull together
 * - The system "breathes" - the geometry evolves over time
 * - Weak nodes (low resonance) are pruned
 * 
 * This replaces the traditional flat Akashic Record with a living,
 * spatial memory system.
 */
public class TopologicalCortex {
    
    private Manifold manifold;
    private Map<String, PhiNode> index; // Fast lookup by data content
    private List<String> categories;
    
    // CONFIGURATION
    private static final double AUTO_CONNECT_THRESHOLD = 20.0; // Auto-connect nodes within this distance
    private static final int MAX_AUTO_CONNECTIONS = 5;         // Max automatic connections per node
    
    // STATISTICS
    private long addCount = 0;
    private long queryCount = 0;
    private long tickCount = 0;
    
    private static final double PHI = 1.618033988749895;
    
    public TopologicalCortex() {
        this.manifold = new Manifold();
        this.index = new HashMap<>();
        this.categories = new ArrayList<>();
        
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ TOPOLOGICAL CORTEX INITIALIZED                            ║");
        System.out.println("║ 3D Spatial Memory System Active                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }
    
    /**
     * ADD - Store data in the cortex
     * Data is converted to a PhiNode and placed in 3D space
     */
    public PhiNode add(String category, Object data) {
        addCount++;
        
        // Create node
        PhiNode node = new PhiNode(data);
        
        // Add to manifold
        manifold.addNode(node);
        
        // Index by string representation
        String key = category + ":" + data.toString();
        index.put(key, node);
        
        // Track category
        if (!categories.contains(category)) {
            categories.add(category);
        }
        
        // Auto-connect to nearby nodes (Hebbian linking)
        autoConnect(node, category);
        
        System.out.println(String.format("   [CORTEX] Added node: %s | Category: %s", 
            node.id.substring(0, 8), category));
        
        return node;
    }
    
    /**
     * AUTO-CONNECT - Automatically create synapses to nearby nodes
     * This implements Hebbian learning - related concepts wire together
     */
    private void autoConnect(PhiNode newNode, String category) {
        List<PhiNode> nearby = manifold.findNodesInRadius(
            newNode.x, newNode.y, newNode.z, AUTO_CONNECT_THRESHOLD
        );
        
        int connectCount = 0;
        for (PhiNode neighbor : nearby) {
            if (connectCount >= MAX_AUTO_CONNECTIONS) break;
            if (neighbor == newNode) continue;
            
            // Connect
            newNode.synapse(neighbor);
            connectCount++;
        }
        
        if (connectCount > 0) {
            System.out.println(String.format("   [CORTEX] Auto-connected to %d nearby nodes", connectCount));
        }
    }
    
    /**
     * QUERY - Search for data
     * Returns nodes whose data contains the search term
     */
    public List<PhiNode> query(String searchTerm) {
        queryCount++;
        
        List<PhiNode> results = new ArrayList<>();
        
        for (PhiNode node : manifold.getNodes()) {
            if (node.data != null && node.data.toString().toLowerCase().contains(searchTerm.toLowerCase())) {
                node.access(); // Boost resonance
                results.add(node);
            }
        }
        
        return results;
    }
    
    /**
     * QUERY BY CATEGORY
     * Search index for nodes by category prefix
     */
    public List<PhiNode> queryCategory(String category) {
        queryCount++;
        
        List<PhiNode> results = new ArrayList<>();
        String prefix = category + ":";
        
        for (Map.Entry<String, PhiNode> entry : index.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                PhiNode node = entry.getValue();
                node.access(); // Boost resonance
                results.add(node);
            }
        }
        
        return results;
    }
    
    /**
     * FIND NEAREST - Find node closest to a point in space
     */
    public PhiNode findNearest(double x, double y, double z) {
        return manifold.findNearest(x, y, z);
    }
    
    /**
     * GET CONNECTED - Get all nodes connected to a given node
     */
    public List<PhiNode> getConnected(PhiNode node) {
        if (node == null) return new ArrayList<>();
        return new ArrayList<>(node.connections);
    }
    
    /**
     * TICK - Advance the physics simulation
     * This makes the brain "breathe" - the geometry evolves
     */
    public void tick(double deltaTime) {
        manifold.tick(deltaTime);
        tickCount++;
    }
    
    /**
     * TICK multiple steps
     */
    public void tick(int steps, double deltaTime) {
        for (int i = 0; i < steps; i++) {
            tick(deltaTime);
        }
    }
    
    /**
     * GET ALL NODES
     */
    public List<PhiNode> getAllNodes() {
        return manifold.getNodes();
    }
    
    /**
     * GET NODE COUNT
     */
    public int getNodeCount() {
        return manifold.getNodeCount();
    }
    
    /**
     * PRINT STATISTICS
     */
    public void printStats() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ TOPOLOGICAL CORTEX STATISTICS                             ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println(String.format("║ Total Nodes:         %-36d║", manifold.getNodeCount()));
        System.out.println(String.format("║ Categories:          %-36d║", categories.size()));
        System.out.println(String.format("║ Nodes Added:         %-36d║", addCount));
        System.out.println(String.format("║ Queries Processed:   %-36d║", queryCount));
        System.out.println(String.format("║ Ticks Simulated:     %-36d║", tickCount));
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        // Also print manifold stats
        manifold.printStats();
    }
    
    /**
     * VISUALIZE - Generate ASCII art representation of the cortex
     */
    public void visualize() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ CORTEX VISUALIZATION (XY Plane)                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        if (manifold.getNodeCount() == 0) {
            System.out.println("   [Empty cortex - no nodes]");
            return;
        }
        
        // Find bounds
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
        
        for (PhiNode node : manifold.getNodes()) {
            minX = Math.min(minX, node.x);
            maxX = Math.max(maxX, node.x);
            minY = Math.min(minY, node.y);
            maxY = Math.max(maxY, node.y);
        }
        
        // Create grid
        int width = 60;
        int height = 20;
        char[][] grid = new char[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = ' ';
            }
        }
        
        // Plot nodes
        for (PhiNode node : manifold.getNodes()) {
            int gridX = (int)((node.x - minX) / (maxX - minX + 1) * (width - 1));
            int gridY = (int)((node.y - minY) / (maxY - minY + 1) * (height - 1));
            
            if (gridX >= 0 && gridX < width && gridY >= 0 && gridY < height) {
                // Character based on resonance
                if (node.resonance > 0.8) {
                    grid[gridY][gridX] = '@';
                } else if (node.resonance > 0.5) {
                    grid[gridY][gridX] = 'O';
                } else if (node.resonance > 0.3) {
                    grid[gridY][gridX] = 'o';
                } else {
                    grid[gridY][gridX] = '.';
                }
            }
        }
        
        // Print grid
        System.out.println("   ┌" + "─".repeat(width) + "┐");
        for (int y = height - 1; y >= 0; y--) {
            System.out.print("   │");
            for (int x = 0; x < width; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println("│");
        }
        System.out.println("   └" + "─".repeat(width) + "┘");
        System.out.println("   Legend: @ = High resonance, O = Medium, o = Low, . = Very low");
    }
    
    /**
     * Clear all nodes
     */
    public void clear() {
        manifold.clear();
        index.clear();
        categories.clear();
    }
    
    /**
     * DEMO - Demonstrate the cortex
     */
    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ TOPOLOGICAL CORTEX DEMONSTRATION                          ║");
        System.out.println("║ 3D Spatial Memory with Hebbian Learning                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        TopologicalCortex cortex = new TopologicalCortex();
        
        // Add some knowledge
        System.out.println("\n>> Adding knowledge to cortex...\n");
        cortex.add("MATH", "PI = 3.14159265359");
        cortex.add("MATH", "PHI = 1.618033988749895");
        cortex.add("MATH", "E = 2.71828182845904");
        cortex.add("PHYSICS", "c = 299792458 m/s");
        cortex.add("PHYSICS", "G = 6.67430e-11");
        cortex.add("BIOLOGY", "DNA has 4 bases: A, T, G, C");
        cortex.add("BIOLOGY", "Neurons fire together, wire together");
        
        // Simulate physics
        System.out.println("\n>> Simulating manifold physics (100 ticks)...\n");
        cortex.tick(100, 0.1);
        
        // Visualize
        cortex.visualize();
        
        // Query
        System.out.println("\n>> Querying for 'PHI'...\n");
        List<PhiNode> results = cortex.query("PHI");
        for (PhiNode node : results) {
            System.out.println("   Found: " + node);
            System.out.println("   Connected to " + node.connections.size() + " other nodes");
        }
        
        // Query by category
        System.out.println("\n>> Querying category 'MATH'...\n");
        results = cortex.queryCategory("MATH");
        for (PhiNode node : results) {
            System.out.println("   " + node);
        }
        
        // Statistics
        cortex.printStats();
        
        System.out.println("\n   ✓ Topological Cortex demo complete.\n");
    }
}
