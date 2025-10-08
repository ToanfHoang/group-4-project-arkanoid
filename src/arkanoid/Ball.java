package arkanoid;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball extends MovableObject {

    public Ball(double x, double y, double radius) {
        // Gọi constructor lớp cha MovableObject
        // width = height = radius * 2 để vẽ hình tròn
        super(x, y, radius * 2, radius * 2);

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
    }
}