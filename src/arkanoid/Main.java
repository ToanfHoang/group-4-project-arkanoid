package arkanoid;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.awt.*;

/**
 * lớp Main khởi động ứng dụng JavaFX và thiết lập cửa sổ trò chơi
 * tạo một bảng trò chơi với kích thước xác định
 * hiển thị bảng trò chơi trong cửa sổ
 * gọi phương thức renderAll để vẽ tất cả các đối tượng trong bảng trò chơi
 */
public class Main extends Application {

    private final int WIDTH = 600;
    private final int HEIGHT = 400;

    @Override
    public void start(Stage stage) {
        GameBoard board = new GameBoard(WIDTH, HEIGHT);
        Scene scene = new Scene(board, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();
        board.renderAll();


    }

    public static void main(String[] args) {
        launch(args);
    }
}