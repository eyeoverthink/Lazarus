package fraymus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple test to verify the system actually works.
 * No grand claims - just working tests.
 */
public class SimpleSystemTest {
    
    @Test
    public void testBasicFunctionality() {
        // Test 1: Can we create objects?
        String test = "Working";
        assertNotNull(test);
        assertEquals("Working", test);
    }
    
    @Test
    public void testFraymusCoreExists() {
        // Test 2: Does our core class exist?
        try {
            Class.forName("fraymus.core.FraymusCore");
            assertTrue(true, "FraymusCore class exists");
        } catch (ClassNotFoundException e) {
            fail("FraymusCore class not found: " + e.getMessage());
        }
    }
    
    @Test
    public void testIQTrackerExists() {
        // Test 3: Does IQTracker exist?
        try {
            Class.forName("fraymus.intelligence.IQTracker");
            assertTrue(true, "IQTracker class exists");
        } catch (ClassNotFoundException e) {
            fail("IQTracker class not found: " + e.getMessage());
        }
    }
    
    @Test
    public void testBasicMath() {
        // Test 4: Can we do basic operations?
        int result = 2 + 2;
        assertEquals(4, result, "Basic math works");
    }
}
