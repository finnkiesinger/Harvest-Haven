package graphics;

import general.Rectangle;

public class SpriteData {
    private String name;
    private Rectangle boundingBox = null;

    public SpriteData(String name) {
        this.name = name;
    }

    public void addBoundingBox(int x, int y, int width, int height) {
        boundingBox = new Rectangle(x, y, width, height);
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }
}
