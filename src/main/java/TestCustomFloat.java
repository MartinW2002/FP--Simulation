import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestCustomFloat {

    @Test
    void testToFloat() {
        FPType type = FPType.E4M3;
        assertEquals(2F, new CustomFloat(2f, type, null).toFloat());
        assertEquals(1.875F, new CustomFloat(1.875f, type, null).toFloat());
        assertEquals(1.875F, new CustomFloat(1.874f, type, null).toFloat());
        assertEquals(1.750F, new CustomFloat(1.750f, type, null).toFloat());

        // Denormals
        assertEquals(0.0078125F, new CustomFloat(0.0078125F, type, null).toString());
    }
}
