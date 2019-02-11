import java.awt.*;
import java.io.IOException;

public abstract class GameObjects {

	protected int x;
	protected int y;
	protected Color color;

	/**
	 * @param x
	 *            Starting x value
	 * @param y
	 *            Starting y value
	 * @param color
	 */
	public GameObjects(int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public abstract void update(GameContainer panel);

	public abstract void paintComponent(Graphics g) throws IOException;

}