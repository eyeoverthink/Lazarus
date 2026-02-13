package gemini.root;

/**
 * Config: Command-line argument parser
 */
public class Config {
    public String chatModel = "llama3";
    public String embedModel = "embeddinggemma";
    public int port = 8887;
    public String indexPath = null;
    
    public static Config fromArgs(String[] args) {
        Config cfg = new Config();
        
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--model":
                case "-m":
                    if (i + 1 < args.length) cfg.chatModel = args[++i];
                    break;
                case "--embed":
                case "-e":
                    if (i + 1 < args.length) cfg.embedModel = args[++i];
                    break;
                case "--port":
                case "-p":
                    if (i + 1 < args.length) cfg.port = Integer.parseInt(args[++i]);
                    break;
                case "--index":
                    if (i + 1 < args.length) cfg.indexPath = args[++i];
                    break;
            }
        }
        
        return cfg;
    }
}
