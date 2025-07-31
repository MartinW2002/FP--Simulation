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
    public static boolean SIZE_32 = true; // 32 or 256
    public static boolean MANTISSA = false; // True: mantissa testing, False: Exponent testing

    public static FPType[] MAIN_TYPES = {FPType.E3M4};
//    public static FPType[] MAIN_TYPES = {FPType.E3M4, FPType.E4M3, FPType.E5M2};

    public static void main(String[] args) throws FileNotFoundException {
        // Build a timestamp like 2025-07-26_18-41-12
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        // Create a filename with the timestamp
        String filename = "output_" + timestamp + ".txt";

        // Redirect System.out to that file
        PrintStream fileOut = new PrintStream(filename);
        System.setOut(fileOut);

        main_accuracy();
//        main_kwantisatie();

//        main_order();
//        test4();
//        test9();

//        test2();
//        test_mult();

        fileOut.close();
    }


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

        for (FPType testType : MAIN_TYPES) {
            FPType comparisonType = FPType.SINGLE_32;

            double totalMSE1 = 0;
            double totalMSE2 = 0;
            for (int i = 0; i < nIter; i++) {
                Matrix comparisonMatrix1 = Matrix.createRandomMatrix(size, size, comparisonType, 1.0f / (float) Math.sqrt(size));
                Matrix comparisonMatrix2 = Matrix.createRandomMatrix(size, size, comparisonType, 1);

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

    public static double roundUp = 0;
    public static double roundDown = 0;

    public static void main_accuracy() {
        long startTime = System.nanoTime();

        // 360 x 32 or 45 x 256

        int size = 256;
        int nIter = 45;
        if (SIZE_32) {
            size = 1024;
//            nIter = 360;
            nIter = 1;
        }

//        FPType[] types = MANTISSA ? FPType.MANTISSA_TYPES : FPType.EXPONENT_TYPES;

        FPType[] types = {FPType.E2M11};

        System.out.println(size + " x " + size);
        System.out.println(nIter + " iterations");
        System.out.println("Testing " + (MANTISSA ? "mantissa" : "exponent"));
        System.out.println("-------------");

        for (FPType mainType : MAIN_TYPES) {

            System.out.println(mainType + " - " + (GAUSS ? "Gaussian" : "T-distribution"));

            int numTypes = FPType.values().length;

            double[] totalErrorArray = new double[numTypes];
            double[][] errorValues = new double[numTypes][nIter]; // Stores all MSEs for std dev


            for (int i = 0; i < nIter; i++) {

                Matrix matrix1 = Matrix.createRandomMatrix(size, size, mainType, 1.0f / (float) Math.sqrt(size));
                Matrix matrix2 = Matrix.createRandomMatrix(size, size, mainType, 1);

                Matrix exactProduct = matrix1.times(matrix2, FPType.DOUBLE_64);
                if (i == 0) {
                    int percentOverflow = (int) Math.round(100.0 * exactProduct.getNOverflows() / (exactProduct.getNCols()
                            * exactProduct.getNRows()));
                    System.out.println("Exactproduct overflows: " + percentOverflow + "%");
                    System.out.println("Std Dev1: " + matrix1.calculateStandardDeviation());
                    System.out.println("Std Dev2: " + matrix2.calculateStandardDeviation());
                    System.out.println("Std Dev: " + exactProduct.calculateStandardDeviation());
                }
                for (FPType type : types) {

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
            for (FPType type : types) {
                int index = type.ordinal();
                double mean = totalErrorArray[index];
                double varianceSum = 0.0;

                for (int i = 0; i < nIter; i++) {
                    varianceSum += Math.pow(errorValues[index][i] - mean, 2);
                }

                stdDevArray[index] = Math.sqrt(varianceSum / nIter);
            }

            for (FPType type : types) {
                System.out.println(type + ": Mean Error = " + totalErrorArray[type.ordinal()] +
                        ", Std Dev = " + stdDevArray[type.ordinal()]);
            }

            System.out.println("----------------");

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

        System.out.println("Rounded up " + (roundUp * 100 / (roundDown + roundUp)) + " % of the time.");

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("Execution time: " + duration / 1_000_000_000 + " s");
    }

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

    public static void printMatrix(Matrix matrix, int n) {
        System.out.print("{");
        for (int i = 0; i < n; i++) {
            System.out.print(matrix.get(0, i));
            System.out.print(",");
        }
        System.out.println("}");
    }

    public static void test4() {
        FPType mainType = FPType.E3M4;
        int n = 32;

        String string1 = "[-0.0 (1.000.0000), 0.015625 (0.000.0001), -0.046875 (1.000.0011), -0.6875 (1.010.0110), 0.21875 (0.000.1110), 0.125 (0.000.1000), 0.875 (0.010.1100), 0.359375 (0.001.0111), 0.296875 (0.001.0011), -0.125 (1.000.1000), 1.0625 (0.011.0001), 0.1875 (0.000.1100), -0.15625 (1.000.1010), 0.28125 (0.001.0010), -0.03125 (1.000.0010), 0.875 (0.010.1100), -0.09375 (1.000.0110), 0.234375 (0.000.1111), 0.015625 (0.000.0001), 0.6875 (0.010.0110), 0.53125 (0.010.0001), -0.484375 (1.001.1111), 0.6875 (0.010.0110), -0.140625 (1.000.1001), 0.0625 (0.000.0100), -0.484375 (1.001.1111), -0.5 (1.010.0000), 0.390625 (0.001.1001), -0.375 (1.001.1000), 0.234375 (0.000.1111), 1.0625 (0.011.0001), 0.15625 (0.000.1010)]";
        String string2 = "[-0.8125 (1.010.1010), -0.171875 (1.000.1011), 0.34375 (0.001.0110), -0.140625 (1.000.1001), 0.5625 (0.010.0010), 0.5625 (0.010.0010), 0.6875 (0.010.0110), 0.421875 (0.001.1011), 0.296875 (0.001.0011), 0.40625 (0.001.1010), -0.234375 (1.000.1111), -0.09375 (1.000.0110), 0.625 (0.010.0100), -0.53125 (1.010.0001), 0.59375 (0.010.0011), -0.28125 (1.001.0010), -0.3125 (1.001.0100), -0.015625 (1.000.0001), -0.6875 (1.010.0110), 0.1875 (0.000.1100), -0.328125 (1.001.0101), 0.5 (0.010.0000), 0.078125 (0.000.0101), -0.59375 (1.010.0011), -0.34375 (1.001.0110), -0.125 (1.000.1000), 0.59375 (0.010.0011), -0.109375 (1.000.0111), 0.03125 (0.000.0010), -0.390625 (1.001.1001), -0.046875 (1.000.0011), 0.203125 (0.000.1101)]";

        Vector vector1 = Vector.fromString(string1, mainType);
        Vector vector2 = Vector.fromString(string2, mainType);

        CustomFloat exactResult = vector1.multiply(vector2, FPType.DOUBLE_64);
        CustomFloat result1 = vector1.multiply(vector2, FPType.E2M4);
        CustomFloat result2 = vector1.multiply(vector2, FPType.E3M4);

        double error1 = Math.abs(result1.toFloat()) - Math.abs(exactResult.toFloat());
        double error2 = Math.abs(result2.toFloat()) - Math.abs(exactResult.toFloat());

        System.out.println("vector1");
        System.out.println(vector1);
        System.out.println("vector2");
        System.out.println(vector2);

        System.out.println("exactResult: " + exactResult);
        System.out.println("result1: " + result1);
        System.out.println("result2: " + result2);

        System.out.println("E2M4: " + error1);
        System.out.println("E3M4: " + error2);
    }

    public static final int TEST2_N = 200;

    public static StringBuilder[] stringBuilders = new StringBuilder[TEST2_N * 2];
    public static float[] resultsArray = new float[TEST2_N * 3];

    public static void test2() {
        FPType mainType = FPType.E3M4;
        int n = TEST2_N;
        int nIter = 1;

        double error1 = 0;
        double error2 = 0;
        double error3 = 0;
        double error4 = 0;
        double error5 = 0;
        double error6 = 0;

        for (int i = 0; i < nIter; i++) {
            Vector vector1 = Vector.random(n, mainType);
            Vector vector2 = Vector.random(n, mainType);

//            Vector vector1 = Vector.fromString("[0.328125 (0.001.0101), 0.078125 (0.000.0101), -0.375 (1.001.1000), 0.171875 (0.000.1011), 0.03125 (0.000.0010), 0.046875 (0.000.0011), -0.203125 (1.000.1101), -0.390625 (1.001.1001), 0.6875 (0.010.0110), 0.625 (0.010.0100), 0.375 (0.001.1000), 0.5625 (0.010.0010), -0.109375 (1.000.0111), 0.015625 (0.000.0001), -0.75 (1.010.1000), -0.90625 (1.010.1101), 0.140625 (0.000.1001), 0.03125 (0.000.0010), -0.390625 (1.001.1001), -0.203125 (1.000.1101)]", mainType);
//            Vector vector2 = Vector.fromString("[-0.125 (1.000.1000), 0.25 (0.001.0000), -0.53125 (1.010.0001), 0.453125 (0.001.1101), -0.84375 (1.010.1011), 0.5 (0.010.0000), -0.625 (1.010.0100), -0.03125 (1.000.0010), 0.15625 (0.000.1010), 0.390625 (0.001.1001), 0.09375 (0.000.0110), 0.359375 (0.001.0111), -0.109375 (1.000.0111), -0.40625 (1.001.1010), 1.25 (0.011.0100), -0.25 (1.001.0000), -0.25 (1.001.0000), -0.625 (1.010.0100), -0.0 (1.000.0000), 0.203125 (0.000.1101)]", mainType);

            CustomFloat exactResult = vector1.multiply(vector2, FPType.DOUBLE_64);
            CustomFloat result1 = vector1.multiply(vector2, FPType.E2M4);
            CustomFloat result2 = vector1.multiply(vector2, FPType.E3M4);
//            CustomFloat result3 = vector1.multiply(vector2, FPType.E4M12);
//            CustomFloat result4 = vector1.multiply(vector2, FPType.E5M12);
//            CustomFloat result5 = vector1.multiply(vector2, FPType.E6M12);

            error1 += Math.abs(result1.toFloat() - exactResult.toFloat());
            error2 += Math.abs(result2.toFloat() - exactResult.toFloat());

            error3 += Math.abs(result1.toFloat()) - Math.abs(exactResult.toFloat());
            error4 += Math.abs(result2.toFloat()) - Math.abs(exactResult.toFloat());

            error5 += result1.toFloat() - exactResult.toFloat();
            error6 += result2.toFloat() - exactResult.toFloat();

            System.out.println("vector1");
        System.out.println(vector1);
        System.out.println("vector2");
        System.out.println(vector2);
//
            System.out.println("exactResult: " + exactResult);
            System.out.println("result1: " + result1);
            System.out.println("result2: " + result2);

        }
        for (int i = 0; i < n; i++) {
            System.out.println(stringBuilders[2 * i]);
            System.out.println(stringBuilders[2 * i + 1]);
            if (i == 0) continue;
            float step1 = (resultsArray[3 * i] - resultsArray[3 * (i - 1)]);
            float step2 = (resultsArray[3 * i + 1] - resultsArray[3 * (i - 1) + 1]);
            float step3 = (resultsArray[3 * i + 2] - resultsArray[3 * (i - 1) + 2]);
            System.out.println(step1  + " | "
                            + step2 + " | "
                            + step3);
            System.out.println((step1 - step2) + " | " + (step1 - step3));
            System.out.println("");
        }

        StringBuilder builder1 = new StringBuilder();
        for (int i = 0; i < n; i++) {
            builder1.append(resultsArray[3 * i]).append(",");
        }
        System.out.println(builder1.toString());

        StringBuilder builder2 = new StringBuilder();
        for (int i = 0; i < n; i++) {
            builder2.append(resultsArray[3 * i + 1]).append(",");
        }
        System.out.println(builder2.toString());

        StringBuilder builder3 = new StringBuilder();
        for (int i = 0; i < n; i++) {
            builder3.append(resultsArray[3 * i + 2]).append(",");
        }
        System.out.println(builder3.toString());
        
        System.out.println("E2M4: " + error1 / nIter);
        System.out.println("E3M4: " + error2 / nIter);

        System.out.println("E2M4: " + error3 / nIter);
        System.out.println("E3M4: " + error4 / nIter);

        System.out.println("E2M4: " + error5 / nIter);
        System.out.println("E3M4: " + error6 / nIter);

        //        System.out.println("E6M4: " + error5 / nIter);


    }

    public static void test3() {
        FPType mainType = FPType.E3M4;
        int n = 32;

        Matrix matrix1 = Matrix.createRandomMatrix(n, n, mainType, 1);
        Matrix matrix2 = Matrix.createRandomMatrix(n, n, mainType, 1); // TODO STD Dev

        Matrix exactProduct = matrix1.times(matrix2, FPType.DOUBLE_64);
        Matrix product1 = matrix1.times(matrix2, FPType.E2M4);
        Matrix product2 = matrix1.times(matrix2, FPType.E3M4);

        double product1MSE = Matrix.MSE(exactProduct, product1);
        double product2MSE = Matrix.MSE(exactProduct, product2);

        System.out.println("exactProduct: ");
        System.out.println(exactProduct);
        System.out.println("product1 - " + product1MSE);
        System.out.println(product1);
        System.out.println("product2 - " + product2MSE);
        System.out.println(product2);
    }

    public static void test9() {
        CustomFloat customFloat1 = new CustomFloat(1.3f, FPType.E2M4, null);
        CustomFloat customFloat2 = new CustomFloat(1.2f, FPType.E2M4, null);


        System.out.println(customFloat1);
        System.out.println(customFloat2);

        System.out.println(customFloat1.times(customFloat2, FPType.E2M4));
        System.out.println(customFloat1.times(customFloat2, FPType.E3M4));
        System.out.println(customFloat1.times(customFloat2, FPType.DOUBLE_64));
    }

    public static void test_mult() {
        int nIter = 1000;

        double error1 = 0;
        double error2 = 0;
        for (int i = 0; i < nIter; i++) {
            float random1 = new Random().nextFloat();
            float random2 = new Random().nextFloat();

            float exact = random1 * random2;
            CustomFloat customFloat1 = new CustomFloat(exact, FPType.E2M4, null);
            CustomFloat customFloat2 = new CustomFloat(exact, FPType.E3M4, null);

            error1 += Math.abs(exact) - Math.abs(customFloat1.toFloat());
            error2 += Math.abs(exact) - Math.abs(customFloat2.toFloat());
        }
        System.out.println("E2M4: " + error1 / nIter);
        System.out.println("E3M4: " + error2 / nIter);
    }
}
