
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import data.MapleStoryMsg;
import data.User;

import javax.swing.JToggleButton;
import javax.swing.JList;

public class MapleStoryView extends JFrame {
	
	//서버관련
	private static final long serialVersionUID = 1L;
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	
	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	//////////////////////////////////////////////////////////////////////////////////////////
	
	
	//펜
	private GamePanel contentPane;
	GameScreen gameScreen;//Canvas 객체를 상속한 화면 묘화 메인 클래스
	//////////////////////////////////////////////////////////////////////////////////////////

	//이미지 자료
	private ImageIcon walk_1 = new ImageIcon("src/res/img/light-191.png");
	private ImageIcon walk_2 = new ImageIcon("src/res/img/light-194.png");
	private ImageIcon walk_3 = new ImageIcon("src/res/img/light-197.png");
	private ImageIcon walk_4 = new ImageIcon("src/res/img/light-200.png");
	private ImageIcon UIImageIcon = new ImageIcon("src/res/img/GameUI.png");
	private ImageIcon backgroundIcon = new ImageIcon("src/res/img/Background.png");

	private ImageIcon userImageIcons[];

	private Image UIImage = UIImageIcon.getImage();
	private Image backgroundImage = backgroundIcon.getImage();
	//////////////////////////////////////////////////////////////////////////////////////////
	
	
	//해상도, 높이가 900이상이면 높은 해상도, 아니면 800, 600해상도
	private int width, height;
	double screenWidth , screenHeight;

	
	//단축기
	final int LEFT_PRESSED	=0x001;
	final int RIGHT_PRESSED	=0x002;
	
	
	//나중에 캐릭터 클래스 만들거임
	private User user;
	private HashMap<String, User> users = new HashMap<String, User>(3);

	// 현재시간
	private long pretime;
	private final int delay = 17; //17 / 1000초 : 58 (프레임 / 초)
	private final int gravity = 20;
	private final int jumpA = 100; //점프 가속도
	private final int animationTime = 125;
	
	
	
	
	
	/**
	 * Create the frame.
	 */
	//모니터
	//매개변수 String username , String ip_addr, String port_no
	public MapleStoryView(String username, String ip_addr, String port_no) {
		
		//온전하게 서버종료
		addWindowListener(new MyWindowAdapter());
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = dim.getWidth();	
		screenHeight = dim.getHeight();
		
		//화면 맞추기 빡세서 포기
		//if(screenHeight < 900) {
			width = 800; height = 600;
		//}
		//else {
			//width = 1200; height = 900;
		//}

		int screenX = (int) ((screenWidth - width) / 2);
		int screenY = (int) ((screenHeight - height) / 2);
		
		setBounds(screenX, screenY, width, height);
		setResizable(false);
		addKeyListener(new KeyEventEx());
		
		contentPane = new GamePanel();
		
		setContentPane(contentPane);
		
		setVisible(true);
		
		
		//닉네임 설정
		init_user(username);
		users.put(username, user); //서버땐 뺴야함
		
		//네트워크 설정 [스레드 뺐음]★

		FrameThread frameThread = new FrameThread();
		frameThread.start();
		

		setFocusable(true);
		requestFocus();
	}
	
	//유저 
	private void init_user(String username)
	{
		userImageIcons = new ImageIcon[10];
		userImageIcons[0] = new ImageIcon("src/res/img/character/IdleLeft.png");
		userImageIcons[1] = new ImageIcon("src/res/img/character/IdleRight.png");
		userImageIcons[2] = new ImageIcon("src/res/img/character/WalkLeft1.png");
		userImageIcons[3] = new ImageIcon("src/res/img/character/WalkLeft2.png");
		userImageIcons[4] = new ImageIcon("src/res/img/character/WalkLeft3.png");
		userImageIcons[5] = new ImageIcon("src/res/img/character/WalkLeft4.png");
		userImageIcons[6] = new ImageIcon("src/res/img/character/WalkRight1.png");
		userImageIcons[7] = new ImageIcon("src/res/img/character/WalkRight2.png");
		userImageIcons[8] = new ImageIcon("src/res/img/character/WalkRight3.png");
		userImageIcons[9] = new ImageIcon("src/res/img/character/WalkRight4.png");
		
		
		user = new User(username,0,0,userImageIcons);
		user.setDegree(0);
		user.setKeybuff(0);
		//init_server_user();
	}
	
