package fraymus.run;

import java.util.*;

/**
 * DrugDiscoveryEngine: Example scientific computing engine.
 * 
 * Simulates drug molecule optimization using physics-based modeling:
 * - Molecules have properties (binding, toxicity)
 * - Gravity organizes by similarity
 * - Fusion creates novel compounds
 * - Demonstrates reproducible drug design
 */
public class DrugDiscoveryEngine {
    
    private static class Molecule {
        double bindingAffinity;
        double toxicity;
        double x, y;  // Position in property space
        String compound;
        
        Molecule(double binding, double toxicity, String compound) {
            this.bindingAffinity = binding;
            this.toxicity = toxicity;
            this.compound = compound;
            this.x = binding;
            this.y = toxicity;
        }
        
        double distanceTo(Molecule other) {
            double dx = x - other.x;
            double dy = y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }
        
        double score() {
            // Higher binding, lower toxicity is better
            return bindingAffinity - toxicity;
        }
    }
    
    private final RunContext ctx;
    private final List<Molecule> molecules;
    
    public DrugDiscoveryEngine(RunContext ctx) {
        this.ctx = ctx;
        this.molecules = new ArrayList<>();
        
        // Initialize candidate molecules
        for (int i = 0; i < ctx.cfg.populationSize; i++) {
            molecules.add(new Molecule(
                ctx.nextDouble() * 100,  // Random binding affinity
                ctx.nextDouble() * 50,   // Random toxicity
                "MOL-" + i
            ));
        }
    }
    
    public void run() {
        ctx.log.header(Map.of(
            "engine", "DrugDiscovery",
            "description", "Molecular optimization via fusion",
            "goal", "Maximize binding, minimize toxicity"
        ));
        
        Molecule bestMolecule = null;
        int novelCompounds = 0;
        
        for (int step = 0; step < ctx.cfg.steps; step++) {
            // Physics simulation
            applyGravity();
            novelCompounds += synthesizeNovelCompounds();
            
            // Find best molecule
            bestMolecule = molecules.stream()
                .max(Comparator.comparingDouble(Molecule::score))
                .orElse(null);
            
            // Log metrics
            double avgBinding = molecules.stream()
                .mapToDouble(m -> m.bindingAffinity)
                .average().orElse(0);
            
            double avgToxicity = molecules.stream()
                .mapToDouble(m -> m.toxicity)
                .average().orElse(0);
            
            ctx.log.step(step, Map.of(
                "bestScore", bestMolecule != null ? bestMolecule.score() : 0,
                "bestBinding", bestMolecule != null ? bestMolecule.bindingAffinity : 0,
                "bestToxicity", bestMolecule != null ? bestMolecule.toxicity : 0,
                "avgBinding", avgBinding,
                "avgToxicity", avgToxicity,
                "novelCompounds", novelCompounds,
                "totalMolecules", molecules.size()
            ));
        }
        
        ctx.log.footer(Map.of(
            "bestCompound", bestMolecule != null ? bestMolecule.compound : "NONE",
            "finalScore", bestMolecule != null ? bestMolecule.score() : 0,
            "totalNovelCompounds", novelCompounds
        ));
    }
    
    private void applyGravity() {
        double g = ctx.cfg.gravityConstant;
        
        for (int i = 0; i < molecules.size(); i++) {
            Molecule m1 = molecules.get(i);
            double fx = 0, fy = 0;
            
            for (int j = 0; j < molecules.size(); j++) {
                if (i == j) continue;
                Molecule m2 = molecules.get(j);
                
                double dist = m1.distanceTo(m2);
                if (dist < 0.1) continue;
                
                // Gravity based on property similarity
                double force = g * (m1.score() * m2.score()) / (dist * dist);
                
                double dx = m2.x - m1.x;
                double dy = m2.y - m1.y;
                
                fx += force * dx / dist;
                fy += force * dy / dist;
            }
            
            // Update position in property space
            m1.x += fx * 0.01;
            m1.y += fy * 0.01;
            
            // Update properties from position
            m1.bindingAffinity = Math.max(0, Math.min(100, m1.x));
            m1.toxicity = Math.max(0, Math.min(50, m1.y));
        }
    }
    
    private int synthesizeNovelCompounds() {
        int synthesized = 0;
        List<Molecule> newMolecules = new ArrayList<>();
        
        for (int i = 0; i < molecules.size() && synthesized < 5; i++) {  // Limit synthesis
            for (int j = i + 1; j < molecules.size() && synthesized < 5; j++) {
                Molecule m1 = molecules.get(i);
                Molecule m2 = molecules.get(j);
                
                double dist = m1.distanceTo(m2);
                
                if (dist < ctx.cfg.fusionDistance) {
                    double combinedScore = m1.score() + m2.score();
                    
                    if (combinedScore > ctx.cfg.energyThreshold) {
                        // Synthesize novel compound
                        Molecule novel = new Molecule(
                            (m1.bindingAffinity + m2.bindingAffinity) / 2,
                            (m1.toxicity + m2.toxicity) / 2,
                            "NOVEL-" + synthesized
                        );
                        
                        newMolecules.add(novel);
                        synthesized++;
                    }
                }
            }
        }
        
        molecules.addAll(newMolecules);
        return synthesized;
    }
    
    public static void main(String[] args) {
        RunConfig cfg = RunConfig.builder()
            .seed(54321)
            .steps(100)
            .populationSize(30)
            .energyThreshold(60.0)
            .outDir("./runs/drugs")
            .build();
        
        RunContext ctx = RunContext.create(cfg, "drug-discovery-001");
        
        DrugDiscoveryEngine engine = new DrugDiscoveryEngine(ctx);
        engine.run();
        
        ctx.close();
    }
}
