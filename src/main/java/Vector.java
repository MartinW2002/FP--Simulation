import org.apache.commons.math3.distribution.TDistribution;

import java.util.Arrays;
import java.util.Random;

public class Vector {
    private final CustomFloat[] data;
    private final FloatType type;

    public Vector(CustomFloat[] data, FloatType type) {
        this.data = data;
        this.type = type;
    }

    public static Vector random(int size, FloatType type) {
        CustomFloat[] arr = new CustomFloat[size];

        float stdDev = 1.0f / (float) Math.pow(size, 0.5);

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

    public CustomFloat multiply(Vector other, FloatType accumulator) {
        if (this.data.length != other.data.length) {
            throw new IllegalArgumentException("Vectors must be the same length");
        }
        CustomFloat result = new CustomFloat(0F, accumulator, null);

        for (int i = 0; i < data.length; i++) {
            /** Debugging
            if (accumulator.equals(FloatType.DOUBLE_64)) {
                Main.stringBuilders[2 * i] = new StringBuilder();
                Main.stringBuilders[2 * i + 1] = new StringBuilder();
                Main.stringBuilders[2 * i].append(this.data[i]).append(" * ").append(other.data[i]).append(" = ");
            }
            Main.stringBuilders[2 * i + 1].append(result).append(" -> ");
            **/

            CustomFloat toAdd = this.data[i].times(other.data[i], accumulator);
//            CustomFloat toAdd = this.data[i].times(other.data[i], FPType.DOUBLE_64);
            result = result.plus(toAdd);
            /** Debugging
            Main.stringBuilders[2 * i + 1].append(result).append(" | ");
            Main.stringBuilders[2 * i].append(toAdd).append(" | ");
            **/
            // TODO Fix
//            if (accumulator == FPType.DOUBLE_64)
//                Main.resultsArray[3 * i] = result.toFloat();
//            if (accumulator == FPType.E2M4)
//                Main.resultsArray[3 * i + 1] = result.toFloat();
//            if (accumulator == FPType.E3M4)
//                Main.resultsArray[3 * i + 2] = result.toFloat();
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

    public static Vector fromString(String input, FloatType type) {
        input = input.trim();
        if (input.startsWith("[") && input.endsWith("]")) {
            input = input.substring(1, input.length() - 1); // remove brackets
        }

        String[] entries = input.split("\\),\\s*"); // split on "), "
        CustomFloat[] data = new CustomFloat[entries.length];

        for (int i = 0; i < entries.length; i++) {
            String entry = entries[i].trim();
            if (!entry.endsWith(")")) {
                entry = entry + ")"; // Add back the closing parenthesis if removed by split
            }

            int valueEnd = entry.indexOf(" (");
            float value = Float.parseFloat(entry.substring(0, valueEnd).trim());

            data[i] = new CustomFloat(value, type, null);
        }

        return new Vector(data, type);
    }


}
