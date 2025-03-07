public class Main {
    public static void main(String[] args) {
        int totalBits1 = 8;
        int exponentBits1 = 3;
        int totalBits2 = 12;
        int exponentBits2 = 3;

        Matrix matrix1 = new Matrix(2, 2, totalBits1, exponentBits1);
        matrix1.set(0, 0, new CustomFloat(1.5f, totalBits1, exponentBits1));
        matrix1.set(0, 1, new CustomFloat(2.5f, totalBits1, exponentBits1));
        matrix1.set(1, 0, new CustomFloat(3.5f, totalBits1, exponentBits1));
        matrix1.set(1, 1, new CustomFloat(4.5f, totalBits1, exponentBits1));

        Matrix matrix2 = new Matrix(2, 2, totalBits1, exponentBits1);
        matrix2.set(0, 0, new CustomFloat(0.5f, totalBits1, exponentBits1));
        matrix2.set(0, 1, new CustomFloat(1.0f, totalBits1, exponentBits1));
        matrix2.set(1, 0, new CustomFloat(1.5f, totalBits1, exponentBits1));
        matrix2.set(1, 1, new CustomFloat(2.0f, totalBits1, exponentBits1));

        System.out.println("Matrix 1:");
        System.out.println(matrix1);
        System.out.println("Matrix 2:");
        System.out.println(matrix2);

        System.out.println("Matrix 1 * Matrix 2 (8 bit acc):");
        Matrix product1 = matrix1.multiply(matrix2, totalBits1, exponentBits1);
        System.out.println(product1);

        System.out.println("Matrix 1 * Matrix 2 (12 bit acc):");
        Matrix product2 = matrix1.multiply(matrix2, totalBits2, exponentBits2);
        System.out.println(product2);
    }
}
