package gemini.root;

import com.google.gson.*;
import fraymus.OllamaSpine;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * ToolRouter: Executes tools based on JSON specifications
 */
public class ToolRouter {
    
    private final VectorVault vault;
    private final Transmudder soul;
    private final OllamaSpine brain;
    
    public static class ToolResult {
        public String tool;
        public String output;
        
        public ToolResult(String tool, String output) {
            this.tool = tool;
            this.output = output;
        }
    }
    
    public ToolRouter(VectorVault vault, Transmudder soul, OllamaSpine brain) {
        this.vault = vault;
        this.soul = soul;
        this.brain = brain;
    }
    
    /**
     * Run a tool with given arguments
     */
    public ToolResult run(String tool, JsonObject args) {
        try {
            switch (tool) {
                case "calc":
                    return calc(args);
                case "memory_search":
                    return memorySearch(args);
                case "list_files":
                    return listFiles(args);
                case "write_file":
                    return writeFile(args);
                case "index_path":
                    return indexPath(args);
                default:
                    return new ToolResult(tool, "Unknown tool: " + tool);
            }
        } catch (Exception e) {
            return new ToolResult(tool, "Error: " + e.getMessage());
        }
    }
    
    private ToolResult calc(JsonObject args) {
        String expr = args.get("expression").getAsString();
        
        // Simple expression evaluator
        try {
            // For safety, only allow basic arithmetic
            expr = expr.replaceAll("[^0-9+\\-*/().\\s]", "");
            
            // Use JavaScript engine for evaluation (if available)
            javax.script.ScriptEngineManager mgr = new javax.script.ScriptEngineManager();
            javax.script.ScriptEngine engine = mgr.getEngineByName("JavaScript");
            Object result = engine.eval(expr);
            
            return new ToolResult("calc", "Result: " + result);
        } catch (Exception e) {
            return new ToolResult("calc", "Calculation error: " + e.getMessage());
        }
    }
    
    private ToolResult memorySearch(JsonObject args) {
        String query = args.get("query").getAsString();
        int limit = args.has("limit") ? args.get("limit").getAsInt() : 5;
        
        // Search hippocampus
        List<String> results = new ArrayList<>();
        for (String block : Hippocampus.chain) {
            if (block.toLowerCase().contains(query.toLowerCase())) {
                results.add(block);
                if (results.size() >= limit) break;
            }
        }
        
        if (results.isEmpty()) {
            return new ToolResult("memory_search", "No memories found for: " + query);
        }
        
        StringBuilder output = new StringBuilder("Found " + results.size() + " memories:\n");
        for (String r : results) {
            output.append("- ").append(r).append("\n");
        }
        
        return new ToolResult("memory_search", output.toString());
    }
    
    private ToolResult listFiles(JsonObject args) {
        String path = args.get("path").getAsString();
        int limit = args.has("limit") ? args.get("limit").getAsInt() : 20;
        
        try {
            File dir = new File(path);
            if (!dir.exists() || !dir.isDirectory()) {
                return new ToolResult("list_files", "Not a directory: " + path);
            }
            
            File[] files = dir.listFiles();
            if (files == null) {
                return new ToolResult("list_files", "Cannot list: " + path);
            }
            
            StringBuilder output = new StringBuilder("Files in " + path + ":\n");
            int count = 0;
            for (File f : files) {
                if (count >= limit) break;
                output.append(f.isDirectory() ? "[DIR] " : "[FILE] ").append(f.getName()).append("\n");
                count++;
            }
            
            return new ToolResult("list_files", output.toString());
        } catch (Exception e) {
            return new ToolResult("list_files", "Error: " + e.getMessage());
        }
    }
    
    private ToolResult writeFile(JsonObject args) {
        String path = args.get("path").getAsString();
        String content = args.get("content").getAsString();
        boolean overwrite = args.has("overwrite") && args.get("overwrite").getAsBoolean();
        
        try {
            File f = new File(path);
            if (f.exists() && !overwrite) {
                return new ToolResult("write_file", "File exists (use overwrite=true): " + path);
            }
            
            Files.writeString(f.toPath(), content);
            return new ToolResult("write_file", "Written: " + path + " (" + content.length() + " chars)");
        } catch (Exception e) {
            return new ToolResult("write_file", "Error: " + e.getMessage());
        }
    }
    
    private ToolResult indexPath(JsonObject args) {
        String path = args.get("path").getAsString();
        int chunkSize = args.has("chunkSize") ? args.get("chunkSize").getAsInt() : 1200;
        int overlap = args.has("overlap") ? args.get("overlap").getAsInt() : 200;
        
        try {
            File f = new File(path);
            if (!f.exists()) {
                return new ToolResult("index_path", "Not found: " + path);
            }
            
            if (f.isDirectory()) {
                return indexDirectory(f, chunkSize, overlap);
            } else {
                return indexFile(f, chunkSize, overlap);
            }
        } catch (Exception e) {
            return new ToolResult("index_path", "Error: " + e.getMessage());
        }
    }
    
    private ToolResult indexDirectory(File dir, int chunkSize, int overlap) throws Exception {
        int totalFiles = 0;
        int totalChunks = 0;
        
        File[] files = dir.listFiles();
        if (files == null) {
            return new ToolResult("index_path", "Cannot list directory");
        }
        
        for (File f : files) {
            if (f.isFile() && f.getName().endsWith(".txt")) {
                ToolResult result = indexFile(f, chunkSize, overlap);
                totalFiles++;
                // Extract chunk count from result
                if (result.output.contains("chunks")) {
                    String[] parts = result.output.split(" ");
                    for (int i = 0; i < parts.length; i++) {
                        if (parts[i].equals("chunks") && i > 0) {
                            try {
                                totalChunks += Integer.parseInt(parts[i-1]);
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }
        }
        
        return new ToolResult("index_path", 
            "Indexed " + totalFiles + " files, " + totalChunks + " chunks | vault=" + vault.size());
    }
    
    private ToolResult indexFile(File f, int chunkSize, int overlap) throws Exception {
        String raw = soul.readFileToText(f.getAbsolutePath());
        String clean = soul.cleanse(raw);
        
        if (clean.isBlank()) {
            return new ToolResult("index_path", "Empty file: " + f.getName());
        }
        
        List<String> chunks = soul.chunk(clean, chunkSize, overlap);
        List<float[]> vectors = brain.embedBatch(chunks);
        
        if (vectors.size() != chunks.size()) {
            return new ToolResult("index_path", "Embedding mismatch");
        }
        
        vault.addAndPersist(f.getName(), chunks, vectors);
        
        return new ToolResult("index_path", 
            "Indexed " + f.getName() + ": " + chunks.size() + " chunks");
    }
}
