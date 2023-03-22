package graphics;

import general.Animation;
import general.Vector2;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AnimatedSprite is a Sprite that can play animations.
 * It is abstract because it requires the SetupAnimations method to be implemented.
 * Instead of drawing a fixed image on each frame, it stores different animations that can be played.
 */
abstract public class AnimatedSprite extends Sprite {
    /**
     * A map of all the animations that can be played.
     */
    protected final Map<String, Animation> animations = new HashMap<>();
    /**
     * The name of the current animation.
     */
    protected String currentAnimation;

    public AnimatedSprite(List<Sprite.SpriteInfo> sprites) {
        super(sprites);
    }

    /**
     * This method is called to add all animations to the animations map.
     */
    abstract protected void SetupAnimations();

    /**
     * Adds an animation to the animations map.
     *
     * @param animation The animation to add.
     */
    protected void addAnimation(Animation animation) {
        animations.put(animation.getName(), animation);
    }

    /**
     * Plays an animation.
     *
     * @param name The name of the animation to play.
     */
    public void playAnimation(String name) {
        boolean newAnimation = currentAnimation == null || !currentAnimation.equals(name);
        if (!newAnimation) {
            return;
        }
        if (currentAnimation != null) {
            newAnimation = animations.get(currentAnimation).canCancel();
        }
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

            if (animation.isDone()) {
                stopAnimation();
            }
        }
    }

    @Override
    public void draw(Graphics2D graphics) {
        Animation animation = animations.get(currentAnimation);
        List<Image> sprites;
        if (animation != null) {
            sprites = List.of(animation.getCurrentFrame());
        } else {
            sprites = images;
        }

        Vector2 position = Camera.main
                .apply(new Vector2((int) x, (int) y));

        sprites.forEach(sprite -> graphics.drawImage(sprite, position.x, position.y, null));
    }
}
