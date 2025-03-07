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
        int sign = (number < 0) ? 1 : 0;
        number = Math.abs(number);

        int exponent = (int) (Math.log(number) / Math.log(2)) + bias;
        float fraction = number / (float) Math.pow(2, exponent - bias) - 1;
        int fractionBits = (int) (fraction * (1 << mantissaBits)) & ((1 << mantissaBits) - 1);

        return (sign << (totalBits - 1)) | ((exponent & ((1 << exponentBits) - 1)) << mantissaBits) | fractionBits;
    }

    public float toFloat() {
        int sign = (value & (1 << (totalBits - 1))) != 0 ? -1 : 1;
        int exponent = ((value >> mantissaBits) & ((1 << exponentBits) - 1)) - bias;
        float fraction = 1 + ((value & ((1 << mantissaBits) - 1)) / (float) (1 << mantissaBits));
        return sign * fraction * (float) Math.pow(2, exponent);
    }

    public CustomFloat add(CustomFloat other) {
        float result = this.toFloat() + other.toFloat();
        return new CustomFloat(result, totalBits, exponentBits);
    }

    public CustomFloat multiply(CustomFloat other, int totalBits, int exponentBits) {
        float result = this.toFloat() * other.toFloat();
        return new CustomFloat(result, totalBits, exponentBits);
    }

    @Override
    public String toString() {
        return String.format("%s (%.3f)", Integer.toBinaryString(value & ((1 << totalBits) - 1)), toFloat());
    }

    public int getTotalBits() {
        return totalBits;
    }

    public int getExponentBits(){
        return exponentBits;
    }
}
