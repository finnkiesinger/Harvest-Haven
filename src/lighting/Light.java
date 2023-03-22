package lighting;

import java.awt.*;

abstract public class Light {
    protected Color color;
    protected double strength;

    abstract public Color getLight(int x, int y);
}
