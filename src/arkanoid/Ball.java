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
        this.dx = 2;
        this.dy = -2;
    }
    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }

    public double getY() {
        return y;
    }

    public double getHeight() {
        return height;
    }

    public double getX() {
        return x;
    }

    public double getWidth() {
        return width;
    }
    public double setDy(double newDy) {
        this.dy = newDy;
        return dy;
    }
    public double setY(double newY) {
        this.y = newY;
        return y;
    }
    public double getDy() {
        return dy;
    }

    public abstract double getY(double v);

    public double setDx(double v){
        return dx;
    }
    public double getDx(){
        return dx;
    }
}