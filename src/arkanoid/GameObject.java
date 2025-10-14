package arkanoid;

import javafx.scene.canvas.GraphicsContext;

/**
 * tọa độ và kích thước của các đối tượng trong trò chơi
 */
public abstract class GameObject {
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    protected GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * vẽ vật thể và cập nhật trạng thái của đối tượng
     * @param gc
     */
    public abstract void render( GraphicsContext gc);
    public abstract void update();
}
