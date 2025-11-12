package arkanoid.entity.powerup.strategies;

import arkanoid.core.GameBoard;
import arkanoid.entity.powerup.PowerupStrategy;

public class GrowPaddleStrategy implements PowerupStrategy {
    @Override
    public void applyEffect(GameBoard gameBoard) {
        gameBoard.getPaddle().grow(15);
    }

    @Override
    public String getType() {
        return "GROW_PADDLE";
    }

    @Override
    public String getImagePath() {
        return "file:resource/image/Buff.png";
    }
}