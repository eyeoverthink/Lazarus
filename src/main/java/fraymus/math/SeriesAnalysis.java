package fraymus.math;

import java.util.function.Function;

/**
 * 📊 SERIES ANALYSIS: Infinite Series & Convergence
 * 
 * Implements:
 * - Taylor/Maclaurin series expansions
 * - Power series
 * - Convergence tests (ratio test, root test, integral test)
 * - Fourier series
 * 
 * Mathematical Foundation:
 * Taylor Series: f(x) = Σ[n=0,∞] (f^(n)(a) / n!) * (x-a)^n
 * Maclaurin Series: Taylor series centered at a=0
 */
public class SeriesAnalysis {
    
    private static final int DEFAULT_TERMS = 100;
    private static final double CONVERGENCE_THRESHOLD = 1e-10;
    
    /**
     * Compute Taylor series expansion around point a
     * f(x) ≈ Σ[n=0,N] (f^(n)(a) / n!) * (x-a)^n
     * 
     * @param f Function to expand
     * @param a Point to expand around
     * @param x Point to evaluate series at
     * @param terms Number of terms
     * @return Series approximation
     */
    public static double taylorSeries(Function<Double, Double> f, double a, double x, int terms) {
        double result = 0;
        double factorial = 1;
        
        for (int n = 0; n < terms; n++) {
            if (n > 0) factorial *= n;
            
            // Compute nth derivative at point a
            double derivative = nthDerivative(f, a, n);
            
            // Add term: f^(n)(a) / n! * (x-a)^n
            result += (derivative / factorial) * Math.pow(x - a, n);
        }
        
        return result;
    }
    
    /**
     * Maclaurin series: Taylor series centered at 0
     */
    public static double maclaurinSeries(Function<Double, Double> f, double x, int terms) {
        return taylorSeries(f, 0, x, terms);
    }
    
    /**
     * Approximate nth derivative using finite differences
     * This is a simplified approach - more accurate methods exist
     */
    private static double nthDerivative(Function<Double, Double> f, double x, int n) {
        if (n == 0) return f.apply(x);
        if (n == 1) return CalculusEngine.derivative(f, x);
        
        // For higher derivatives, use recursive finite differences
        double h = 1e-5;
        
        // Build up derivatives recursively
        Function<Double, Double> currentDeriv = f;
        for (int i = 0; i < n; i++) {
            final Function<Double, Double> deriv = currentDeriv;
            currentDeriv = t -> CalculusEngine.derivative(deriv, t);
        }
        
        return currentDeriv.apply(x);
    }
    
    /**
     * Compute power series: Σ[n=0,∞] a_n * (x-c)^n
     * where a_n is provided by coefficient function
     */
    public static double powerSeries(Function<Integer, Double> coefficients, 
                                    double x, double center, int terms) {
        double result = 0;
        for (int n = 0; n < terms; n++) {
            result += coefficients.apply(n) * Math.pow(x - center, n);
        }
        return result;
    }
    
    /**
     * Ratio test for convergence
     * Returns limit of |a_(n+1) / a_n| as n→∞
     * Converges if limit < 1, diverges if > 1
     */
    public static double ratioTest(Function<Integer, Double> term, int maxN) {
        double ratio = 0;
        for (int n = maxN - 100; n < maxN; n++) {
            double current = Math.abs(term.apply(n));
            double next = Math.abs(term.apply(n + 1));
            if (current > 0) {
                ratio = next / current;
            }
        }
        return ratio;
    }
    
    /**
     * Root test for convergence
     * Returns limit of (|a_n|)^(1/n) as n→∞
     * Converges if limit < 1
     */
    public static double rootTest(Function<Integer, Double> term, int maxN) {
        double sum = 0;
        int count = 100;
        for (int n = maxN - count; n < maxN; n++) {
            sum += Math.pow(Math.abs(term.apply(n)), 1.0 / n);
        }
        return sum / count;
    }
    
