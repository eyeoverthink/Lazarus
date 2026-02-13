package fraymus.cortex;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 🧬 THE MANIFOLD
 * The 11-Dimensional shape that holds the nodes.
 * 
 * This is the physics engine that makes the brain "breathe" - 
 * nodes pull together (attraction) when connected, and push apart
 * (repulsion) when too close.
 * 
 * Uses Hooke's Law for spring forces between connected nodes.
 */
public class Manifold {
    private List<PhiNode> nodes;
    
    // PHYSICS CONSTANTS
    private static final double PHI = 1.618033988749895;
    private static final double SPRING_CONSTANT = 0.05;      // k in Hooke's Law (F = -kx)
    private static final double REPULSION_STRENGTH = 50.0;   // Electrostatic repulsion
    private static final double IDEAL_DISTANCE = 10.0;       // Ideal spring length
    private static final double DAMPING = 0.95;              // Velocity damping (friction)
    private static final double MAX_FORCE = 100.0;           // Force cap to prevent explosions
    
    // DECAY SETTINGS
    private static final double RESONANCE_DECAY_RATE = 0.001; // Per tick
    private static final double PRUNE_THRESHOLD = 0.1;        // Remove nodes below this resonance
    
    // STATISTICS
    private long tickCount = 0;
    private int nodesPruned = 0;
    
    public Manifold() {
        this.nodes = new CopyOnWriteArrayList<>();
    }
    
    /**
     * ADD NODE - Add a particle to the manifold
     */
    public void addNode(PhiNode node) {
        if (node != null && !nodes.contains(node)) {
            nodes.add(node);
        }
    }
    
    /**
     * REMOVE NODE - Remove a particle from the manifold
     */
    public void removeNode(PhiNode node) {
        if (node == null) return;
        
        // Disconnect from all other nodes first
        for (PhiNode other : node.connections) {
            other.connections.remove(node);
        }
        node.connections.clear();
        
        nodes.remove(node);
        nodesPruned++;
    }
    
    /**
     * GET ALL NODES
     */
    public List<PhiNode> getNodes() {
        return nodes;
    }
    
    /**
     * TICK - One physics simulation step
     * This is where the magic happens - the brain "breathes"
     */
    public void tick(double deltaTime) {
        tickCount++;
        
        // 1. APPLY FORCES
        applyForces();
        
        // 2. UPDATE POSITIONS
        for (PhiNode node : nodes) {
            node.updatePosition(deltaTime);
            node.applyDamping(DAMPING);
        }
        
        // 3. DECAY RESONANCE
        for (PhiNode node : nodes) {
            node.decay(RESONANCE_DECAY_RATE);
        }
        
        // 4. PRUNE WEAK NODES (every 100 ticks)
        if (tickCount % 100 == 0) {
            pruneWeakNodes();
        }
    }
    
