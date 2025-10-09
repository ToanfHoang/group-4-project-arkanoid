package arkanoid;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Brick {
    private double x, y, width, height;
    private boolean destroyed = false; // kiểm tra xem gạch có bị phá hủy hay không

    // constructor
    public Brick(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // vẽ viên gạch
    public void render(GraphicsContext gc) {
        if (!destroyed) {
            gc.setFill(Color.DARKCYAN); // màu gạch
            gc.fillRect(x, y, width, height);

            // (tùy chọn) vẽ viền trắng quanh gạch để nhìn rõ hơn
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, width, height);
        }
    }
}