    /**
     * Compute partial sum of series
     * Useful for testing convergence
     */
    public static double partialSum(Function<Integer, Double> term, int n) {
        double sum = 0;
        for (int i = 0; i < n; i++) {
            sum += term.apply(i);
        }
        return sum;
    }
    
    /**
     * Test if series converges by checking if partial sums stabilize
     */
    public static boolean converges(Function<Integer, Double> term, int maxTerms) {
        double sum1 = partialSum(term, maxTerms / 2);
        double sum2 = partialSum(term, maxTerms);
        
        return Math.abs(sum2 - sum1) < CONVERGENCE_THRESHOLD;
    }
    
    /**
     * Common series expansions
     */
    public static class CommonSeries {
        
        /**
         * e^x = Σ[n=0,∞] x^n / n!
         */
        public static double exponential(double x, int terms) {
            double result = 0;
            double factorial = 1;
            double power = 1;
            
            for (int n = 0; n < terms; n++) {
                if (n > 0) {
                    factorial *= n;
                    power *= x;
                }
                result += power / factorial;
            }
            return result;
        }
        
        /**
         * sin(x) = Σ[n=0,∞] (-1)^n * x^(2n+1) / (2n+1)!
         */
        public static double sine(double x, int terms) {
            double result = 0;
            double factorial = 1;
            double power = x;
            int sign = 1;
            
            for (int n = 0; n < terms; n++) {
                if (n > 0) {
                    factorial *= (2*n) * (2*n + 1);
                    power *= x * x;
                    sign *= -1;
                }
                result += sign * power / factorial;
            }
            return result;
        }
        
        /**
         * cos(x) = Σ[n=0,∞] (-1)^n * x^(2n) / (2n)!
         */
        public static double cosine(double x, int terms) {
            double result = 0;
            double factorial = 1;
            double power = 1;
            int sign = 1;
            
            for (int n = 0; n < terms; n++) {
                if (n > 0) {
                    factorial *= (2*n - 1) * (2*n);
                    power *= x * x;
                    sign *= -1;
                }
                result += sign * power / factorial;
            }
            return result;
        }
        
        /**
         * ln(1+x) = Σ[n=1,∞] (-1)^(n+1) * x^n / n  (for |x| < 1)
         */
        public static double logarithm(double x, int terms) {
            if (Math.abs(x) >= 1) {
                throw new IllegalArgumentException("Series only converges for |x| < 1");
            }
            
            double result = 0;
            double power = x;
            int sign = 1;
            
            for (int n = 1; n <= terms; n++) {
                result += sign * power / n;
                power *= x;
                sign *= -1;
            }
            return result;
        }
        
        /**
         * Geometric series: Σ[n=0,∞] r^n = 1/(1-r)  (for |r| < 1)
         */
        public static double geometric(double r, int terms) {
            if (Math.abs(r) >= 1) {
                throw new IllegalArgumentException("Series diverges for |r| >= 1");
            }
            
            double result = 0;
            double power = 1;
            
            for (int n = 0; n < terms; n++) {
                result += power;
                power *= r;
            }
            return result;
        }
        
        /**
         * Binomial series: (1+x)^α = Σ[n=0,∞] (α choose n) * x^n
         */
        public static double binomial(double x, double alpha, int terms) {
            double result = 0;
            double coefficient = 1;
            double power = 1;
            
            for (int n = 0; n < terms; n++) {
                if (n > 0) {
                    coefficient *= (alpha - n + 1) / n;
                    power *= x;
                }
                result += coefficient * power;
            }
            return result;
        }
    }
    
    /**
     * Fourier series representation
     * f(x) = a₀/2 + Σ[n=1,∞] (aₙ·cos(nx) + bₙ·sin(nx))
     */
    public static class FourierSeries {
        
        private final double[] cosCoefficients;
        private final double[] sinCoefficients;
        private final double a0;
        
