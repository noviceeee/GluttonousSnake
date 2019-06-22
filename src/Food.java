import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Food {
	private static Random r = new Random();
	private static final int UNIT_SIZE = 25;
	int x;
	int y;
	private Ground ground;
	private Snake snake;
	private int time = 100;// 存在时间
	boolean exist = true;
	boolean collide = false;

	public Food(Ground ground, Snake snake) {
		this.ground = ground;
		this.snake = snake;
		x = r.nextInt(ground.GAME_WIDTH / UNIT_SIZE) * 25;// 在窗口范围内随机生成
		y = 25 + r.nextInt(ground.GAME_HEIGHT / UNIT_SIZE - 1) * 25;				
	}


	public void draw(Graphics g) {
		if (time <= 0)
			exist = false;
		Color c = g.getColor();
		g.setColor(Color.pink);
		g.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
		g.setColor(c);
		time--;// 每重画一次，食物的存在时间减少
	}

}
