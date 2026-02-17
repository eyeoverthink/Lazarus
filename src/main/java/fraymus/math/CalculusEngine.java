package fraymus.math;

import java.util.function.Function;

/**
 * 📐 CALCULUS ENGINE: Integration & Differentiation
 * 
 * Implements Calculus II concepts including:
 * - Integration by parts (numerical approximation)
 * - Definite and indefinite integration
 * - LIATE rule for selecting u and dv
 * - Numerical differentiation
 * 
 * Mathematical Foundation:
 * ∫ u dv = uv - ∫ v du (Integration by Parts)
 * 
 * LIATE Rule (Choose u in this order):
 * L - Logarithmic: ln(x), log(x)
 * I - Inverse Trig: arcsin(x), arctan(x)
 * A - Algebraic: x², x³, polynomials
 * T - Trigonometric: sin(x), cos(x)
 * E - Exponential: e^x, a^x
 */
public class CalculusEngine {
    
    // Integration precision parameters
    private static final int DEFAULT_STEPS = 10000;
    private static final double EPSILON = 1e-10;
    
    /**
     * Definite integration using Simpson's Rule
     * ∫[a,b] f(x) dx
     * 
     * @param f Function to integrate
     * @param a Lower bound
     * @param b Upper bound
     * @param steps Number of steps (must be even)
     * @return Integral approximation
     */
    public static double integrate(Function<Double, Double> f, double a, double b, int steps) {
        if (steps % 2 != 0) steps++; // Ensure even number of steps
        
        double h = (b - a) / steps;
        double sum = f.apply(a) + f.apply(b);
        
        // Simpson's Rule: (h/3)[f(x0) + 4f(x1) + 2f(x2) + 4f(x3) + ... + f(xn)]
        for (int i = 1; i < steps; i++) {
            double x = a + i * h;
            sum += (i % 2 == 0 ? 2 : 4) * f.apply(x);
        }
        
        return sum * h / 3.0;
    }
    
    /**
     * Definite integration with default precision
     */
    public static double integrate(Function<Double, Double> f, double a, double b) {
        return integrate(f, a, b, DEFAULT_STEPS);
    }
    
    /**
     * Integration by parts: ∫ u dv = uv - ∫ v du
     * 
     * This is a numerical approximation where:
     * - u is the LIATE-selected function
     * - dv is the remaining part
     * - v is the integral of dv
     * 
     * @param u Function u(x)
     * @param dv Function dv/dx (derivative of v)
     * @param a Lower bound
     * @param b Upper bound
     * @return Approximation of ∫ u dv
     */
    public static double integrationByParts(
            Function<Double, Double> u,
            Function<Double, Double> dv,
            double a, double b) {
        
        // First, compute v(x) = ∫ dv/dx dx numerically
        // We approximate v(x) at each point using cumulative integration
        
        int steps = DEFAULT_STEPS;
        double h = (b - a) / steps;
        
        // Calculate uv term evaluated at bounds
        // v(b) ≈ ∫[a,b] dv(x) dx
        double vb = integrate(dv, a, b);
        double va = 0; // v(a) = 0 (choosing constant of integration = 0)
        
        double uvTerm = u.apply(b) * vb - u.apply(a) * va;
        
        // Calculate ∫ v du numerically
        // We need v'(x) = dv(x) and u'(x)
        Function<Double, Double> vdu = x -> {
            // v(x) ≈ ∫[a,x] dv(t) dt
            double vx = integrate(dv, a, x, 1000);
            // du/dx ≈ (u(x+h) - u(x-h)) / 2h
            double du = derivative(u, x);
            return vx * du;
        };
        
        double vduIntegral = integrate(vdu, a, b);
        
        return uvTerm - vduIntegral;
    }
    
    /**
     * Numerical derivative using central difference formula
     * f'(x) ≈ (f(x+h) - f(x-h)) / 2h
     */
    public static double derivative(Function<Double, Double> f, double x) {
        double h = Math.max(Math.abs(x) * EPSILON, EPSILON);
        return (f.apply(x + h) - f.apply(x - h)) / (2 * h);
    }
    
    /**
     * Second derivative
     * f''(x) ≈ (f(x+h) - 2f(x) + f(x-h)) / h²
     */
    public static double secondDerivative(Function<Double, Double> f, double x) {
        double h = Math.max(Math.abs(x) * EPSILON, EPSILON);
        return (f.apply(x + h) - 2 * f.apply(x) + f.apply(x - h)) / (h * h);
    }
    
    /**
     * Trapezoidal rule integration (alternative method)
     */
    public static double trapezoidalIntegration(
            Function<Double, Double> f, double a, double b, int steps) {
        double h = (b - a) / steps;
        double sum = 0.5 * (f.apply(a) + f.apply(b));
        
        for (int i = 1; i < steps; i++) {
            sum += f.apply(a + i * h);
        }
        
        return sum * h;
    }
    
