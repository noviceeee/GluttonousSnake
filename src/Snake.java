import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Snake {
	boolean live = true;
	 int x;
	 int y;
	 int bodyIndex=0;
			int head_old_x;
	 int head_old_y;
	private static final int UNIT_SIZE = 25;
	Direction dir = Direction.STOP;
	private Ground ground;
	boolean bU = false, bR = false, bD = false, bL = false;
	private Head head = new Head();
	private Body b;
	ArrayList<Body> bodies = new ArrayList<Body>();
	public void draw(Graphics g) {
		head.draw(g);
		for(int i = 0; i < bodies.size(); i++) {
			Body body = bodies.get(i);
			body.draw(g);
}
		System.out.println();
	}
	
	public Snake(int x, int y,Direction dir, Ground ground) {
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.ground = ground;
	}
	
	void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_UP:
			if(dir==Direction.D)
				break;
			bU = true;
			break;
		case KeyEvent.VK_RIGHT:
			if(dir==Direction.L)
				break;
			bR = true;
			break;
		case KeyEvent.VK_DOWN:
			if(dir==Direction.U)
				break;
			bD = true;
			break;
		case KeyEvent.VK_LEFT:
			if(dir==Direction.R)
				break;
			bL = true;
			break;
		}
		myDirection();
	}

	void myDirection() {
		if (bU && !bR && !bD && !bL)
			dir = Direction.U;
		if (!bU && bR && !bD && !bL)
			dir = Direction.R;
		if (!bU && !bR && bD && !bL)
			dir = Direction.D;
		if (!bU && !bR && !bD && bL)
			dir = Direction.L;
	}

	 void keyReleased(KeyEvent e) {
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
	
	class Head{
		public void draw(Graphics g) {
			Color color = g.getColor();
			g.setColor(Color.RED);
			g.fillRect(x, y, UNIT_SIZE,UNIT_SIZE);
			g.setColor(color);
			move(dir);
			eat();
		}
		
		public void move(Direction dir) {
			head_old_x = x;
			head_old_y = y;
			switch(dir) {
			case U:
				y -= UNIT_SIZE;
				break;
			case R:
				x += UNIT_SIZE;
				break;
			case D:
				y += UNIT_SIZE;
				break;
			case L:
				x -= UNIT_SIZE;
				break;
			}
			live();
		}
		
		private void eat() {
			if(live&&ground.food.exist&&x==ground.food.x&&y==ground.food.y) {
				ground.food.exist=false;
				ground.score += 1;
				if(bodies.size()==0) {
				 b = new Body(bodyIndex++,head_old_x, head_old_y);
				bodies.add(b);
				}else {
					Body b2 = new Body(bodyIndex++,bodies);
					bodies.add(b2);
					b = b2;
				}	
			}
		}
		
		private void live() {//蛇会在以下情况死亡：
			if(x<0||x>=ground.GAME_WIDTH||y<=0||y>=ground.GAME_HEIGHT)//触碰边界
				live = false;
			Body b=null;
			for(int i = 0; i<bodies.size();i++) {//咬到自己
				b = bodies.get(i);
				if(x==b.x&&y==b.y)
					live=false;
			}
			
		}
	}
	
	class Body{
		int x;
		int y;
		int old_x;
		int old_y;
		private Body b;
		private int bodyIndex;
		public Body(int bodyIndex,int x, int y) {
			this.x = x;
			this.y = y;
			this.bodyIndex=bodyIndex;
		}
		public Body(int bodyIndex,ArrayList<Body> bodies) {
			this.x = bodies.get(bodyIndex-1).old_x;
			this.y = bodies.get(bodyIndex-1).old_y;
			this.bodyIndex=bodyIndex;
		}
		public void draw(Graphics g) {
			Color color = g.getColor();
			g.setColor(Color.ORANGE);
			g.fillRect(x, y, UNIT_SIZE,UNIT_SIZE);
			g.setColor(color);
			
			move();
		}
		
		public void move() {
			old_x = x;
			old_y = y;
			if(bodyIndex==0) {
			x = head_old_x;
			y = head_old_y;
			}
			else {
				x = bodies.get(bodyIndex-1).old_x;
				y = bodies.get(bodyIndex-1).old_y;
				
			}
		}
	}
}