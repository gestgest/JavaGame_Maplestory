package data;

import javax.swing.ImageIcon;

public class User {
	private String name;
	private int x,y;
	//�������� �̹���
	private ImageIcon image; 
	
	//���� ���� [�α����� ó�� �ߴٸ�]
	public User(String name, int x, int y, ImageIcon image)
	{
		this.name = name;
		this.x = x;
		this.y = y;
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
		this.image = user.image;
		this.x = user.x;
		this.y = user.y;
	}

	public String getName() {return name; }
	public void setName(String name) {this.name = name; }
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
	
	//����
	
	//hp
	//����
	//����ġ
	//���ݷ�
	//
	
	
}
