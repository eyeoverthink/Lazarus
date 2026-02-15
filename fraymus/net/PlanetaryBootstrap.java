package fraymus.net;

import fraymus.bio.NeuroQuant;
import fraymus.net.PlanetaryNode.PeerInfo;
import java.io.*;
import java.net.Socket;
import java.util.Map;

/**
 * PlanetaryBootstrap - Bootstrap Protocol for Planetary Cortex
 * 
 * Allows nodes to either:
 * - Genesis Mode: Start as the first node in the universe
 * - Join Mode: Connect to a seed node and join the network
 */
public class PlanetaryBootstrap {
    
    private final PlanetaryNode localNode;
    
    public PlanetaryBootstrap(PlanetaryNode node) {
        this.localNode = node;
    }
    
    /**
     * GENESIS MODE: Start as the first node in the universe
     */
    public void startAsGenesis() {
        System.out.println("");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("🌱 GENESIS MODE ACTIVATED");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("");
        System.out.println("Creating the FIRST NODE in the universe...");
        System.out.println("");
        System.out.println("   Node ID: " + localNode.self.id);
        System.out.println("   Vector Signature: " + formatVector(localNode.self.getVector()));
        System.out.println("");
        
        // Mark as genesis
        localNode.markAsGenesis();
        
        // Start server
        localNode.startServer();
        
        System.out.println("✅ GENESIS COMPLETE");
        System.out.println("");
        System.out.println("This node is now the SEED of the global brain");
        System.out.println("Waiting for other nodes to join...");
        System.out.println("");
        System.out.println("═══════════════════════════════════════════════════════");
    }
    
    /**
     * JOIN MODE: Connect to a known seed node
     */
    public void connectToSeed(String seedIp, int seedPort) {
        System.out.println("");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("🚀 BOOTSTRAP: Attempting handshake with " + seedIp + ":" + seedPort);
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("");
        
        try (Socket socket = new Socket(seedIp, seedPort);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            
            // 1. SEND MY IDENTITY (Who I Am)
            System.out.println("Step 1: Sending Vector Identity");
            System.out.println("   Node ID: " + localNode.self.id);
            System.out.println("   Vector: " + formatVector(localNode.self.getVector()));
            out.writeObject(localNode.self);
            
            // 2. RECEIVE SEED IDENTITY (Who They Are)
            System.out.println("");
            System.out.println("Step 2: Receiving Seed Identity");
            Object response = in.readObject();
            if (response instanceof NeuroQuant) {
                NeuroQuant seedIdentity = (NeuroQuant) response;
                double resonance = localNode.self.similarity(seedIdentity);
                
                System.out.println("   ✓ Handshake Accepted");
                System.out.println("   Connected to: " + seedIdentity.id);
                System.out.println("   Resonance: " + String.format("%.2f", resonance));
                
                // Register the seed as my first peer
                localNode.registerPeer(seedIdentity, seedIp);
            }
            
            // 3. DOWNLOAD THE HIVE MAP (Gossip)
            System.out.println("");
            System.out.println("Step 3: Requesting Peer Table");
            System.out.println("   Downloading hive map...");
            out.writeObject("SYNC_PEERS");
            
            // 4. RECEIVE PEER TABLE
            System.out.println("");
            System.out.println("Step 4: Peer Synchronization");
            Object peerData = in.readObject();
            if (peerData instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, PeerInfo> peerTable = (Map<String, PeerInfo>) peerData;
                
                System.out.println("   Received " + peerTable.size() + " peers:");
                
                // Register all peers
                int count = 0;
                for (Map.Entry<String, PeerInfo> entry : peerTable.entrySet()) {
                    PeerInfo info = entry.getValue();
                    
                    // Don't register self
                    if (!info.identity.id.equals(localNode.self.id)) {
                        localNode.registerPeer(info.identity, info.ipAddress);
                        count++;
                        
                        System.out.println("      • " + info.identity.id + 
                                         " (" + info.ipAddress + ":" + info.port + ")");
                    }
                }
                
                System.out.println("");
                System.out.println("   ✓ Registered " + count + " peers");
            }
            
            System.out.println("");
            System.out.println("✅ BOOTSTRAP COMPLETE");
            System.out.println("");
            System.out.println("Successfully joined the global brain!");
            System.out.println("Known peers: " + localNode.getPeerCount());
            System.out.println("Network ready for queries");
            System.out.println("");
            System.out.println("═══════════════════════════════════════════════════════");
            
        } catch (IOException e) {
            System.err.println("");
            System.err.println("❌ BOOTSTRAP FAILED");
            System.err.println("   Connection error: " + e.getMessage());
            System.err.println("");
            System.err.println("Possible causes:");
            System.err.println("   • Seed node is not running");
            System.err.println("   • Incorrect IP address or port");
            System.err.println("   • Network connectivity issues");
            System.err.println("");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Protocol error: " + e.getMessage());
        }
    }
    
    /**
     * Format vector for display (first 5 elements)
     */
    private String formatVector(double[] vector) {
        if (vector.length == 0) return "[]";
        
        StringBuilder sb = new StringBuilder("[");
        int limit = Math.min(5, vector.length);
        for (int i = 0; i < limit; i++) {
            sb.append(String.format("%.2f", vector[i]));
            if (i < limit - 1) sb.append(", ");
        }
        if (vector.length > limit) {
            sb.append(", ...");
        }
        sb.append("]");
        return sb.toString();
    }
}
