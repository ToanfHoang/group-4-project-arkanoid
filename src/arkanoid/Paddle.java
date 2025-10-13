package arkanoid;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Paddle extends MovableObject {
    private Image image;

    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.image = new Image("file:resource/image/paddle.png");
    }

    @Override
    public void update() {
        // Paddle không tự động di chuyển, chỉ di chuyển theo chuột
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }

    public void setX(double newX, double boardWidth) {
        if (newX < 0) {
            this.x = 0;
        } else if (newX + width > boardWidth) {
            this.x = boardWidth - width;
        } else {
            this.x = newX;
        }
    }
}