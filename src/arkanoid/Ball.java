package arkanoid;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class Ball extends MovableObject {
    private Image image;

    public Ball(double x, double y, double radius) {
        super(x, y, radius * 2, radius * 2);
        // Gọi constructor lớp cha MovableObject
        // width = height = radius * 2 để vẽ hình tròn

        image = new Image("file:resource/image/ball_1.png");

        this.dx = 3;
        this.dy = -3;
    }

    @Override
    public void update() {
        // Di chuyển bóng
        move();

        // Giới hạn trong màn hình 600x400
        if (x <= 0 || x + width >= 600) {
            dx = -dx;
        }
        if (y <= 0 || y + height >= 400) {
            dy = -dy;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillOval(x, y, width, height);
        gc.drawImage(image, x, y, width, height);
    }

}