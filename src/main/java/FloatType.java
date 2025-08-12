import java.util.Objects;

public final class FloatType {

    public static final FloatType SINGLE_32 = new FloatType(8, 23);
    public static final FloatType DOUBLE_64 = new FloatType(11, 52);
    public static final FloatType E3M4 = new FloatType(3, 4);
    public static final FloatType E4M3 = new FloatType(4, 3);
    public static final FloatType E5M2 = new FloatType(5, 2);
    public static final FloatType E6M3 = new FloatType(6, 3);
    public static final FloatType E8M3 = new FloatType(8, 3);

    private final int exponentBits;
    private final int mantissaBits;
    private final int ordinal;

    private final float stdDev;

    public FloatType(int exponentBits, int mantissaBits) {
        this.exponentBits = exponentBits;
        this.mantissaBits = mantissaBits;
        this.ordinal = exponentBits * 32 + mantissaBits;

        if (exponentBits == 3 && mantissaBits == 4)
            this.stdDev = 4f;
        else if (exponentBits == 4 && mantissaBits == 3)
            this.stdDev = 64f;
        else if (exponentBits == 5 && mantissaBits == 2)
            this.stdDev = 16384f; // 2^14
        else if (exponentBits == 6 && mantissaBits == 3)
            this.stdDev = (float) Math.pow(2, 30);
        else if (exponentBits == 8 && mantissaBits == 3)
            this.stdDev = (float) Math.pow(2, 125);
        else
            this.stdDev = -1f;
    }

    public int getExponentBits() {
        return exponentBits;
    }

    public int getMantissaBits() {
        return mantissaBits;
    }

    public int getTotalBits() {
        return exponentBits + mantissaBits + 1;
    }

    public float getStdDev() {
//        if (exponentBits == 3 && mantissaBits == 4)
//            return 4f;
//        if (exponentBits == 4 && mantissaBits == 3)
//            return 64f;
//        if (exponentBits == 5 && mantissaBits == 2)
//            return 16384f; // 2^14
//
//        return -1f;
//        throw new RuntimeException("invalid type: E" + exponentBits + "M" + mantissaBits);
        return stdDev;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FloatType floatType = (FloatType) o;
        return exponentBits == floatType.exponentBits && mantissaBits == floatType.mantissaBits;
    }

    @Override
    public int hashCode() {
        return Objects.hash(exponentBits, mantissaBits);
    }

    public int ordinal() {
        return ordinal;
    }

    @Override
    public String toString() {
        return "E" + exponentBits +
                "M" + mantissaBits;
    }
}
