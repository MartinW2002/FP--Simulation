import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // TODO 32x32 and 256x256
        Matrix matrix1 = Matrix.createRandomMatrix(10, 10, FPType.E5M2_8);
        Matrix matrix2 = Matrix.createRandomMatrix(10, 10, FPType.E5M2_8);

//        System.out.println("Matrix 1:");
//        System.out.println(matrix1);
//        System.out.println("Matrix 2:");
//        System.out.println(matrix2);
//        System.out.println("Matrix 3:");
//        System.out.println(matrix3);
//        System.out.println("Matrix 1:");
//        System.out.println(matrix1);

        System.out.println("Matrix 1 * Matrix 2 (64 bit acc):");
        Matrix product0 = matrix1.times(matrix2, FPType.DOUBLE_64);
        System.out.println(product0);

//        System.out.println("Matrix 1 * Matrix 2 (8 bit acc):");
//        Matrix product1 = matrix1.multiply(matrix2, totalBits1, exponentBits1);
//        System.out.println(product1);
//
//        System.out.println("Matrix 1 * Matrix 2 (12 bit acc):");
//        Matrix product2 = matrix1.multiply(matrix2, totalBits2, exponentBits2);
//        System.out.println(product2);

        System.out.println("Matrix 1 * Matrix 2 (E5M2 acc):");
        Matrix product3 = matrix1.times(matrix2, FPType.E5M2_8);
        System.out.println(product3);

//        System.out.println("\nDifference 8: ");
//        System.out.println(product0.substract(product1).norm());
//
//        System.out.println("\nDifference 12: ");
//        System.out.println(product0.substract(product2).norm());

        System.out.println("\nDifference: ");
        Matrix difference = product0.minus(product3);
        System.out.println(difference);
        System.out.println("Norm :" + difference.norm());

        // TODO Quantisation paper metric
        // TODO MSE, Cosine similarity, Normalise norm of difference
        // TODO Take FP32 data, quantisise to get distribution

    }

}
