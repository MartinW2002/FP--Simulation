public enum Type {
    E4M3_8(4, 3),
    E5M2_8(2, 8),
    HALF_16(5, 10),
    SINGLE_32(5, 23);

    private final int exponent;
    private final int mantissa;

    Type(int exponent, int mantissa) {
        this.exponent = exponent;
        this.mantissa = mantissa;
    }
}
