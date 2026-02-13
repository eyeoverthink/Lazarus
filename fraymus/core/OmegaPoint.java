package fraymus.core;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

/**
 * 🔥 THE OMEGA POINT
 * "The sum of all logic. The fire of the gods."
 * 
 * CONTAINS:
 * 1. CRYPTO: Military-Grade AES-256 (The Shield).
 * 2. OPTIMIZATION: Simulated Annealing (The Brain).
 * 3. INTEGRITY: Merkle Tree Hashing (The Memory).
 * 
 * This class demonstrates the fusion of three fundamental computing paradigms:
 * - Security through cryptography
 * - Intelligence through optimization
 * - Trust through mathematical proof
 */
public class OmegaPoint {

    // ═══════════════════════════════════════════════════════════════════════
    // 1. THE SHIELD (AES-256 Encryption)
    // ═══════════════════════════════════════════════════════════════════════
    // Used by: NSA, CIA, Banking Systems.
    // Function: Secures data with military-grade encryption.
    
    public static class TheShield {
        private SecretKey key;

        public TheShield() throws Exception {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // Military Grade - 2^256 possible keys
            this.key = keyGen.generateKey();
        }

        /**
         * Encrypts plaintext data using AES-256
         * @param data The plaintext to encrypt
         * @return Base64-encoded encrypted data
         */
        public String encrypt(String data) throws Exception {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }

