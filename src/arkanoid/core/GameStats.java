package arkanoid.core;

import arkanoid.entity.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Class quản lý điểm số và mạng trong game
 */
public class GameStats {
    private int score;
    private int lives;
    private int highScore;

    // Số mạng ban đầu
    private static final int INITIAL_LIVES = 3;

    public GameStats() {
        reset();
    }

    public void reset() {
        score = 0;
        lives = INITIAL_LIVES;

        // Cập nhật high score
        if (score > highScore) {
            highScore = score;
        }
    }


    // Thêm điểm khi phá gạch
    public void addScore(Brick brick) {
        int points = 10;
        if (brick != null) {
            points = brick.getType().getScore();
        }

        score += points;

        if (score > highScore) {
            highScore = score;
        }
    }

    // Mất một mạng
    public void loseLife() {
        lives--;
    }

    // Kiểm tra còn mạng không
    public boolean hasLivesLeft() {
        return lives > 0;
    }

    // Thêm mạng (bonus)
    public void addLife() {
        lives++;
    }

    // Getters


    public int getLives() {
        return lives;
    }
    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;

        // Cập nhật high score nếu cần
        if (this.score > highScore) {
            highScore = this.score;
        }
    }

    public int getHighScore() {
        return highScore;
    }
    public int setHighScore(int highScore) {
        return this.highScore = highScore;
    }

    // Vẽ UI điểm số và mạng
    public void render(GraphicsContext gc, double canvasWidth, double canvasHeight) {
        // Font chữ
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Vẽ Score (góc trái trên)
        gc.setFill(Color.WHITE);
        gc.fillText("SCORE: " + score, 10, 25);

        // Vẽ High Score (giữa trên)
        gc.setFill(Color.GOLD);
        gc.fillText("HIGH: " + highScore, canvasWidth / 2 - 60, 25);

        // Vẽ Lives (góc phải trên)
        gc.setFill(Color.RED);
        gc.fillText("LIVES: ", canvasWidth - 150, 25);

        // Vẽ icon trái tim cho mạng
        for (int i = 0; i < lives; i++) {
            gc.setFill(Color.RED);
            gc.fillText("❤", canvasWidth - 70 + i * 25, 25);
        }
    }
}