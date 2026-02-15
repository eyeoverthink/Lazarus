package fraymus.run;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.time.Instant;
import com.google.gson.*;

/**
 * EventLogger: JSONL event logging for reproducible scientific runs.
 * 
 * Writes events in JSON Lines format (one JSON object per line).
 * Each event is timestamped and can be plotted/analyzed later.
 * 
 * Format:
 * - header() writes metadata (config, start time)
 * - event() writes individual events (step, metrics)
 * - footer() writes summary (end time, stats)
 * 
 * Files created:
 * - {runName}.jsonl - Machine-readable events
 * - (optional) console output for human readability
 */
public class EventLogger {
    
    private final RunConfig cfg;
    private final RunClock clock;
    private final String runName;
    private final PrintWriter jsonlWriter;
    private final boolean consoleEnabled;
    
    private static final Gson gson = new GsonBuilder()
        .disableHtmlEscaping()
        .create();
    
    /**
     * Create EventLogger with automatic file naming.
     */
    public EventLogger(RunConfig cfg, RunClock clock, String runName) throws IOException {
        this.cfg = cfg;
        this.clock = clock;
        this.runName = runName;
        this.consoleEnabled = cfg.prettyConsole;
        
        // Create output directory if needed
        if (cfg.jsonl) {
            Files.createDirectories(cfg.outDir);
            Path logPath = cfg.outDir.resolve(runName + ".jsonl");
            this.jsonlWriter = new PrintWriter(
                new BufferedWriter(new FileWriter(logPath.toFile()))
            );
        } else {
            this.jsonlWriter = null;
        }
    }
    
    /**
     * Write header with run metadata.
     */
    public void header(Map<String, Object> metadata) {
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("event", "header");
        header.put("timestamp", clock.now());
        header.put("runName", runName);
        header.put("seed", cfg.seed);
        header.put("steps", cfg.steps);
        header.put("populationSize", cfg.populationSize);
        header.put("gravityConstant", cfg.gravityConstant);
        header.put("fusionDistance", cfg.fusionDistance);
        header.put("energyThreshold", cfg.energyThreshold);
        
        // Add custom metadata
        if (metadata != null) {
            header.putAll(metadata);
        }
        
        writeEvent(header);
        
        if (consoleEnabled) {
            System.out.println("╔═══════════════════════════════════════════════════╗");
            System.out.println("║  " + runName.toUpperCase());
            System.out.println("╠═══════════════════════════════════════════════════╣");
            System.out.println("  Seed: " + cfg.seed);
            System.out.println("  Steps: " + cfg.steps);
            System.out.println("  Population: " + cfg.populationSize);
            System.out.println("  Gravity: " + cfg.gravityConstant);
            System.out.println("╚═══════════════════════════════════════════════════╝");
        }
    }
    
    /**
     * Log an event (step, iteration, measurement).
     */
    public void event(String eventType, Map<String, Object> data) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("event", eventType);
        event.put("timestamp", clock.now());
        event.put("elapsedMs", clock.elapsedMs());
        
        // Add event data
        if (data != null) {
            event.putAll(data);
        }
        
        writeEvent(event);
    }
    
    /**
     * Log a step event (common case).
     */
    public void step(int stepNum, Map<String, Object> metrics) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("step", stepNum);
        if (metrics != null) {
            data.putAll(metrics);
        }
        event("step", data);
        
        // Pretty console output every N steps
        if (consoleEnabled && stepNum % 100 == 0) {
            System.out.printf("  Step %4d/%d [%s] %s\n", 
                stepNum, cfg.steps, clock.elapsed(), formatMetrics(metrics));
        }
    }
    
    /**
     * Write footer with run summary.
     */
    public void footer(Map<String, Object> summary) {
        Map<String, Object> footer = new LinkedHashMap<>();
        footer.put("event", "footer");
        footer.put("timestamp", clock.now());
        footer.put("elapsedMs", clock.elapsedMs());
        footer.put("elapsedSec", clock.elapsedSec());
        
        // Add summary data
        if (summary != null) {
            footer.putAll(summary);
        }
        
        writeEvent(footer);
        
        if (consoleEnabled) {
            System.out.println("╔═══════════════════════════════════════════════════╗");
            System.out.println("  Run completed in " + clock.elapsed());
            if (summary != null) {
                for (Map.Entry<String, Object> e : summary.entrySet()) {
                    System.out.println("  " + e.getKey() + ": " + e.getValue());
                }
            }
            System.out.println("╚═══════════════════════════════════════════════════╝");
        }
        
        close();
    }
    
    /**
     * Write event to JSONL file.
     */
    private void writeEvent(Map<String, Object> event) {
        if (jsonlWriter != null) {
            jsonlWriter.println(gson.toJson(event));
            jsonlWriter.flush(); // Ensure written immediately
        }
    }
    
    /**
     * Format metrics for console display.
     */
    private String formatMetrics(Map<String, Object> metrics) {
        if (metrics == null || metrics.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> e : metrics.entrySet()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(e.getKey()).append("=");
            
            Object v = e.getValue();
            if (v instanceof Double) {
                sb.append(String.format("%.4f", (Double) v));
            } else if (v instanceof Float) {
                sb.append(String.format("%.4f", (Float) v));
            } else {
                sb.append(v);
            }
        }
        return sb.toString();
    }
    
    /**
     * Close the logger and flush all output.
     */
    public void close() {
        if (jsonlWriter != null) {
            jsonlWriter.close();
        }
    }
}
