package gemini.root;

import fraymus.OllamaSpine;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * LibraryAbstractor: Dynamic Library Learning via Transmudder
 * 
 * THE KEY INNOVATION: Learn any library by reading its documentation.
 * 
 * Traditional AI: Can only use libraries it was trained on.
 * Lazarus OS: Read docs → Index → Use through natural language.
 * 
 * Example:
 *   1. Point at NumPy documentation
 *   2. Transmudder reads and chunks it
 *   3. Index into VectorVault
 *   4. Ask: "How do I create a matrix?"
 *   5. RAG retrieves NumPy docs
 *   6. LLM generates correct NumPy code
 *   7. WORKS - without any NumPy-specific training!
 * 
 * This makes the system truly self-contained: it can learn
 * ANY library just by reading documentation.
 */
public class LibraryAbstractor {
    
    private final OllamaSpine brain;
    private final VectorVault vault;
    private final Transmudder transmudder;
    private final Map<String, LibraryMetadata> learnedLibraries;
    
    public LibraryAbstractor(OllamaSpine brain, VectorVault vault, Transmudder transmudder) {
        this.brain = brain;
        this.vault = vault;
        this.transmudder = transmudder;
        this.learnedLibraries = new HashMap<>();
    }
    
    /**
     * Learn a library by indexing its documentation.
     * 
     * @param libraryName Name of the library (e.g., "numpy", "tensorflow")
     * @param docsPath Path to documentation (directory or file)
     */
    public void learnLibrary(String libraryName, String docsPath) throws Exception {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("   📚 LEARNING LIBRARY: " + libraryName);
        System.out.println("═══════════════════════════════════════════════");
        System.out.println();
        
        long startTime = System.currentTimeMillis();
        int functionsIndexed = 0;
        
        // Phase 1: Scan documentation
        System.out.println("[Phase 1] Scanning documentation at: " + docsPath);
        List<Path> docFiles = findDocumentationFiles(docsPath);
        System.out.println("   Found " + docFiles.size() + " documentation files");
        System.out.println();
        
        // Phase 2: Index documentation
        System.out.println("[Phase 2] Indexing " + libraryName + " documentation...");
        for (Path file : docFiles) {
            try {
                int functions = indexLibraryDocFile(libraryName, file);
                functionsIndexed += functions;
                System.out.println("   Indexed: " + file.getFileName() + " (" + functions + " chunks)");
            } catch (Exception e) {
                System.err.println("   Warning: Failed to index " + file + ": " + e.getMessage());
            }
        }
        System.out.println();
        
        // Phase 3: Record metadata
        long duration = System.currentTimeMillis() - startTime;
        LibraryMetadata metadata = new LibraryMetadata(
            libraryName,
            docsPath,
            functionsIndexed,
            docFiles.size(),
            duration
        );
        learnedLibraries.put(libraryName, metadata);
        
        System.out.println("[Phase 3] Learning complete");
        System.out.println("   ✓ Library: " + libraryName);
        System.out.println("   ✓ Chunks indexed: " + functionsIndexed);
        System.out.println("   ✓ Files processed: " + docFiles.size());
        System.out.println("   ✓ Time: " + duration + "ms");
        System.out.println();
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("   ✅ " + libraryName.toUpperCase() + " LEARNED");
        System.out.println("   Ready for use through natural language!");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println();
    }
    
    /**
     * Generate code using a learned library
     * 
     * @param libraryName The library to use
     * @param task What to accomplish
     * @return Generated code
     */
    public String generateUsage(String libraryName, String task) throws Exception {
        if (!learnedLibraries.containsKey(libraryName)) {
            return "ERROR: Library '" + libraryName + "' not learned yet. " +
                   "Use learnLibrary() first to index its documentation.";
        }
        
        // Build context from indexed library docs
        RagEngine rag = new RagEngine(brain, vault);
        String query = libraryName + " " + task;
        String libraryDocs = rag.buildContext(query, 8, 6000);
        
        // Generate code using library documentation
        List<OllamaSpine.Msg> msgs = List.of(
            new OllamaSpine.Msg("system",
                "You are a code generator using the " + libraryName + " library. " +
                "Use the provided library documentation to generate correct code. " +
                "Only use functions and APIs documented in the context. " +
                "Include comments explaining the code."),
            new OllamaSpine.Msg("user",
                "Library documentation:\n" + libraryDocs + "\n\n" +
                "Task: " + task + "\n\n" +
                "Generate code:")
        );
        
        return brain.chatOnce(msgs, null, Map.of("temperature", 0.3));
    }
    
