package arkanoid;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Ball extends MovableObject {
    private Image image;
    private double canvasWidth;
    private double canvasHeight;

    public Ball(double x, double y, double radius, double canvasWidth, double canvasHeight) {
        super(x, y, radius * 2, radius * 2);
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.image = new Image("file:resource/image/ball_1.png");
        this.dx = 2;
        this.dy = -2;
    }

    @Override
    public void update() {
        move();

        // Xử lý va chạm với tường trái/phải
        if (x <= 0 || x + width >= canvasWidth) {
            dx = -dx;
        }

        // Xử lý va chạm với tường trên
        if (y <= 0) {
            dy = -dy;
        }

        // Xử lý khi bóng rơi xuống đáy
        if (y + height >= canvasHeight) {
            dy = -dy;
            y = canvasHeight - height;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }

    public void checkPaddleCollision(Paddle paddle) {
        // Kiểm tra va chạm với paddle
        if (y + height >= paddle.getY() &&
                y + height <= paddle.getY() + paddle.getHeight() &&
                x + width >= paddle.getX() &&
                x <= paddle.getX() + paddle.getWidth() &&
                dy > 0) { // Chỉ khi bóng đang đi xuống

            // Đặt bóng lên trên paddle
            y = paddle.getY() - height;

            // Tính góc phản xạ dựa trên vị trí chạm paddle
            double paddleCenter = paddle.getX() + paddle.getWidth() / 2.0;
            double ballCenter = x + width / 2.0;
            double distanceFromCenter = ballCenter - paddleCenter;

            // Chuẩn hóa giá trị từ -1 đến +1
            double ratio = Math.max(-1, Math.min(1, distanceFromCenter / (paddle.getWidth() / 2.0)));

            // Tính góc phản xạ
            double maxAngle = Math.toRadians(75);
            double angle = ratio * maxAngle;

            // Đặt lại vận tốc với góc mới
            double speed = Math.sqrt(dx * dx + dy * dy);
            dx = speed * Math.sin(angle);
            dy = -speed * Math.cos(angle);
        }
    }
}