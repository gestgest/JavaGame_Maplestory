package data;
// ChatMsg.java 채팅 메시지 ObjectStream 용.
import java.io.Serializable;
import javax.swing.ImageIcon;

public class MapleStoryMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	private String userName;
	private String code; // 100:로그인, 400:로그아웃, 200:채팅메시지, 300:Image, player : 400
	private int keybuff;
	
	private String data;
	
	private int x,y;
	private int type; //직업타입
	// 이미지를 안보내도 되지않을까 [이미지 타임만 보내면 되지 않을까]
	private ImageIcon imageLeft;
	private ImageIcon imageRight;
	//코드와 짜잘한 x,y와 같은 사소한 data값을 넣고 프로토콜로 분류한다

	//100 [로그인]
	//유저이름 프로토콜 유저정보
	//101
	//유저이름 프로토콜 x
	//102
	//유저이름 프로토콜 y
	//103
	//유저이름 프로토콜 Idle이미지
	//104
	//유저이름 프로토콜 버퍼
	//110
	//유저이름 프로토콜 유저정보
	

	public MapleStoryMsg(String code) {
		this.code = code;
	}

	/////////////////////////////////////
	//프로토콜 코드
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	

	
	/////////////////////////////////////
	//닉네임
	public String getName() {
		return userName;
	}
	public void setName(String userName) {
		this.userName = userName;
	}

	
	/////////////////////////////////////
	//데이터
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
	//유저
	public int getX() { return x; }
	public int getY() { return y; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.x = y; }
	public int getKeybuff() { return keybuff; }
	public void setKeybuff(int keybuff ) { this.keybuff = keybuff; }
	public int getType() { return type; }
	public void setType(int type ) { this.type = type; }
	
	//이미지
	public ImageIcon getImg(int index) {
		if(index == 1)
			return imageRight;
		else
			return imageLeft;
	}
	
	public void setImg(ImageIcon image, int index) {
		if(index == 1)
			this.imageRight = image;
		else
			this.imageLeft = image;
	}
	
	public void setUser(User user)
	{
		this.x = user.getX();
		this.y = user.getY();
		this.imageLeft = user.getImg(0);
		this.imageRight = user.getImg(1);
		this.keybuff = user.getKeybuff();
	}
	
	public User getUser() {
		User user = new User(userName, type);
		user.setX(x);
		user.setY(y);
		user.setImg(imageLeft, 0);
		user.setImg(imageRight, 1);
		user.setKeybuff(keybuff);
		
		return user;
	}

}