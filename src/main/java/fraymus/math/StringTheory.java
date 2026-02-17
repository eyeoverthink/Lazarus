package fraymus.math;

/**
 * 🌌 STRING THEORY & DIMENSIONAL ANALYSIS
 * 
 * Implements concepts from M-theory and string theory:
 * - Extra dimensions (11D spacetime)
 * - Calabi-Yau manifolds (simplified)
 * - String vibration modes
 * - Dimensional compactification
 * 
 * Mathematical Foundation:
 * M-theory: 11-dimensional spacetime (10 space + 1 time)
 * String modes: E_n = n/(2L) where L is string length
 */
public class StringTheory {
    
    // Physical constants (in natural units where c = ℏ = 1)
    private static final double PLANCK_LENGTH = 1.616255e-35; // meters
    private static final double STRING_COUPLING = 0.1;
    
    /**
     * Point in 11-dimensional spacetime (M-theory)
     * Dimensions: [t, x, y, z, x4, x5, x6, x7, x8, x9, x10]
     */
    public static class Point11D {
        public final double[] coordinates;
        
        public Point11D(double... coords) {
            if (coords.length != 11) {
                throw new IllegalArgumentException("Must have 11 coordinates");
            }
            this.coordinates = coords.clone();
        }
        
        /**
         * Calculate distance in 11D space
         * Using Minkowski metric with signature (-,+,+,+,+,+,+,+,+,+,+)
         */
        public double distanceTo(Point11D other) {
            double dist2 = 0;
            
            // Time component (negative signature)
            dist2 -= Math.pow(coordinates[0] - other.coordinates[0], 2);
            
            // Spatial components (positive signature)
            for (int i = 1; i < 11; i++) {
                dist2 += Math.pow(coordinates[i] - other.coordinates[i], 2);
            }
            
            return Math.sqrt(Math.abs(dist2));
        }
        
        /**
         * Project to 4D spacetime (compactify extra dimensions)
         * Returns [t, x, y, z]
         */
        public double[] projectTo4D() {
            return new double[] {
                coordinates[0],
                coordinates[1],
                coordinates[2],
                coordinates[3]
            };
        }
        
        /**
         * Get compactified dimensions [x4, x5, x6, x7, x8, x9, x10]
         */
        public double[] getCompactDimensions() {
            double[] compact = new double[7];
            System.arraycopy(coordinates, 4, compact, 0, 7);
            return compact;
        }
    }
    
    /**
     * String vibration in 11D spacetime
     */
    public static class VibrationString11D {
        private final double length;           // String length
        private final double tension;          // String tension
        private final Point11D[] points;       // Discretized string points
        private final double[][] velocities;   // Velocities of string points
        
        public VibrationString11D(int numPoints, double length, double tension) {
            this.length = length;
            this.tension = tension;
            this.points = new Point11D[numPoints];
            this.velocities = new double[numPoints][11];
            
            // Initialize as straight string along x-axis
            for (int i = 0; i < numPoints; i++) {
                double x = (i * length) / (numPoints - 1);
                double[] coords = new double[11];
                coords[1] = x; // x-coordinate
                points[i] = new Point11D(coords);
            }
        }
        
        /**
         * Excite string with specific vibration mode
         * Mode n: sin(nπx/L)
         */
        public void exciteMode(int n, double amplitude, int dimension) {
            for (int i = 0; i < points.length; i++) {
                double x = (i * length) / (points.length - 1);
                double displacement = amplitude * Math.sin(n * Math.PI * x / length);
                
                double[] coords = points[i].coordinates.clone();
                coords[dimension] += displacement;
                points[i] = new Point11D(coords);
            }
        }
        
        /**
         * Calculate energy of vibration mode n
         * E_n = n/(2L) in natural units
         */
        public static double modeEnergy(int n, double stringLength) {
            return n / (2.0 * stringLength);
        }
        
        /**
         * Get total string energy
         */
        public double totalEnergy() {
            double energy = 0;
            
            // Kinetic energy
            for (int i = 0; i < points.length; i++) {
                for (int d = 0; d < 11; d++) {
                    energy += 0.5 * velocities[i][d] * velocities[i][d];
                }
            }
            
            // Potential energy (string tension)
            for (int i = 0; i < points.length - 1; i++) {
                double dist = points[i].distanceTo(points[i + 1]);
                energy += tension * dist;
            }
            
            return energy;
        }
        
