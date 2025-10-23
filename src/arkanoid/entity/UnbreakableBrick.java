package arkanoid.entity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class UnbreakableBrick extends Brick {
    public UnbreakableBrick(double x, double y, double width, double height) {
        super(x, y, width, height, 999);
        image = new Image("file:resource/image/unbreakableBrick.png");
    }


    public void hasCollided() {
        // Không làm gì - không thể phá
        // Bóng vẫn bật lại bình thường
    }
}
