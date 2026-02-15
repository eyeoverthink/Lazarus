package fraymus.physics;

import java.util.*;

/**
 * ⚡ FUSION REACTOR: Collision Handler & Event Emitter
 * 
 * When particles collide (get close enough), fusion events occur.
 * This can create new particles, trigger actions, or transform existing ones.
 * 
 * Critical Mass: Energy > 80
 * Fusion Distance: < 2.0 units
 */
public class FusionReactor {
    
    private final GravityEngine engine;
    private final List<FusionListener> listeners;
    
    // Fusion thresholds
    public static final double CRITICAL_MASS = 80.0;
    public static final double FUSION_DISTANCE = 2.0;
    
    // Statistics
    private long fusionCount = 0;
    private long lastFusionTick = 0;
    
    public FusionReactor(GravityEngine engine) {
        this.engine = engine;
        this.listeners = new ArrayList<>();
    }
    
    /**
     * Handle collision between two particles
     */
    public void handleCollision(PhiSuit<?> p1, PhiSuit<?> p2) {
        double combinedEnergy = p1.heat + p2.heat + p1.amplitude + p2.amplitude;
        
        // Check if fusion threshold met
        if (combinedEnergy >= CRITICAL_MASS) {
            performFusion(p1, p2, combinedEnergy);
        } else {
            // Simple collision (bounce)
            p1.onCollision(p2);
            p2.onCollision(p1);
        }
    }
    
    /**
     * FUSION EVENT: Merge particles and create something new
     */
    private void performFusion(PhiSuit<?> p1, PhiSuit<?> p2, double energy) {
        fusionCount++;
        lastFusionTick = engine.getTickCount();
        
        System.out.println("⚡ FUSION EVENT #" + fusionCount + ":");
        System.out.println("   " + p1.label + " + " + p2.label);
        System.out.println("   Energy: " + energy);
        
        // Trigger custom collision handlers
        p1.onCollision(p2);
        p2.onCollision(p1);
        
        // Notify listeners
        for (FusionListener listener : listeners) {
            listener.onFusion(p1, p2, energy);
        }
        
        // Heat spike from fusion
        p1.heat = Math.min(p1.heat + 20, 100);
        p2.heat = Math.min(p2.heat + 20, 100);
    }
    
    /**
     * Add a fusion event listener
     */
    public void addListener(FusionListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Get fusion statistics
     */
    public long getFusionCount() {
        return fusionCount;
    }
    
    public long getLastFusionTick() {
        return lastFusionTick;
    }
    
    /**
     * Interface for fusion event listeners
     */
    public interface FusionListener {
        void onFusion(PhiSuit<?> p1, PhiSuit<?> p2, double energy);
    }
}
