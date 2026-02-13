package fraymus.run;

import java.util.*;

/**
 * MolecularDynamicsEngine - Advanced molecular simulation
 * 
 * Simulates:
 * - Atomic interactions (Lennard-Jones potential)
 * - Temperature control (Nosé-Hoover thermostat)
 * - Pressure calculation (virial theorem)
 * - Phase transitions
 * 
 * Demonstrates RunContext usage for physics simulation
 */
public class MolecularDynamicsEngine {
    
    private static class Atom {
        double x, y, z;          // Position
        double vx, vy, vz;       // Velocity
        double fx, fy, fz;       // Force
        double mass;
        String element;
        
        Atom(double x, double y, double z, double mass, String element) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.mass = mass;
            this.element = element;
            this.vx = this.vy = this.vz = 0;
            this.fx = this.fy = this.fz = 0;
        }
    }
    
    public void run(RunContext ctx) {
        ctx.log.header(Map.of(
            "engine", "MolecularDynamics",
            "description", "Atomic-scale molecular simulation",
            "potential", "Lennard-Jones",
            "integrator", "Velocity Verlet",
            "thermostat", "Nosé-Hoover"
        ));
        
        // Initialize atoms in a cubic lattice
        List<Atom> atoms = initializeLattice(ctx);
        
        // Simulation parameters
        double dt = 0.001;              // Time step (ps)
        double temperature = 300.0;     // Target temperature (K)
        double sigma = 3.4;             // LJ parameter (Angstrom)
        double epsilon = 0.996;         // LJ parameter (kJ/mol)
        
        // Nosé-Hoover thermostat
        double Q = 1000.0;              // Thermostat mass
        double xi = 0.0;                // Thermostat variable
        
        for (int step = 0; step < ctx.cfg.steps; step++) {
            // Calculate forces (Lennard-Jones)
            calculateForces(atoms, sigma, epsilon);
            
            // Velocity Verlet integration (first half)
            for (Atom atom : atoms) {
                atom.vx += 0.5 * dt * atom.fx / atom.mass;
                atom.vy += 0.5 * dt * atom.fy / atom.mass;
                atom.vz += 0.5 * dt * atom.fz / atom.mass;
                
                atom.x += dt * atom.vx;
                atom.y += dt * atom.vy;
                atom.z += dt * atom.vz;
                
                // Periodic boundary conditions
                atom.x = atom.x - Math.floor(atom.x / 10.0) * 10.0;
                atom.y = atom.y - Math.floor(atom.y / 10.0) * 10.0;
                atom.z = atom.z - Math.floor(atom.z / 10.0) * 10.0;
            }
            
            // Recalculate forces
            calculateForces(atoms, sigma, epsilon);
            
            // Velocity Verlet integration (second half)
            for (Atom atom : atoms) {
                atom.vx += 0.5 * dt * atom.fx / atom.mass;
                atom.vy += 0.5 * dt * atom.fy / atom.mass;
                atom.vz += 0.5 * dt * atom.fz / atom.mass;
            }
            
            // Nosé-Hoover thermostat
            double kineticEnergy = calculateKineticEnergy(atoms);
            double currentTemp = (2.0 / 3.0) * kineticEnergy / (atoms.size() * 8.314e-3);
            
            xi += dt * (currentTemp - temperature) / Q;
            
            for (Atom atom : atoms) {
                atom.vx *= (1.0 - 0.5 * dt * xi);
                atom.vy *= (1.0 - 0.5 * dt * xi);
                atom.vz *= (1.0 - 0.5 * dt * xi);
            }
            
            // Calculate observables
            double potentialEnergy = calculatePotentialEnergy(atoms, sigma, epsilon);
            double totalEnergy = kineticEnergy + potentialEnergy;
            double pressure = calculatePressure(atoms, sigma, epsilon, currentTemp);
            
            // Log every 10 steps
            if (step % 10 == 0) {
                ctx.log.step(step, Map.of(
                    "temperature", currentTemp,
                    "kineticEnergy", kineticEnergy,
                    "potentialEnergy", potentialEnergy,
                    "totalEnergy", totalEnergy,
                    "pressure", pressure,
                    "thermostatXi", xi
                ));
                
                if (ctx.cfg.prettyConsole) {
                    System.out.printf("Step %4d: T=%.2fK, E_kin=%.2f, E_pot=%.2f, P=%.2f atm\n",
                        step, currentTemp, kineticEnergy, potentialEnergy, pressure);
                }
            }
        }
        
        // Final statistics
        double finalKE = calculateKineticEnergy(atoms);
        double finalPE = calculatePotentialEnergy(atoms, sigma, epsilon);
        double finalTemp = (2.0 / 3.0) * finalKE / (atoms.size() * 8.314e-3);
        
        ctx.log.footer(Map.of(
            "finalTemperature", finalTemp,
            "finalKineticEnergy", finalKE,
            "finalPotentialEnergy", finalPE,
            "totalAtoms", atoms.size()
        ));
        
        if (ctx.cfg.prettyConsole) {
            System.out.println("\n=== Molecular Dynamics Complete ===");
            System.out.printf("Final Temperature: %.2f K\n", finalTemp);
            System.out.printf("Final Energy: %.2f kJ/mol\n", finalKE + finalPE);
            System.out.printf("Atoms simulated: %d\n", atoms.size());
        }
    }
    
    private List<Atom> initializeLattice(RunContext ctx) {
        List<Atom> atoms = new ArrayList<>();
        
        int nx = (int) Math.ceil(Math.cbrt(ctx.cfg.populationSize));
        double spacing = 3.0; // Angstrom
        
        for (int ix = 0; ix < nx; ix++) {
            for (int iy = 0; iy < nx; iy++) {
                for (int iz = 0; iz < nx; iz++) {
                    if (atoms.size() >= ctx.cfg.populationSize) break;
                    
                    double x = ix * spacing;
                    double y = iy * spacing;
                    double z = iz * spacing;
                    
                    atoms.add(new Atom(x, y, z, 39.948, "Ar")); // Argon
                    
                    // Random initial velocity (Maxwell-Boltzmann)
                    Atom atom = atoms.get(atoms.size() - 1);
                    atom.vx = ctx.nextGaussian() * 0.1;
                    atom.vy = ctx.nextGaussian() * 0.1;
                    atom.vz = ctx.nextGaussian() * 0.1;
                }
            }
        }
        
        // Remove center of mass motion
        double vxCom = atoms.stream().mapToDouble(a -> a.vx).average().orElse(0);
        double vyCom = atoms.stream().mapToDouble(a -> a.vy).average().orElse(0);
        double vzCom = atoms.stream().mapToDouble(a -> a.vz).average().orElse(0);
        
        for (Atom atom : atoms) {
            atom.vx -= vxCom;
            atom.vy -= vyCom;
            atom.vz -= vzCom;
        }
        
        return atoms;
    }
    
    private void calculateForces(List<Atom> atoms, double sigma, double epsilon) {
        // Reset forces
        for (Atom atom : atoms) {
            atom.fx = atom.fy = atom.fz = 0;
        }
        
        // Lennard-Jones potential: V(r) = 4ε[(σ/r)^12 - (σ/r)^6]
        // Force: F = -dV/dr
        
        for (int i = 0; i < atoms.size(); i++) {
            Atom a1 = atoms.get(i);
            
            for (int j = i + 1; j < atoms.size(); j++) {
                Atom a2 = atoms.get(j);
                
                double dx = a1.x - a2.x;
                double dy = a1.y - a2.y;
                double dz = a1.z - a2.z;
                
                // Minimum image convention
                dx -= 10.0 * Math.round(dx / 10.0);
                dy -= 10.0 * Math.round(dy / 10.0);
                dz -= 10.0 * Math.round(dz / 10.0);
                
                double r2 = dx * dx + dy * dy + dz * dz;
                double r = Math.sqrt(r2);
                
                if (r < 10.0) { // Cutoff
                    double sr2 = (sigma * sigma) / r2;
                    double sr6 = sr2 * sr2 * sr2;
                    double sr12 = sr6 * sr6;
                    
                    double force = 24.0 * epsilon * (2.0 * sr12 - sr6) / r2;
                    
                    double fx = force * dx;
                    double fy = force * dy;
                    double fz = force * dz;
                    
                    a1.fx += fx;
                    a1.fy += fy;
                    a1.fz += fz;
                    
                    a2.fx -= fx;
                    a2.fy -= fy;
                    a2.fz -= fz;
                }
            }
        }
    }
    
    private double calculateKineticEnergy(List<Atom> atoms) {
        double ke = 0;
        for (Atom atom : atoms) {
            double v2 = atom.vx * atom.vx + atom.vy * atom.vy + atom.vz * atom.vz;
            ke += 0.5 * atom.mass * v2;
        }
        return ke;
    }
    
    private double calculatePotentialEnergy(List<Atom> atoms, double sigma, double epsilon) {
        double pe = 0;
        
        for (int i = 0; i < atoms.size(); i++) {
            Atom a1 = atoms.get(i);
            
            for (int j = i + 1; j < atoms.size(); j++) {
                Atom a2 = atoms.get(j);
                
                double dx = a1.x - a2.x;
                double dy = a1.y - a2.y;
                double dz = a1.z - a2.z;
                
                dx -= 10.0 * Math.round(dx / 10.0);
                dy -= 10.0 * Math.round(dy / 10.0);
                dz -= 10.0 * Math.round(dz / 10.0);
                
                double r2 = dx * dx + dy * dy + dz * dz;
                double r = Math.sqrt(r2);
                
                if (r < 10.0) {
                    double sr = sigma / r;
                    double sr6 = Math.pow(sr, 6);
                    double sr12 = sr6 * sr6;
                    
                    pe += 4.0 * epsilon * (sr12 - sr6);
                }
            }
        }
        
        return pe;
    }
    
    private double calculatePressure(List<Atom> atoms, double sigma, double epsilon, double temp) {
        double virial = 0;
        
        for (int i = 0; i < atoms.size(); i++) {
            Atom a1 = atoms.get(i);
            
            for (int j = i + 1; j < atoms.size(); j++) {
                Atom a2 = atoms.get(j);
                
                double dx = a1.x - a2.x;
                double dy = a1.y - a2.y;
                double dz = a1.z - a2.z;
                
                dx -= 10.0 * Math.round(dx / 10.0);
                dy -= 10.0 * Math.round(dy / 10.0);
                dz -= 10.0 * Math.round(dz / 10.0);
                
                double r2 = dx * dx + dy * dy + dz * dz;
                double r = Math.sqrt(r2);
                
                if (r < 10.0) {
                    double sr2 = (sigma * sigma) / r2;
                    double sr6 = sr2 * sr2 * sr2;
                    double sr12 = sr6 * sr6;
                    
                    double force = 24.0 * epsilon * (2.0 * sr12 - sr6) / r;
                    virial += force * r;
                }
            }
        }
        
        double volume = 10.0 * 10.0 * 10.0; // Cubic box
        double pressure = (atoms.size() * 8.314e-3 * temp + virial / 3.0) / volume;
        
        return pressure * 16.6054; // Convert to atm
    }
    
    public static void main(String[] args) {
        RunConfig cfg = RunConfig.builder()
            .seed(42)
            .steps(1000)
            .populationSize(64) // 64 argon atoms
            .prettyConsole(true)
            .jsonl(true)
            .outDir("runs/molecular")
            .build();
        
        try (RunContext ctx = RunContext.create(cfg, "molecular-dynamics-001")) {
            new MolecularDynamicsEngine().run(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
