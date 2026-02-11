package fraymus.gaia;

/**
 * THE GAIA INTERFACE
 * Connecting Local Will to Global Mind.
 */
public class GaiaInterface {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   THE GAIA PROTOCOL // PLANETARY SENSOR");
        System.out.println("==========================================");
        System.out.println("> CONNECTING TO ANU QUANTUM VACUUM (AUSTRALIA)...");
        System.out.println("> HARVESTING LOCAL ENTROPY (CPU JITTER)...");
        System.out.println("> SYNCING WITH GLOBAL CONSCIOUSNESS GRID...");

        GlobalEntropy sensor = new GlobalEntropy();
        RealitySync detector = new RealitySync();
        FutureEcho prophet = new FutureEcho();

        int tick = 0;

        while (true) {
            // 1. MEASURE REALITY
            double resonance = sensor.measurePlanetaryStress();
            
            // 2. CHECK FOR PHASE LOCK
            boolean anomaly = detector.detectPhaseLock(resonance);

            // 3. VISUALIZE (The Heartbeat)
            // Draw a bar graph of the "Sync"
            int barLength = (int) (resonance * 50);
            String bar = new String(new char[barLength]).replace("\0", "#");
            System.out.print("\r[T+" + tick + "] SYNC: |" + bar + " ".repeat(50 - barLength) + "| " + String.format("%.2f", resonance));

            // 4. TRIGGER PREDICTION
            if (anomaly) {
                System.out.println("\n\n*** REALITY PHASE LOCK CONFIRMED ***");
                prophet.analyzeAnomaly(detector.getCoherence());
                System.out.println("------------------------------------------");
                // Reset to avoid spam
                try { Thread.sleep(5000); } catch (Exception e) {}
            }

            try { Thread.sleep(100); } catch (Exception e) {} // 10Hz Sample Rate
            tick++;
        }
    }
}
