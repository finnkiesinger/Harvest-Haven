package graphics;

import game.Assets;
import general.Global;
import general.Rectangle;
import general.Trigger;
import general.Vector2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Sprite implements Comparable<Sprite> {
    public static final int BOUNDING_BOX = 0;
    public static final int SPRITE = 1;
    public static final int TRIGGER = 2;
    public static final int EVERYTHING = 3;

    protected final List<Image> images = new ArrayList<>();
    protected double x, y;

    private String name = null;

    int width, height;

    protected Rectangle boundingBox = null;
    protected Trigger trigger = null;

    public Sprite(String name, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;

        images.add(Assets.instance
                .getImage(name)
                .getSubimage(x, y, width, height)
                .getScaledInstance((int) (width * Global.SPRITE_SCALE), (int) (height * Global.SPRITE_SCALE), Image.SCALE_SMOOTH));
    }

    public Sprite(String name, int x, int y) {
        this.name = name;
        this.x = (int) (x * Global.SPRITE_SCALE);
        this.y = (int) (y * Global.SPRITE_SCALE);
        BufferedImage image = Assets.instance.getImage(name);
        this.width = (int) (image.getWidth() * Global.SPRITE_SCALE);
        this.height = (int) (image.getHeight() * Global.SPRITE_SCALE);

        images.add(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    public String getName() {
        return name;
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
        if ((mode == TRIGGER || mode == EVERYTHING) && trigger != null) {
            Vector2 position = Camera.main
                    .apply(new Vector2((int) x, (int) y));
            graphics.setColor(Color.GREEN);
            graphics.drawRect(position.x + trigger.getBoundingBox().x, position.y + trigger.getBoundingBox().y, trigger.getBoundingBox().width, trigger.getBoundingBox().height);
        }
    }

    /**
     * Update the sprite
     *
     * @param deltaTime time since last update, in nanoseconds
     */
    public void update(long deltaTime) {

    }

    /**
     * Get the position of the sprite
     *
     * @return the position of the sprite
     */
    public Vector2 getPosition() {
        return new Vector2((int) x, (int) y);
    }

    /**
     * Used to order sprites by according to the drawing order.
     * Sprites with higher y values are drawn on top of sprites with lower y values.
     *
     * @param other the object to be compared.
     * @return a negative integer if this object should be drawn below the other object,
     * and a positive integer if this object should be drawn above the other object.
     */
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

    public void setTrigger(Trigger trigger) {
        if (trigger == null) return;

        trigger.setBoundingBox(trigger.getBoundingBox().scaled(Global.SPRITE_SCALE));
        this.trigger = trigger;
    }

    public Trigger getTrigger() {
        return trigger;
    }
}
