package arkanoid;

import javafx.animation.Animation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Background;
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
    private Paddle paddle;
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

        // Thiết lập sự kiện di chuyển chuột để điều khiển paddle
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
    }

    public void initLevel() {
        paddle = new Paddle (250, 370, 100, 20);
        ball = new Ball (295, 350, 10, 520,700 );
        bricks.clear();

        for (int i = 0; i < 7; i++){
            for (int j = 0; j < 10; j++){
                bricks.add(new Brick(50 + j * 50, 40 + i * 30, 50, 30));
            }
        }
    }



    public void renderAll() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc .drawImage(background, 0, 0, canvas.getWidth(), canvas.getHeight());
        paddle.render(gc);
        ball.render(gc);
        for (Brick brick : bricks){
            //Ball brick = null
            brick.render(gc);
        }
    }
}
