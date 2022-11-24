package data;

import javax.swing.ImageIcon;

public class User {
	private String name;
	private Point point;
	//�������� �̹���
	private ImageIcon image; 
	
	//���� ���� [�α����� ó�� �ߴٸ�]
	public User(String name, int x, int y, ImageIcon image)
	{
		this.name = name;
		this.point = new Point(x,y);
		this.image = image;
		//Init�Լ��� ó���ϵ� �ؾ��� 
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
	
	//����
	
	//hp
	//����
	//����ġ
	//���ݷ�
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
