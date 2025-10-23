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

    // Điểm cho từng loại gạch
    private static final int POINTS_NORMAL = 10;
    private static final int POINTS_STRONG = 20;
    private static final int POINTS_EXPLOSIVE = 30;
    private static final int POINTS_BONUS = 10;

    // Số mạng ban đầu
    private static final int INITIAL_LIVES = 3;

    public GameStats() {
        reset();
    }

    public void reset() {
        score = 0;
        lives = INITIAL_LIVES;
    }

    // Thêm điểm khi phá gạch
    public void addScore(Brick brick) {
        int points = 0;

        if (brick instanceof UnbreakableBrick) {
            points = 0;
        } else if (brick instanceof DropItemBrick) {
            points = POINTS_BONUS;
        } else if (brick instanceof ExplosiveBrick) {
            points = POINTS_EXPLOSIVE;
        } else if (brick instanceof StrongBrick) {
            points = POINTS_STRONG;
        } else {
            points = POINTS_NORMAL;
        }

        score += points;

        // Cập nhật high score
        if (score > highScore) {
            highScore = score;
        }
    }


    public void loseLife() {
        lives--;
    }


    public boolean hasLivesLeft() {
        return lives > 0;
    }


    public void addLife() {
        lives++;
    }

    // Getters
    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public int getHighScore() {
        return highScore;
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