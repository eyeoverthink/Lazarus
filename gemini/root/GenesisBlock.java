package gemini.root;

import java.io.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * ⛓️ THE GENESIS BLOCK
 * Role: The Atom of Memory.
 * Feature: Self-Referencing (Chained by Hash).
 * 
 * Generation: 195 - The Universal Interface
 * Philosophy: "Memory is blockchain. Truth is immutable."
 */
public class GenesisBlock implements Serializable {
    private static final long serialVersionUID = 1L;

    public String hash;
    public String previousHash;
    public String type; // "MEMORY", "CODE", "FACT", "CONVERSATION"
    public String data; // The content (JSON-like string or raw text)
    public long timestamp;

    public GenesisBlock(String previousHash, String type, String data) {
        this.previousHash = previousHash;
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    /**
     * IDENTITY: Unique based on content + history
     * Uses SHA-256 for cryptographic integrity
     */
    public String calculateHash() {
        try {
            String input = previousHash + Long.toString(timestamp) + type + data;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verify block integrity
     */
    public boolean isValid() {
        return this.hash.equals(calculateHash());
    }

    @Override
    public String toString() {
        return String.format("GenesisBlock{hash='%s...', type='%s', time=%d}", 
                           hash.substring(0, 8), type, timestamp);
    }
}
