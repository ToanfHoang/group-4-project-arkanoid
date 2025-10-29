package arkanoid.core;

import arkanoid.entity.Ball;
import arkanoid.entity.Brick;
import arkanoid.entity.Paddle;
import arkanoid.entity.Powerup;
import arkanoid.sound.Sound;
import arkanoid.entity.*;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.sqrt;

/**
 * Lớp GameBoard quản lý toàn bộ trò chơi Arkanoid:
 * - Điều khiển Paddle, Ball, Brick
 * - Xử lý trạng thái MENU / PLAYING / PAUSED / GAME OVER
 * - Render toàn bộ lên Canvas
 */
public class GameBoard extends Pane {
    private final Map<String, Boolean> hoverState = new HashMap<>();
    private double mouseX = 0, mouseY = 0;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private LevelManager levelManager = new LevelManager();
    private Paddle paddle;
    private final List<Ball> balls = new ArrayList<>(); //danh sach bong
    private final List<Brick> bricks = new ArrayList<>();
    private final StatusGame status = new StatusGame();
    private final GameStats gameStats = new GameStats();
    private final List<Powerup> powerups = new ArrayList<>();
    private LevelCompleteOverlay levelOverlay;
    private boolean levelComplete = false;

    private AnimationTimer gameLoop;
    private boolean gameOver;
    private Image background;

