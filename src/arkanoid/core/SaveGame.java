package arkanoid.core;

/**
 * Class chứa dữ liệu cần save/load
 */
public class SaveGame {
    private int score;
    private int lives;
    private int currentLevel;
    private int highScore;

    // Constructor
    public SaveGame(int score, int lives, int currentLevel, int highScore) {
        this.score = score;
        this.lives = lives;
        this.currentLevel = currentLevel;
        this.highScore = highScore;
    }

    // Constructor rỗng
    public SaveGame() {
    }

    // Getters and Setters
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    @Override
    public String toString() {
        return "SaveGame{" +
                "score=" + score +
                ", lives=" + lives +
                ", currentLevel=" + currentLevel +
                ", highScore=" + highScore +
                '}';
    }
}