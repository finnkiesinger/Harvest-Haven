package graphics;

import general.Global;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Tiles {
    private static final Map<String, Map<Integer, BufferedImage>> gids = new HashMap<>();


    public static BufferedImage getTileImage(String source, int gid) {
        return gids.get(source).get(gid);
    }

    public static void addTile(int gid, int tileWidth, int tileHeight, int columns, BufferedImage source, String sourceName) {
        if (!gids.containsKey(sourceName)) {
            gids.put(sourceName, new HashMap<>());
        }
        if (!gids.get(sourceName).containsKey(gid)) {
            int x = (gid - 1) % columns;
            int y = (gid - 1) / columns;

            BufferedImage sourceImage = source
                    .getSubimage(x * tileWidth, y * tileHeight, tileWidth, tileHeight);

            BufferedImage tileImage = new BufferedImage(
                    (int) (tileWidth * Global.SPRITE_SCALE),
                    (int) (tileHeight * Global.SPRITE_SCALE),
                    BufferedImage.TYPE_INT_ARGB
            );

            AffineTransform tx = AffineTransform.getScaleInstance(Global.SPRITE_SCALE, Global.SPRITE_SCALE);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            tileImage = op.filter(sourceImage, tileImage);
            Map<Integer, BufferedImage> tile = gids.computeIfAbsent(sourceName, k -> new HashMap<>());
            tile.put(gid, tileImage);
        }
    }
}
