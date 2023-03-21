package game;

import exceptions.MapLoadException;
import general.Vector2;
import graphics.Camera;
import graphics.Level;
import graphics.MainWindow;
import graphics.Player;

import java.awt.event.KeyEvent;

public class Game {
    private final MainWindow window;

    public Game() {
        window = new MainWindow();
        Assets.instance.loadAssets();
        window.setVisible(true);
        startGameLoop();

        window.setVisible(false);
        System.exit(0);
    }

    private void startGameLoop() {
        boolean quit = false;
        Camera.initialize(100, 100);
        Level level = null;

        try {
            level = new Level("PlayerBase");
        } catch (MapLoadException e) {
            System.out.println("Map could not be loaded. Exiting...");
            quit = true;
        }
        assert level != null;

        Player player = new Player(level.getPlayerSpawn());
        player.setLevel(level);
        level.addActor(player);
        window.setLevel(level);
        window.update(0);

        long lastTime = System.nanoTime();

        while (!quit) {
            if (Input.instance.isKeyHeld(KeyEvent.VK_ESCAPE)) {
                quit = true;
                continue;
            }

            boolean isMovingHorizontal = false;
            boolean isMovingVertical = false;
            boolean registeredKey = false;
            if (Input.instance.isKeyHeld(KeyEvent.VK_A)) {
                player.moveHorizontal(-1);
                isMovingHorizontal = true;
                player.playAnimation("WalkLeft");
                registeredKey = true;
            }
            if (Input.instance.isKeyHeld(KeyEvent.VK_D)) {
                player.moveHorizontal(1);
                isMovingHorizontal = true;
                if (!registeredKey) {
                    player.playAnimation("WalkRight");
                    registeredKey = true;
                }
            }
            if (Input.instance.isKeyHeld(KeyEvent.VK_W)) {
                player.moveVertical(-1);
                isMovingVertical = true;
                if (!registeredKey) {
                    player.playAnimation("WalkUp");
                    registeredKey = true;
                }
            }
            if (Input.instance.isKeyHeld(KeyEvent.VK_S)) {
                player.moveVertical(1);
                isMovingVertical = true;
                if (!registeredKey) {
                    player.playAnimation("WalkDown");
                }
            }
            player.stopMoving(!isMovingHorizontal, !isMovingVertical);

            long currentTime = System.nanoTime();
            long deltaTime = currentTime - lastTime;
            update(deltaTime);
            lastTime = currentTime;

            Camera.main.setPosition(player.getPosition());

            window.draw();
            Input.instance.beginNewFrame();
            try {
                Thread.sleep(8 - Math.min(8, deltaTime / 1000000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update(long deltaTime) {
        window.update(deltaTime);
    }
}
