import java.util.Objects;

public final class FloatType {

    public static final FloatType SINGLE_32 = new FloatType(8, 23);
    public static final FloatType DOUBLE_64 = new FloatType(11, 52);

    private final int exponentBits;
    private final int mantissaBits;
    private final int ordinal;

    public FloatType(int exponentBits, int mantissaBits) {
        this.exponentBits = exponentBits;
        this.mantissaBits = mantissaBits;
        this.ordinal = exponentBits * 32 + mantissaBits;;
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
        if (exponentBits == 3 && mantissaBits == 4)
            return 4f;
        if (exponentBits == 4 && mantissaBits == 3)
            return 64f;
        if (exponentBits == 5 && mantissaBits == 2)
            return 16384f; // 2^14

        throw new RuntimeException("invalid type: E" + exponentBits + "M" + mantissaBits);
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
