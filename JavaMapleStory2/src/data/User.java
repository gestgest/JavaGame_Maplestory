package data;

import javax.swing.ImageIcon;

public class User {
	private String name;

	private int x,y; //실제론 10000, 10000 => 100,100
	//여러개의 이미지
	// 왼쪽0
	// 오른쪽1
	// 왼쪽 걷기 2 3 4 5
	// 오른쪽 걷기 6 7 8 9
	private ImageIcon image[];
	private int keybuff;
	private int degree;
	private boolean isLeft;
	private boolean isJump;
	private boolean isWalk;
	private long walkStart;
	private long walkTime;
	private int velocity;
	
	
	//없앨 내용 [로그인을 처음 했다면]
	public User(String name, int x, int y, ImageIcon image[])
	{
		this.name = name;
		this.x = x;
		this.y = y;

		this.image = new ImageIcon[image.length];
		for(int i = 0; i < image.length; i++)
		{
			this.image[i] = image[i];
		}
	}
	public User(String username)
	{
		this.name = username;
	}
	
	public User(User user)
	{
		this.name = user.name;
		this.x = user.x;
		this.y = user.y;
		for(int i = 0; i < user.image.length; i++)
		{
			this.image[i] = user.getImg(i);
		}
	}
	
	private void init_User()
	{
		//Init함수로 처리하든 해야함 
		this.isLeft = true;
		this.isJump = true;
		this.isWalk = false;
		this.walkStart = 0;
		this.walkTime = 0;
		this.velocity = 0;
		this.keybuff = 0;
		this.degree = 0;
		
	}

	public String getName() {return name; }
	public void setName(String name) {this.name = name; }
	public int getX() { return x; }
	public int getY() { return y; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	public void setX(String x) { this.x = Integer.parseInt(x); }
	public void setY(String y) { this.y = Integer.parseInt(y); }

	public int getKeybuff() { return keybuff; }
	public void setKeybuff(int keybuff) { this.keybuff = keybuff; }
	
	public int getDegree() { return degree; }
	public void setDegree(int degree) { this.degree = degree; }

	public boolean getIsLeft() { return isLeft; }
	public void setIsLeft(boolean isLeft) { this.isLeft = isLeft; }
	public boolean getIsJump() { return isJump; }
	public void setIsJump(boolean isJump) { this.isJump = isJump; }
	public boolean getIsWalk() { return isWalk; }
	public void setIsWalk(boolean isWalk) { this.isWalk = isWalk; }

	public int getVelocity() { return velocity; }
	public void setVelocity(int velocity) { this.velocity = velocity; }

	public long getWalkStart() { return walkStart; }
	public void setWalkStart(long walkStart) { this.walkStart = walkStart; }
	public long getWalkTime() { return walkTime; }
	public void setWalkTime(long walkTime) { this.walkTime = walkTime; }
	
	
	//index : 0 : leftidle
	//1 : rightidle
	public ImageIcon getImg(int index) {
		if(10 <= index) {
			System.out.println("10 넘음");
			return image[0];
		}
		return image[index];
	}
	
	
	public void setImg(ImageIcon image, int index) {
		this.image[index] = image;
	}
	
	//직업
	
	//hp
	//마나
	//경험치
	//공격력
	//
	
	
}
