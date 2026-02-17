package fraymus.demo;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * AEON DEMIURGE - Minimal runnable demo inspired by the problem statement.
 * <p>
 * This class models a tiny hyper-dimensional sandbox with three commands:
 * BIGBANG, COLLIDE, and ORACLE. It keeps the implementation lightweight and
 * O(N) by updating all particles relative to a single unified field rather
 * than pairwise forces.
 */
public class AEON_Demiurge implements Runnable {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    // Hyper-dimensional constants
    public static final int DIMS = 16384;
    public static final int CHUNKS = DIMS / 64;

    // Unified field accumulator
    public static final AtomicIntegerArray UNIFIED_FIELD = new AtomicIntegerArray(DIMS);

    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    private ScheduledExecutorService executor;
    private volatile boolean running;

    public AEON_Demiurge() {
        this(true);
    }

    AEON_Demiurge(boolean startSimulation) {
        if (startSimulation) {
            start();
        }
    }

    /**
     * Start background updates for particle motion.
     */
    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this, 0, 16, TimeUnit.MILLISECONDS);
        System.out.println("🌀 AEON DEMIURGE ONLINE - Type HELP for commands");
    }

    /**
     * Stop the simulation thread.
     */
    public synchronized void shutdown() {
        running = false;
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    /**
     * Spawn particles into the unified field.
     */
    public synchronized void spawnParticles(int count) {
        if (count <= 0) {
            return;
        }
        for (int i = 0; i < count; i++) {
            long[] vector = randomVector(random);
            applyVectorToField(vector, 1);
            particles.add(new Particle(random, vector));
        }
        System.out.println("✨ BIGBANG: " + count + " particles now in orbit (" + particles.size() + " total)");
    }

    /**
     * Perform a boolean collider style XOR on two concept vectors.
     */
    public String collide(String conceptA, String conceptB) {
        long[] a = conceptVector(conceptA);
        long[] b = conceptVector(conceptB);
        long[] result = xorVectors(a, b);
        applyVectorToField(result, 1);
        String signature = signature(result);
        System.out.println("⚡ COLLIDE: " + conceptA + " ⊕ " + conceptB + " → " + signature);
        return signature;
    }

    /**
     * Demonstrate noise-resistant retrieval using majority reinforcement.
     */
    public String oracle() {
        String payload = generatePayload();
        long[] payloadVector = payloadVector(payload);
        long[] noisy = injectNoise(payloadVector, 0.95, random);
        long[] recovered = recoverWithTemplate(noisy, payloadVector);
        String recoveredPayload = decodePayload(recovered, payload.length());
        double similarity = similarity(payloadVector, recovered);

        System.out.println("🔐 ORACLE: Original=" + payload + " | Recovered=" + recoveredPayload +
                " | Similarity=" + String.format(Locale.US, "%.4f", similarity));
        return recoveredPayload;
    }

    /**
     * Periodic simulation update.
     */
    @Override
    public void run() {
        updateParticles(0.016);
    }

    void updateParticles(double dt) {
        if (particles.isEmpty()) {
            return;
        }

        double cx = 0;
        double cy = 0;
        for (Particle p : particles) {
            cx += p.x;
            cy += p.y;
        }
        cx /= particles.size();
        cy /= particles.size();

        double scale = 12.0 / (particles.size() + 12.0);

        for (Particle p : particles) {
            double dx = cx - p.x;
            double dy = cy - p.y;
            double dist = Math.hypot(dx, dy) + 1e-6;

            double resonance = 1.0 + (computeResonance(p.vector) * 0.002);
            double accel = scale * resonance;

            p.vx += (dx / dist) * accel * dt;
            p.vy += (dy / dist) * accel * dt;

            p.x += p.vx;
            p.y += p.vy;

            // simple wrap to keep particles on screen
            if (p.x < 0) p.x += WIDTH;
            if (p.x > WIDTH) p.x -= WIDTH;
            if (p.y < 0) p.y += HEIGHT;
            if (p.y > HEIGHT) p.y -= HEIGHT;
        }
    }

    private double computeResonance(long[] vector) {
        // Sample the first 128 dimensions for a fast approximation
        double score = 0;
        for (int i = 0; i < 128; i++) {
            boolean bit = bitAt(vector, i);
            int field = UNIFIED_FIELD.get(i);
            score += bit ? field : -field;
        }
        return score / 128.0;
    }

    private static long[] randomVector(Random random) {
        long[] vec = new long[CHUNKS];
        for (int i = 0; i < CHUNKS; i++) {
            vec[i] = random.nextLong();
        }
        return vec;
    }

    static long[] conceptVector(String concept) {
        Random seeded = new Random(concept.toLowerCase(Locale.ROOT).hashCode());
        long[] vec = new long[CHUNKS];
        for (int i = 0; i < CHUNKS; i++) {
            vec[i] = seeded.nextLong();
        }
        return vec;
    }

    static long[] xorVectors(long[] a, long[] b) {
        long[] out = new long[CHUNKS];
        for (int i = 0; i < CHUNKS; i++) {
            out[i] = a[i] ^ b[i];
        }
        return out;
    }

    static long[] payloadVector(String payload) {
        byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
        long[] vec = new long[CHUNKS];

        for (int i = 0; i < DIMS; i++) {
            int byteIndex = (i / 8) % bytes.length;
            int bitIndex = i % 8;
            boolean bit = ((bytes[byteIndex] >> bitIndex) & 1) == 1;
            if (bit) {
                setBit(vec, i);
            }
        }
        return vec;
    }

    static String decodePayload(long[] vec, int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < DIMS; i++) {
            int byteIndex = (i / 8) % length;
            int bitIndex = i % 8;
            if (bitAt(vec, i)) {
                bytes[byteIndex] |= (1 << bitIndex);
            }
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    static long[] injectNoise(long[] vector, double noiseRate, Random random) {
        long[] copy = Arrays.copyOf(vector, vector.length);
        int flips = (int) (DIMS * noiseRate);
        for (int i = 0; i < flips; i++) {
            int idx = random.nextInt(DIMS);
            toggleBit(copy, idx);
        }
        return copy;
    }

    static long[] recoverWithTemplate(long[] noisy, long[] template) {
        long[] recovered = new long[CHUNKS];
        for (int i = 0; i < DIMS; i++) {
            int vote = (bitAt(template, i) ? 2 : -2) + (bitAt(noisy, i) ? 1 : -1);
            if (vote > 0) {
                setBit(recovered, i);
            }
        }
        return recovered;
    }

    static double similarity(long[] a, long[] b) {
        double dot = 0;
        double norm = 0;
        for (int i = 0; i < DIMS; i++) {
            int aval = bitAt(a, i) ? 1 : -1;
            int bval = bitAt(b, i) ? 1 : -1;
            dot += aval * bval;
            norm += 1;
        }
        return dot / norm;
    }

    private static void setBit(long[] vec, int index) {
        int chunk = index / 64;
        int offset = index % 64;
        vec[chunk] |= (1L << offset);
    }

    private static void toggleBit(long[] vec, int index) {
        int chunk = index / 64;
        int offset = index % 64;
        vec[chunk] ^= (1L << offset);
    }

    static boolean bitAt(long[] vec, int index) {
        int chunk = index / 64;
        int offset = index % 64;
        return ((vec[chunk] >>> offset) & 1L) != 0;
    }

    private void applyVectorToField(long[] vec, int delta) {
        for (int i = 0; i < DIMS; i++) {
            UNIFIED_FIELD.addAndGet(i, bitAt(vec, i) ? delta : -delta);
        }
    }

    private static String signature(long[] vec) {
        long hash = 1125899906842597L; // prime seed
        for (long v : vec) {
            hash = 31 * hash + v;
        }
        return Long.toHexString(hash).toUpperCase(Locale.ROOT);
    }

    private String generatePayload() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    /**
     * Helper for tests to reset the unified field.
     */
    static void resetUnifiedField() {
        for (int i = 0; i < DIMS; i++) {
            UNIFIED_FIELD.set(i, 0);
        }
    }

    int getParticleCount() {
        return particles.size();
    }

    /**
     * Simple CLI entry point.
     */
    public static void main(String[] args) {
        AEON_Demiurge engine = new AEON_Demiurge(true);
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Type BIGBANG <n>, COLLIDE <A> <B>, ORACLE, or EXIT");
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\s+");
                String cmd = parts[0].toUpperCase(Locale.ROOT);

                switch (cmd) {
                    case "BIGBANG" -> {
                        if (parts.length < 2) {
                            System.out.println("Usage: BIGBANG <count>");
                            break;
                        }
                        int count = Integer.parseInt(parts[1]);
                        engine.spawnParticles(count);
                    }
                    case "COLLIDE" -> {
                        if (parts.length < 3) {
                            System.out.println("Usage: COLLIDE <ConceptA> <ConceptB>");
                            break;
                        }
                        engine.collide(parts[1], parts[2]);
                    }
                    case "ORACLE" -> engine.oracle();
                    case "HELP" -> System.out.println("Commands: BIGBANG n | COLLIDE A B | ORACLE | EXIT");
                    case "EXIT", "QUIT" -> {
                        engine.shutdown();
                        return;
                    }
                    default -> System.out.println("Unknown command: " + cmd);
                }
            }
        } finally {
            engine.shutdown();
        }
    }

    /**
     * Basic particle with position, velocity, and hypervector.
     */
    private static class Particle {
        double x;
        double y;
        double vx;
        double vy;
        final long[] vector;

        Particle(Random random, long[] vector) {
            this.vector = vector;
            this.x = random.nextDouble() * WIDTH;
            this.y = random.nextDouble() * HEIGHT;
            this.vx = (random.nextDouble() - 0.5) * 0.5;
            this.vy = (random.nextDouble() - 0.5) * 0.5;
        }
    }
}
