package graphics;

import game.Assets;
import general.Animation;
import general.Global;

import java.awt.*;
import java.time.Duration;
import java.util.List;

public class AnimatedWorldObject extends AnimatedSprite {
    public AnimatedWorldObject(List<List<String>> sprites, int x, int y, long frameTime) {
        super(List.of());
        this.x = (int) (x * Global.SPRITE_SCALE);
        this.y = (int) (y * Global.SPRITE_SCALE);
        this.width = (int) (Assets.instance.getImage(sprites.get(0).get(0)).getWidth(null) * Global.SPRITE_SCALE);
        this.height = (int) (Assets.instance.getImage(sprites.get(0).get(0)).getHeight(null) * Global.SPRITE_SCALE);

        images.add(Assets.instance.getImage(sprites.get(0).get(0)).getScaledInstance(width, height, Image.SCALE_SMOOTH));

        Animation animation = new Animation("Interaction", sprites, frameTime, false);

        addAnimation(animation);
    }

    public AnimatedWorldObject(List<List<String>> sprites, int x, int y) {
        this(sprites, x, y, Duration.ofMillis(150).toNanos());
    }

    @Override
    protected void SetupAnimations() {

    }

    @Override
    public void update(long deltaTime) {
        super.update(deltaTime);
    }
}
