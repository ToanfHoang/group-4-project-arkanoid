package arkanoid.collision;

import arkanoid.entity.Ball;
import arkanoid.entity.Brick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BallBrickCollisionTest {

    private Ball ball;
    private Brick brick;

    @BeforeEach
    void setUp() {
        // Tạo ball và brick không cần JavaFX context
        ball = createTestBall(100, 100);
        brick = createTestBrick(100, 100, Brick.BrickType.NORMAL);
    }

    @Test
    void testBallCollidesWithBrickTop() {
        // Arrange
        ball.setX(110);
        ball.setY(95);
        ball.setDx(0);
        ball.setDy(1.0);

        // Act
        boolean collision = checkCollision(ball, brick);

        // Assert
        assertTrue(collision, "Should detect top collision");

        // Test bounce direction
        double initialDy = ball.getDy();
        handleBrickCollision(ball, brick);
        assertEquals(-initialDy, ball.getDy(), 0.001, "Ball should bounce vertically");
    }

    @Test
    void testBallCollidesWithBrickLeft() {
        // Arrange
        ball.setX(95);
        ball.setY(110);
        ball.setDx(1.0);
        ball.setDy(0);

        // Act
        boolean collision = checkCollision(ball, brick);

        // Assert
        assertTrue(collision, "Should detect left collision");

        double initialDx = ball.getDx();
        handleBrickCollision(ball, brick);
        assertEquals(-initialDx, ball.getDx(), 0.001, "Ball should bounce horizontally");
    }

    @Test
    void testNoCollisionWhenFarApart() {
        // Arrange
        ball.setX(500);
        ball.setY(500);
        brick = createTestBrick(100, 100, Brick.BrickType.NORMAL);

        // Act
        boolean collision = checkCollision(ball, brick);

        // Assert
        assertFalse(collision, "Should not detect collision when far apart");
    }

    @Test
    void testFireballReducesBrickHitpoints() {
        // Arrange
        Brick strongBrick = createTestBrick(100, 100, Brick.BrickType.STRONG);

        // Act - Simulate fireball collision (2 hits)
        strongBrick.hasCollided();
        strongBrick.hasCollided();

        // Assert
        assertEquals(0, strongBrick.getHitpoint(), "Fireball should reduce hitpoints by 2");
        assertTrue(strongBrick.isDestroyed(), "Brick should be destroyed");
    }

    @Test
    void testUnbreakableBrickNotDestroyed() {
        // Arrange
        Brick unbreakableBrick = createTestBrick(100, 100, Brick.BrickType.UNBREAKABLE);
        int initialHitpoints = unbreakableBrick.getHitpoint();

        // Act
        unbreakableBrick.hasCollided();

        // Assert
        assertEquals(initialHitpoints, unbreakableBrick.getHitpoint(),
                "Unbreakable brick should not lose hitpoints");
        assertFalse(unbreakableBrick.isDestroyed(),
                "Unbreakable brick should not be destroyed");
    }

    @Test
    void testBrickTypesHaveCorrectHitpoints() {
        // Test all brick types
        assertEquals(1, Brick.BrickType.NORMAL.getHitPoints());
        assertEquals(2, Brick.BrickType.STRONG.getHitPoints());
        assertEquals(3, Brick.BrickType.SUPER_STRONG.getHitPoints());
        assertEquals(1, Brick.BrickType.EXPLOSIVE.getHitPoints());
        assertEquals(999, Brick.BrickType.UNBREAKABLE.getHitPoints());
    }

    @Test
    void testBrickTypesHaveCorrectScores() {
        // Test scoring for different brick types
        assertEquals(10, Brick.BrickType.NORMAL.getScore());
        assertEquals(20, Brick.BrickType.STRONG.getScore());
        assertEquals(30, Brick.BrickType.SUPER_STRONG.getScore());
        assertEquals(15, Brick.BrickType.EXPLOSIVE.getScore());
        assertEquals(0, Brick.BrickType.UNBREAKABLE.getScore());
    }

    // Helper methods
    private Ball createTestBall(double x, double y) {
        Ball ball = new Ball(x, y, 10, 800, 600);
        // Bypass JavaFX dependencies for testing
        ball.setDx(0);
        ball.setDy(0);
        return ball;
    }

    private Brick createTestBrick(double x, double y, Brick.BrickType type) {
        return new Brick(x, y, 50, 20, type);
    }

    private boolean checkCollision(Ball ball, Brick brick) {
        return ball.getX() < brick.getX() + brick.getWidth() &&
                ball.getX() + ball.getWidth() > brick.getX() &&
                ball.getY() < brick.getY() + brick.getHeight() &&
                ball.getY() + ball.getHeight() > brick.getY();
    }

    private void handleBrickCollision(Ball ball, Brick brick) {
        double overlapLeft = (ball.getX() + ball.getWidth()) - brick.getX();
        double overlapRight = (brick.getX() + brick.getWidth()) - ball.getX();
        double overlapTop = (ball.getY() + ball.getHeight()) - brick.getY();
        double overlapBottom = (brick.getY() + brick.getHeight()) - ball.getY();

        double minOverlap = Math.min(Math.min(overlapLeft, overlapRight),
                Math.min(overlapTop, overlapBottom));

        if (minOverlap == overlapTop || minOverlap == overlapBottom)
            ball.setDy(-ball.getDy());
        else
            ball.setDx(-ball.getDx());
    }
}