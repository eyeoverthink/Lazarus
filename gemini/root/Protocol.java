package gemini.root;

/**
 * THE PROTOCOL
 * Role: WebSocket message structures.
 * Function: Type-safe communication between UI and server.
 */
public class Protocol {

    /**
     * WEBSOCKET INPUT (from client)
     */
    public static class WsIn {
        public String type;   // "user" | "transmute" | "command"
        public String text;   // user message
        public String path;   // file path for transmute
        
        public WsIn() {}
        
        public WsIn(String type, String text) {
            this.type = type;
            this.text = text;
        }
    }

    /**
     * WEBSOCKET OUTPUT (to client)
     */
    public static class WsOut {
        public String type;     // "start" | "token" | "final" | "info" | "error"
        public String data;     // message content
        public Object meta;     // optional metadata
        
        public WsOut() {}
        
        public WsOut(String type, String data) {
            this.type = type;
            this.data = data;
        }
        
        public WsOut(String type, String data, Object meta) {
            this.type = type;
            this.data = data;
            this.meta = meta;
        }
    }
}