    /**
     * APPLY FORCES - Spring forces and repulsion
     * 
     * Connected nodes: Spring force pulls them to ideal distance
     * All nodes: Repulsion force pushes apart if too close
     */
    private void applyForces() {
        // Reset forces
        for (PhiNode node : nodes) {
            // Gravity towards center (keeps the manifold bounded)
            double centerForce = -0.001;
            node.applyForce(node.x * centerForce, node.y * centerForce, node.z * centerForce);
        }
        
        // Process each node
        for (int i = 0; i < nodes.size(); i++) {
            PhiNode nodeA = nodes.get(i);
            
            for (int j = i + 1; j < nodes.size(); j++) {
                PhiNode nodeB = nodes.get(j);
                
                double distance = nodeA.distanceTo(nodeB);
                if (distance < 0.001) distance = 0.001; // Avoid division by zero
                
                // Direction vector from A to B
                double dx = nodeB.x - nodeA.x;
                double dy = nodeB.y - nodeA.y;
                double dz = nodeB.z - nodeA.z;
                
                // Normalize
                double nx = dx / distance;
                double ny = dy / distance;
                double nz = dz / distance;
                
                // REPULSION (all nodes push apart if too close)
                if (distance < IDEAL_DISTANCE * 2) {
                    double repulsionForce = REPULSION_STRENGTH / (distance * distance);
                    repulsionForce = Math.min(repulsionForce, MAX_FORCE);
                    
                    // Push apart
                    nodeA.applyForce(-nx * repulsionForce, -ny * repulsionForce, -nz * repulsionForce);
                    nodeB.applyForce(nx * repulsionForce, ny * repulsionForce, nz * repulsionForce);
                }
                
                // SPRING FORCE (connected nodes pull together)
                if (nodeA.isConnectedTo(nodeB)) {
                    // Hooke's Law: F = -k * (distance - ideal_distance)
                    double displacement = distance - IDEAL_DISTANCE;
                    double springForce = SPRING_CONSTANT * displacement;
                    springForce = Math.max(-MAX_FORCE, Math.min(MAX_FORCE, springForce));
                    
                    // Scale by connection strength (resonance)
                    double strength = nodeA.getConnectionStrength(nodeB);
                    springForce *= strength;
                    
                    // Pull together
                    nodeA.applyForce(nx * springForce, ny * springForce, nz * springForce);
                    nodeB.applyForce(-nx * springForce, -ny * springForce, -nz * springForce);
                }
            }
        }
    }
    
    /**
     * PRUNE WEAK NODES
     * Remove nodes with very low resonance (they've been forgotten)
     */
    private void pruneWeakNodes() {
        List<PhiNode> toPrune = new ArrayList<>();
        
        for (PhiNode node : nodes) {
            if (node.resonance < PRUNE_THRESHOLD && node.connections.isEmpty()) {
                toPrune.add(node);
            }
        }
        
        for (PhiNode node : toPrune) {
            removeNode(node);
        }
    }
    
    /**
     * FIND NEAREST NODE to a point in space
     */
    public PhiNode findNearest(double x, double y, double z) {
        PhiNode nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (PhiNode node : nodes) {
            double dx = node.x - x;
            double dy = node.y - y;
            double dz = node.z - z;
            double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
            
            if (distance < minDistance) {
                minDistance = distance;
                nearest = node;
            }
        }
        
        return nearest;
    }
    
    /**
     * FIND NODES within a radius
     */
    public List<PhiNode> findNodesInRadius(double x, double y, double z, double radius) {
        List<PhiNode> found = new ArrayList<>();
        
        for (PhiNode node : nodes) {
            double dx = node.x - x;
            double dy = node.y - y;
            double dz = node.z - z;
            double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
            
            if (distance <= radius) {
                found.add(node);
            }
        }
        
        return found;
    }
    
    /**
     * GET STATISTICS
     */
    public void printStats() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ MANIFOLD STATISTICS                                       ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println(String.format("║ Total Nodes:         %-36d║", nodes.size()));
        System.out.println(String.format("║ Ticks Simulated:     %-36d║", tickCount));
        System.out.println(String.format("║ Nodes Pruned:        %-36d║", nodesPruned));
        
        // Calculate average resonance
        double avgResonance = 0;
        int totalConnections = 0;
        for (PhiNode node : nodes) {
            avgResonance += node.resonance;
            totalConnections += node.connections.size();
        }
        if (!nodes.isEmpty()) {
            avgResonance /= nodes.size();
        }
        
        System.out.println(String.format("║ Average Resonance:   %-36.3f║", avgResonance));
        System.out.println(String.format("║ Total Connections:   %-36d║", totalConnections / 2)); // Divide by 2 (bidirectional)
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }
    
    /**
     * Clear all nodes
     */
    public void clear() {
        for (PhiNode node : nodes) {
            node.connections.clear();
        }
        nodes.clear();
    }
    
    public int getNodeCount() {
        return nodes.size();
    }
    
    public long getTickCount() {
        return tickCount;
    }
}
