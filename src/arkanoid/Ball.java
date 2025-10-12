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
    public void update() {
        move();
        if (x <=0 || x + width >= 600) {
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
    public void checkPaddleCollision(Paddle paddle) {
        // kiểm tra va chạm đơn giản
        if (y + height >= paddle.getY() && y + height <= paddle.getY() + paddle.getHeight()
                && x + width >= paddle.getX() && x <= paddle.getX() + paddle.getWidth()) {

            y = paddle.getY() - height - 1;

            // Tính khoảng cách từ tâm bóng đến tâm paddle
            double paddleCenter = paddle.getX() + paddle.getWidth() / 2.0;
            double ballCenter = x + width / 2.0;
            double distanceFromCenter = ballCenter - paddleCenter;

            // Chuẩn hóa giá trị từ -1 (bên trái) → +1 (bên phải)
            double ratio = distanceFromCenter / (paddle.getWidth() / 2.0);

            // Giới hạn tỉ lệ để không vượt biên
            ratio = Math.max(-1, Math.min(1, ratio));

            // Tính góc phản xạ tối đa (radians)
            double maxAngle = Math.toRadians(60); // có thể chỉnh góc tối đa ở mép paddle

            // Tính góc phản xạ thực tế
            double angle = ratio * maxAngle;

            // Đặt lại vận tốc
            double speed = Math.sqrt(dx * dx + dy * dy); // giữ nguyên tốc độ
            dx = speed * Math.sin(angle);
            dy = -speed * Math.cos(angle); // âm vì đi lên
        }
    }


    public abstract double getY(double v);
}