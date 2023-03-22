package lighting;

import java.awt.*;

public class DirectionalLight extends Light {

    public DirectionalLight(Color color, double strength) {
        this.color = color;
        this.strength = strength;
    }

    @Override
    public Color getLight(int x, int y) {
        return new Color(
                (int) (color.getRed() * strength),
                (int) (color.getGreen() * strength),
                (int) (color.getBlue() * strength)
        );
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }
}
