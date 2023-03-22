package graphics;

import general.Vector2;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Layer {
    private final int index;
    private final String name;
    private final int width;
    private final int height;
    private final int tileWidth;
    private final int tileHeight;
    private final List<Integer> tiles = new ArrayList<>();
    private final String source;

    private BufferedImage layerImage = null;

    public Layer(int index, String name, int width, int height, int tileWidth, int tileHeight, String source) {
        this.index = index;
        this.name = name;
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void addTile(int gid) {
        tiles.add(gid);
    }

    public int getTile(int x, int y) {
        return tiles.get(y * width + x);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Vector2 getSize() {
        return new Vector2(width * tileWidth, height * tileHeight);
    }

    public void createLayerImage() {
        BufferedImage layerImage = new BufferedImage(
                width * tileWidth,
                height * tileHeight,
                BufferedImage.TYPE_INT_ARGB
        );
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gid = getTile(x, y);
                if (gid == 0) {
                    for (int i = 0; i < tileWidth; i++) {
                        for (int j = 0; j < tileHeight; j++) {
                            layerImage.setRGB(x * tileWidth + i, y * tileHeight + j, 0);
                        }
                    }
                } else {
                    BufferedImage tileImage = Tiles.getTileImage(source, gid);
                    for (int i = 0; i < tileWidth; i++) {
                        for (int j = 0; j < tileHeight; j++) {
                            layerImage.setRGB(x * tileWidth + i, y * tileHeight + j, tileImage.getRGB(i, j));
                        }
                    }
                }
            }
        }

        this.layerImage = layerImage;
    }

    public BufferedImage getLayerImage() {
        return layerImage;
    }
}
