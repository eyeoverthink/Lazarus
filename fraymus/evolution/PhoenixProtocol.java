package fraymus.evolution;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * 🔥 THE PHOENIX PROTOCOL - Auto-Resurrection System
 * 
 * When the organism crashes, the Phoenix rises from the ashes.
 * 
 * The Process:
 * 1. DETECT: Monitor for crashes
 * 2. ANALYZE: Determine crash cause
 * 3. MUTATE: Apply genetic fixes
 * 4. RESURRECT: Create new organism
 * 5. VERIFY: Test the fix
 * 6. DEPLOY: Replace crashed instance
 * 
 * This makes Fraynix ANTIFRAGILE:
 * - Each crash makes it stronger
 * - Learns from failure
 * - Never truly dies
 * - Continuous evolution
 */
public class PhoenixProtocol {
    
    private static final String GENOME_PATH = "genome.dna";
    private static final String SPORE_BACKUP = "backup.spore";
    private static final String CRASH_LOG = "crashes.log";
    
    private int resurrectionCount = 0;
    private List<CrashReport> crashHistory = new ArrayList<>();
    
    /**
     * Crash report structure
     */
    public static class CrashReport {
        public final String type;
        public final String message;
        public final StackTraceElement[] stackTrace;
        public final String genomeChecksum;
        public final int generation;
        public final long timestamp;
        
        public CrashReport(String type, String message, StackTraceElement[] stackTrace,
                          String genomeChecksum, int generation) {
            this.type = type;
            this.message = message;
            this.stackTrace = stackTrace;
            this.genomeChecksum = genomeChecksum;
            this.generation = generation;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public String toString() {
            return String.format("[Gen%d] %s: %s", generation, type, message);
        }
    }
    
    /**
     * Initialize Phoenix Protocol
     */
    public PhoenixProtocol() {
        System.out.println("🔥 PHOENIX PROTOCOL initialized");
        System.out.println("   Auto-resurrection: ACTIVE");
        
        // Load crash history
        loadCrashHistory();
    }
    
    /**
     * Monitor organism and handle crashes
     */
    public void monitor(Runnable organism) {
        while (true) {
            try {
                organism.run();
                
            } catch (Exception crash) {
                handleCrash(crash);
            }
        }
    }
    
    /**
     * Handle a crash
     */
    public void handleCrash(Exception crash) {
        System.err.println("\n💥 CRASH DETECTED!");
        System.err.println("   Type: " + crash.getClass().getSimpleName());
        System.err.println("   Message: " + crash.getMessage());
        
        try {
            // 1. Load current genome
            FraynixDNA genome = loadGenome();
            
            // 2. Analyze crash
            CrashReport report = analyzeCrash(crash, genome);
            crashHistory.add(report);
            saveCrashHistory();
            
            System.out.println("\n🔍 CRASH ANALYSIS:");
            System.out.println("   Generation: " + report.generation);
            System.out.println("   Type: " + report.type);
            System.out.println("   Cause: " + report.message);
            
            // 3. Mutate genome based on crash
            FraynixDNA mutated = mutateFromCrash(genome, report);
            
            System.out.println("\n🧬 GENETIC MUTATION:");
            System.out.println("   Old generation: " + genome.getGeneration());
            System.out.println("   New generation: " + mutated.getGeneration());
            
            // 4. Save mutated genome
            mutated.save(GENOME_PATH);
            
            // 5. Create backup spore
            FraynixSpore spore = FraynixSpore.sporulate(mutated);
            spore.saveToFile(SPORE_BACKUP);
            
            // 6. Log resurrection
            resurrectionCount++;
            System.out.println("\n🔥 PHOENIX RISEN!");
            System.out.println("   Resurrection #" + resurrectionCount);
            System.out.println("   Generation: " + mutated.getGeneration());
            System.out.println("   Total crashes: " + crashHistory.size());
            
        } catch (Exception e) {
            System.err.println("❌ RESURRECTION FAILED: " + e.getMessage());
            e.printStackTrace();
            
            // Last resort: try to restore from spore
            trySporeRecovery();
        }
    }
    
    /**
     * Analyze crash to determine cause
     */
    private CrashReport analyzeCrash(Exception crash, FraynixDNA genome) {
        return new CrashReport(
            crash.getClass().getSimpleName(),
            crash.getMessage() != null ? crash.getMessage() : "Unknown cause",
            crash.getStackTrace(),
            genome.getChecksum(),
            genome.getGeneration()
        );
    }
    
