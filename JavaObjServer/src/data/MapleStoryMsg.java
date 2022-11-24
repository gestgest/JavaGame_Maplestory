package data;
// ChatMsg.java ä�� �޽��� ObjectStream ��.
import java.io.Serializable;
import javax.swing.ImageIcon;

public class MapleStoryMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	private String userName;
	private String code; // 100:�α���, 400:�α׾ƿ�, 200:ä�ø޽���, 300:Image, player : 400
	
	private String data;
	private ImageIcon img;
	private User user;
	//�ڵ�� ¥���� x,y�� ���� ����� data���� �ְ� �������ݷ� �з��Ѵ�
	
	//100
	//�������� ĳ����_����

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

	/////////////////////////////////////
	//�̹���
	public ImageIcon getImg() {
		return img;
	}
	
	public void setImg(ImageIcon img) {
		this.img = img;
	}

	/////////////////////////////////////
	//character
	public void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
}