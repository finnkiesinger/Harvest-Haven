package graphics;

import general.Vector2;

/**
 * Camera class.
 * Used to draw object relative to the camera position.
 *
 * @author Finn Kiesinger
 */
public class Camera {
    public static Camera main;
    public static boolean isInitialized = false;

    /**
     * Checks if a sprite is visible on the screen.
     *
     * @param sprite The sprite to check.
     * @return True if the sprite is visible, false otherwise.
     */
    public boolean isVisible(Sprite sprite) {
        Vector2 position = apply(sprite.getPosition());
        return position.x + sprite.width > 0 && position.x < MainWindow.instance.getWidth() &&
                position.y + sprite.height > 0 && position.y < MainWindow.instance.getHeight();
    }

    public static void initialize(int x, int y) {
        if (isInitialized) {
            return;
        }

        main = new Camera(x, y);
        isInitialized = true;
    }

    private int x, y;

    private Camera(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Applies the camera to an absolute position. Returns the position relative to the camera.
     *
     * @param position The position to apply the camera to.
     * @return The position relative to the camera.
     */
    public Vector2 apply(Vector2 position) {
        return MainWindow.instance.getCenter().add(position.subtract(new Vector2(x, y)));
    }

    /**
     * Sets the camera position.
     *
     * @param position The new position.
     */
    public void setPosition(Vector2 position) {
        Vector2 center = MainWindow.instance.getCenter();
        x = Math.max(center.x, position.x);
        y = Math.max(center.y, position.y);
    }
}
