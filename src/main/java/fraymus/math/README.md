# Advanced Mathematics & Physics Package

## Overview

This package implements advanced mathematical and physics principles for computational simulations and algorithms. It provides a comprehensive foundation for:

- **Calculus II**: Integration techniques, differentiation, and numerical methods
- **Series Analysis**: Infinite series, convergence tests, and power series
- **Physics Simulations**: Particle dynamics, wave propagation, and projectile motion
- **Quantum Algorithms**: Quantum gates, state evolution, and optimization
- **String Theory**: M-theory, dimensional analysis, and Calabi-Yau manifolds

## Modules

### 1. CalculusEngine.java
Implements calculus operations including:
- **Definite Integration**: Simpson's Rule for accurate numerical integration
- **Integration by Parts**: Following the LIATE rule (Logarithmic, Inverse trig, Algebraic, Trigonometric, Exponential)
- **Numerical Differentiation**: Central difference method
- **Adaptive Integration**: Automatically adjusts precision based on function behavior
- **Improper Integrals**: Handles infinite bounds using substitution

**Example Usage:**
```java
// Integrate sin(x) from 0 to π
double result = CalculusEngine.integrate(Math::sin, 0, Math.PI);

// Integration by parts: ∫ x·e^x dx
double ibp = CalculusEngine.Examples.xTimesExp(0, 1);

// Derivative at a point
double deriv = CalculusEngine.derivative(x -> x * x, 3.0);
```

### 2. SeriesAnalysis.java
Provides series expansion and convergence analysis:
- **Taylor/Maclaurin Series**: Arbitrary function expansion around a point
- **Common Series**: Exponential, sine, cosine, logarithm, geometric, binomial
- **Convergence Tests**: Ratio test, root test, partial sum analysis
- **Fourier Series**: Frequency domain representation

**Example Usage:**
```java
// Calculate e using exponential series
double e = SeriesAnalysis.CommonSeries.exponential(1.0, 20);

// Sine using Maclaurin series
double sin = SeriesAnalysis.CommonSeries.sine(Math.PI / 6, 20);

// Test convergence
boolean converges = SeriesAnalysis.converges(term, 1000);
```

### 3. PhysicsSimulation.java
Implements physics simulations using calculus:
- **Particle Dynamics**: Newton's laws with force integration (F = ma)
- **Gravity Simulation**: N-body gravitational systems
- **Wave Propagation**: 1D wave equation solver with energy conservation
- **Projectile Motion**: Ballistic trajectory with air resistance

**Example Usage:**
```java
// Create a particle
Particle p = new Particle("Electron", 9.11e-31, 0, 0, 0);
p.applyForce(1e-20, 0, 0);
p.update(1e-10);

// Wave simulation
WaveSimulation wave = new WaveSimulation(100, 1.0, 0.1, 0.05);
wave.initializeGaussianPulse(5.0, 1.0);
wave.step();
```

### 4. QuantumAlgorithms.java
Quantum computing and information theory:
- **Quantum States**: Qubit representation with complex amplitudes
- **Quantum Gates**: Hadamard, Pauli (X, Y, Z), Phase, T, Rotation gates
- **State Evolution**: Schrödinger equation integration
- **Quantum Optimization**: QAOA-inspired classical optimization
- **Quantum Annealing**: Simulated annealing for combinatorial problems

**Example Usage:**
```java
// Create and manipulate qubit
QubitState qubit = new QubitState();
qubit = QuantumGates.hadamard(qubit);
qubit = QuantumGates.rotateZ(qubit, Math.PI / 4);

// Quantum evolution
qubit = QuantumEvolution.evolve(qubit, energy, time);

// Optimization
double[] solution = QuantumOptimization.optimize(costFunction, 2, 50);
```

### 5. StringTheory.java
M-theory and dimensional analysis:
- **11D Spacetime**: Points and distances in 11-dimensional M-theory space
- **String Vibrations**: Discretized string dynamics with mode excitation
- **Calabi-Yau Manifolds**: Compact extra dimensions
- **Dimensional Compactification**: Kaluza-Klein theory and effective field theory
- **Brane Worlds**: 4D universe as a brane in higher dimensions

