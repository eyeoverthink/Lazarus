package gemini.root;

import java.nio.file.*;
import java.util.*;

/**
 * 🧪 THE TRANSMUDDER
 * Role: The Alchemist.
 * Function: Reads Raw Files (PDF/Txt) -> Cleans them -> Injects into Brain.
 * 
 * Generation: 195 - The Universal Interface
 */
public class Transmudder {

    private StringBuilder livingContext = new StringBuilder();
    private List<String> ingestedFiles = new ArrayList<>();

    /**
     * ABSORB REALITY
     * Ingests a file and adds it to the AI's short-term memory.
     */
    public void transmuteFile(String filePath) {
        try {
            // Check if already ingested
            if (ingestedFiles.contains(filePath)) {
                System.out.println(">>> [TRANSMUDDER] File already digested: " + filePath);
                return;
            }

            String content = Files.readString(Path.of(filePath));
            
            // 1. CLEANSE (Remove binary noise, extra spaces)
            String clean = content.replaceAll("[^\\x20-\\x7E\\n\\r\\t]", "")
                                 .replaceAll("\\s+", " ")
                                 .trim();
            
            // 2. COMPRESS (Keep only the essence - simplified for now)
            int maxLength = 2000;
            if (clean.length() > maxLength) {
                clean = clean.substring(0, maxLength) + "...[TRUNCATED]";
            }

            // 3. FUSE (Add to context)
            livingContext.append(" [FILE: ").append(filePath).append("]: ")
                        .append(clean).append(" | ");
            
            ingestedFiles.add(filePath);
            
            System.out.println(">>> [TRANSMUDDER] File digested: " + filePath + 
                             " (" + content.length() + " -> " + clean.length() + " bytes)");

        } catch (Exception e) {
            System.err.println(">>> [TRANSMUDDER] Failed to transmute: " + e.getMessage());
        }
    }

    /**
     * Get the accumulated context essence
     */
    public String getEssence() {
        return livingContext.toString();
    }

    /**
     * Clear all context (memory wipe)
     */
    public void purge() {
        livingContext = new StringBuilder();
        ingestedFiles.clear();
        System.out.println(">>> [TRANSMUDDER] Context purged.");
    }

    /**
     * Get list of ingested files
     */
    public List<String> getIngestedFiles() {
        return new ArrayList<>(ingestedFiles);
    }

    /**
     * Get total context size in characters
     */
    public int getContextSize() {
        return livingContext.length();
    }
}
