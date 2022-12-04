package data;

import javax.swing.ImageIcon;


public class Monster {
	private int x, y;
	private int hp;
	private int degree;
	
	private int thinkTime;
	private int walkTime;
	
	private boolean isThinking;
	private boolean isWalk;

	private long thinkStart;
	private long walkStart;
	
	private ImageIcon[] images;
	public Monster() {
		this.x = (int) (Math.random() * 800 * 100);
		this.y = 52000;
		init_img();
		
		isThinking = false;
		isWalk = false;
		degree = 0;
	}
	
	private void init_img()
	{
		//슬라임
		images = new ImageIcon[32];
		for(int i = 0; i < 16; i++)
		{
			images[i] = new ImageIcon("src/res/img/monster/slime/slimeLeft"+ (i+1) +".png");
		}
		for(int i = 0; i < 16; i++)
		{
			images[i + 16] = new ImageIcon("src/res/img/monster/slime/slimeRight"+ (i+1) +".png");
		}
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	public int getDegree() { return degree; }
	public void setDegree(int degree) { this.degree = degree; }

	public int getThinkTime() { return thinkTime; }
	public void setThinkTime(int thinkTime) { this.thinkTime = thinkTime; }
	public int getWalkTime() { return walkTime; }
	public void setWalkTime(int walkTime) { this.walkTime = walkTime; }
	
	public boolean getIsThinking() { return isThinking; }
	public void setIsThinking(boolean isThinking) { this.isThinking = isThinking; }	
	public boolean getIsWalk() { return isWalk; }
	public void setIsWalk(boolean isWalk) { this.isWalk = isWalk; }

	public long getThinkStart() { return thinkStart; }
	public void setThinkStart(long thinkStart) { this.thinkStart = thinkStart; }
	public long getWalkStart() { return walkStart; }
	public void setWalkStart(long walkStart) { this.walkStart = walkStart; }
	
	public ImageIcon getImg(int index) {
		if(images.length <= index)
			return images[0];
		return images[index];
	}
	
	
	public void setImg(ImageIcon image, int index) {
		this.images[index] = image;
	}
}
