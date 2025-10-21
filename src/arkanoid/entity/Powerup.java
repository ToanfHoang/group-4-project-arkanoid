package arkanoid.entity;

import arkanoid.core.GameObject;
import arkanoid.main.Main;
import javafx.application.Application;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.canvas.Canvas;

import java.awt.*;

public class Powerup extends GameObject {
    public boolean remove = false;
    private int speed = 2;
    private Image image;
    public int powerup;
    private final double canvasHeight;

    public Powerup(int x, int y, int type, double canvasHeight) {
        super(x, y, 25, 25);
        this.powerup = type;
        this.canvasHeight = canvasHeight;

        if(powerup == 1){
            image = new Image("file:resource/image/PUMultiBall.png");
        } else if(powerup == 2){
            image = new Image("file:resource/image/PUGrowth.png");
        } else if(powerup == 3){
            image = new Image("file:resource/image/PUFireball.png");
        }

    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }

    @Override
    public void update() {
        if(y + height <= canvasHeight){
            y += speed;
            bounds = new Rectangle(x, y, width, height);
        } else {
            remove = true;
        }
    }

    public Image getImage() {
        return image;
    }
}
