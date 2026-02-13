package gemini.root;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Transmudder: Text extraction and chunking utility
 */
public class Transmudder {
    
    /**
     * Read a file to text
     */
    public String readFileToText(String path) throws IOException {
        return Files.readString(Path.of(path));
    }
    
    /**
     * Cleanse text (remove excessive whitespace, etc.)
     */
    public String cleanse(String raw) {
        if (raw == null) return "";
        
        // Remove excessive whitespace
        String clean = raw.replaceAll("\\s+", " ");
        clean = clean.trim();
        
        return clean;
    }
    
    /**
     * Chunk text into overlapping segments
     */
    public List<String> chunk(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        
        if (text == null || text.isEmpty()) {
            return chunks;
        }
        
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));
            start += chunkSize - overlap;
        }
        
        return chunks;
    }
}
