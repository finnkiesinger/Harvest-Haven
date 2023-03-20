package graphics;

import game.Input;
import general.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainWindow extends JFrame {
    public static MainWindow instance;

    private final GameCanvas canvas;

    public MainWindow() {
        super("Harvest Haven");
        setSize(1280, 720);
        if (instance == null) {
            instance = this;
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        canvas = new GameCanvas();
        add(canvas, BorderLayout.CENTER);

        addKeyListener(Input.instance);
    }

    public Vector2 getCenter() {
        return new Vector2(canvas.getWidth() / 2, canvas.getHeight() / 2);
    }

    public void addSprite(Sprite sprite) {
        canvas.addSprite(sprite);
    }

    public void setLevel(Level level) {
        canvas.setLevel(level);
    }

    public void draw() {
        canvas.draw();
    }

    public void update(long deltaTime) {
        canvas.update(deltaTime);
    }
}
