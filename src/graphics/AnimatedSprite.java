package graphics;

import general.Animation;
import general.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class AnimatedSprite extends Sprite {
    protected final Map<String, Animation> animations = new HashMap<>();
    protected String currentAnimation;

    public AnimatedSprite(List<Sprite.SpriteInfo> sprites) {
        super(sprites);
    }

    abstract protected void SetupAnimations();

    protected void addAnimation(Animation animation) {
        animations.put(animation.getName(), animation);
    }

    public void playAnimation(String name) {
        boolean newAnimation = currentAnimation == null || !currentAnimation.equals(name);
        if (newAnimation) {
            currentAnimation = name;
            Animation animation = animations.get(currentAnimation);
            if (animation != null) {
                animation.reset();
            }
        }
    }

    public void stopAnimation() {
        Animation animation = animations.get(currentAnimation);
        if (animation != null) {
            animation.reset();
        }
        currentAnimation = null;
    }

    public void update(long deltaTime) {
        Animation animation = animations.get(currentAnimation);

        if (animation != null) {
            animation.update(deltaTime);
        }
    }

    @Override
    public void draw(Graphics2D graphics) {
        Animation animation = animations.get(currentAnimation);
        List<Image> sprites;
        if (animation != null) {
            sprites = animation.getCurrentFrame();
        } else {
            sprites = images;
        }

        Vector2 position = Camera.main
                .apply(new Vector2((int) x, (int) y));

        sprites.forEach(sprite -> graphics.drawImage(sprite, position.x, position.y, null));
    }
}
