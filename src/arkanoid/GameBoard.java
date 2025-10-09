package arkanoid;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

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

    public GameBoard(int width, int height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        initLevel();

        canvas.setOnMouseMoved(event -> {
            paddle.setX(event.getX() - paddle.getWidth() / 2);
            renderAll();
        });

    }

    public void initLevel() {
        paddle = new Paddle (250, 370, 100, 20);
        ball = new Ball (295, 350, 10);

        for (int i = 0; i < 7; i++){
            for (int j = 0; j < 10; j++){
                bricks.add(new Brick(50 + j * 50, 40 + i * 30, 50, 20));
            }
        }
    }


    public void renderAll() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        paddle.render(gc);
        ball.render(gc);
        for (Brick brick : bricks){
            //Ball brick = null
            brick.render(gc);
        }


    }
}
