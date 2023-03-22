package lighting;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Lighting {
    public static final Lighting instance = new Lighting();
    private final List<Light> lights = new ArrayList<>();

    private Lighting() {
    }

    public static int calculateBrightness(Color color) {
        return (color.getRed() + color.getGreen() + color.getBlue()) / 3;
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    public void removeLight(Light light) {
        lights.remove(light);
    }

    private Color getLight(int x, int y) {
        Color brightestColor = Color.BLACK;
        int maxBrightness = calculateBrightness(brightestColor);

        for (Light light : lights) {
            Color lightColor = light.getLight(x, y);
            int brightness = calculateBrightness(lightColor);
            if (brightness > maxBrightness) {
                brightestColor = lightColor;
                maxBrightness = brightness;
            }
        }

        return new Color(0, 0, 0, 255 - maxBrightness);
    }

    public BufferedImage calculateLightMap(int width, int height) {
        BufferedImage lightMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                lightMap.setRGB(x, y, getLight(x, y).getRGB());
            }
        }
        return lightMap;
    }
}
