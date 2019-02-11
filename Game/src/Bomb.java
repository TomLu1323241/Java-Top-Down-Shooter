import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

public class Bomb extends GameObjects {

	int frameWidth;
	boolean first = true;
	String bomb = "/**";
	boolean start = false;
	int startPos;
	boolean temp = false;

	public Bomb(int x, int y, Color color, int frameWidth) {// will start at the top of the screen and will be out of view unless something happens
		super(x, y, color);
		this.frameWidth = frameWidth;
		startPos = y;
	}

	@Override
	public void update(GameContainer panel) {
		if ((panel.getInput().isKey(KeyEvent.VK_B) || start)) {// starts the bomb
			if (panel.getInput().isKey(KeyEvent.VK_B) && !start) {
				if (panel.hackerman.bombCount <= 0) {
					return;
				}
				panel.hackerman.bombCount = panel.hackerman.bombCount - 1;
				System.out.println(panel.hackerman.bombCount);
			}
			y -= 4;
			start = true;
			if (y < -100) {
				y = startPos;
				start = false;
			}
		}
	}

	public Rectangle2D getHitBox() {
		return new Rectangle2D.Double(x, y - 20, frameWidth, 20);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setFont(new Font(null, Font.PLAIN, 30));
		g.setColor(color);
		if (first) { // dynamically determines size of bomb
			first = false;
			while (true) {
				if (g.getFontMetrics().stringWidth(bomb + "****/") >= frameWidth) {
					bomb += "/";
					break;
				}
				bomb += "*";
			}
		}
		g.drawString(bomb, (frameWidth - g.getFontMetrics().stringWidth(bomb)) / 2 + x - 2, y);
	}
}
