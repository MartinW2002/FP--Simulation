import org.apache.commons.math3.distribution.TDistribution;

import java.util.Random;

public class Matrix {
    private CustomFloat[][] data;
    private int rows;
    private int cols;
    private FPType type;
    private int nOverflows = 0;

    public static Matrix createRandomMatrix(int rows, int cols, FPType type) {
        return new Matrix(rows, cols, type);
    }

    // Random
    private Matrix(int rows, int cols, FPType type) {
        this.rows = rows;
        this.cols = cols;
        this.type = type;

        this.data = new CustomFloat[rows][cols];

        float stdDev = (float) Math.sqrt(1.0 / (float) rows);

        if (Main.GAUSS) {
            Random random = new Random();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    float randomValue = (float) (random.nextGaussian() * stdDev);
                    this.data[i][j] = new CustomFloat(randomValue, type, null);
                }
            }
        } else {
            TDistribution tDistribution = new TDistribution(Main.NU);
            double stddevT = Math.sqrt(Main.NU / (Main.NU - 2.0)); // Theoretical stddev of t-dist

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    double tValue = tDistribution.sample();
                    float scaledValue = (float) (tValue * (stdDev / stddevT));
                    this.data[i][j] = new CustomFloat(scaledValue, type, null);
                }
            }
        }
    }

    // Generates a matrix with a different FPType based on the values in an old matrix
    public Matrix(Matrix old, FPType type) {
        this.rows = old.rows;
        this.cols = old.cols;
        this.type = type;
        this.data = new CustomFloat[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.data[i][j] = new CustomFloat(old.data[i][j].toFloat(), type, null);
            }
        }
    }

    // Constructor to create an empty matrix with given dimensions
//    public Matrix(int rows, int cols, FPType type) {
//        this.rows = rows;
//        this.cols = cols;
//        this.type = type;
//        data = new CustomFloat[rows][cols];
//
//        // Initialize the matrix with zeros
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < cols; j++) {
//                data[i][j] = new CustomFloat(0f, type, null);
//            }
//        }
//    }

    // Constructor to create a matrix with predefined data
    public Matrix(CustomFloat[][] data) {
        this.rows = data.length;
        this.cols = data[0].length;
        this.data = data;
        this.type = data[0][0].getType();
    }

    // Input represents a matrix with spaces in between columns and semicolons or newlines in between rows
    public Matrix(String input, int totalBits, int exponentBits) {
        // Split the input by semicolons or newlines to get rows
        String[] rows = input.split("[;\\n]");
        this.rows = rows.length;

        // Initialize the matrix with the number of rows
        this.data = new CustomFloat[rows.length][];

        // Iterate through the rows
        for (int i = 0; i < rows.length; i++) {
            // Split each row by space to get individual elements
            String[] elements = rows[i].trim().split("\\s+");
            this.cols = elements.length;

            // Initialize the row for this matrix row
            this.data[i] = new CustomFloat[elements.length];

            // Parse each element as a CustomFloat
            for (int j = 0; j < elements.length; j++) {
                float value = Float.parseFloat(elements[j].trim());
                this.data[i][j] = new CustomFloat(value, type, null);
            }
        }
    }

    // Get the value at a specific row and column
    public CustomFloat get(int row, int col) {
        return data[row][col];
    }

    // Set the value at a specific row and column
    public void set(int row, int col, CustomFloat value) {
        if (value.getType() != this.type) {
            throw new IllegalArgumentException("Incompatible floating point representation");
        }
        data[row][col] = value;
    }

    // Add another matrix to this matrix
    public Matrix plus(Matrix other) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new IllegalArgumentException("Matrix dimensions must match for addition");
        }

        Matrix result = new Matrix(this.rows, this.cols, this.type);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                CustomFloat sum = this.data[i][j].plus(other.data[i][j]);
                result.set(i, j, sum);
            }
        }
        return result;
    }

    // Subtract another matrix to this matrix
    public Matrix minus(Matrix other) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new IllegalArgumentException("Matrix dimensions must match for subtraction");
        }

        Matrix result = new Matrix(this.rows, this.cols, this.type);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                CustomFloat sum = this.data[i][j].minus(other.data[i][j]);
                result.set(i, j, sum);
            }
        }
        return result;
    }

    // Multiply this matrix by another matrix
    public Matrix times(Matrix other, FPType accType) {
        if (this.cols != other.rows) {
            throw new IllegalArgumentException("Matrix dimensions must match for multiplication");
        }

        Matrix result = new Matrix(this.rows, other.cols, this.type);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                // TODO Check different order
                CustomFloat sum = new CustomFloat(0f, accType, null);
                for (int k = 0; k < this.cols; k++) {
                    sum = sum.plus(this.data[i][k].times(other.data[k][j], accType));
                }
                result.set(i, j, new CustomFloat(sum.toFloat(), this.type, result));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(data[i][j].toString()).append(" ");
            }
            if (i < (rows - 1)) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public double norm() {
        double sum = 0.0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double value = data[i][j].toFloat();
                sum += value * value;
            }
        }
        return Math.sqrt(sum);
    }

    public int getNRows() {
        return rows;
    }

    public int getNCols() {
        return cols;
    }

    public static double MSE(Matrix matrix1, Matrix matrix2) {
        double result = 0.0;
        Matrix difference = matrix1.minus(matrix2);
        int rows = difference.rows;
        int cols = difference.cols;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double value = difference.data[i][j].toFloat();
                result += value * value;
            }
        }
        result /= (rows * cols);
        return result;
    }

    public static double relativeError(Matrix matrix1, Matrix matrix2) {
        double result = 0.0;

        int rows = matrix1.rows;
        int cols = matrix1.cols;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double difference = matrix1.get(i, j).toFloat() - matrix2.get(i, j).toFloat();
                double avg = (matrix1.get(i, j).toFloat() + matrix2.get(i, j).toFloat()) / 2.0;
                if (avg != 0)
                    result += Math.abs(difference / avg);
            }
        }
        result /= (rows * cols);
        return 1 - result;
    }

    public void addOverflow() {
        nOverflows++;
    }

    public int getNOverflows() {
        return nOverflows;
    }
}
