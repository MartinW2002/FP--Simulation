public enum FPType {
    E4M3_8(4, 3),
    E5M2_8(5, 2),
    HALF_16(5, 10),
    BFLOAT_16(8,7), // Brain Floating Point
    TF32_19(8, 10), // TensorFloat-32
    SINGLE_32(8, 23),
    DOUBLE_64(11, 52);

    private final int exponent;
    private final int mantissa;

    FPType(int exponent, int mantissa) {
        this.exponent = exponent;
        this.mantissa = mantissa;
    }

    public int getTotalBits() {
        return exponent + mantissa;
    }

    public int getExponentBits() {
        return exponent;
    }

    public int getMantissaBits() {
        return mantissa;
    }
}
