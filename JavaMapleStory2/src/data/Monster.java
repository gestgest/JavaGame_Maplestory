package data;

import javax.swing.ImageIcon;

public class Monster {
	private int x, y;
	private int hp;
	private ImageIcon[] images;
	public Monster() {
		this.x = (int) (Math.random() * 800 * 100);
		this.y = 45000;
		init_img();
		
	}
	
	private void init_img()
	{
		images = new ImageIcon[1];
		images[0] = new ImageIcon("src/res/img/monster/idleSlime1.png");
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	
	public ImageIcon getImg(int index) {
		if(images.length <= index)
			return images[0];
		return images[index];
	}
	
	
	public void setImg(ImageIcon image, int index) {
		this.images[index] = image;
	}
}
