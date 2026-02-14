package fraymus;

import fraymus.physics.*;

/**
 * 🕸️ FRAYNIX HIVE: The Singularity Point
 * 
 * This is where Fraynix (The Brain) meets OpenClaw (The Body).
 * Thoughts become particles. Gravity pulls them toward execution.
 * Collisions trigger real-world actions.
 * 
 * YOU DON'T TYPE COMMANDS ANYMORE. YOU HAVE TELEPATHY.
 * 
 * How to use:
 * 1. Install and start OpenClaw (openclaw serve or similar)
 * 2. Run this class
 * 3. Watch as gravity causes code execution
 */
public class FraynixHive {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║   🕸️  HIVE MIND ONLINE: Fraynix + OpenClaw   ║");
        System.out.println("║   Where Gravity Causes Code Execution       ║");
        System.out.println("╚═══════════════════════════════════════════════╝\n");

        // 1. Start the Physics Engine
        GravityEngine universe = new GravityEngine();
        SpatialRegistry registry = new SpatialRegistry(universe);

        // 2. Spawn the CLAW (The Body)
        // Positioned at the center of the universe (50, 50, 50)
        System.out.println("📍 Spawning OpenClaw particle at universe center...");
        ClawParticle claw = new ClawParticle(50, 50, 50);
        universe.register(claw);
        registry.register(claw);
        
        // Check connection
        if (claw.isConnected()) {
            System.out.println("✅ OpenClaw is ONLINE at " + claw.toString());
        } else {
            System.out.println("⚠️  WARNING: OpenClaw not detected.");
            System.out.println("   Tasks will still simulate, but won't execute physically.");
            System.out.println("   To connect: Start OpenClaw on port 18789\n");
        }

        // 3. Inject Thoughts (The Intents)
        // These are task particles that will be pulled toward the Claw
        System.out.println("\n💭 THOUGHT INJECTION: Creating task particles...\n");
        
        // Task 1: Create a deployment script
        PhiSuit<String> task1 = new PhiSuit<>("Create a bash deployment script for Fraynix", 20, 20, 20);
        task1.label = "TASK_DEPLOY";
        task1.amplitude = 85.0; // High importance
        task1.heat = 50.0;      // High energy
        universe.register(task1);
        registry.register(task1);
        System.out.println("   ⭐ " + task1);
        
        // Task 2: Build a website
        PhiSuit<String> task2 = new PhiSuit<>("Build a simple HTML landing page", 80, 80, 20);
        task2.label = "TASK_WEBSITE";
        task2.amplitude = 75.0;
        task2.heat = 40.0;
        universe.register(task2);
        registry.register(task2);
        System.out.println("   ⭐ " + task2);
        
        // Task 3: Code refactoring
        PhiSuit<String> task3 = new PhiSuit<>("Refactor the GravityEngine for better performance", 30, 70, 50);
        task3.label = "TASK_REFACTOR";
        task3.amplitude = 70.0;
        task3.heat = 30.0;
        universe.register(task3);
        registry.register(task3);
        System.out.println("   ⭐ " + task3);

        // 4. Run Physics Loop
        // The Gravity Engine will naturally pull tasks toward the claw
        // When they collide, OpenClaw executes them in the real world
        System.out.println("\n🌌 STARTING GRAVITY SIMULATION...\n");
        System.out.println("Watch as tasks spiral toward the Claw due to Hebbian gravity:");
        System.out.println("F = φ × (A₁ × A₂) / d²\n");
        
        for (int i = 0; i < 200; i++) {
            universe.tick();
            
            // Print state every 20 ticks
            if (i % 20 == 0) {
                System.out.println("\n─── Tick " + i + " ───");
                System.out.println("Claw: " + claw);
                System.out.println("Task1 distance: " + String.format("%.2f", task1.distanceTo(claw)));
                System.out.println("Task2 distance: " + String.format("%.2f", task2.distanceTo(claw)));
                System.out.println("Task3 distance: " + String.format("%.2f", task3.distanceTo(claw)));
                
                // Show spatial map
                if (i % 40 == 0) {
                    registry.printMap(60, 20);
                }
            }
            
            try { 
                Thread.sleep(50); // 50ms per tick = 20 ticks/second
            } catch (Exception e) {
                break;
            }
        }
        
        // Final statistics
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║        SIMULATION COMPLETE           ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println("Total Ticks: " + universe.getTickCount());
        System.out.println("Tasks Executed: " + claw.getTasksExecuted());
        System.out.println("Final Claw State: " + claw);
        
        universe.printState();
    }
}
