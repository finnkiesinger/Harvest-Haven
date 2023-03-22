package lighting;

import general.Vector2;
import graphics.MainWindow;

public class LightingThread extends Thread {
    @Override
    public void run() {
        while (!isInterrupted()) {
            Vector2 size = MainWindow.instance.getLevelSize();
            MainWindow.instance.setLightMap(Lighting.instance.calculateLightMap(size.x, size.y));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
