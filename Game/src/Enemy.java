import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Enemy extends GameObjects {

    Pattern pattern;
    double yScale;
    double xScale;
    double yShift;
    double xShift;
    double speed;
    int health;
    double width;
    boolean startLeft;
    int count = 1;
    boolean shoot;
    int rof;
    int score;
    int size;
    boolean importantKill;
    Pattern bulletType;
    boolean inPos;
    double speedBackup;
    //
    String error;

    /**
     * @param color Color
     * @param pattern Pattern
     * @param yScale Y Scale
     * @param xScale X Scale or Slope for Lines
     * @param yShift Y Shift
     * @param xShift X Shift
     * @param speed Speed
     * @param startLeft Starts of the left side if true and right if false
     * @param error Displayed String
     * @param health Total Health
     * @param shoot can this enemy shoot?
     * @param rof rate of fire
     * @param score points gained when enemy is killed
     * @param size size of the enemy
     * @param importantKill is killing this enemy going to end the game?
     */
    public Enemy(Color color, Pattern pattern, double yScale, double xScale, double yShift, double xShift, double speed,
            boolean startLeft, String error, int health, boolean shoot, int rof, int score, int size, boolean importantKill, Pattern bulletType) {
        super(0, 0, color);
        this.pattern = pattern;
        this.yScale = yScale;
        this.xScale = xScale;
        this.yShift = yShift;
        this.xShift = xShift;
        this.speed = speed;
        this.startLeft = startLeft;
        this.error = error;
        this.health = health;
        this.shoot = shoot;
        this.rof = rof;
        this.score = score;
        this.size = size;
        this.importantKill = importantKill;
        this.bulletType = bulletType;
        if (!startLeft) { // sets starting postition
            x = (int) (720 + width / 2);
        } else {// changed value to allow for enemies to spawn offscreen
            x = (int) (-100 - width / 2);
        }
    }

    public boolean hit(Bullets bullet) {
        return bullet.getHitBox().intersects(this.getHitBox());
    }

    public boolean hit(Bomb bomb) {
        return bomb.getHitBox().intersects(this.getHitBox());
    }

    public Rectangle2D getHitBox() {
        return new Rectangle2D.Double(x, y - 20, width, 20);
    }

    @Override
    public void update(GameContainer panel) {
        count++;

        // Enemy Movement
        if (pattern == Pattern.Hover) {
            if (count < 125 && inPos == false) {
                x += speed;
            } else if (count == 125 && inPos == false) {
                count = 0;
                inPos = true;
            } else if (count % 200 == 0 && inPos == true) {
                speed = -speed;
                x += speed;
            } else {
                x += speed;
            }
        } else if (pattern == Pattern.Rectangle) {
            if (count < 125 && inPos == false) {
                x += speed;
            } else if (count == 125 && inPos == false) {
                count = 0;
                inPos = true;
            } else if (count % 800 == 0 && inPos == true) {
                speed = speedBackup;
                speedBackup = 0;
                speed = -speed;
                x += speed;
                count = 0;
            } else if (count % 600 == 0 && inPos == true) {
                speedBackup = speed;
                speed = 0;
            } else if (count % 400 == 0 && inPos == true) {
                speed = speedBackup;
                speedBackup = 0;
                speed = -speed;
                x += speed;
            } else if (count % 200 == 0 && inPos == true) {
                speedBackup = speed;
                speed = 0;
            } else {
                x += speed;
                y += speedBackup;
            }
        } else if (pattern == Pattern.Snake) {
            if (count < 125 && inPos == false) {
                x += speed;
            } else if (count == 125 && inPos == false) {
                count = 0;
                inPos = true;
            } else if (count % 250 == 0 && inPos == true) {
                speed = -speedBackup;
                speedBackup = 0;
                x += speed;
                count = 0;
            } else if (count % 200 == 0 && inPos == true) {
                speedBackup = speed;
                speed = 0;
            } else {
                x += speed;
                y += Math.abs(speedBackup);
            }
        } else if (pattern == Pattern.Immobile) {
            if (count < 350 && inPos == false) {
                x += speed;
            }
            
        } else if (startLeft) {
            x += speed;
        } else {
            x -= speed;
        }
        //
        switch (pattern) {
            case Sine:
                y = (int) (yScale * Math.sin((double) x / (double) xScale + xShift) + yShift);
                break;
            case Horizontal:
                y = (int) yShift;
                break;
            case Parabola:
                y = (int) (Math.pow((x / xScale - xShift), 2) / -yScale + yShift);
                break;
            case Diagonal:
                y = (int) ((xScale * x) + yShift);
                break;
            //Ricky: Added this
            case Hover:
                y = (int) yShift;
                break;
            case Rectangle:
                if (inPos == false) {
                    y = (int) (yShift);
                }
                break;
            case Snake:
                if (inPos == false) {
                    y = (int) (yShift);
                }
                break;
            case Immobile:
                y = (int) yShift;
                break;
            //
            default:
                break;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(color);
        g.setFont(new Font(null, Font.PLAIN, size));
        g.drawString(error, x, y);
        width = g.getFontMetrics().stringWidth(error);
    }

    public void setHealth(int damage) {
        if (damage == 1) {
            this.health -= 1;
        } else {
            this.health -= 2;
        }
    }

    public boolean shoot() {// shoots every second Ricky: modified this
        if (shoot) {
            if (count % rof == 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public int getHealth() {
        return health;
    }

    public boolean getKillInfo() {
        return importantKill;
    }

    public int getScore() {
        return score;
    }

    public Pattern getBullet() {
        return bulletType;
    }
}
