package fraymus.intelligence;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * IQ TRACKER - Persistent Intelligence Measurement
 * 
 * ACTUAL implementation (not documentation) that:
 * - Tracks IQ over time
 * - Saves to disk
 * - Loads previous measurements
 * - Shows growth
 * 
 * @author Vaughn Scott
 * @version 1.0 REAL
 */
public class IQTracker {
    
    private static final String IQ_FILE = System.getProperty("user.home") + "/.fraymus/iq_history.txt";
    
    private List<IQMeasurement> history;
    private double currentIQ;
    
    public IQTracker() {
        this.history = new ArrayList<>();
        loadHistory();
        this.currentIQ = getCurrentIQ();
    }
    
    /**
     * Record a new IQ measurement
     */
    public void recordIQ(double iq, String reason) {
        IQMeasurement measurement = new IQMeasurement(
            System.currentTimeMillis(),
            iq,
            reason
        );
        
        history.add(measurement);
        currentIQ = iq;
        saveHistory();
        
        System.out.println("💾 IQ Recorded: " + iq + " (" + reason + ")");
    }
    
    /**
     * Get current IQ
     */
    public double getIQ() {
        return currentIQ;
    }
    
    /**
     * Get IQ growth since start
     */
    public double getGrowth() {
        if (history.isEmpty()) return 0.0;
        double firstIQ = history.get(0).iq;
        return currentIQ - firstIQ;
    }
    
    /**
     * Get total number of measurements
     */
    public int getMeasurementCount() {
        return history.size();
    }
    
    /**
     * Load IQ history from disk
     */
    private void loadHistory() {
        File file = new File(IQ_FILE);
        if (!file.exists()) {
            System.out.println("📝 No previous IQ history found - starting fresh");
            // Initialize with baseline
            recordIQ(100.0, "baseline");
            return;
        }
        
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    long timestamp = Long.parseLong(parts[0]);
                    double iq = Double.parseDouble(parts[1]);
                    String reason = parts[2];
                    history.add(new IQMeasurement(timestamp, iq, reason));
                }
            }
            System.out.println("✅ Loaded " + history.size() + " IQ measurements");
        } catch (Exception e) {
            System.out.println("⚠️  Error loading IQ history: " + e.getMessage());
        }
    }
    
    /**
     * Save IQ history to disk
     */
    private void saveHistory() {
        try {
            File file = new File(IQ_FILE);
            file.getParentFile().mkdirs();
            
            try (PrintWriter writer = new PrintWriter(file)) {
                for (IQMeasurement m : history) {
                    writer.println(m.timestamp + "," + m.iq + "," + m.reason);
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️  Error saving IQ history: " + e.getMessage());
        }
    }
    
    /**
     * Get current IQ from history
     */
    private double getCurrentIQ() {
        if (history.isEmpty()) return 100.0;
        return history.get(history.size() - 1).iq;
    }
    
    /**
     * Print IQ history
     */
    public void printHistory() {
        System.out.println("\n📊 IQ HISTORY:");
        System.out.println("═══════════════════════════════════════");
        for (int i = 0; i < history.size(); i++) {
            IQMeasurement m = history.get(i);
            System.out.println((i + 1) + ". IQ " + m.iq + " - " + m.reason);
        }
        System.out.println("═══════════════════════════════════════");
        System.out.println("Growth: +" + getGrowth() + " points");
        System.out.println();
    }
    
    /**
     * IQ Measurement record
     */
    private static class IQMeasurement {
        long timestamp;
        double iq;
        String reason;
        
        IQMeasurement(long timestamp, double iq, String reason) {
            this.timestamp = timestamp;
            this.iq = iq;
            this.reason = reason;
        }
    }
    
    /**
     * Test the IQ tracker
     */
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════");
        System.out.println("   IQ TRACKER - ACTUAL IMPLEMENTATION");
        System.out.println("═══════════════════════════════════════\n");
        
        IQTracker tracker = new IQTracker();
        
        System.out.println("Current IQ: " + tracker.getIQ());
        System.out.println("Total measurements: " + tracker.getMeasurementCount());
        System.out.println("Growth: +" + tracker.getGrowth());
        
        // Simulate some learning
        System.out.println("\n🧠 Simulating learning...\n");
        tracker.recordIQ(105.0, "learned pattern recognition");
        tracker.recordIQ(110.0, "improved reasoning");
        tracker.recordIQ(115.0, "meta-learning achievement");
        
        tracker.printHistory();
        
        System.out.println("═══════════════════════════════════════");
        System.out.println("   TEST COMPLETE");
        System.out.println("   IQ History saved to: " + IQ_FILE);
        System.out.println("═══════════════════════════════════════");
    }
}
