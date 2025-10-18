package arkanoid;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class StatusGame {

    public enum GameState {MENU, PLAYING, PAUSED, GAME_OVER}

    private GameState state = GameState.MENU;

    private final Image menuImage = new Image("file:resource/image/gamemenu.png");
    private final Image pausedImage = new Image("file:resource/image/pause.png");
    private final Image gameOverImage = new Image("file:resource/image/gameover.png");

    public GameState getState() {
        return state;
    }

    public boolean isMenu() {
        return state == GameState.MENU;
    }

    public boolean isPlaying() {
        return state == GameState.PLAYING;
    }

    public boolean isPaused() {
        return state == GameState.PAUSED;
    }

    public boolean isGameOver() {
        return state == GameState.GAME_OVER;
    }

    public void toMenu() {
        state = GameState.MENU;
    }

    public void toPlaying() {
        state = GameState.PLAYING;
    }

    public void toPaused() {
        state = GameState.PAUSED;
    }

    public void toGameOver() {
        state = GameState.GAME_OVER;
    }

    public void renderOverlay(GraphicsContext gc, double w, double h) {
        if (isPlaying()) return;

        if (isMenu()) {
            gc.drawImage(menuImage, 0, 0, w, h);
        } else if (isPaused()) {
            gc.drawImage(pausedImage, 0, 0, w, h);
        } else if (isGameOver()) {
            gc.drawImage(gameOverImage, 0, 0, w, h);
        }
    }
}



