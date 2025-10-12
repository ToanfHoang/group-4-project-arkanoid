package arkanoid;

import javafx.scene.canvas.GraphicsContext;
/**
 * lớp trừu tượng cho các đối tượng có thể di chuyển trong trò chơi
 * kế thừa từ GameObject
 * thêm thuộc tính dx, dy để xác định vận tốc di chuyển theo trục x và y
 */
public abstract class MovableObject {
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    // vận tốc di chuyển theo trục x và y
    protected double dx;
    protected double dy;

    protected MovableObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dx = 0;
        this.dy = 0;
    }

    public abstract void render(GraphicsContext gc);
    public abstract void update();

    public void move() {
        x += dx;
        y += dy;
    }

}
