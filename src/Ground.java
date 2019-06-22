import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Ground extends Frame {

	public static final int GAME_HEIGHT = 400;// 游戏窗口大小
	public static final int GAME_WIDTH = 500;
	public static final int UNIT_SIZE = 25;// 单元格大小
	public int score = 0;
	private boolean play = true;
	private boolean restart = false;
	Snake snake = new Snake(50, 50, Direction.STOP, this);
	Food food = new Food(this, snake);

	public void paint(Graphics g) {
		if (snake.live)
			play(g);
		else
			gameOver(g);
	}

	private void play(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.darkGray);
		for (int i = UNIT_SIZE; i < GAME_HEIGHT; i += UNIT_SIZE) {
			g.drawLine(0, i, GAME_WIDTH, i);
		}
		for (int j = UNIT_SIZE; j < GAME_WIDTH; j += UNIT_SIZE) {
			g.drawLine(j, 0, j, GAME_HEIGHT);
		}
		g.setColor(Color.white);

		if (!food.exist)
			food = new Food(this, snake);
		food.draw(g);
		snake.draw(g);

		g.drawString("score:" + score, 10, 50);

		g.setColor(c);
	}

	private void gameOver(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.WHITE);
		g.setFont(new Font("宋体", Font.BOLD, 80));
		g.drawString("GAME OVER", 50, 210);
		g.setFont(new Font("宋体", Font.BOLD, 40));
		g.drawString("your score: " + score, 110, 260);
		g.setFont(new Font("宋体", Font.CENTER_BASELINE, 20));
		g.drawString("press R to play again", 140, 380);
		g.setColor(c);
	}

	public void init() {
		score = 0;
		play = true;
		restart = false;
		snake = new Snake(150, 50, Direction.STOP, this);
		food = new Food(this, snake);
	}

	Image offScreenImage = null;

	public void update(Graphics g) {
		if (offScreenImage == null) {
			offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.black);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		g.setColor(c);
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);
	}

	public void launch() {
		this.setLocation(400, 100);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setTitle("The Gluttonous Snake");
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		this.setResizable(false);
		this.setBackground(Color.black);
		this.addKeyListener(new KeyMonitor());
		this.setVisible(true);
		new Thread(new PaintThread()).start();
		;
	}

	public static void main(String[] args) {
		new Ground().launch();
	}

	private class PaintThread implements Runnable {// 线程控制画面保持匀速刷新
		public void run() {
			while (true) {
				if (play) {
					if (restart)
						init();
					repaint();
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class KeyMonitor extends KeyAdapter {// 键盘控制操作
		public void keyPressed(KeyEvent e) {
			snake.keyPressed(e);
		}

		public void keyReleased(KeyEvent e) {
			snake.keyReleased(e);
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_SPACE && snake.live) {
				if (play)
					play = false;
				else
					play = true;
			}
			if (key == KeyEvent.VK_R && !snake.live)
				restart = true;
		}
	}
}