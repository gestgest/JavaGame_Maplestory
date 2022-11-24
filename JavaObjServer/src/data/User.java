package data;

import javax.swing.ImageIcon;

public class User {
	private String name;
	private Point point;
	//여러개의 이미지
	private ImageIcon image; 
	
	//없앨 내용 [로그인을 처음 했다면]
	public User(String name, int x, int y, ImageIcon image)
	{
		this.name = name;
		this.point = new Point(x,y);
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
		this.point = new Point(user.getPoint());
		this.image = user.image;
	}
	
	//직업
	
	//hp
	//마나
	//경험치
	//공격력
	//

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Point getPoint() {
		return point;
	}
	public void setPoint(Point point) {
		this.point = point;
	}
	
	private class Point {
		private int x,y;
		public Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		public Point(Point point)
		{
			this.x = point.getX();
			this.y = point.getY();
		}
		public int getX() { return x; }
		public int getY() { return y; }
		
	}
	
}
