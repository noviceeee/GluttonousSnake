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
	public int score = 0;//游戏得分
	private boolean play = true;//控制游戏暂停/开始
	private boolean restart = false;//控制游戏重新开始
	
	Snake snake = new Snake(50, 50, Direction.STOP, this);
	Food food = new Food(snake);

	public void paint(Graphics g) {//显示游戏运行界面或结束界面
		if (snake.live)
			play(g);
		else
			gameOver(g);
	}

	private void play(Graphics g) {//游戏运行
		Color c = g.getColor();
		g.setColor(Color.darkGray);
		
		for (int i = UNIT_SIZE; i < GAME_HEIGHT; i += UNIT_SIZE) {//背景横线
			g.drawLine(0, i, GAME_WIDTH, i);
		}
		for (int j = UNIT_SIZE; j < GAME_WIDTH; j += UNIT_SIZE) {//背景竖线
			g.drawLine(j, 0, j, GAME_HEIGHT);
		}
		

		if (!food.exist)//如果旧食物不存在就创建一个新的
			food = new Food(snake);
		food.draw(g);
		snake.draw(g);

		g.setColor(Color.white);
		g.drawString("score:" + score, 10, 50);//显示得分

		g.setColor(c);
	}

	private void gameOver(Graphics g) {//游戏结束
		Color c = g.getColor();
		g.setColor(Color.WHITE);
		g.setFont(new Font("宋体", Font.BOLD, 80));
		g.drawString("GAME OVER", 50, 210);
		g.setFont(new Font("宋体", Font.BOLD, 40));
		g.drawString("your score: " + score, 90, 260);
		g.setFont(new Font("宋体", Font.CENTER_BASELINE, 20));
		g.drawString("press R to play again", 135, 380);
		g.setColor(c);
	}

	public void init() {//重新开始时初始化数据
		score = 0;
		play = true;
		restart = false;
		snake = new Snake(50, 50, Direction.STOP, this);
		food = new Food(snake);
	}

	Image offScreenImage = null;//双缓冲解决屏幕刷新闪烁问题
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

	public void launch() {//游戏启动
		this.setLocation(400, 100);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setTitle("The Gluttonous Snake");
		
		this.addWindowListener(new WindowAdapter() {//设置窗口可关闭
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		this.setResizable(false);//窗口不可改变大小
		this.setBackground(Color.black);
		this.addKeyListener(new KeyMonitor());
		this.setVisible(true);//窗口可视
		new Thread(new PaintThread()).start();//启动线程
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
			if (key == KeyEvent.VK_SPACE && snake.live) {//按空格键暂停/继续游戏
				if (play)
					play = false;
				else
					play = true;
			}
			
			if (key == KeyEvent.VK_R && !snake.live)//游戏结束后按R键重新开始
				restart = true;
		}
	}
}