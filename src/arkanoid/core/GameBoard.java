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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final List<Brick> unbreakableBricks = new ArrayList<>();
    private final StatusGame status = new StatusGame();
    private final GameStats gameStats = new GameStats();
    private final List<Powerup> powerups = new ArrayList<>();
    private final boolean levelComplete = false;
    private final List<ExplosionEffect> explosions = new ArrayList<>();

    private AnimationTimer gameLoop;
    private boolean gameOver;
    private final Image background;

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
        initNewGame();
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
        });

        canvas.setOnMouseClicked(e -> {
            double mx = e.getX(), my = e.getY();

            if (status.isMenu()) {
                if (status.isInsidePlay(mx, my)) {
                    SaveManager.deleteSave();
                    initNewGame();            // khởi tạo game mới
                    status.toPlaying();     // chuyển sang trạng thái chơi
                    startGameLoop();        // bắt đầu game loop
                } else if (status.isInsideExit(mx, my)) {
                    System.exit(0);
                } else if (status.isInsideLoad(mx, my)) {  //  THÊM PHẦN NÀY
                    // Load game từ file save
                    SaveGame saveData = SaveManager.loadGame();

                    if (saveData != null) {
                        // Có save file -> restore game state
                        restoreGameState(saveData);
                        status.toPlaying();
                        startGameLoop();
                        playSE(6); // Sound effect
                    } else {
                        // Không có save file -> hiển thị thông báo
                        System.out.println("⚠️ No save data available!");
                        playSE(3); // Fail sound
                    }
                }
                return;
            }

            if (status.isPaused()) {
                if (status.isInsideContinue(mx, my)) {
                    playMusic(4);
                    status.toPlaying();     // tiếp tục chơi

                    if (gameLoop != null) {
                        gameLoop.stop();
                    }

                    startGameLoop();
                } else if (status.isInsideReplay(mx, my)) {
                    initNewGame();            // khởi tạo lại level hoàn chỉnh
                    status.toPlaying();     // quay lại trạng thái chơi
                    startGameLoop();        // khởi động lại vòng lặp
                }
                return;
            }

            if (status.isGameOver()) {
                if (status.isInsideReplayOver(mx, my)) {
                    initNewGame();           // tạo lại brick, ball, paddle
                    status.toPlaying();    // quay lại chơi
                } else if (status.isInsideExitOver(mx, my)) {
                    System.exit(0);        // thoát game
                }
                return;
            }

            if (status.isWin()) {
                if (status.isInsideReplayWin(mx, my)) {
                    playMusic(4);
                    initNewGame();           // tạo lại brick, ball, paddle
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

                    autoSaveGame(); // tự động lưu game

                    initLevel();
                    resetBallAndPaddle();
                    status.toPlaying();
                    startGameLoop();
                } else if (status.isInsideExitLevel(mx, my)) {
                    System.exit(0);
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

        Powerup.updateAllPowerups(powerups, paddle, this);

        explosions.removeIf(ExplosionEffect::isExpired);

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

                if (gameStats.getScore() > gameStats.getHighScore()) {
                    gameStats.setHighScore(gameStats.getScore());
                }

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
            if (!brick.isDestroyed() && ball.checkBrickCollision(brick)) {
                if (ball.isOnFire()) {
                    brick.handleFireballCollision();

                    // Nếu gạch bị phá sau khi giảm hitpoint
                    if (brick.isDestroyed()) {
                        gameStats.addScore(brick);
                    }
                    if (brick.getHitpoint() > 0) {
                        ball.handleBrickCollision(brick);
                    }
                    playSE(2);
                } else {
                    brick.hasCollided();

                    if (brick.isDestroyed()) {
                        gameStats.addScore(brick);
                    }
                    playSE(2);
                    ball.handleBrickCollision(brick);
                }

                // Kiểm tra và tạo powerup
                Powerup newPowerup = brick.createPowerupIfNeeded(canvas.getHeight());
                if (newPowerup != null) {
                    powerups.add(newPowerup);
                }
                //xử lý vụ nổ
                if (brick.isExploding() && brick.getType() == Brick.BrickType.EXPLOSIVE) {
                    explosions.add(new ExplosionEffect(brick)); // Thêm vào list để vẽ
                    brick.handleExplosion(bricks, gameStats);
                }
                break;
            }
        }

        for (Brick brick : unbreakableBricks) {
            if (ball.checkBrickCollision(brick)) {
                ball.handleBrickCollision(brick); // Chỉ đổi hướng, không phá gạch
                playSE(2); // Vẫn có âm thanh va chạm
                break;
            }
        }

        if (Brick.areAllBricksDestroyed(bricks)) {
            playSE(5);
            bgm.stop();

            int currentLevel = levelManager.getCurrentLevel();
            // Nếu hoàn tất map 3 => WIN, còn không thì Level Complete
            if (currentLevel >= 3) {
                // cập nhật high score nếu cần
                if (gameStats.getScore() > gameStats.getHighScore()) {
                    gameStats.setHighScore(gameStats.getScore());
                }
                status.toWin();
                if (gameLoop != null) gameLoop.stop();
            } else {
                status.toLevelComplete();
                if (gameLoop != null) gameLoop.stop();
            }
        }

    }

    public void createExtraBalls() {
        int ballToCreate = 1;
        boolean isFireActive = !balls.isEmpty() && balls.get(0).isOnFire();
        for (int i = 0; i < ballToCreate; i++) {
            Ball newBall = Ball.createExtraBall(paddle, canvas.getWidth(), canvas.getHeight(),
                    isFireActive);
            balls.add(newBall);
        }
        System.out.println("Multi-ball activated!");
    }

    public void activateFireball() {
        for (Ball ball : balls) {
            ball.fireBall(6);
        }
        System.out.println("Fireball activated!");
    }

    public void loseLife() {
        gameStats.loseLife();

        if (!gameStats.hasLivesLeft()) {
            if (gameStats.getScore() > gameStats.getHighScore()) {
                gameStats.setHighScore(gameStats.getScore());
            }
            status.toGameOver();
            stopMusic();
            gameOver = true;
        }
        System.out.println("lose1Life activated!");
    }

    public void gainLife() {
        gameStats.gainLife();
        System.out.println("Gain1Life activated!");
    }

    public void freezePaddle() {
        paddle.freeze(5000); // Đóng băng 5 giây
        System.out.println("ZA WARUDO HANAMERO TOKI WO TOMERUNDAAAAAA WRYYYYYYYYYYYYYYYYYYYY");
    }

    public void activateDoubleScore() {
        gameStats.activateDoubleScore(10000); // X2 điểm trong 10 giây
        System.out.println("DoubleScore activated!");
    }

    private void restoreGameState(SaveGame data) {
        gameOver = false;

        // Restore score, lives, highScore
        gameStats.setScore(data.getScore());
        gameStats.setLives(data.getLives());
        gameStats.setHighScore(data.getHighScore());

        // Restore level
        levelManager.setCurrentLevel(data.getCurrentLevel());

        // Load level đó
        initLevelContent();

        System.out.println("✅ Game restored: Level " + data.getCurrentLevel() +
                ", Score: " + data.getScore() + ", Lives: " + data.getLives());
    }

    private void autoSaveGame() {
        SaveGame saveData = new SaveGame(
                gameStats.getScore(),
                gameStats.getLives(),
                levelManager.getCurrentLevel(),
                gameStats.getHighScore()
        );

        SaveManager.saveGame(saveData);
    }

    // Thêm class helper để track explosion timing
    private static class ExplosionEffect {
        Brick brick;
        long startTime;

        ExplosionEffect(Brick brick) {
            this.brick = brick;
            this.startTime = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - startTime > 300; // Hiển thị 300ms
        }
    }



    private void initNewGame() {
        gameOver = false;
        gameStats.reset(); // Reset cả score
        levelManager = new LevelManager(); // Reset về level 1
        initLevelContent();
    }


    public void initLevel() {
        gameOver = false;
        initLevelContent(); // Tạo màn chơi
    }


    private void initLevelContent() {
        paddle = new Paddle(250, 340, 100, 20);

        Ball mainBall = new Ball(295, 350, 10, canvas.getWidth(), canvas.getHeight());
        mainBall.attachToPaddle(paddle);

        balls.clear();
        bricks.clear();
        unbreakableBricks.clear();
        powerups.clear();
        explosions.clear();

        MapLoader.MapData mapData = levelManager.loadCurrentLevel();
        bricks.addAll(mapData.breakableBricks);
        unbreakableBricks.addAll(mapData.unbreakableBricks);
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
        for (Brick brick : unbreakableBricks) brick.render(gc);

        // Vẽ hiệu ứng nổ
        for (ExplosionEffect explosion : explosions) {
            explosion.brick.render(gc);
        }

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

        if (status.isWin()) {
            gc.save();
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 28));
            String hsText = "High Score: " + gameStats.getHighScore();

            // đo width để căn giữa (dùng Text helper)
            javafx.scene.text.Text helper = new javafx.scene.text.Text(hsText);
            helper.setFont(gc.getFont());
            double textWidth = helper.getLayoutBounds().getWidth();

            double centerX = canvas.getWidth() / 2.0;
            double textY = 180; // bạn có thể điều chỉnh để phù hợp với background Win
            gc.fillText(hsText, centerX - textWidth / 2.0, textY);
            gc.restore();
        }

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
            boolean hoverLoad = status.isInsideLoad(mouseX, mouseY);
            boolean hoverExit = status.isInsideExit(mouseX, mouseY);

            handleHoverSound("menu_play", hoverPlay);
            handleHoverSound("menu_load", hoverLoad);
            handleHoverSound("menu_exit", hoverExit);

            if (hoverPlay) drawGlow(220, 200, 140, 50);
            if (hoverLoad) drawGlow(220, 265, 140, 50);
            if (hoverExit) drawGlow(220, 330, 140, 50);
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

        if (status.isLevelComplete()) {
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

    public Paddle getPaddle() {
        return paddle;
    }
}
