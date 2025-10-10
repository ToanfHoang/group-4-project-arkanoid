package arkanoid;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import java.awt.*;

/*
    * tao lớp Paddle kế thừa từ MovableObject
    * có thể di chuyển trái phải
    * vẽ hình chữ nhật màu xanh dương
    * có thể dừng lại
    * tốc độ di chuyển cố định
    * kích thước và vị trí khởi tạo từ tham số truyền vào
 */
public class Paddle {
    private double x, y, width, height;
    private Image paddle;

    public Paddle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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


    public void render(GraphicsContext gc) {
        gc.drawImage(paddle, x, y, width, height);
    }

    /*
    @Override
    public void update(){
        move();
    }
    public void moveLeft(){
        dx = -5;
    }
    public void moveRight() {
        dx = 5;
    }
    public void stop() {
        dx = 0;
    }

*/
}