    /**
     * Validate that a library is properly learned
     */
    public boolean validateLibraryKnowledge(String libraryName, String testQuery) throws Exception {
        if (!learnedLibraries.containsKey(libraryName)) {
            return false;
        }
        
        // Try to generate code for a basic task
        String code = generateUsage(libraryName, testQuery);
        
        // Check if the library name appears in the generated code
        // (simple validation - real validation would execute the code)
        return code.toLowerCase().contains(libraryName.toLowerCase());
    }
    
    /**
     * Find all documentation files recursively
     */
    private List<Path> findDocumentationFiles(String root) throws IOException {
        List<Path> docFiles = new ArrayList<>();
        Path rootPath = Paths.get(root);
        
        if (!Files.exists(rootPath)) {
            System.err.println("Warning: Documentation path not found: " + root);
            return docFiles;
        }
        
        if (Files.isDirectory(rootPath)) {
            // Recursively find doc files
            Files.walk(rootPath)
                .filter(p -> {
                    String name = p.toString().toLowerCase();
                    return name.endsWith(".md") || 
                           name.endsWith(".txt") ||
                           name.endsWith(".rst") ||
                           name.endsWith(".html") ||
                           name.endsWith(".java") ||
                           name.endsWith(".py") ||
                           name.endsWith(".js") ||
                           name.endsWith(".ts");
                })
                .forEach(docFiles::add);
        } else if (Files.exists(rootPath)) {
            // Single file
            docFiles.add(rootPath);
        }
        
        return docFiles;
    }
    
    /**
     * Index a single documentation file for a library
     */
    private int indexLibraryDocFile(String libraryName, Path file) throws Exception {
        // Read documentation
        String content = Files.readString(file);
        
        // Clean and chunk
        String cleaned = transmudder.cleanse(content);
        List<String> chunks = transmudder.chunk(cleaned, 1000, 150);
        
        // Label chunks with library and source
        List<String> labeledChunks = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            String label = String.format("[LIBRARY: %s | DOC: %s chunk %d/%d]\n%s",
                libraryName,
                file.getFileName(),
                i + 1,
                chunks.size(),
                chunks.get(i));
            labeledChunks.add(label);
        }
        
        // Embed and store
        List<float[]> embeddings = brain.embedBatch(labeledChunks);
        vault.addAndPersist(libraryName + ":" + file.toString(), labeledChunks, embeddings);
        
        return chunks.size();
    }
    
    /**
     * Get list of all learned libraries
     */
    public List<String> getLearnedLibraries() {
        return new ArrayList<>(learnedLibraries.keySet());
    }
    
    /**
     * Get metadata about a learned library
     */
    public LibraryMetadata getLibraryMetadata(String libraryName) {
        return learnedLibraries.get(libraryName);
    }
    
    /**
     * Get statistics about all learned libraries
     */
    public String getStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════\n");
        sb.append("   LEARNED LIBRARIES\n");
        sb.append("═══════════════════════════════════════════════\n");
        
        if (learnedLibraries.isEmpty()) {
            sb.append("   No libraries learned yet.\n");
        } else {
            for (LibraryMetadata meta : learnedLibraries.values()) {
                sb.append(String.format("   ✓ %s: %d chunks, %d files (%dms)\n",
                    meta.name,
                    meta.chunksIndexed,
                    meta.filesProcessed,
                    meta.learningTimeMs));
            }
        }
        
        sb.append("═══════════════════════════════════════════════\n");
        return sb.toString();
    }
    
    /**
     * Metadata about a learned library
     */
    public static class LibraryMetadata {
        public final String name;
        public final String docsPath;
        public final int chunksIndexed;
        public final int filesProcessed;
        public final long learningTimeMs;
        
        public LibraryMetadata(String name, String docsPath, int chunks, int files, long time) {
            this.name = name;
            this.docsPath = docsPath;
            this.chunksIndexed = chunks;
            this.filesProcessed = files;
            this.learningTimeMs = time;
        }
        
        @Override
        public String toString() {
            return String.format("Library '%s': %d chunks from %d files (learned in %dms)",
                name, chunksIndexed, filesProcessed, learningTimeMs);
        }
    }
}
