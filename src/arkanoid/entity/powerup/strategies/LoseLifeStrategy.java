package arkanoid.entity.powerup.strategies;

import arkanoid.core.GameBoard;
import arkanoid.entity.powerup.PowerupStrategy;

public class LoseLifeStrategy implements PowerupStrategy {
    @Override
    public void applyEffect(GameBoard gameBoard) {
        gameBoard.loseLife();
    }

    @Override
    public String getType() {
        return "LOSE_LIFE";
    }

    @Override
    public String getImagePath() {
        return "file:resource/image/Debuff.png";
    }
}