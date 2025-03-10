import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomFloatTest {

    CustomFloat fp8_1;
    CustomFloat fp8_2;


    @BeforeEach
    void setUp() {
        fp8_1 = new CustomFloat(12f, 8, 3);
    }

    @Test
    void toFloat() {
        assertEquals(12f, fp8_1.toFloat());
    }

    @Test
    void getBitRepresentation() {
        assertEquals("", fp8_1.toString());
    }

    @Test
    void add() {
    }

    @Test
    void substract() {
    }

    @Test
    void multiply() {
    }

    @Test
    void getTotalBits() {
    }

    @Test
    void getExponentBits() {
    }
}