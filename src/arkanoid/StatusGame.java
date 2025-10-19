package arkanoid;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class StatusGame {

    public void triggerButtonEffect(String overContinue) {
    }

    public boolean isInsideOverContinue(double mx, double my) {
        return false;
    }

    public boolean isInsideOverMenu(double mx, double my) {
        return false;
    }

    public enum GameState { MENU, PLAYING, PAUSED, GAME_OVER }
    private GameState state = GameState.MENU;

    private long flashTime = 0;
    private String activeButton = "";
    private String hoverButton = "";

    private final double startX = 220, startY = 220, startW = 160, startH = 50;
    private final double exitX  = 220, exitY  = 290, exitW  = 160, exitH  = 50;

    private final double contX = 220, contY = 200, contW = 160, contH = 50;
    private final double menuX = 220, menuY = 270, menuW = 160, menuH = 50;

    public boolean isInsideStart(double x, double y) {
        return x >= startX && x <= startX + startW && y >= startY && y <= startY + startH;
    }
    public boolean isInsideExit(double x, double y) {
        return x >= exitX && x <= exitX + exitW && y >= exitY && y <= exitY + exitH;
    }
    public boolean isInsideContinue(double x, double y) {
        return x >= contX && x <= contX + contW && y >= contY && y <= contY + contH;
    }
    public boolean isInsideMenu(double x, double y) {
        return x >= menuX && x <= menuX + menuW && y >= menuY && y <= menuY + menuH;
    }

    public boolean isMenu()     { return state == GameState.MENU; }
    public boolean isPlaying()  { return state == GameState.PLAYING; }
    public boolean isPaused()   { return state == GameState.PAUSED; }
    public boolean isGameOver() { return state == GameState.GAME_OVER; }

    public void toMenu()     { state = GameState.MENU; }
    public void toPlaying()  { state = GameState.PLAYING; }
    public void toPaused()   { state = GameState.PAUSED; }
    public void toGameOver() { state = GameState.GAME_OVER; }

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

        // Tuỳ trạng thái
        if (isMenu()) {
            drawNeonText(gc, "ARKANOID", w / 2, 140, Color.CYAN, 72);
            drawButton(gc, "START", startX, startY, startW, startH);
            drawButton(gc, "EXIT", exitX, exitY, exitW, exitH);
            drawHint(gc, w, h, "CLICK TO START");
        }
        else if (isPaused()) {
            drawNeonText(gc, "PAUSED", w / 2, 140, Color.CYAN, 60);
            drawButton(gc, "CONTINUE", contX, contY, contW, contH);
            drawButton(gc, "MENU", menuX, menuY, menuW, menuH);
            drawHint(gc, w, h, "PRESS P TO RESUME");
        }
        else if (isGameOver()) {
            drawNeonText(gc, "GAME OVER", w / 2, 140, Color.ORANGE, 60);
            drawButton(gc, "RETRY", contX, contY, contW, contH);
            drawButton(gc, "MENU", menuX, menuY, menuW, menuH);
            drawHint(gc, w, h, "PRESS R TO RESTART");
        }

        // Hiệu ứng sáng click
        if (System.currentTimeMillis() - flashTime < 150) {
            gc.setGlobalAlpha(0.5);
            gc.setFill(Color.AQUA);
            switch (activeButton) {
                case "START" -> gc.fillRoundRect(startX, startY, startW, startH, 10, 10);
                case "EXIT" -> gc.fillRoundRect(exitX, exitY, exitW, exitH, 10, 10);
                case "CONTINUE", "RETRY" -> gc.fillRoundRect(contX, contY, contW, contH, 10, 10);
                case "MENU" -> gc.fillRoundRect(menuX, menuY, menuW, menuH, 10, 10);
            }
            gc.setGlobalAlpha(1.0);
        }
    }

    private void drawButton(GraphicsContext gc, String label, double x, double y, double w, double h) {
        boolean isHover = label.equals(hoverButton);

        Color neon = Color.CYAN.interpolate(Color.WHITE, isHover ? 0.4 : 0.0);
        gc.setLineWidth(3);
        gc.setStroke(neon);
        gc.setFill(Color.rgb(0, 255, 255, isHover ? 0.15 : 0.05));

        gc.fillRoundRect(x, y, w, h, 12, 12);
        gc.strokeRoundRect(x, y, w, h, 12, 12);

        // Viền sáng mờ
        gc.setGlobalAlpha(0.5);
        gc.setStroke(Color.rgb(0, 255, 255, 0.3));
        gc.strokeRoundRect(x - 2, y - 2, w + 4, h + 4, 16, 16);
        gc.setGlobalAlpha(1);

        // Text
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", 20));
        drawCenter(gc, w, x, y + h / 1.6, label);
    }

    private void drawNeonText(GraphicsContext gc, String text, double centerX, double y, Color color, int size) {
        Font font = Font.font("Consolas", size);
        gc.setFont(font);
        gc.setFill(color);
        gc.setStroke(color.brighter());
        gc.setLineWidth(2);
        double textWidth = getTextWidth(font, text);
        double x = centerX - textWidth / 2;

        // Phát sáng nhẹ
        gc.setGlobalAlpha(0.3);
        gc.setFill(color.deriveColor(0, 1, 1, 0.2));
        gc.fillText(text, x + 2, y + 2);
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
