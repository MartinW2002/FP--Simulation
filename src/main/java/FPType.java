public enum FPType {

    E3M4(3, 4),
    E4M3(4, 3),

    E5M2(5, 2),
    E5M3(5, 3),
    E5M4(5, 4),
    E5M5(5, 5),
    E5M6(5, 6),
    E5M7(5, 7),
    E5M8(5, 8),
    E5M9(5, 9),
    E5M10(5, 10),
    E5M11(5, 11),
    E5M12(5, 12),
    E5M13(5, 13),
    E5M14(5, 14),
    E5M15(5, 15),

    E4M10(4, 10),
    E6M10(6, 10),
    E7M10(7, 10),
    E5M16(5, 16),
    E4M16(4, 16),
    E6M16(6, 16),
    E7M16(7, 16),
    E5M32(5, 32),
    E4M32(4, 32),
    E6M32(6, 32),
    E7M32(7, 32),
    E5M20(5, 20),
    //    CUSTOM_12(5, 6),
//    HALF_16(5, 10),
//    BFLOAT_16(8,7), // Brain Floating Point
//    TF32_19(8, 10), // TensorFloat-32
//    CUSTOM_24(8, 15),
    SINGLE_32(8, 23),
    E3M32(3, 32),
    DOUBLE_64(11, 52);


    private final int exponent;
    private final int mantissa;

    public static final FPType[] types = {E5M3, E5M4, E5M5, E5M6, E5M7, E5M8, E5M9, E5M10, E5M12, E5M13, E5M14, E5M14, E5M15, E5M16};
//    public static final FPType[] types = {E3M32, E4M32, E5M32, E6M32, E7M32};
//    public static final FPType[] types = {E3M4};

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
