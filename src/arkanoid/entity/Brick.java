package arkanoid.entity;

import arkanoid.core.GameStats;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Random;

public class Brick {
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    public int originalHitPoints;
    public boolean dropPowerup = false;
    private boolean destroyed = false; // kiểm tra xem gạch có bị phá hủy hay không
    private Image image;
    public int hitPoints = 1; // số lần va chạm để phá hủy gạch

    private int powerup = 0;

    private final BrickType type;
    private boolean isExploding = false;
    private Image superBrickFull;
    private Image superBrickCrack1;
    private Image superBrickCrack2;

    public int getHitpoint() {
        return hitPoints;
    }

    public enum BrickType {
        NORMAL(1, Color.DARKCYAN, 10),           // Gạch thường - 1 hit
        STRONG(2, Color.DARKRED, 20),            // Gạch cứng - 2 hits
        SUPER_STRONG(3, Color.DARKVIOLET, 30),   // Gạch siêu cứng - 3 hits
        EXPLOSIVE(1, Color.ORANGE, 15),          // Gạch nổ - phá các gạch xung quanh
        UNBREAKABLE(999, Color.GRAY, 0),         // Gạch không thể phá
        MOVING(1, Color.LIGHTBLUE, 15);       // Gạch di chuyển cho boss


        private final int hitPoints;
        private final int score;

        BrickType(int hitPoints, Color color, int score) {
            this.hitPoints = hitPoints;
            this.score = score;
        }

        public int getHitPoints() {
            return hitPoints;
        }

        public int getScore() {
            return score;
        }
    }

    // constructor
    public Brick(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        originalHitPoints = hitPoints;
        this.type = BrickType.NORMAL;
        image = new Image("file:resource/image/brick.png");
    }

    // constructor với loại gạch
    public Brick(double x, double y, double width, double height, BrickType type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.hitPoints = type.getHitPoints();
        this.originalHitPoints = this.hitPoints;

        Random rand = new Random();

        // Tăng tỷ lệ powerup cho gạch khó hơn
        if (type == BrickType.STRONG || type == BrickType.SUPER_STRONG) {
            powerup = rand.nextInt(15);
        } else if (type == BrickType.EXPLOSIVE) {
            powerup = rand.nextInt(15)
        } else if (type != BrickType.UNBREAKABLE) {
            powerup = rand.nextInt(10);
        }
        if (type == BrickType.SUPER_STRONG) {
            superBrickFull  = new Image("file:resource/image/strong_brick.png");
            superBrickCrack1 = new Image("file:resource/image/strong1_brick.png");
            superBrickCrack2 = new Image("file:resource/image/strong2_brick.png");
        }
        // Load image phù hợp với loại gạch
        try {
            switch (type) {
                case NORMAL:
                    image = new Image("file:resource/image/normal_brick.png");
                    break;
                case STRONG:
                    image = new Image("file:resource/image/hard_brick.png");
                    break;
                case SUPER_STRONG:
                    image = new Image("file:resource/image/items_brick.png");
                    break;
                case EXPLOSIVE:
                    image = new Image("file:resource/image/exploding_brick.png");
                    break;
                case UNBREAKABLE:
                    image = new Image("file:resource/image/unbreakable_brick.png");
                    break;
            }
        } catch (Exception e) {
            image = null;
        }
    }

    // vẽ viên gạch
    public void render(GraphicsContext gc) {
        if (!destroyed) {
            gc.setFill(Color.DARKCYAN); // màu gạch
            gc.fillRect(x, y, width, height);
            if (image != null) {
                gc.drawImage(image, x, y, width, height);
            }
            if (type == BrickType.SUPER_STRONG) {
                if (hitPoints >= 3) {
                    gc.drawImage(superBrickFull, x, y, width, height);
                } else if (hitPoints == 2) {
                    gc.drawImage(superBrickCrack1, x, y, width, height);
                } else if (hitPoints == 1) {
                    gc.drawImage(superBrickCrack2, x, y, width, height);
                }
            } else {
                gc.drawImage(image, x, y, width, height);
            }
            if (type == BrickType.SUPER_STRONG && hitPoints <= 0) {
                gc.setFill(Color.rgb(255, 255, 255, 0.4));
                gc.fillRect(x, y, width, height);
            }

            // Hiệu ứng đặc biệt cho gạch nổ
            if (type == BrickType.EXPLOSIVE) {
                gc.setStroke(Color.YELLOW);
                gc.setLineWidth(1);
                double centerX = x + width / 2;
                double centerY = y + height / 2;
                gc.strokeLine(centerX - 5, centerY, centerX + 5, centerY);
                gc.strokeLine(centerX, centerY - 5, centerX, centerY + 5);
            }
        }
    }

    public int hasPowerup() {
        return powerup;
    }

    // va chạm
    public void hasCollided() {
        if (type == BrickType.UNBREAKABLE) {
            return;
        }

        if (hitPoints >= 1) {
            hitPoints--;

            if (type == BrickType.STRONG && hitPoints == 1) {
                try {
                    Image cracked = new Image("file:resource/image/crack_brick.png");
                    if (cracked.getWidth() > 0) image = cracked;
                } catch (Exception e) {
                    // nếu không tìm được ảnh cracked thì im lặng (giữ ảnh cũ)
                }
            }

            if (hitPoints == 0) {
                destroyed = true;
                if (type == BrickType.EXPLOSIVE) {
                    isExploding = true;
                }
                if (hasPowerup() > 0) {
                    dropPowerup = true;
                }
            }
        }
    }

    public void destroyed() {
        if (type == BrickType.UNBREAKABLE) {
            return;
        }

        hitPoints = 0;
        destroyed = true;
        if (type == BrickType.EXPLOSIVE) {
            isExploding = true;
        }
        if (hasPowerup() > 0) {
            dropPowerup = true;
        }
    }

    public boolean isInExplosionRange(Brick other) {
        double centerX = x + width / 2;
        double centerY = y + height / 2;
        double otherCenterX = other.x + other.width / 2;
        double otherCenterY = other.y + other.height / 2;

        double distance = Math.sqrt(
                Math.pow(centerX - otherCenterX, 2) +
                        Math.pow(centerY - otherCenterY, 2)
        );

        int explosionRadius = 50;
        return distance <= explosionRadius;
    }

    public static boolean areAllBricksDestroyed(List<Brick> bricks) {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    public void handleExplosion(List<Brick> allBricks, GameStats gameStats) {
        if (this.isExploding() && this.type == BrickType.EXPLOSIVE) {
            for (Brick other : allBricks) {
                if (!other.isDestroyed() && this.isInExplosionRange(other)) {
                    other.destroyed();
                    gameStats.addScore(other);
                }
            }
        }
    }

    public Powerup createPowerupIfNeeded(double canvasHeight) {
        if (this.isDestroyed() && this.hasPowerup() > 0) {
            return new Powerup(
                    (int) (this.getX() + this.getWidth() / 2),
                    (int) (this.getY() + this.getHeight() / 2),
                    this.hasPowerup(),
                    canvasHeight
            );
        }
        return null;
    }

    public void handleFireballCollision() {
        if (this.type != BrickType.UNBREAKABLE) {
            // Fireball: giảm hitpoint đi 2
            for (int i = 0; i < 2; i++) {
                this.hasCollided();
            }
        }
    }


    public boolean isExploding() {
        return isExploding;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public BrickType getType() {
        return type;
    }


}
