import java.util.Random;

public class Matrix {
    private CustomFloat[][] data;
    private int rows;
    private int cols;
    private FPType type;

    public static Matrix createRandomMatrix(int rows, int cols, FPType type) {
        return new Matrix(rows, cols, type, 0.05F);
    }

    // Random
    private Matrix(int rows, int cols, FPType type, float stdDev) {
        this.rows = rows;
        this.cols = cols;
        this.type = type;

        this.data = new CustomFloat[rows][cols];

        Random random = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                float randomValue = (float) (random.nextGaussian() * stdDev);
                this.data[i][j] = new CustomFloat(randomValue, type);
            }
        }
    }

    // Constructor to create an empty matrix with given dimensions
    public Matrix(int rows, int cols, FPType type) {
        this.rows = rows;
        this.cols = cols;
        this.type = type;
        data = new CustomFloat[rows][cols];

        // Initialize the matrix with zeros
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = new CustomFloat(0f, type);
            }
        }
    }

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
                this.data[i][j] = new CustomFloat(value, type);
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
                CustomFloat sum = new CustomFloat(0f, accType);
                for (int k = 0; k < this.cols; k++) {
                    sum = sum.plus(this.data[i][k].times(other.data[k][j], accType));
                }
                result.set(i, j, new CustomFloat(sum.toFloat(), this.type));
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
}
