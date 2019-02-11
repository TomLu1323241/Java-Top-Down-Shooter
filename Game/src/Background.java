import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Background extends GameObjects{
	
	int number;
	String text;
	String output ="";

	public Background(int x, int y, Color color, int number, String text) {
		super(x, y, color);
		this.number = number;
		this.text = text;
	}

	@Override
	public void update(GameContainer panel) {// Moves slowly down
		super.y += 2;
		String temp = "";
		if (String.valueOf(number).length() > 3 && Math.random() < 0.1) {
			for (int i = 0; i < 6; i++) {
				temp += panel.getRandom()[(int)(Math.random() * panel.getRandom().length)];
			}
			output = temp;
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setFont(new Font(null, Font.PLAIN, 18));
		g.setColor(color);
		if (String.valueOf(number).length() > 3) {
			g.drawString("" + output, 30 - g.getFontMetrics().stringWidth(output), y);// Number and text
		} else {
			g.drawString("" + number, 30 - String.valueOf(number).length() * 10, y);// "GLitches" and text
		}
		g.setColor(Color.decode("#48494C"));
		g.drawString(text, 40, y);
	}

}
