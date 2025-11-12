package arkanoid.entity;

import arkanoid.core.GameBoard;
import arkanoid.core.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Powerup extends GameObject {
    public boolean remove = false;
    private Image image;
    public int powerup;
    protected final double canvasHeight;

    public Powerup(int x, int y, int type, double canvasHeight) {
        super(x, y, 25, 25);
        this.powerup = type;
        this.canvasHeight = canvasHeight;

        if (powerup == 1) { //MultiBall
            image = new Image("file:resource/image/Buff.png");
        } else if (powerup == 2) { //GrowPaddle
            image = new Image("file:resource/image/Buff.png");
        } else if (powerup == 3) { //FireBall
            image = new Image("file:resource/image/Buff.png");
        } else if (powerup == 4) { // -1 máu
            image = new Image("file:resource/image/Debuff.png");
        } else if (powerup == 5) { // +1 máu
            image = new Image("file:resource/image/Buff.png");
        } else if (powerup == 6) { // Đóng băng paddle
            image = new Image("file:resource/image/Debuff.png");
        } else if (powerup == 7) { // X2 điểm
            image = new Image("file:resource/image/Buff.png");
        }


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
        switch (this.powerup) {
            case 1: // Multi-ball
                gameBoard.createExtraBalls();
                break;
            case 2: // Growth - paddle
                gameBoard.getPaddle().grow(15);
                break;
            case 3: // Fireball
                gameBoard.activateFireball();
                break;
            case 4: // -1 máu
                gameBoard.loseLife();
                break;
            case 5: // +1 máu
                gameBoard.gainLife();
                break;
            case 6: // Đóng băng paddle
                gameBoard.freezePaddle();
                break;
            case 7: // X2 điểm
                gameBoard.activateDoubleScore();
                break;
        }
    }
}
