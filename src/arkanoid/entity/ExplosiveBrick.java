package arkanoid.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import java.util.List;
import java.util.Objects;

public class ExplosiveBrick extends Brick {
    private List<Brick> allBricks;

    public ExplosiveBrick(double x, double y, double width, double height, List<Brick> allBricks) {
        super(x, y, width, height, 1);
        image = new Image("file:resource/image/explosiveBrick.png");
        this.allBricks = allBricks;
    }

    @Override
    protected void onDestroyed(Ball ball) {
        System.out.println("💥 BOOM! Explosive brick destroyed!");

        double explosionRadius = 80;
        double thisCenterX = x + width / 2;
        double thisCenterY = y + height / 2;

        for (Brick brick : allBricks) {
            if (brick == this || brick.isDestroyed()) continue;

            double brickCenterX = brick.getX() + brick.getWidth() / 2;
            double brickCenterY = brick.getY() + brick.getHeight() / 2;

            double distance = Math.sqrt(
                    Math.pow(brickCenterX - thisCenterX, 2) +
                            Math.pow(brickCenterY - thisCenterY, 2)
            );

            if (distance <= explosionRadius) {
                brick.hasCollided(ball);
            }
        }
    }

/*
    @Override
    protected void renderExtra(GraphicsContext gc) {
        // Vẽ biểu tượng nổ
        gc.setFill(Color.YELLOW);

        gc.setFont(javafx.scene.text.Font.font(18));


    }

 */


}
