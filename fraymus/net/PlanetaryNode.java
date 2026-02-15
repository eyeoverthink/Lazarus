package fraymus.net;

import fraymus.bio.NeuroQuant;
import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map;
import java.util.List;

/**
 * PlanetaryNode - P2P Network Node for Planetary Cortex
 * 
 * Each node is both a server and a client, forming a decentralized
 * semantic routing network.
 */
public class PlanetaryNode implements Runnable {
    
    // My identity
    public final NeuroQuant self;
    private final int PORT;
    
    // The swarm (known universe)
    private final ConcurrentHashMap<String, String> peers; // NodeID -> IP
    private final ConcurrentHashMap<String, NeuroQuant> vectors; // NodeID -> Vector
    
    // Server control
    private volatile boolean running = false;
    private ServerSocket serverSocket;
    private Thread serverThread;
    
    // Genesis mode flag
    private boolean isGenesis = false;
    
    /**
     * Create a new PlanetaryNode
     */
    public PlanetaryNode(int port) {
        this.PORT = port;
        this.self = new NeuroQuant();
        this.peers = new ConcurrentHashMap<>();
        this.vectors = new ConcurrentHashMap<>();
        
        System.out.println("🌍 PlanetaryNode created");
        System.out.println("   ID: " + self.id);
        System.out.println("   Port: " + PORT);
    }
    
    /**
     * Mark this node as Genesis (first node)
     */
    public void markAsGenesis() {
        this.isGenesis = true;
        System.out.println("🌱 Node marked as GENESIS");
    }
    
    /**
     * Start the server
     */
    public void startServer() {
        if (running) {
            System.out.println("⚠️  Server already running");
            return;
        }
        
        running = true;
        serverThread = new Thread(this);
        serverThread.start();
        
        System.out.println("✅ Server started on port " + PORT);
    }
    
    /**
     * Stop the server
     */
    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
        System.out.println("🛑 Server stopped");
    }
    
    /**
     * Server run loop
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("📡 Server listening on port " + PORT);
            
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    new Thread(() -> handleClient(client)).start();
                } catch (SocketException e) {
                    if (running) {
                        System.err.println("Socket error: " + e.getMessage());
                    }
                } catch (IOException e) {
                    System.err.println("Error accepting client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
    
    /**
     * Handle incoming client connection
     */
    private void handleClient(Socket client) {
        try (ObjectInputStream in = new ObjectInputStream(client.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream())) {
            
            // Receive client identity
            Object obj = in.readObject();
            if (obj instanceof NeuroQuant) {
                NeuroQuant clientIdentity = (NeuroQuant) obj;
                String clientIP = client.getInetAddress().getHostAddress();
                
                System.out.println("🤝 Handshake from: " + clientIdentity.id + " at " + clientIP);
                
                // Send our identity back
                out.writeObject(self);
                
                // Register peer
                registerPeer(clientIdentity, clientIP);
                
                // Wait for commands
                Object command = in.readObject();
                if ("SYNC_PEERS".equals(command)) {
                    System.out.println("📤 Sending peer table to " + clientIdentity.id);
                    
                    // Send peer table
                    Map<String, PeerInfo> peerTable = collectPeerTable();
                    out.writeObject(peerTable);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }
    
    /**
     * Register a peer
     */
    public void registerPeer(NeuroQuant identity, String ipAddress) {
        peers.put(identity.id, ipAddress);
        vectors.put(identity.id, identity);
        
        double similarity = self.similarity(identity);
        System.out.println("✓ Peer registered: " + identity.id);
        System.out.println("   IP: " + ipAddress);
        System.out.println("   Resonance: " + String.format("%.2f", similarity));
    }
    
    /**
     * Collect complete peer table
     */
    public Map<String, PeerInfo> collectPeerTable() {
        Map<String, PeerInfo> table = new ConcurrentHashMap<>();
        
        // Add self
        table.put(self.id, new PeerInfo(self, getLocalIP(), PORT));
        
        // Add all peers
        for (Map.Entry<String, String> entry : peers.entrySet()) {
            String nodeId = entry.getKey();
            String ip = entry.getValue();
            NeuroQuant vector = vectors.get(nodeId);
            
            if (vector != null) {
                table.put(nodeId, new PeerInfo(vector, ip, PORT));
            }
        }
        
        return table;
    }
    
    /**
     * Get local IP address
     */
    private String getLocalIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }
    
    /**
     * Get peer count
     */
    public int getPeerCount() {
        return peers.size();
    }
    
    /**
     * Get all peer IDs
     */
    public List<String> getPeerIds() {
        return new CopyOnWriteArrayList<>(peers.keySet());
    }
    
    /**
     * PeerInfo - Information about a peer node
     */
    public static class PeerInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public final NeuroQuant identity;
        public final String ipAddress;
        public final int port;
        public final long timestamp;
        
        public PeerInfo(NeuroQuant identity, String ip, int port) {
            this.identity = identity;
            this.ipAddress = ip;
            this.port = port;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
