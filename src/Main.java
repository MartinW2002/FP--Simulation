public class Main {
    // TODO Quantisation paper metric
    // TODO MSE, Cosine similarity, Normalise norm of difference
    // TODO Take FP32 data, quantisise to get distribution
    // TODO 32x32 and 256x256

    public static float STD_DEV = 0.01F;
    public static FPType MAIN_TYPE = FPType.E4M3_8;

    public static void main2(String[] args) {
        System.out.println(Matrix.createRandomMatrix(32,32, FPType.E4M3_8));
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        int size = 64;
        int nIter = 100;
        int numTypes = FPType.values().length;

        double[] totalErrorArray = new double[numTypes];
        double[][] errorValues = new double[numTypes][nIter]; // Stores all MSEs for std dev

        for (int i = 0; i < nIter; i++) {

            Matrix matrix1 = Matrix.createRandomMatrix(size, size, MAIN_TYPE);
            Matrix matrix2 = Matrix.createRandomMatrix(size, size, MAIN_TYPE);

            Matrix exactProduct = matrix1.times(matrix2, FPType.DOUBLE_64);

            for (FPType type : FPType.types) {

                Matrix product = matrix1.times(matrix2, type);

//                double error = Matrix.MSE(exactProduct, product);
                double error = Matrix.relativeError(exactProduct, product);

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
