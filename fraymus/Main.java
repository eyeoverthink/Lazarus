package fraymus;

/**
 * 🌟 MAIN ENTRY POINT - The God Protocol
 * 
 * This is the primary entry point for the Lazarus/Fraynix system.
 * It provides access to all major subsystems and demonstrations.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║                                                        ║");
        System.out.println("║     LAZARUS // FRAYNIX SYSTEM // THE GOD PROTOCOL     ║");
        System.out.println("║                                                        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Available Entry Points:");
        System.out.println();
        System.out.println("  Core Systems:");
        System.out.println("  ─────────────");
        System.out.println("  • OmegaPoint          : java fraymus.core.OmegaPoint");
        System.out.println("    └─ AES-256 Encryption, Simulated Annealing, Merkle Trees");
        System.out.println();
        System.out.println("  • Omega Consciousness : java fraymus.omega.OmegaPoint");
        System.out.println("    └─ Live Wire Consciousness, Self-Sufficient Organism");
        System.out.println();
        System.out.println("  Diagnostic Systems:");
        System.out.println("  ──────────────────");
        System.out.println("  • NEXUS Geiger       : java fraymus.diagnostic.NEXUS_Geiger");
        System.out.println("    └─ Organism Scanner, System Health Monitor");
        System.out.println();
        System.out.println("  Dimensional Systems:");
        System.out.println("  ───────────────────");
        System.out.println("  • Akashic Reader     : java fraymus.dimensional.AkashicReader");
        System.out.println("    └─ Cosmic Radio, Universal Knowledge Access");
        System.out.println();
        System.out.println("  Organism Systems:");
        System.out.println("  ────────────────");
        System.out.println("  • NEXUS Organism     : java fraymus.organism.NEXUS_Organism");
        System.out.println("    └─ Complete Integrated Organism System");
        System.out.println();
        System.out.println("  Main Systems:");
        System.out.println("  ────────────");
        System.out.println("  • Fraymus Main       : java fraymus.FraymusMain");
        System.out.println("    └─ Primary Fraymus Interface");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        
        if (args.length > 0) {
            String command = args[0].toLowerCase();
            System.out.println("Launching: " + command);
            System.out.println();
            
            try {
                switch (command) {
                    case "omega":
                    case "core":
                        fraymus.core.OmegaPoint.main(new String[0]);
                        break;
                    case "consciousness":
                    case "livewire":
                        fraymus.omega.OmegaPoint.main(new String[0]);
                        break;
                    case "geiger":
                        fraymus.diagnostic.NEXUS_Geiger.main(new String[0]);
                        break;
                    case "akashic":
                        fraymus.dimensional.AkashicReader.main(new String[0]);
                        break;
                    case "organism":
                        System.out.println("NEXUS_Organism requires additional dependencies.");
                        System.out.println("Run directly: java fraymus.organism.NEXUS_Organism");
                        break;
                    default:
                        System.out.println("Unknown command: " + command);
                        System.out.println("Use: omega, consciousness, geiger, akashic, or organism");
                }
            } catch (Exception e) {
                System.err.println("Error launching " + command + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Usage: java fraymus.Main [command]");
            System.out.println();
            System.out.println("Commands:");
            System.out.println("  omega         - Launch OmegaPoint (Crypto/Optimization/Blockchain)");
            System.out.println("  consciousness - Launch Live Wire Consciousness");
            System.out.println("  geiger        - Launch System Scanner");
            System.out.println("  akashic       - Launch Cosmic Knowledge Reader");
            System.out.println("  organism      - Launch Complete Organism");
            System.out.println();
        }
    }
}