    /**
     * Adaptive integration - automatically adjusts step size
     * for better accuracy in regions where function varies rapidly
     */
    public static double adaptiveIntegration(
            Function<Double, Double> f, double a, double b, double tolerance) {
        
        // Calculate with different step sizes
        double integral1 = integrate(f, a, b, 100);
        double integral2 = integrate(f, a, b, 200);
        
        // If difference is within tolerance, return more accurate result
        if (Math.abs(integral2 - integral1) < tolerance) {
            return integral2;
        }
        
        // Otherwise, split interval and recurse
        double mid = (a + b) / 2;
        return adaptiveIntegration(f, a, mid, tolerance / 2) + 
               adaptiveIntegration(f, mid, b, tolerance / 2);
    }
    
    /**
     * Improper integral handler (for infinite bounds)
     * ∫[a,∞) f(x) dx ≈ ∫[a,L] f(x) dx where L is large
     */
    public static double improperIntegral(
            Function<Double, Double> f, double a, double limit, int steps) {
        // Use substitution x = a + tan(u) to map [a,∞) to finite interval
        double result = 0;
        double h = Math.PI / (2 * steps);
        
        for (int i = 0; i < steps; i++) {
            double u = i * h;
            double x = a + Math.tan(u);
            double weight = 1 / (Math.cos(u) * Math.cos(u)); // dx/du
            
            if (x < limit && !Double.isInfinite(x) && !Double.isNaN(x)) {
                result += f.apply(x) * weight * h;
            }
        }
        
        return result;
    }
    
    /**
     * Common integration examples demonstrating LIATE rule
     */
    public static class Examples {
        
        /**
         * ∫ x * e^x dx using integration by parts
         * u = x (Algebraic), dv = e^x dx (Exponential)
         */
        public static double xTimesExp(double a, double b) {
            Function<Double, Double> u = x -> x;
            Function<Double, Double> dv = x -> Math.exp(x);
            return integrationByParts(u, dv, a, b);
        }
        
        /**
         * ∫ x * sin(x) dx
         * u = x (Algebraic), dv = sin(x) dx (Trigonometric)
         */
        public static double xTimesSin(double a, double b) {
            Function<Double, Double> u = x -> x;
            Function<Double, Double> dv = x -> Math.sin(x);
            return integrationByParts(u, dv, a, b);
        }
        
        /**
         * ∫ ln(x) dx
         * u = ln(x) (Logarithmic), dv = 1 dx (Algebraic)
         */
        public static double naturalLog(double a, double b) {
            Function<Double, Double> u = x -> Math.log(x);
            Function<Double, Double> dv = x -> 1.0;
            return integrationByParts(u, dv, a, b);
        }
        
        /**
         * ∫ arctan(x) dx
         * u = arctan(x) (Inverse Trig), dv = 1 dx (Algebraic)
         */
        public static double arctan(double a, double b) {
            Function<Double, Double> u = x -> Math.atan(x);
            Function<Double, Double> dv = x -> 1.0;
            return integrationByParts(u, dv, a, b);
        }
    }
    
    /**
     * Test/Demo main method
     */
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════");
        System.out.println("📐 CALCULUS ENGINE DEMONSTRATION");
        System.out.println("═══════════════════════════════════════\n");
        
        // Test 1: Simple integration
        System.out.println("Test 1: ∫[0,π] sin(x) dx");
        double result1 = integrate(Math::sin, 0, Math.PI);
        System.out.println("Result: " + result1);
        System.out.println("Expected: 2.0");
        System.out.println("Error: " + Math.abs(result1 - 2.0) + "\n");
        
        // Test 2: x² integration
        System.out.println("Test 2: ∫[0,1] x² dx");
        double result2 = integrate(x -> x * x, 0, 1);
        System.out.println("Result: " + result2);
        System.out.println("Expected: 0.333333...");
        System.out.println("Error: " + Math.abs(result2 - 1.0/3.0) + "\n");
        
        // Test 3: Integration by parts - x * e^x
        System.out.println("Test 3: ∫[0,1] x·e^x dx (Integration by Parts)");
        double result3 = Examples.xTimesExp(0, 1);
        System.out.println("Result: " + result3);
        System.out.println("Expected: 1.0 (approximately)");
        System.out.println("Error: " + Math.abs(result3 - 1.0) + "\n");
        
        // Test 4: Integration by parts - x * sin(x)
        System.out.println("Test 4: ∫[0,π] x·sin(x) dx (Integration by Parts)");
        double result4 = Examples.xTimesSin(0, Math.PI);
        System.out.println("Result: " + result4);
        System.out.println("Expected: π ≈ 3.14159");
        System.out.println("Error: " + Math.abs(result4 - Math.PI) + "\n");
        
        // Test 5: Derivative
        System.out.println("Test 5: d/dx[x²] at x=3");
        double result5 = derivative(x -> x * x, 3.0);
        System.out.println("Result: " + result5);
        System.out.println("Expected: 6.0");
        System.out.println("Error: " + Math.abs(result5 - 6.0) + "\n");
        
        System.out.println("═══════════════════════════════════════");
        System.out.println("✅ CALCULUS ENGINE TESTS COMPLETE");
        System.out.println("═══════════════════════════════════════");
    }
}
