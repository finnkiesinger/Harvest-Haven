package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class Input implements KeyListener {
    public static Input instance = new Input();

    private Input() {}

    private final Map<Integer, Boolean> pressedKeys = new HashMap<>();
    private final Map<Integer, Boolean> heldKeys = new HashMap<>();
    private final Map<Integer, Boolean> releasedKeys = new HashMap<>();

    public boolean wasKeyPressed(int keyCode) {
        return pressedKeys.getOrDefault(keyCode, false);
    }

    public boolean wasKeyReleased(int keyCode) {
        return releasedKeys.getOrDefault(keyCode, false);
    }

    public boolean isKeyHeld(int keyCode) {
        return heldKeys.getOrDefault(keyCode, false);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.put(e.getKeyCode(), true);
        heldKeys.put(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        releasedKeys.put(e.getKeyCode(), true);
        heldKeys.put(e.getKeyCode(), false);
    }

    public void beginNewFrame() {
        pressedKeys.clear();
        releasedKeys.clear();
    }
}
