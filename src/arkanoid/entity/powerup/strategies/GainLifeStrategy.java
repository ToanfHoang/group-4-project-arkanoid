package arkanoid.entity.powerup.strategies;

import arkanoid.core.GameBoard;
import arkanoid.entity.powerup.PowerupStrategy;

public class GainLifeStrategy implements PowerupStrategy {
    @Override
    public void applyEffect(GameBoard gameBoard) {
        gameBoard.gainLife();
    }

    @Override
    public String getType() {
        return "GAIN_LIFE";
    }

    @Override
    public String getImagePath() {
        return "file:resource/image/Buff.png";
    }
}