package arkanoid.core;

import arkanoid.entity.Brick;
import java.util.List;

public class LevelManager {
    private int currentLevel = 1;
    private final int maxLevel = 5;

    public MapLoader.MapData loadCurrentLevel() {
        String filename = String.format("map%02d.txt", currentLevel);
        return MapLoader.loadMap(filename, 50, 30);
    }

    public void nextLevel() {
        currentLevel++;
        if (currentLevel > maxLevel) {
            currentLevel = 1;
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int level) {
        this.currentLevel = level;

        // Đảm bảo level hợp lệ
        if (this.currentLevel < 1) {
            this.currentLevel = 1;
        } else if (this.currentLevel > maxLevel) {
            this.currentLevel = maxLevel;
        }
    }

    public void reset() {
        currentLevel = 1;
    }
}