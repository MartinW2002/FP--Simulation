import java.util.Random;

public class Matrix {
    private CustomFloat[][] data;
    private int rows;
    private int cols;
    private int elementsTotalBits;
    private int elementsExponentBits;

    // Constructor to create a matrix with given dimensions
    public Matrix(int rows, int cols, int elementsTotalBits, int elementsExponentBits) {
        this.rows = rows;
        this.cols = cols;
        this.elementsTotalBits = elementsTotalBits;
        this.elementsExponentBits = elementsExponentBits;
        data = new CustomFloat[rows][cols];

        // Initialize the matrix with zeros
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = new CustomFloat(0f, elementsTotalBits, elementsExponentBits);
            }
        }
    }

    // Constructor to create a matrix with predefined data
    public Matrix(CustomFloat[][] data) {
        this.rows = data.length;
        this.cols = data[0].length;
        this.data = data;
        this.elementsExponentBits = data[0][0].getExponentBits();
        this.elementsTotalBits = data[0][0].getTotalBits();
    }

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
                this.data[i][j] = new CustomFloat(value, totalBits, exponentBits);
            }
        }
    }

    public Matrix(int rows, int cols, int elementsTotalBits, int elementsExponentBits, float minValue, float maxValue) {
        this.rows = rows;
        this.cols = cols;
        this.elementsTotalBits = elementsTotalBits;
        this.elementsExponentBits = elementsExponentBits;
        this.data = new CustomFloat[rows][cols];

        Random random = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                float randomValue = minValue + (random.nextFloat() * (maxValue - minValue));
                this.data[i][j] = new CustomFloat(randomValue, elementsTotalBits, elementsExponentBits);
            }
        }
    }


    // Get the value at a specific row and column
    public CustomFloat get(int row, int col) {
        return data[row][col];
    }

    // Set the value at a specific row and column
    public void set(int row, int col, CustomFloat value) {
        data[row][col] = value;
    }

    // Add another matrix to this matrix
    public Matrix plus(Matrix other) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new IllegalArgumentException("Matrix dimensions must match for addition");
        }

        Matrix result = new Matrix(this.rows, this.cols, this.elementsTotalBits, this.elementsExponentBits);
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

        Matrix result = new Matrix(this.rows, this.cols, this.elementsTotalBits, this.elementsExponentBits);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                CustomFloat sum = this.data[i][j].minus(other.data[i][j]);
                result.set(i, j, sum);
            }
        }
        return result;
    }

    // Multiply this matrix by another matrix
    public Matrix times(Matrix other, int accTotalBits, int accExponentBits) {
        if (this.cols != other.rows) {
            throw new IllegalArgumentException("Matrix dimensions must match for multiplication");
        }

        Matrix result = new Matrix(this.rows, other.cols, this.elementsTotalBits, this.elementsExponentBits);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                CustomFloat sum = new CustomFloat(0f, accTotalBits, accExponentBits);
                for (int k = 0; k < this.cols; k++) {
                    sum = sum.plus(this.data[i][k].times(other.data[k][j], accTotalBits, accExponentBits));
                }
                result.set(i, j, new CustomFloat(sum.toFloat(), elementsTotalBits, elementsExponentBits));
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
}
