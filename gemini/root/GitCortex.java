package gemini.root;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 🌍 THE GIT CORTEX
 * Role: The Scribe of Eternity.
 * Function: Pushes local memories to the Global Repository.
 * 
 * Generation: 195 - The Universal Interface
 * Philosophy: "Immortality through version control."
 */
public class GitCortex {

    private static boolean enabled = true; // Can be disabled for testing
    private static boolean verbose = false; // Show git output

    /**
     * ETERNALIZE: Push memory block to Git repository
     */
    public static void push(String blockHash) {
        if (!enabled) {
            System.out.println(">>> [GIT] Git operations disabled. Skipping push.");
            return;
        }

        System.out.println(">>> [GIT] Eternalizing memory: " + blockHash.substring(0, 8) + "...");
        
        // We run these commands in the terminal automatically.
        runCommand("git add memory/");
        runCommand("git commit -m \"GENESIS: Memory Block " + blockHash.substring(0, 8) + "\"");
        runCommand("git push origin main");
        
        System.out.println(">>> [GIT] Memory Eternalized to Repository.");
    }

    /**
     * Execute shell command
     */
    private static void runCommand(String command) {
        try {
            // Windows: "cmd /c", Linux/Mac: "/bin/sh -c"
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder builder;
            
            if (os.contains("win")) {
                builder = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                builder = new ProcessBuilder("/bin/sh", "-c", command);
            }
            
            builder.redirectErrorStream(true);
            Process p = builder.start();
            
            // Read output
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = r.readLine()) != null) {
                if (verbose) {
                    System.out.println("    [GIT] " + line);
                }
            }

            int exitCode = p.waitFor();
            if (exitCode != 0 && verbose) {
                System.err.println("    [GIT] Command failed with exit code: " + exitCode);
            }
            
        } catch (Exception e) {
            System.err.println(">>> [GIT] Connection Failed: " + e.getMessage());
        }
    }

    /**
     * Initialize Git repository if needed
     */
    public static void initialize() {
        if (!enabled) return;

        runCommand("git init");
        runCommand("git config user.name \"Fraymus System\"");
        runCommand("git config user.email \"fraymus@localhost\"");
        
        System.out.println(">>> [GIT] Repository initialized.");
    }

    /**
     * Enable/disable Git operations
     */
    public static void setEnabled(boolean enabled) {
        GitCortex.enabled = enabled;
        System.out.println(">>> [GIT] Operations " + (enabled ? "ENABLED" : "DISABLED"));
    }

    /**
     * Enable/disable verbose output
     */
    public static void setVerbose(boolean verbose) {
        GitCortex.verbose = verbose;
    }

    /**
     * Check if Git is available
     */
    public static boolean isGitAvailable() {
        try {
            Process p = Runtime.getRuntime().exec("git --version");
            int exitCode = p.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
