package arkanoid.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.image.Image;

public class StatusGame {

    public enum GameState { MENU, PLAYING, PAUSED, GAME_OVER, LEVEL_COMPLETE, WIN }

    private Image menuBackground = new Image("file:resource/image/menu_background.png");
    private Image pauseBackground = new Image("file:resource/image/pause_background.png");
    private Image gameOverBackground = new Image("file:resource/image/gameover_background.png");
    private Image btnPlay = new Image("file:resource/image/play.png");
    private Image btnContinue = new Image("file:resource/image/continue.png");
    private Image btnReplay = new Image("file:resource/image/replay.png");
    private Image btnExit = new Image("file:resource/image/exit.png");

    private Image btnLoad = new Image("file:resource/image/load.png");

    private Image WinGame  = new Image("file:resource/image/win_game.png");
    private Image completeBackground = new Image("file:resource/image/complete.png");
    private Image btnLevelContinue = new Image("file:resource/image/continue.png");
    private Image btnLevelExit = new Image("file:resource/image/exit.png");

    private final double contLevelX = 220, contLevelY = 230, contLevelW = 140, contLevelH = 50;
    private final double exitLevelX = 220, exitLevelY = 300, exitLevelW = 140, exitLevelH = 50;

    private final double loadX = 220, loadY = 255, loadW = 140, loadH = 50;

    private GameState state = GameState.MENU;

    private final double playX = 220, playY = 220, playW = 140, playH = 50;
    private final double exitX  = 220, exitY  = 290, exitW  = 140, exitH  = 50;

    private final double contX = 220, contY = 200, contW = 140, contH = 50;
    private final double replayX = 220, replayY = 270, replayW = 140, replayH = 50;

    private final double replayOverX = 220, replayOverY = 230, replayOverW = 140, replayOverH = 50;
    private final double exitOverX = 220, exitOverY = 300, exitOverW = 140, exitOverH = 50;

    private final double replayWinX = 220, replayWinY = 230, replayWinW = 140, replayWinH = 50;
    private final double exitWinX = 220, exitWinY = 300, exitWinW = 140, exitWinH = 50;

    public void toLevelComplete() { state = GameState.LEVEL_COMPLETE; }
    public boolean isLevelComplete() { return state == GameState.LEVEL_COMPLETE; }

    public boolean isInsidePlay(double x, double y) {
        return x >= playX && x <= playX + playW && y >= playY && y <= playY + playH;
    }
    public boolean isInsideExit(double x, double y) {
        return x >= exitX && x <= exitX + exitW && y >= exitY && y <= exitY + exitH;
    }
    public boolean isInsideContinue(double x, double y) {
        return x >= contX && x <= contX + contW && y >= contY && y <= contY + contH;
    }
    public boolean isInsideReplay(double x, double y) {
        return x >= replayX && x <= replayX + replayW && y >= replayY && y <= replayY + replayH;
    }
    public boolean isInsideReplayOver(double x, double y) {
        return x >= replayOverX && x <= replayOverX + replayOverW && y >= replayOverY && y <= replayOverY + replayOverH;
    }

    public boolean isInsideExitOver(double x, double y) {
        return x >= exitOverX && x <= exitOverX + exitOverW && y >= exitOverY && y <= exitOverY + exitOverH;
    }

    public boolean isInsideExitComplete(double x, double y) {
        return x >= exitLevelX && x <= exitLevelX + exitLevelW && y >= exitLevelY && y <= exitLevelY + exitLevelH;
    }

    public boolean isInsideContinueComplete(double x, double y) {
        return x >= contLevelX && x <= contLevelX + contLevelW && y >= contLevelY && y <= contLevelY + contLevelH;
    }

    public boolean isInsideReplayWin(double x, double y) {
        return x >= replayWinX && x <= replayWinX + replayWinW && y >= replayWinY && y <= replayWinY + replayWinH;
    }

    public boolean isInsideExitWin(double x, double y) {
        return x >= exitWinX && x <= exitWinX + exitWinW && y >= exitWinY && y <= exitWinY + exitWinH;
    }

    public boolean isInsideLoad(double x, double y) {
        return x >= loadX && x <= loadX + loadW && y >= loadY && y <= loadY + loadH;
    }

    public boolean isMenu()     { return state == GameState.MENU; }
    public boolean isPlaying()  { return state == GameState.PLAYING; }
    public boolean isPaused()   { return state == GameState.PAUSED; }
    public boolean isGameOver() { return state == GameState.GAME_OVER; }
    public boolean isWin()      { return state == GameState.WIN; }

    public boolean isInsideContinueLevel(double x, double y) {
        return x >= contLevelX && x <= contLevelX + contLevelW
                && y >= contLevelY && y <= contLevelY + contLevelH;
    }
    public boolean isInsideExitLevel(double x, double y) {
        return x >= exitLevelX && x <= exitLevelX + exitLevelW
                && y >= exitLevelY && y <= exitLevelY + exitLevelH;
    }

    public void toPlaying()  { state = GameState.PLAYING; }
    public void toPaused()   { state = GameState.PAUSED; }
    public void toGameOver() { state = GameState.GAME_OVER; }
    public void toWin()      { state = GameState.WIN; }

    public void renderOverlay(GraphicsContext gc, double w, double h) {
        if (isPlaying()) return;

        // Nền gradient tối
        Stop[] stops = new Stop[] {
                new Stop(0, Color.rgb(10, 20, 30)),
                new Stop(1, Color.rgb(5, 10, 15))
        };
        gc.setFill(new LinearGradient(0, 0, 0, h, false, CycleMethod.NO_CYCLE, stops));
        gc.fillRect(0, 0, w, h);

        if (isMenu()) {
            gc.drawImage(menuBackground, 0, 0, w, h);
            gc.drawImage(btnPlay, playX, playY, playW, playH);

            gc.drawImage(btnLoad, loadX, loadY, loadW, loadH);

            gc.drawImage(btnExit, exitX, exitY, exitW, exitH);
        }
        else if (isPaused()) {
            gc.drawImage(pauseBackground, 0, 0, w, h);
            gc.drawImage(btnContinue, contX, contY, contW, contH);
            gc.drawImage(btnReplay, replayX, replayY, replayW, replayH);
        }
        else if (isGameOver()) {
            gc.drawImage(gameOverBackground, 0,0, w, h);
            gc.drawImage(btnReplay, replayOverX, replayOverY, replayOverW, replayOverH);
            gc.drawImage(btnExit, exitOverX, exitOverY, exitOverW, exitOverH);
        }
        else if (isWin()) {
            gc.drawImage(WinGame, 0,0, w, h);
            gc.drawImage(btnReplay, replayWinX, replayWinY, replayWinW, replayWinH);
            gc.drawImage(btnExit, exitWinX, exitWinY, exitWinW, exitWinH);
        }
        else if (isLevelComplete()) {
            gc.drawImage(completeBackground, 0, 0, w, h);
            gc.drawImage(btnLevelContinue, contLevelX, contLevelY, contLevelW, contLevelH);
            gc.drawImage(btnLevelExit, exitLevelX, exitLevelY, exitLevelW, exitLevelH);
        }
    }
}
