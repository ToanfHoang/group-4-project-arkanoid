package arkanoid.core;

import javafx.scene.canvas.GraphicsContext;

import java.awt.*;

/**
 * tọa độ và kích thước của các đối tượng trong trò chơi
 */
public abstract class GameObject {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Rectangle bounds;

    protected GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(x, y, width, height);
    }

    /**
     * vẽ vật thể và cập nhật trạng thái của đối tượng
     * @param gc
     */
    public abstract void render( GraphicsContext gc);
    public abstract void update();

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Rectangle getBounds() { return bounds; }
}
