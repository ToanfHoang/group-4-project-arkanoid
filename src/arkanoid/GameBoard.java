package arkanoid;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
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
    private final StatusGame status = new StatusGame();

    public GameBoard(int width, int height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        background = new Image("file:resource/image/background.png");
        initLevel();
        startGameLoop();


        canvas.setOnMouseMoved(e -> {
            double mouseX = e.getX();
            paddle.setX(mouseX - paddle.getWidth() / 2, canvas.getWidth());
            // Nếu bóng đang dính paddle → di chuyển theo paddle
            if (ball.isAttached()) {
                ball.attachToPaddle(paddle);
            }
        });

        canvas.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (status.isMenu()) {
                    status.toPlaying();
                    return;
                }
                if (status.isGameOver()) {
                    initLevel();
                    status.toPlaying();
                    return;
                }
                if (status.isPaused()) {
                    status.toPlaying();
                    return;
                }
                // Bắn bóng khi đang chơi
                if (status.isPlaying()) {
                    ball.releaseFromPaddle();
                }
            }
        });


        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER -> { if (status.isMenu()) status.toPlaying(); }
                case P -> {
                    if (status.isPlaying()) {
                        status.toPaused();
                    } else if (status.isPaused()) {
                        status.toPlaying();
                    }
                }
            }
        });
    }

    public void endGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
            gameOver = true;
            status.toGameOver();
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
        // Tạm dừng hoặc menu hoặc game over → dừng hoàn toàn
        if (!status.isPlaying()) return;
        if (gameOver) return;

        ball.update();
        if (ball.isFellOut()) {
            endGameLoop();
            return;
        }

        ball.checkPaddleCollision(paddle);

        // Xử lý va chạm với gạch
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && checkCollision(ball, brick)) {
                brick.hitPoints--;
                if (brick.hitPoints <= 0) {
                    brick.hasCollided();

                    // Giữ tốc độ ổn định
                    double speed = sqrt(ball.dx * ball.dx + ball.dy * ball.dy);
                    if (speed > 4.0) {
                        double angle = Math.atan2(ball.dy, ball.dx);
                        ball.dx = 4.0 * Math.cos(angle);
                        ball.dy = 4.0 * Math.sin(angle);
                    }
                }

                handleBrickCollision(ball, brick);
                break;
            }
        }
    }


    private void handleBrickCollision(Ball ball, Brick brick) {
        double overlapLeft = (ball.getX() + ball.getWidth()) - brick.getX();
        double overlapRight = (brick.getX() + brick.getWidth()) - ball.getX();
        double overlapTop = (ball.getY() + ball.getHeight()) - brick.getY();
        double overlapBottom = (brick.getY() + brick.getHeight()) - ball.getY();

        double minOverlap = Math.min(Math.min(overlapLeft, overlapRight),
                Math.min(overlapTop, overlapBottom));

        if (minOverlap == overlapTop || minOverlap == overlapBottom) {
            ball.setDy(-ball.getDy());
        } else {
            ball.setDx(-ball.getDx());
        }
    }


    private boolean checkCollision(Ball ball, Brick brick) {
        return ball.getX() < brick.getX() + brick.getWidth() &&
                ball.getX() + ball.getWidth() > brick.getX() &&
                ball.getY() < brick.getY() + brick.getHeight() &&
                ball.getY() + ball.getHeight() > brick.getY();
    }

    public void initLevel() {
        gameOver = false;
        paddle = new Paddle(250, 340, 100, 20);
        ball = new Ball(295, 350, 10, canvas.getWidth(), canvas.getHeight());
        ball.attachToPaddle(paddle);

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

        // Vẽ gạch, paddle, bóng
        for (Brick brick : bricks) brick.render(gc);
        paddle.render(gc);
        ball.render(gc);

        // Hiển thị overlay MENU / PAUSE / GAME OVER
        status.renderOverlay(gc, canvas.getWidth(), canvas.getHeight());
    }
}
