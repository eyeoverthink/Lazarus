package fraymus.physics;

/**
 * 🌟 PHI-SUIT: Universal Particle Wrapper
 * 
 * Everything in Fraynix is a particle wrapped in φ-based properties.
 * This is the fundamental unit of existence in the physics engine.
 * 
 * @param <T> The data payload this particle carries
 */
public class PhiSuit<T> {
    
    // Identity
    public String label;
    public T data;
    
    // Position in 3D space
    public double x, y, z;
    
    // Physics properties
    public double amplitude = 50.0;  // Mass/Importance (0-100)
    public double heat = 0.0;         // Energy/Activity level
    public double velocity = 0.0;     // Speed of movement
    
    // State
    protected boolean active = true;
    
    // φ constant (golden ratio)
    public static final double PHI = 1.618033988749895;
    
    /**
     * Create a new particle with data and position
     */
    public PhiSuit(T data, double x, double y, double z) {
        this.data = data;
        this.x = x;
        this.y = y;
        this.z = z;
        this.label = "PARTICLE_" + System.currentTimeMillis();
    }
    
    /**
     * Create a particle at origin
     */
    public PhiSuit(T data) {
        this(data, 0, 0, 0);
    }
    
    /**
     * Calculate distance to another particle
     */
    public double distanceTo(PhiSuit<?> other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
    /**
     * COLLISION HANDLER: Override this for custom fusion logic
     */
    public void onCollision(PhiSuit<?> other) {
        // Default: Transfer some energy
        double energyTransfer = Math.min(this.heat, 10.0);
        this.heat -= energyTransfer;
        other.heat += energyTransfer;
    }
    
    /**
     * Calculate φ-harmonic resonance with another particle
     */
    public double resonanceWith(PhiSuit<?> other) {
        double dist = distanceTo(other);
        if (dist < 0.1) dist = 0.1; // Prevent division by zero
        
        // Hebbian Gravity: F = φ × (A₁ × A₂) / d²
        return PHI * (this.amplitude * other.amplitude) / (dist * dist);
    }
    
    /**
     * Move particle based on force vector
     */
    public void applyForce(double fx, double fy, double fz) {
        // Simplified physics: direct position update
        double damping = 0.1; // Movement speed
        this.x += fx * damping;
        this.y += fy * damping;
        this.z += fz * damping;
    }
    
    /**
     * Decay heat over time (entropy)
     */
    public void coolDown() {
        this.heat *= 0.95; // 5% decay per tick
    }
    
    @Override
    public String toString() {
        return String.format("%s [%.1f, %.1f, %.1f] A=%.1f H=%.1f", 
            label, x, y, z, amplitude, heat);
    }
}
