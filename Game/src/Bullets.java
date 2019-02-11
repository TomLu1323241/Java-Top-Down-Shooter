import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

public class Bullets extends GameObjects {

	private int level;
	private int[] rectangle = new int[4];
	private int mid = 0;
	private final int HACKERMAN_SIZE = 58;

	/**
	 * This will create a bullet moving upwards
	 * 
	 * @author Tom Yuming Lu
	 * 
	 * @version 1.0
	 * 
	 * @since 2018-07-07
	 * 
	 * @param x starting x position
	 * 
	 * @param y starting y position
	 * 
	 * @param color color of bullet
	 * 
	 * @param level level of bullet
	 */
	public Bullets(int x, int y, Color color, int level) {// normal bullets
		super(x, y - 20, color);
		this.level = level;
		this.mid = mid;
	}
	
	public Bullets(int x, int y, Color color, int level, int mid) {// bullets the go sideways
		super(x, y - 20, color);
		if (mid > 0) {
			super.x -= HACKERMAN_SIZE / 2;
		} else if (mid < 0) {
			super.x += HACKERMAN_SIZE / 2;
		}

		this.level = level;
		this.mid = mid;
	}

	@Override
	public void update(GameContainer panel) {
		if (mid == 0) {
			y -= 15;
		} else if (mid < 0) {// sideways right
			y -= 15;
			x += 5;
		} else if (mid > 0) {// sideways left
			y -= 15;
			x -= 5;
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(super.color);
		g.setFont(new Font(null, Font.PLAIN, 30));
		if (level == 1) {
			g.drawString("/", (HACKERMAN_SIZE - g.getFontMetrics().stringWidth("/")) / 2 + x, y);
			rectangle[0] = (HACKERMAN_SIZE - g.getFontMetrics().stringWidth("/")) / 2 + x;
			rectangle[1] = y - g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();
			rectangle[2] = g.getFontMetrics().stringWidth("/");
			rectangle[3] = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
//			Graphics2D g2 = (Graphics2D) g; // HitBox
//			g2.draw(new Rectangle2D.Double(rectangle[0], rectangle[1], rectangle[2], rectangle[3]));
		} else if (level == 2) {
			g.drawString("//", (HACKERMAN_SIZE - g.getFontMetrics().stringWidth("//")) / 2 + x, y);
			rectangle[0] = (HACKERMAN_SIZE - g.getFontMetrics().stringWidth("//")) / 2 + x;
			rectangle[1] = y - g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();
			rectangle[2] = g.getFontMetrics().stringWidth("//");
			rectangle[3] = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
//			Graphics2D g2 = (Graphics2D) g; // HitBox
//			g2.draw(new Rectangle2D.Double(rectangle[0], rectangle[1], rectangle[2], rectangle[3]));
		} else if (level == 3) {
			g.drawString("/***/", (HACKERMAN_SIZE - g.getFontMetrics().stringWidth("/***/")) / 2 + x, y);
			rectangle[0] = (HACKERMAN_SIZE - g.getFontMetrics().stringWidth("/***/")) / 2 + x;
			rectangle[1] = y - g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();
			rectangle[2] = g.getFontMetrics().stringWidth("/***/");
			rectangle[3] = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
//			Graphics2D g2 = (Graphics2D) g; // HitBox
//			g2.draw(new Rectangle2D.Double(rectangle[0], rectangle[1], rectangle[2], rectangle[3]));
		} else if (level == 4) {
			g.drawString("/*****/", (HACKERMAN_SIZE - g.getFontMetrics().stringWidth("/*****/")) / 2 + x, y);
			rectangle[0] = (HACKERMAN_SIZE - g.getFontMetrics().stringWidth("/*****/")) / 2 + x;
			rectangle[1] = y - g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();
			rectangle[2] = g.getFontMetrics().stringWidth("/*****/");
			rectangle[3] = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
//			Graphics2D g2 = (Graphics2D) g; // HitBox
//			g2.draw(new Rectangle2D.Double(rectangle[0], rectangle[1], rectangle[2], rectangle[3]));
		} else if (level == 5){
			if (mid == 0) {
				g.drawString("/*****/", (HACKERMAN_SIZE - g.getFontMetrics().stringWidth("/*****/")) / 2 + x, y);
				rectangle[0] = (HACKERMAN_SIZE - g.getFontMetrics().stringWidth("/*****/")) / 2 + x;
				rectangle[1] = y - g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();
				rectangle[2] = g.getFontMetrics().stringWidth("/*****/");
				rectangle[3] = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
			} else if (mid < 0) {
				g.drawString("//", (HACKERMAN_SIZE - g.getFontMetrics().stringWidth("//")) / 2 + x, y);
				rectangle[0] = (HACKERMAN_SIZE - g.getFontMetrics().stringWidth("//")) / 2 + x;
				rectangle[1] = y - g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();
				rectangle[2] = g.getFontMetrics().stringWidth("//");
				rectangle[3] = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
			} else if (mid > 0) {
				g.drawString("//", (HACKERMAN_SIZE - g.getFontMetrics().stringWidth("//")) / 2 + x, y);
				rectangle[0] = (HACKERMAN_SIZE - g.getFontMetrics().stringWidth("//")) / 2 + x;
				rectangle[1] = y - g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();
				rectangle[2] = g.getFontMetrics().stringWidth("//");
				rectangle[3] = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
			}
		}
	}

	/**
	 * This will return the hitbox of the bullet
	 * 
	 * @return returns hitbox
	 */
	public Rectangle2D getHitBox() {
		return new Rectangle2D.Double(rectangle[0], rectangle[1], rectangle[2], rectangle[3]);
	}

	/**
	 * This will get the level of the bullet
	 * 
	 * @return returns level of the bullet
	 */
	public int getLevel() {
		return level;
	}

}
