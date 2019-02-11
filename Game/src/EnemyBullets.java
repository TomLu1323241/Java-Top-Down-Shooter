
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class EnemyBullets extends GameObjects {

    private int speed;
    private String[] orintation = {"|", "\\", "-", "/", "|", "\\", "-", "/"};
    private int posOrin = (int) (Math.random() * 4); // random starting position
    private int count;
    private boolean track;
    private boolean goingRight;
    private double slope;
    private int shift;
    private Pattern bulletType;
    private int intercept;

    /**
     * This will create a bullet moving towards the target or down
     *
     * @param x starting x position
     *
     * @param y starting y position
     *
     * @param color color of bullet
     *
     * @param level level of bullet
     */
    EnemyBullets(int x, int y, Color color, int speed, int xTrack, int yTrack, Pattern bulletType, double slope) {// track need some work, Ricky: added stuff
        super(x, y, color);
        this.speed = speed;
        this.track = track;
        this.bulletType = bulletType;
        if (bulletType != Pattern.Track) {
            this.slope = slope;
        }
        this.intercept = x;
        if (bulletType == Pattern.Track && x - xTrack != 0) {// Slope and shift generation
            this.slope = (double) (y - yTrack) / (double) (x - xTrack);
            shift = (int) (yTrack - this.slope * xTrack);
        }
        if (x - xTrack > 0) {
            goingRight = false;
        } else {
            goingRight = true;
        }
    }

	@Override
    public void update(GameContainer panel) {// Movement
        switch (bulletType) {
            case Track:
                if (goingRight) {
                    x += speed;
                    y = (int) (slope * x + shift);
                } else {
                    x -= speed;
                    y = (int) (slope * x + shift);
                }
                break;
            case Single:
                super.y += speed;
                break;
            case Double:
                super.y += speed;
                break;
            case Spread:
                super.y += speed;
                super.x += slope;
                break;
            case Wiggle:
                super.y += speed;
                super.x = (int) (20 * Math.sin(0.03 * ((double) super.y)) + intercept);
                break;
            default:
                break;

        }

    }

    public boolean hit(Bomb bomb) {
        return bomb.getHitBox().intersects(this.getHitBox());
    }

    @Override
    public void paintComponent(Graphics g) {// Draws stuff
    	if (bulletType == Pattern.Track) {
            g.setColor(Color.BLUE);
    	} else {
            g.setColor(super.color);
    	}
        g.setFont(new Font(null, Font.BOLD, 15));
        count++;
        if (count == 6) {
            count = 0;
            posOrin++;
        }
        if (posOrin % 8 == 2 || posOrin % 8 == 6) {// alternates bettween bullets to create animation
            g.drawString(orintation[posOrin % 8], x - 5, y);
        } else {
            g.drawString(orintation[posOrin % 8], x, y);
        }
    }

    /**
     * This will return the hitbox of the bullet
     *
     * @return returns hitbox
     */
    public Rectangle2D getHitBox() {
        return new Rectangle2D.Double(x - 4, y - 15, 10, 20);
    }

}
