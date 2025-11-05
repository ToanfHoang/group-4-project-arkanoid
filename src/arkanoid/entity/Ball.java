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
    private final double normalSpeed = 1.0; // Tốc độ bình thường

    private boolean attached; // Bóng ban đầu gắn vào paddle
    private boolean fellOut = false;

    private final Sound sound = new Sound();
    public boolean onFire = false;
    private final Image fireballImage;
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
        checkWallCollision();
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
            playSE(1);

            // Đặt bóng lên trên paddle
            y = paddle.getY() - height;

            // Tính góc phản xạ dựa trên vị trí chạm paddle
            double paddleCenter = paddle.getX() + paddle.getWidth() / 2.0;
            double ballCenter = x + width / 2.0;
            double distanceFromCenter = ballCenter - paddleCenter;

            // Chuẩn hóa giá trị từ -1 đến +1
            double ratio = Math.max(-1,
                    Math.min(1, distanceFromCenter / (paddle.getWidth() / 2.0)));

            // Tính góc phản xạ
            double maxAngle = Math.toRadians(65);
            double angle = ratio * maxAngle;

            // Đặt lại vận tốc với góc mới
            dx = currentSpeed * Math.sin(angle);
            dy = -currentSpeed * Math.cos(angle);
        }
    }

    public void stopSound() {
        sound.stop();
    }

    public void reset() {
        fellOut = false;
        attached = true;
        currentSpeed = normalSpeed;
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


    public void playSE(int i) {
        sound.setFile(i);
        sound.play();
    }

    public void fireBall(int seconds) {
        if (!onFire) {
            onFire = true;
            this.image = fireballImage; // Đổi hình ảnh
            // Tốc độ khi fireball
            currentSpeed = 2.0;
            updateVelocity(); // Cập nhật dx, dy
            timer.schedule(new RemindTask(), seconds * 1000L);
        }
    }

    class RemindTask extends TimerTask {
        public void run() {
            onFire = false;
            image = new Image("file:resource/image/ball_1.png"); // Đổi lại hình ảnh bình thường

            currentSpeed = normalSpeed;
            updateVelocity();
        }
    }

    // cập nhật vận tốc dx, dy dựa trên currentSpeed
    private void updateVelocity() {
        double magnitude = Math.sqrt(dx * dx + dy * dy);
        if (magnitude > 0) {
            // Giữ nguyên hướng, chỉ thay đổi tốc độ
            dx = (dx / magnitude) * currentSpeed;
            dy = (dy / magnitude) * currentSpeed;
        }
    }

    public boolean isOnFire() {
        return onFire;
    }

    // Kiểm tra va chạm với gạch
    public boolean checkBrickCollision(Brick brick) {
        return x < brick.getX() + brick.getWidth() &&
                x + width > brick.getX() &&
                y < brick.getY() + brick.getHeight() &&
                y + height > brick.getY();
    }

    // Xử lý va chạm với gạch
    public void handleBrickCollision(Brick brick) {
        double overlapLeft = (x + width) - brick.getX();
        double overlapRight = (brick.getX() + brick.getWidth()) - x;
        double overlapTop = (y + height) - brick.getY();
        double overlapBottom = (brick.getY() + brick.getHeight()) - y;

        double minOverlap = Math.min(Math.min(overlapLeft, overlapRight),
                Math.min(overlapTop, overlapBottom));

        if (minOverlap == overlapTop || minOverlap == overlapBottom)
            dy = -dy;
        else
            dx = -dx;
    }

    public void checkWallCollision() {
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
        }
    }

    public static Ball createExtraBall(Paddle paddle, double canvasWidth, double canvasHeight,
                                       boolean fireActive) {
        Ball newBall = new Ball(
                paddle.getX() + paddle.getWidth() / 2 - 10,
                paddle.getY() - 20,
                10, canvasWidth, canvasHeight
        );

        newBall.releaseFromPaddle();

        double angle = Math.toRadians(-60 + Math.random() * 120);
        newBall.setDx(newBall.getSpeed() * Math.sin(angle));
        newBall.setDy(-Math.abs(newBall.getSpeed() * Math.cos(angle)));

        if (fireActive) {
            newBall.fireBall(6);
        }

        return newBall;
    }
}