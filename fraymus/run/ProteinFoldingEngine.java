package fraymus.run;

import java.util.*;

/**
 * ProteinFoldingEngine: Example scientific computing engine.
 * 
 * Simulates protein folding using physics-based modeling:
 * - Amino acids have positions and charges
 * - Gravity attracts/repels based on charge
 * - Fusion creates stable structures
 * - Demonstrates reproducible protein structure prediction
 */
public class ProteinFoldingEngine {
    
    private static class AminoAcid {
        double x, y, z;     // 3D position
        double charge;      // Electrical charge
        double hydrophobicity;
        int index;
        
        AminoAcid(int index, double x, double y, double z, double charge, double hydro) {
            this.index = index;
            this.x = x;
            this.y = y;
            this.z = z;
            this.charge = charge;
            this.hydrophobicity = hydro;
        }
        
        double distanceTo(AminoAcid other) {
            double dx = x - other.x;
            double dy = y - other.y;
            double dz = z - other.z;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
    }
    
    private final RunContext ctx;
    private final List<AminoAcid> chain;
    
    public ProteinFoldingEngine(RunContext ctx) {
        this.ctx = ctx;
        this.chain = new ArrayList<>();
        
        // Initialize amino acid chain
        for (int i = 0; i < ctx.cfg.populationSize; i++) {
            chain.add(new AminoAcid(
                i,
                i * 2.0,  // Initial linear arrangement
                0,
                0,
                ctx.nextDouble() * 2 - 1,  // Charge -1 to +1
                ctx.nextDouble()            // Hydrophobicity 0 to 1
            ));
        }
    }
    
    public void run() {
        ctx.log.header(Map.of(
            "engine", "ProteinFolding",
            "description", "3D protein structure prediction",
            "chainLength", chain.size()
        ));
        
        double minEnergy = Double.MAX_VALUE;
        int stableStructures = 0;
        
        for (int step = 0; step < ctx.cfg.steps; step++) {
            // Physics simulation
            applyForces();
            stableStructures += detectStableStructures();
            
            // Calculate total energy
            double energy = calculateTotalEnergy();
            if (energy < minEnergy) {
                minEnergy = energy;
            }
            
            // Calculate compactness (radius of gyration)
            double compactness = calculateCompactness();
            
            ctx.log.step(step, Map.of(
                "energy", energy,
                "minEnergy", minEnergy,
                "compactness", compactness,
                "stableStructures", stableStructures
            ));
        }
        
        ctx.log.footer(Map.of(
            "finalEnergy", minEnergy,
            "finalCompactness", calculateCompactness(),
            "totalStableStructures", stableStructures
        ));
    }
    
    private void applyForces() {
        double g = ctx.cfg.gravityConstant;
        
        for (int i = 0; i < chain.size(); i++) {
            AminoAcid a1 = chain.get(i);
            double fx = 0, fy = 0, fz = 0;
            
            for (int j = 0; j < chain.size(); j++) {
                if (Math.abs(i - j) <= 1) continue; // Skip neighbors in chain
                
                AminoAcid a2 = chain.get(j);
                double dist = a1.distanceTo(a2);
                if (dist < 0.1) continue;
                
                // Force based on charge interaction and hydrophobicity
                double chargeForce = g * (a1.charge * a2.charge) / (dist * dist);
                double hydroForce = g * (a1.hydrophobicity * a2.hydrophobicity) / (dist * dist);
                double totalForce = chargeForce + hydroForce;
                
                double dx = a2.x - a1.x;
                double dy = a2.y - a1.y;
                double dz = a2.z - a1.z;
                
                fx += totalForce * dx / dist;
                fy += totalForce * dy / dist;
                fz += totalForce * dz / dist;
            }
            
            // Update position
            a1.x += fx * 0.01;
            a1.y += fy * 0.01;
            a1.z += fz * 0.01;
            
            // Maintain chain connectivity (spring constraint)
            if (i > 0) {
                AminoAcid prev = chain.get(i - 1);
                double dist = a1.distanceTo(prev);
                double targetDist = 2.0;
                
                if (dist > targetDist) {
                    double dx = prev.x - a1.x;
                    double dy = prev.y - a1.y;
                    double dz = prev.z - a1.z;
                    double factor = (dist - targetDist) / dist * 0.5;
                    
                    a1.x += dx * factor;
                    a1.y += dy * factor;
                    a1.z += dz * factor;
                }
            }
        }
    }
    
    private int detectStableStructures() {
        int structures = 0;
        
        for (int i = 0; i < chain.size(); i++) {
            for (int j = i + 3; j < chain.size(); j++) {
                AminoAcid a1 = chain.get(i);
                AminoAcid a2 = chain.get(j);
                
                double dist = a1.distanceTo(a2);
                
                // Detect hydrogen bonds or stable contacts
                if (dist < ctx.cfg.fusionDistance) {
                    double interaction = a1.charge * a2.charge + 
                                       a1.hydrophobicity * a2.hydrophobicity;
                    
                    if (Math.abs(interaction) > 0.5) {
                        structures++;
                    }
                }
            }
        }
        
        return structures;
    }
    
    private double calculateTotalEnergy() {
        double energy = 0;
        
        for (int i = 0; i < chain.size(); i++) {
            for (int j = i + 1; j < chain.size(); j++) {
                AminoAcid a1 = chain.get(i);
                AminoAcid a2 = chain.get(j);
                
                double dist = a1.distanceTo(a2);
                if (dist < 0.1) continue;
                
                // Energy from charge repulsion/attraction
                energy += (a1.charge * a2.charge) / dist;
                
                // Energy from hydrophobic effect
                energy -= (a1.hydrophobicity * a2.hydrophobicity) / dist;
            }
        }
        
        return energy;
    }
    
    private double calculateCompactness() {
        // Radius of gyration
        double cx = 0, cy = 0, cz = 0;
        
        for (AminoAcid a : chain) {
            cx += a.x;
            cy += a.y;
            cz += a.z;
        }
        
        cx /= chain.size();
        cy /= chain.size();
        cz /= chain.size();
        
        double rg = 0;
        for (AminoAcid a : chain) {
            double dx = a.x - cx;
            double dy = a.y - cy;
            double dz = a.z - cz;
            rg += dx * dx + dy * dy + dz * dz;
        }
        
        return Math.sqrt(rg / chain.size());
    }
    
    public static void main(String[] args) {
        RunConfig cfg = RunConfig.builder()
            .seed(99999)
            .steps(500)
            .populationSize(50)
            .gravityConstant(2.0)
            .fusionDistance(3.0)
            .outDir("./runs/protein")
            .build();
        
        RunContext ctx = RunContext.create(cfg, "protein-folding-001");
        
        ProteinFoldingEngine engine = new ProteinFoldingEngine(ctx);
        engine.run();
        
        ctx.close();
    }
}
