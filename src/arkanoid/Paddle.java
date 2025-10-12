package arkanoid;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import java.awt.*;

/*
    * lớp Paddle đại diện cho thanh điều khiển trong trò chơi Arkanoid
    * có thuộc tính vị trí (x, y), kích thước (width, height) và hình ảnh (paddle)
    * cung cấp các phương thức để lấy và thay đổi vị trí x của paddle
    * và phương thức render để vẽ paddle
    * sử dụng hình ảnh từ file "resource/image/paddle.png" để hiển thị paddle
    * kích thước và vị trí của paddle được xác định khi tạo đối tượng Paddle
    * paddle di chuyển theo trục x để người chơi điều khiển
    * paddle có chiều rộng cố định để người chơi dễ dàng đánh bóng
 */
public class Paddle extends MovableObject {

    private Image paddle;

    private  double hitbox_paddle_width ;
    private  double hitbox_paddle_height;


    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.hitbox_paddle_height = height;
        this.hitbox_paddle_width = width;

        paddle = new Image("file:resource/image/paddle.png");
    }

    // lấy giá trị x
    public double getX() {
        return x;
    }

    // thay đổi giá trị x
    public void setX(double x) {
        this.x = x;
    }

    // Getter: lấy chiều rộng
    public double getWidth() {
        return width;
    }

    // Getter: lấy chiều cao
    public double getHeight() { return height;}


    public void render(GraphicsContext gc) {
        gc.drawImage(paddle, x, y, width, height);
    }

    @Override
    public void update() {

    }

    public  void setX(double newX, int boardWidth) {
        if (newX < 0) {
            this.x = 0;
        } else if (newX + width > boardWidth) {
            this.x = boardWidth - width;
        } else {
            this.x = newX;
        }
    }

    public int getY() {
        return (int) y;
    }
}
