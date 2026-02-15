package gemini.root;

import fraymus.OllamaSpine;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * BootstrapOS: Self-Initialization System
 * 
 * The system reads its own source code, indexes it into the VectorVault,
 * and becomes self-aware. It can then answer questions about its own
 * implementation by retrieving from its indexed codebase.
 * 
 * This is the foundation of self-contained operation: the system
 * understands itself without external documentation.
 */
public class BootstrapOS {
    
    private final OllamaSpine brain;
    private final VectorVault vault;
    private final Transmudder transmudder;
    private final String sourceRoot;
    
    public BootstrapOS(OllamaSpine brain, VectorVault vault, Transmudder transmudder) {
        this.brain = brain;
        this.vault = vault;
        this.transmudder = transmudder;
        this.sourceRoot = "src/main/java";
    }
    
    /**
     * Initialize the system by reading and indexing its own source code.
     * This makes the system self-aware - it can explain how it works.
     */
    public void initialize() throws Exception {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("   🧠 BOOTSTRAP OS: SELF-INITIALIZATION");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println();
        
        // Phase 1: Scan source directory
        System.out.println("[Phase 1] Scanning source code...");
        List<Path> javaFiles = findJavaFiles(sourceRoot);
        System.out.println("   Found " + javaFiles.size() + " source files");
        System.out.println();
        
        // Phase 2: Index each file
        System.out.println("[Phase 2] Indexing source code into self-knowledge vault...");
        int indexed = 0;
        for (Path file : javaFiles) {
            try {
                indexSourceFile(file);
                indexed++;
                if (indexed % 5 == 0) {
                    System.out.println("   Indexed: " + indexed + "/" + javaFiles.size());
                }
            } catch (Exception e) {
                System.err.println("   Warning: Failed to index " + file + ": " + e.getMessage());
            }
        }
        System.out.println("   Total indexed: " + indexed + " files");
        System.out.println();
        
        // Phase 3: Self-awareness achieved
        System.out.println("[Phase 3] Self-awareness achieved");
        System.out.println("   ✓ System can now explain its own implementation");
        System.out.println("   ✓ Query example: \"How does the Reflector work?\"");
        System.out.println("   ✓ Answer will come from indexed source code");
        System.out.println();
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("   ✅ BOOTSTRAP COMPLETE");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println();
    }
    
    /**
     * Index a single source file into the vault
     */
    private void indexSourceFile(Path file) throws Exception {
        // Read source code
        String sourceCode = Files.readString(file);
        
        // Extract class/package info
        String className = extractClassName(sourceCode);
        String packageName = extractPackageName(sourceCode);
        String fullName = packageName.isEmpty() ? className : packageName + "." + className;
        
        // Clean and chunk
        String cleaned = transmudder.cleanse(sourceCode);
        List<String> chunks = transmudder.chunk(cleaned, 1200, 200);
        
        // Create labeled chunks with source info
        List<String> labeledChunks = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            String label = String.format("[SOURCE: %s chunk %d/%d]\n%s", 
                fullName, i+1, chunks.size(), chunks.get(i));
            labeledChunks.add(label);
        }
        
        // Embed and store
        List<float[]> embeddings = brain.embedBatch(labeledChunks);
        vault.addAndPersist(file.toString(), labeledChunks, embeddings);
    }
    
    /**
     * Find all Java source files recursively
     */
    private List<Path> findJavaFiles(String root) throws IOException {
        List<Path> javaFiles = new ArrayList<>();
        Path rootPath = Paths.get(root);
        
        if (!Files.exists(rootPath)) {
            System.err.println("Warning: Source root not found: " + root);
            return javaFiles;
        }
        
        Files.walk(rootPath)
            .filter(p -> p.toString().endsWith(".java"))
            .forEach(javaFiles::add);
        
        return javaFiles;
    }
    
    /**
     * Extract class name from source code
     */
    private String extractClassName(String source) {
        String[] lines = source.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("public class ") || line.startsWith("class ")) {
                String[] parts = line.split("\\s+");
                for (int i = 0; i < parts.length - 1; i++) {
                    if (parts[i].equals("class")) {
                        String className = parts[i + 1];
                        // Remove generic parameters or implements/extends
                        className = className.split("[<{]")[0];
                        return className;
                    }
                }
            }
        }
        return "Unknown";
    }
    
    /**
     * Extract package name from source code
     */
    private String extractPackageName(String source) {
        String[] lines = source.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("package ")) {
                String pkg = line.substring("package ".length());
                pkg = pkg.replace(";", "").trim();
                return pkg;
            }
        }
        return "";
    }
    
    /**
     * Index additional external documentation
     */
    public void indexDocumentation(String docPath) throws Exception {
        System.out.println("Indexing external documentation: " + docPath);
        
        Path path = Paths.get(docPath);
        if (Files.isDirectory(path)) {
            // Index all files in directory
            Files.walk(path)
                .filter(p -> p.toString().matches(".*\\.(md|txt|java|py|js|ts|go|rs)$"))
                .forEach(file -> {
                    try {
                        indexDocumentationFile(file);
                    } catch (Exception e) {
                        System.err.println("Failed to index " + file + ": " + e.getMessage());
                    }
                });
        } else if (Files.exists(path)) {
            // Index single file
            indexDocumentationFile(path);
        }
        
        System.out.println("Documentation indexing complete");
    }
    
    /**
     * Index a single documentation file
     */
    private void indexDocumentationFile(Path file) throws Exception {
        String content = Files.readString(file);
        String cleaned = transmudder.cleanse(content);
        List<String> chunks = transmudder.chunk(cleaned, 1200, 200);
        
        // Label with source
        List<String> labeled = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            labeled.add(String.format("[DOC: %s chunk %d]\n%s", 
                file.getFileName(), i+1, chunks.get(i)));
        }
        
        List<float[]> embeddings = brain.embedBatch(labeled);
        vault.addAndPersist(file.toString(), labeled, embeddings);
    }
    
    /**
     * Query the system about its own implementation
     */
    public String queryOwnImplementation(String question) throws Exception {
        RagEngine rag = new RagEngine(brain, vault);
        String context = rag.buildContext(question, 6, 4000);
        
        List<OllamaSpine.Msg> msgs = List.of(
            new OllamaSpine.Msg("system", 
                "You are explaining your own source code. " +
                "Use the provided source code context to answer accurately. " +
                "Cite specific files and line ranges when possible."),
            new OllamaSpine.Msg("user", 
                "Context from source code:\n" + context + "\n\nQuestion: " + question)
        );
        
        return brain.chatOnce(msgs, null, Map.of("temperature", 0.2));
    }
    
    /**
     * Get statistics about indexed source code
     */
    public BootstrapStats getStats() {
        return new BootstrapStats(
            vault.size(),
            vault.size() / 3 // Rough estimate of files (assuming ~3 chunks per file)
        );
    }
    
    public static class BootstrapStats {
        public final int totalChunks;
        public final int estimatedFiles;
        
        public BootstrapStats(int chunks, int files) {
            this.totalChunks = chunks;
            this.estimatedFiles = files;
        }
        
        @Override
        public String toString() {
            return String.format("Bootstrap Stats: %d chunks from ~%d files", 
                totalChunks, estimatedFiles);
        }
    }
}
