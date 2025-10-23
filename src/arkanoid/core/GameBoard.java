package arkanoid.core;

import arkanoid.entity.*;
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

    private GameStats stats = new GameStats();

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
                    status.triggerButtonEffect("PLAY");
                    initLevel();            // khởi tạo lại màn chơi
                    status.toPlaying();     // chuyển sang trạng thái chơi
                    startGameLoop();        // bắt đầu game loop
                }
                else if (status.isInsideExit(mx, my)) {
                    status.triggerButtonEffect("EXIT");
                    System.exit(0);
                }
                return;
            }

            if (status.isPaused()) {
                if (status.isInsideContinue(mx, my)) {
                    status.triggerButtonEffect("CONTINUE");
                    status.toPlaying();     // tiếp tục chơi
                    startGameLoop();
                }
                else if (status.isInsideReplay(mx, my)) {
                    status.triggerButtonEffect("REPLAY");

                    initLevel();            // khởi tạo lại level hoàn chỉnh
                    status.toPlaying();     // quay lại trạng thái chơi
                    startGameLoop();        // khởi động lại vòng lặp
                }
                return;
            }

            if (status.isGameOver()) {
                if (status.isInsideOverContinue(mx, my)) {
                    status.triggerButtonEffect("OVER_CONTINUE");
                    initLevel();            // khởi tạo lại màn chơi mới
                    status.toPlaying();     // quay lại chơi
                    startGameLoop();
                }
                else if (status.isInsideOverMenu(mx, my)) {
                    status.triggerButtonEffect("OVER_MENU");
                    status.toMenu();        // quay lại menu
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
                case ENTER -> { // Bắt đầu / tiếp tục
                    if (status.isMenu()) {
                        status.triggerButtonEffect("PLAY");
                        status.toPlaying();
                    } else if (status.isPaused()) {
                        status.triggerButtonEffect("CONTINUE");
                        status.toPlaying();
                    } else if (status.isGameOver()) {
                        status.triggerButtonEffect("OVER_CONTINUE");
                        initLevel();
                        status.toPlaying();
                    }
                }
                case M, ESCAPE -> status.toMenu(); // Về menu
                case R -> { // Restart khi Game Over
                    playMusic(4);
                    if (status.isGameOver()) {
                        initLevel();
                        status.toPlaying();
                    }
                }
                default -> {
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

            stats.loseLife();
            if (!stats.hasLivesLeft()) {
                status.toGameOver();
                stopMusic();
                gameOver = true;
                return;
            }
            else {
                resetBallAndPaddle();
                ball.resetSpeed();
            }

        }

        ball.checkPaddleCollision(paddle);

        // Va chạm gạch
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && checkCollision(ball, brick)) {

                playSE(2);

                brick.hasCollided(ball);
                if( brick.isDestroyed()) {
                    stats.addScore(brick);
                }

                //kiem tra van toc
                System.out.println(ball.dx + ", " + ball.dy + " | Speed: " + ball.getSpeed());

                handleBrickCollision(ball, brick);
                break;
            }
        }
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
        stats.reset();
        paddle = new Paddle(250, 340, 100, 20);

        ball = new Ball(295, 350, 10, canvas.getWidth(), canvas.getHeight());
        ball.attachToPaddle(paddle);

        bricks.clear();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                double x = 50 + j * 50;
                double y = 40 + i * 30;

                switch (i) {
                    case 0:
                        bricks.add(new UnbreakableBrick(x, y, 50, 30)); // Normal
                        break;
                    case 1:
                        bricks.add(new StrongBrick(x, y, 50, 30));
                        break;
                    case 2:
                        bricks.add(new Brick(x, y, 50, 30));
                        break;
                    case 3:
                        bricks.add(new ExplosiveBrick(x, y, 50, 30, bricks));
                        break;
                    case 4:
                        bricks.add(new DropItemBrick(x, y, 50, 30));
                        break;
                }
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
        for (Brick StrongBrick : bricks) {
            if (StrongBrick instanceof StrongBrick) {
                StrongBrick.render(gc);
            }
        }
        for(Brick ExplosiveBrick : bricks) {
            if (ExplosiveBrick instanceof ExplosiveBrick) {
                ExplosiveBrick.render(gc);
            }
        }
        for(Brick DropItemBrick : bricks) {
            if (DropItemBrick instanceof DropItemBrick) {
                DropItemBrick.render(gc);
            }
        }
        for (Brick UnbreakableBrick : bricks) {
            if (UnbreakableBrick instanceof UnbreakableBrick) {
                UnbreakableBrick.render(gc);
            }
        }

        // Vẽ paddle và bóng
        paddle.render(gc);
        ball.render(gc);

        // Vẽ thông tin điểm số và mạng
        if (status.isPlaying()) {
            stats.render(gc, canvas.getWidth(), canvas.getHeight());
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
