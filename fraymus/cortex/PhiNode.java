package fraymus.cortex;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 🧬 THE PARTICLE
 * A single point in the Calabi-Yau Manifold.
 * 
 * This is a data node in the Topological Cortex - a 3D spatial memory system.
 * Unlike traditional flat storage, each node has geometric coordinates and
 * forms Hebbian connections with related nodes.
 */
public class PhiNode {
    public String id;
    public Object data; // The memory (String, Image, Logic)
    
    // SPATIAL COORDINATES (The 3D position in the brain)
    public double x, y, z;
    
    // ENERGY METRICS
    public double resonance; // 0.0 to 1.0 (How 'lit up' it is)
    public List<PhiNode> connections; // Synaptic links to other nodes
    
    // PHYSICS PROPERTIES (for spring force simulation)
    public double vx, vy, vz; // Velocity
    public double mass; // For gravitational effects
    
    // TEMPORAL TRACKING
    public long createdAt;
    public long lastAccessedAt;
    public int accessCount;
    
    private static final double PHI = 1.618033988749895;

    /**
     * Constructor - Creates a new node in the void
     */
    public PhiNode(Object data) {
        this.id = UUID.randomUUID().toString();
        this.data = data;
        this.connections = new ArrayList<>();
        
        // Initialize at random point in the 'Void'
        this.x = (Math.random() - 0.5) * 100;
        this.y = (Math.random() - 0.5) * 100;
        this.z = (Math.random() - 0.5) * 100;
        
        // Initialize velocity
        this.vx = 0;
        this.vy = 0;
        this.vz = 0;
        
        // Base Phi charge
        this.resonance = 0.618; // φ - 1
        this.mass = 1.0;
        
        // Temporal tracking
        this.createdAt = System.currentTimeMillis();
        this.lastAccessedAt = this.createdAt;
        this.accessCount = 0;
    }
    
    /**
     * Constructor with specific position
     */
    public PhiNode(Object data, double x, double y, double z) {
        this(data);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * HEBBIAN LINKING
     * "Neurons that fire together, wire together."
     * Wire this node with another - creates bidirectional connection.
     */
    public void synapse(PhiNode other) {
        if (other == null || other == this) return;
        
        if (!this.connections.contains(other)) {
            this.connections.add(other);
            other.connections.add(this); // Bidirectional
            
            // Boost resonance when connecting
            this.resonance = Math.min(1.0, this.resonance + 0.05);
            other.resonance = Math.min(1.0, other.resonance + 0.05);
        }
    }
    
    /**
     * DISCONNECT - Remove a synaptic connection
     */
    public void disconnect(PhiNode other) {
        if (other == null) return;
        
        this.connections.remove(other);
        other.connections.remove(this);
        
        // Reduce resonance when disconnecting
        this.resonance = Math.max(0.0, this.resonance - 0.05);
        other.resonance = Math.max(0.0, other.resonance - 0.05);
    }
    
    /**
     * ACCESS - Mark this node as accessed
     */
    public void access() {
        this.lastAccessedAt = System.currentTimeMillis();
        this.accessCount++;
        
        // Boost resonance on access
        this.resonance = Math.min(1.0, this.resonance + 0.1);
    }
    
    /**
     * DECAY - Reduce resonance over time (entropy)
     */
    public void decay(double amount) {
        this.resonance = Math.max(0.0, this.resonance - amount);
    }
    
    /**
     * Distance to another node in 3D space
     */
    public double distanceTo(PhiNode other) {
        if (other == null) return Double.MAX_VALUE;
        
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
    /**
     * Is this node connected to another?
     */
    public boolean isConnectedTo(PhiNode other) {
        return this.connections.contains(other);
    }
    
    /**
     * Get connection strength (inverse of distance, modulated by resonance)
     */
    public double getConnectionStrength(PhiNode other) {
        if (!isConnectedTo(other)) return 0.0;
        
        double distance = distanceTo(other);
        if (distance < 0.001) distance = 0.001; // Avoid division by zero
        
        // Strength = (resonance_this + resonance_other) / (2 * distance)
        double avgResonance = (this.resonance + other.resonance) / 2.0;
        return avgResonance / distance;
    }
    
    /**
     * Apply velocity to position
     */
    public void updatePosition(double deltaTime) {
        this.x += this.vx * deltaTime;
        this.y += this.vy * deltaTime;
        this.z += this.vz * deltaTime;
    }
    
    /**
     * Apply force to this node (F = ma, so a = F/m)
     */
    public void applyForce(double fx, double fy, double fz) {
        this.vx += fx / mass;
        this.vy += fy / mass;
        this.vz += fz / mass;
    }
    
    /**
     * Apply damping to velocity (friction)
     */
    public void applyDamping(double damping) {
        this.vx *= damping;
        this.vy *= damping;
        this.vz *= damping;
    }
    
    @Override
    public String toString() {
        String dataStr = data != null ? data.toString() : "null";
        if (dataStr.length() > 30) {
            dataStr = dataStr.substring(0, 27) + "...";
        }
        
        return String.format("PhiNode[id=%s, pos=(%.2f,%.2f,%.2f), resonance=%.3f, connections=%d, data=%s]",
            id.substring(0, 8), x, y, z, resonance, connections.size(), dataStr);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PhiNode)) return false;
        PhiNode other = (PhiNode) obj;
        return this.id.equals(other.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
