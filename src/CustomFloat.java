import java.util.Arrays;

public class CustomFloat {
    private final FPType type;

    private final int totalBits;
    private final int exponentBits;
    private final int mantissaBits;
    private final int bias;

    private boolean sign;
    private boolean[] exponent;
    private boolean[] mantissa;

    public CustomFloat(float number, FPType type) {
        this.type = type;
        this.totalBits = type.getTotalBits();
        this.exponentBits = type.getExponentBits();
        this.mantissaBits = type.getMantissaBits();
        this.bias = (1 << (exponentBits - 1)) - 1; // TODO Add bias as variable

        encodeFloat(number);
    }

    private void encodeFloat(float number) {
        // TODO Even rounding!
        if (number == 0) {
            sign = false;
            exponent = new boolean[exponentBits];
            mantissa = new boolean[mantissaBits];
            return;
        }

        sign = number < 0;
        number = Math.abs(number);

        int exp = (int) (Math.log(number) / Math.log(2));
        float fraction = (number / ((float) Math.pow(2, exp))) - 1;
        exp += bias;

        // Handle underflow
//        if (exp <= 0) {
//            sign = false;
//            exponent = new boolean[exponentBits];
//            mantissa = new boolean[mantissaBits];
//            return;
//        }

        // Handle denormalized numbers (exp < 1)
        if (exp <= 0) {
            exp = 0; // Set exponent to zero for denormals
            fraction = number / ((float) Math.pow(2, 1 - bias)); // Scale fraction
        }

        // Handle overflow
        if (exp > ((1 << exponentBits) - 1)) {
            System.out.print("Overflow");
            exponent = intToBooleanArray((1 << exponentBits) - 1, exponentBits);
            mantissa = new boolean[mantissaBits];
            Arrays.fill(mantissa, true);
            return;
        }

        exponent = intToBooleanArray(exp, exponentBits);
        mantissa = fractionToBooleanArray(fraction, mantissaBits);
    }

    public static boolean[] intToBooleanArray(int value, int size) {
        boolean[] bits = new boolean[size];
        for (int i = size - 1; i >= 0; i--) {
            bits[size - i - 1] = (value & (1 << i)) != 0;
        }
        return bits;
    }

    private boolean[] fractionToBooleanArray(float fraction, int size) {
        boolean[] bits = new boolean[size];
        for (int i = 0; i < size; i++) {
            fraction *= 2;
            if (fraction >= 1) {
                bits[i] = true;
                fraction -= 1;
            }
        }
        return bits;
    }

    public float toFloat() {
        int exp = booleanArrayToInt(exponent);

        if (!sign && (exp == 0) && isAllZero(mantissa))
            return 0;

        float fraction = 1.0f;

        // Denormals
        if (exp == 0) {
            fraction = 0.0f;
            exp = (exp - bias) + 1;
        } else {
            exp -= bias;
        }
        for (int i = 0; i < mantissa.length; i++) {
            if (mantissa[i]) {
                fraction += Math.pow(2, -(i + 1));
            }
        }
        float value = (sign ? -1 : 1) * fraction * (float) Math.pow(2, exp);
        return value;
    }

    private boolean isAllZero(boolean[] array) {
        for (boolean b : array) {
            if (b) return false;
        }
        return true;
    }

    private int booleanArrayToInt(boolean[] bits) {
        int value = 0;
        for (int i = 0; i < bits.length; i++) {
            if (bits[i]) {
                value += (1 << (bits.length - 1 - i));
            }
        }
        return value;
    }

    public CustomFloat plus(CustomFloat other) {
        float result = this.toFloat() + other.toFloat();
        return new CustomFloat(result, type);
    }

    public CustomFloat minus(CustomFloat other) {
        float result = this.toFloat() - other.toFloat();
        return new CustomFloat(result, type);
    }

    public CustomFloat times(CustomFloat other, FPType type) {
        float result = this.toFloat() * other.toFloat();
        return new CustomFloat(result, type);
    }

    @Override
    public String toString() {
//        return String.format("Sign: %b, Exponent: %s, Mantissa: %s (%.3f)",
//                sign, Arrays.toString(exponent), Arrays.toString(mantissa), toFloat());
        return toFloat() + " (" + getBitRepresentation() + ")";
    }

    public FPType getType() {
        return type;
    }

    public int getTotalBits() {
        return totalBits;
    }

    public int getExponentBits(){
        return exponentBits;
    }

    public String getBitRepresentation() {
        StringBuilder sb = new StringBuilder();
        if (sign)
            sb.append("1");
        else
            sb.append("0");
        for (boolean b : exponent) {
            if (b)
                sb.append("1");
            else
                sb.append("0");
        }
        for (boolean b : mantissa) {
            if (b)
                sb.append("1");
            else
                sb.append("0");
        }
        return sb.toString();
    }
}
