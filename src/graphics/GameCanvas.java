package graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GameCanvas extends JPanel {
    private final List<Sprite> spriteList = new ArrayList<>();
    private Level level = null;


    public GameCanvas() {
        setBackground(Color.BLACK);
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        setCursor(blankCursor);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (level != null) {
            level.draw(g2d);
        }
        for (Sprite sprite : spriteList) {
            sprite.draw(g2d);
        }
    }

    public void addSprite(Sprite sprite) {
        spriteList.add(sprite);
    }

    public void removeSprite(Sprite sprite) {
        spriteList.remove(sprite);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void draw() {
        repaint();
    }

    public void update(long deltaTime) {
        for (Sprite sprite : spriteList) {
            sprite.update(deltaTime);
        }
        level.update(deltaTime);
    }
}
