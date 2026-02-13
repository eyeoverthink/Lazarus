package gemini.root;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;

/**
 * 🧠 THE HIPPOCAMPUS
 * Role: Manages the Chain of Memory.
 * Function: Save to Disk, Load from Disk.
 * 
 * Generation: 195 - The Universal Interface
 * Philosophy: "Remember everything. Forget nothing."
 */
public class Hippocampus {
    
    public static List<GenesisBlock> chain = new ArrayList<>();
    public static String lastHash = "0"; // Genesis Seed
    private static final String MEMORY_DIR = "memory";

    /**
     * REMEMBER: Create a new block and save it.
     */
    public static void commitMemory(String type, String data) {
        // 1. Create Block
        GenesisBlock newBlock = new GenesisBlock(lastHash, type, data);
        chain.add(newBlock);
        lastHash = newBlock.hash;

        // 2. Crystallize to Disk (Local Persistence)
        saveToDisk(newBlock);
        
        System.out.println(">>> [HIPPOCAMPUS] Memory committed: " + newBlock);
    }

    private static void saveToDisk(GenesisBlock block) {
        try {
            // Ensure directory exists
            new File(MEMORY_DIR).mkdirs();

            // Filename: "BLOCK_[Timestamp]_[Hash].genesis"
            String filename = MEMORY_DIR + "/BLOCK_" + block.timestamp + 
                            "_" + block.hash.substring(0, 8) + ".genesis";
            
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(block);
            oos.close();
            
            System.out.println(">>> [HIPPOCAMPUS] Block Crystallized: " + filename);
        } catch (Exception e) {
            System.err.println(">>> [HIPPOCAMPUS] Amnesia Error: " + e.getMessage());
        }
    }

    /**
     * WAKE UP: Load the last state from the file system.
     */
    public static void recall() {
        try {
            File folder = new File(MEMORY_DIR);
            if (!folder.exists()) {
                System.out.println(">>> [HIPPOCAMPUS] No previous memories found. Starting fresh.");
                return;
            }

            File[] files = folder.listFiles((dir, name) -> name.endsWith(".genesis"));
            if (files == null || files.length == 0) {
                System.out.println(">>> [HIPPOCAMPUS] No memory blocks found.");
                return;
            }

            // Sort by timestamp (embedded in filename)
            Arrays.sort(files, Comparator.comparingLong(f -> {
                String name = f.getName();
                try {
                    String timestamp = name.substring(6, name.indexOf("_", 6));
                    return Long.parseLong(timestamp);
                } catch (Exception e) {
                    return 0L;
                }
            }));

            // Load all blocks
            int loaded = 0;
            for (File file : files) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    GenesisBlock block = (GenesisBlock) ois.readObject();
                    if (block.isValid()) {
                        chain.add(block);
                        lastHash = block.hash;
                        loaded++;
                    } else {
                        System.err.println(">>> [HIPPOCAMPUS] Corrupted block: " + file.getName());
                    }
                }
            }

            System.out.println(">>> [HIPPOCAMPUS] Previous Life Recalled: " + loaded + " blocks loaded.");
            System.out.println(">>> [HIPPOCAMPUS] Last Hash: " + lastHash.substring(0, 16) + "...");
            
        } catch (Exception e) {
            System.err.println(">>> [HIPPOCAMPUS] Recall Failed: " + e.getMessage());
        }
    }

    /**
     * Get total number of memories
     */
    public static int getMemoryCount() {
        return chain.size();
    }

    /**
     * Get recent memories (last N blocks)
     */
    public static List<GenesisBlock> getRecentMemories(int count) {
        int size = chain.size();
        int start = Math.max(0, size - count);
        return new ArrayList<>(chain.subList(start, size));
    }

    /**
     * Verify entire chain integrity
     */
    public static boolean verifyChain() {
        for (int i = 1; i < chain.size(); i++) {
            GenesisBlock current = chain.get(i);
            GenesisBlock previous = chain.get(i - 1);
            
            if (!current.isValid()) {
                System.err.println(">>> [HIPPOCAMPUS] Invalid block at " + i);
                return false;
            }
            
            if (!current.previousHash.equals(previous.hash)) {
                System.err.println(">>> [HIPPOCAMPUS] Chain broken at " + i);
                return false;
            }
        }
        return true;
    }
}
