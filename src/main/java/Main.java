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
    public static boolean SIZE_32 = true; // 32 or 256 // TODO Obsolete
    public static int N = 16; // 16, 64, 256 or 1024 - Must be even power of 2
    public static boolean MANTISSA = false; // True: mantissa testing, False: Exponent testing

    public static FloatType[] MAIN_TYPES = {new FloatType(3, 4)};
//    public static FloatType[] MAIN_TYPES = {FPType.E3M4, FPType.E4M3, FPType.E5M2};

    public static void main(String[] args) throws FileNotFoundException {
        // Build a timestamp like 2025-07-26_18-41-12
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        // Create a filename with the timestamp
        String filename = "output_" + timestamp + ".txt";

        // Redirect System.out to that file
        PrintStream fileOut = new PrintStream(filename);
//        System.setOut(fileOut);

        main_accuracy();
//        main_kwantisatie();

//        main_order();
//        test4();
//        test9();

//        test2();
//        test_mult();

        fileOut.close();
    }


    // TODO Fix
    public static void main_kwantisatie() {
        int size = 32;
        int nIter = 8000;

        if (!SIZE_32) {
            size = 256;
            nIter = 1000;
        }
        System.out.println(size + " x " + size);
        System.out.println(nIter + " iterations");
        System.out.println((GAUSS ? "Gaussian" : "T-distribution"));

        for (FloatType testType : MAIN_TYPES) {
            FloatType comparisonType = FloatType.SINGLE_32;

            double totalMSE1 = 0;
            double totalMSE2 = 0;
            for (int i = 0; i < nIter; i++) {
                Matrix comparisonMatrix1 = Matrix.createRandomMatrix(size, size, comparisonType);
                Matrix comparisonMatrix2 = Matrix.createRandomMatrix(size, size, comparisonType);

                Matrix testMatrix1 = new Matrix(comparisonMatrix1, testType);
                Matrix testMatrix2 = new Matrix(comparisonMatrix2, testType);

                double mse1 = Matrix.MSE(comparisonMatrix1, testMatrix1);
                double mse2 = Matrix.MSE(comparisonMatrix2, testMatrix2);
                totalMSE1 += mse1;
                totalMSE2 += mse2;
            }
            System.out.println(testType);

            System.out.println("STD: 1/sqrt(N) - 1");
            System.out.println((totalMSE1 / (float) nIter) + " - " + (totalMSE2 / (float) nIter));

            System.out.println("-----");
        }
    }

    public static void main_accuracy() {
        long startTime = System.nanoTime();

        int size = N;
        int nIter = 1024 / N * 1024 / N;

        System.out.println(size + " x " + size);
        System.out.println(nIter + " iterations");
        System.out.println("Testing " + (MANTISSA ? "mantissa" : "exponent"));
        System.out.println("-------------");

        int eBegin = 4;
        int eEnd = 5;
        int mBegin = 3;
        int mEnd = 5;


//        FloatType[] types = new FloatType[(eEnd - eBegin + 1) * (mEnd - mBegin + 1)];
        List<FloatType> types = new ArrayList<>();
        for (int e = eBegin; e <= eEnd; e++) {
            for (int m = mBegin; m <= mEnd; m++) {
                types.add(new FloatType(e, m));
            }
        }

        // Loop through the three main types
        for (FloatType mainType : MAIN_TYPES) {

            System.out.println(mainType + " - " + (GAUSS ? "Gaussian" : "T-distribution"));

            int numTypes = FPType.values().length;

            double[] totalErrorArray = new double[(eEnd + 1) * 32]; // e * 32 + m
            double[][] errorValues = new double[(eEnd + 1) * 32][nIter]; // Stores all MSEs for std dev

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
                    errorValues[type.ordinal()][i] = error;
                }
                System.out.println((i + 1) + "/" + nIter);
            }
            for (int i = 0; i < totalErrorArray.length; i++) {
                totalErrorArray[i] /= nIter;
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

}
