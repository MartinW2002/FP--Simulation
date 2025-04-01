public enum FPType {

    E4M3_8(4, 3),
    E5M2_8(5, 2),
    E5M3(5, 3),
    E5M4(5, 4),
    E5M5(5, 5),
    E5M6(5, 6),
    E5M7(5, 7),
    E5M8(5, 8),
    E5M9(5, 9),
    E5M10(5, 10),
    E4M10(4, 10),
    E6M10(6, 10),
    E7M10(7, 10),
    CUSTOM_12(5, 6),
    HALF_16(5, 10),
    BFLOAT_16(8,7), // Brain Floating Point
    TF32_19(8, 10), // TensorFloat-32
    CUSTOM_24(8, 15),
    SINGLE_32(8, 23),
    DOUBLE_64(11, 52);

    private final int exponent;
    private final int mantissa;

    public static final FPType[] types = {E5M3, E5M4, E5M5, E5M6, E5M7, E5M8, E5M9, E5M10};
//    public static final FPType[] types = {E4M10, E5M10, E6M10, E7M10};

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
