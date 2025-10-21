package arkanoid.core;

import arkanoid.entity.Ball;
import arkanoid.entity.Brick;
import arkanoid.entity.Paddle;
import arkanoid.entity.Powerup;
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
    private Ball mainBall;
    private final List<Ball> balls = new ArrayList<>(); //danh sach bong
    private final List<Brick> bricks = new ArrayList<>();
    private final StatusGame status = new StatusGame();
    private final List<Powerup> powerups = new ArrayList<>();

    private AnimationTimer gameLoop;
    private boolean gameOver;
    private Image background;
    private void resetBallAndPaddle() {
        // Đưa paddle về giữa màn hình
        paddle.setX((canvas.getWidth() - paddle.getWidth()) / 2, canvas.getWidth());

        // Đặt bóng lên trên paddle
        mainBall.attachToPaddle(paddle); // bóng dính paddle
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
            if (mainBall.isAttached()) mainBall.attachToPaddle(paddle);
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
                if (mainBall.isAttached()) {
                    mainBall.releaseFromPaddle();
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
        if (!status.isPlaying()) return;
        if (gameOver) return;

        if (!mainBall.isFellOut()) {
            mainBall.update();
            mainBall.checkPaddleCollision(paddle);
            checkBrickCollisions(mainBall);
        }
        updatePowerup();
        List<Ball> ballsToRemove = new ArrayList<>();
        for (Ball ball : balls) {
            ball.update();

            // Kiểm tra nếu bóng phụ rơi ra ngoài
            if (ball.isFellOut()) {
                ballsToRemove.add(ball);
            } else {
                ball.checkPaddleCollision(paddle);
                checkBrickCollisions(ball);
            }
        }
        balls.removeAll(ballsToRemove);

        if (mainBall.isFellOut() && balls.isEmpty()) {
            status.toGameOver();
            stopMusic();
            gameOver = true;
            return;
        }

        mainBall.checkPaddleCollision(paddle);
        checkBrickCollisions(mainBall);
    }

    private void checkBrickCollisions(Ball ball) {
        if (ball.isFellOut()) {
            return;
        }
        // Va chạm gạch
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && checkCollision(ball, brick)) {
                if (ball.isOnFire()) {
                    brick.destroyed();
                    ball.increaseSpeed();
                    playSE(2);
                } else {
                    brick.hasCollided();
                    ball.increaseSpeed();
                    playSE(2);
                    handleBrickCollision(ball, brick);
                }

                // Kiểm tra và tạo powerup
                if (brick.isDestroyed() && brick.hasPowerup() > 0) {
                    Powerup p = new Powerup((int) (brick.getX() + brick.getWidth() / 2),
                            (int) (brick.getY() + brick.getHeight() / 2),
                            brick.hasPowerup(), canvas.getHeight());
                    powerups.add(p);
                }
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

    private void updatePowerup() {
        List<Powerup> toRemove = new ArrayList<>();

        for (Powerup powerup : powerups) {
            powerup.update();

            // va cham voi paddle
            if (checkPowerupPaddleCollision(powerup, paddle)) {
                applyPowerupEffect(powerup.powerup);
                toRemove.add(powerup);
                continue;
            }
            if (powerup.remove || powerup.getY() > canvas.getHeight()) {
                toRemove.add(powerup);
            }
        }
        powerups.removeAll(toRemove);
    }

    private boolean checkPowerupPaddleCollision(Powerup powerup, Paddle paddle) {
        return powerup.getX() < paddle.getX() + paddle.getWidth() &&
                powerup.getX() + powerup.getWidth() > paddle.getX() &&
                powerup.getY() < paddle.getY() + paddle.getHeight() &&
                powerup.getY() + powerup.getHeight() > paddle.getY();
    }

    private void applyPowerupEffect(int powerupType) {
        switch (powerupType) {
            case 1: // Multi-ball
                createExtraBalls();
                break;
            case 2: // Growth - paddle
                paddle.setWidth(paddle.getWidth() + 15);
                break;
            case 3: // Fireball
                activateFireball();
                break;
        }
    }

    private void createExtraBalls() {
        int ballToCreate = 2;
        for (int i = 0; i < ballToCreate; i++) {
            Ball newBall = new Ball(
                    paddle.getX() + paddle.getWidth() / 2 - 10,
                    paddle.getY() - 20,
                    10, canvas.getWidth(), canvas.getHeight()
            );
            // Set bóng không attached ngay từ đầu
            newBall.releaseFromPaddle();

            // Set hướng ngẫu nhiên khác nhau cho mỗi bóng
            double angle = Math.toRadians(-60 + Math.random() * 120);
            newBall.setDx(newBall.getSpeed() * Math.sin(angle));
            newBall.setDy(-Math.abs(newBall.getSpeed() * Math.cos(angle)));
            balls.add(newBall);
        }
        System.out.println("Multi-ball activated!");
    }

    private void activateFireball() {
        mainBall.fireBall(4);
        System.out.println("Fireball activated!");
    }

    // Kiểm tra va chạm giữa bóng và gạch
    private boolean checkCollision(Ball ball, Brick brick) {
        return ball.getX() < brick.getX() + brick.getWidth() &&
                ball.getX() + ball.getWidth() > brick.getX() &&
                ball.getY() < brick.getY() + brick.getHeight() &&
                ball.getY() + ball.getHeight() > brick.getY();
    }

    // Xử lý va chạm giữa bóng và gạch
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

        mainBall = new Ball(295, 350, 10, canvas.getWidth(), canvas.getHeight());
        mainBall.attachToPaddle(paddle);

        balls.clear();
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

        // Vẽ powerups
        for (Powerup powerup : powerups) {
            powerup.render(gc);
        }

        // Vẽ paddle và bóng
        paddle.render(gc);
        if (!mainBall.isFellOut()) {
            mainBall.render(gc);
        }

        // Vẽ tất cả các bóng phụ
        for (Ball ball : balls) {
            ball.render(gc);
        }

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
