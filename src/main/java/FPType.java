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
    E5M16(5, 16),
    E5M20(5, 20),

    E4M10(4, 10),
    E6M10(6, 10),
    E7M10(7, 10),
    E4M16(4, 16),
    E6M16(6, 16),
    E7M16(7, 16),
    E5M32(5, 32),
    E4M32(4, 32),
    E6M32(6, 32),
    E7M32(7, 32),
    //    CUSTOM_12(5, 6),
//    HALF_16(5, 10),
//    BFLOAT_16(8,7), // Brain Floating Point
//    TF32_19(8, 10), // TensorFloat-32
//    CUSTOM_24(8, 15),
    E3M12(3, 12),
    E4M12(4, 12),
    E6M12(6, 12),
    E7M12(7, 12),
    SINGLE_32(8, 23),
    E3M32(3, 32),
    DOUBLE_64(11, 52),
    E3M8(3, 8),
    E3M5(3, 5),
    E3M6(3, 6),
    E4M4(4, 4),
    E4M5(4, 5),
    E4M6(4, 6),
    E6M4(6, 4),
    E3M2(3, 2),
    E3M7(3, 7),
    E3M9(3, 9),
    E3M10(3, 10),
    E3M11(3, 11),
    E3M13(3, 13),
    E3M14(3, 14),
    E2M4(2, 4),
    E2M5(2, 5),
    E2M6(2, 6),
    E2M7(2, 7),
    E2M8(2, 8),
    E2M9(2, 9),
    E2M32(2, 32),
    E1M5(1, 5),
    E1M12(1, 12),
    E3M3(3, 3),
    E15M5(15, 5),

    E2M10(2, 10),
    E2M11(2, 12),
    E2M12(2, 12),
    E2M13(2, 13),
    E2M14(2, 14),

    E4M7(4, 7),
    E4M8(4, 8),
    E4M9(4, 9),
    E4M11(4, 11),
    E4M13(4, 13),
    E4M14(4, 14);


    private final int exponent;
    private final int mantissa;


//    public static final FPType[] MANTISSA_TYPES = {E5M3, E5M4, E5M5, E5M6, E5M7, E5M8, E5M9, E5M10, E5M11, E5M12, E5M13, E5M14};
//    public static final FPType[] MANTISSA_TYPES = {E4M4, E4M5, E4M6, E4M7, E4M8, E4M9, E4M10, E4M11, E4M12, E4M13, E4M14};
//    public static final FPType[] MANTISSA_TYPES = {E3M4, E3M5, E3M6, E3M7, E3M8, E3M9, E3M10, E3M11, E3M12, E3M13, E3M14};
    public static final FPType[] MANTISSA_TYPES = {E2M4, E2M5, E2M6, E2M7, E2M8, E2M9, E2M10, E2M11, E2M12, E2M13, E2M14};

//    public static final FPType[] EXPONENT_TYPES = {E2M32, E3M32, E4M32, E5M32};
    public static final FPType[] EXPONENT_TYPES = {E2M12, E3M12, E4M12, E5M12, E6M12, E7M12};

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

//    public CustomFloat[] allPossible() {
//        int maxExponent = (int) Math.pow(2, exponent);
//        int maxMantissa = (int) Math.pow(2, mantissa);
//        CustomFloat[] floats = new CustomFloat[maxExponent*maxMantissa];
//        for (int i = 0; i < maxExponent; i++) {
//            for (int j = 0; j < maxMantissa; j++) {
//                CustomFloat customFloat = new CustomFloat(false, i, j, this);
//                floats[i * maxMantissa + j] = customFloat;
//            }
//        }
//        return floats;
//    }
}
