package fraymus;

/**
 * LogicCircuit Demo - Demonstrates the Survival Instinct
 * Shows momentum-based detection vs traditional threshold detection
 */
public class LogicCircuitDemo {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  LOGIC CIRCUIT DEMO - THE SURVIVAL INSTINCT               ║");
        System.out.println("║  'Momentum > Average. Reaction > Calculation.'            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        LogicCircuit brain = new LogicCircuit();
        
        // SCENARIO 1: Normal Operation (No Spike)
        System.out.println("=== SCENARIO 1: Normal Operation ===");
        simulateNormalLoad(brain);
        System.out.println();
        
        // SCENARIO 2: Gradual Increase (Controlled)
        System.out.println("=== SCENARIO 2: Gradual Increase ===");
        simulateGradualIncrease(brain);
        System.out.println();
        
        // SCENARIO 3: Sudden Spike (Chaos!)
        System.out.println("=== SCENARIO 3: Sudden Spike (CHAOS) ===");
        simulateSuddenSpike(brain);
        System.out.println();
        
        // SCENARIO 4: Recovery
        System.out.println("=== SCENARIO 4: System Recovery ===");
        simulateRecovery(brain);
        System.out.println();
        
        System.out.println("✅ Demo complete. LogicCircuit operational.");
    }
    
    private static void simulateNormalLoad(LogicCircuit brain) {
        System.out.println("CPU fluctuating normally (20-30%)...");
        double[] loads = {20, 25, 23, 28, 22, 26, 24};
        for (double load : loads) {
            System.out.printf("  CPU: %.1f%% ", load);
            brain.analyzeSystemState(load, 50.0);
            System.out.println();
            sleep(50);
        }
    }
    
    private static void simulateGradualIncrease(LogicCircuit brain) {
        System.out.println("CPU gradually increasing...");
        for (double load = 20; load <= 60; load += 5) {
            System.out.printf("  CPU: %.1f%% ", load);
            brain.analyzeSystemState(load, 50.0);
            System.out.println();
            sleep(50);
        }
    }
    
    private static void simulateSuddenSpike(LogicCircuit brain) {
        System.out.println("CPU SPIKING RAPIDLY!");
        double[] loads = {20, 35, 60, 85, 95};
        for (double load : loads) {
            System.out.printf("  CPU: %.1f%% ", load);
            brain.analyzeSystemState(load, 50.0);
            System.out.println();
            sleep(50);
        }
    }
    
    private static void simulateRecovery(LogicCircuit brain) {
        System.out.println("System recovering after termination...");
        double[] loads = {30, 20, 15, 10, 12, 11};
        for (double load : loads) {
            System.out.printf("  CPU: %.1f%% ", load);
            brain.analyzeSystemState(load, 50.0);
            System.out.println();
            sleep(50);
        }
    }
    
    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }
}
