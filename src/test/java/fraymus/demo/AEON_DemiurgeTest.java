package fraymus.demo;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class AEON_DemiurgeTest {

    @AfterEach
    void resetField() {
        AEON_Demiurge.resetUnifiedField();
    }

    @Test
    void conceptVectorsAreDeterministic() {
        long[] first = AEON_Demiurge.conceptVector("Quantum");
        long[] second = AEON_Demiurge.conceptVector("Quantum");
        assertArrayEquals(first, second);
    }

    @Test
    void xorWithSelfClearsVector() {
        long[] vec = AEON_Demiurge.conceptVector("Entropy");
        long[] result = AEON_Demiurge.xorVectors(vec, vec);
        for (long word : result) {
            assertEquals(0L, word, "XOR with self should yield zero vector");
        }
    }

    @Test
    void oracleRecoversPayloadThroughNoise() {
        String payload = "SECRETCHANNEL";
        long[] payloadVec = AEON_Demiurge.payloadVector(payload);
        long[] noisy = AEON_Demiurge.injectNoise(payloadVec, 0.95, new Random(42));
        long[] recovered = AEON_Demiurge.recoverWithTemplate(noisy, payloadVec);
        String recoveredPayload = AEON_Demiurge.decodePayload(recovered, payload.length());

        assertEquals(payload, recoveredPayload);
        assertTrue(AEON_Demiurge.similarity(payloadVec, recovered) > 0.5);
    }

    @Test
    void bigBangAddsParticlesAndFieldEnergy() {
        AEON_Demiurge engine = new AEON_Demiurge(false);
        engine.spawnParticles(5);

        assertEquals(5, engine.getParticleCount());

        boolean fieldChanged = false;
        for (int i = 0; i < AEON_Demiurge.DIMS; i++) {
            if (AEON_Demiurge.UNIFIED_FIELD.get(i) != 0) {
                fieldChanged = true;
                break;
            }
        }
        assertTrue(fieldChanged, "Unified field should reflect spawned particles");

        engine.shutdown();
    }
}
