package arkanoid;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Ball extends MovableObject {
    private final Image image;
    private final double canvasWidth;
    private final double canvasHeight;
    private double speed;
    private boolean attached = true; // Bóng ban đầu gắn vào paddle

    private boolean fellOut = false;
    public Ball(double x, double y, double radius, double canvasWidth, double canvasHeight) {
        super(x, y, radius * 2, radius * 2);
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.image = new Image("file:resource/image/ball_1.png");
        this.dx = 1;
        this.dy = -1;
    }


    @Override
    public void update() {
        if (attached) {
            return; // Nếu bóng đang gắn vào paddle, không di chuyển
        }
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
            fellOut = true; // Đánh dấu bóng đã rơi ra ngoài
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }
    public boolean isFellOut() {
        return fellOut;
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
            speed = Math.sqrt(dx * dx + dy * dy);
            dx = speed * Math.sin(angle);
            dy = -speed * Math.cos(angle);
        }
    }

    public double getSpeed() {
        return speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public boolean isAttached() { return attached; }

    public void attachToPaddle(Paddle paddle) {
        attached = true;
        // đặt bóng ngay trên paddle, chính giữa
        this.x = paddle.getX() + paddle.getWidth() / 2 - this.width / 2;
        this.y = paddle.getY() - this.height - 1;
        this.dx = 0;
        this.dy = 0;
    }

    public void releaseFromPaddle() {
        if (attached) {
            attached = false;
            // bắn lên trên một góc nhẹ
            double speed = 1;
            double angle = Math.toRadians(-60 + Math.random() * 120); // góc ngẫu nhiên
            this.dx = speed * Math.sin(angle);
            this.dy = -Math.abs(speed * Math.cos(angle));
        }
    }

}