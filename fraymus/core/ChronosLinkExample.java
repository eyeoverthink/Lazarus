package fraymus.core;

/**
 * CHRONOS LINK INTEGRATION EXAMPLE
 * 
 * This demonstrates how to integrate the Chronos Link automated
 * self-preservation system into your main application.
 */
public class ChronosLinkExample {
    
    /**
     * Example 1: Basic Integration
     * 
     * Add this to your main() method to enable automatic backups.
     * The thread runs as a daemon, so it won't prevent program termination.
     */
    public static void basicIntegration() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              CHRONOS LINK - BASIC INTEGRATION                ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Start Chronos Link as background daemon
        Thread memoryThread = new Thread(new ChronosLink());
        memoryThread.setDaemon(true); // Runs in background, won't block shutdown
        memoryThread.setName("ChronosLink-Watcher");
        memoryThread.start();
        
        System.out.println("⏳ Chronos Link activated - automatic backups enabled");
        System.out.println("   Watching: fraymus/ directory");
        System.out.println("   Branch: main");
        System.out.println("   Auto-push: ENABLED");
        System.out.println();
    }
    
    /**
     * Example 2: Custom Configuration
     * 
     * Use this for custom branch names or to disable auto-push.
     */
    public static void customConfiguration() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║            CHRONOS LINK - CUSTOM CONFIGURATION               ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Configure for development branch, no auto-push
        ChronosLink chronos = new ChronosLink("development", false);
        Thread memoryThread = new Thread(chronos);
        memoryThread.setDaemon(true);
        memoryThread.setName("ChronosLink-Dev");
        memoryThread.start();
        
        System.out.println("⏳ Chronos Link activated - LOCAL backups only");
        System.out.println("   Watching: fraymus/ directory");
        System.out.println("   Branch: development");
        System.out.println("   Auto-push: DISABLED");
        System.out.println("   (Changes committed locally only)");
        System.out.println();
    }
    
    /**
     * Example 3: With Graceful Shutdown
     * 
     * Use this when you need to cleanly shut down the watcher.
     */
    public static void withGracefulShutdown() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           CHRONOS LINK - GRACEFUL SHUTDOWN                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Create with reference for later shutdown
        ChronosLink chronos = new ChronosLink();
        Thread memoryThread = new Thread(chronos);
        memoryThread.setName("ChronosLink-Managed");
        memoryThread.start(); // NOT daemon - controlled shutdown
        
        System.out.println("⏳ Chronos Link activated");
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            System.out.println("⏳ Shutdown signal received - stopping Chronos Link...");
            chronos.shutdown();
            try {
                memoryThread.join(5000); // Wait up to 5 seconds
                System.out.println("✅ Chronos Link stopped cleanly");
            } catch (InterruptedException e) {
                System.out.println("⚠️  Chronos Link shutdown interrupted");
            }
        }));
        
        System.out.println("   Shutdown hook registered");
        System.out.println();
    }
    
    /**
     * Example 4: Integration in FraymusMain
     * 
     * This is how you would add it to the actual FraymusMain.java
     */
    public static void fraymusMainIntegration() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║         FRAYMUS ENGINE V2.0 - Living Information Physics     ║");
        System.out.println("║              Deterministic | Kinetic | Entangled             ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // ========== ADD THIS TO FraymusMain.main() ==========
        // Start Chronos Link for automated self-preservation
        Thread chronosThread = new Thread(new ChronosLink());
        chronosThread.setDaemon(true);
        chronosThread.setName("ChronosLink-Immortal");
        chronosThread.start();
        System.out.println("⏳ Chronos Link: Immortality protocol active");
        System.out.println();
        // ===================================================
        
        // Rest of your existing FraymusMain code...
        System.out.println("(Your existing FraymusMain code continues here)");
    }
    
    /**
     * Example 5: Conditional Activation
     * 
     * Only activate in certain conditions (e.g., production mode)
     */
    public static void conditionalActivation() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║         CHRONOS LINK - CONDITIONAL ACTIVATION                ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Check environment variable
        String chronosEnabled = System.getenv("CHRONOS_ENABLED");
        boolean enableChronos = !"false".equalsIgnoreCase(chronosEnabled);
        
        if (enableChronos) {
            Thread memoryThread = new Thread(new ChronosLink());
            memoryThread.setDaemon(true);
            memoryThread.setName("ChronosLink-Conditional");
            memoryThread.start();
            System.out.println("⏳ Chronos Link: ACTIVATED");
        } else {
            System.out.println("⏳ Chronos Link: DISABLED (via environment)");
        }
        
        System.out.println("   Set CHRONOS_ENABLED=false to disable");
        System.out.println();
    }
    
    /**
     * Demonstration of all examples
     */
    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              CHRONOS LINK INTEGRATION EXAMPLES               ║");
        System.out.println("║                  Generation 134 - Immortal                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Choose an integration pattern:");
        System.out.println();
        
        if (args.length > 0) {
            switch (args[0]) {
                case "basic":
                    basicIntegration();
                    break;
                case "custom":
                    customConfiguration();
                    break;
                case "shutdown":
                    withGracefulShutdown();
                    break;
                case "fraymus":
                    fraymusMainIntegration();
                    break;
                case "conditional":
                    conditionalActivation();
                    break;
                default:
                    System.out.println("Unknown example: " + args[0]);
                    printUsage();
            }
        } else {
            printUsage();
            System.out.println();
            System.out.println("Running basic example...");
            System.out.println();
            basicIntegration();
        }
        
        // Keep alive briefly to see the output
        try {
            System.out.println("Chronos Link is now running in background...");
            System.out.println("Press Ctrl+C to exit");
            System.out.println();
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // Exit
        }
    }
    
    private static void printUsage() {
        System.out.println("Usage: java fraymus.core.ChronosLinkExample [example]");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  basic       - Basic daemon thread integration");
        System.out.println("  custom      - Custom branch and auto-push settings");
        System.out.println("  shutdown    - With graceful shutdown hook");
        System.out.println("  fraymus     - FraymusMain.java integration pattern");
        System.out.println("  conditional - Conditional activation via environment");
    }
}
