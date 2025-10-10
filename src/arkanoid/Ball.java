package arkanoid;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class Ball extends MovableObject {
    private Image image;
    private double boardWidth;
    private double boardHeight;
    // constructor
    public Ball(double x, double y, double radius, double boardWidth, double boardHeight) {
        super(x, y, radius * 2, radius * 2);
        // Gọi constructor lớp cha MovableObject
        // width = height = radius * 2 để vẽ hình tròn
        image = new Image("file:resource/image/ball_1.png");
        this.dx = 2;
        this.dy = -2;
        this.width = boardWidth;
        this.height= boardHeight;
    }

    @Override
    public void update() {
        move();
        if (x <=0 || x + width >= boardWidth) {
            dx = -dx; // Đổi hướng khi chạm tường trái hoặc phải
        }
        if (y <= 0 || y + height <= 0) {
            dy = -dy; // Đổi hướng khi chạm tường trên

        }
        if (y + height >= boardHeight) {
            x = boardWidth / 2 - width / 2; // Đặt lại vị trí bóng ở giữa dưới cùng
            y = boardHeight /2;
            dx = 2; // Đặt lại vận tốc ban đầu
            dy = -2;
        }

    }


    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillOval(x, y, width, height);
        gc.drawImage(image, x, y, width, height);
    }

}