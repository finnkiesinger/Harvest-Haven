package lighting;

import java.awt.*;

public class PointLight extends Light {
    private int radius;
    private int x;
    private int y;

    public PointLight(Color color, double strength, int radius, int x, int y) {
        this.color = color;
        this.strength = strength;
        this.radius = radius;
        this.x = x;
        this.y = y;
    }

    @Override
    public Color getLight(int x, int y) {
        double distance = Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2));
        if (distance > radius) {
            return Color.BLACK;
        }
        double factor = 1 - distance / radius;
        return new Color(
                (int) (color.getRed() * strength * factor),
                (int) (color.getGreen() * strength * factor),
                (int) (color.getBlue() * strength * factor)
        );
    }
}
