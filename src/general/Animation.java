package general;

import game.Assets;
import graphics.Sprite;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Animation {
    private final String name;
    private final int frameCount;
    private int currentFrame;
    private int elapsedTime;
    private final long frameTime;
    private final boolean loop;
    private boolean done;
    private final List<List<Image>> frames = new ArrayList<>();

    public <T> Animation(String name, List<List<T>> layers, long frameTime, boolean loop) {
        assert layers.size() > 0;

        this.name = name;
        this.frameCount = layers.get(0).size();
        this.currentFrame = 0;
        this.frameTime = frameTime;
        this.loop = loop;
        boolean isSpriteInfo = layers.get(0).get(0) instanceof Sprite.SpriteInfo;

        if (isSpriteInfo) {
            for (List<?> sprites : layers) {
                List<Image> images = new ArrayList<>();
                sprites.forEach(spriteInfo -> images.add(Assets.instance
                        .getImage(((Sprite.SpriteInfo) spriteInfo).name)
                        .getSubimage(((Sprite.SpriteInfo) spriteInfo).x, ((Sprite.SpriteInfo) spriteInfo).y, ((Sprite.SpriteInfo) spriteInfo).width, ((Sprite.SpriteInfo) spriteInfo).height)
                        .getScaledInstance((int) (((Sprite.SpriteInfo) spriteInfo).width * Global.SPRITE_SCALE), (int) (((Sprite.SpriteInfo) spriteInfo).height * Global.SPRITE_SCALE), Image.SCALE_SMOOTH)));
                frames.add(images);
            }
        } else {
            for (List<?> sprites : layers) {
                List<Image> images = new ArrayList<>();
                sprites.forEach(spriteInfo -> {
                    Image image = Assets.instance.getImage((String) spriteInfo);
                    images.add(image.getScaledInstance((int) (image.getWidth(null) * Global.SPRITE_SCALE), (int) (image.getHeight(null) * Global.SPRITE_SCALE), Image.SCALE_SMOOTH));
                });
                frames.add(images);
            }
        }
    }

    public <T> Animation(String name, List<List<T>> layers, long frameTime) {
        this(name, layers, frameTime, false);
    }

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

    public List<Image> getCurrentFrame() {
        List<Image> images = new ArrayList<>();
        for (List<Image> frame : frames) {
            images.add(frame.get(currentFrame));
        }

        return images;
    }

    public void reset() {
        currentFrame = 0;
        elapsedTime = 0;
        done = false;
    }

    public boolean isDone() {
        return done;
    }
}
