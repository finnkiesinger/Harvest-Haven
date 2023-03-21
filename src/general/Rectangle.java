package general;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class Rectangle implements Cloneable {
    public final int x, y, width, height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Intersection intersects(Rectangle other) {
        boolean intersects = x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y;
        if (intersects) {
            // Find which side is intersecting and pass it to the intersection object
            List<AbstractMap.SimpleEntry<Side, Double>> sides = new ArrayList<>();
            sides.add(new AbstractMap.SimpleEntry<>(Side.TOP, (double) (other.y + other.height - y)));
            sides.add(new AbstractMap.SimpleEntry<>(Side.BOTTOM, (double) (y + height - other.y)));
            sides.add(new AbstractMap.SimpleEntry<>(Side.LEFT, (double) (other.x + other.width - x)));
            sides.add(new AbstractMap.SimpleEntry<>(Side.RIGHT, (double) (x + width - other.x)));
            sides.sort(java.util.Map.Entry.comparingByValue());
            return new Intersection(true, sides.get(0).getKey());
        }

        return new Intersection(false, null);
    }

    public Rectangle scaled(double scale) {
        return new Rectangle(
                (int) (this.x * scale),
                (int) (this.y * scale),
                (int) (this.width * scale),
                (int) (this.height * scale)
        );
    }

    public Rectangle clone() {
        try {
            super.clone();
            return new Rectangle(x, y, width, height);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
