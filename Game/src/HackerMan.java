import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class HackerMan extends GameObjects {

	int level;
	int speed = 10;
	private int[] rectangle = new int[4];// Hitbox
	int health = 3;
	boolean hit = false;
	long invulnerability;
	long invulnerabilityTimer;
	int bombCount = 3;

	public HackerMan(int x, int y, Color color, int level) {
		super(x, y, color);
		this.level = level;
	}

	@Override
	public void update(GameContainer panel) {// Movement
		if (panel.getInput().isKey(KeyEvent.VK_LEFT) || panel.getInput().isKey(KeyEvent.VK_A)) {
			super.x -= speed;
			if (super.x < 0) {
				super.x = 0;
			}
		}
		if (panel.getInput().isKey(KeyEvent.VK_RIGHT) || panel.getInput().isKey(KeyEvent.VK_D)) {
			super.x += speed;
			if (super.x > panel.getWidth() - 58) {
				super.x = panel.getWidth() - 58;
			}
		}
		if (panel.getInput().isKey(KeyEvent.VK_DOWN) || panel.getInput().isKey(KeyEvent.VK_S)) {
			super.y += speed;
			if (super.y > panel.getHeight()) {
				super.y = panel.getHeight();
			}
		}
		if (panel.getInput().isKey(KeyEvent.VK_UP) || panel.getInput().isKey(KeyEvent.VK_W)) {
			super.y -= speed;
			if (super.y < 25) {
				super.y = 25;
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(color);
		g.setFont(new Font(null, Font.PLAIN, 30));
		if (hit && System.currentTimeMillis() < invulnerability + invulnerabilityTimer) {// Flashes during invulnerability
			if (System.currentTimeMillis() % 200 < 100) {
				g.drawString("Man", super.x, super.y);
			}
		} else if (hit) {// Stops flashing
			hit = false;
			g.drawString("Man", super.x, super.y);
		} else {// Normal
			g.drawString("Man", super.x, super.y);
		}
		rectangle[0] = x;
		rectangle[1] = y - g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();
		rectangle[2] = g.getFontMetrics().stringWidth("Man");
		rectangle[3] = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		if (level > 5) {
			return;
		}
		this.level = level;
	}

	public boolean hit(EnemyBullets bullet) {
		return bullet.getHitBox().intersects(this.getHitBox());
	}

	public boolean hit(Enemy enemy) {
		return enemy.getHitBox().intersects(this.getHitBox());
	}

	public boolean hit(PowerUp powerUp) {
		return powerUp.getHitBox().intersects(this.getHitBox());
	}

	public Rectangle2D getHitBox() {
		return new Rectangle2D.Double(rectangle[0], rectangle[1], rectangle[2], rectangle[3]);
	}

	public int getHealth() {
		return health;
	}

	public void invulnerability() {
		invulnerabilityTimer = 10000;
		invulnerability = System.currentTimeMillis();
		hit = true;
	}

	public void setHealth(int health) {
		if (hit) {// doesn't reduce health during Invulnerability
			return;
		}
		if (health > this.health) {
			this.health = health;
			return;
		}
		invulnerabilityTimer = 5000;
		System.out.println(health);
		this.health = health;
		invulnerability = System.currentTimeMillis();
		hit = true;
	}
}