        public FourierSeries(Function<Double, Double> f, double period, int terms) {
            this.cosCoefficients = new double[terms];
            this.sinCoefficients = new double[terms];
            
            // Compute a₀
            this.a0 = (2.0 / period) * CalculusEngine.integrate(f, 0, period);
            
            // Compute aₙ and bₙ
            for (int n = 1; n < terms; n++) {
                final int fn = n;
                
                // aₙ = (2/T) ∫ f(x)·cos(2πnx/T) dx
                Function<Double, Double> cosIntegrand = 
                    x -> f.apply(x) * Math.cos(2 * Math.PI * fn * x / period);
                cosCoefficients[n] = (2.0 / period) * 
                    CalculusEngine.integrate(cosIntegrand, 0, period);
                
                // bₙ = (2/T) ∫ f(x)·sin(2πnx/T) dx
                Function<Double, Double> sinIntegrand = 
                    x -> f.apply(x) * Math.sin(2 * Math.PI * fn * x / period);
                sinCoefficients[n] = (2.0 / period) * 
                    CalculusEngine.integrate(sinIntegrand, 0, period);
            }
        }
        
        /**
         * Evaluate Fourier series at point x
         */
        public double evaluate(double x, double period) {
            double result = a0 / 2.0;
            
            for (int n = 1; n < cosCoefficients.length; n++) {
                double angle = 2 * Math.PI * n * x / period;
                result += cosCoefficients[n] * Math.cos(angle) + 
                         sinCoefficients[n] * Math.sin(angle);
            }
            
            return result;
        }
    }
    
    /**
     * Test/Demo main method
     */
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════");
        System.out.println("📊 SERIES ANALYSIS DEMONSTRATION");
        System.out.println("═══════════════════════════════════════\n");
        
        // Test 1: e^x series
        System.out.println("Test 1: e^1 using Maclaurin series");
        double e1 = CommonSeries.exponential(1.0, 20);
        System.out.println("Result: " + e1);
        System.out.println("Expected: " + Math.E);
        System.out.println("Error: " + Math.abs(e1 - Math.E) + "\n");
        
        // Test 2: sin(π/6) = 0.5
        System.out.println("Test 2: sin(π/6) using series");
        double sinPi6 = CommonSeries.sine(Math.PI / 6, 20);
        System.out.println("Result: " + sinPi6);
        System.out.println("Expected: 0.5");
        System.out.println("Error: " + Math.abs(sinPi6 - 0.5) + "\n");
        
        // Test 3: cos(π/3) = 0.5
        System.out.println("Test 3: cos(π/3) using series");
        double cosPi3 = CommonSeries.cosine(Math.PI / 3, 20);
        System.out.println("Result: " + cosPi3);
        System.out.println("Expected: 0.5");
        System.out.println("Error: " + Math.abs(cosPi3 - 0.5) + "\n");
        
        // Test 4: Geometric series
        System.out.println("Test 4: Geometric series with r=0.5");
        double geom = CommonSeries.geometric(0.5, 50);
        System.out.println("Result: " + geom);
        System.out.println("Expected: 2.0 (1/(1-0.5))");
        System.out.println("Error: " + Math.abs(geom - 2.0) + "\n");
        
        // Test 5: Ratio test
        System.out.println("Test 5: Ratio test for e^x series (x=1)");
        Function<Integer, Double> expTerm = n -> {
            double factorial = 1;
            for (int i = 1; i <= n; i++) factorial *= i;
            return 1.0 / factorial;
        };
        double ratio = ratioTest(expTerm, 100);
        System.out.println("Ratio: " + ratio);
        System.out.println("Converges: " + (ratio < 1) + "\n");
        
        System.out.println("═══════════════════════════════════════");
        System.out.println("✅ SERIES ANALYSIS TESTS COMPLETE");
        System.out.println("═══════════════════════════════════════");
    }
}
