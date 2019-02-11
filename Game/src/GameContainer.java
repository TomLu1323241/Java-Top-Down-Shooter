
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GameContainer extends JPanel implements Runnable {

	private ArrayList<String> text = new ArrayList<>();
	private ArrayList<GameObjects> enemies = new ArrayList<>();
	private ArrayList<GameObjects> enemyBullets = new ArrayList<>();
	private ArrayList<GameObjects> setting = new ArrayList<>();
	protected HackerMan hackerman = new HackerMan(284, 800, Color.ORANGE, 1);
	private ArrayList<GameObjects> bullets = new ArrayList<>();
	private ArrayList<ArrayList<GameObjects>> gameObjects = new ArrayList<>();
	private Bomb bomb;
	private boolean runMenu = true;
	private boolean runGame = false;
	private boolean runEnd = false;
	private Input input;
	private PowerUp powerUp;
	// Ricky: Added this
	private int level = 3;
	private int score = 0;
	private boolean gameEnded = false;

	private enum State {
		Menu, Game, End
	};

	private State state = State.Menu;

	private String[] random = { "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "-", "+", "=", "[", "{", "]",
			"}", "\\", "|", ":", ";", "\'", "\"", "<", ",", ">", ".", "?", "/" };// used to generate "glitchy" feeling after 999

	public void read(ArrayList<String> list, String path) throws IOException {// reads the java files used and puts into background
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		String st;
		while ((st = br.readLine()) != null) {
			if (st.contains("\t")) {
				st = st.replaceAll("\t", "        ");
			}
			list.add(st);
		}
	}

	public GameContainer() throws Exception {
		// reads all the files
		read(text, "src/Background.java");
		read(text, "src/Bomb.java");
		read(text, "src/Bullets.java");
		read(text, "src/Enemy.java");
		read(text, "src/EnemyBullets.java");
		read(text, "src/GameObjects.java");
		read(text, "src/HackerMan.java");
		read(text, "src/Input.java");
		read(text, "src/Pattern.java");
		read(text, "src/PowerUpType.java");
		read(text, "src/PowerUp.java");
		read(text, "src/GameContainer.java");
		// uses polymophisum
		gameObjects.add(setting);
		gameObjects.add(enemies);
		gameObjects.add(enemyBullets);
		gameObjects.add(bullets);

		JFrame frame = new JFrame();
		input = new Input();

		frame.setVisible(true);
		frame.setSize(720, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.addKeyListener(input);// inputs
		frame.addMouseListener(input);
		frame.addMouseMotionListener(input);
		addKeyListener(input);
		addMouseListener(input);
		addMouseMotionListener(input);
		setPreferredSize(new Dimension(720, 1000));
		setBackground(Color.decode("#292929"));
		setFocusable(true);
		frame.add(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		bomb = new Bomb(0, frame.getHeight(), Color.decode("#626262"), frame.getWidth());
		powerUp = new PowerUp();
		start();
	}

	public void start() {
		Thread thread = new Thread(this);
		runGame = true;
		gameEnded = false;
		thread.start();
	}

	public void stop() {
		runGame = false;
	}

	public void run() {
		while (true) {
			if (state == State.Menu) {// Menu
				while (runMenu) {
					if (input.clicked) {// Check click location
						if (20 < input.location()[0] && input.location()[0] < 20 + (getWidth() - 3 * 20) / 2 &&
								250 < input.location()[1] && input.location()[1] < 250 + (getWidth() - 3 * 20) / 2) {// Easy Mode
							level = 1;
							runMenu = false;
							runGame = true;
							state = State.Game;
						} else if (20 * 2 + (getWidth() - 3 * 20) / 2 < input.location()[0] && 
								input.location()[0] < 20 * 2 + (getWidth() - 3 * 20) / 2 + 20 + (getWidth() - 3 * 20) / 2 &&
								250 < input.location()[1] && 
								input.location()[1] < 250 + (getWidth() - 3 * 20) / 2){// Medium Mode
							level = 2;
							runMenu = false;
							runGame = true;
							state = State.Game;
						} else if (getWidth() / 2 - (getWidth() - 3 * 20) / 4 < input.location()[0] && 
								input.location()[0] < getWidth() / 2 - (getWidth() - 3 * 20) / 4 + (getWidth() - 3 * 20) / 2 &&
								250 + 20 + (getWidth() - 3 * 20) / 2 < input.location()[1] && 
								input.location()[1] < 250 + 20 + (getWidth() - 3 * 20) / 2 + (getWidth() - 3 * 20) / 2) {// Hard Mode
							level = 3;
							runMenu = false;
							runGame = true;
							state = State.Game;
						}
					}
					input.clicked = false;// reset click
					repaint();
					try {
						Thread.sleep(17);
					} catch (Exception e) {
					}
				}
			} else if (state == State.Game) {// Game
				int fire = 6;
				int line = 50;
				int codeLine = 50;
				long loseLifeSound = 0;
				long invincibilitySound = 0;
				// reset everything
				while (!enemies.isEmpty()) {
					enemies.remove(0);
				}
				while (!enemyBullets.isEmpty()) {
					enemyBullets.remove(0);
				}
				while (!setting.isEmpty()) {
					setting.remove(0);
				}
				while (!bullets.isEmpty()) {
					bullets.remove(0);
				}
				hackerman = new HackerMan(284, 800, Color.ORANGE, 1);
				score = 0;
				for (int i = 0; i < 50; i++) {// Create Background
					setting.add(new Background(0, -i * 22, Color.decode("#657B83"), i, text.get(text.size() - i - 1)));
				}
				while (runGame) {
					// Updates
					hackerman.update(this);
					powerUp.update(this);
					for (ArrayList<GameObjects> i : gameObjects) {
						for (GameObjects i2 : i) {
							i2.update(this);
						}
					}
					bomb.update(this);
					powerUp.update(this);
					repaint();
					// Firing bullets
					fire++;
					if (fire >= 9 && input.isKey(KeyEvent.VK_SPACE)) {
						fire = 0;
						if (hackerman.level == 5) {
							bullets.add(new Bullets(hackerman.x, hackerman.y, Color.decode("#626262"),
									hackerman.getLevel()));
							bullets.add(new Bullets(hackerman.x, hackerman.y, Color.decode("#626262"),
									hackerman.getLevel(), 1));
							bullets.add(new Bullets(hackerman.x, hackerman.y, Color.decode("#626262"),
									hackerman.getLevel(), -1));
						} else {
							bullets.add(new Bullets(hackerman.x, hackerman.y, Color.decode("#626262"),
									hackerman.getLevel()));
						}
						playAudio("laser");
					}

					// Clean up stuff
					while (bullets.size() > 50) {
						bullets.remove(0);
					}
					while (enemyBullets.size() > 100) {
						enemyBullets.remove(0);
					}
					while (enemies.size() > 50) {
						enemies.remove(0);
					}

					// Side numbers and bar
					if (setting.get(0).getY() > this.getHeight() + 100) {
						setting.remove(0);
						if (codeLine == text.size() - 1) {
							codeLine = 0;
						}
						setting.add(new Background(0, 6, Color.decode("#657B83"), line++,
								text.get(text.size() - codeLine++ - 1)));
						// Level 1: Strings, booleans, Printing Strings, ints,
						// while
						// loops
						if (level == 1) {
							switch (line) {
							case 51:
								spawnGroupA(300);
								break;
							case 80:
								spawnGroupA(400);
								break;
							case 110:
								spawnGroupA(500);
								spawnGroupA(300);
								break;
							case 140:
								spawnGroupA(600);
								spawnGroupC(500);
								break;
							case 170:
								spawnGroupB(200);
								break;
							case 180:
								spawnGroupB(300);
								spawnGroupA(400);
								break;
							case 186:
								spawnGroupB(500);
								spawnGroupB(400);
								spawnGroupC(450);
								break;
							case 200:
								spawnGroupA(600);
								spawnGroupC(100);
								spawnGroupC(250);
								break;
							case 210:
								spawnGroupD(500, 2);
								break;
							case 220:
								spawnGroupD(700, 2);
								spawnGroupD(600, 3);
								spawnGroupD(500, 4);
								break;
							case 240:
								spawnGroupA(300);
								spawnGroupB(500);
								spawnGroupC(400);
								break;
							case 260:
								spawnGroupC(300);
								break;
							case 265:
								spawnGroupC(400);
								break;
							case 270:
								spawnGroupC(500);
								break;
							case 280:
								spawnGroupA(600);
								spawnGroupB(700);
								break;
							case 300:
								miniBossA(200, true);
								break;
							default:
								break;
							}
						} else if (level == 2) {
							switch (line) {
							case 51:
								spawnGroupA(300);
								break;
							case 80:
								spawnGroupA(400);
								break;
							case 110:
								spawnGroupA(500);
								spawnGroupC(300);
								break;
							case 140:
								spawnGroupA(600);
								spawnGroupC(500);
								break;
							case 150:
								spawnGroupA(600);
								spawnGroupC(500);
								break;
							case 170:
								spawnGroupF(200);
								break;
							case 180:
								spawnGroupB(300);
								spawnGroupA(400);
								spawnGroupD(500, 3);
								break;
							case 186:
								spawnGroupB(500);
								spawnGroupB(400);
								spawnGroupC(450);
								spawnGroupA(550);
								break;
							case 200:
								spawnGroupA(600);
								spawnGroupC(100);
								spawnGroupC(250);
								break;
							case 210:
								spawnGroupD(500, 2);
								break;
							case 220:
								spawnGroupD(700, 2);
								spawnGroupD(600, 3);
								spawnGroupD(500, 4);
								break;
							case 240:
								spawnGroupA(300);
								spawnGroupB(500);
								spawnGroupC(400);
								break;
							case 260:
								spawnGroupC(300);
								break;
							case 265:
								spawnGroupG(400);
								break;
							case 270:
								spawnGroupC(500);
								break;
							case 280:
								spawnGroupE(600);
								spawnGroupB(700);
								break;
							case 300:
								miniBossA(200, false);
								break;
							case 350:
								spawnGroupA(300);
								break;
							case 370:
								spawnGroupE(400);
								break;
							case 390:
								spawnGroupA(300);
								spawnGroupC(250);
								break;
							case 415:
								spawnGroupC(600);
								node(300);
								break;
							case 420:
								node(300);
								break;
							case 425:
								node(300);
								break;
							case 430:
								node(300);
								break;
							case 435:
								node(300);
								break;
							case 440:
								node(300);
								spawnGroupH(500, 1);
								break;
							case 445:
								node(300);
								break;
							case 450:
								node(300);
								spawnGroupC(200);
								break;
							case 455:
								node(300);
								break;
							case 460:
								node(300);
								spawnGroupG(300);
								break;
							case 480:
								spawnGroupF(500);
								break;
							case 500:
								spawnGroupE(200);
								break;
							case 510:
								spawnGroupA(200);
								spawnGroupD(400, 1);
								spawnGroupC(300);
								break;
							case 540:
								miniBossA(200, false);
								break;
							case 550:
								miniBossA(200, false);
								break;
							case 560:
								miniBossA(200, false);
								break;
							case 600:
								miniBossB(100, false);
								break;
							case 610:
								miniBossB(100, false);
								break;
							case 620:
								miniBossB(100, false);
								break;
							case 630:
								miniBossB(100, false);
								break;
							case 640:
								miniBossB(100, false);
								break;
							case 650:
								miniBossB(100, false);
								break;
							case 660:
								miniBossB(100, false);
								miniBossC(300, true);
								break;
							case 670:
								miniBossB(100, false);
								break;
							case 680:
								miniBossB(100, false);
								break;
							case 690:
								miniBossB(100, false);
								break;

							default:
								break;
							}

						}
						if (level == 3) {
							switch (line) {
							case 51:
								spawnGroupE(300);
								break;
							case 80:
								spawnGroupE(400);
								break;
							case 110:
								spawnGroupE(500);
								spawnGroupG(300);
								break;
							case 140:
								spawnGroupE(600);
								spawnGroupG(500);
								break;
							case 150:
								spawnGroupE(600);
								spawnGroupG(500);
								break;
							case 170:
								spawnGroupF(200);
								spawnGroupF(400);
								break;
							case 180:
								spawnGroupF(300);
								spawnGroupE(400);
								spawnGroupH(500, 3);
								break;
							case 186:
								spawnGroupF(500);
								spawnGroupF(400);
								spawnGroupG(450);
								spawnGroupE(550);
								break;
							case 200:
								spawnGroupE(600);
								spawnGroupG(100);
								spawnGroupG(250);
								break;
							case 210:
								spawnGroupH(500, 2);
								break;
							case 220:
								spawnGroupH(700, 2);
								spawnGroupH(600, 3);
								spawnGroupH(500, 4);
								break;
							case 240:
								spawnGroupE(300);
								spawnGroupF(500);
								spawnGroupG(400);
								break;
							case 260:
								spawnGroupG(300);
								break;
							case 265:
								spawnGroupG(400);
								break;
							case 270:
								spawnGroupG(500);
								spawnGroupE(550);
								break;
							case 280:
								spawnGroupE(600);
								spawnGroupF(700);
								break;
							case 300:
								miniBossA(200, false);
								break;
							case 350:
								spawnGroupE(300);
								break;
							case 370:
								spawnGroupE(400);
								break;
							case 390:
								spawnGroupE(300);
								spawnGroupG(250);
								break;
							case 415:
								spawnGroupG(600);
								brokenNode(300);
								break;
							case 420:
								brokenNode(300);
								break;
							case 425:
								brokenNode(300);
								break;
							case 430:
								brokenNode(300);
								break;
							case 435:
								brokenNode(300);
								break;
							case 440:
								brokenNode(300);
								spawnGroupH(500, 1);
								break;
							case 445:
								brokenNode(300);
								break;
							case 450:
								brokenNode(300);
								spawnGroupG(200);
								break;
							case 455:
								brokenNode(300);
								break;
							case 460:
								brokenNode(300);
								spawnGroupG(300);
								break;
							case 480:
								spawnGroupF(500);
								break;
							case 500:
								spawnGroupE(200);
								break;
							case 510:
								spawnGroupE(200);
								spawnGroupH(400, 1);
								spawnGroupG(300);
								break;
							case 540:
								miniBossA(200, false);
								break;
							case 550:
								miniBossA(200, false);
								break;
							case 560:
								miniBossA(200, false);
								break;
							case 600:
								miniBossD(100, false);
								break;
							case 610:
								miniBossD(100, false);
								break;
							case 620:
								miniBossD(100, false);
								break;
							case 630:
								miniBossD(100, false);
								break;
							case 640:
								miniBossD(100, false);
								break;
							case 650:
								miniBossD(100, false);
								break;
							case 660:
								miniBossD(100, false);
								miniBossE(300, true);
								break;
							case 670:
								miniBossD(100, false);
								break;
							case 680:
								miniBossD(100, false);
								break;
							case 690:
								miniBossD(100, false);
								break;

							}
						}
					}

					// Hit Reg
					// Enemies hit by bullets
					for (int i = 0; i < bullets.size(); i++) {
						for (int i2 = 0; i2 < enemies.size(); i2++) {
							if (((Enemy) enemies.get(i2)).hit(((Bullets) bullets.get(i)))) {
								((Enemy) enemies.get(i2)).setHealth(((Bullets) bullets.get(i)).getLevel());
								bullets.remove(i);
								if (((Enemy) enemies.get(i2)).getHealth() <= 0) {
									score += ((Enemy) enemies.get(i2)).getScore();
									gameEnded = ((Enemy) enemies.get(i2)).getKillInfo();
									System.out.println(score);
									if (Math.random() * 25 < line % 25
											&& (!powerUp.start || powerUp.y > this.getHeight())) {// Spawns Power Ups
										powerUp.start(((Enemy) enemies.get(i2)).x, ((Enemy) enemies.get(i2)).y,
												PowerUpType.values()[(int) (Math.random() * 4)]);
									}
									enemies.remove(i2);
								}
								playAudio("hitenemy");
								break;
							}
						}
					}
					// Enemey bullets hit by bomb
					for (int i = 0; i < enemyBullets.size(); i++) {
						if (((EnemyBullets) enemyBullets.get(i)).hit(bomb)) {
							enemyBullets.remove(i);
						}
					}
					// Enemies hit by bomb
					for (int i = 0; i < enemies.size(); i++) {
						if (((Enemy) enemies.get(i)).hit(bomb)) {
							((Enemy) enemies.get(i)).setHealth(((Enemy) enemies.get(i)).getHealth() - 30);
							if (((Enemy) enemies.get(i)).getHealth() <= 0) {
								enemies.remove(i);
							}
						}
					}
					// Hackerman hit by enemies bullets
					for (int i = 0; i < enemyBullets.size(); i++) {
						if (hackerman.hit((EnemyBullets) enemyBullets.get(i))) {
							hackerman.setHealth(hackerman.getHealth() - 1);
							if (loseLifeSound == 0 || loseLifeSound < System.currentTimeMillis()) {
								loseLifeSound = System.currentTimeMillis() + 5000;
								playAudio("loselife");
							}
							enemyBullets.remove(i);
							if (hackerman.getHealth() == 0) {// game over
								JOptionPane.showMessageDialog(null, "Java ran out of memory");
								System.exit(0);
							}
						}
					}
					// Hackerman Hit by enemies
					for (int i = 0; i < enemies.size(); i++) {// Enemies
						if (hackerman.hit((Enemy) enemies.get(i))) {
							hackerman.setHealth(hackerman.getHealth() - 1);
							enemies.remove(i);
							if (loseLifeSound == 0 || loseLifeSound < System.currentTimeMillis()) {
								loseLifeSound = System.currentTimeMillis() + 5000;
								playAudio("loselife");
							}
							if (hackerman.getHealth() == 0) {// game over
								JOptionPane.showMessageDialog(null, "Java ran out of memory");
								System.exit(0);
							}
						}
					}
					// Power up Collection
					if (hackerman.hit(powerUp)) {
						switch (powerUp.type) {
						case Bomb:
							hackerman.bombCount++;
							playAudio("powerup");
							break;
						case Bullet:
							hackerman.setLevel(hackerman.getLevel() + 1);
							playAudio("powerup");
							break;
						case Health:
							hackerman.setHealth(hackerman.getHealth() + 1);
							playAudio("powerup");
							break;
						case Invulnerability:
							hackerman.invulnerability();
							if (invincibilitySound == 0 || invincibilitySound < System.currentTimeMillis()) {
								invincibilitySound = System.currentTimeMillis() + 10000;
								playAudio("invincibility10");
							}
							break;
						default:
							break;

						}
						powerUp.start = false;
						powerUp.x = -100;
						powerUp.y = -100;
					}
					// EnemyBullet Generation
					for (int i = 0; i < enemies.size(); i++) {
						if (((Enemy) enemies.get(i)).shoot()) {
							if (((Enemy) enemies.get(i)).getBullet() == Pattern.Single) {
								enemyBullets.add(
										new EnemyBullets((int) (enemies.get(i).x + ((Enemy) enemies.get(i)).width / 2),
												enemies.get(i).y, Color.pink, 6, hackerman.x + 152 / 2, hackerman.y,
												((Enemy) enemies.get(i)).getBullet(), 0.0));
							} else if (((Enemy) enemies.get(i)).getBullet() == Pattern.Double) {
								enemyBullets.add(new EnemyBullets(
										(int) (enemies.get(i).x + 10 + ((Enemy) enemies.get(i)).width / 2),
										enemies.get(i).y, Color.pink, 8, hackerman.x + 152 / 2, hackerman.y,
										((Enemy) enemies.get(i)).getBullet(), 0));
								enemyBullets.add(new EnemyBullets(
										(int) (enemies.get(i).x + -10 + ((Enemy) enemies.get(i)).width / 2),
										enemies.get(i).y, Color.pink, 8, hackerman.x + 152 / 2, hackerman.y,
										((Enemy) enemies.get(i)).getBullet(), 0));
							} else if (((Enemy) enemies.get(i)).getBullet() == Pattern.Spread) {
								enemyBullets.add(
										new EnemyBullets((int) (enemies.get(i).x + ((Enemy) enemies.get(i)).width / 2),
												enemies.get(i).y, Color.pink, 6, hackerman.x + 152 / 2, hackerman.y,
												((Enemy) enemies.get(i)).getBullet(), 3));
								enemyBullets.add(
										new EnemyBullets((int) (enemies.get(i).x + ((Enemy) enemies.get(i)).width / 2),
												enemies.get(i).y, Color.pink, 6, hackerman.x + 152 / 2, hackerman.y,
												((Enemy) enemies.get(i)).getBullet(), 0));
								enemyBullets.add(
										new EnemyBullets((int) (enemies.get(i).x + ((Enemy) enemies.get(i)).width / 2),
												enemies.get(i).y, Color.pink, 6, hackerman.x + 152 / 2, hackerman.y,
												((Enemy) enemies.get(i)).getBullet(), -3));
							} else if (((Enemy) enemies.get(i)).getBullet() == Pattern.Track) {
								enemyBullets.add(
										new EnemyBullets((int) (enemies.get(i).x + ((Enemy) enemies.get(i)).width / 2),
												enemies.get(i).y, Color.pink, 6, hackerman.x + 152 / 2, hackerman.y,
												((Enemy) enemies.get(i)).getBullet(), 0));
							} else {
								enemyBullets.add(new EnemyBullets(
										(int) (enemies.get(i).x + 10 + ((Enemy) enemies.get(i)).width / 2),
										enemies.get(i).y, Color.pink, 8, hackerman.x + 152 / 2, hackerman.y,
										((Enemy) enemies.get(i)).getBullet(), 0));
							}
						}
					}
					if (gameEnded) {// chamges State
						System.out.println("CONGRATS! YOU WON!");
						gameEnded = false;
						state = state.End;
						runGame = false;
						runEnd = true;
					}

					try {
						Thread.sleep(17);
					} catch (Exception e) {
					}
				}
			} else if (state == State.End) {// End Game
				while (runEnd) {
					if (input.clicked) {// Check Click
						if (75 < input.location()[0] && input.location()[0] < 75 + (getWidth() - 75 * 2) &&
								400 < input.location()[1] && input.location()[1] < 400 + (getWidth() - 75 * 2)) {
							level = 1;
							runMenu = true;// Change State
							runEnd = false;
							state = State.Menu;
						}
					}
					input.clicked = false;
					repaint();
					try {
						Thread.sleep(17);
					} catch (Exception e) {
					}
				}
			}
		}
	}
	
	public void playAudio(String name) {
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("assets/" + name + ".wav"));
         	Clip clip = AudioSystem.getClip();
         	clip.open(audioIn);
         	clip.start(); 
		}
		catch (Exception e) {
		}
	}

	public void paintComponent(Graphics g) {
		if (state == State.Menu) {
			super.paintComponent(g);
			g.setFont(new Font(null, Font.PLAIN, 50));
			g.setColor(Color.WHITE);
			g.drawString("Level Selection", (getWidth() - g.getFontMetrics().stringWidth("Level Selection")) / 2, 200);
			g.setColor(Color.decode("#657B83"));
			g.fillRect(20, 250, (getWidth() - 3 * 20) / 2, (getWidth() - 3 * 20) / 2);// Draws buttons
			g.fillRect(20 * 2 + (getWidth() - 3 * 20) / 2, 250, (getWidth() - 3 * 20) / 2, (getWidth() - 3 * 20) / 2);
			g.fillRect(getWidth() / 2 - (getWidth() - 3 * 20) / 4, 250 + 20 + (getWidth() - 3 * 20) / 2, (getWidth() - 3 * 20) / 2, (getWidth() - 3 * 20) / 2);
			g.setColor(Color.decode("#292929"));
			g.fillRect(20 + 20, 250 + 20, (getWidth() - 3 * 20) / 2 - 40, (getWidth() - 3 * 20) / 2 - 40);
			g.fillRect(20 * 2 + (getWidth() - 3 * 20) / 2 + 20, 250 + 20, (getWidth() - 3 * 20) / 2 - 40, (getWidth() - 3 * 20) / 2 - 40);
			g.fillRect(getWidth() / 2 - (getWidth() - 3 * 20) / 4 + 20, 250 + 20 + (getWidth() - 3 * 20) / 2 + 20, (getWidth() - 3 * 20) / 2 - 40, (getWidth() - 3 * 20) / 2 - 40);
			g.setColor(Color.GREEN);// Draw Easy, Medium, Hard
			g.drawString("Easy", 130, 430);
			g.setColor(Color.YELLOW);
			g.drawString("Medium", 455, 430);
			g.setColor(Color.RED);
			g.drawString("HARDCORE", 218, 790);
		} else if (state == State.Game) {
			try {// Draw Everything
				super.paintComponent(g);
				for (ArrayList<GameObjects> i : gameObjects) {
					for (GameObjects i2 : i) {
						i2.paintComponent(g);
					}
				}
				bomb.paintComponent(g);
				hackerman.paintComponent(g);
				// hitler.paintComponent(g);
				powerUp.paintComponent(g);
				g.setFont(new Font(null, Font.PLAIN, 30));
				g.setColor(Color.GREEN);
				g.drawString("Score: " + score, 5, 29);
				g.drawString("Health: " + hackerman.getHealth(), 5, getHeight() - 5);
				g.drawString("Bombs: " + hackerman.bombCount, getWidth() - 132, getHeight() - 5);
				if (System.currentTimeMillis() < hackerman.invulnerability + hackerman.invulnerabilityTimer) {
					g.setColor(Color.RED);
					if (System.currentTimeMillis() % 200 < 100) {
						g.drawString("" + (hackerman.invulnerability + hackerman.invulnerabilityTimer
								- System.currentTimeMillis()), getWidth() - 70, 24);
					}
				}
			} catch (Exception e) {
			}
		} else if (state == State.End) {
			super.paintComponent(g);
			g.setFont(new Font(null, Font.PLAIN, 50));
			g.setColor(Color.WHITE);
			g.drawString("GG YOU WON!!!", (getWidth() - g.getFontMetrics().stringWidth("GG YOU WON!!!")) / 2, 200);
			g.drawString("Final Score:" + score, (getWidth() - g.getFontMetrics().stringWidth("Final Score:" + score)) / 2, 250);
			g.setColor(Color.decode("#657B83"));// Draw Button
			g.fillRect(75, 400, (getWidth() - 75 * 2) , (getWidth() - 75 * 2));
			g.setColor(Color.decode("#292929"));
			g.fillRect(100, 425, (getWidth() - 75 * 2 - 50) , (getWidth() - 75 * 2 - 50));
			g.setColor(Color.CYAN);
			g.setFont(new Font(Font.SERIF, Font.PLAIN, 70));
			g.drawString("Play Again?", 200, 700);
		}
	}

	public static void main(String[] args) throws Exception {
		GameContainer control = new GameContainer();
	}

	public Input getInput() {
		return input;
	}

	public String[] getRandom() {
		return random;
	}

	// Spawn Groups
	// Strings
	public void spawnGroupA(int y) {
		enemies.add(new Enemy(Color.YELLOW, Pattern.Horizontal, 200, 30, y, 0, 3, true, "Hello World", 3, false, 60,
				300, 15, false, Pattern.Single));
		enemies.add(new Enemy(Color.YELLOW, Pattern.Horizontal, 200, 30, y + 30, 0, 3, false, "This is a String", 3,
				false, 60, 300, 15, false, Pattern.Single));
		enemies.add(new Enemy(Color.YELLOW, Pattern.Horizontal, 200, 30, y + 60, 0, 3, true, "L M A O", 3, false, 60,
				300, 15, false, Pattern.Single));
		enemies.add(new Enemy(Color.YELLOW, Pattern.Horizontal, 200, 30, y + 90, 0, 3, false, "Hi Mr Qayum", 3, false,
				60, 300, 15, false, Pattern.Single));
	}

	// Booleans
	public void spawnGroupB(int y) {
		enemies.add(new Enemy(Color.YELLOW, Pattern.Sine, 30, 30, y, 0, 2, true, "true", 2, true, 100, 500, 20, false,
				Pattern.Single));
		enemies.add(new Enemy(Color.YELLOW, Pattern.Sine, 30, 30, y, 0, 2, false, "false", 2, true, 100, 500, 20, false,
				Pattern.Single));
	}

	// Printing Strings
	public void spawnGroupC(int y) {
		enemies.add(new Enemy(Color.YELLOW, Pattern.Horizontal, 200, 30, y, 0, 2, true, "System.out.println()", 3, true,
				60, 500, 20, false, Pattern.Single));
		enemies.add(new Enemy(Color.YELLOW, Pattern.Horizontal, 200, 30, y + 30, 0, 2, false, "System.out.println()", 3,
				true, 60, 500, 20, false, Pattern.Single));

	}

	// Ints
	public void spawnGroupD(int y, int speed) {
		enemies.add(new Enemy(Color.YELLOW, Pattern.Parabola, 40, 10, y, 0, speed, true, "int", 2, true, 200, 550, 20,
				false, Pattern.Single));
		enemies.add(new Enemy(Color.YELLOW, Pattern.Parabola, 40, 10, y, 0, speed, false, "int", 2, true, 200, 550, 20,
				false, Pattern.Single));

	}

	// Doubles
	public void spawnGroupH(int y, int speed) {
		enemies.add(new Enemy(Color.YELLOW, Pattern.Parabola, 40, 10, y, 0, speed, true, "double", 4, true, 400, 1100,
				20, false, Pattern.Double));
		enemies.add(new Enemy(Color.YELLOW, Pattern.Parabola, 40, 10, y, 0, speed, false, "double", 4, true, 400, 1100,
				20, false, Pattern.Double));

	}

	// Broken String
	public void spawnGroupE(int y) {
		enemies.add(new Enemy(Color.YELLOW, Pattern.Horizontal, 200, 30, y, 0, 5, true, "He]lo W%rld", 9, true, 60, 500,
				15, false, Pattern.Single));
		enemies.add(new Enemy(Color.YELLOW, Pattern.Horizontal, 200, 30, y + 30, 0, 5, false, "T&is i* a Stri?g", 9,
				true, 60, 500, 15, false, Pattern.Single));
		enemies.add(new Enemy(Color.YELLOW, Pattern.Horizontal, 200, 30, y + 60, 0, 5, true, "L M AAAAaa O", 9, true,
				60, 500, 15, false, Pattern.Single));
		enemies.add(new Enemy(Color.YELLOW, Pattern.Horizontal, 200, 30, y + 90, 0, 5, false, "H# Mr Qa7um", 9, true,
				60, 500, 15, false, Pattern.Single));
	}

	// Broken Booleans
	public void spawnGroupF(int y) {
		enemies.add(new Enemy(Color.YELLOW, Pattern.Sine, 30, 30, y, 0, 2, true, "trEuu2e", 12, true, 100, 1000, 20,
				false, Pattern.Double));
		enemies.add(new Enemy(Color.YELLOW, Pattern.Sine, 30, 30, y, 0, 2, false, "fal[\\se", 12, true, 100, 1000, 20,
				false, Pattern.Double));
	}

	// Broken String Print
	public void spawnGroupG(int y) {
		enemies.add(new Enemy(Color.YELLOW, Pattern.Horizontal, 200, 30, y, 0, 1, true,
				"System.out.println().println()", 16, true, 60, 1000, 20, false, Pattern.Double));
		enemies.add(new Enemy(Color.YELLOW, Pattern.Horizontal, 200, 30, y + 30, 0, 1, false,
				"Systemmmmmmm.out.println()", 16, true, 60, 1000, 20, false, Pattern.Wiggle));

	}

	// Linked List
	public void node(int y) {
		enemies.add(new Enemy(Color.YELLOW, Pattern.Snake, 40, 10, y, 0, 2, true, "Node", 3, false, 200, 550, 20, false,
				Pattern.Single));
	}

	// Broken Linked List
	public void brokenNode(int y) {
		enemies.add(new Enemy(Color.YELLOW, Pattern.Snake, 40, 10, y, 0, 2, false, "HA!", 11, false, 200, 1100, 20,
				false, Pattern.Double));
	}

	// While loop
	public void miniBossA(int y, boolean killImportance) {
		enemies.add(new Enemy(Color.YELLOW, Pattern.Hover, 40, 10, y, 0, 2, true, "while", 30, true, 30, 1000, 30,
				killImportance, Pattern.Single));
	}

	public void miniBossB(int y, boolean killImportance) {
		enemies.add(new Enemy(Color.YELLOW, Pattern.Rectangle, 40, 10, y, 0, 2, true, "for", 50, true, 100, 2000, 30,
				killImportance, Pattern.Wiggle));
	}

	public void miniBossC(int y, boolean killImportance) {
		enemies.add(new Enemy(Color.YELLOW, Pattern.Immobile, 40, 10, y, 0, 1, true, "do while", 150, true, 100, 10000,
				50, killImportance, Pattern.Spread));
	}

	public void miniBossD(int y, boolean killImportance) {
		enemies.add(new Enemy(Color.YELLOW, Pattern.Rectangle, 40, 10, y, 0, 2, true, "do while", 200, true, 100, 10000,
				40, killImportance, Pattern.Spread));
	}

	public void miniBossE(int y, boolean killImportance) {
		enemies.add(new Enemy(Color.YELLOW, Pattern.Immobile, 40, 10, y, 0, 1, true, "HACKERMAN", 400, true, 100,
				10000000, 60, killImportance, Pattern.Track));
	}
}
