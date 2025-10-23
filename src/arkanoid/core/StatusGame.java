package arkanoid.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.image.Image;

public class StatusGame {

    public enum GameState { MENU, PLAYING, PAUSED, GAME_OVER, WIN }

    private Image menuBackground = new Image("file:resource/image/menu_background.png");
    private Image pauseBackground = new Image("file:resource/image/pause_background.png");
    private Image gameOverBackground = new Image("file:resource/image/gameover_background.png");
    private Image btnPlay = new Image("file:resource/image/play.png");
    private Image btnContinue = new Image("file:resource/image/continue.png");
    private Image btnReplay = new Image("file:resource/image/replay.png");
    private Image btnExit = new Image("file:resource/image/exit.png");
    private Image WinGame  = new Image("file:resource/image/win_game.png");

    private GameState state = GameState.MENU;

    private long flashTime = 0;
    private String activeButton = "";
    private String hoverButton = "";

    private final double playX = 220, playY = 220, playW = 140, playH = 50;
    private final double exitX  = 220, exitY  = 290, exitW  = 140, exitH  = 50;

    private final double contX = 220, contY = 200, contW = 140, contH = 50;
    private final double replayX = 220, replayY = 270, replayW = 140, replayH = 50;

    private final double replayOverX = 220, replayOverY = 230, replayOverW = 140, replayOverH = 50;
    private final double exitOverX = 220, exitOverY = 300, exitOverW = 140, exitOverH = 50;

    private final double replayWinX = 220, replayWinY = 230, replayWinW = 140, replayWinH = 50;
    private final double exitWinX = 220, exitWinY = 300, exitWinW = 140, exitWinH = 50;

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

    public boolean isInsideReplayWin(double x, double y) {
        return x >= replayWinX && x <= replayWinX + replayWinW && y >= replayWinY && y <= replayWinY + replayWinH;
    }

    public boolean isInsideExitWin(double x, double y) {
        return x >= exitWinX && x <= exitWinX + exitWinW && y >= exitWinY && y <= exitWinY + exitWinH;
    }

    public boolean isMenu()     { return state == GameState.MENU; }
    public boolean isPlaying()  { return state == GameState.PLAYING; }
    public boolean isPaused()   { return state == GameState.PAUSED; }
    public boolean isGameOver() { return state == GameState.GAME_OVER; }
    public boolean isWin()      { return state == GameState.WIN; }

    public void toPlaying()  { state = GameState.PLAYING; }
    public void toPaused()   { state = GameState.PAUSED; }
    public void toGameOver() { state = GameState.GAME_OVER; }
    public void toWin()      { state = GameState.WIN; }

    public void triggerButton(String name) {
        activeButton = name;
        flashTime = System.currentTimeMillis();
    }

    public void setHover(String name) {
        hoverButton = name;
    }

    public void clearHover() {
        hoverButton = "";
    }

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
    }

    private void drawNeonText(GraphicsContext gc, String text, double centerX, double y, Color color, int size) {
        Font font = Font.font("Consolas", size);
        gc.setFont(font);
        double textWidth = getTextWidth(font, text);
        double x = centerX - textWidth / 2;

        gc.setFill(color);
        gc.setStroke(color.brighter());
        gc.setLineWidth(2);

        gc.setGlobalAlpha(0.25);
        gc.setFill(color.deriveColor(0, 1, 1, 0.2));
        gc.fillText(text, x + 3, y + 3);
        gc.setGlobalAlpha(1);

        gc.strokeText(text, x, y);
        gc.fillText(text, x, y);
    }

    private void drawHint(GraphicsContext gc, double w, double h, String text) {
        gc.setFont(Font.font("Consolas", 16));
        gc.setFill(Color.GRAY);
        double textW = getTextWidth(gc.getFont(), text);
        gc.fillText(text, (w - textW) / 2, h - 30);
    }

    private void drawCenter(GraphicsContext gc, double btnW, double x, double y, String text) {
        double textW = getTextWidth(gc.getFont(), text);
        gc.fillText(text, x + (btnW - textW) / 2, y);
    }

    private double getTextWidth(Font font, String s) {
        Text t = new Text(s);
        t.setFont(font);
        t.setBoundsType(TextBoundsType.VISUAL);
        return t.getLayoutBounds().getWidth();
    }
}
