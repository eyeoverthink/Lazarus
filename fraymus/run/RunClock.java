package fraymus.run;

import java.time.Instant;
import java.time.Duration;

/**
 * RunClock: Tracks time for reproducible scientific runs.
 * 
 * Provides:
 * - Start timestamp
 * - Current elapsed time
 * - Formatted timestamps for logging
 * 
 * Used for timing events in JSONL logs and progress reporting.
 */
public class RunClock {
    
    private final Instant startTime;
    private final long startNano;
    
    public RunClock() {
        this.startTime = Instant.now();
        this.startNano = System.nanoTime();
    }
    
    /**
     * Get the start time of this run.
     */
    public Instant startTime() {
        return startTime;
    }
    
    /**
     * Get elapsed time since start in milliseconds.
     */
    public long elapsedMs() {
        return (System.nanoTime() - startNano) / 1_000_000;
    }
    
    /**
     * Get elapsed time since start in seconds.
     */
    public double elapsedSec() {
        return (System.nanoTime() - startNano) / 1_000_000_000.0;
    }
    
    /**
     * Get current timestamp (ISO-8601 format).
     */
    public String now() {
        return Instant.now().toString();
    }
    
    /**
     * Format elapsed time as human-readable string.
     */
    public String elapsed() {
        long ms = elapsedMs();
        if (ms < 1000) {
            return ms + "ms";
        } else if (ms < 60000) {
            return String.format("%.2fs", ms / 1000.0);
        } else {
            long sec = ms / 1000;
            long min = sec / 60;
            sec = sec % 60;
            return String.format("%dm %ds", min, sec);
        }
    }
    
    @Override
    public String toString() {
        return String.format("RunClock{start=%s, elapsed=%s}", startTime, elapsed());
    }
}
