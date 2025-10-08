package arkanoid;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
/*
    * tao lớp Paddle kế thừa từ MovableObject
    * có thể di chuyển trái phải
    * vẽ hình chữ nhật màu xanh dương
    * có thể dừng lại
    * tốc độ di chuyển cố định
    * kích thước và vị trí khởi tạo từ tham số truyền vào
 */
public class Paddle extends MovableObject{
    public Paddle(double x, double y, double width, double height) {
        super(x,y,width,height);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLUE);
        gc.fillRect(x, y, width, height);
    }
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

}
