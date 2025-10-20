package arkanoid.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class Brick {
    private double x, y, width, height;
    private boolean destroyed = false; // kiểm tra xem gạch có bị phá hủy hay không
    private Image image;
    public int hitPoints = 1; // số lần va chạm để phá hủy gạch

    // constructor
    public Brick(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        image = new Image("file:resource/image/brick.png");
    }

    // vẽ viên gạch
    public void render(GraphicsContext gc) {
        if (!destroyed) {
            gc.setFill(Color.DARKCYAN); // màu gạch
            gc.fillRect(x, y, width, height);
            gc.drawImage(image, x, y, width, height);

            // (tùy chọn) vẽ viền trắng quanh gạch để nhìn rõ hơn
            gc.setStroke(Color.WHITE);
            gc.strokeRect(x, y, width, height);
        }
    }
    // va chạm
    public void hasCollided(){
        destroyed = true;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
