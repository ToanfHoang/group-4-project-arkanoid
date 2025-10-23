package arkanoid.entity;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import java.util.Objects;

public class DropItemBrick extends Brick {
    public DropItemBrick(double x, double y, double width, double height) {
        super(x, y, width, height, 1);
        image = new Image("file:resource/image/dropItemBrick.png");
    }


    protected void onDestroyed() {
        System.out.println("⭐ Bonus item dropped!");
        //  Thả PowerUp item xuống
        // gameBoard.spawnPowerUp(x + width/2, y + height/2);
    }

    /*
    @Override
    protected void renderExtra(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(20));
        gc.fillText("?", x + width/2 - 6, y + height/2 + 7);
    }

     */
}