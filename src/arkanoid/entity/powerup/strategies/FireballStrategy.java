package arkanoid.entity.powerup.strategies;

import arkanoid.core.GameBoard;
import arkanoid.entity.powerup.PowerupStrategy;

public class FireballStrategy implements PowerupStrategy {
    @Override
    public void applyEffect(GameBoard gameBoard) {
        gameBoard.activateFireball();
    }

    @Override
    public String getType() {
        return "FIREBALL";
    }

    @Override
    public String getImagePath() {
        return "file:resource/image/Buff.png";
    }
}