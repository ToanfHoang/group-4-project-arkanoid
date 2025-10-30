package arkanoid.scoring;

import arkanoid.core.GameStats;
import arkanoid.entity.Brick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameScoringTest {

    private GameStats gameStats;

    @BeforeEach
    void setUp() {
        gameStats = new GameStats();
    }

    @Test
    void testInitialGameStats() {
        // Assert
        assertEquals(0, gameStats.getScore(), "Initial score should be 0");
        assertEquals(3, gameStats.getLives(), "Initial lives should be 3");
        assertEquals(0, gameStats.getHighScore(), "Initial high score should be 0");
        assertTrue(gameStats.hasLivesLeft(), "Should have lives left initially");
    }

    @Test
    void testAddScoreForDifferentBrickTypes() {
        // Test all brick types
        testBrickScoring(Brick.BrickType.NORMAL, 10);
        testBrickScoring(Brick.BrickType.STRONG, 20);
        testBrickScoring(Brick.BrickType.SUPER_STRONG, 30);
        testBrickScoring(Brick.BrickType.EXPLOSIVE, 15);
        testBrickScoring(Brick.BrickType.UNBREAKABLE, 0);
    }

    private void testBrickScoring(Brick.BrickType brickType, int expectedPoints) {
        // Arrange
        GameStats stats = new GameStats();
        Brick brick = new Brick(0, 0, 50, 20, brickType);
        int initialScore = stats.getScore();

        // Act
        stats.addScore(brick);

        // Assert
        assertEquals(initialScore + expectedPoints, stats.getScore(),
                brickType + " brick should give " + expectedPoints + " points");
    }

    @Test
    void testLoseLife() {
        // Arrange
        int initialLives = gameStats.getLives();

        // Act
        gameStats.loseLife();

        // Assert
        assertEquals(initialLives - 1, gameStats.getLives(), "Should lose one life");
        assertTrue(gameStats.hasLivesLeft(), "Should still have lives left");
    }

    @Test
    void testGameOverWhenNoLivesLeft() {
        // Act - Mất hết mạng
        gameStats.loseLife(); // 2 lives left
        gameStats.loseLife(); // 1 life left
        gameStats.loseLife(); // 0 lives left

        // Assert
        assertEquals(0, gameStats.getLives(), "Should have 0 lives");
        assertFalse(gameStats.hasLivesLeft(), "Should have no lives left");
    }

    @Test
    void testAddLife() {
        // Arrange
        int initialLives = gameStats.getLives();

        // Act
        gameStats.addLife();

        // Assert
        assertEquals(initialLives + 1, gameStats.getLives(), "Should gain one life");
    }

    @Test
    void testHighScoreUpdate() {
        // Arrange
        Brick brick = new Brick(0, 0, 50, 20, Brick.BrickType.STRONG);

        // Act - Thêm điểm vượt quá high score
        gameStats.addScore(brick); // +20 points
        gameStats.addScore(brick); // +20 points, total 40

        // Assert
        assertEquals(40, gameStats.getHighScore(), "High score should update to maximum score");
        assertEquals(40, gameStats.getScore(), "Current score should match");
    }

    @Test
    void testHighScoreNotUpdatedWhenLower() {
        // Arrange
        Brick brick = new Brick(0, 0, 50, 20, Brick.BrickType.STRONG);
        gameStats.addScore(brick); // Score = 20, High Score = 20

        // Act - Reset và thêm ít điểm hơn
        gameStats.reset();
        gameStats.addScore(new Brick(0, 0, 50, 20, Brick.BrickType.NORMAL)); // +10 points

        // Assert
        assertEquals(20, gameStats.getHighScore(), "High score should remain at previous maximum");
        assertEquals(10, gameStats.getScore(), "Current score should be 10");
    }

    @Test
    void testResetGameStats() {
        // Arrange - Thay đổi trạng thái
        Brick brick = new Brick(0, 0, 50, 20, Brick.BrickType.NORMAL);
        gameStats.addScore(brick);
        gameStats.loseLife();

        // Act
        gameStats.reset();

        // Assert
        assertEquals(0, gameStats.getScore(), "Score should reset to 0");
        assertEquals(3, gameStats.getLives(), "Lives should reset to 3");
        assertTrue(gameStats.getHighScore() >= 0, "High score should be preserved or 0");
    }

    @Test
    void testMultipleBrickDestructionScoring() {
        // Test scoring for multiple bricks
        Brick normalBrick = new Brick(0, 0, 50, 20, Brick.BrickType.NORMAL);
        Brick strongBrick = new Brick(100, 0, 50, 20, Brick.BrickType.STRONG);

        gameStats.addScore(normalBrick); // +10
        gameStats.addScore(strongBrick); // +20

        assertEquals(30, gameStats.getScore(), "Total score should be 30");
        assertEquals(30, gameStats.getHighScore(), "High score should be 30");
    }
}