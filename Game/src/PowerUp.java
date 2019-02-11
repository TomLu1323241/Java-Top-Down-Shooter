import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PowerUp extends GameObjects {

	PowerUpType type;
	int originalx;
	int count = 0;
	boolean start = false;
	
	public void start (int x, int y, PowerUpType type) {// spawns it into a location
		super.x = x;
		super.y = y;
		this.type = type;
		originalx = x;
		start = true;
	}
	
	PowerUp() {// starts off screen
		super(-100, -100, Color.black);
	}

	@Override
	public void update(GameContainer panel) {// wiggels down
		if (!start) {
			return;
		}
		super.y += count % 2;
		super.x = (int) (60 * Math.sin(0.03 * ((double) super.y)) + originalx);
		count++;
	}

	@Override
	public void paintComponent(Graphics g) throws IOException {// Draws images depending on what power up
		if (!start) {
			return;
		}
		Graphics2D g2 = (Graphics2D) g; // HitBox
		Color transparentColor = new Color(0, 0, 0, 0);
		g2.setColor(transparentColor);
		// Draw image
		BufferedImage powerUpImage = null;
		switch (type) {
		case Bullet:
			powerUpImage = ImageIO.read(new File("assets/Coffee" + ((count / 20) % 4 + 1) + ".png"));
			break;
		case Health:
			powerUpImage = ImageIO.read(new File("assets/Heart" + ((count / 20) % 8 + 1) + ".png"));
			break;
		case Invulnerability:
			powerUpImage = ImageIO.read(new File("assets/Star" + ((count / 20) % 8 + 1) + ".png"));
			break;
		case Bomb:
			powerUpImage = ImageIO.read(new File("assets/Bomb.png"));
			break;
		default:
			break;
		}
		g2.drawImage(powerUpImage, x, y, null);
	}
	
    public Rectangle2D getHitBox() {
        return new Rectangle2D.Double(x, y , 20, 20);
    }
}