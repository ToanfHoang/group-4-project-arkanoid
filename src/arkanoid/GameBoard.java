package arkanoid;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.animation.AnimationTimer;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;

public class GameBoard extends Pane {
    private Canvas canvas;
    private GraphicsContext gc;
    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks = new ArrayList<>();
    private Image background;
    private AnimationTimer gameLoop;
    private boolean gameOver;

    public GameBoard(int width, int height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        background = new Image("file:resource/image/background.png");
        initLevel();

        // Di chuyển chuột để điều khiển paddle
        canvas.setOnMouseMoved(event -> {
            double mouseX = event.getX();
            paddle.setX(mouseX - paddle.getWidth() / 2, canvas.getWidth());
            renderAll();
        });

        startGameLoop();
    }

    public void endGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
            gameOver = true;
        }
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
        if(gameOver) {
            return;
        }
        ball.update();
        if (ball.isFellOut()) {
            endGameLoop();   // gameLoop.stop(); gameOver = true; ...
            return;
        }
        ball.checkPaddleCollision(paddle);

        // Xử lý va chạm với gạch
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && checkCollision(ball, brick)) {
                brick.hitPoints--;
                if (brick.hitPoints <= 0){
                    brick.hasCollided();
                    ball.dx *= 1.05; // Tăng tốc độ bóng sau mỗi lần phá gạch
                    ball.dy *= 1.05;

                    if ( sqrt(ball.dx*ball.dx + ball.dy*ball.dy ) > 4.0) {

                        double angle = Math.atan2(ball.dy, ball.dx);
                        ball.dx = 4.0 * Math.cos(angle);
                        ball.dy = 4.0 * Math.sin(angle);
                    }
                    
                    System.out.println(ball.dx + " " + ball.dy + " " + sqrt(ball.dx*ball.dx + ball.dy*ball.dy));
                }

                handleBrickCollision(ball, brick);
                break; // Chỉ xử lý va chạm với một viên gạch tại một thời điểm
            }
        }



    }

    // xác định hướng phản xạ của bóng khi va chạm với gạch
    private void handleBrickCollision(Ball ball, Brick brick) {
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
        } else {
            // Va chạm từ trái hoặc phải → đổi dx
            ball.setDx(-ball.getDx());
        }
    }

    // kiểm tra va chạm giữa bóng và gạch
    private boolean checkCollision(Ball ball, Brick brick) {
        return ball.getX() < brick.getX() + brick.getWidth() &&
                ball.getX() + ball.getWidth() > brick.getX() &&
                ball.getY() < brick.getY() + brick.getHeight() &&
                ball.getY() + ball.getHeight() > brick.getY();
    }

    // khởi tạo vị trí ban đầu của paddle, bóng và gạch
    public void initLevel() {
        paddle = new Paddle(250, 340, 100, 20);
        ball = new Ball(295, 350, 10, canvas.getWidth(), canvas.getHeight());
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
            brick.render(gc);
        }
    }
}