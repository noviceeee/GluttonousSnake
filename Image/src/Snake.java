import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Snake {
	boolean live = true;//蛇是否活着
	int x;
	int y;
	int bodyIndex = 0;//用于记录各节身体的编号
	int head_old_x;//头部旧坐标
	int head_old_y;
	private static final int UNIT_SIZE = Ground.UNIT_SIZE;//蛇的尺寸（和窗口单元格大小相同）
	private int status = 0;
	//蛇的状态，用于确选择头部图片：0-默认 1-默认（颠倒）2-超开心3-超开心（颠倒） 4-开心 5-开心（颠倒）6-不开心7-开心（颠倒） 8-失望9-失望（颠倒）
	private int time =1;//特殊状态持续时间
	Direction dir = Direction.STOP;//初始方向
	private Ground ground;
	boolean bU = false, bR = false, bD = false, bL = false;//记录方向键使用情况
	private Head head = new Head();//创建头部对象
	ArrayList<Body> bodies = new ArrayList<Body>();//数组列表存储多节身体
	
	private static Image[] emotions = {//加载图片
			new ImageIcon(Food.class.getClassLoader().getResource("images/head0.png")).getImage(),
			new ImageIcon(Food.class.getClassLoader().getResource("images/head0U.png")).getImage(),
			new ImageIcon(Food.class.getClassLoader().getResource("images/head1.png")).getImage(),
			new ImageIcon(Food.class.getClassLoader().getResource("images/head1U.png")).getImage(),
			new ImageIcon(Food.class.getClassLoader().getResource("images/head2.png")).getImage(),	
			new ImageIcon(Food.class.getClassLoader().getResource("images/head2U.png")).getImage(),	
			new ImageIcon(Food.class.getClassLoader().getResource("images/head3.png")).getImage(),	
			new ImageIcon(Food.class.getClassLoader().getResource("images/head3U.png")).getImage(),	
			new ImageIcon(Food.class.getClassLoader().getResource("images/head4.png")).getImage(),	
			new ImageIcon(Food.class.getClassLoader().getResource("images/head4U.png")).getImage()			
	};
	private static Image body =new ImageIcon(Food.class.getClassLoader().getResource("images/body.png")).getImage();
		
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
	
	private void checkEat() {
		if (live && ground.food.exist && x == ground.food.x && y == ground.food.y) {//当蛇头与食物重合，判定为吃到食物
			ground.food.exist = false;//此食物设为不存在

			switch(ground.food.status)	{
			case 0://食物最好
				status=2;//超开心
				if(dir==Direction.U)//如果此时蛇朝上运动
					status=3;//超开心（颠倒）
				ground.score +=5;
				break;
			case 1://食物好
				status=4;//开心
				if(dir==Direction.U)//如果此时蛇朝上运动
					status=5;//超开心（颠倒）
				ground.score +=3;
				break;
			case 2://食物坏
				status=6;//不开心
				if(dir==Direction.U)//如果此时蛇朝上运动
					status=7;//超开心（颠倒）
				ground.score -=3;
				break;
			}	
			grow();		
		}
	
	}
	private void checkMiss() {//错过食物
	if (ground.food.time<0) { //此食物消失则扣分
			status = 8;//失望
			if(dir==Direction.U)//如果运动方向向上
				status=9;//失望（颠倒）
			ground.score -= 5;
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
	
	private void getScore() {
		checkEat();
		checkMiss();
		if(ground.score<0)//扣到0为止
			ground.score =0;
	}

	class Head {
		public void draw(Graphics g) {//画头
			g.drawImage(emotions[status], x, y, null);//某状态
			time--;
			if(time<0) {//特殊状态时间结束，恢复默认状态
				if(dir==Direction.U)
				status = 1;
				else
					status=0;
				time=1;//恢复时间
				}
			move(dir);
			getScore();
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
			g.drawImage(body, x, y, null);//某状态
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