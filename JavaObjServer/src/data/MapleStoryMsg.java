package data;
// ChatMsg.java ä�� �޽��� ObjectStream ��.
import java.io.Serializable;
import javax.swing.ImageIcon;

public class MapleStoryMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	private String userName;
	private String code; // 100:�α���, 400:�α׾ƿ�, 200:ä�ø޽���, 300:Image, player : 400
	
	
	private String data;
	
	private int x,y;
	private ImageIcon img;
	//�ڵ�� ¥���� x,y�� ���� ����� data���� �ְ� �������ݷ� �з��Ѵ�

	//100
	//�����̸� �������� 
	//101
	//�����̸� �������� x
	//102
	//�����̸� �������� y
	//103
	//�����̸� �������� Idle�̹���

	public MapleStoryMsg(String code) {
		this.code = code;
	}

	/////////////////////////////////////
	//�������� �ڵ�
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	

	
	/////////////////////////////////////
	//�г���
	public String getName() {
		return userName;
	}
	public void setName(String userName) {
		this.userName = userName;
	}

	
	/////////////////////////////////////
	//������
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	public void setData(int data) {
		this.data = Integer.toString(data);
	}
	
	

	/////////////////////////////////////
	//����
	public int getX() { return x; }
	public int getY() { return y; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.x = y; }
	
	//�̹���
	public ImageIcon getImg() {
		return img;
	}
	
	public void setImg(ImageIcon img) {
		this.img = img;
	}

}