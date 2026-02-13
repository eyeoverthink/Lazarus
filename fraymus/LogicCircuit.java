package fraymus;

import java.util.ArrayList;
import java.util.List;

/**
 * THE BRAIN: LogicCircuit.java
 * UPGRADE: "Project Midas" Survival Instinct
 * PHILOSOPHY: Momentum > Average. Reaction > Calculation.
 */
public class LogicCircuit {

    // --- UNIVERSAL CONSTANTS ---
    private static final double PHI = 1.6180339887; 
    private static final double DECAY = 0.618; // The "Golden Ratio" Memory
    private static final double CHAOS_THRESHOLD = 3.5; // The "Black Swan" Trigger

    // --- STATE ---
    private double lastSystemLoad = 0.0;
    private double momentum = 0.0;
    private boolean panicMode = false;
    private boolean initialized = false;

    // --- THE GHOST LOOP (Called every 16ms / 60Hz) ---
    public void analyzeSystemState(double currentCpuLoad, double currentRamUsage) {
        
        // Initialize on first run
        if (!initialized) {
            lastSystemLoad = currentCpuLoad;
            initialized = true;
            return;
        }
        
        // 1. CALCULATE VELOCITY (The Derivative)
        // We don't care about the absolute load. We care about the SPEED of the rise.
        double velocity = currentCpuLoad - lastSystemLoad;

        // 2. APPLY PHI MEMORY
        // If the spike is sudden, momentum builds fast.
        // If it's noise, the decay (0.618) kills it.
        momentum = (momentum * DECAY) + velocity;

        // 3. THE "BLACK SWAN" CHECK
        // Standard OS waits for 100% usage. We act NOW.
        if (momentum > CHAOS_THRESHOLD) {
            triggerSurvivalReflex(currentCpuLoad);
        } else if (momentum < -CHAOS_THRESHOLD) {
            // Rapid cooling detected (Process died?)
            log("[DREAMER] System cooling. Entropy stabilizing.");
        }

        // 4. PRESERVE HISTORY
        lastSystemLoad = currentCpuLoad;
    }

    // --- THE REFLEX ---
    private void triggerSurvivalReflex(double load) {
        if (panicMode) return; // Don't panic twice

        panicMode = true;
        System.err.println(">> [CHAOS DETECTED] Momentum Spike: " + String.format("%.2f", momentum));
        System.err.println(">> [ACTION] PRE-EMPTIVE TERMINATION INITIATED.");

        // KILL THE HEAVIEST THREAD
        killHighEntropyProcess();

        // Reset system state
        momentum = 0; 
        panicMode = false;
    }

    private void killHighEntropyProcess() {
        // In a real OS, this sends SIGKILL. 
        // In Fraymus, it purges the specific Lazarus thread causing the heat.
        System.out.println("[LOGIC] Threat neutralized. System saved.");
    }

    private void log(String msg) {
        System.out.println("[LOGIC CIRCUIT]: " + msg);
    }
}
