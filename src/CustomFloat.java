public class CustomFloat {

    private final int totalBits;
    private final int exponentBits;
    private final int mantissaBits;
    private final int bias;

    private int value;

    public CustomFloat(float number, int totalBits, int exponentBits) {
        this.totalBits = totalBits;
        this.exponentBits = exponentBits;
        this.mantissaBits = totalBits - exponentBits - 1;
        this.bias = (1 << (exponentBits - 1)) - 1;

        this.value = floatToCustom(number);
    }

    public CustomFloat(int value, int totalBits, int exponentBits) {
        this.totalBits = totalBits;
        this.exponentBits = exponentBits;
        this.mantissaBits = totalBits - exponentBits - 1;
        this.bias = (1 << (exponentBits - 1)) - 1;

        this.value = value;
    }

    private int floatToCustom(float number) {
        if (number == 0) return 0; // Special case for zero

        int sign = (number < 0) ? 1 : 0;
        number = Math.abs(number);

        int exponent = (int) (Math.log(number) / Math.log(2));  // Unbiased exponent

        // Handle underflow (subnormal numbers)
        if (exponent < -bias + 1) {
            // Subnormal number case
            exponent = 0;
            number *= (1 << (bias - 1)); // Scale up to fit in subnormal range
        } else {
            // Normalized number case
            number /= Math.pow(2, exponent); // Normalize to [1,2)
            exponent += bias; // Apply bias
        }

        // Check for overflow (exponent too large)
        if (exponent >= (1 << exponentBits) - 1) {
            // Set to max exponent (infinity representation)
            exponent = (1 << exponentBits) - 1;
            return (sign << (totalBits - 1)) | (exponent << mantissaBits);
        }

        // Compute the mantissa
        float fraction = number - 1;  // Remove leading 1
        int fractionBits = (int) (fraction * (1 << mantissaBits)) & ((1 << mantissaBits) - 1);

        // Assemble the final FP representation
        return (sign << (totalBits - 1)) | ((exponent & ((1 << exponentBits) - 1)) << mantissaBits) | fractionBits;
    }

    public float toFloat() {
        if (value == 0) return 0;
        int sign = (value & (1 << (totalBits - 1))) != 0 ? -1 : 1;
        int exponent = ((value >> mantissaBits) & ((1 << exponentBits) - 1)) - bias;
        float fraction = 1 + ((value & ((1 << mantissaBits) - 1)) / (float) (1 << mantissaBits));
        return sign * fraction * (float) Math.pow(2, exponent);
    }

    public CustomFloat add(CustomFloat other) {
        float result = this.toFloat() + other.toFloat();
        return new CustomFloat(result, totalBits, exponentBits);
    }

    public CustomFloat substract(CustomFloat other) {
        float result = this.toFloat() - other.toFloat();
        CustomFloat customFloat = new CustomFloat(result, totalBits, exponentBits);
        return customFloat;
    }

    public CustomFloat multiply(CustomFloat other, int totalBits, int exponentBits) {
        float result = this.toFloat() * other.toFloat();
        return new CustomFloat(result, totalBits, exponentBits);
    }

    @Override
    public String toString() {
        return String.format("%s (%.3f)", Integer.toBinaryString(value & ((1 << totalBits) - 1)), toFloat());
//        return String.valueOf(toFloat());
    }

    public int getTotalBits() {
        return totalBits;
    }

    public int getExponentBits(){
        return exponentBits;
    }
}
