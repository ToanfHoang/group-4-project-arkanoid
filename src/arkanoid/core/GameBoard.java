package arkanoid.core;

import arkanoid.entity.Ball;
import arkanoid.entity.Brick;
import arkanoid.entity.Paddle;
import arkanoid.sound.Sound;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.sqrt;

/**
 * Lớp GameBoard quản lý toàn bộ trò chơi Arkanoid:
 * - Điều khiển Paddle, Ball, Brick
 * - Xử lý trạng thái MENU / PLAYING / PAUSED / GAME OVER
 * - Render toàn bộ lên Canvas
 */
public class GameBoard extends Pane {
    private final Canvas canvas;
    private final GraphicsContext gc;

    private Paddle paddle;
    private Ball ball;
    private final List<Brick> bricks = new ArrayList<>();
    private final StatusGame status = new StatusGame();

    private AnimationTimer gameLoop;
    private boolean gameOver;
    private Image background;
    private void resetBallAndPaddle() {
        // Đưa paddle về giữa màn hình
        paddle.setX((canvas.getWidth() - paddle.getWidth()) / 2, canvas.getWidth());

        // Đặt bóng lên trên paddle
        ball.attachToPaddle(paddle); // bóng dính paddle
    }

    public GameBoard(int width, int height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
        background = new Image("file:resource/image/background.png");
        initLevel();
        playMusic(4);
        startGameLoop();

        canvas.setOnMouseMoved(e -> {
            if (!status.isPlaying()) return; // Chỉ di chuyển khi đang chơi
            double mouseX = e.getX();
            paddle.setX(mouseX - paddle.getWidth() / 2, canvas.getWidth());
            if (ball.isAttached()) ball.attachToPaddle(paddle);
        });

        canvas.setOnMouseClicked(e -> {
            double mx = e.getX(), my = e.getY();

            if (status.isMenu()) {
                if (status.isInsidePlay(mx, my)) {
                    initLevel();            // khởi tạo lại màn chơi
                    status.toPlaying();     // chuyển sang trạng thái chơi
                    startGameLoop();        // bắt đầu game loop
                }
                else if (status.isInsideExit(mx, my)) {
                    System.exit(0);
                }
                return;
            }

            if (status.isPaused()) {
                if (status.isInsideContinue(mx, my)) {
                    status.toPlaying();     // tiếp tục chơi
                    startGameLoop();
                }
                else if (status.isInsideReplay(mx, my)) {
                    initLevel();            // khởi tạo lại level hoàn chỉnh
                    status.toPlaying();     // quay lại trạng thái chơi
                    startGameLoop();        // khởi động lại vòng lặp
                }
                return;
            }

            if (status.isGameOver()) {
                if (status.isInsideReplayOver(mx, my)) {
                    initLevel();           // tạo lại brick, ball, paddle
                    status.toPlaying();    // quay lại chơi
                }
                else if (status.isInsideExitOver(mx, my)) {
                    System.exit(0);        // thoát game
                }
                return;
            }

            if (status.isWin()) {
                if (status.isInsideReplayWin(mx, my)) {
                    initLevel();           // tạo lại brick, ball, paddle
                    status.toPlaying();    // quay lại chơi
                }
                else if (status.isInsideExitWin(mx, my)) {
                    System.exit(0);        // thoát game
                }
                return;
            }

            if (status.isPlaying() && e.getButton() == MouseButton.PRIMARY) {
                // chỉ bắn bóng khi nó đang gắn với paddle
                if (ball.isAttached()) {
                    ball.releaseFromPaddle();
                }
            }
        });


        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case P -> { // Pause/Resume
                    if (status.isPlaying()) {
                        status.toPaused();
                        stopMusic();
                    }
                    else if (status.isPaused()) {
                        status.toPlaying();
                        playMusic(4);
                    }
                }
            }
        });

        canvas.requestFocus();
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
        // Không update nếu không chơi
        if (!status.isPlaying()) return;
        if (gameOver) return;

        ball.update();

        if (ball.isFellOut()) {
            status.toGameOver();
            stopMusic();
            gameOver = true;
            return;
        }

        ball.checkPaddleCollision(paddle);

        // Va chạm gạch
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && checkCollision(ball, brick)) {
                brick.hitPoints--;
                playSE(2);
                if (brick.hitPoints <= 0) {
                    brick.hasCollided();
                    // Giữ vận tốc hợp lý
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
        // Nếu tất cả gạch đã bị phá -> thắng
        boolean allDestroyed = true;
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                allDestroyed = false;
                break;
            }
        }
        if (allDestroyed) {
            status.toWin();
            if (gameLoop != null) gameLoop.stop();
            return;
        }

    }

    private boolean checkCollision(Ball ball, Brick brick) {
        return ball.getX() < brick.getX() + brick.getWidth() &&
                ball.getX() + ball.getWidth() > brick.getX() &&
                ball.getY() < brick.getY() + brick.getHeight() &&
                ball.getY() + ball.getHeight() > brick.getY();
    }

    private void handleBrickCollision(Ball ball, Brick brick) {
        double overlapLeft = (ball.getX() + ball.getWidth()) - brick.getX();
        double overlapRight = (brick.getX() + brick.getWidth()) - ball.getX();
        double overlapTop = (ball.getY() + ball.getHeight()) - brick.getY();
        double overlapBottom = (brick.getY() + brick.getHeight()) - ball.getY();

        double minOverlap = Math.min(Math.min(overlapLeft, overlapRight),
                Math.min(overlapTop, overlapBottom));

        if (minOverlap == overlapTop || minOverlap == overlapBottom)
            ball.setDy(-ball.getDy());
        else
            ball.setDx(-ball.getDx());
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
        // Nếu đang chơi game -> vẽ ảnh background thật
        if (status.isPlaying()) {
            gc.drawImage(background, 0, 0, canvas.getWidth(), canvas.getHeight());
        } else {
            // Nền mờ xanh tím cho menu / pause / over
            gc.setFill(javafx.scene.paint.Color.rgb(10, 10, 25));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }

        // Vẽ gạch
        for (Brick brick : bricks) brick.render(gc);

        // Vẽ paddle và bóng
        paddle.render(gc);
        ball.render(gc);

        // Vẽ lớp overlay (menu, pause, game over)
        status.renderOverlay(gc, canvas.getWidth(), canvas.getHeight());
    }

    Sound sound = new Sound();
    Sound bgm = new Sound();

    public void playMusic(int i) { //chạy nhạc background
        bgm.setFile(i);
        bgm.play();
        bgm.loop();
    }

    public void stopMusic() {  //dừng nhạc
        bgm.stop();
    }

    public void playSE(int i) {  //chạy Sound Effects
        sound.setFile(i);
        sound.play();
    }
}
