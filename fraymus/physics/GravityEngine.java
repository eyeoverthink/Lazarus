package fraymus.physics;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ⚛️ GRAVITY ENGINE: Hebbian Physics Simulator
 * 
 * This is the universe. All particles exist here.
 * Gravity pulls related concepts together.
 * Collisions trigger fusion events.
 * 
 * Based on φ-harmonic mathematics where F = φ × (A₁ × A₂) / d²
 */
public class GravityEngine {
    
    // All particles in the universe
    private final List<PhiSuit<?>> particles;
    
    // Fusion detector
    private final FusionReactor fusionReactor;
    
    // Physics constants
    public static final double FUSION_DISTANCE = 2.0;  // Collision threshold
    public static final double PHI = 1.618033988749895; // Golden ratio
    
    // State
    private long tickCount = 0;
    private boolean running = false;
    
    /**
     * Create a new universe
     */
    public GravityEngine() {
        this.particles = new CopyOnWriteArrayList<>();
        this.fusionReactor = new FusionReactor(this);
    }
    
    /**
     * Add a particle to the universe
     */
    public void register(PhiSuit<?> particle) {
        particles.add(particle);
        System.out.println("⭐ PARTICLE REGISTERED: " + particle.label);
    }
    
    /**
     * Remove a particle
     */
    public void unregister(PhiSuit<?> particle) {
        particles.remove(particle);
    }
    
    /**
     * Get all particles
     */
    public List<PhiSuit<?>> getParticles() {
        return new ArrayList<>(particles);
    }
    
    /**
     * PHYSICS TICK: One step of simulation
     * 
     * 1. Calculate gravity between all particles
     * 2. Apply forces (movement)
     * 3. Detect collisions
     * 4. Trigger fusion events
     * 5. Decay energy
     */
    public void tick() {
        tickCount++;
        
        // 1. Calculate and apply gravitational forces
        for (PhiSuit<?> p1 : particles) {
            if (!p1.active) continue;
            
            double totalFx = 0, totalFy = 0, totalFz = 0;
            
            for (PhiSuit<?> p2 : particles) {
                if (p1 == p2 || !p2.active) continue;
                
                // Calculate Hebbian gravity
                double force = calculateGravity(p1, p2);
                
                // Direction vector (normalized)
                double dx = p2.x - p1.x;
                double dy = p2.y - p1.y;
                double dz = p2.z - p1.z;
                double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
                
                if (dist > 0.1) {
                    // Normalize and scale by force
                    totalFx += (dx / dist) * force;
                    totalFy += (dy / dist) * force;
                    totalFz += (dz / dist) * force;
                }
            }
            
            // Apply force to particle
            p1.applyForce(totalFx, totalFy, totalFz);
        }
        
        // 2. Detect collisions and trigger fusion
        for (int i = 0; i < particles.size(); i++) {
            PhiSuit<?> p1 = particles.get(i);
            if (!p1.active) continue;
            
            for (int j = i + 1; j < particles.size(); j++) {
                PhiSuit<?> p2 = particles.get(j);
                if (!p2.active) continue;
                
                double distance = p1.distanceTo(p2);
                
                if (distance < FUSION_DISTANCE) {
                    // COLLISION DETECTED!
                    fusionReactor.handleCollision(p1, p2);
                }
            }
        }
        
        // 3. Cool down all particles (entropy decay)
        for (PhiSuit<?> p : particles) {
            p.coolDown();
        }
    }
    
    /**
     * Calculate Hebbian gravity between two particles
     * F = φ × (A₁ × A₂) / d²
     */
    private double calculateGravity(PhiSuit<?> p1, PhiSuit<?> p2) {
        double distance = p1.distanceTo(p2);
        if (distance < 0.1) distance = 0.1; // Prevent singularity
        
        return PHI * (p1.amplitude * p2.amplitude) / (distance * distance);
    }
    
    /**
     * Run the physics loop continuously
     */
    public void start() {
        running = true;
        System.out.println("🌌 GRAVITY ENGINE STARTED");
        
        new Thread(() -> {
            while (running) {
                tick();
                try {
                    Thread.sleep(50); // 20 ticks per second
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }
    
    /**
     * Stop the physics loop
     */
    public void stop() {
        running = false;
        System.out.println("🌌 GRAVITY ENGINE STOPPED");
    }
    
    /**
     * Get current simulation time
     */
    public long getTickCount() {
        return tickCount;
    }
    
    /**
     * Print universe state
     */
    public void printState() {
        System.out.println("\n═══ UNIVERSE STATE (Tick " + tickCount + ") ═══");
        System.out.println("Active Particles: " + particles.size());
        for (PhiSuit<?> p : particles) {
            System.out.println("  " + p);
        }
        System.out.println("═══════════════════════════════\n");
    }
}