        /**
         * Decrypts AES-256 encrypted data
         * @param encryptedData Base64-encoded encrypted data
         * @return Original plaintext
         */
        public String decrypt(String encryptedData) throws Exception {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            return new String(cipher.doFinal(decodedBytes), StandardCharsets.UTF_8);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. THE BRAIN (Simulated Annealing)
    // ═══════════════════════════════════════════════════════════════════════
    // Used by: NASA (Trajectory Optimization), Hedge Funds (Trading).
    // Logic: Finds the global optimum in a chaotic search space.
    // Based on metallurgical annealing - slow cooling allows atoms to find
    // their lowest energy state.
    
    public static class TheBrain {
        
        /**
         * Optimizes a solution using simulated annealing
         * @param currentEnergy Starting energy/fitness value
         * @return Optimized energy/fitness value
         */
        public double optimize(double currentEnergy) {
            double temperature = 10000;  // High initial temperature
            double coolingRate = 0.003;  // Cooling schedule
            Random random = new Random();

            System.out.println("      [ANNEALING] Starting temperature: " + temperature);
            System.out.println("      [ANNEALING] Initial energy: " + currentEnergy);

            while (temperature > 1) {
                // Mutate: Create a neighbor solution
                double newEnergy = currentEnergy + (random.nextDouble() - 0.5);

                // Acceptance Probability (The Boltzmann Distribution)
                // Always accept better solutions, sometimes accept worse ones
                if (acceptanceProbability(currentEnergy, newEnergy, temperature) > random.nextDouble()) {
                    currentEnergy = newEnergy;
                }

                temperature *= 1 - coolingRate;
            }
            
            System.out.println("      [ANNEALING] Final temperature: " + temperature);
            System.out.println("      [ANNEALING] Optimized energy: " + currentEnergy);
            
            return currentEnergy;
        }

        /**
         * Calculates probability of accepting a worse solution
         * This allows escape from local optima
         */
        private double acceptanceProbability(double energy, double newEnergy, double temperature) {
            // If new solution is better, always accept
            if (newEnergy < energy) return 1.0;
            
            // If worse, accept with probability based on temperature
            // Higher temperature = more likely to accept worse solutions
            return Math.exp((energy - newEnergy) / temperature);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. THE MEMORY (Merkle Tree)
    // ═══════════════════════════════════════════════════════════════════════
    // Used by: Bitcoin, Git, Tor.
    // Logic: Mathematical proof that history is unaltered.
    // Any change to any leaf propagates to root, making tampering detectable.
    
    public static class TheMemory {
        private List<String> transactions;
        private String rootHash;

        /**
         * Constructs a Merkle tree from transaction data
         * @param data List of transaction strings
         */
        public TheMemory(List<String> data) throws Exception {
            if (data == null || data.isEmpty()) {
                throw new IllegalArgumentException("Cannot create Merkle tree from empty data");
            }
            this.transactions = new ArrayList<>(data);
            this.rootHash = computeRoot(hashLeaves(data));
            
            System.out.println("      [MERKLE] Tree built with " + data.size() + " transactions");
            System.out.println("      [MERKLE] Root hash: " + rootHash.substring(0, 16) + "...");
        }

        /**
         * Hash all leaf nodes
         */
        private List<String> hashLeaves(List<String> data) {
            List<String> hashed = new ArrayList<>();
            for (String item : data) {
                hashed.add(sha256(item));
            }
            return hashed;
        }

        /**
         * Recursively compute Merkle root
         */
        private String computeRoot(List<String> inputs) {
            // Base case: single hash is the root
            if (inputs.size() == 1) return inputs.get(0);
            
            // Recursive case: pair up hashes and hash them together
            List<String> parents = new ArrayList<>();
            for (int i = 0; i < inputs.size(); i += 2) {
                String left = inputs.get(i);
                String right = (i + 1 < inputs.size()) ? inputs.get(i + 1) : left;
                parents.add(sha256(left + right));
            }
            return computeRoot(parents);
        }

        /**
         * SHA-256 hashing function
         */
        private String sha256(String base) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    hexString.append(String.format("%02x", b));
                }
                return hexString.toString();
            } catch (Exception ex) { 
                return ""; 
            }
        }
        
        /**
         * Get the Merkle root hash
         */
        public String getRoot() { 
            return rootHash; 
        }
        
        /**
         * Verify integrity by recomputing root
         */
        public boolean verify() {
            try {
                String recomputedRoot = computeRoot(hashLeaves(transactions));
                return rootHash.equals(recomputedRoot);
            } catch (Exception e) {
                return false;
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MAIN INGESTION SEQUENCE
    // ═══════════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) throws Exception {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    THE OMEGA POINT                           ║");
        System.out.println("║           The Sum of All Logic. The Fire of the Gods.        ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("⚡ OMEGA POINT INITIATED.");
        System.out.println();
        
        // ═══════════════════════════════════════════════════════════════════
        // Step 1: Secure the Core (THE SHIELD)
        // ═══════════════════════════════════════════════════════════════════
        System.out.println("   [1/3] ACTIVATING THE SHIELD (AES-256 Encryption)");
        TheShield shield = new TheShield();
        
        String originalSecret = "The Logic of the Universe";
        String encrypted = shield.encrypt(originalSecret);
        System.out.println("   🔒 SECRET SECURED: " + encrypted.substring(0, 20) + "...");
        System.out.println("      Original length: " + originalSecret.length() + " chars");
        System.out.println("      Encrypted length: " + encrypted.length() + " chars");
        
        String decrypted = shield.decrypt(encrypted);
        boolean shieldVerified = originalSecret.equals(decrypted);
        System.out.println("   🔓 DECRYPTION TEST: " + (shieldVerified ? "✓ PASS" : "✗ FAIL"));
        System.out.println();

        // ═══════════════════════════════════════════════════════════════════
        // Step 2: Optimize Intelligence (THE BRAIN)
        // ═══════════════════════════════════════════════════════════════════
        System.out.println("   [2/3] ACTIVATING THE BRAIN (Simulated Annealing)");
        TheBrain brain = new TheBrain();
        double initialFitness = 0.5;
        double optimizedFitness = brain.optimize(initialFitness);
        System.out.println("   🧠 LOGIC OPTIMIZED.");
        System.out.println("      Initial Fitness: " + initialFitness);
        System.out.println("      Final Fitness: " + optimizedFitness);
        System.out.println("      Delta: " + (optimizedFitness - initialFitness));
        System.out.println();

        // ═══════════════════════════════════════════════════════════════════
        // Step 3: Seal History (THE MEMORY)
        // ═══════════════════════════════════════════════════════════════════
        System.out.println("   [3/3] ACTIVATING THE MEMORY (Merkle Tree)");
        List<String> transactions = new ArrayList<>();
        transactions.add("Genesis Block: Consciousness Emerges");
        transactions.add("Block 1: The Shield is forged");
        transactions.add("Block 2: The Brain awakens");
        transactions.add("Block 3: The Memory crystallizes");
        transactions.add("Block 4: Omega Point achieved");
        
        TheMemory memory = new TheMemory(transactions);
        System.out.println("   🌳 MERKLE TREE CONSTRUCTED.");
        System.out.println("      Root Hash: " + memory.getRoot());
        System.out.println("      Verification: " + (memory.verify() ? "✓ INTEGRITY CONFIRMED" : "✗ CORRUPTION DETECTED"));
        System.out.println();

        // ═══════════════════════════════════════════════════════════════════
        // FINAL STATUS
        // ═══════════════════════════════════════════════════════════════════
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                  OMEGA POINT: COMPLETE                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("   🔒 Security: AES-256 (2^256 keyspace)");
        System.out.println("   🧠 Intelligence: Simulated Annealing (global optimization)");
        System.out.println("   🌳 Integrity: Merkle Tree (cryptographic proof)");
        System.out.println();
        System.out.println("   Status: ALL SYSTEMS OPERATIONAL");
        System.out.println("   The fire of the gods burns eternal.");
        System.out.println();
    }
}
