import org.apache.commons.math3.distribution.TDistribution;

import java.util.Arrays;
import java.util.Random;

public class Vector {
    private final CustomFloat[] data;
    private final FPType type;

    public Vector(CustomFloat[] data, FPType type) {
        this.data = data;
        this.type = type;
    }

    public static Vector random(int size, FPType type) {
        CustomFloat[] arr = new CustomFloat[size];

        float stdDev = (float) Math.sqrt(2.0 / (float) size);

        if (Main.GAUSS) {
            Random random = new Random();
            for (int i = 0; i < size; i++) {
                float randomValue = (float) (random.nextGaussian() * stdDev);
                arr[i] = new CustomFloat(randomValue, type, null);
            }
        } else {
            TDistribution tDistribution = new TDistribution(Main.NU);
            double stddevT = Math.sqrt(Main.NU / (Main.NU - 2.0)); // Theoretical stddev of t-dist

            for (int i = 0; i < size; i++) {
                double tValue = tDistribution.sample();
                float scaledValue = (float) (tValue * (stdDev / stddevT));
                arr[i] = new CustomFloat(scaledValue, type, null);
            }
        }
        return new Vector(arr, type);
    }

    public CustomFloat multiply(Vector other, FPType accumulator) {
        if (this.data.length != other.data.length) {
            throw new IllegalArgumentException("Vectors must be the same length");
        }
        CustomFloat result = new CustomFloat(0F, accumulator, null);

        for (int i = 0; i < data.length; i++) {
            result = result.plus(this.data[i].times(other.data[i], accumulator));
        }

        return result;
    }

    public int size() {
        return data.length;
    }

    public CustomFloat get(int index) {
        return data[index];
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }

    public VectorPair sortWithCompanion(Vector companion, boolean ascending, boolean absolute) {
        if (this.size() != companion.size()) {
            throw new IllegalArgumentException("Vectors must have the same size");
        }

        int n = this.size();
        IndexedFloat[] indexed = new IndexedFloat[n];
        for (int i = 0; i < n; i++) {
            float value = this.data[i].toFloat();
            indexed[i] = new IndexedFloat(i, absolute ? Math.abs(value) : value);
        }

        Arrays.sort(indexed, (a, b) -> ascending
                ? Float.compare(a.value, b.value)
                : Float.compare(b.value, a.value));

        CustomFloat[] sortedMain = new CustomFloat[n];
        CustomFloat[] sortedCompanion = new CustomFloat[n];
        for (int i = 0; i < n; i++) {
            sortedMain[i] = this.data[indexed[i].index];
            sortedCompanion[i] = companion.data[indexed[i].index];
        }

        return new VectorPair(new Vector(sortedMain, type), new Vector(sortedCompanion, companion.type));
    }

    private static class IndexedFloat {
        int index;
        float value;

        IndexedFloat(int index, float value) {
            this.index = index;
            this.value = value;
        }
    }

}