    private void resetBallAndPaddle() {
        // Đưa paddle về giữa màn hình
        paddle.setX((canvas.getWidth() - paddle.getWidth()) / 2, canvas.getWidth());

        // Nếu không còn bóng nào -> tạo bóng mới
        if (balls.isEmpty()) {
            Ball newBall = new Ball(
                    paddle.getX() + paddle.getWidth() / 2 - 10,
                    paddle.getY() - 20,
                    10, canvas.getWidth(), canvas.getHeight()
            );
            balls.add(newBall);
        }

        // Reset tất cả bóng hiện có
        for (Ball ball : balls) {
            ball.reset();
        }

        // Đặt bóng đầu tiên dính vào paddle
        balls.get(0).attachToPaddle(paddle);
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
            this.mouseX = e.getX();
            this.mouseY = e.getY();

            if (!status.isPlaying()) return; // Chỉ di chuyển khi đang chơi
            double mouseX = e.getX();
            paddle.setX(mouseX - paddle.getWidth() / 2, canvas.getWidth());
            if (!balls.isEmpty() && balls.get(0).isAttached()) {
                balls.get(0).attachToPaddle(paddle);
            }
            ;
        });

        canvas.setOnMouseClicked(e -> {
            double mx = e.getX(), my = e.getY();

            if (status.isMenu()) {
                if (status.isInsidePlay(mx, my)) {
                    initLevel();            // khởi tạo lại màn chơi
                    status.toPlaying();     // chuyển sang trạng thái chơi
                    startGameLoop();        // bắt đầu game loop
                } else if (status.isInsideExit(mx, my)) {
                    System.exit(0);
                }
                return;
            }

            if (status.isPaused()) {
                if (status.isInsideContinue(mx, my)) {
                    playMusic(4);
                    status.toPlaying();     // tiếp tục chơi
                    startGameLoop();
                } else if (status.isInsideReplay(mx, my)) {
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
                } else if (status.isInsideExitOver(mx, my)) {
                    System.exit(0);        // thoát game
                }
                return;
            }

            if (status.isWin()) {
                if (status.isInsideReplayWin(mx, my)) {
                    playMusic(4);
                    initLevel();           // tạo lại brick, ball, paddle
                    status.toPlaying();    // quay lại chơi
                } else if (status.isInsideExitWin(mx, my)) {
                    System.exit(0);        // thoát game
                }
                return;
            }

            if (status.isPlaying() && e.getButton() == MouseButton.PRIMARY) {
                // chỉ bắn bóng khi nó đang gắn với paddle
                if (!balls.isEmpty() && balls.get(0).isAttached()) {
                    balls.get(0).releaseFromPaddle();
                }
            }
            if (status.isLevelComplete()) {
                if (status.isInsideContinueLevel(mx, my)) {
                    playMusic(4);
                    levelManager.nextLevel(); // load map tiếp theo
                    initLevel();
                    resetBallAndPaddle();
                    status.toPlaying();
                    startGameLoop();
                } else if (status.isInsideExitLevel(mx, my)) {
                    System.exit(0);
                }
                return;
            }
        });


        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case P -> { // Pause/Resume
                    if (status.isPlaying()) {
                        status.toPaused();
                        stopMusic();
                    } else if (status.isPaused()) {
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

        if (balls.isEmpty()) {
            gameStats.loseLife();

            if (!gameStats.hasLivesLeft()) {

                playSE(3);
                status.toGameOver();
                stopMusic();
                gameOver = true;

            } else {
                resetBallAndPaddle();
            }
        }
    }

    private void checkBrickCollisions(Ball ball) {
        if (ball.isFellOut()) {
            return;
        }
        // Va chạm gạch
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && checkCollision(ball, brick)) {
                if (ball.isOnFire()) {
                    // Fireball: giảm hitpoint đi 2
                    for (int i = 0; i < 2; i++) {
                        brick.hasCollided();
                    }

                    // Nếu gạch bị phá sau khi giảm hitpoint
                    if (brick.isDestroyed()) {
                        gameStats.addScore(brick);
                    }
                    if (brick.getHitpoint() > 0) {
                        handleBrickCollision(ball, brick);
                    }
                    playSE(2);
                } else {
                    brick.hasCollided();

                    if (brick.isDestroyed()) {
                        gameStats.addScore(brick);
                    }
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
                if (brick.isExploding()) {
                    for (Brick other : bricks) {
                        if (!other.isDestroyed() && brick.isInExplosionRange(other)) {
                            other.destroyed();
                            gameStats.addScore(other);
                        }
                    }
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
            playSE(5);
            bgm.stop();

            status.toLevelComplete();
            gameLoop.stop();
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
        int ballToCreate = 1;
        boolean isFireActive = !balls.isEmpty() && balls.get(0).isOnFire();
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
            if (isFireActive) {
                newBall.fireBall(6);
            }
            balls.add(newBall);
        }
        System.out.println("Multi-ball activated!");
    }

    private void activateFireball() {
        for (Ball ball : balls) {
            ball.fireBall(6);
        }
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
        gameStats.reset();
        paddle = new Paddle(250, 340, 100, 20);

        Ball mainBall = new Ball(295, 350, 10, canvas.getWidth(), canvas.getHeight());
        mainBall.attachToPaddle(paddle);

        balls.clear();
        bricks.clear();
        powerups.clear();

        bricks.addAll(levelManager.loadCurrentLevel());
        balls.add(mainBall);
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

        // Vẽ tất cả các bóng phụ
        for (Ball ball : balls) {
            ball.render(gc);
        }

        gameStats.render(gc, canvas.getWidth(), canvas.getHeight());

        // Vẽ lớp overlay (menu, pause, game over)
        status.renderOverlay(gc, canvas.getWidth(), canvas.getHeight());
        drawHoverEffect();
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

    private void drawHoverEffect() {
        // MENU
        if (status.isMenu()) {
            boolean hoverPlay = status.isInsidePlay(mouseX, mouseY);
            boolean hoverExit = status.isInsideExit(mouseX, mouseY);

            handleHoverSound("menu_play", hoverPlay);
            handleHoverSound("menu_exit", hoverExit);

            if (hoverPlay) drawGlow(220, 220, 140, 50);
            if (hoverExit) drawGlow(220, 290, 140, 50);
        }

        // PAUSED
        if (status.isPaused()) {
            boolean hoverContinue = status.isInsideContinue(mouseX, mouseY);
            boolean hoverReplay = status.isInsideReplay(mouseX, mouseY);

            handleHoverSound("paused_continue", hoverContinue);
            handleHoverSound("paused_replay", hoverReplay);

            if (hoverContinue) drawGlow(220, 200, 140, 50);
            if (hoverReplay) drawGlow(220, 270, 140, 50);
        }

        // GAME OVER
        if (status.isGameOver()) {
            boolean hoverReplay = status.isInsideReplayOver(mouseX, mouseY);
            boolean hoverExit = status.isInsideExitOver(mouseX, mouseY);

            handleHoverSound("gameover_replay", hoverReplay);
            handleHoverSound("gameover_exit", hoverExit);

            if (hoverReplay) drawGlow(220, 230, 140, 50);
            if (hoverExit) drawGlow(220, 300, 140, 50);
        }

        if (status.isLevelComplete()){
            boolean hoverContinue = status.isInsideContinueComplete(mouseX, mouseY);
            boolean hoverExit = status.isInsideExitComplete(mouseX, mouseY);

            handleHoverSound("complete_continue", hoverContinue);
            handleHoverSound("complete_exit", hoverExit);

            if (hoverContinue) drawGlow(220, 230, 140, 50);
            if (hoverExit) drawGlow(220, 300, 140, 50);
        }

        // WIN
        if (status.isWin()) {
            boolean hoverReplay = status.isInsideReplayWin(mouseX, mouseY);
            boolean hoverExit = status.isInsideExitWin(mouseX, mouseY);

            handleHoverSound("win_replay", hoverReplay);
            handleHoverSound("win_exit", hoverExit);

            if (hoverReplay) drawGlow(220, 230, 140, 50);
            if (hoverExit) drawGlow(220, 300, 140, 50);
        }
    }

    private void drawGlow(double x, double y, double w, double h) {
        gc.save();
        double pulse = 0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 200.0);
        gc.setGlobalAlpha(0.5 + 0.3 * pulse);
        gc.setStroke(Color.web("#FFD700")); // vàng sáng
        gc.setLineWidth(3 + 2 * pulse);
        gc.strokeRoundRect(x - 5, y - 5, w + 10, h + 10, 15, 15);
        gc.restore();
    }

    private void handleHoverSound(String key, boolean isHovering) {
        boolean wasHovering = hoverState.getOrDefault(key, false);
        if (isHovering && !wasHovering) {
            playSE(7);
        }
        hoverState.put(key, isHovering);
    }
}
