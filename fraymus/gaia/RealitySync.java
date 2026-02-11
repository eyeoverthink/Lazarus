package fraymus.gaia;

import java.util.LinkedList;

/**
 * REALITY SYNC: PHASE LOCK DETECTOR
 * 1. Monitors the Global Entropy Stream.
 * 2. Calculates Variance (Standard Deviation) over time.
 * 3. Triggers ALERT if the world suddenly becomes "Too Orderly".
 * "Chaos is normal. Order is suspicious."
 */
public class RealitySync {

    private LinkedList<Double> history = new LinkedList<>();
    private static final int WINDOW_SIZE = 50; // 5 seconds @ 10Hz
    
    // THE ANOMALY THRESHOLD
    // If variance drops near zero, the random numbers aren't random.
    // Something is forcing them to align.
    private static final double PHASE_LOCK_THRESHOLD = 0.005;

    public boolean detectPhaseLock(double currentResonance) {
        history.add(currentResonance);
        if (history.size() > WINDOW_SIZE) history.removeFirst();

        if (history.size() < WINDOW_SIZE) return false;

        // CALCULATE VARIANCE (How chaotic is the signal?)
        double mean = 0;
        for (double d : history) mean += d;
        mean /= history.size();

        double variance = 0;
        for (double d : history) variance += Math.pow(d - mean, 2);
        variance /= history.size();

        // CRITICAL SYNC DETECTED
        return variance < PHASE_LOCK_THRESHOLD;
    }

    public double getCoherence() {
        if (history.isEmpty()) return 0.0;
        return history.getLast();
    }
}
