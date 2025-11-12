package arkanoid.entity;

import arkanoid.core.MovableObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Paddle extends MovableObject {
    private final Image image;
    private boolean frozen = false;
    private long freezeEndTime = 0;

    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.image = new Image("file:resource/image/paddle.png");
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }

    public void setX(double newX, double boardWidth) {
        if (!frozen) {
            if (newX < 0) {
                this.x = 0;
            } else if (newX + width > boardWidth) {
                this.x = boardWidth - width;
            } else {
                this.x = newX;
            }
        }

        if (frozen && System.currentTimeMillis() > freezeEndTime) {
            frozen = false;
        }
    }

    public boolean checkPowerupCollision(Powerup powerup) {
        return powerup.getX() < this.x + this.width &&
                powerup.getX() + powerup.getWidth() > this.x &&
                powerup.getY() < this.y + this.height &&
                powerup.getY() + powerup.getHeight() > this.y;
    }

    public void grow(int amount) {
        this.width += amount;
        // Có thể thêm giới hạn tối đa nếu cần
        if (this.width > 200) { // Ví dụ: giới hạn max 200
            this.width = 200;
        }
        System.out.println("GrowPaddle activated!");
    }

    public void freeze(long duration) {
        this.frozen = true;
        this.freezeEndTime = System.currentTimeMillis() + duration;
    }

    public boolean isFrozen() {
        return frozen;
    }
}