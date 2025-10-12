package arkanoid;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;

/**
 * lớp GameBoard quản lý và hiển thị các đối tượng trò chơi
 * kế thừa từ Pane để tạo một vùng chứa đồ họa
 * sử dụng Canvas để vẽ các đối tượng trò chơi như paddle, ball và bricks
 * cung cấp phương thức initLevel để khởi tạo các đối tượng trò chơi
 * cung cấp phương thức renderAll để vẽ tất cả các đối tượng trên bảng trò chơi
 */

public class GameBoard extends Pane {
    private Canvas canvas;
    private GraphicsContext gc;
    private static Paddle paddle;
    private Ball ball;
    private List<Brick> bricks = new ArrayList<>();
    private Image background;
    private AnimationTimer gameLoop;
    private Paddle cavans;

    public GameBoard(int width, int height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        background = new Image("file:resource/image/background.png");
        initLevel();

        // di chuyển chuột để điều khiển paddle
        canvas.setOnMouseMoved(event -> {
            double mouseX = event.getX();
            paddle.setX(mouseX - paddle.getWidth() / 2, (int) canvas.getWidth());
            renderAll();
        });
        startGameLoop();
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                renderAll();
            }
        };
        gameLoop.start();
    }

    private void update() {
        ball.update();
        // Kiểm tra va chạm giữa ball và paddle
        if (    ball.getY() + ball.getHeight() >= paddle.getY() &&
                ball.getX() + ball.getWidth() >= paddle.getX() &&
                ball.getX() <= paddle.getX() + paddle.getWidth() &&
                ball.getDy() > 0) { // Chỉ va chạm khi ball đang đi XUỐNG

            ball.setDy(-ball.getDy()); // Đổi hướng bóng khi va chạm với paddle
            ball.setY(paddle.getY() - ball.getHeight()); // Đặt bóng lên trên paddle
        }

        // va cham voi gach
        for (Brick brick : bricks) {
            if(!brick.isDestroyed() && checkCollision(ball, brick)){
                brick.hasCollided();

                // Tính độ chồng lấn theo từng phương
                double overlapLeft = (ball.getX() + ball.getWidth()) - brick.getX();
                double overlapRight = (brick.getX() + brick.getWidth()) - ball.getX();
                double overlapTop = (ball.getY() + ball.getHeight()) - brick.getY();
                double overlapBottom = (brick.getY() + brick.getHeight()) - ball.getY();

                // Tìm overlap nhỏ nhất
                double minOverlap = Math.min(
                        Math.min(overlapLeft, overlapRight),
                        Math.min(overlapTop, overlapBottom)
                );

                // Va chạm từ trên hoặc dưới → đổi dy
                if (minOverlap == overlapTop || minOverlap == overlapBottom) {
                    ball.setDy(-ball.getDy());
                }
                // Va chạm từ trái hoặc phải → đổi dx
                else {
                    ball.setDx(-ball.getDx());
                }

                break; // Chỉ xử lý va chạm với một viên gạch tại một thời điểm

            }
        }
    }

    //ktra va cham ball va brick
    private boolean checkCollision(Ball ball, Brick brick) {
        return ball.getX() < brick.getX() + brick.getWidth() && // kiểm tra tọa độ trái của ball với tọa độ phải của brick
                ball.getX() + ball.getWidth() > brick.getX() && // kiểm tra tọa độ phải của ball với tọa độ trái của brick
                ball.getY() < brick.getY() + brick.getHeight() && // kiểm tra tọa độ trên của ball với tọa độ dưới của brick
                ball.getY() + ball.getHeight() > brick.getY(); // kiểm tra tọa độ dưới của ball với tọa độ trên của brick
    }

    public void initLevel() {
        paddle = new Paddle(250, 340, 100, 20);
        ball = new Ball(295, 350, 10) {
            @Override
            public void update() {
                move();
                if (x <= 0 || x + width >= canvas.getWidth()) {
                    dx = -dx; // Đổi hướng khi chạm tường trái hoặc phải
                }
                if (y <= 0) {
                    dy = -dy; // Đổi hướng khi chạm tường trên
                }
                if (y + height >= canvas.getHeight()) {
                    // Bóng rơi xuống dưới cùng, có thể xử lý mất mạng hoặc kết thúc trò chơi ở đây
                    //dy = -dy; // Tạm thời đổi hướng lên để bóng không biến mất
                    y = canvas.getHeight() - height; // Đặt bóng lại trên cùng
                }

                checkPaddleCollision(GameBoard.paddle); // hoặc biến paddle bạn đang dùng

                if (y > 400) {
                    // rơi xuống đáy
                    dy = -dy;
                }
            }


        };
        bricks.clear();

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 10; j++) {
                bricks.add(new Brick(50 + j * 50, 40 + i * 30, 50, 30));
            }
        }
    }

    public void renderAll() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(background, 0, 0, canvas.getWidth(), canvas.getHeight());
        paddle.render(gc);
        ball.render(gc);
        for (Brick brick : bricks) {
            //Ball brick = null
            brick.render(gc);
        }
    }
}

