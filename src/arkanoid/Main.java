package arkanoid;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public class Main extends Application {

    private final int WIDTH = 600;
    private final int HEIGHT = 400;

    @Override
    public void start(Stage stage) {
        GameBoard board = new GameBoard(WIDTH, HEIGHT);
        //board.initSample();
        Scene scene = new Scene(board, WIDTH, HEIGHT);

        stage.setScene(scene);
        stage.show();

        board.renderAll();


    }

    public static void main(String[] args) {
        launch(args);
    }
}