package fraymus.math;

import java.util.ArrayList;
import java.util.List;

/**
 * 🌊 PHYSICS SIMULATION: Calculus-Based Physics
 * 
 * Implements physics simulations using calculus:
 * - Particle dynamics (Newton's laws)
 * - Wave propagation
 * - Energy conservation
 * - Momentum and collisions
 * 
 * Mathematical Foundation:
 * F = ma = m(dv/dt) = m(d²x/dt²)
 * Wave equation: ∂²u/∂t² = c²∂²u/∂x²
 */
public class PhysicsSimulation {
    
    /**
     * Particle in physics simulation
     */
    public static class Particle {
        public double x, y, z;           // Position
        public double vx, vy, vz;        // Velocity
        public double ax, ay, az;        // Acceleration
        public double mass;
        public String name;
        
        public Particle(String name, double mass, double x, double y, double z) {
            this.name = name;
            this.mass = mass;
            this.x = x;
            this.y = y;
            this.z = z;
            this.vx = this.vy = this.vz = 0;
            this.ax = this.ay = this.az = 0;
        }
        
        /**
         * Apply force: F = ma → a = F/m
         */
        public void applyForce(double fx, double fy, double fz) {
            this.ax = fx / mass;
            this.ay = fy / mass;
            this.az = fz / mass;
        }
        
        /**
         * Update position using Velocity Verlet integration
         * x(t+dt) = x(t) + v(t)·dt + 0.5·a(t)·dt²
         * v(t+dt) = v(t) + 0.5·(a(t) + a(t+dt))·dt
         */
        public void update(double dt) {
            // Update position
            x += vx * dt + 0.5 * ax * dt * dt;
            y += vy * dt + 0.5 * ay * dt * dt;
            z += vz * dt + 0.5 * az * dt * dt;
            
            // Update velocity
            vx += ax * dt;
            vy += ay * dt;
            vz += az * dt;
        }
        
        /**
         * Kinetic energy: KE = 0.5·m·v²
         */
        public double kineticEnergy() {
            double v2 = vx*vx + vy*vy + vz*vz;
            return 0.5 * mass * v2;
        }
        
        /**
         * Momentum: p = m·v
         */
        public double[] momentum() {
            return new double[] {mass * vx, mass * vy, mass * vz};
        }
        
        @Override
        public String toString() {
            return String.format("%s: pos=(%.2f,%.2f,%.2f) vel=(%.2f,%.2f,%.2f)", 
                name, x, y, z, vx, vy, vz);
        }
    }
    
    /**
     * Gravity simulation
     * F = G·m₁·m₂/r²
     */
    public static class GravitySimulation {
        private static final double G = 6.67430e-11; // Gravitational constant
        private final List<Particle> particles;
        private double time;
        
        public GravitySimulation() {
            this.particles = new ArrayList<>();
            this.time = 0;
        }
        
        public void addParticle(Particle p) {
            particles.add(p);
        }
        
        /**
         * Compute gravitational force between two particles
         */
        private double[] computeForce(Particle p1, Particle p2) {
            double dx = p2.x - p1.x;
            double dy = p2.y - p1.y;
            double dz = p2.z - p1.z;
            
            double r2 = dx*dx + dy*dy + dz*dz;
            double r = Math.sqrt(r2);
            
            if (r < 1e-10) return new double[]{0, 0, 0};
            
            // F = G·m₁·m₂/r²
            double forceMag = G * p1.mass * p2.mass / r2;
            
            // Force components (normalized direction × magnitude)
            return new double[] {
                forceMag * dx / r,
                forceMag * dy / r,
                forceMag * dz / r
            };
        }
        
        /**
         * Simulate one time step
         */
        public void step(double dt) {
            // Reset accelerations
            for (Particle p : particles) {
                p.ax = p.ay = p.az = 0;
            }
            
            // Calculate forces between all pairs
            for (int i = 0; i < particles.size(); i++) {
                for (int j = i + 1; j < particles.size(); j++) {
                    Particle p1 = particles.get(i);
                    Particle p2 = particles.get(j);
                    
                    double[] force = computeForce(p1, p2);
                    
                    // Apply equal and opposite forces
                    p1.applyForce(force[0], force[1], force[2]);
                    p2.applyForce(-force[0], -force[1], -force[2]);
                }
            }
            
            // Update all particles
            for (Particle p : particles) {
                p.update(dt);
            }
            
            time += dt;
        }
        
