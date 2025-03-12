import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int totalBits1 = 8;
        int exponentBits1 = 3;
        int totalBits2 = 12;
        int exponentBits2 = 4;

//        Matrix matrix3 = new Matrix("1 2 3 1 2 3 1 2 3; 4 5 6 4 5 6 4 5 6; 7 8 9 7 8 9 7 8 9", totalBits1, exponentBits1);
        // TODO 32x32 and 256x256
        Matrix matrix1 = Matrix.createRandomMatrix(10, 10, totalBits1, exponentBits1, -3, 3);
        Matrix matrix2 = Matrix.createRandomMatrix(10, 10, totalBits1, exponentBits1, -3, 3);

//        System.out.println("Matrix 1:");
//        System.out.println(matrix1);
//        System.out.println("Matrix 2:");
//        System.out.println(matrix2);
//        System.out.println("Matrix 3:");
//        System.out.println(matrix3);
//        System.out.println("Matrix 1:");
//        System.out.println(matrix1);

        System.out.println("Matrix 1 * Matrix 2 (32 bit acc):");
        Matrix product0 = matrix1.times(matrix2, 64, 12);
        System.out.println(product0);

//        System.out.println("Matrix 1 * Matrix 2 (8 bit acc):");
//        Matrix product1 = matrix1.multiply(matrix2, totalBits1, exponentBits1);
//        System.out.println(product1);
//
//        System.out.println("Matrix 1 * Matrix 2 (12 bit acc):");
//        Matrix product2 = matrix1.multiply(matrix2, totalBits2, exponentBits2);
//        System.out.println(product2);

        System.out.println("Matrix 1 * Matrix 2 (24 bit acc):");
        Matrix product3 = matrix1.times(matrix2, 8, 3);
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
