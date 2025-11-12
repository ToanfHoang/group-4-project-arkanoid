package arkanoid.entity.powerup.strategies;

import arkanoid.core.GameBoard;
import arkanoid.entity.powerup.PowerupStrategy;

public class DoubleScoreStrategy implements PowerupStrategy {
    @Override
    public void applyEffect(GameBoard gameBoard) {
        gameBoard.activateDoubleScore();
    }

    @Override
    public String getType() {
        return "DOUBLE_SCORE";
    }

    @Override
    public String getImagePath() {
        return "file:resource/image/Buff.png";
    }
}