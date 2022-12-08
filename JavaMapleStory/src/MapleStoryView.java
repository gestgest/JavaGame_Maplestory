
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
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
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
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
import data.Monster;
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
	private MyMouseEvent myMouseEvent; 
	//////////////////////////////////////////////////////////////////////////////////////////

	//이미지 자료
	private ImageIcon UIImageIcon = new ImageIcon("src/res/img/GameUI.jpg");
	private ImageIcon UIhpIcon = new ImageIcon("src/res/img/hp.jpg");
	private ImageIcon backgroundIcon = new ImageIcon("src/res/img/Background.png");
	private ImageIcon maptileIcon = new ImageIcon("src/res/img/maptile/mapTiles5.png");

	
	private Image UIImage = UIImageIcon.getImage();
	private Image backgroundImage = backgroundIcon.getImage();
	private Image maptile = maptileIcon.getImage();
	private Image UIhpImage = UIhpIcon.getImage();
	
	//////////////////////////////////////////////////////////////////////////////////////////
	
	
	//해상도, 높이가 900이상이면 높은 해상도, 아니면 800, 600해상도
	private int width, height;
	double screenWidth , screenHeight;

	
	//단축기
	final int LEFT_PRESSED	=0x001;
	final int RIGHT_PRESSED	=0x002;
	

	// 현재시간
	private final int delay = 17; //17 / 1000초 : 58 (프레임 / 초)
	private final int gravity = 20;
	private final int jumpA = 100; //점프 가속도
	private final int animationTime = 125;
	private final int ATTACK_TIME = 250;
	private final int RESPAWN_TIME = 15000;
	private final int MAX_MONSTER_COUNT = 5; //최대 슬라임 개수
	
	
	//나중에 캐릭터 클래스 만들거임
	private User user;
	private HashMap<String, User> users = new HashMap<String, User>(3);
	private Vector<Monster> monsters = new Vector<Monster>(MAX_MONSTER_COUNT);


	private long pretime;
	private long spawnStart;
	private boolean isSpawn = false;
	private Clip clip;
	private boolean isLogin = false;
	private boolean isSend = false;
	
	
	
	
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
		myMouseEvent = new MyMouseEvent();
		contentPane = new GamePanel();
		
		setContentPane(contentPane);

		contentPane.addMouseListener(myMouseEvent);
		setVisible(true);
		
		try {
     	    File file = new File("src/res/bgm/slimedead.wav");
	        clip = AudioSystem.getClip();
	        clip.open(AudioSystem.getAudioInputStream(file));
	        FloatControl gainControl = 
	        	    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	        	gainControl.setValue(-20.0f); // Reduce volume by 10 decibels.
	        
        } catch (Exception e) {
            System.err.println("Put the music.wav file in the sound folder if you want to play background music, only optional!");
        }
		
		
		//네트워크 설정 [스레드 뺐음]★

		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());


			
			//로그인 메세지 보내는 기능
			init_user(username);
			users.put(username, user); //서버땐 뺴야함

			//네트워크 스레드 [받는 기능]
			ListenNetwork net = new ListenNetwork();
			net.start();


			FrameThread frameThread = new FrameThread();
			frameThread.start();
			//SendThread sendThread = new SendThread();
			//sendThread.start();
			
		} catch (NumberFormatException | IOException e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		setFocusable(true);
		requestFocus();
	}
	
	//유저 
	private void init_user(String username)
	{
		
		
		
		// 추가
		
		
		user = new User(username,0,0,0);
		
		
		user.setDegree(0);
		user.setKeybuff(0);
		init_server_user();
	}
	
	//초기 설정
	private void init_server_user()
	{
		MapleStoryMsg obcm = new MapleStoryMsg("100");
		
		obcm.setName(user.getName());
		obcm.setX(user.getX());
		obcm.setY(user.getY());
		obcm.setKeybuff(0);

		//gameScreen.repaint();//화면 리페인트
		
		SendObject(obcm);
	}
	
	//메인 메인 판넬
	private class GamePanel extends JPanel{
		private UIBar uibar; 
		public GamePanel()
		{
			setLayout(null);
			setBorder(new EmptyBorder(5, 5, 5, 5));
			uibar = new UIBar();
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
		
		public void painthp() {
			uibar.repaint();
		}
		
		
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
				g.clipRect(224, 20, 105 * user.getHP() / 100, 14);
				g.drawImage(UIhpImage,224,20,105,14,this);
			}
		}
	}
	
	

	// Server Message를 수신해서 화면에 표시 [받기]
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {
					User adduser = null;
					Object obcm = null;
					String msg = null;
					MapleStoryMsg cm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						System.out.println("입력 클래스 오류 : "+e.toString());
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
						if(!( cm.getName().equals(user.getName()) )) {
							users.put(cm.getName(), new User( cm.getName() ));
							
							adduser = users.get(cm.getName());
							adduser.setX(cm.getX());
							adduser.setY(cm.getY());
						}
						else {
							System.out.println("로그인");
							isLogin = true;
						}
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
						//x,y
						adduser = users.get(cm.getName());
						adduser.setX(cm.getX());
						adduser.setY(cm.getY());
						break;
					case "105":
						//is left
						adduser = users.get(cm.getName());
						adduser.setIsLeft(cm.getIsData());
						break;
					case "106":
						adduser = users.get(cm.getName());
						adduser.setIsJump(cm.getIsData());
						break;
					case "107":
						adduser = users.get(cm.getName());
						adduser.setIsWalk(cm.getIsData());
						break;
					case "108":
						adduser = users.get(cm.getName());
						adduser.setIsAttack(cm.getIsData());
						break;
					case "109":
						adduser = users.get(cm.getName());
						adduser.setIsDamaged(cm.getIsData());
						break;
					case "110":
						adduser = users.get(cm.getName());
						adduser.setType(cm.getType());
						break;
					case "400":
						//x,y
						users.remove(cm.getName());
						System.out.println(cm.getName() + " 제거");
						
						break;
					}
					
				} catch (IOException e) {
					System.out.println("입력 오류 : "+e.toString());
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

			if(!isSend) {
				isSend = true;
				oos.writeObject(ob);
				isSend = false;
			}
			else {
				return;
			}
		} catch (IOException e) {
			//서버오류
			//클래스 단위로 보내지 마라

			System.out.println(e.toString());
			System.exit(0);
		}
	}
	public void SendObjectbool(String code, String username, boolean bool) { // 서버로 메세지를 보내는 메소드
		try {

			if(!isSend) {
				isSend = true;
				MapleStoryMsg ob = new MapleStoryMsg(code);
				ob.setName(username);
				ob.setIsData(bool);
				isSend = false;
				oos.writeObject(ob);
			}
			else {
				return;
			}
			
		} catch (IOException e) {
			//서버오류
			//클래스 단위로 보내지 마라

			System.out.println(e.toString());
			System.exit(0);
		}
	}
	public void SendObjectType(String code, String username, int type) { // 서버로 메세지를 보내는 메소드
		try {
			if(!isSend) {
				isSend = true;
				MapleStoryMsg ob = new MapleStoryMsg(code);
				ob.setName(username);
				ob.setType(type);
				oos.writeObject(ob);
				isSend = false;
			}
			else {
				return;
			}
			
		} catch (IOException e) {
			//서버오류
			//클래스 단위로 보내지 마라

			System.out.println(e.toString());
			System.exit(0);
		}
	}
	
	//중력 : 나중에 네트워크 스레드에 추가 [사실 클라 시간 스레드는 필요없지 않을까? = 애니메이션 구현]
	class FrameThread extends Thread{
		int count = 0;
		@Override
		public void run(){
			try
			{
				while(true){
					pretime=System.currentTimeMillis();
					
					contentPane.painthp();
					gameScreen.repaint();//화면 리페인트
					process();//각종 충돌 처리
					keypross();//키 처리

					
					//프레임 유지
					if(System.currentTimeMillis()-pretime < delay) {
						Thread.sleep(delay - System.currentTimeMillis()+pretime);
						
						
						
					}
						//게임 루프를 처리하는데 걸린 시간을 체크해서 딜레이값에서 차감하여 딜레이를 일정하게 유지한다.
						//루프 실행 시간이 딜레이 시간보다 크다면 게임 속도가 느려지게 된다.
				}
			}
			catch (Exception e)
			{
				
				System.out.println(e.toString());
			}
		}
		
		
		private void process() {
			//나중에 중력 가속도 + 점프 넣을 예정
			int x = user.getX();
			int y = user.getY();
			int velocity = user.getVelocity();


			if(height * 100 - 8700 < y)
			{
				y = height * 100 - 8700;
				velocity = 0;
				user.setIsJump(false);
			}
			else {
				velocity += gravity;
				y += velocity;
						
			}
			
			if(!user.getIsAttack() || user.getIsJump())
				x += user.getDegree() * 100;


			//경계선
			if(x < 0)
				x = 0;
			else if(width * 100 - 4600 < x)
				x = width * 100 - 4600;
			
			if(y < 0)
				y = 0;
			if(height * 100 - 8700 < y)
			{
				y = height * 100 - 8700;
				velocity = 0;
				user.setIsJump(false);
			}
			//이 밖에도 땅처리
			

			//걷는 애니메이션
			if(user.getIsWalk())
			{
				long mytime = user.getWalkStart();
				mytime = pretime - mytime;
				if(animationTime * 4 <= mytime)
					mytime %= (animationTime * 4);

				
				user.setWalkTime(mytime);
			}
			if(user.getIsAttack())
			{
				long mytime = user.getAttackStart();
				mytime = pretime - mytime;
				if(ATTACK_TIME * 3 <= mytime)
				{
					user.setIsAttack(false);
					mytime = 0;
				}
				user.setAttackTime(mytime);
			}

			user.setX(x);
			user.setY(y);
			user.setVelocity(velocity);
			
			
			damagedProcess();
			monsterRespawn();
			monster_process();
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
		
		private void damagedProcess() {
			int w = user.getImg(0).getIconWidth();
			w *= 70;
			for(int i = 0; i < monsters.size(); i++) {
				Monster monster = monsters.get(i);
				
				//위치비교 [몬스터가 오른쪽]
				if(0 <= monster.getX() - user.getX() && monster.getX() - user.getX() <= w && !user.getIsDamaged()) {
					int hp = user.getHP();
					hp -= 10;
					
					user.setHP(hp);
					
					user.setIsDamaged(true);
					user.setDamagedStart(pretime);
				}
				//몬스터가 왼쪽
				else if(0 <= user.getX() - monster.getX()&& (user.getX() - monster.getX())  <= (w * 0.5) && !user.getIsDamaged()) {
					int hp = user.getHP();
					hp -= 10;
					//if
					user.setHP(hp);
					
					user.setIsDamaged(true);
					user.setDamagedStart(pretime);
				}
			}
			if(user.getIsDamaged()) {
				user.setDamagedTime((int) (pretime - user.getDamagedStart()));
				//무적시간
				if(1500 <= user.getDamagedTime()) {
					user.setIsDamaged(false);
				}
				
			}
		}
		
		//스폰
		private void monsterRespawn() {
			//spawnStart
			if(monsters.size() < MAX_MONSTER_COUNT)
			{
				if(!isSpawn) {
					spawnStart = pretime;
					isSpawn = true;

					return;
				}
				//대기 시간이 지나면 슬라임 리스폰
				if(RESPAWN_TIME <= pretime - spawnStart)
				{
					int count = (int) (Math.random() * 3 + 1);
					isSpawn = false;
					for(int i = 0; i < count && monsters.size() < 5; i++)
					{
						//이미지들
						Monster monster = new Monster();
						monsters.add(monster);
					}
				}
			}
		}
		private void monster_process() {

			for(int i = 0; i < monsters.size(); i++)
			{
				Monster monster = monsters.get(i);
				
				if(monster.getIsDead())
				{
					monster.setDeadTime((int)(pretime - monster.getDeadStart()));
					if(animationTime * 4 <= monster.getDeadTime()) {
						monsters.remove(i);
						continue;
					}
				}
				
				//아무 행동을 안한 경우 [idle상태 + 걷지도 않는 경우]
				if(!monster.getIsThinking() && !monster.getIsWalk())
				{
					monster.setThinkStart(pretime);
					monster.setIsThinking(true);
					int time = (int)(Math.random() * 2000 + 3000);
					monster.setThinkTime(time);
				}
				//행동
				else if(!monster.getIsWalk() && monster.getIsThinking())
				{
					//행동 개시
					if(monster.getThinkTime() < pretime - monster.getThinkStart())
					{
						int degree = (int)(Math.random() * 3) - 1;

						if(degree != 0)
						{
							monster.setWalkStart(pretime);
							int time = (int)(Math.random() * 2000 + 3000);
							monster.setWalkTime(time);
							monster.setDegree(degree);
							monster.setIsWalk(true);
						}
						monster.setIsThinking(false);
						
					}
				}
				else if(monster.getIsWalk())
				{ 
					int x = monster.getX();
					//다 걸음
					if(monster.getWalkTime() < pretime - monster.getWalkStart())
					{
						monster.setIsWalk(false);
						
					}
					///////////움직이는 함수
					x += monster.getDegree() * 100;

					if(x < 0)
						x = 0;
					else if(width * 100 - 4600 < x)
						x = width * 100 - 4600;
					
					monster.setX(x);
				}
				
			}
			
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
			this.addMouseListener(myMouseEvent);

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
			
			gc.drawImage(backgroundImage,-100,-100,width + 100,height + 100,this);//배경
			//벽

			drawTile();
			drawUser();
			drawMonster();
			
		}
		private void drawTile() {

			for(int i = 0; i < width / 50; i++)
				gc.drawImage(maptile,i * 50,500,50,50,this);//배경
		}
		
		//이미지 index 설정
		private void settype() {
			if(user.getIsAttack())
			{
				int index = (int)user.getAttackTime();
				index /= ATTACK_TIME;
				
				if(user.getIsLeft())
					user.setType(index + 12);
				else
					user.setType(index + 15);
				return;
			}
			
			if(user.getIsJump())
			{
				if(user.getIsLeft())
					user.setType(10);
				else
					user.setType(11);
				return;
			}


			int index = (int)user.getWalkTime();
			index /= animationTime;

			switch(user.getDegree()) {
			case -1:
				user.setType(index + 2);
				break;
			case 1:
				user.setType(index + 6);
				break;
			default:
				if(user.getIsLeft()) {

					user.setType(0);
				}
				else {
					user.setType(1);
				}
				break;
				
			}
		}
		
		private void drawUser()
		{
			settype();
			Iterator<String> keys = users.keySet().iterator();
			while(keys.hasNext())
			{
				String key = keys.next();
				User user = users.get(key);
				int x = user.getX() / 100;
				int y = user.getY() / 100;
				Image img;

				img = user.getImg(user.getType()).getImage();

				
				int w = img.getWidth(myGamePanel);
				int h = img.getHeight(myGamePanel);
				
				y = y - h;
				//System.out.println("이름 : " + user.getName()+ " x : " + x + " y : " + y + " w : " + w + " h : " + h);
				gc.drawImage(img,x,y,w,h,this);

				
			}
		}
		
		//몬스터 그리기
		private void drawMonster() 
		{
			for(int i = 0; i < monsters.size(); i++)
			{
				Monster monster = monsters.get(i);
				//대충 그리는 함수
				
				int degree = monster.getDegree();
				int x = monster.getX() / 100;
				int y = monster.getY() / 100;
				Image img;
				
				
				//이동하는 경우
				if(monster.getIsWalk())
				{
					int deltatime = (int) (pretime - monster.getWalkStart());
					int index = deltatime / animationTime;
					index %= 7;
					
					if(degree == 1) {
						 img = monster.getImg(19 + index).getImage();
					}
					else {
						 img = monster.getImg(3 + index).getImage();
					}
						
				}
				else {
					int deltatime = (int) (pretime - monster.getThinkStart());
					int index = deltatime / (animationTime * 2);
					index %= 3;
					if(degree == 1) {
						 img = monster.getImg(16 + index).getImage();
					}
					else {
						 img = monster.getImg(index).getImage();
					}
				}

				if(monster.getIsDead()) {
					int index = monster.getDeadTime() / animationTime;
					index %= 4;
					if(degree == 1) {
						 img = monster.getImg(28 + index).getImage();
					}
					else {
						 img = monster.getImg(12 + index).getImage();
					}
					
				}
				
				int w = img.getWidth(myGamePanel);
				int h = img.getHeight(myGamePanel);
				
				y = y - h;
				gc.drawImage(img,x,y,w,h,this);
			}
		}
		
	}
	
	class SendThread extends Thread{
		int count = 0;
		@Override
		public void run(){
			try
			{
				while(true){

					
					//프레임 유지
					if(System.currentTimeMillis()-pretime < 150) {
						Thread.sleep(150 - System.currentTimeMillis()+pretime);
						SendObjectType("110", user.getName(), user.getType());
						
						
						
					}
						//게임 루프를 처리하는데 걸린 시간을 체크해서 딜레이값에서 차감하여 딜레이를 일정하게 유지한다.
						//루프 실행 시간이 딜레이 시간보다 크다면 게임 속도가 느려지게 된다.
				}
			}
			catch (Exception e)
			{
				
				System.out.println(e.toString());
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

				if(isLogin) {

					MapleStoryMsg obcm = new MapleStoryMsg("103");
					obcm.setName(user.getName());
					obcm.setX(user.getX());
					obcm.setY(user.getY());
					
					SendObject(obcm);
				}
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
				if(isLogin) {

					MapleStoryMsg obcm = new MapleStoryMsg("103");
					obcm.setName(user.getName());
					obcm.setX(user.getX());
					obcm.setY(user.getY());
					
					SendObject(obcm);
				}
				
				break;
			case KeyEvent.VK_UP:
				break;
			case KeyEvent.VK_DOWN:
				break;
			case KeyEvent.VK_CONTROL:
				
				boolean isAttack = user.getIsAttack();
				if(!isAttack){
					isAttack = true;
					//몬스터가 있는지
					
					boolean isLeft = user.getIsLeft();
					int userX = user.getX();
					
					for(int i = 0; i < monsters.size(); i++) {
						Monster monster = monsters.get(i);
						int monsterX = monster.getX();
						if(isLeft)
						{
							//슬라임 기준만 정함
							if(userX - monsterX  <=  9000 && -2000 <= (userX - monsterX))
							{
								monster.setDeadStart(pretime);
								monster.setIsDead(true);
						        clip.loop(1);
								clip.start();
								break;
							}
						}
						else{
							if((monsterX - userX <=  9000) && (-2000 <= monsterX - userX))
							{
								monster.setDeadStart(pretime);
								monster.setIsDead(true);
						        clip.loop(1);
								clip.start();
								break;
							}
							
						}
					}
					user.setIsAttack(isAttack);
					user.setAttackStart(pretime);
					
				}
				
				break;
			case KeyEvent.VK_ALT:
				e.consume();
				boolean isJump = user.getIsJump();
				
				if(!isJump) {
					int velocity = user.getVelocity();
					isJump = true;
					velocity -= 500;
					user.setVelocity(velocity);
					user.setIsJump(isJump);
				}
				if(isLogin) {

					MapleStoryMsg obcm = new MapleStoryMsg("103");
					obcm.setName(user.getName());
					obcm.setX(user.getX());
					obcm.setY(user.getY());
					
					SendObject(obcm);
				}
				break;
			}

			SendObjectType("110", user.getName(), user.getType());
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

			SendObjectType("110", user.getName(), user.getType());
		}
	}
	
	class MyMouseEvent implements MouseListener, MouseMotionListener{
		public void mouseDragged(MouseEvent e) {
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			MapleStoryView.this.setFocusable(true);
			MapleStoryView.this.requestFocus();
		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
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
			MapleStoryMsg obcm = new MapleStoryMsg("400");
			obcm.setName(user.getName());
			if(oos != null)
				SendObject(obcm);
			
			wnd.setVisible(false);
			wnd.dispose();
			System.exit(0);
		}
	}
	
	
}

