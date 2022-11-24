package data;

import javax.swing.ImageIcon;

public class User {
	private String name;
	private int x,y;
	//여러개의 이미지
	private ImageIcon image; 
	
	//없앨 내용 [로그인을 처음 했다면]
	public User(String name, int x, int y, ImageIcon image)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.image = image;
		//Init함수로 처리하든 해야함 
	}
	public User(String username)
	{
		this.name = username;
	}
	
	public User(User user)
	{
		this.name = user.name;
		this.image = user.image;
		this.x = user.x;
		this.y = user.y;
	}

	public int getX() { return x; }
	public int getY() { return y; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.x = y; }
	public void setX(String x) { this.x = Integer.parseInt(x); }
	public void setY(String y) { this.y = Integer.parseInt(y); }


	public ImageIcon getImg() {
		return image;
	}
	
	public void setImg(ImageIcon image) {
		this.image = image;
	}
	
	//직업
	
	//hp
	//마나
	//경험치
	//공격력
	//
	
	
}
