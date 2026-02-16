package gemini.root;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Hippocampus: Long-term memory persistence
 * Stores conversation blocks as a simple append-only log
 */
public class Hippocampus {
    
    private static final String MEMORY_FILE = "fraymus_hippocampus.log";
    public static List<String> chain = new ArrayList<>();
    
    /**
     * Load memory from disk on startup
     */
    public static void recall() {
        try {
            File f = new File(MEMORY_FILE);
            if (f.exists()) {
                List<String> lines = Files.readAllLines(f.toPath());
                chain.addAll(lines);
                System.out.println(">>> [HIPPOCAMPUS] Recalled " + chain.size() + " memory blocks");
            } else {
                System.out.println(">>> [HIPPOCAMPUS] No prior memory found");
            }
        } catch (IOException e) {
            System.err.println(">>> [HIPPOCAMPUS] Recall failed: " + e.getMessage());
        }
    }
    
    /**
     * Commit a new memory block
     */
    public static void commitMemory(String type, String content) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        // Escape newlines to prevent log corruption
        String escapedContent = content.replace("\n", "\\n").replace("\r", "\\r");
        String block = "[" + timestamp + "|" + type + "] " + escapedContent;
        chain.add(block);
        
        // Persist asynchronously (consider using ExecutorService for production)
        new Thread(() -> {
            synchronized (MEMORY_FILE) {  // Prevent concurrent writes
                try (FileWriter fw = new FileWriter(MEMORY_FILE, true)) {
                    fw.write(block + "\n");
                } catch (IOException e) {
                    System.err.println(">>> [HIPPOCAMPUS] Persist failed: " + e.getMessage());
                }
            }
        }).start();
    }
}