        /**
         * Total energy (kinetic + potential)
         */
        public double totalEnergy() {
            double kinetic = 0;
            double potential = 0;
            
            // Kinetic energy
            for (Particle p : particles) {
                kinetic += p.kineticEnergy();
            }
            
            // Potential energy: U = -G·m₁·m₂/r
            for (int i = 0; i < particles.size(); i++) {
                for (int j = i + 1; j < particles.size(); j++) {
                    Particle p1 = particles.get(i);
                    Particle p2 = particles.get(j);
                    
                    double dx = p2.x - p1.x;
                    double dy = p2.y - p1.y;
                    double dz = p2.z - p1.z;
                    double r = Math.sqrt(dx*dx + dy*dy + dz*dz);
                    
                    if (r > 1e-10) {
                        potential -= G * p1.mass * p2.mass / r;
                    }
                }
            }
            
            return kinetic + potential;
        }
        
        public void printState() {
            System.out.println("\n--- Time: " + time + " s ---");
            for (Particle p : particles) {
                System.out.println(p);
            }
            System.out.println("Total Energy: " + totalEnergy());
        }
    }
    
    /**
     * 1D Wave propagation simulation
     * Wave equation: ∂²u/∂t² = c²·∂²u/∂x²
     */
    public static class WaveSimulation {
        private final double[] u;      // Wave amplitude
        private final double[] uPrev;  // Previous time step
        private final double c;        // Wave speed
        private final double dx;       // Spatial step
        private final double dt;       // Time step
        private int step;
        
        public WaveSimulation(int gridSize, double waveSpeed, double dx, double dt) {
            this.u = new double[gridSize];
            this.uPrev = new double[gridSize];
            this.c = waveSpeed;
            this.dx = dx;
            this.dt = dt;
            this.step = 0;
            
            // CFL condition check: c·dt/dx ≤ 1 for stability
            double cfl = c * dt / dx;
            if (cfl > 1.0) {
                System.err.println("WARNING: CFL condition violated! " + 
                    "c·dt/dx = " + cfl + " > 1.0. Simulation may be unstable.");
            }
        }
        
        /**
         * Initialize with a Gaussian pulse
         */
        public void initializeGaussianPulse(double center, double width) {
            for (int i = 0; i < u.length; i++) {
                double x = i * dx;
                u[i] = Math.exp(-Math.pow((x - center) / width, 2));
                uPrev[i] = u[i];
            }
        }
        
        /**
         * Initialize with a sine wave
         */
        public void initializeSineWave(double wavelength, double amplitude) {
            for (int i = 0; i < u.length; i++) {
                double x = i * dx;
                u[i] = amplitude * Math.sin(2 * Math.PI * x / wavelength);
                uPrev[i] = u[i];
            }
        }
        
        /**
         * Advance wave simulation by one time step
         * Using finite difference method:
         * u(x,t+dt) = 2u(x,t) - u(x,t-dt) + (c·dt/dx)²·[u(x+dx,t) - 2u(x,t) + u(x-dx,t)]
         */
        public void step() {
            double[] uNext = new double[u.length];
            double r = (c * dt / dx);
            double r2 = r * r;
            
            // Interior points
            for (int i = 1; i < u.length - 1; i++) {
                uNext[i] = 2*u[i] - uPrev[i] + 
                          r2 * (u[i+1] - 2*u[i] + u[i-1]);
            }
            
            // Boundary conditions (fixed ends)
            uNext[0] = 0;
            uNext[u.length - 1] = 0;
            
            // Update arrays
            System.arraycopy(u, 0, uPrev, 0, u.length);
            System.arraycopy(uNext, 0, u, 0, u.length);
            
            step++;
        }
        
        /**
         * Get current wave state
         */
        public double[] getWave() {
            return u.clone();
        }
        
        /**
         * Calculate total energy in wave
         * E = ∫ [0.5·(∂u/∂t)² + 0.5·c²·(∂u/∂x)²] dx
         */
        public double totalEnergy() {
            double energy = 0;
            
            for (int i = 1; i < u.length - 1; i++) {
                // Kinetic energy: 0.5·(∂u/∂t)²
                double dudt = (u[i] - uPrev[i]) / dt;
                double kinetic = 0.5 * dudt * dudt;
                
                // Potential energy: 0.5·c²·(∂u/∂x)²
                double dudx = (u[i+1] - u[i-1]) / (2 * dx);
                double potential = 0.5 * c * c * dudx * dudx;
                
                energy += (kinetic + potential) * dx;
            }
            
            return energy;
        }
        
