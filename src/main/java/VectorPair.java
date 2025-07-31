public class VectorPair {
    public final Vector v1;
    public final Vector v2;

    public VectorPair(Vector v1, Vector v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public VectorPair random(int size, FloatType type) {
        return new VectorPair(Vector.random(size, type), Vector.random(size, type));
    }

}