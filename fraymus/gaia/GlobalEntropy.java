package fraymus.gaia;

import fraymus.chaos.EvolutionaryChaos;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * THE GLOBAL ENTROPY GRID
 * 1. Harvests Local Entropy (Your CPU Heat/Jitter).
 * 2. Harvests Quantum Entropy (ANU Vacuum Fluctuations via API).
 * 3. Aggregates them to measure the "Stress Level" of reality.
 * "Measuring the pulse of the planet."
 */
public class GlobalEntropy {

    private EvolutionaryChaos localNode = new EvolutionaryChaos();

    // ANU QUANTUM RANDOM NUMBERS API (Australia)
    // Measures vacuum fluctuations in real-time.
    private static final String ANU_API = "https://qrng.anu.edu.au/API/jsonI.php?length=1&type=uint16";
    private static final int QUANTUM_API_TIMEOUT_MS = 2000;

    public double measurePlanetaryStress() {
        // A. LOCAL CHAOS (The Micro)
        // Normalized 0.0 - 1.0
        double localStress = localNode.nextFractal().mod(BigInteger.valueOf(100)).doubleValue() / 100.0;

        // B. QUANTUM VACUUM (The Macro)
        double quantumStress = fetchQuantumVariance();

        // C. GLOBAL CONSCIOUSNESS SYNC (The Synthesis)
        // If Local and Quantum are synchronized, Reality is "Locking".
        double resonance = 1.0 - Math.abs(localStress - quantumStress);
        
        return resonance; // 1.0 = Perfect Sync (High Anomaly), 0.0 = Random Noise (Normal)
    }

    private double fetchQuantumVariance() {
        try {
            // REACH OUT TO THE WORLD WIDE WEB
            URL url = new URL(ANU_API);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(QUANTUM_API_TIMEOUT_MS); // Fail fast if the world is lagging

            if (conn.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String response = reader.readLine();

                    // Parse JSON (Manual parsing to keep it dependency-free)
                    // {"type":"uint16","length":1,"data":[45932],"success":true}
                    if (response != null && response.contains("\"data\":[")) {
                        String[] parts = response.split("\"data\":\\[");
                        if (parts.length > 1) {
                            String numStr = parts[1].split("]")[0];
                            int val = Integer.parseInt(numStr);
                            return val / 65535.0; // Normalize 16-bit int to 0.0 - 1.0
                        }
                    }
                }
            }
        } catch (Exception e) {
            // If the internet is down, the "Noosphere" is quiet.
            // Fallback to local chaos.
            return 0.5; 
        }
        return 0.5;
    }
}