        /**
         * Evolve string dynamics for one time step
         */
        public void evolve(double dt) {
            // Wave equation on string: ∂²x/∂t² = (T/μ)∂²x/∂s²
            double c2 = tension; // Wave speed squared
            double ds = length / (points.length - 1);
            
            for (int i = 1; i < points.length - 1; i++) {
                for (int d = 0; d < 11; d++) {
                    // Second derivative in space
                    double d2x = (points[i+1].coordinates[d] - 2*points[i].coordinates[d] 
                                + points[i-1].coordinates[d]) / (ds * ds);
                    
                    // Update velocity
                    velocities[i][d] += c2 * d2x * dt;
                    
                    // Update position
                    double[] newCoords = points[i].coordinates.clone();
                    newCoords[d] += velocities[i][d] * dt;
                    points[i] = new Point11D(newCoords);
                }
            }
        }
    }
    
    /**
     * Calabi-Yau manifold (simplified representation)
     * These are the compact extra dimensions in string theory
     */
    public static class CalabiYauManifold {
        private final int topology;  // Topological invariant
        private final double volume;  // Compactification volume
        
        public CalabiYauManifold(int topology, double volume) {
            this.topology = topology;
            this.volume = volume;
        }
        
        /**
         * Map point on manifold to compactified coordinates
         * Simplified: use toroidal compactification
         */
        public double[] toroidalMap(double[] params) {
            if (params.length != 6) {
                throw new IllegalArgumentException("Need 6 parameters for 6D manifold");
            }
            
            // Map to torus: x_i = R_i * sin(θ_i)
            double[] coords = new double[6];
            double radius = Math.cbrt(volume / Math.pow(2 * Math.PI, 3));
            
            for (int i = 0; i < 6; i++) {
                coords[i] = radius * Math.sin(params[i]);
            }
            
            return coords;
        }
        
        /**
         * Calculate moduli (parameters of the manifold)
         */
        public int numberOfModuli() {
            // For a generic Calabi-Yau, related to Hodge numbers
            // Simplified: h^{1,1} + h^{2,1}
            return topology * 2;
        }
    }
    
    /**
     * Dimensional compactification utilities
     */
    public static class DimensionalAnalysis {
        
        /**
         * Kaluza-Klein mass spectrum
         * m_n = n/R where R is compactification radius
         */
        public static double kaluzaKleinMass(int n, double compactRadius) {
            return n / compactRadius;
        }
        
        /**
         * Calculate compactification radius from energy scale
         */
        public static double compactificationRadius(double energyScale) {
            // R ~ 1/E in natural units
            return 1.0 / energyScale;
        }
        
        /**
         * Transform between dimensions using Fourier modes
         * Projects higher-D field to 4D effective field
         */
        public static double[] fourierDecomposition(
                double[] field11D, int modesPerDim) {
            
            // Simplified: just keep zero modes (ground state)
            // In reality, would do full Fourier transform
            double[] field4D = new double[4];
            System.arraycopy(field11D, 0, field4D, 0, 4);
            
            return field4D;
        }
        
        /**
         * Calculate effective coupling in 4D from 11D
         * g_4D = g_11D / V^(7/2) where V is compactification volume
         */
        public static double effectiveCoupling(double coupling11D, double volume) {
            return coupling11D / Math.pow(volume, 3.5);
        }
    }
    
    /**
     * Brane-world scenario
     * Our 4D universe as a brane in higher dimensions
     */
    public static class BraneWorld {
        private final Point11D position;  // Position of brane in bulk
        private final double tension;     // Brane tension
        
        public BraneWorld(Point11D position, double tension) {
            this.position = position;
            this.tension = tension;
        }
        
