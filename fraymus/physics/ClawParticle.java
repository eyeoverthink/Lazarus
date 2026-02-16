package fraymus.physics;

import fraymus.limbs.ClawConnector;

/**
 * 🖐️ CLAW PARTICLE: OpenClaw as a Physical Entity
 * 
 * In the Fraynix universe, OpenClaw isn't just software running on a port.
 * It's a MASSIVE GRAVITY WELL - a particle with immense amplitude.
 * 
 * Task particles are naturally pulled toward it.
 * When they collide, the task automatically executes in the real world.
 * 
 * This is TELEPATHIC EXECUTION: Gravity causes code to run.
 */
public class ClawParticle extends PhiSuit<String> {

    private final ClawConnector nerve;
    private boolean isBusy = false;
    private int tasksExecuted = 0;

    /**
     * Create the OpenClaw particle at a position
     */
    public ClawParticle(double x, double y, double z) {
        super("OPEN_CLAW_AGENT", x, y, z);
        this.nerve = new ClawConnector();
        this.amplitude = 100.0; // MASSIVE gravity well
        this.heat = 0.0;         // Cool execution
        this.label = "OPEN_CLAW_AGENT";
    }
    
    /**
     * Create with custom gateway URL
     */
    public ClawParticle(double x, double y, double z, String gatewayUrl) {
        super("OPEN_CLAW_AGENT", x, y, z);
        this.nerve = new ClawConnector(gatewayUrl);
        this.amplitude = 100.0;
        this.heat = 0.0;
        this.label = "OPEN_CLAW_AGENT";
    }

    /**
     * FUSION LOGIC: When a Task particle collides with the Claw, EXECUTE IT.
     * 
     * This is where gravity becomes action.
     * This is where thought becomes reality.
     */
    @Override
    public void onCollision(PhiSuit<?> other) {
        // Only react to High-Energy Tasks
        if (other.label.startsWith("TASK_") && !isBusy) {
            System.out.println("⚡ KINETIC CAPTURE: Claw caught " + other.label);
            this.isBusy = true;
            this.heat = 100.0; // Spike heat during work
            
            // Execute in background (don't block physics loop)
            new Thread(() -> {
                try {
                    String taskIntent = other.data != null ? other.data.toString() : other.label;
                    String context = "Priority: Critical | Energy: " + other.heat;
                    
                    String result = nerve.dispatch(taskIntent, context);
                    System.out.println("   🎯 RESULT: " + result);
                    
                    tasksExecuted++;
                    
                    // Mark task as completed (reduce its amplitude)
                    other.amplitude *= 0.1;
                    other.heat = 0.0;
                    
                } catch (Exception e) {
                    System.err.println("   ❌ EXECUTION ERROR: " + e.getMessage());
                } finally {
                    this.isBusy = false;
                    this.heat = 10.0; // Cool down
                }
            }).start();
        }
    }
    
    /**
     * Check if OpenClaw is actually running
     */
    public boolean isConnected() {
        return nerve.isConnected();
    }
    
    /**
     * Get statistics
     */
    public int getTasksExecuted() {
        return tasksExecuted;
    }
    
    public boolean isBusy() {
        return isBusy;
    }
    
    @Override
    public String toString() {
        return String.format("CLAW [%.1f, %.1f, %.1f] A=%.1f H=%.1f Busy=%s Tasks=%d", 
            x, y, z, amplitude, heat, isBusy, tasksExecuted);
    }
}
