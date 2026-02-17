package fraymus.math;

/**
 * 🎓 ADVANCED MATHEMATICS & PHYSICS DEMO
 * 
 * Comprehensive demonstration of mathematical foundations for:
 * - Calculus II (Integration techniques, Series)
 * - Quantum Information Theory
 * - String Theory & M-Theory
 * - Physics Simulations
 * 
 * This demonstrates the complete implementation of advanced mathematical
 * and physics principles for computational applications.
 */
public class AdvancedMathDemo {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  ADVANCED MATHEMATICS & PHYSICS PRINCIPLES           ║");
        System.out.println("║  Calculus II • Quantum Theory • String Theory        ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");
        
        // Section 1: Calculus II - Integration Techniques
        demonstrateCalculusII();
        
        // Section 2: Series Analysis
        demonstrateSeries();
        
        // Section 3: Physics Simulations
        demonstratePhysics();
        
        // Section 4: Quantum Algorithms
        demonstrateQuantum();
        
        // Section 5: String Theory
        demonstrateStringTheory();
        
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  ✅ ALL DEMONSTRATIONS COMPLETE                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
    }
    
    private static void demonstrateCalculusII() {
        System.out.println("┌───────────────────────────────────────────────────────┐");
        System.out.println("│ 📐 CALCULUS II: Integration Techniques               │");
        System.out.println("└───────────────────────────────────────────────────────┘");
        
        // Definite Integration
        System.out.println("\n1. Definite Integration (Simpson's Rule):");
        double integral = CalculusEngine.integrate(Math::sin, 0, Math.PI);
        System.out.printf("   ∫[0,π] sin(x) dx = %.6f (expected: 2.0)\n", integral);
        
        // Integration by Parts
        System.out.println("\n2. Integration by Parts (LIATE Rule):");
        double ibp = CalculusEngine.Examples.xTimesExp(0, 1);
        System.out.printf("   ∫[0,1] x·e^x dx = %.6f (expected: ~1.0)\n", ibp);
        
        // Derivative
        System.out.println("\n3. Numerical Differentiation:");
        double deriv = CalculusEngine.derivative(x -> x * x, 3.0);
        System.out.printf("   d/dx[x²] at x=3 = %.6f (expected: 6.0)\n", deriv);
        
        System.out.println();
    }
    
    private static void demonstrateSeries() {
        System.out.println("┌───────────────────────────────────────────────────────┐");
        System.out.println("│ 📊 SERIES ANALYSIS: Infinite Series & Convergence     │");
        System.out.println("└───────────────────────────────────────────────────────┘");
        
        // Exponential series
        System.out.println("\n1. Exponential Series:");
        double e = SeriesAnalysis.CommonSeries.exponential(1.0, 20);
        System.out.printf("   e^1 = %.10f (expected: %.10f)\n", e, Math.E);
        
        // Trigonometric series
        System.out.println("\n2. Trigonometric Series:");
        double sin = SeriesAnalysis.CommonSeries.sine(Math.PI / 6, 20);
        System.out.printf("   sin(π/6) = %.6f (expected: 0.5)\n", sin);
        
        // Geometric series
        System.out.println("\n3. Geometric Series:");
        double geom = SeriesAnalysis.CommonSeries.geometric(0.5, 50);
        System.out.printf("   Σ(0.5)^n = %.6f (expected: 2.0)\n", geom);
        
        System.out.println();
    }
    
    private static void demonstratePhysics() {
        System.out.println("┌───────────────────────────────────────────────────────┐");
        System.out.println("│ 🌊 PHYSICS SIMULATIONS: Calculus-Based Physics       │");
        System.out.println("└───────────────────────────────────────────────────────┘");
        
        // Particle dynamics
        System.out.println("\n1. Particle Dynamics (Newton's Laws):");
        PhysicsSimulation.Particle particle = 
            new PhysicsSimulation.Particle("Electron", 9.11e-31, 0, 0, 0);
        particle.applyForce(1e-20, 0, 0);
        particle.update(1e-10);
        System.out.printf("   Position after force: x=%.2e m\n", particle.x);
        
        // Wave propagation
        System.out.println("\n2. Wave Propagation (Wave Equation):");
        PhysicsSimulation.WaveSimulation wave = 
            new PhysicsSimulation.WaveSimulation(100, 1.0, 0.1, 0.05);
        wave.initializeGaussianPulse(5.0, 1.0);
        double initialEnergy = wave.totalEnergy();
        for (int i = 0; i < 50; i++) wave.step();
        double finalEnergy = wave.totalEnergy();
        System.out.printf("   Energy conservation: initial=%.4f, final=%.4f\n", 
            initialEnergy, finalEnergy);
        
        // Projectile motion
        System.out.println("\n3. Projectile Motion (with air resistance):");
        PhysicsSimulation.ProjectileMotion proj = 
            new PhysicsSimulation.ProjectileMotion(1.0, 0.1);
        proj.launch(50.0, Math.PI / 4);
        var trajectory = proj.simulate(0.01, 10.0);
        System.out.printf("   Trajectory points: %d\n", trajectory.size());
        
        System.out.println();
    }
    
    private static void demonstrateQuantum() {
        System.out.println("┌───────────────────────────────────────────────────────┐");
        System.out.println("│ ⚛️  QUANTUM ALGORITHMS: Information Theory           │");
        System.out.println("└───────────────────────────────────────────────────────┘");
        
        // Quantum gates
        System.out.println("\n1. Quantum Gates & Superposition:");
        QuantumAlgorithms.QubitState qubit = new QuantumAlgorithms.QubitState();
        qubit = QuantumAlgorithms.QuantumGates.hadamard(qubit);
        System.out.printf("   After Hadamard: P(0)=%.4f, P(1)=%.4f\n", 
            qubit.probability0(), qubit.probability1());
        
        // Quantum evolution
        System.out.println("\n2. Quantum State Evolution (Schrödinger Equation):");
        QuantumAlgorithms.QubitState evolving = 
            QuantumAlgorithms.QuantumGates.hadamard(new QuantumAlgorithms.QubitState());
        evolving = QuantumAlgorithms.QuantumEvolution.evolve(evolving, 1e-20, 1e-10);
        System.out.printf("   Phase evolution complete: P(0)=%.4f\n", 
            evolving.probability0());
        
        // Quantum optimization
        System.out.println("\n3. Quantum-Inspired Optimization:");
        java.util.function.Function<double[], Double> costFunc = 
            (double[] x) -> Math.pow(x[0] - 1, 2) + Math.pow(x[1] - 2, 2);
        double[] optimal = QuantumAlgorithms.QuantumOptimization.optimize(costFunc, 2, 30);
        System.out.printf("   Optimized: x=%.4f, y=%.4f (expected: 1.0, 2.0)\n", 
            optimal[0], optimal[1]);
        
        System.out.println();
    }
    
    private static void demonstrateStringTheory() {
        System.out.println("┌───────────────────────────────────────────────────────┐");
        System.out.println("│ 🌌 STRING THEORY: M-Theory & Extra Dimensions        │");
        System.out.println("└───────────────────────────────────────────────────────┘");
        
        // 11D spacetime
        System.out.println("\n1. M-Theory Spacetime (11 Dimensions):");
        double[] coords = new double[11];
        coords[1] = 1.0; // x
        coords[2] = 1.0; // y
        StringTheory.Point11D point = new StringTheory.Point11D(coords);
        double[] projection = point.projectTo4D();
        System.out.printf("   11D point projects to 4D: (%.1f, %.1f, %.1f, %.1f)\n",
            projection[0], projection[1], projection[2], projection[3]);
        
        // String vibrations
        System.out.println("\n2. String Vibration Modes:");
        double energy1 = StringTheory.VibrationString11D.modeEnergy(1, 1e-35);
        double energy2 = StringTheory.VibrationString11D.modeEnergy(2, 1e-35);
        System.out.printf("   E₁/E₂ ratio = %.4f (expected: 0.5)\n", energy1 / energy2);
        
        // Kaluza-Klein spectrum
        System.out.println("\n3. Kaluza-Klein Mass Spectrum:");
        double R = 1e-30;
        double mass1 = StringTheory.DimensionalAnalysis.kaluzaKleinMass(1, R);
        System.out.printf("   First KK mode mass: %.2e\n", mass1);
        
        // Calabi-Yau manifold
        System.out.println("\n4. Calabi-Yau Compactification:");
        StringTheory.CalabiYauManifold cy = 
            new StringTheory.CalabiYauManifold(10, Math.pow(1e-35, 6));
        System.out.printf("   Number of moduli parameters: %d\n", cy.numberOfModuli());
        
        System.out.println();
    }
}
