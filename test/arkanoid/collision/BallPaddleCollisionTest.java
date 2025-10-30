package arkanoid.collision;

import arkanoid.entity.Ball;
import arkanoid.entity.Paddle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BallPaddleCollisionTest {

    private Paddle paddle;
    private Ball ball;

    @BeforeEach
    void setUp() {
        paddle = new Paddle(350, 500, 100, 20);
        ball = createTestBall(400, 480);
    }

    @Test
    void testBallCollidesWithPaddleCenter() {
        // Arrange
        ball.setX(395);
        ball.setY(485);
        ball.setDx(0.5);
        ball.setDy(1.0);

        // Act
        boolean collision = ball.checkPaddleCollision(paddle);

        // Assert
        assertTrue(collision, "Ball should collide with paddle center");
        assertTrue(ball.getDy() < 0, "Ball should bounce upward after collision");
    }

    @Test
    void testBallCollidesWithPaddleLeftEdge() {
        // Arrange
        ball.setX(351);
        ball.setY(485);
        ball.setDx(0.5);
        ball.setDy(1.0);

        // Act
        boolean collision = ball.checkPaddleCollision(paddle);

        // Assert
        assertTrue(collision, "Ball should collide with paddle left edge");
        assertTrue(ball.getDx() < 0, "Ball should have negative DX when hitting left edge");
    }

    @Test
    void testBallCollidesWithPaddleRightEdge() {
        // Arrange
        ball.setX(449);
        ball.setY(485);
        ball.setDx(-0.5);
        ball.setDy(1.0);

        // Act
        boolean collision = ball.checkPaddleCollision(paddle);

        // Assert
        assertTrue(collision, "Ball should collide with paddle right edge");
        assertTrue(ball.getDx() > 0, "Ball should have positive DX when hitting right edge");
    }

    @Test
    void testNoCollisionWhenBallMovingAway() {
        // Arrange
        ball.setX(400);
        ball.setY(485);
        ball.setDx(0.5);
        ball.setDy(-1.0); // Moving upward

        // Act
        boolean collision = ball.checkPaddleCollision(paddle);

        // Assert
        assertFalse(collision, "No collision when ball moving away from paddle");
    }

    @Test
    void testNoCollisionWhenBallTooFar() {
        // Arrange
        ball.setX(600); // Far from paddle
        ball.setY(485);
        ball.setDx(0.5);
        ball.setDy(1.0);

        // Act
        boolean collision = ball.checkPaddleCollision(paddle);

        // Assert
        assertFalse(collision, "No collision when ball is too far from paddle");
    }

    @Test
    void testPaddleBoundaryMovement() {
        // Arrange
        double boardWidth = 800;

        // Act - Di chuyển paddle ra ngoài biên trái
        paddle.setX(-10, boardWidth);

        // Assert
        assertEquals(0, paddle.getX(), "Paddle should not go beyond left boundary");

        // Act - Di chuyển paddle ra ngoài biên phải
        paddle.setX(750, boardWidth); // 750 + 100 = 850 > 800

        // Assert
        assertEquals(700, paddle.getX(), "Paddle should not go beyond right boundary");
    }

    @Test
    void testPaddleDimensions() {
        // Test paddle properties
        assertEquals(350, paddle.getX(), 0.001);
        assertEquals(500, paddle.getY(), 0.001);
        assertEquals(100, paddle.getWidth(), 0.001);
        assertEquals(20, paddle.getHeight(), 0.001);
    }

    // Helper method
    private Ball createTestBall(double x, double y) {
        Ball ball = new Ball(x, y, 10, 800, 600);
        ball.setDx(0);
        ball.setDy(0);
        return ball;
    }
}