	//초기 설정
	private void init_server_user()
	{
		MapleStoryMsg obcm = new MapleStoryMsg("100");
		
		obcm.setName(user.getName());
		obcm.setX(user.getX());
		obcm.setY(user.getY());
		obcm.setImg(user.getImg(0), 0);
		obcm.setImg(user.getImg(1), 1);
		obcm.setKeybuff(0);

		gameScreen.repaint();//화면 리페인트
		
		//SendObject(obcm);
	}
	
	//메인 메인 판넬
	private class GamePanel extends JPanel{
		
		public GamePanel()
		{
			setLayout(null);
			setBorder(new EmptyBorder(5, 5, 5, 5));
			UIBar uibar = new UIBar();
			add(uibar);
			
			
			gameScreen = new GameScreen(this);
			gameScreen.setBounds(0,0,width,height);
			add(gameScreen);
			
		}
		
		//그리는 이미지
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			
			//유저 벡터 이미지 drawImage
			//drawUser(g);
			//g.drawImage(idleImage,x,y,46,74,this);
			
		}
		//키 이벤트
		
		
		//UI바
		private class UIBar extends JPanel{
			private int UIy; 
			private int UIx; 
			private int UIheight; 
			private int UIwidth; 
			public UIBar() {
				
				UIwidth = width;
				UIheight = height / 8;
				UIx = 0;
				UIy = UIheight * 7;
				setBounds(UIx,UIy,UIwidth,UIheight);
				
			}
			public void paintComponent(Graphics g) {
				super.paintComponent(g); //795 / 41 
				g.drawImage(UIImage,0,0,width,width * 41 / 795,this);
			}
		}
	}
	
	

	// Server Message를 수신해서 화면에 표시 [받기]
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {
					User user = null;
					Object obcm = null;
					String msg = null;
					MapleStoryMsg cm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					//쳇인경우
					if (obcm instanceof MapleStoryMsg) {
						cm = (MapleStoryMsg) obcm;
					} 
					else
						continue;
					//프로토콜 구분
					switch (cm.getCode()) {
					//로그인
					case "100":
						//닉네임
						users.put(cm.getName(), new User( cm.getName() ));
						user = users.get(cm.getName());
						user.setX(cm.getX());
						user.setY(cm.getY());
						user.setImg(cm.getImg(0), 0);
						user.setImg(cm.getImg(1), 1);
						System.out.println(cm.getName());
						break;
					case "101":
						//x
						
						user = users.get(cm.getName());
						user.setX(cm.getX());
						break;
					case "102":
						//y
						System.out.println("102받음");
						user = users.get(cm.getName());
						user.setY(cm.getY());
						break;
					case "103":
						//이미지
						System.out.println("103받음");
						user = users.get(cm.getName());
						user.setImg(cm.getImg(0), 0);
						user.setImg(cm.getImg(1), 1);
						break;
					}
					
				} catch (IOException e) {
					//에러 메세지 AppendText("ois.readObject() error");
					try {
//						dos.close();
//						dis.close();
						ois.close();
						oos.close();
						socket.close();

						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝

			}
		}
	}
	
	


	// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
	public byte[] MakePacket(String msg) {
		byte[] packet = new byte[BUF_LEN];
		byte[] bb = null;
		int i;
		for (i = 0; i < BUF_LEN; i++)
			packet[i] = 0;
		try {
			bb = msg.getBytes("euc-kr");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		for (i = 0; i < bb.length; i++)
			packet[i] = bb[i];
		return packet;
	}

	// Server에게 network으로 전송 [write]
	public void SendMessage(String msg) {
		try {
			
			//보내기
			MapleStoryMsg obcm = new MapleStoryMsg("200");
			//메세지 추가 set★
			
			//예시 보내기
			oos.writeObject(obcm);
		} catch (IOException e) {
			// AppendText("dos.write() error");
			//에러메세지
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}

	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			
			oos.writeObject(ob);
		} catch (IOException e) {
			//서버오류
			//클래스 단위로 보내지 마라
			System.exit(0);
		}
	}

	//중력 : 나중에 네트워크 스레드에 추가 [사실 클라 시간 스레드는 필요없지 않을까? = 애니메이션 구현]
	class FrameThread extends Thread{
		@Override
		public void run(){
			try
			{
				while(true){
					pretime=System.currentTimeMillis();

					gameScreen.repaint();//화면 리페인트
					process();//각종 충돌 처리
					keypross();//키 처리

					//프레임 유지
					if(System.currentTimeMillis()-pretime < delay) 
						Thread.sleep(delay - System.currentTimeMillis()+pretime);
						//게임 루프를 처리하는데 걸린 시간을 체크해서 딜레이값에서 차감하여 딜레이를 일정하게 유지한다.
						//루프 실행 시간이 딜레이 시간보다 크다면 게임 속도가 느려지게 된다.
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		private void process() {
			//나중에 중력 가속도 + 점프 넣을 예정
			int x = user.getX();
			int y = user.getY();
			int velocity = user.getVelocity();

			if(height * 100 - 10000 < y) {
				y = height * 100 - 12000;
				velocity = 0;
				user.setIsJump(false);
			}
			else {
				velocity += gravity;
				y += velocity;
						
			}
			
			x += user.getDegree() * 100;


			//경계선
			if(x < 0)
				x = 0;
			else if(width * 100 - 4600 < x)
				x = width * 100 - 4600;
			
			if(y < 0)
				y = 0;
			if(height * 100 - 14000 < y)
			{
				y = height * 100 - 14000;
				velocity = 0;
				user.setIsJump(false);
			}
			//이 밖에도 땅처리
			

			if(user.getIsWalk())
			{
				long mytime = user.getWalkStart();
				mytime = pretime - mytime;
				if(animationTime * 4 <= mytime)
					mytime %= (animationTime * 4);

				user.setWalkTime(mytime);
			}

			user.setX(x);
			user.setY(y);
			user.setVelocity(velocity);
		}
		
		//키 입력 받았던거 보내는 역할
		private void keypross()
		{
			switch(user.getKeybuff()) {
			case 0:
			case 3:
				user.setDegree(0);
				break;
			case LEFT_PRESSED:
				user.setDegree(-1);
				break;
			case RIGHT_PRESSED:
				user.setDegree(+1);
				break;
			}
			
			//이미지
			
			//MapleStoryMsg obcm = new MapleStoryMsg("104");
			//obcm.setKeybuff(keybuff);
			//obcm.setName(user.getName());
			//SendObject(obcm);
		}
		
	}
	
	private class GameScreen extends Canvas{
		GamePanel myGamePanel;
		//더블 버퍼용
		Image dblbuff;//더블버퍼링용 백버퍼
		Graphics gc;//더블버퍼링용 그래픽 컨텍스트 [오리지널은 g]
		GameScreen(GamePanel gamePanel)
		{
			this.myGamePanel = gamePanel;
		}
		public void paint(Graphics g)
		{
			//싱글톤마냥 처음에만 생성
			if(gc==null) 
			{
				//이미지 저장
				dblbuff=createImage(width,height);//더블 버퍼링용 오프스크린 버퍼 생성. 필히 paint 함수 내에서 해 줘야 한다. 그렇지 않으면 null이 반환된다.
				if(dblbuff==null) System.out.println("오프스크린 버퍼 생성 실패");
				else gc=dblbuff.getGraphics();//오프스크린 버퍼에 그리기 위한 그래픽 컨텍스트 획득
				return;
				//update는 안함
			}
			update(g);
		}
		
		@Override
		public void update(Graphics g){//화면 깜박거림을 줄이기 위해, paint에서 화면을 바로 묘화하지 않고 update 메소드를 호출하게 한다.
			if(gc==null) return;
			dblpaint();//오프스크린 버퍼에 그리고
			g.drawImage(dblbuff,0,0,this);

			//버퍼[오프스크린] => 화면
		}
		public void dblpaint(){
			//실제 그리는 동작은 이 함수에서 모두 행한다.
			//버퍼에 그리는 기능
			
			//배경
			gc.drawImage(backgroundImage,-100,-100,width + 100,height + 100,this);
			drawUser();
		}
		
		private void drawUser()
		{
			Iterator<String> keys = users.keySet().iterator();
			while(keys.hasNext())
			{
				String key = keys.next();
				 
				User user = users.get(key);

				int index = (int)user.getWalkTime();
				index /= animationTime;

				switch(user.getDegree()) {
				//idle
				case 0:
					if(user.getIsLeft())
						gc.drawImage(user.getImg(0).getImage(),user.getX() / 100, user.getY() / 100,46,74,this);
					else
						gc.drawImage(user.getImg(1).getImage(),user.getX() / 100, user.getY() / 100,46,74,this);
					break;
				case -1:
					gc.drawImage(user.getImg(index + 2).getImage(),user.getX() / 100, user.getY() / 100,46,74,this);
					break;
				case 1:

					gc.drawImage(user.getImg(index + 6).getImage(),user.getX() / 100, user.getY() / 100,46,74,this);
					break;
				}
			}
		}
		
	}
	
	private class KeyEventEx extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e)
		{

			boolean isWalk;
			int keydown = e.getKeyCode();
			switch(keydown) {
			//다중에 repaint가 아닌 오브젝트 보내기로 보낸다
			//이후 오브젝트를 받으면 그때 repaint를 해야한다
			//버퍼를 둔 이유는 
			case KeyEvent.VK_LEFT:
				user.setKeybuff(user.getKeybuff()|LEFT_PRESSED);//멀티키의 누르기 처리
				user.setIsLeft(true);
				isWalk = user.getIsWalk();

				if(!isWalk) {
					isWalk = true;
					user.setWalkStart(pretime);
					user.setIsWalk(isWalk);
				}
				//send?
				break;
			case KeyEvent.VK_RIGHT:
				user.setKeybuff(user.getKeybuff()|RIGHT_PRESSED);//멀티키의 누르기 처리
				user.setIsLeft(false);
				isWalk = user.getIsWalk();
				if(!isWalk) {
					isWalk = true;
					user.setWalkStart(pretime);
					user.setIsWalk(isWalk);
				}
				//send?
				break;
			case KeyEvent.VK_UP:
				break;
			case KeyEvent.VK_DOWN:
				break;
			case KeyEvent.VK_ALT:
				e.consume();
				int velocity = user.getVelocity();
				boolean isJump = user.getIsJump();
				
				if(!isJump) {
					isJump = true;
					velocity -= 500;
					user.setVelocity(velocity);
					user.setIsJump(isJump);
				}
				MapleStoryView.this.setFocusable(true);
				MapleStoryView.this.requestFocus();
				break;
			}
		}
		
		@Override
		public void keyReleased(KeyEvent e) 
		{				
			int keydown = e.getKeyCode();
			switch(keydown) {
			case KeyEvent.VK_LEFT:
				user.setKeybuff(user.getKeybuff()&(~LEFT_PRESSED));//멀티키의 누르기 처리
				user.setIsWalk(false);
				user.setWalkTime(0);
				break;
			case KeyEvent.VK_RIGHT:
				user.setKeybuff(user.getKeybuff()&(~RIGHT_PRESSED));//멀티키의 누르기 처리
				user.setIsWalk(false);
				user.setWalkTime(0);
				break;
			}
		}
	}
	
	class MyWindowAdapter extends WindowAdapter
	{
		// 윈도우를 닫기 위한 부가 클래스. 실제 닫는 동작은
		// setVisible(false);
		// dispose();
		// System.exit(0);
		// 이상 세 라인으로 이루어진다.
		//
		MyWindowAdapter() {}
		
		//
		
		public void windowClosing(WindowEvent e) {
			Window wnd = e.getWindow();
			
			//온전하게 로그아웃 됐다고 표시 [서버]
			//MapleStoryMsg obcm = new MapleStoryMsg("400");
			//SendObject(obcm);
			
			wnd.setVisible(false);
			wnd.dispose();
			System.exit(0);
		}
	}
	
}

