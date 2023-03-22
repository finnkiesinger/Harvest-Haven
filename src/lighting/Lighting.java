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

    /**
     * Calculates the brightness of a color
     *
     * @param color the color
     * @return the brightness of the color
     */
    public static int calculateBrightness(Color color) {
        return (color.getRed() + color.getGreen() + color.getBlue()) / 3;
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    public void removeLight(Light light) {
        lights.remove(light);
    }

    /**
     * Returns the light color at the given coordinates
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return the light color at the given coordinates
     */
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

    /**
     * Calculates the light map, which is a BufferedImage with the light color at each pixel
     *
     * @param width  width of the light map (derived from level size)
     * @param height height of the light map (derived from level size)
     * @return the light map, as a BufferedImage
     */
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
