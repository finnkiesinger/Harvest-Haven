package graphics;

import game.Assets;
import general.Global;
import general.Rectangle;
import general.Vector2;

import java.awt.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Sprite implements Comparable<Sprite> {
    public static final int BOUNDING_BOX = 0;
    public static final int SPRITE = 1;
    public static final int EVERYTHING = 2;

    protected final List<Image> images = new ArrayList<>();
    protected double x, y;

    int width, height;

    protected Rectangle boundingBox = null;

    public Sprite(String name, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;

        images.add(Assets.instance
                .getImage(name)
                .getSubimage(x, y, width, height)
                .getScaledInstance((int) (width * Global.SPRITE_SCALE), (int) (height * Global.SPRITE_SCALE), Image.SCALE_SMOOTH));
    }

    public Sprite(String name, int x, int y) {
        this.x = (int) (x * Global.SPRITE_SCALE);
        this.y = (int) (y * Global.SPRITE_SCALE);
        BufferedImage image = Assets.instance.getImage(name);
        this.width = (int) (image.getWidth() * Global.SPRITE_SCALE);
        this.height = (int) (image.getHeight() * Global.SPRITE_SCALE);

        images.add(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    public Sprite(SpriteInfo spriteInfo) {
        this(spriteInfo.name, spriteInfo.x, spriteInfo.y, spriteInfo.width, spriteInfo.height);
    }

    public void addBoundingBox(int x, int y, int width, int height) {
        this.boundingBox = new Rectangle(
                (int) (x * Global.SPRITE_SCALE),
                (int) (y * Global.SPRITE_SCALE),
                (int) (width * Global.SPRITE_SCALE),
                (int) (height * Global.SPRITE_SCALE)
        );
    }

    public void addBoundingBox(Rectangle box) {
        this.boundingBox = new Rectangle(
                (int) (box.x * Global.SPRITE_SCALE),
                (int) (box.y * Global.SPRITE_SCALE),
                (int) (box.width * Global.SPRITE_SCALE),
                (int) (box.height * Global.SPRITE_SCALE)
        );
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public Sprite(List<SpriteInfo> spriteInfoList) {
        for (SpriteInfo spriteInfo : spriteInfoList) {
            if (this.width == 0 && this.height == 0) {
                this.width = spriteInfo.width;
                this.height = spriteInfo.height;
            } else if (this.width != spriteInfo.width || this.height != spriteInfo.height) {
                throw new IllegalArgumentException("All sprites.xml must have the same width and height");
            }

            images.add(Assets.instance
                    .getImage(spriteInfo.name)
                    .getSubimage(spriteInfo.x, spriteInfo.y, spriteInfo.width, spriteInfo.height)
                    .getScaledInstance((int) (spriteInfo.width * Global.SPRITE_SCALE), (int) (spriteInfo.height * Global.SPRITE_SCALE), Image.SCALE_SMOOTH));
        }
    }

    protected Vector2 getCenter() {
        return new Vector2(
                (int) (Global.SPRITE_SCALE * width / 2),
                (int) (Global.SPRITE_SCALE * height / 2)
        );
    }

    public void draw(Graphics2D graphics) {
        Vector2 position = Camera.main
                .apply(new Vector2((int) x, (int) y));
        images.forEach(image -> graphics.drawImage(image, position.x, position.y, null));
    }

    public void draw(Graphics2D graphics, int mode) {
        if (mode == SPRITE || mode == EVERYTHING) {
            draw(graphics);
        }
        if ((mode == BOUNDING_BOX || mode == EVERYTHING) && boundingBox != null) {
            Vector2 position = Camera.main
                    .apply(new Vector2((int) x, (int) y));
            graphics.setColor(Color.RED);
            graphics.drawRect(position.x + boundingBox.x, position.y + boundingBox.y, boundingBox.width, boundingBox.height);
        }
    }

    public void update(long deltaTime) {

    }

    public Vector2 getPosition() {
        return new Vector2((int) x, (int) y);
    }

    @Override
    public int compareTo(Sprite other) {
        if (this.boundingBox != null && other.boundingBox != null) {
            int bottom = (int) (this.y + this.boundingBox.y + this.boundingBox.height);
            int otherBottom = (int) (other.y + other.boundingBox.y + other.boundingBox.height);
            return bottom - otherBottom;
        }

        int bottom = (int) (this.y + this.height);
        int otherBottom = (int) (other.y + other.height);
        return bottom - otherBottom;
    }

    public static class SpriteInfo {
        public final int x, y, width, height;
        public final String name;

        public SpriteInfo(String name, int x, int y, int width, int height) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
