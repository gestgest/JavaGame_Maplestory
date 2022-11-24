package data;
// ChatMsg.java 채팅 메시지 ObjectStream 용.
import java.io.Serializable;
import javax.swing.ImageIcon;

public class MapleStoryMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	private String userName;
	private String code; // 100:로그인, 400:로그아웃, 200:채팅메시지, 300:Image, player : 400
	
	private String data;
	private ImageIcon img;
	private User user;
	//코드와 짜잘한 x,y와 같은 사소한 data값을 넣고 프로토콜로 분류한다
	
	//100
	//유저이름 프로토콜 캐릭터_정보

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

	/////////////////////////////////////
	//이미지
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