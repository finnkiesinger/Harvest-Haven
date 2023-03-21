package graphics;

import general.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class SpriteData {
    private final List<List<String>> names = new ArrayList<>();
    private Rectangle boundingBox = null;
    private final boolean animated;

    public SpriteData(String name) {
        this.names.add(List.of(name));
        animated = false;
    }

    public SpriteData(List<String> names, boolean animated) {
        this.animated = animated;
        if (animated) {
            this.names.add(names);
        } else {
            names.forEach(name -> this.names.add(List.of(name)));
        }
    }

    public boolean isAnimated() {
        return animated;
    }

    public void addBoundingBox(int x, int y, int width, int height) {
        boundingBox = new Rectangle(x, y, width, height);
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public List<List<String>> getNames() {
        return names;
    }
}
