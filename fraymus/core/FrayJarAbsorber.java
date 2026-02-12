package fraymus.core;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;

/**
 * 🏺 THE JAR ABSORBER
 * "I know Kung Fu."
 * 
 * FUNCTION:
 * 1. INGEST: Reads a .jar file from disk.
 * 2. LOAD: Injects the classes into Fraymus's runtime memory.
 * 3. INDEX: Scans for 'main' methods or specific interfaces (like PApplet).
 * 4. EXECUTE: Runs the library as if it were native code.
 * 
 * This component allows Fraymus to dynamically load external Java libraries
 * at runtime, scan their capabilities, and execute their code without
 * restarting the JVM. It's the bridge to the entire Java ecosystem.
 */
public class FrayJarAbsorber {

    private List<String> absorbedClasses = new ArrayList<>();
    private List<URLClassLoader> classLoaders = new ArrayList<>();

    /**
     * CONSUME LIBRARY
     * @param jarPath Path to the .jar file (e.g., "libs/processing-core.jar")
     */
    public void absorb(String jarPath) {
        File file = new File(jarPath);
        if (!file.exists()) {
            System.err.println("❌ TARGET MISSING: " + jarPath);
            return;
        }

        System.out.println("⚡ ABSORBING JAVA ARTIFACT: [" + file.getName() + "]...");

        try {
            // 1. EXTEND THE CLASSPATH DYNAMICALLY
            URL url = file.toURI().toURL();
            URLClassLoader child = new URLClassLoader(new URL[]{url}, this.getClass().getClassLoader());
            classLoaders.add(child);

            // 2. SCAN THE BRAIN (Look for capabilities)
            JarFile jar = new JarFile(file);
            Enumeration<JarEntry> entries = jar.entries();

            int classCount = 0;
            int executableCount = 0;
            int processingCount = 0;

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.').replace(".class", "");
                    
                    // Don't load everything, just scan signatures
                    // (Optimization: In a real system, we'd use ASM to scan bytecode without loading)
                    try {
                        Class<?> cls = Class.forName(className, false, child);
                        absorbedClasses.add(className);
                        classCount++;
                        
                        // CHECK: Is this a Processing Sketch?
                        if (isProcessingSketch(cls)) {
                            System.out.println("   🎨 FOUND VISUAL CORTEX: " + className);
                            processingCount++;
                        }
                        
                        // CHECK: Does it have a main method?
                        if (hasMain(cls)) {
                            System.out.println("   🚀 FOUND EXECUTABLE: " + className);
                            executableCount++;
                        }
                        
                    } catch (Throwable t) {
                        // Ignore classes that can't load (dependencies missing)
                    }
                }
            }
            
            jar.close();
            
            System.out.println("✅ ASSIMILATION COMPLETE. Library is now Part of Us.");
            System.out.println("   📊 Classes absorbed: " + classCount);
            System.out.println("   🚀 Executables found: " + executableCount);
            System.out.println("   🎨 Processing sketches: " + processingCount);

        } catch (Exception e) {
            System.err.println("❌ REJECTION: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * EXECUTE ABSORBED CODE
     * Runs the main method of an absorbed class
     * @param className Fully qualified class name
     * @param args Arguments to pass to main method
     */
    public void executeMain(String className, String[] args) {
        System.out.println("⚡ EXECUTING ABSORBED CODE: " + className);
        
        try {
            // Find the class in our absorbed classloaders
            Class<?> cls = null;
            for (URLClassLoader loader : classLoaders) {
                try {
                    cls = Class.forName(className, true, loader);
                    break;
                } catch (ClassNotFoundException e) {
                    // Try next classloader
                }
            }
            
            if (cls == null) {
                System.err.println("❌ CLASS NOT FOUND: " + className);
                System.err.println("   Hint: Did you absorb the JAR containing this class?");
                return;
            }
            
            // Get the main method
            Method mainMethod = cls.getMethod("main", String[].class);
            
            // Invoke it
            mainMethod.invoke(null, (Object) args);
            
            System.out.println("✅ EXECUTION COMPLETE");
            
        } catch (Exception e) {
            System.err.println("❌ EXECUTION FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * LIST ABSORBED CLASSES
     */
    public void listAbsorbedClasses() {
        System.out.println("📚 ABSORBED CAPABILITIES (" + absorbedClasses.size() + " classes):");
        for (String className : absorbedClasses) {
            System.out.println("   - " + className);
        }
    }

    // --- HEURISTICS ---

    private boolean isProcessingSketch(Class<?> cls) {
        // Processing sketches usually extend 'PApplet'
        Class<?> superclass = cls.getSuperclass();
        while (superclass != null) {
            if (superclass.getName().equals("processing.core.PApplet")) return true;
            superclass = superclass.getSuperclass();
        }
        return false;
    }

    private boolean hasMain(Class<?> cls) {
        try {
            Method m = cls.getMethod("main", String[].class);
            return java.lang.reflect.Modifier.isStatic(m.getModifiers());
        } catch (Exception e) {
            return false;
        }
    }

    // --- DEMO ---
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           FRAY JAR ABSORBER - DEMO MODE                    ║");
        System.out.println("║              \"I know Kung Fu.\"                             ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        FrayJarAbsorber absorber = new FrayJarAbsorber();
        
        if (args.length > 0) {
            String jarPath = args[0];
            absorber.absorb(jarPath);
            
            if (args.length > 1 && args[1].equals("execute") && args.length > 2) {
                String className = args[2];
                String[] execArgs = new String[args.length - 3];
                System.arraycopy(args, 3, execArgs, 0, execArgs.length);
                absorber.executeMain(className, execArgs);
            }
        } else {
            System.out.println("Usage:");
            System.out.println("  java fraymus.core.FrayJarAbsorber <jar-path>");
            System.out.println("  java fraymus.core.FrayJarAbsorber <jar-path> execute <class-name> [args...]");
            System.out.println();
            System.out.println("Examples:");
            System.out.println("  java fraymus.core.FrayJarAbsorber libs/processing-core.jar");
            System.out.println("  java fraymus.core.FrayJarAbsorber mylib.jar execute com.example.Main");
            System.out.println();
            System.out.println("This system allows Fraymus to:");
            System.out.println("  ✓ Load external Java libraries at runtime");
            System.out.println("  ✓ Scan for Processing sketches (PApplet)");
            System.out.println("  ✓ Detect executable classes (main methods)");
            System.out.println("  ✓ Execute absorbed code without JVM restart");
            System.out.println();
            System.out.println("\"Install new software into its own brain without restarting.\"");
        }
    }
}
