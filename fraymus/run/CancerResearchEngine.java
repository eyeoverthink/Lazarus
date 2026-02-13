package fraymus.run;

import java.util.*;

/**
 * CancerResearchEngine: Example scientific computing engine.
 * 
 * Simulates cancer cell evolution using physics-based modeling:
 * - Cells have energy and position
 * - Gravity attracts similar cells
 * - Fusion creates resistant mutations
 * - Demonstrates reproducible, logged computation
 * 
 * This is a demonstration of how to use RunContext for scientific computing.
 */
public class CancerResearchEngine {
    
    private static class Cell {
        double x, y;           // Position
        double energy;         // Energy level
        boolean resistant;     // Drug resistance
        
        Cell(double x, double y, double energy) {
            this.x = x;
            this.y = y;
            this.energy = energy;
            this.resistant = false;
        }
        
        double distanceTo(Cell other) {
            double dx = x - other.x;
            double dy = y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }
    }
    
    private final RunContext ctx;
    private final List<Cell> cells;
    
    public CancerResearchEngine(RunContext ctx) {
        this.ctx = ctx;
        this.cells = new ArrayList<>();
        
        // Initialize population
        for (int i = 0; i < ctx.cfg.populationSize; i++) {
            cells.add(new Cell(
                ctx.nextDouble() * 100,  // Random position
                ctx.nextDouble() * 100,
                ctx.nextDouble() * 100   // Random energy
            ));
        }
    }
    
    public void run() {
        ctx.log.header(Map.of(
            "engine", "CancerResearch",
            "description", "Cancer cell evolution simulation"
        ));
        
        int resistantCells = 0;
        int fusionEvents = 0;
        
        for (int step = 0; step < ctx.cfg.steps; step++) {
            // Simulate physics
            applyGravity();
            fusionEvents += detectFusion();
            
            // Count resistant cells
            resistantCells = (int) cells.stream().filter(c -> c.resistant).count();
            
            // Log metrics
            double avgEnergy = cells.stream().mapToDouble(c -> c.energy).average().orElse(0);
            
            ctx.log.step(step, Map.of(
                "resistantCells", resistantCells,
                "fusionEvents", fusionEvents,
                "avgEnergy", avgEnergy,
                "totalCells", cells.size()
            ));
        }
        
        ctx.log.footer(Map.of(
            "finalResistantCells", resistantCells,
            "totalFusionEvents", fusionEvents,
            "resistanceRate", resistantCells / (double) cells.size()
        ));
    }
    
    private void applyGravity() {
        double g = ctx.cfg.gravityConstant;
        
        for (int i = 0; i < cells.size(); i++) {
            Cell c1 = cells.get(i);
            double fx = 0, fy = 0;
            
            for (int j = 0; j < cells.size(); j++) {
                if (i == j) continue;
                Cell c2 = cells.get(j);
                
                double dist = c1.distanceTo(c2);
                if (dist < 0.1) continue; // Avoid division by zero
                
                // Hebbian gravity: F = g × (E1 × E2) / d²
                double force = g * (c1.energy * c2.energy) / (dist * dist);
                
                double dx = c2.x - c1.x;
                double dy = c2.y - c1.y;
                
                fx += force * dx / dist;
                fy += force * dy / dist;
            }
            
            // Update position
            c1.x += fx * 0.01;
            c1.y += fy * 0.01;
            
            // Keep in bounds
            c1.x = Math.max(0, Math.min(100, c1.x));
            c1.y = Math.max(0, Math.min(100, c1.y));
        }
    }
    
    private int detectFusion() {
        int fusions = 0;
        List<Cell> newCells = new ArrayList<>();
        
        for (int i = 0; i < cells.size() && fusions < 10; i++) {  // Limit fusions per step
            for (int j = i + 1; j < cells.size() && fusions < 10; j++) {
                Cell c1 = cells.get(i);
                Cell c2 = cells.get(j);
                
                double dist = c1.distanceTo(c2);
                
                if (dist < ctx.cfg.fusionDistance && 
                    c1.energy + c2.energy > ctx.cfg.energyThreshold) {
                    
                    // Fusion: Create resistant mutation
                    Cell mutant = new Cell(
                        (c1.x + c2.x) / 2,
                        (c1.y + c2.y) / 2,
                        (c1.energy + c2.energy) / 2
                    );
                    mutant.resistant = true;
                    
                    newCells.add(mutant);
                    fusions++;
                }
            }
        }
        
        cells.addAll(newCells);
        return fusions;
    }
    
    public static void main(String[] args) {
        RunConfig cfg = RunConfig.builder()
            .seed(12345)
            .steps(100)
            .populationSize(50)
            .outDir("./runs/cancer")
            .build();
        
        RunContext ctx = RunContext.create(cfg, "cancer-research-001");
        
        CancerResearchEngine engine = new CancerResearchEngine(ctx);
        engine.run();
        
        ctx.close();
    }
}
