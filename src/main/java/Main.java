public class Main {
    // TODO 32x32 and 256x256

    // TODO Fout minder groot maken door volgorde groot klein, plus min
    // TODO E3M4
    // TODO Kwantisatie fout naar fp8 gaan meten
    // TODO int8

    // TODO Calculate percent overflow for acc.

    // Grafieken voor
    // Gaus vs t distr
    // E5M2, E4M3, E3M4
    // Vergelijken mantissa en vergelijken exponent

    public static float STD_DEV = 1.0F;
    public static FPType MAIN_TYPE = FPType.E5M2;
    public static int MU = 3;
    public static boolean GAUSS = false; // True: Gaussian Distribution, False: t-distribution

    public static void main2(String[] args) {
        int size = 10;
        Matrix matrix1 = Matrix.createRandomMatrix(size, size, MAIN_TYPE);
        Matrix matrix2 = Matrix.createRandomMatrix(size, size, MAIN_TYPE);

        System.out.println(matrix1);

        Matrix result1 = matrix1.times(matrix2, FPType.E5M3);
        Matrix result3 = matrix1.times(matrix2, FPType.E5M5);
        Matrix result12 = matrix1.times(matrix2, FPType.E5M10);

        for (int i = 0; i < 4; i++) {
            System.out.println(result1.get(i, i));
            System.out.println(result3.get(i, i));
            System.out.println(result12.get(i, i));
            System.out.println("--");
        }
    }

    public static void main3(String[] args) {
        long startTime = System.nanoTime();

        // 100 x 32 or 1 x 256

//        int size = 256;
//        int nIter = 1;

        int size = 32;
        int nIter = 1;

        int numTypes = FPType.values().length;

        double[] totalErrorArray = new double[numTypes];
        double[][] errorValues = new double[numTypes][nIter]; // Stores all MSEs for std dev

        for (int i = 0; i < nIter; i++) {

            Matrix matrix1 = Matrix.createRandomMatrix(size, size, MAIN_TYPE);
            Matrix matrix2 = Matrix.createRandomMatrix(size, size, MAIN_TYPE);

            Matrix exactProduct = matrix1.times(matrix2, FPType.DOUBLE_64);
            if (i == 0) {
                int percentOverflow = (int) Math.round(100.0 * exactProduct.getNOverflows() / (exactProduct.getNCols()
                        * exactProduct.getNRows()));
                System.out.println("Exactproduct overflows: " + percentOverflow + "%");
            }
            for (FPType type : FPType.types) {

                Matrix product = matrix1.times(matrix2, type);

//                System.out.println(type + " product number of overflows: " + product.getNOverflows() + "/"
//                        + product.getNCols() * product.getNRows());

                double error = Matrix.MSE(exactProduct, product);
//                double error = Matrix.relativeError(exactProduct, product);

                totalErrorArray[type.ordinal()] += error;
                errorValues[type.ordinal()][i] = error;
            }
        }
        for (int i = 0; i < totalErrorArray.length; i++) {
            totalErrorArray[i] /= nIter;
        }

        // Compute standard deviation
        double[] stdDevArray = new double[numTypes];
        for (FPType type : FPType.types) {
            int index = type.ordinal();
            double mean = totalErrorArray[index];
            double varianceSum = 0.0;

            for (int i = 0; i < nIter; i++) {
                varianceSum += Math.pow(errorValues[index][i] - mean, 2);
            }

            stdDevArray[index] = Math.sqrt(varianceSum / nIter);
        }

        for (FPType type : FPType.types) {
            System.out.println(type + ": Mean Error = " + totalErrorArray[type.ordinal()] +
                    ", Std Dev = " + stdDevArray[type.ordinal()]);
        }

        System.out.println("----------------");

        StringBuilder stringBuilder1 = new StringBuilder();
        StringBuilder stringBuilder2 = new StringBuilder();
        for (FPType type : FPType.types) {
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

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("Execution time: " + duration / 1_000_000_000 + " s");
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        int nIter = 1000;
        int size = 256;
        FPType type = FPType.E5M6;

        double totalErrorRandom = 0;
        double totalErrorAscending = 0;
        double totalErrorDescending = 0;
        double totalErrorAscendingAbs = 0;
        double totalErrorDescendingAbs = 0;

        for (int i = 0; i < nIter; i++) {

            Vector vector1 = Vector.random(size, MAIN_TYPE);
            Vector vector2 = Vector.random(size, MAIN_TYPE);

            VectorPair ascendingPair = vector1.sortWithCompanion(vector2, true, false);
            VectorPair descendingPair = vector1.sortWithCompanion(vector2, false, false);
            VectorPair ascendingPairAbs = vector1.sortWithCompanion(vector2, true, true);
            VectorPair descendingPairAbs = vector1.sortWithCompanion(vector2, false, true);

            CustomFloat exactResult = vector1.multiply(vector2, FPType.DOUBLE_64);

            CustomFloat randomResult = vector1.multiply(vector2, type);
            CustomFloat ascendingResult = ascendingPair.v1.multiply(ascendingPair.v2, type);
            CustomFloat descendingResult = descendingPair.v1.multiply(descendingPair.v2, type);
            CustomFloat ascendingResultAbs = ascendingPairAbs.v1.multiply(ascendingPairAbs.v2, type);
            CustomFloat descendingResultAbs = descendingPairAbs.v1.multiply(descendingPairAbs.v2, type);

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
}
