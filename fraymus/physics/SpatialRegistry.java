package fraymus.physics;

import java.util.*;

/**
 * 📍 SPATIAL REGISTRY: Universe Tracking System
 * 
 * Keeps track of all particles and their relationships.
 * Provides spatial queries and statistics.
 */
public class SpatialRegistry {
    
    private final Map<String, PhiSuit<?>> particleMap;
    private final GravityEngine engine;
    
    public SpatialRegistry(GravityEngine engine) {
        this.engine = engine;
        this.particleMap = new HashMap<>();
    }
    
    /**
     * Register a particle
     */
    public void register(PhiSuit<?> particle) {
        particleMap.put(particle.label, particle);
    }
    
    /**
     * Find particle by label
     */
    public PhiSuit<?> find(String label) {
        return particleMap.get(label);
    }
    
    /**
     * Find all particles within radius of a point
     */
    public List<PhiSuit<?>> findNearby(double x, double y, double z, double radius) {
        List<PhiSuit<?>> nearby = new ArrayList<>();
        
        for (PhiSuit<?> p : particleMap.values()) {
            double dx = p.x - x;
            double dy = p.y - y;
            double dz = p.z - z;
            double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
            
            if (dist <= radius) {
                nearby.add(p);
            }
        }
        
        return nearby;
    }
    
    /**
     * Get statistics
     */
    public int getParticleCount() {
        return particleMap.size();
    }
    
    /**
     * Get total energy in system
     */
    public double getTotalEnergy() {
        return particleMap.values().stream()
            .mapToDouble(p -> p.heat + p.amplitude)
            .sum();
    }
    
    /**
     * Print ASCII map of particle positions (2D projection)
     */
    public void printMap(int width, int height) {
        char[][] grid = new char[height][width];
        
        // Initialize grid
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = '.';
            }
        }
        
        // Plot particles
        for (PhiSuit<?> p : particleMap.values()) {
            int px = (int) (p.x % width);
            int py = (int) (p.y % height);
            
            if (px >= 0 && px < width && py >= 0 && py < height) {
                // Character based on amplitude
                if (p.amplitude > 80) grid[py][px] = '█';
                else if (p.amplitude > 50) grid[py][px] = '▓';
                else grid[py][px] = '░';
            }
        }
        
        // Print
        System.out.println("\n╔" + "═".repeat(width) + "╗");
        for (char[] row : grid) {
            System.out.println("║" + new String(row) + "║");
        }
        System.out.println("╚" + "═".repeat(width) + "╝\n");
    }
}
