import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    // Grafieken voor
    // Gaus vs t distr
    // E5M2, E4M3, E3M4
    // Vergelijken mantissa en vergelijken exponent

    //    public static FPType MAIN_TYPE = FPType.E3M4;
    public static int NU = 3;
    public static boolean GAUSS = true; // True: Gaussian Distribution, False: t-distribution
    public static int N = 256; // 16, 64, 256 or 1024 - Must be even power of 2

//    public static FloatType[] MAIN_TYPES = {FloatType.E3M4};
    public static FloatType[] MAIN_TYPES = {FloatType.E3M4, FloatType.E4M3, FloatType.E5M2};
//    public static FloatType[] MAIN_TYPES = {FloatType.E6M3, FloatType.E4M3};

    public static void main(String[] args) throws FileNotFoundException {
        // Build a timestamp like 2025-07-26_18-41
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM_HH-mm"));

        // Create a filename with the timestamp
        String filename = "output_" + timestamp + ".txt";

        // Redirect System.out to that file
        PrintStream fileOut = new PrintStream(filename);
//        System.setOut(fileOut);

//        main_accuracy();
        main_kwantisatie();
//        main_order();

        fileOut.close();
    }


    public static void main_kwantisatie() {
        System.out.println((GAUSS ? "Gaussian" : "T-distribution"));
        int size = 16;
        int nIter = 256000 / size;

        System.out.println(size + " x " + size);
        System.out.println(nIter + " iterations");

        for (FloatType testType : MAIN_TYPES) {
            FloatType comparisonType = FloatType.SINGLE_32;

            double totalMSE = 0;
            for (int i = 0; i < nIter; i++) {
                Matrix comparisonMatrix = Matrix.createRandomMatrix(size, size, comparisonType, testType.getStdDev(), CustomFloat.MAX_VALUE(testType).toFloat());

                Matrix testMatrix = new Matrix(comparisonMatrix, testType);

                double mse = Matrix.MSE(comparisonMatrix, testMatrix);
                totalMSE += mse;
            }
            System.out.println(testType);

            System.out.println((totalMSE / (float) nIter));

            System.out.println("-----");
        }
    }

    public static void main_accuracy() {
        long startTime = System.nanoTime();

        int size = N;
        int nIter = 1024 / N * 16;

        System.out.println(size + " x " + size);
        System.out.println(nIter + " iterations");
//        System.out.println("Testing " + (MANTISSA ? "mantissa" : "exponent"));
        System.out.println("-------------");

        // Loop through the three main types
        for (FloatType mainType : MAIN_TYPES) {

            System.out.println(mainType + " - " + (GAUSS ? "Gaussian" : "T-distribution"));

            double kwantFout = getKwantisatieFout(mainType);

            // 1024
            int eBegin = mainType.getExponentBits() + 1;
            int eEnd = eBegin + 2;
            int mBegin = 9;
            int mEnd = 13;

            // 16 - 64
//            int eBegin = mainType.getExponentBits();
//            int eEnd = eBegin + 2;
//            int mBegin = 5;
//            int mEnd = 10;

            // 256
//            int eBegin = mainType.getExponentBits() + 1;
//            int eEnd = eBegin + 2;
//            int mBegin = 7;
//            int mEnd = 12;

            // Steff
//            int eBegin = 8;
//            int eEnd = 8;
//            int mBegin = 5;
//            int mEnd = 10;

            List<FloatType> types = new ArrayList<>();
            for (int e = eBegin; e <= eEnd; e++) {
                for (int m = mBegin; m <= mEnd; m++) {
                    types.add(new FloatType(e, m));
                }
            }

            int numTypes = (eEnd + 1) * 32;

            double[] totalErrorArray = new double[numTypes]; // e * 32 + m
            double[][] errorValues = new double[numTypes][nIter]; // Stores all MSEs for std dev

            for (int i = 0; i < nIter; i++) {

                Matrix matrix1 = Matrix.createRandomMatrix(size, size, mainType);
                Matrix matrix2 = Matrix.createRandomMatrix(size, size, mainType);

                Matrix exactProduct = matrix1.times(matrix2, FloatType.DOUBLE_64);
                if (i == 0) {
                    int percentOverflow = (int) Math.round(100.0 * exactProduct.getNOverflows() / (exactProduct.getNCols()
                            * exactProduct.getNRows()));
                    System.out.println("Exactproduct overflows: " + percentOverflow + "%");
                    System.out.println("Std Dev1: " + matrix1.calculateStandardDeviation());
                    System.out.println("Std Dev2: " + matrix2.calculateStandardDeviation());
                    System.out.println("Std Dev: " + exactProduct.calculateStandardDeviation());
                }
                for (FloatType type : types) {

                    Matrix product = matrix1.times(matrix2, type);
//                System.out.println(type + " product number of overflows: " + product.getNOverflows() + "/"
//                        + product.getNCols() * product.getNRows());

                    double error = Matrix.MSE(exactProduct, product);

                    totalErrorArray[type.ordinal()] += error;
                    errorValues[type.ordinal()][i] = error / kwantFout;
                }
                System.out.println((i + 1) + "/" + nIter);
            }
            for (int i = 0; i < totalErrorArray.length; i++) {
                totalErrorArray[i] /= nIter;
                totalErrorArray[i] /= kwantFout;
            }

            // Compute standard deviation
            double[] stdDevArray = new double[numTypes];
            for (FloatType type : types) {
                int index = type.ordinal();
                double mean = totalErrorArray[index];
                double varianceSum = 0.0;

                for (int i = 0; i < nIter; i++) {
                    varianceSum += Math.pow(errorValues[index][i] - mean, 2);
                }

                stdDevArray[index] = Math.sqrt(varianceSum / nIter);
            }

            for (FloatType type : types) {
                System.out.println(type + ": Mean Error = " + totalErrorArray[type.ordinal()] +
                        ", Std Dev = " + stdDevArray[type.ordinal()]);
            }
            System.out.println(mainType + " - " + (GAUSS ? "Gaussian" : "T-distribution") + " - " + kwantFout);
            System.out.println("----------------");

            System.out.print("e\\m\t\t");
            for (int m = mBegin; m <= mEnd; m++) {
                System.out.print(m + "\t\t");
            }
            System.out.println();

            for (int e = eBegin; e <= eEnd; e++) {
                System.out.print(e + "\t");
                for (int m = mBegin; m <= mEnd; m++) {
                    FloatType type = new FloatType(e, m);
                    double value = totalErrorArray[type.ordinal()];
                    System.out.printf("%.6f\t", value);
                }
                System.out.println();
            }

            System.out.println("----------------");

            StringBuilder stringBuilder1 = new StringBuilder();
            StringBuilder stringBuilder2 = new StringBuilder();
            for (FloatType type : types) {
                stringBuilder1.append("'");
                stringBuilder1.append(type.toString());
                stringBuilder1.append("'");
                stringBuilder1.append(", ");

                stringBuilder2.append(totalErrorArray[type.ordinal()]);
                stringBuilder2.append(", ");
            }
            System.out.println(stringBuilder1);
            System.out.println(stringBuilder2);

            System.out.println("----------------");
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("Execution time: " + duration / 1_000_000_000 + " s");
    }
    /*
    public static void main_accuracy2() {
        long startTime = System.nanoTime();

        // 120 x 32 or 15 x 256

        int size = 256;
        int nIter = 45;
        if (SIZE_32) {
            size = 32;
            nIter = 360;
        }

        FPType[] types = MANTISSA ? FPType.MANTISSA_TYPES : FPType.EXPONENT_TYPES;

//        FPType[] types = {FPType.E2M4, FPType.E3M4};

        System.out.println(size + " x " + size);
        System.out.println(nIter + " iterations");
        System.out.println("Testing " + (MANTISSA ? "mantissa" : "exponent"));
        System.out.println("-------------");

        for (FPType mainType : MAIN_TYPES) {

            System.out.println(mainType + " - " + (GAUSS ? "Gaussian" : "T-distribution"));

            int numTypes = FPType.values().length;

            double[] totalErrorArray = new double[numTypes];

            for (int i = 0; i < nIter; i++) {

                Matrix matrix1 = Matrix.createRandomMatrix(size, size, mainType, 1.0f / (float) Math.sqrt(size));
                Matrix matrix2 = Matrix.createRandomMatrix(size, size, mainType, 1);

                Matrix exactProduct = matrix1.times(matrix2, FPType.DOUBLE_64);
                for (FPType type : types) {

                    Matrix product = matrix1.times(matrix2, type);
                    double error = Matrix.MSE(exactProduct, product);

                    totalErrorArray[type.ordinal()] += error;
                }
                System.out.println((i + 1) + "/" + nIter);
            }
            for (int i = 0; i < totalErrorArray.length; i++) {
                totalErrorArray[i] /= nIter;
            }

            StringBuilder stringBuilder1 = new StringBuilder();
            StringBuilder stringBuilder2 = new StringBuilder();
            for (FPType type : types) {
                stringBuilder1.append("'");
                stringBuilder1.append(type.toString());
                stringBuilder1.append("'");
                stringBuilder1.append(", ");

                stringBuilder2.append(totalErrorArray[type.ordinal()]);
                stringBuilder2.append(", ");
            }
            System.out.println(stringBuilder1);
            System.out.println(stringBuilder2);

            System.out.println("----------------");
        }
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("Execution time: " + duration / 1_000_000_000 + " s");
    }

    public static void main_order() {
        long startTime = System.nanoTime();

        int nIter = 100;
        int size = 200;

        FPType mainType = FPType.E3M4;
        FPType accType = FPType.E3M5;

        double totalErrorRandom = 0;
        double totalErrorAscending = 0;
        double totalErrorDescending = 0;
        double totalErrorAscendingAbs = 0;
        double totalErrorDescendingAbs = 0;

        for (int i = 0; i < nIter; i++) {

            Vector vector1 = Vector.random(size, mainType);
            Vector vector2 = Vector.random(size, mainType);

            VectorPair ascendingPair = vector1.sortWithCompanion(vector2, true, false);
            VectorPair descendingPair = vector1.sortWithCompanion(vector2, false, false);
            VectorPair ascendingPairAbs = vector1.sortWithCompanion(vector2, true, true);
            VectorPair descendingPairAbs = vector1.sortWithCompanion(vector2, false, true);

            CustomFloat exactResult = vector1.multiply(vector2, FPType.DOUBLE_64);

            CustomFloat randomResult = vector1.multiply(vector2, accType);
            CustomFloat ascendingResult = ascendingPair.v1.multiply(ascendingPair.v2, accType);
            CustomFloat descendingResult = descendingPair.v1.multiply(descendingPair.v2, accType);
            CustomFloat ascendingResultAbs = ascendingPairAbs.v1.multiply(ascendingPairAbs.v2, accType);
            CustomFloat descendingResultAbs = descendingPairAbs.v1.multiply(descendingPairAbs.v2, accType);

            totalErrorRandom += Math.pow(exactResult.toFloat() - randomResult.toFloat(), 2);
            totalErrorAscending += Math.pow(exactResult.toFloat() - ascendingResult.toFloat(), 2);
            totalErrorDescending += Math.pow(exactResult.toFloat() - descendingResult.toFloat(), 2);
            totalErrorAscendingAbs += Math.pow(exactResult.toFloat() - ascendingResultAbs.toFloat(), 2);
            totalErrorDescendingAbs += Math.pow(exactResult.toFloat() - descendingResultAbs.toFloat(), 2);
        }
        double randomMSE = totalErrorRandom / nIter;
        double ascendingMSE = totalErrorAscending / nIter;
        double descendingMSE = totalErrorDescending / nIter;
        double ascendingAbsMSE = totalErrorAscendingAbs / nIter;
        double descendingAbsMSE = totalErrorDescendingAbs / nIter;

        System.out.println("Random: " + randomMSE);
        System.out.println("Ascending: " + ascendingMSE);
        System.out.println("Descending: " + descendingMSE);
        System.out.println("Ascending Abs: " + ascendingAbsMSE);
        System.out.println("Descending Abs: " + descendingAbsMSE);

        System.out.println("------------------");

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("Execution time: " + duration / 1_000_000_000 + " s");
    }

    public static void main_test() {
        FPType[] typesToTest = {FPType.E2M4, FPType.E3M4};
        for (FPType type : typesToTest) {
            System.out.println(type);
            System.out.println(Arrays.toString(type.allPossible()));
            System.out.println("---------------");
        }
    }
    */

    public static void printMatrix(Matrix matrix, int n) {
        System.out.print("{");
        for (int i = 0; i < n; i++) {
            System.out.print(matrix.get(0, i));
            System.out.print(",");
        }
        System.out.println("}");
    }

    public static final int TEST2_N = 200;

    public static StringBuilder[] stringBuilders = new StringBuilder[TEST2_N * 2];
    public static float[] resultsArray = new float[TEST2_N * 3];

    public static double getKwantisatieFout(FloatType type) {
        if (GAUSS) {
        if (type.equals(FloatType.E3M4)) {
            return 0.002827f;
        } else if (type.equals(FloatType.E4M3)) {
            return 2.8929f;
        } else if (type.equals(FloatType.E5M2)) {
            return 749675.75f;
        } else if (type.equals(FloatType.E8M3)) {
            return 1.2764053084209045E72;
        } else if (type.equals(FloatType.E6M3)) {
            return 8.119280536755954E14;
        } else {
            throw new RuntimeException("Invalid type: " + type);
        }
        } else {
            if (type.equals(FloatType.E3M4)) {
                return 0.00234f;
            } else if (type.equals(FloatType.E4M3)) {
                return 2.38074f;
            } else if (type.equals(FloatType.E5M2)) {
                return 614191f;
            } else {
                throw new RuntimeException("Invalid type: " + type);
            }
        }
    }

    public static void test() {
        Matrix matrix = Matrix.createRandomMatrix(10,10, FloatType.E5M2);
        double total = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                total += matrix.get(i, j).toFloat();
            }
        }
        total /= 100.0;

        double squaredDiffSum = 0;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                squaredDiffSum += Math.pow(matrix.get(i, j).toFloat() - total, 2);
            }
        }
        double stdDev = Math.sqrt(squaredDiffSum / 100.0);
        System.out.println(stdDev);

    }
}