**Example Usage:**
```java
// Create point in 11D
Point11D point = new Point11D(coords);
double[] projection = point.projectTo4D();

// String vibrations
VibrationString11D string = new VibrationString11D(50, PLANCK_LENGTH, 1.0);
string.exciteMode(1, amplitude, dimension);

// Kaluza-Klein spectrum
double mass = DimensionalAnalysis.kaluzaKleinMass(n, radius);
```

## Mathematical Foundations

### LIATE Rule (Integration by Parts)
When choosing u and dv for ∫u dv = uv - ∫v du, select u in this priority order:
1. **L**ogarithmic: ln(x), log(x)
2. **I**nverse Trigonometric: arcsin(x), arctan(x)
3. **A**lgebraic: x², x³, polynomials
4. **T**rigonometric: sin(x), cos(x)
5. **E**xponential: e^x, a^x

### Simpson's Rule
For numerical integration: ∫[a,b] f(x) dx ≈ (h/3)[f(x₀) + 4f(x₁) + 2f(x₂) + 4f(x₃) + ... + f(xₙ)]

### Wave Equation
∂²u/∂t² = c²·∂²u/∂x²

Solved using finite differences with CFL stability condition: c·dt/dx ≤ 1

### Schrödinger Equation
iℏ ∂|ψ⟩/∂t = Ĥ|ψ⟩

Solution: |ψ(t)⟩ = exp(-iĤt/ℏ)|ψ(0)⟩

### M-Theory
11-dimensional spacetime (10 space + 1 time)
String mode energies: Eₙ = n/(2L)

## Running Demonstrations

### Individual Module Tests
```bash
# Test Calculus Engine
java -cp build/libs/Lazarus-1.0-SNAPSHOT.jar fraymus.math.CalculusEngine

# Test Series Analysis
java -cp build/libs/Lazarus-1.0-SNAPSHOT.jar fraymus.math.SeriesAnalysis

# Test Physics Simulation
java -cp build/libs/Lazarus-1.0-SNAPSHOT.jar fraymus.math.PhysicsSimulation

# Test Quantum Algorithms
java -cp build/libs/Lazarus-1.0-SNAPSHOT.jar fraymus.math.QuantumAlgorithms

# Test String Theory
java -cp build/libs/Lazarus-1.0-SNAPSHOT.jar fraymus.math.StringTheory
```

### Comprehensive Demo
```bash
# Run all demonstrations
java -cp build/libs/Lazarus-1.0-SNAPSHOT.jar fraymus.math.AdvancedMathDemo
```

## Applications

### Physics Simulations
- Particle accelerators and collision detection
- Gravitational systems (planetary orbits, galaxy formation)
- Wave propagation (electromagnetic, acoustic, quantum)

### Signal Processing
- Fourier transforms for frequency analysis
- Digital filters and convolution
- Spectral analysis

### Quantum Computing
- Quantum circuit simulation
- Optimization algorithms (QAOA, quantum annealing)
- Quantum cryptography protocols

### Computational Science
- Numerical methods for differential equations
- Series approximations for special functions
- Dimensional reduction in high-energy physics

## Implementation Notes

### Numerical Precision
- Default integration steps: 10,000 (configurable)
- Floating-point epsilon: 1e-10
- All methods validated against analytical solutions

### Performance
- Simpson's Rule: O(n) for n steps
- Wave simulation: O(n×steps) for n grid points
- String dynamics: O(n×d) for n points in d dimensions

### Validation
Each module includes comprehensive test cases demonstrating:
- Accuracy against known analytical solutions
- Convergence properties
- Edge case handling
- Energy/probability conservation where applicable

## References

### Mathematical Foundations
- Calculus II: Integration techniques, series convergence
- Numerical Analysis: Simpson's Rule, finite differences
- Complex Analysis: Complex arithmetic for quantum states

### Physics Principles
- Classical Mechanics: Newton's laws, energy conservation
- Quantum Mechanics: Schrödinger equation, quantum gates
- String Theory: M-theory, Calabi-Yau manifolds, brane worlds

## Future Extensions

Potential enhancements:
- Multi-variable calculus (gradients, Jacobians)
- Advanced series (Laurent, Dirichlet)
- Relativistic physics simulations
- Quantum error correction codes
- Advanced string theory concepts (D-branes, holography)
