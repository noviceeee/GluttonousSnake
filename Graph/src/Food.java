import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Food {
	private static Random r = new Random();
	private static final int UNIT_SIZE = Ground.UNIT_SIZE;//食物大小和窗口单元格相同
	int status = 1;//食物状态：1-最好  2-好 3-坏
	static int x;
	static int y;
	private Snake snake;
	 int time = 60;// 存在时间，超过后此食物会消失
	boolean exist = true;//食物是否存在

	public Food(Snake snake) {
		this.snake = snake;		
		locate();
	}
	
	public static void locate() {//确定食物坐标
		x = r.nextInt(Ground.GAME_WIDTH / UNIT_SIZE) * 25;// 在窗口范围内随机生成
		y = 25 + r.nextInt(Ground.GAME_HEIGHT / UNIT_SIZE - 1) * 25;						
	}

	public void checkCollide() {//检查是否与蛇身重合，若重合则重新生成食物坐标
		Snake.Body b;
		for(int i=0; i < snake.bodies.size();i++) {
			b=snake.bodies.get(i);
			if(x==b.x&&y==b.y) 
				locate();
		}
	}

	public void draw(Graphics g) {
		checkCollide();
		if (time <= 0)//时间到了则食物消失
			exist = false;
		Color c = g.getColor();
		if(time>40) {//不同时间食物状态不一样，即吃掉后得分不同
		g.setColor(Color.red);
		}
		else if(time>10) {
			g.setColor(Color.pink);
			status=2;
			}
		else {
			g.setColor(Color.gray);
		status = 3;	
		}
			
		g.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
		g.setColor(c);
		time--;// 每重画一次，食物的存在时间减少
	}

}
