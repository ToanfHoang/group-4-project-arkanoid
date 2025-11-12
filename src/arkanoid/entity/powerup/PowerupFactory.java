package arkanoid.entity.powerup;

import arkanoid.entity.Powerup;
import arkanoid.entity.powerup.strategies.*;

import java.util.HashMap;
import java.util.Map;

public class PowerupFactory {
    private static final Map<Integer, PowerupStrategy> STRATEGY_MAP = new HashMap<>();

    static {
        STRATEGY_MAP.put(1, new MultiBallStrategy());
        STRATEGY_MAP.put(2, new GrowPaddleStrategy());
        STRATEGY_MAP.put(3, new FireballStrategy());
        STRATEGY_MAP.put(4, new LoseLifeStrategy());
        STRATEGY_MAP.put(5, new GainLifeStrategy());
        STRATEGY_MAP.put(6, new FreezePaddleStrategy());
        STRATEGY_MAP.put(7, new DoubleScoreStrategy());
    }

    public static Powerup createPowerup(int x, int y, int type, double canvasHeight) {
        PowerupStrategy strategy = STRATEGY_MAP.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown powerup type: " + type);
        }
        return new Powerup(x, y, strategy, canvasHeight);
    }

    public static void registerStrategy(int type, PowerupStrategy strategy) {
        STRATEGY_MAP.put(type, strategy);
    }
}