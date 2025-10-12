package arkanoid;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public abstract class Ball extends MovableObject {
    private Image image;
    // constructor
    public Ball(double x, double y, double radius) {
        super(x, y, radius * 2, radius * 2);
        // Gọi constructor lớp cha MovableObject
        // width = height = radius * 2 để vẽ hình tròn
        image = new Image("file:resource/image/ball_1.png");
        // Khởi tạo vận tốc ban đầu
        this.dx = 0.5;
        this.dy = -0.5;
    }

    @Override
    public void update() {
        move();
        if (x <= 0 || x + width >= 600) {
            dx = -dx; // Đổi hướng khi chạm tường trái hoặc phải
        }
        if (y <= 0 || y + height <= 400) {
            dy = -dy; // Đổi hướng khi chạm tường trên
        }
    }


    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    // lay van toc moi
    public double setDy(double newDy) {
        this.dy = newDy;
        return dy;
    }
    public double setDx(double newDx) {
        this.dx = newDx;
        return dx;
    }
    public double getDy() {
        return dy;
    }
    public double getDx() {
        return dx;
    }

    public double setY(double newY) {
        this.y = newY;
        return y;
    }
    public double setX(double newX) {
        this.x = newX;
        return x;
    }



}