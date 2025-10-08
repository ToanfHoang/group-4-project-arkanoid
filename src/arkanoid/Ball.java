package arkanoid;


public class Ball extends MovableObject {
    /*public int width = 23;
    public int height = 23;
    */
    private int x;
    private int y;
    /*
    private double dx = 0.25;
    private double dy = -6;

    private int speed = 5;

*/
    public Ball(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void getImage() {}
    public void setX(int pos) {}
    public void setY(int pos) {}
    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }

    public void checkBrickCollision() {}
    public void checkPaddleCollision(Paddle paddle) {}
    public void switchDirections() {}
    public void hitPaddle(Paddle paddle) {}
    public void render() {}
}
