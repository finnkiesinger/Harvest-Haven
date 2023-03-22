package graphics;

import game.Input;
import general.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainWindow extends JFrame {
    static GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];

    public static MainWindow instance;

    private final GameCanvas canvas = new GameCanvas();

    public MainWindow() {
        super("Harvest Haven");
        setUndecorated(true);
        if (instance == null) {
            instance = this;
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());


        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new BorderLayout());
        layeredPane.add(canvas, BorderLayout.CENTER, 0);

        add(layeredPane, BorderLayout.CENTER);

        addKeyListener(Input.instance);
        device.setFullScreenWindow(this);
    }

    public Vector2 getCenter() {
        return new Vector2(canvas.getWidth() / 2, canvas.getHeight() / 2);
    }

    public void addSprite(Sprite sprite) {
        canvas.addSprite(sprite);
    }

    public void setLightMap(BufferedImage lightMap) {
        canvas.setLightMap(lightMap);
    }

    public void setLevel(Level level) {
        canvas.setLevel(level);
    }

    public Vector2 getLevelSize() {
        return canvas.getLevelSize();
    }

    public void draw() {
        canvas.draw();
    }

    public void update(long deltaTime) {
        canvas.update(deltaTime);
    }
}
