package gemini.root;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * TraceLogger: Request tracing for observability
 * 
 * Logs:
 * - Request ID
 * - RAG snippet IDs
 * - Tool calls and outputs
 * - Reflection on/off
 * - Model and context size
 * 
 * Saved to memory/trace_*.jsonl (separate from Hippocampus)
 */
public class TraceLogger {
    
    private static final String TRACE_DIR = "memory";
    private static final DateTimeFormatter FILE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public static class TraceEntry {
        public String requestId;
        public String timestamp;
        public String query;
        public int ragSnippets;
        public List<String> ragSources;
        public List<String> toolCalls;
        public int toolOutputLength;
        public boolean reflectorEnabled;
        public String model;
        public int contextSize;
        public int answerLength;
        public long latencyMs;
        
        public TraceEntry(String requestId) {
            this.requestId = requestId;
            this.timestamp = LocalDateTime.now().format(TIME_FORMAT);
            this.ragSources = new ArrayList<>();
            this.toolCalls = new ArrayList<>();
        }
        
        public String toJson() {
            StringBuilder json = new StringBuilder("{");
            json.append("\"requestId\":\"").append(requestId).append("\",");
            json.append("\"timestamp\":\"").append(timestamp).append("\",");
            json.append("\"query\":\"").append(escape(query)).append("\",");
            json.append("\"ragSnippets\":").append(ragSnippets).append(",");
            json.append("\"ragSources\":[");
            for (int i = 0; i < ragSources.size(); i++) {
                if (i > 0) json.append(",");
                json.append("\"").append(escape(ragSources.get(i))).append("\"");
            }
            json.append("],");
            json.append("\"toolCalls\":[");
            for (int i = 0; i < toolCalls.size(); i++) {
                if (i > 0) json.append(",");
                json.append("\"").append(escape(toolCalls.get(i))).append("\"");
            }
            json.append("],");
            json.append("\"toolOutputLength\":").append(toolOutputLength).append(",");
            json.append("\"reflectorEnabled\":").append(reflectorEnabled).append(",");
            json.append("\"model\":\"").append(model).append("\",");
            json.append("\"contextSize\":").append(contextSize).append(",");
            json.append("\"answerLength\":").append(answerLength).append(",");
            json.append("\"latencyMs\":").append(latencyMs);
            json.append("}");
            return json.toString();
        }
        
        private String escape(String s) {
            if (s == null) return "";
            return s.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
        }
    }
    
    /**
     * Log a trace entry
     */
    public static void log(TraceEntry entry) {
        try {
            // Ensure directory exists
            new File(TRACE_DIR).mkdirs();
            
            // Daily log file
            String date = LocalDate.now().format(FILE_FORMAT);
            String filename = TRACE_DIR + "/trace_" + date + ".jsonl";
            
            // Append to file
            try (FileWriter fw = new FileWriter(filename, true)) {
                fw.write(entry.toJson() + "\n");
            }
            
        } catch (IOException e) {
            System.err.println(">>> [TRACE] Log failed: " + e.getMessage());
        }
    }
    
    /**
     * Generate unique request ID
     */
    public static String generateRequestId() {
        return "req_" + System.currentTimeMillis() + "_" + 
               Integer.toHexString(new Random().nextInt(0xFFFF));
    }
}