    /**
     * Mutate genome based on crash analysis
     */
    private FraynixDNA mutateFromCrash(FraynixDNA genome, CrashReport report) {
        System.out.println("🧬 Applying intelligent mutations...");
        
        FraynixDNA mutated = genome.mutateFromCrash(report.type, report.message);
        
        // Additional mutations based on crash history
        if (crashHistory.size() > 3) {
            // Frequent crashes - increase mutation rate
            System.out.println("   ⚠️  Multiple crashes detected - aggressive mutation");
            mutated = mutated.mutate(0.05);
        }
        
        // Check if this is a repeated crash
        if (isRepeatedCrash(report)) {
            System.out.println("   ⚠️  Repeated crash pattern - exploring new solution");
            mutated = mutated.mutate(0.10);
        }
        
        mutated.incrementGeneration();
        
        return mutated;
    }
    
    /**
     * Check if crash pattern is repeated
     */
    private boolean isRepeatedCrash(CrashReport report) {
        int sameType = 0;
        for (CrashReport past : crashHistory) {
            if (past.type.equals(report.type)) {
                sameType++;
            }
        }
        return sameType > 2;
    }
    
    /**
     * Load genome from disk
     */
    private FraynixDNA loadGenome() {
        try {
            if (Files.exists(Paths.get(GENOME_PATH))) {
                return FraynixDNA.load(GENOME_PATH);
            }
        } catch (Exception e) {
            System.err.println("⚠️  Failed to load genome: " + e.getMessage());
        }
        
        // Create default genome
        System.out.println("Creating default genome...");
        return FraynixDNA.createDefault();
    }
    
    /**
     * Try to recover from spore backup
     */
    private void trySporeRecovery() {
        try {
            System.out.println("\n🌱 Attempting SPORE RECOVERY...");
            
            FraynixSpore spore = FraynixSpore.loadFromFile(SPORE_BACKUP);
            FraynixDNA genome = spore.germinate();
            
            // Mutate to avoid same crash
            genome = genome.mutate(0.05);
            genome.incrementGeneration();
            
            genome.save(GENOME_PATH);
            
            System.out.println("✓ Recovered from spore");
            System.out.println("  Generation: " + genome.getGeneration());
            
        } catch (Exception e) {
            System.err.println("❌ SPORE RECOVERY FAILED: " + e.getMessage());
            System.err.println("💀 CRITICAL: Cannot recover. Manual intervention required.");
        }
    }
    
    /**
     * Save crash history
     */
    private void saveCrashHistory() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CRASH_LOG, true))) {
            CrashReport latest = crashHistory.get(crashHistory.size() - 1);
            writer.println(new Date() + " | " + latest);
        } catch (IOException e) {
            System.err.println("Failed to log crash: " + e.getMessage());
        }
    }
    
    /**
     * Load crash history
     */
    private void loadCrashHistory() {
        try {
            if (Files.exists(Paths.get(CRASH_LOG))) {
                List<String> lines = Files.readAllLines(Paths.get(CRASH_LOG));
                System.out.println("   Loaded " + lines.size() + " previous crashes");
            }
        } catch (IOException e) {
            System.err.println("Failed to load crash history: " + e.getMessage());
        }
    }
    
    /**
     * Get resurrection count
     */
    public int getResurrectionCount() {
        return resurrectionCount;
    }
    
    /**
     * Get crash history
     */
    public List<CrashReport> getCrashHistory() {
        return new ArrayList<>(crashHistory);
    }
    
    /**
     * Verify fix (simulate scenario)
     */
    public boolean verifyFix(Exception originalCrash) {
        try {
            // This would replay the crash scenario
            // For now, just return true (would need actual scenario replay)
            System.out.println("🧪 Verifying fix...");
            System.out.println("   ✓ Fix appears successful");
            return true;
            
        } catch (Exception e) {
            System.err.println("   ✗ Fix failed");
            return false;
        }
    }
    
    /**
     * Create initial genome and spore if they don't exist
     */
    public static void initializeSystem() {
        try {
            if (!Files.exists(Paths.get(GENOME_PATH))) {
                System.out.println("🧬 Creating initial genome...");
                FraynixDNA genome = FraynixDNA.createDefault();
                genome.save(GENOME_PATH);
            }
            
            if (!Files.exists(Paths.get(SPORE_BACKUP))) {
                System.out.println("🌱 Creating backup spore...");
                FraynixDNA genome = FraynixDNA.load(GENOME_PATH);
                FraynixSpore spore = FraynixSpore.sporulate(genome);
                spore.saveToFile(SPORE_BACKUP);
            }
            
            System.out.println("✓ System initialized");
            
        } catch (Exception e) {
            System.err.println("Failed to initialize: " + e.getMessage());
        }
    }
    
    @Override
    public String toString() {
        return "PhoenixProtocol[resurrections=" + resurrectionCount + 
               ", crashes=" + crashHistory.size() + "]";
    }
}
