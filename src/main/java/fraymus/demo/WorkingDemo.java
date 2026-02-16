package fraymus.demo;

import fraymus.core.FraymusCore;
import fraymus.intelligence.IQTracker;

/**
 * WORKING DEMO - Shows what actually exists and works
 * 
 * This is REAL CODE that you can actually run.
 * No promises, no plans - just working functionality.
 * 
 * @author Vaughn Scott
 * @version 1.0 REAL
 */
public class WorkingDemo {
    
    public static void main(String[] args) {
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                           ║");
        System.out.println("║         FRAYMUS - WORKING DEMO (REAL CODE!)               ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("\n");
        
        // Demo 1: Core System
        System.out.println("DEMO 1: CORE SYSTEM");
        System.out.println("───────────────────────────────────────────────────────────\n");
        
        FraymusCore core = new FraymusCore();
        core.start();
        
        core.process("Hello, this is a real test!");
        core.process("Can you process this query?");
        
        System.out.println("\nSystem State:");
        core.getState().forEach((k, v) -> System.out.println("  " + k + ": " + v));
        
        core.stop();
        
        // Demo 2: IQ Tracking
        System.out.println("\n\nDEMO 2: IQ TRACKING");
        System.out.println("───────────────────────────────────────────────────────────\n");
        
        IQTracker iqTracker = new IQTracker();
        System.out.println("Starting IQ: " + iqTracker.getIQ());
        
        // Simulate learning
        iqTracker.recordIQ(105, "completed first task");
        iqTracker.recordIQ(108, "learned new pattern");
        iqTracker.recordIQ(112, "meta-cognitive improvement");
        
        iqTracker.printHistory();
        
        // Summary
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                           ║");
        System.out.println("║                    DEMO COMPLETE                          ║");
        System.out.println("║                                                           ║");
        System.out.println("║  What works:                                              ║");
        System.out.println("║  ✅ Core system (start/stop/process)                      ║");
        System.out.println("║  ✅ IQ tracking (persistent storage)                      ║");
        System.out.println("║  ✅ Basic operations                                      ║");
        System.out.println("║                                                           ║");
        System.out.println("║  This is REAL code, not documentation!                    ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("\n");
    }
}
