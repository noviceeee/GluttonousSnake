import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Snake {
	boolean live = true;//蛇是否活着
	int x;
	int y;
	int bodyIndex = 0;//用于记录各节身体的编号
	int head_old_x;//头部旧坐标
	int head_old_y;
	private static final int UNIT_SIZE = Ground.UNIT_SIZE;//蛇的尺寸（和窗口单元格大小相同）
	Direction dir = Direction.STOP;//初始方向
	private Ground ground;
	boolean bU = false, bR = false, bD = false, bL = false;//记录方向键使用情况
	private Head head = new Head();//创建头部对象
	ArrayList<Body> bodies = new ArrayList<Body>();//数组列表存储多节身体

	public void draw(Graphics g) {//画头和多节身体
		head.draw(g);
		for (int i = 0; i < bodies.size(); i++) {
			Body body = bodies.get(i);
			body.draw(g);
		}
	}

	public Snake(int x, int y, Direction dir, Ground ground) {
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.ground = ground;
	}

	void keyPressed(KeyEvent e) {//方向键控制蛇头运动方向
		int key = e.getKeyCode();//记录按键情况
		switch (key) {
		case KeyEvent.VK_UP:
			if (dir == Direction.D)
				break;
			bU = true;
			break;
		case KeyEvent.VK_RIGHT:
			if (dir == Direction.L)
				break;
			bR = true;
			break;
		case KeyEvent.VK_DOWN:
			if (dir == Direction.U)
				break;
			bD = true;
			break;
		case KeyEvent.VK_LEFT:
			if (dir == Direction.R)
				break;
			bL = true;
			break;
		}
		myDirection();
	}

	void myDirection() {//根据按键确定方向
		if (bU && !bR && !bD && !bL)
			dir = Direction.U;
		if (!bU && bR && !bD && !bL)
			dir = Direction.R;
		if (!bU && !bR && bD && !bL)
			dir = Direction.D;
		if (!bU && !bR && !bD && bL)
			dir = Direction.L;
	}

	void keyReleased(KeyEvent e) {//释放键时恢复原来状态
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_UP:
			bU = false;
			break;
		case KeyEvent.VK_RIGHT:
			bR = false;
			break;
		case KeyEvent.VK_DOWN:
			bD = false;
			break;
		case KeyEvent.VK_LEFT:
			bL = false;
			break;
		}
	}

	class Head {
		public void draw(Graphics g) {//画头
			Color color = g.getColor();
			g.setColor(Color.orange);
			g.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
			g.setColor(color);
			move(dir);
			eat();
		}

		public void move(Direction dir) {//头部根据当前方向移动
			head_old_x = x;
			head_old_y = y;
			switch (dir) {
			case U:
				y -= UNIT_SIZE;
				if(y<25)
					y = ground.GAME_HEIGHT-UNIT_SIZE;
				break;
			case R:
				x += UNIT_SIZE;
				if(x>ground.GAME_WIDTH-UNIT_SIZE)
					x=0;
				break;
			case D:
				y += UNIT_SIZE;
				if(y>ground.GAME_HEIGHT-UNIT_SIZE)
					y=25;
				break;
			case L:
				x -= UNIT_SIZE;
				if(x<0)
					x=ground.GAME_WIDTH-UNIT_SIZE;
				break;
			}
			checkLive();
		}

		private void eat() {
			if (live && ground.food.exist && x == ground.food.x && y == ground.food.y) {//当蛇头与食物重合，判定为吃到食物
				ground.food.exist = false;//此食物设为不存在
				
				getScore();
				grow();		
			}
			else if (ground.food.time<0) {//此食物消失则扣分
				ground.score -= 5;
				if(ground.score<0)//扣到0为止
					ground.score =0;
			}
		}
		
		private void getScore() {
			if(ground.food.status==1)//根据食物状态得分或扣分
				ground.score += 5;
			else if(ground.food.status==2)
				ground.score += 3;
			else {
				ground.score -=3;
				if(ground.score<0)//扣到0为止
					ground.score =0;	
			}
		}
		
		private void grow() {
			if (bodies.size() == 0) //如果没有身体就添加一个初始坐标为头部旧坐标的身体对象
				bodies.add(new Body(bodyIndex++, head_old_x, head_old_y));
			else //如果已有身体就添加一个初始坐标为前一节身体旧坐标的对象（这里好像看不出来，具体见各节身体的移动方式设置）
				bodies.add(new Body(bodyIndex++, bodies));
		}

		private void checkLive() {// 检查是否活着，蛇咬到自己后死亡：
			Body b = null;
			for (int i = 0; i < bodies.size(); i++) {
				b = bodies.get(i);
				if (x == b.x && y == b.y)
					live = false;
			}

		}
	}

	class Body {//某节身体
		int x;
		int y;
		int old_x;
		int old_y;
		private int bodyIndex;//该节身体的编号

		public Body(int bodyIndex, int x, int y) {//此前没有身体时
			this.x = x;
			this.y = y;
			this.bodyIndex = bodyIndex;
		}

		public Body(int bodyIndex, ArrayList<Body> bodies) {//此前有身体时
			this.x = bodies.get(bodyIndex - 1).old_x;
			this.y = bodies.get(bodyIndex - 1).old_y;
			this.bodyIndex = bodyIndex;
		}

		public void draw(Graphics g) {
			Color color = g.getColor();
			g.setColor(Color.yellow);
			g.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
			g.setColor(color);

			move();
		}

		public void move() {
			old_x = x;
			old_y = y;
			if (bodyIndex == 0) {//如果没有身体，则此节为第一节，坐标为头部旧坐标
				x = head_old_x;
				y = head_old_y;
			} else {//如果已有身体则此节身体坐标为前一节的旧坐标
				x = bodies.get(bodyIndex - 1).old_x;
				y = bodies.get(bodyIndex - 1).old_y;

			}
		}
	}
}