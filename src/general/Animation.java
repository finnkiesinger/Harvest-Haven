package general;

import game.Assets;
import graphics.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Animation class
 *
 * @author Finn Kiesinger
 */
public class Animation {
    /**
     * Name of the animation
     */
    private final String name;
    /**
     * Number of frames in the animation
     */
    private final int frameCount;
    /**
     * Time to display each frame, in nanoseconds
     */
    private final long frameTime;
    /**
     * Whether the animation loops
     */
    private final boolean loop;
    /**
     * List of frames
     */
    private final List<Image> frames = new ArrayList<>();
    /**
     * Current frame
     */
    private int currentFrame;
    /**
     * Time since last frame change, in nanoseconds
     */
    private int elapsedTime;
    /**
     * Whether the animation is done
     */
    private boolean done;
    /**
     * Whether the animation can be cancelled
     */
    private boolean cancel;

    public <T> Animation(String name, List<List<T>> layers, long frameTime, boolean loop) {
        assert layers.size() > 0;

        this.name = name;
        this.frameCount = layers.get(0).size();
        this.currentFrame = 0;
        this.frameTime = frameTime;
        this.loop = loop;
        boolean isSpriteInfo = layers.get(0).get(0) instanceof Sprite.SpriteInfo;

        List<List<Image>> mergeImages = new ArrayList<>();

        if (isSpriteInfo) {
            for (List<?> sprites : layers) {
                List<Image> images = new ArrayList<>();
                sprites.forEach(spriteInfo -> images.add(Assets.instance
                        .getImage(((Sprite.SpriteInfo) spriteInfo).name)
                        .getSubimage(((Sprite.SpriteInfo) spriteInfo).x, ((Sprite.SpriteInfo) spriteInfo).y, ((Sprite.SpriteInfo) spriteInfo).width, ((Sprite.SpriteInfo) spriteInfo).height)
                        .getScaledInstance((int) (((Sprite.SpriteInfo) spriteInfo).width * Global.SPRITE_SCALE), (int) (((Sprite.SpriteInfo) spriteInfo).height * Global.SPRITE_SCALE), Image.SCALE_SMOOTH)));
                mergeImages.add(images);
            }
        } else {
            for (List<?> sprites : layers) {
                List<Image> images = new ArrayList<>();
                sprites.forEach(spriteInfo -> {
                    Image image = Assets.instance.getImage((String) spriteInfo);
                    images.add(image.getScaledInstance((int) (image.getWidth(null) * Global.SPRITE_SCALE), (int) (image.getHeight(null) * Global.SPRITE_SCALE), Image.SCALE_SMOOTH));
                });
                mergeImages.add(images);
            }
        }

        for (int i = 0; i < frameCount; i++) {
            BufferedImage image = new BufferedImage(mergeImages.get(0).get(i).getWidth(null), mergeImages.get(0).get(i).getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            for (List<Image> images : mergeImages) {
                g.drawImage(images.get(i), 0, 0, null);
            }
            g.dispose();
            frames.add(image);
        }
        this.cancel = true;
    }

    public boolean canCancel() {
        return cancel;
    }

    /**
     * Update the animation
     *
     * @param deltaTime time since last update, in nanoseconds
     */
    public void update(long deltaTime) {
        if (done) {
            return;
        }
        elapsedTime += deltaTime;
        if (elapsedTime > frameTime) {
            currentFrame++;
            if (currentFrame == frameCount) {
                currentFrame = 0;
                if (!loop) {
                    done = true;
                }
            }
            elapsedTime = 0;
        }
    }

    public String getName() {
        return name;
    }

    public Image getCurrentFrame() {
        return frames.get(currentFrame);
    }

    /**
     * Reset the animation
     */
    public void reset() {
        currentFrame = 0;
        elapsedTime = 0;
        done = false;
    }

    /**
     * Check if the animation is done
     */
    public boolean isDone() {
        return done;
    }
}
