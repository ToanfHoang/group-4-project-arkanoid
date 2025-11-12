package arkanoid.entity.powerup;

import arkanoid.core.GameBoard;

public interface PowerupStrategy {
    void applyEffect(GameBoard gamebBoard);
    String getType();
    String getImagePath();
}
