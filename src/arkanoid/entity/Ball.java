package arkanoid.entity;

import arkanoid.core.MovableObject;
import arkanoid.sound.Sound;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.TimerTask;

public class Ball extends MovableObject {
    private Image image;
    private final double canvasWidth;
    private final double canvasHeight;

    private double currentSpeed = 0.5;

    private boolean attached = true; // Bóng ban đầu gắn vào paddle
    private boolean fellOut = false;

    private final Sound sound = new Sound();
    public boolean onFire = false;
    private int fireSec = 0;
    private Image fireballImage;
    private java.util.Timer timer = new java.util.Timer();

    public Ball(double x, double y, double radius, double canvasWidth, double canvasHeight) {
        super(x, y, radius * 2, radius * 2);
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.image = new Image("file:resource/image/ball_1.png");
        this.fireballImage = new Image("file:resource/image/FireBall.png");

        this.dx = currentSpeed * Math.sin(Math.toRadians(45));
        this.dy = -currentSpeed * Math.cos(Math.toRadians(45));

        //bong mới
        this.attached = false;
        this.currentSpeed = 1.0;

        // Hướng ngẫu nhiên
        double angle = Math.toRadians(-60 + Math.random() * 120);
        this.dx = currentSpeed * Math.sin(angle);
        this.dy = -Math.abs(currentSpeed * Math.cos(angle));
    }

    @Override
    public void update() {
        if (attached || fellOut) {
            return; // Nếu bóng đang gắn vào paddle, không di chuyển
        }
        move();

        // Xử lý va chạm với tường trái/phải
        if (x <= 0 || x + width >= canvasWidth) {
            dx = -dx;
            playSE(0);
        }

        // Xử lý va chạm với tường trên
        if (y <= 0) {
            dy = -dy;
            playSE(0);
        }

        // Xử lý khi bóng rơi xuống đáy
        if (y + height >= canvasHeight) {
            fellOut = true;
            // Đánh dấu bóng đã rơi ra ngoài
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }

    public boolean isFellOut() {
        return fellOut;
    }

    public boolean checkPaddleCollision(Paddle paddle) {
        // Kiểm tra va chạm với paddle
        if (y + height >= paddle.getY() &&
                y + height <= paddle.getY() + paddle.getHeight() &&
                x + width >= paddle.getX() &&
                x <= paddle.getX() + paddle.getWidth() &&
                dy > 0) { // Chỉ khi bóng đang đi xuống
            playSE(1);

            // Đặt bóng lên trên paddle
            y = paddle.getY() - height;

            // Tính góc phản xạ dựa trên vị trí chạm paddle
            double paddleCenter = paddle.getX() + paddle.getWidth() / 2.0;
            double ballCenter = x + width / 2.0;
            double distanceFromCenter = ballCenter - paddleCenter;

            // Chuẩn hóa giá trị từ -1 đến +1
            double ratio = Math.max(-1, Math.min(1, distanceFromCenter / (paddle.getWidth() / 2.0)));

            // Tính góc phản xạ
            double maxAngle = Math.toRadians(65);
            double angle = ratio * maxAngle;

            // Đặt lại vận tốc với góc mới
            dx = currentSpeed * Math.sin(angle);
            dy = -currentSpeed * Math.cos(angle);
        }
        return false;
    }

    public void stopSound() {
        if (sound != null) {
            sound.stop();
        }
    }
    public void reset() {
        fellOut = false;
        attached = true;
        currentSpeed = 1.0;
        onFire = false;
        stopSound();

        // Reset hình ảnh về bình thường
        this.image = new Image("file:resource/image/ball_1.png");

        // Reset timer nếu có
        if (timer != null) {
            timer.cancel();
            timer = new java.util.Timer();
        }
    }

    // Tăng tốc độ cơ bản - gọi khi phá gạch
    public void increaseSpeed() {
        currentSpeed *= 1.05;  // tăng speed

        // Giới hạn tốc độ tối đa
        if (currentSpeed > 2.0) {
            currentSpeed = 2.0;
        }

        // Cập nhật lại dx, dy theo hướng hiện tại
        double angle = Math.atan2(dy, dx);
        dx = currentSpeed * Math.cos(angle);
        dy = currentSpeed * Math.sin(angle);
    }

    public double getSpeed() {
        return currentSpeed;  // ← Trả về speed chuẩn
    }

    // kiểm tra bóng có đang gắn vào paddle không
    public boolean isAttached() {
        return attached;
    }

    // gắn bóng vào paddle
    public void attachToPaddle(Paddle paddle) {
        attached = true;
        // đặt bóng ngay trên paddle, chính giữa
        this.x = paddle.getX() + paddle.getWidth() / 2 - this.width / 2;
        this.y = paddle.getY() - this.height - 1;
        this.dx = 0;
        this.dy = 0;
    }

    // giải phóng bóng khỏi paddle
    public void releaseFromPaddle() {
        if (attached) {
            attached = false;
            // bắn lên trên một góc nhẹ

            double angle = Math.toRadians(-60 + Math.random() * 120); // góc ngẫu nhiên
            this.dx = currentSpeed * Math.sin(angle);
            this.dy = -Math.abs(currentSpeed * Math.cos(angle));
        }
    }

    // Reset speed về ban đầu khi bắt đầu level mới
    public void resetSpeed() {
        currentSpeed = 1.0;
    }

    public void playSE(int i) {
        sound.setFile(i);
        sound.play();
    }

    public void fireBall(int seconds){
        if(!onFire){
            fireSec = seconds;
            onFire = true;
            this.image = fireballImage; // Đổi hình ảnh
            timer.schedule(new RemindTask(), seconds * 1000);
        }
    }
    class RemindTask extends TimerTask {
        public void run() {
            onFire = false;
            image = new Image("file:resource/image/ball_1.png"); // Đổi lại hình ảnh bình thường
        }
    }

    public boolean isOnFire() {
        return onFire;
    }
}