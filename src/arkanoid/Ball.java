package arkanoid;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class Ball extends MovableObject {
    private Image image;

    private double hitbox_ball_radius;

    // constructor
    public Ball(double x, double y, double radius) {
        super(x, y, radius * 2, radius * 2);
        // Gọi constructor lớp cha MovableObject
        // width = height = radius * 2 để vẽ hình tròn
        image = new Image("file:resource/image/ball_1.png");
        this.dx = 2;
        this.dy = -2;

    }

    @Override
    public void update() {
        move();
        if (x < 0 || x + width > 600) {
            dx = -dx; // đổi hướng khi chạm tường trái hoặc phải
        }
        if (y < 0 || y + height > 400 ) { // chạm tường trên
            dy = -dy; // đổi hướng khi chạm tường trên
        }
    }


    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillOval(x, y, width, height);
        gc.drawImage(image, x, y, width, height);
    }

}