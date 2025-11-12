package arkanoid.entity.powerup.strategies;

import arkanoid.core.GameBoard;
import arkanoid.entity.powerup.PowerupStrategy;

public class MultiBallStrategy implements PowerupStrategy {
    @Override
    public void applyEffect(GameBoard gameBoard) {
        gameBoard.createExtraBalls();
    }

    @Override
    public String getType() {
        return "MULTI_BALL";
    }

    @Override
    public String getImagePath() {
        return "file:resource/image/Buff.png";
    }
}