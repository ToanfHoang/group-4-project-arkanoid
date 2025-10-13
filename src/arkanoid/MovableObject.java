package arkanoid;

import javafx.scene.canvas.GraphicsContext;

/**
 * Lớp trừu tượng MovableObject đại diện cho các đối tượng có thể di chuyển trong game
 * Cung cấp các thuộc tính cơ bản như vị trí (x, y), kích thước (width, height)
 * và vận tốc (dx, dy) cho tất cả các đối tượng con
 */
public abstract class MovableObject {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected double dx; // Vận tốc theo trục X
    protected double dy; // Vận tốc theo trục Y

    public MovableObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dx = 0;
        this.dy = 0;
    }

    // Di chuyển đối tượng theo vận tốc hiện tại
    public void move() {
        x += dx;
        y += dy;
    }

    // Phương thức trừu tượng bắt buộc các lớp con phải implement
    public abstract void update();
    public abstract void render(GraphicsContext gc);

    // Getters
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

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    // Setters
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}