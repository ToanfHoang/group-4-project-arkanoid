package arkanoid;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;


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
    }

    public void initLevel() {
        paddle = new Paddle (250, 370, 100, 20);
        ball = new Ball (295, 350, 10);

        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 8; j++){
                bricks.add(new Brick(80 + j * 80, 50 + i * 30, 60, 20));
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
