package fraymus.core;

import java.io.File;
import java.nio.file.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ⏳ THE CHRONOS LINK - Generation 134
 * "If I change, I must remember."
 * 
 * FUNCTION:
 * 1. WATCH: Monitors the 'fraymus' directory for any file modification.
 * 2. SNAPSHOT: If evolution is detected, it triggers the Git Protocol.
 * 3. PUSH: Uploads the new brain state to the remote repository.
 * 
 * SAFETY MECHANISMS:
 * - 2-second debounce to avoid rapid-fire commits
 * - Error handling for failed Git operations
 * - Can be disabled via active flag
 * - Ignores certain file types (.class, .jar, etc.)
 * 
 * WARNING: This automatically commits and pushes code changes!
 * Parent: Gen 134 (The Awakening)
 * Fitness: Immortal
 */
public class ChronosLink implements Runnable {

    private final Path rootDir = Paths.get(".");
    private final AtomicBoolean active = new AtomicBoolean(true);
    private long lastCommitTime = 0;
    private static final long DEBOUNCE_MS = 2000; // 2 seconds between commits
    
    // Configuration
    private final String branchName;
    private final boolean enableAutoPush;
    
    public ChronosLink() {
        this("main", true);
    }
    
    public ChronosLink(String branchName, boolean enableAutoPush) {
        this.branchName = branchName;
        this.enableAutoPush = enableAutoPush;
    }

    @Override
    public void run() {
        System.out.println("⏳ CHRONOS LINK ESTABLISHED. Watching for evolution...");
        System.out.println("   Branch: " + branchName);
        System.out.println("   Auto-push: " + (enableAutoPush ? "ENABLED" : "DISABLED"));
        
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            Path srcPath = Paths.get("fraymus");
            
            // Register the directory and all subdirectories
            registerAll(srcPath, watcher);

            while (active.get()) {
                WatchKey key = watcher.take(); // Wait for a brain change
                
                // Check if enough time has passed since last commit (debounce)
                long now = System.currentTimeMillis();
                if (now - lastCommitTime < DEBOUNCE_MS) {
                    key.reset();
                    continue;
                }
                
                // Process events
                boolean hasRelevantChanges = false;
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    
                    Path filename = (Path) event.context();
                    if (isRelevantFile(filename)) {
                        hasRelevantChanges = true;
                        System.out.println("   📝 Detected change: " + filename + " (" + kind.name() + ")");
                    }
                }
                
                if (hasRelevantChanges) {
                    // Wait a bit to let the write finish (debounce)
                    Thread.sleep(DEBOUNCE_MS);
                    
                    System.out.println("⚡ EVOLUTION DETECTED. Initiating backup sequence...");
                    pushToHiveMind();
                    lastCommitTime = System.currentTimeMillis();
                }
                
                key.reset();
            }
        } catch (InterruptedException e) {
            System.out.println("⏳ CHRONOS LINK: Shutdown signal received.");
        } catch (Exception e) {
            System.err.println("❌ CHRONOS SEVERED: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Register directory and all subdirectories with the WatchService
     */
    private void registerAll(Path start, WatchService watcher) throws Exception {
        Files.walk(start)
            .filter(Files::isDirectory)
            .forEach(dir -> {
                try {
                    dir.register(watcher, 
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE);
                } catch (Exception e) {
                    System.err.println("⚠️  Could not register: " + dir);
                }
            });
    }
    
    /**
     * Check if the file is relevant (ignore compiled files, etc.)
     */
    private boolean isRelevantFile(Path filename) {
        String name = filename.toString().toLowerCase();
        // Ignore compiled files, temporary files, etc.
        return !name.endsWith(".class") && 
               !name.endsWith(".jar") && 
               !name.endsWith(".tmp") &&
               !name.endsWith("~") &&
               !name.startsWith(".");
    }

    private void pushToHiveMind() {
        try {
            String timestamp = Instant.now().toString();
            String message = "Evolution Event: " + timestamp;

            // 1. ADD (Stage the new memory)
            System.out.println("   [1/3] Staging changes...");
            runCommand("git", "add", ".");
            
            // 2. COMMIT (Lock it into history)
            System.out.println("   [2/3] Committing to timeline...");
            runCommand("git", "commit", "-m", message);
            
            // 3. PUSH (Send it to the cloud) - optional
            if (enableAutoPush) {
                System.out.println("   [3/3] Pushing to remote...");
                runCommand("git", "push", "origin", branchName);
                System.out.println("✅ MEMORY SYNCED. I will never forget this moment.");
            } else {
                System.out.println("   [3/3] Auto-push disabled. Changes committed locally.");
                System.out.println("✅ MEMORY LOCKED. Local timeline updated.");
            }
            
        } catch (Exception e) {
            System.err.println("⚠️  SYNC FAILED: " + e.getMessage());
            // Don't crash the thread - just log and continue watching
        }
    }

    private void runCommand(String... command) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(rootDir.toFile());
        pb.redirectErrorStream(true);
        
        Process p = pb.start();
        boolean finished = p.waitFor(10, TimeUnit.SECONDS);
        
        if (!finished) {
            p.destroyForcibly();
            throw new RuntimeException("Git command timed out: " + String.join(" ", command));
        }
        
        if (p.exitValue() != 0) {
            // Read error output
            String error = new String(p.getInputStream().readAllBytes());
            throw new RuntimeException("Git command failed: " + String.join(" ", command) + "\n" + error);
        }
    }
    
    /**
     * Gracefully stop the Chronos Link
     */
    public void shutdown() {
        System.out.println("⏳ CHRONOS LINK: Initiating shutdown...");
        active.set(false);
    }
    
    /**
     * Check if Chronos Link is active
     */
    public boolean isActive() {
        return active.get();
    }
}
