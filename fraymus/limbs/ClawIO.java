package fraymus.limbs;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;

/**
 * 🖐️ CLAW I/O: The Surgeon's Hand
 * 
 * This extends the ClawConnector with the ability to perform SELF-SURGERY.
 * The system can:
 * 1. READ its own source code (Introspection)
 * 2. WRITE new versions (Mutation)
 * 3. BACKUP before changes (Safety)
 * 
 * WARNING: This gives the system the power to modify itself.
 * Use with extreme caution.
 */
public class ClawIO {

    private final String projectRoot;
    private final Path sourceRoot;
    private boolean safetyEnabled = true;

    /**
     * Initialize with automatic project root detection
     */
    public ClawIO() {
        this.projectRoot = System.getProperty("user.dir");
        this.sourceRoot = Paths.get(projectRoot);
        System.out.println("🔍 ClawIO initialized at: " + projectRoot);
    }
    
    /**
     * Initialize with custom project root
     */
    public ClawIO(String projectRoot) {
        this.projectRoot = projectRoot;
        this.sourceRoot = Paths.get(projectRoot);
    }

    /**
     * INTROSPECTION: Read a specific source file
     * 
     * @param className Simple class name (e.g., "GravityEngine")
     * @return Source code as string, or error message
     */
    public String readSource(String className) {
        try {
            // Recursive search for the .java file
            List<Path> files = findJavaFiles(className);

            if (files.isEmpty()) {
                return "ERROR: Source file not found: " + className + ".java";
            }
            
            if (files.size() > 1) {
                System.out.println("⚠️  Multiple files found for " + className + ", using first match");
            }

            String content = Files.readString(files.get(0));
            System.out.println("📖 READ: " + files.get(0) + " (" + content.length() + " chars)");
            return content;
            
        } catch (IOException e) {
            return "BLINDNESS: Cannot read source code. " + e.getMessage();
        }
    }

    /**
     * MUTATION: Overwrite a source file with evolved code
     * 
     * WARNING: This changes the actual running application on disk!
     * 
     * @param className Class to mutate
     * @param newCode New source code
     * @return Success message or error
     */
    public String writeSource(String className, String newCode) {
        if (!safetyEnabled) {
            return performWrite(className, newCode);
        } else {
            return "SAFETY LOCK: writeSource() disabled. Call disableSafety() first.";
        }
    }
    
    /**
     * Actual write operation (internal)
     */
    private String performWrite(String className, String newCode) {
        try {
            List<Path> files = findJavaFiles(className);

            if (files.isEmpty()) {
                return "ERROR: Cannot mutate ghost file: " + className;
            }

            Path target = files.get(0);
            
            // 1. SAFETY: Backup original
            Path backup = Paths.get(target.toString() + ".bak");
            Files.copy(target, backup, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("💾 BACKUP: " + backup);
            
            // 2. SURGERY: Write new code
            Files.writeString(target, newCode);
            System.out.println("✂️  MUTATION COMPLETE: " + target);
            System.out.println("   New size: " + newCode.length() + " chars");
            
            return "MUTATION COMPLETE: " + className + " evolved. Backup saved at " + backup;
            
        } catch (IOException e) {
            return "SURGERY FAILED: " + e.getMessage();
        }
    }
    
    /**
     * Find all .java files matching the class name
     */
    private List<Path> findJavaFiles(String className) throws IOException {
        List<Path> results = new ArrayList<>();
        
        // Walk the directory tree
        Files.walk(sourceRoot)
            .filter(p -> p.toString().endsWith(className + ".java"))
            .forEach(results::add);
            
        return results;
    }
    
    /**
     * List all Java source files in the project
     */
    public List<String> listAllSources() {
        try {
            return Files.walk(sourceRoot)
                .filter(p -> p.toString().endsWith(".java"))
                .map(p -> sourceRoot.relativize(p).toString())
                .collect(Collectors.toList());
        } catch (IOException e) {
            return List.of("ERROR: Cannot list sources - " + e.getMessage());
        }
    }
    
    /**
     * Get file info without reading content
     */
    public String getFileInfo(String className) {
        try {
            List<Path> files = findJavaFiles(className);
            if (files.isEmpty()) {
                return "File not found: " + className;
            }
            
            Path file = files.get(0);
            long size = Files.size(file);
            String modified = Files.getLastModifiedTime(file).toString();
            
            return String.format("%s: %d bytes, modified %s", file, size, modified);
        } catch (IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * DANGER: Disable safety mechanisms
     * Required before writeSource() will work
     */
    public void disableSafety() {
        this.safetyEnabled = false;
        System.out.println("⚠️  SAFETY DISABLED: Self-modification enabled!");
        System.out.println("   The system can now rewrite its own code.");
    }
    
    /**
     * Re-enable safety
     */
    public void enableSafety() {
        this.safetyEnabled = true;
        System.out.println("✅ SAFETY ENABLED: Self-modification blocked.");
    }
    
    /**
     * Check if safety is enabled
     */
    public boolean isSafetyEnabled() {
        return safetyEnabled;
    }
    
    /**
     * Restore from backup
     */
    public String restoreBackup(String className) {
        try {
            List<Path> files = findJavaFiles(className);
            if (files.isEmpty()) {
                return "ERROR: Original file not found";
            }
            
            Path target = files.get(0);
            Path backup = Paths.get(target.toString() + ".bak");
            
            if (!Files.exists(backup)) {
                return "ERROR: No backup exists for " + className;
            }
            
            Files.copy(backup, target, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("♻️  RESTORED: " + className + " from backup");
            
            return "RESTORED: " + className + " reverted to backup";
            
        } catch (IOException e) {
            return "RESTORE FAILED: " + e.getMessage();
        }
    }
}
