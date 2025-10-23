package arkanoid.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class StrongBrick extends Brick {
    public StrongBrick(double x, double y, double width, double height) {
        super(x, y, width, height, 2);
        image = new Image("file:resource/image/strongBrick.png");
    }

    /*
    @Override
    protected void renderExtra(GraphicsContext gc) {
        // Hiển thị số lần đánh còn lại
        if (hitPoints > 0) {
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(16));
            gc.fillText(String.valueOf(hitPoints), x + width/2 - 5, y + height/2 + 5);
        }
    }

     */


}
