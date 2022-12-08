package data;
// ChatMsg.java ä�� �޽��� ObjectStream ��.
import java.io.Serializable;
import javax.swing.ImageIcon;

public class MapleStoryMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	private String userName;
	private String code; // 100:�α���, 400:�α׾ƿ�, 200:ä�ø޽���, 300:Image, player : 400
	private int keybuff;
	
	private boolean isData;
	private String data;
	
	private int x,y;
	private int type; //����Ÿ��
	// �̹����� �Ⱥ����� ���������� [�̹��� Ÿ�Ӹ� ������ ���� ������]
	//�ڵ�� ¥���� x,y�� ���� ����� data���� �ְ� �������ݷ� �з��Ѵ�

	//100 [�α���]
	//�����̸� �������� ��������
	//101
	//�����̸� �������� x
	//102
	//�����̸� �������� y
	//103
	//�����̸� �������� x, y
	//104
	//�����̸� �������� ����
	//105
	//106
	//107
	//108
	//109
	// bool : 
	//private boolean isLeft;
	//private boolean isJump;
	//private boolean isWalk;
	//private boolean isAttack;
	//private boolean isDamaged;
	//110
	//�����̸� �������� ��������
	

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
	public void setY(int y) { this.y = y; }
	public int getKeybuff() { return keybuff; }
	public void setKeybuff(int keybuff ) { this.keybuff = keybuff; }
	public int getType() { return type; }
	public void setType(int type ) { this.type = type; }

	public boolean getIsData() { return isData; }
	public void setIIsData(boolean isData) { this.isData = isData; }
	
	//�̹���
	
	public void setUser(User user)
	{
		this.x = user.getX();
		this.y = user.getY();
		this.keybuff = user.getKeybuff();
	}
	
	public User getUser() {
		User user = new User(userName);
		user.setX(x);
		user.setY(y);
		user.setKeybuff(keybuff);
		
		return user;
	}

}