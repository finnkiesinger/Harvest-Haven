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

    public Animation(String name, List<List<Sprite.SpriteInfo>> layers, long frameTime, boolean loop) {
        assert layers.size() > 0;

        this.name = name;
        this.frameCount = layers.get(0).size();
        this.currentFrame = 0;
        this.frameTime = frameTime;
        this.loop = loop;
        for (List<Sprite.SpriteInfo> sprites : layers) {
            List<Image> images = new ArrayList<>();
            sprites.forEach(spriteInfo -> images.add(Assets.instance
                    .getImage(spriteInfo.name)
                    .getSubimage(spriteInfo.x, spriteInfo.y, spriteInfo.width, spriteInfo.height)
                    .getScaledInstance((int) (spriteInfo.width * Global.SPRITE_SCALE), (int) (spriteInfo.height * Global.SPRITE_SCALE), Image.SCALE_SMOOTH)));
            frames.add(images);
        }
    }

    public Animation(String name, List<List<Sprite.SpriteInfo>> layers, long frameTime) {
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
}
