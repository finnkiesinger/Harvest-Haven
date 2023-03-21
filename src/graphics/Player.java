package graphics;

import general.*;
import general.Rectangle;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Player extends AnimatedSprite {
    private static final double SPEED = 80;
    private double dx = 0;
    private double dy = 0;
    Direction facing;
    private Level level;

    public Player(Vector2 spawnPoint) {
        super(List.of(
                new SpriteInfo("character", 0, 0, 32, 32),
                new SpriteInfo("basic", 0, 0, 32, 32),
                new SpriteInfo("pants", 0, 0, 32, 32),
                new SpriteInfo("shoes", 0, 0, 32, 32)
        ));

        x = spawnPoint.x;
        y = spawnPoint.y;
        addBoundingBox(-6, 6, 12, 12);
        facing = Direction.DOWN;
        SetupAnimations();
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
                .apply(new Vector2((int) x, (int) y))
                .subtract(getCenter());

        sprites.forEach(sprite -> graphics.drawImage(sprite, position.x, position.y, null));
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public void update(long deltaTime) {
        super.update(deltaTime);
        double adjustX = dx * SPEED * deltaTime / 1e9f;
        double adjustY = dy * SPEED * deltaTime / 1e9f;
        x += adjustX;
        y += adjustY;

        if (level != null) {
            for (Rectangle other : level.getCollisionRects(this)) {
                Rectangle intersect = new Rectangle(
                        (int) (x + boundingBox.x),
                        (int) (y + boundingBox.y),
                        boundingBox.width,
                        boundingBox.height
                );
                Intersection intersection = intersect.intersects(other);
                if (intersection.intersects) {
                    if (intersection.side == Side.LEFT) {
                        x = other.x + other.width - boundingBox.x;
                    } else if (intersection.side == Side.RIGHT) {
                        x = other.x - boundingBox.x - boundingBox.width;
                    } else if (intersection.side == Side.TOP) {
                        y = other.y + other.height - boundingBox.y;
                    } else if (intersection.side == Side.BOTTOM) {
                        y = other.y - boundingBox.y - boundingBox.height;
                    }
                }
            }
            Rectangle intersect = new Rectangle(
                    (int) (x + boundingBox.x),
                    (int) (y + boundingBox.y),
                    boundingBox.width,
                    boundingBox.height
            );
            for (Sprite other : level.getSpriteTriggers(this)) {
                Rectangle otherIntersect = new Rectangle(
                        (int) (other.x + other.getTrigger().getBoundingBox().x),
                        (int) (other.y + other.getTrigger().getBoundingBox().y),
                        other.getTrigger().getBoundingBox().width,
                        other.getTrigger().getBoundingBox().height
                );
                Intersection intersection = intersect.intersects(otherIntersect);
                if (intersection.intersects) {
                    other.getTrigger().trigger();
                }
            }
        }
    }

    public void moveHorizontal(int axisValue) {
        dx = axisValue;
        if (axisValue > 0) {
            facing = Direction.RIGHT;
        } else {
            facing = Direction.LEFT;
        }
    }

    public void moveVertical(int axisValue) {
        dy = axisValue;
        if (axisValue > 0) {
            facing = Direction.DOWN;
        } else {
            facing = Direction.UP;
        }
    }

    public void stopMoving (boolean x, boolean y) {
        if (x) {
            dx = 0;
        }
        if (y) {
            dy = 0;
        }
        if (x && y) {
            PlayIdleAnimation();
        }
    }

    public boolean isFacing(Direction direction) {
        return facing == direction;
    }

    private void PlayIdleAnimation() {
        switch (facing) {
            case UP:
                playAnimation("IdleUp");
                break;
            case DOWN:
                playAnimation("IdleDown");
                break;
            case LEFT:
                playAnimation("IdleLeft");
                break;
            case RIGHT:
                playAnimation("IdleRight");
                break;
            default:
                stopAnimation();
                break;
        }
    }

    /**
     * Sets up the animations for the player
     * @param component The name of the component
     * @param offset offset within source image (to specify stuff like hair color)
     * @param walkDown List of sprites for walking down animation
     * @param walkUp List of sprites for walking up animation
     * @param walkLeft List of sprites for walking left animation
     * @param walkRight List of sprites for walking right animation
     * @param idleDown  List of sprites for idle down animation
     * @param idleUp List of sprites for idle up animation
     * @param idleLeft List of sprites for idle left animation
     * @param idleRight List of sprites for idle right animation
     */
    private void SetupCharacterComponent(
            String component,
            int offset,
            List<List<SpriteInfo>> walkDown,
            List<List<SpriteInfo>> walkUp,
            List<List<SpriteInfo>> walkLeft,
            List<List<SpriteInfo>> walkRight,
            List<List<SpriteInfo>> idleDown,
            List<List<SpriteInfo>> idleUp,
            List<List<SpriteInfo>> idleLeft,
            List<List<SpriteInfo>> idleRight
    ) {
        List<SpriteInfo> componentWalkDown = new ArrayList<>();
        List<SpriteInfo> componentWalkUp = new ArrayList<>();
        List<SpriteInfo> componentWalkLeft = new ArrayList<>();
        List<SpriteInfo> componentWalkRight = new ArrayList<>();
        List<SpriteInfo> componentIdleDown = new ArrayList<>();
        List<SpriteInfo> componentIdleUp = new ArrayList<>();
        List<SpriteInfo> componentIdleLeft = new ArrayList<>();
        List<SpriteInfo> componentIdleRight = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            componentWalkDown.add(new SpriteInfo(component, i * 32 + 8 * offset * 32, 0, 32, 32));
            componentWalkUp.add(new SpriteInfo(component, i * 32 + 8 * offset * 32, 32, 32, 32));
            componentWalkRight.add(new SpriteInfo(component, i * 32 + 8 * offset * 32, 64, 32, 32));
            componentWalkLeft.add(new SpriteInfo(component, i * 32 + 8 * offset * 32, 96, 32, 32));
        }

        componentIdleDown.add(new SpriteInfo(component, 8 * offset * 32, 0, 32, 32));
        componentIdleUp.add(new SpriteInfo(component, 8 * offset * 32, 32, 32, 32));
        componentIdleRight.add(new SpriteInfo(component, 8 * offset * 32, 64, 32, 32));
        componentIdleLeft.add(new SpriteInfo(component, 8 * offset * 32, 96, 32, 32));

        walkDown.add(componentWalkDown);
        walkUp.add(componentWalkUp);
        walkLeft.add(componentWalkLeft);
        walkRight.add(componentWalkRight);
        idleDown.add(componentIdleDown);
        idleUp.add(componentIdleUp);
        idleLeft.add(componentIdleLeft);
        idleRight.add(componentIdleRight);
    }

    /**
     * Set up the animations for the character
     */
    @Override
    protected void SetupAnimations() {
        // Walk Down Animation
        List<List<SpriteInfo>> walkDown = new ArrayList<>();
        List<List<SpriteInfo>> walkUp = new ArrayList<>();
        List<List<SpriteInfo>> walkRight = new ArrayList<>();
        List<List<SpriteInfo>> walkLeft = new ArrayList<>();
        List<List<SpriteInfo>> idleDown = new ArrayList<>();
        List<List<SpriteInfo>> idleUp = new ArrayList<>();
        List<List<SpriteInfo>> idleRight = new ArrayList<>();
        List<List<SpriteInfo>> idleLeft = new ArrayList<>();

        SetupCharacterComponent(
                "character",
                2,
                walkDown,
                walkUp,
                walkLeft,
                walkRight,
                idleDown,
                idleUp,
                idleLeft,
                idleRight
        );
        SetupCharacterComponent(
                "basic",
                0,
                walkDown,
                walkUp,
                walkLeft,
                walkRight,
                idleDown,
                idleUp,
                idleLeft,
                idleRight
        );
        SetupCharacterComponent(
                "pants",
                0,
                walkDown,
                walkUp,
                walkLeft,
                walkRight,
                idleDown,
                idleUp,
                idleLeft,
                idleRight
        );
        SetupCharacterComponent(
                "shoes",
                0,
                walkDown,
                walkUp,
                walkLeft,
                walkRight,
                idleDown,
                idleUp,
                idleLeft,
                idleRight
        );
        SetupCharacterComponent(
                "hair",
                0,
                walkDown,
                walkUp,
                walkLeft,
                walkRight,
                idleDown,
                idleUp,
                idleLeft,
                idleRight
        );

        addAnimation(new Animation("WalkDown", walkDown, Duration.ofMillis(100).toNanos(), true));
        addAnimation(new Animation("WalkUp", walkUp, Duration.ofMillis(100).toNanos(), true));
        addAnimation(new Animation("WalkLeft", walkLeft, Duration.ofMillis(100).toNanos(), true));
        addAnimation(new Animation("WalkRight", walkRight, Duration.ofMillis(100).toNanos(), true));
        addAnimation(new Animation("IdleDown", idleDown, Duration.ofMillis(100).toNanos()));
        addAnimation(new Animation("IdleUp", idleUp, Duration.ofMillis(100).toNanos()));
        addAnimation(new Animation("IdleLeft", idleLeft, Duration.ofMillis(100).toNanos()));
        addAnimation(new Animation("IdleRight", idleRight, Duration.ofMillis(100).toNanos()));
    }
}