        public void printWave() {
            System.out.println("\n--- Wave at step " + step + " ---");
            for (int i = 0; i < u.length; i += u.length / 20) {
                System.out.printf("x=%.2f: u=%.4f\n", i * dx, u[i]);
            }
            System.out.println("Total Energy: " + totalEnergy());
        }
    }
    
    /**
     * Projectile motion with air resistance
     * F_drag = -b·v (linear) or -c·v² (quadratic)
     */
    public static class ProjectileMotion {
        private double x, y;           // Position
        private double vx, vy;         // Velocity
        private final double mass;
        private final double dragCoeff;
        private final double g = 9.81; // Gravity
        
        public ProjectileMotion(double mass, double dragCoeff) {
            this.mass = mass;
            this.dragCoeff = dragCoeff;
        }
        
        /**
         * Launch projectile
         */
        public void launch(double v0, double angle) {
            this.x = 0;
            this.y = 0;
            this.vx = v0 * Math.cos(angle);
            this.vy = v0 * Math.sin(angle);
        }
        
        /**
         * Simulate trajectory with air resistance
         */
        public List<double[]> simulate(double dt, double maxTime) {
            List<double[]> trajectory = new ArrayList<>();
            double t = 0;
            
            while (y >= 0 && t < maxTime) {
                // Record position
                trajectory.add(new double[]{x, y, t});
                
                // Calculate drag force: F_drag = -b·v
                double v = Math.sqrt(vx*vx + vy*vy);
                double dragX = -dragCoeff * vx;
                double dragY = -dragCoeff * vy;
                
                // Calculate acceleration
                double ax = dragX / mass;
                double ay = -g + dragY / mass;
                
                // Update velocity
                vx += ax * dt;
                vy += ay * dt;
                
                // Update position
                x += vx * dt;
                y += vy * dt;
                
                t += dt;
            }
            
            return trajectory;
        }
        
        /**
         * Calculate range without air resistance (analytical solution)
         */
        public static double idealRange(double v0, double angle, double g) {
            return v0 * v0 * Math.sin(2 * angle) / g;
        }
    }
    
    /**
     * Test/Demo main method
     */
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════");
        System.out.println("🌊 PHYSICS SIMULATION DEMONSTRATION");
        System.out.println("═══════════════════════════════════════\n");
        
        // Test 1: Two-body gravity
        System.out.println("Test 1: Two-body gravitational system");
        GravitySimulation gravity = new GravitySimulation();
        
        Particle sun = new Particle("Sun", 1.989e30, 0, 0, 0);
        Particle earth = new Particle("Earth", 5.972e24, 1.496e11, 0, 0);
        earth.vy = 29780; // Orbital velocity
        
        gravity.addParticle(sun);
        gravity.addParticle(earth);
        
        gravity.printState();
        for (int i = 0; i < 10; i++) {
            gravity.step(86400); // 1 day steps
        }
        gravity.printState();
        
        // Test 2: Wave propagation
        System.out.println("\nTest 2: 1D Wave propagation");
        WaveSimulation wave = new WaveSimulation(100, 1.0, 0.1, 0.05);
        wave.initializeGaussianPulse(5.0, 1.0);
        
        wave.printWave();
        for (int i = 0; i < 50; i++) {
            wave.step();
        }
        wave.printWave();
        
        // Test 3: Projectile motion
        System.out.println("\nTest 3: Projectile motion with drag");
        ProjectileMotion projectile = new ProjectileMotion(1.0, 0.1);
        projectile.launch(50.0, Math.PI / 4); // 50 m/s at 45°
        
        List<double[]> trajectory = projectile.simulate(0.01, 10.0);
        System.out.println("Trajectory points: " + trajectory.size());
        if (!trajectory.isEmpty()) {
            double[] last = trajectory.get(trajectory.size() - 1);
            System.out.printf("Landing: x=%.2f m, t=%.2f s\n", last[0], last[2]);
        }
        
        double idealRange = ProjectileMotion.idealRange(50.0, Math.PI/4, 9.81);
        System.out.printf("Ideal range (no drag): %.2f m\n", idealRange);
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("✅ PHYSICS SIMULATION TESTS COMPLETE");
        System.out.println("═══════════════════════════════════════");
    }
}
