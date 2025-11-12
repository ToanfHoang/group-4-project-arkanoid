package arkanoid.entity;

import arkanoid.core.GameBoard;
import arkanoid.core.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import arkanoid.entity.powerup.PowerupStrategy;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Powerup extends GameObject {
    public boolean remove = false;
    private Image image;
    private PowerupStrategy strategy;
    public int powerup;
    protected final double canvasHeight;

    public Powerup(int x, int y, PowerupStrategy strategy, double canvasHeight) {
        super(x, y, 25, 25);
        this.strategy = strategy;
        this.canvasHeight = canvasHeight;
        this.image = new Image(strategy.getImagePath());
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }

    @Override
    public void update() {
        if (y + height <= canvasHeight) {
            int speed = 2;
            y += speed;
            bounds = new Rectangle(x, y, width, height);
        } else {
            remove = true;
        }
    }

    public static void updateAllPowerups(List<Powerup> powerups, Paddle paddle,
                                         GameBoard gameBoard) {
        List<Powerup> toRemove = new ArrayList<>();

        for (Powerup powerup : powerups) {
            powerup.update();

            // Kiểm tra va chạm với paddle
            if (paddle.checkPowerupCollision(powerup)) {
                powerup.applyEffect(gameBoard);
                toRemove.add(powerup);
                continue;
            }

            // Kiểm tra nếu powerup cần xóa
            if (powerup.remove || powerup.getY() > powerup.canvasHeight) {
                toRemove.add(powerup);
            }
        }

        // Xóa các powerup đã thu thập hoặc ra khỏi màn hình
        powerups.removeAll(toRemove);
    }

    public void applyEffect(GameBoard gameBoard) {
        strategy.applyEffect(gameBoard);
    }
}
