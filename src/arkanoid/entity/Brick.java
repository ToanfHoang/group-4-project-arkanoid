package arkanoid.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class Brick  {
    protected double x, y, width, height;
    protected boolean destroyed ; // kiểm tra xem gạch có bị phá hủy hay không
    protected Image image;

    protected int hitPoints ;
    protected  Color color;

    private Random rand;
    private int powerup = 0;

    // constructor
    public Brick(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hitPoints = 1;
        this.destroyed = false;

        image = new Image("file:resource/image/brick.png");
    }

    protected Brick(double x, double y, double width, double height, int hits) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hitPoints = hits;
        this.destroyed = false;

    }

    // va chạm
    public void hasCollided(Ball ball) {
        hitPoints--;

        if (hitPoints <= 0) {
            destroyed = true;
            ball.increaseSpeed();

            // Gọi phương thức onDestroyed khi gạch bị phá hủy
            onDestroyed(ball);
        }
    }

    // phương thức khi gạch bị phá hủy
    protected void onDestroyed(Ball ball) {

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


    public void render(GraphicsContext gc) {
        if (destroyed) return;

        if (image != null && !image.isError()) {
            gc.drawImage(image, x, y, width, height);
        } else {
            // Fallback vẽ bằng màu
            gc.setFill(color);
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeRect(x, y, width, height);
        }

        renderExtra(gc); // Cho class con vẽ thêm
    }

    // Hook cho class con vẽ thêm (số hits, icon, etc.)
    protected void renderExtra(GraphicsContext gc) {
           // Mặc định không vẽ gì
    }


    public void update() {
        // Gạch không di chuyển
    }

}
