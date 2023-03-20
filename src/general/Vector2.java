package general;

public class Vector2 {
    public final int x, y;

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(x + other.x, y + other.y);
    }

    public Vector2 subtract(Vector2 other) {
        return new Vector2(x - other.x, y - other.y);
    }

    public Vector2 multiply(int scalar) {
        return new Vector2(x * scalar, y * scalar);
    }

    public Vector2 divide(int scalar) {
        return new Vector2(x / scalar, y / scalar);
    }

    @Override
    public String toString() {
        return "Vector2{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
