public class Main {
    // TODO Quantisation paper metric
    // TODO MSE, Cosine similarity, Normalise norm of difference
    // TODO Take FP32 data, quantisise to get distribution
    // TODO 32x32 and 256x256

    // TODO Fout minder groot maken door volgorde groot klein, plus min

    // Grafieken voor
    // Gaus vs t distr
    // E5M2, E4M3, E3M4
    // Vergelijken mantissa en vergelijken exponent

    public static float STD_DEV = 1.0F;
    public static FPType MAIN_TYPE = FPType.E4M3_8;

    public static void main(String[] args) {
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

    public static void main2(String[] args) {
        long startTime = System.nanoTime();

        int size = 256;
        int nIter = 1;
        int numTypes = FPType.values().length;

        double[] totalErrorArray = new double[numTypes];
        double[][] errorValues = new double[numTypes][nIter]; // Stores all MSEs for std dev

        for (int i = 0; i < nIter; i++) {

            Matrix matrix1 = Matrix.createRandomMatrix(size, size, MAIN_TYPE);
            Matrix matrix2 = Matrix.createRandomMatrix(size, size, MAIN_TYPE);

            Matrix exactProduct = matrix1.times(matrix2, FPType.DOUBLE_64);

            for (FPType type : FPType.types) {

                Matrix product = matrix1.times(matrix2, type);

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

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("Execution time: " + duration / 1_000_000.0 + " ms");
    }

}
