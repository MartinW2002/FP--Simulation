import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestCustomFloat {

    float[] testValues;
    float[] expected_E2M4;
    float[] expected_E3M4;
    float[] expected_E4M4;

    float[] all_E2M4 = makeAllE2M4();
    float[] all_E3M4 = makeAllE3M4();
    float[] all_E4M4 = makeAllE4M4();

    @Test
    void testToFloat() {
        FPType type = FPType.E4M3;
        assertEquals(2F, new CustomFloat(2f, type, null).toFloat());
        assertEquals(1.875F, new CustomFloat(1.875f, type, null).toFloat());
        assertEquals(1.875F, new CustomFloat(1.874f, type, null).toFloat());
        assertEquals(1.750F, new CustomFloat(1.750f, type, null).toFloat());

        // Denormals
        assertEquals(0.0078125F, new CustomFloat(0.0078125F, type, null).toFloat());
    }

    @BeforeEach
    void init() {


        testValues = new float[] {
                0.0f, 0.001f, 0.01f, 0.03f, 0.05f, 0.1f, 0.2f, 0.3f, 0.5f, 0.6f, 0.75f,
                1.0f, 1.1f, 1.5f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 10.0f, 12.0f,
                15.0f, 16.0f, 20.0f, 30.0f, 40.0f
        };

        expected_E2M4 = new float[] {
                0.0f, 0.0f, 0.0f, 0.0625f, 0.0625f, 0.125f, 0.1875f, 0.3125f, 0.5f, 0.5625f, 0.75f,
                1.0f, 1.125f, 1.5f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 7.75f, 7.75f, 7.75f, 7.75f,
                7.75f, 7.75f, 7.75f, 7.75f
        };

        expected_E3M4 = new float[] {
                0.0f, 0.0f, 0.015625f, 0.03125f, 0.0625f, 0.09375f, 0.1875f, 0.3125f, 0.5f,
                0.625f, 0.75f, 1.0f, 1.125f, 1.5f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f,
                10.0f, 12.0f, 15.0f, 16.0f, 20.0f, 30.0f, 40.0f
        };

        expected_E4M4 = new float[] {
                0.0f, 0.0009765625f, 0.0078125f, 0.03125f, 0.0625f, 0.09375f, 0.1875f,
                0.3125f, 0.5f, 0.625f, 0.75f, 1.0f, 1.125f, 1.5f, 2.0f, 3.0f, 4.0f, 5.0f,
                6.0f, 7.0f, 8.0f, 10.0f, 12.0f, 15.0f, 16.0f, 20.0f, 30.0f, 40.0f
        };


    }

    @Test
    void testToFloat2() {
        for (int i = 0; i < testValues.length; i++) {
            float value = testValues[i];
            System.out.println(i + " - " + value);
            CustomFloat e2Float = new CustomFloat(value, FPType.E2M4, null);
            CustomFloat e3Float = new CustomFloat(value, FPType.E3M4, null);
            CustomFloat e4Float = new CustomFloat(value, FPType.E4M4, null);

            assertEquals(expected_E2M4[i], e2Float.toFloat());
            assertEquals(expected_E3M4[i], e3Float.toFloat());
            assertEquals(expected_E4M4[i], e4Float.toFloat());
        }
    }

    @Test
    void testToFloat3() {
        for (float f : testValues) {
            System.out.println(f);
            CustomFloat e2CustomFloat = new CustomFloat(f, FPType.E2M4, null);
            CustomFloat e3CustomFloat = new CustomFloat(f, FPType.E3M4, null);
            CustomFloat e4CustomFloat = new CustomFloat(f, FPType.E4M4, null);

            float e2Float = e2CustomFloat.toFloat();
            float e3Float = e3CustomFloat.toFloat();
            float e4Float = e4CustomFloat.toFloat();

            for (float f1 : all_E2M4) {
                assert Math.abs(f1 - f) >= Math.abs(e2Float - f);
            }
            for (float f1 : all_E3M4) {
                assert Math.abs(f1 - f) >= Math.abs(e3Float - f);
            }
            for (float f1 : all_E4M4) {
                assert Math.abs(f1 - f) >= Math.abs(e4Float - f);
            }
        }
    }

    @Test
    void test() {
        CustomFloat customFloat = new CustomFloat(false, 0, 1, FPType.E2M4);
        System.out.println(customFloat.toString());
    }

    static float[] makeAllE2M4() {
        List<Float> vals = new ArrayList<>();
        // Subnormals (exp == 0 → exponent = -1)
        for (int m = 0; m < 16; m++) vals.add(m * (1f / 16));
        // Normals
        for (int e = 1; e <= 3; e++) { // exp = 1..3 → exponent = 0..2
            for (int m = 0; m < 16; m++) {
                float val = (1 + m / 16f) * (float)Math.pow(2, e - 1);
                vals.add(val);
            }
        }
        return toArray(vals);
    }

    static float[] makeAllE3M4() {
        List<Float> vals = new ArrayList<>();
        // Subnormals (exp == 0 → exponent = -2)
        for (int m = 0; m < 16; m++) vals.add(m * (1f / 64));
        for (int e = 1; e <= 7; e++) { // exponent = e - 3
            for (int m = 0; m < 16; m++) {
                float val = (1 + m / 16f) * (float)Math.pow(2, e - 3);
                vals.add(val);
            }
        }
        return toArray(vals);
    }

    static float[] makeAllE4M4() {
        List<Float> vals = new ArrayList<>();
        // Subnormals (exp == 0 → exponent = -6)
        for (int m = 0; m < 16; m++) vals.add(m * (1f / 1024));
        for (int e = 1; e <= 15; e++) { // exponent = e - 7
            for (int m = 0; m < 16; m++) {
                float val = (1 + m / 16f) * (float)Math.pow(2, e - 7);
                vals.add(val);
            }
        }
        return toArray(vals);
    }

    static float[] toArray(List<Float> list) {
        float[] arr = new float[list.size()];
        for (int i = 0; i < arr.length; i++) arr[i] = list.get(i);
        return arr;
    }
}