        /**
         * Check if point is on the brane
         * (simplified: brane at fixed extra dimensions)
         */
        public boolean containsPoint(Point11D point) {
            double[] compact1 = position.getCompactDimensions();
            double[] compact2 = point.getCompactDimensions();
            
            // Point is on brane if extra dimensions match
            double dist = 0;
            for (int i = 0; i < compact1.length; i++) {
                dist += Math.pow(compact1[i] - compact2[i], 2);
            }
            
            return Math.sqrt(dist) < PLANCK_LENGTH * 10;
        }
        
        /**
         * Calculate graviton propagation to bulk
         * Gravity can escape into extra dimensions
         */
        public double gravitonPropagator(Point11D source, Point11D target) {
            double r4D = 0;  // 4D distance
            for (int i = 0; i < 4; i++) {
                r4D += Math.pow(target.coordinates[i] - source.coordinates[i], 2);
            }
            r4D = Math.sqrt(r4D);
            
            double r11D = source.distanceTo(target);  // Full 11D distance
            
            // At short distances: 1/r^9 (11D gravity)
            // At long distances: 1/r^2 (4D gravity)
            double crossover = 1e-3; // Example crossover scale
            
            if (r11D < crossover) {
                return 1.0 / Math.pow(r11D, 9);
            } else {
                return 1.0 / (r4D * r4D);
            }
        }
    }
    
    /**
     * Test/Demo main method
     */
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════");
        System.out.println("🌌 STRING THEORY DEMONSTRATION");
        System.out.println("═══════════════════════════════════════\n");
        
        // Test 1: 11D points
        System.out.println("Test 1: M-theory spacetime (11 dimensions)");
        double[] coords1 = new double[11];
        coords1[0] = 0; // time
        coords1[1] = 1; // x
        Point11D point1 = new Point11D(coords1);
        
        double[] coords2 = new double[11];
        coords2[0] = 0;
        coords2[2] = 1; // y
        Point11D point2 = new Point11D(coords2);
        
        double dist = point1.distanceTo(point2);
        System.out.println("Distance in 11D: " + dist);
        
        double[] proj = point1.projectTo4D();
        System.out.println("4D projection: t=" + proj[0] + ", x=" + proj[1] + 
                          ", y=" + proj[2] + ", z=" + proj[3]);
        
        // Test 2: String vibrations
        System.out.println("\nTest 2: String vibration modes");
        VibrationString11D string = new VibrationString11D(50, PLANCK_LENGTH * 10, 1.0);
        
        // Excite fundamental mode
        string.exciteMode(1, PLANCK_LENGTH, 2);
        System.out.println("String excited with n=1 mode");
        
        double energy1 = VibrationString11D.modeEnergy(1, PLANCK_LENGTH * 10);
        double energy2 = VibrationString11D.modeEnergy(2, PLANCK_LENGTH * 10);
        System.out.printf("Mode energies: E_1=%.2e, E_2=%.2e\n", energy1, energy2);
        
        // Test 3: Calabi-Yau manifold
        System.out.println("\nTest 3: Calabi-Yau compactification");
        double volume = Math.pow(PLANCK_LENGTH, 6);
        CalabiYauManifold cy = new CalabiYauManifold(10, volume);
        System.out.println("Topology invariant: " + 10);
        System.out.println("Number of moduli: " + cy.numberOfModuli());
        
        // Test 4: Kaluza-Klein spectrum
        System.out.println("\nTest 4: Kaluza-Klein mass spectrum");
        double R = 1e-30; // Compactification radius
        for (int n = 1; n <= 3; n++) {
            double mass = DimensionalAnalysis.kaluzaKleinMass(n, R);
            System.out.printf("KK mode n=%d: mass = %.2e\n", n, mass);
        }
        
        // Test 5: Brane world
        System.out.println("\nTest 5: Brane-world scenario");
        double[] braneCoords = new double[11];
        Point11D branePos = new Point11D(braneCoords);
        BraneWorld brane = new BraneWorld(branePos, 1.0);
        
        boolean onBrane = brane.containsPoint(point1);
        System.out.println("Point on brane: " + onBrane);
        
        double propagator = brane.gravitonPropagator(point1, point2);
        System.out.println("Graviton propagator: " + propagator);
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("✅ STRING THEORY TESTS COMPLETE");
        System.out.println("═══════════════════════════════════════");
    }
}
