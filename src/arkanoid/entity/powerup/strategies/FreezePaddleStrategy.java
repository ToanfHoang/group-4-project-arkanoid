package arkanoid.entity.powerup.strategies;

import arkanoid.core.GameBoard;
import arkanoid.entity.powerup.PowerupStrategy;

public class FreezePaddleStrategy implements PowerupStrategy {
    @Override
    public void applyEffect(GameBoard gameBoard) {
        gameBoard.freezePaddle();
    }

    @Override
    public String getType() {
        return "FREEZE_PADDLE";
    }

    @Override
    public String getImagePath() {
        return "file:resource/image/Debuff.png";
    }
}