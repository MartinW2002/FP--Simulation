import javax.crypto.EncryptedPrivateKeyInfo;
import java.util.Arrays;
import java.util.HashMap;

public class Main {
    // TODO Quantisation paper metric
    // TODO MSE, Cosine similarity, Normalise norm of difference
    // TODO Take FP32 data, quantisise to get distribution
    // TODO 32x32 and 256x256

    public static void main(String[] args) {
        int size = 32;
        int nIter = 100;
        double[] totalMSEArray = new double[FPType.values().length];

        for (int i = 0; i < nIter; i++) {

            Matrix matrix1 = Matrix.createRandomMatrix(size, size, FPType.E5M2_8);
            Matrix matrix2 = Matrix.createRandomMatrix(size, size, FPType.E5M2_8);

            Matrix exactProduct = matrix1.times(matrix2, FPType.DOUBLE_64);

            for (FPType type : FPType.values()) {

                Matrix product = matrix1.times(matrix2, type);

                double mse = Matrix.MSE(exactProduct, product);

                totalMSEArray[type.ordinal()] += mse;
            }
        }
        for (int i = 0; i < totalMSEArray.length; i++) {
            totalMSEArray[i] /= nIter;
        }
        for (FPType type : FPType.values()) {
            System.out.println(type + ": " + totalMSEArray[type.ordinal()]);
        }
    }

}
