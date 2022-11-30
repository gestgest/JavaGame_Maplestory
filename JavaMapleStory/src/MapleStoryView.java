
import java.awt.BorderLayout;
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
	//////////////////////////////////////////////////////////////////////////////////////////

	//이미지 자료
	private ImageIcon walk_1 = new ImageIcon("src/res/img/light-191.png");
	private ImageIcon walk_2 = new ImageIcon("src/res/img/light-194.png");
	private ImageIcon walk_3 = new ImageIcon("src/res/img/light-197.png");
	private ImageIcon walk_4 = new ImageIcon("src/res/img/light-200.png");
	private ImageIcon UIImageIcon = new ImageIcon("src/res/img/GameUI.png");
	private ImageIcon backgroundIcon = new ImageIcon("src/res/img/Background.png");
	private ImageIcon idleIcon =  new ImageIcon("src/res/img/debug.png");

	private Image UIImage = UIImageIcon.getImage();
	private Image backgroundImage = backgroundIcon.getImage();
	//////////////////////////////////////////////////////////////////////////////////////////
	
   //디버그
	private Image idleImage = idleIcon.getImage();
	
	//해상도, 높이가 900이상이면 높은 해상도, 아니면 800, 600해상도
	private int width, height;
	double screenWidth , screenHeight;

	
	//단축기
	final int LEFT_PRESSED	=0x001;
	final int RIGHT_PRESSED	=0x002;
	
	
	//나중에 캐릭터 클래스 만들거임
	private User user;
	private HashMap<String, User> users = new HashMap<String, User>(3);
	private int x = 0,y = 0;

	// 현재시간
	private long pretime;
	private final int delay = 17; //17 / 1000초 : 58 (프레임 / 초)
	
	int keybuff;//키 버퍼값
	
	
	
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
		
		if(screenHeight < 900) {
			width = 800; height = 600;
		}
		else {
			width = 1200; height = 900;
		}

		int screenX = (int) ((screenWidth - width) / 2);
		int screenY = (int) ((screenHeight - height) / 2);
		
		setBounds(screenX, screenY, width, height);
		setResizable(false);
		contentPane = new GamePanel();
		
		setContentPane(contentPane);
		
		setVisible(true);
		DebugTime debugTime = new DebugTime();
		//debugTime.start();
		
		//닉네임 설정
		user = new User(username,x,y,idleIcon);
		
		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());


			
			//로그인 메세지 보내는 기능
			MapleStoryMsg obcm = new MapleStoryMsg("100");
			obcm.setName(user.getName());
			obcm.setData(user.getName());
			SendObject(obcm);

			MapleStoryMsg obcm1 = new MapleStoryMsg("101");
			obcm.setCode("101");
			obcm.setName(user.getName());
			obcm.setData(user.getX());
			SendObject(obcm1);
			
			/*
			
			obcm.setCode("102");
			obcm.setData(user.getY());
			SendObject(obcm);
			
			obcm.setCode("103");
			obcm.setImg(user.getImg());
			SendObject(obcm);
			*/
			

			//네트워크 스레드 [받는 기능]
			ListenNetwork net = new ListenNetwork();
			net.start();

			

		} catch (NumberFormatException | IOException e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//메인 메인 판넬
	private class GamePanel extends JPanel{
		
		public GamePanel()
		{
			setLayout(null);
			setBorder(new EmptyBorder(5, 5, 5, 5));
			UIBar uibar = new UIBar();
			add(uibar);
			
			addKeyListener(new KeyEventEx());

			setFocusable(true);
			requestFocus();
		}
		
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			g.drawImage(backgroundImage,0,0,width,height,this);
			
			//유저 벡터 이미지 drawImage
			
			//벡터 함수
			//drawUser(g);
			//g.drawImage(idleImage,x,y,46,74,this);
			
		}
		
		private void drawUser(Graphics g) {
			Iterator<String> keys = users.keySet().iterator();
			while(keys.hasNext())
			{
				String key = keys.next();
				 
				User user = users.get(key);
				//이미지나 x와 y 하나라도 안왔으면?★
				
				g.drawImage(user.getImg().getImage(),user.getX(), user.getY(),46,74,this);
			}
		}
		
		//키 이벤트
		private class KeyEventEx extends KeyAdapter{
			@Override
			public void keyPressed(KeyEvent e)
			{
				
				int keydown = e.getKeyCode();
				switch(keydown) {
				//다중에 repaint가 아닌 오브젝트 보내기로 보낸다
				//이후 오브젝트를 받으면 그때 repaint를 해야한다
				
				//버퍼를 둔 이유는 
				case KeyEvent.VK_LEFT:
					keybuff|=LEFT_PRESSED;//멀티키의 누르기 처리
					break;
				case KeyEvent.VK_RIGHT:
					keybuff|=RIGHT_PRESSED;
					break;
				case KeyEvent.VK_UP:
					break;
				case KeyEvent.VK_DOWN:
					break;
				case KeyEvent.VK_ALT:
					y -= 30;
					contentPane.setFocusable(true);
					contentPane.requestFocus();
					break;
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) 
			{
				int keydown = e.getKeyCode();
				switch(keydown) {
				case KeyEvent.VK_LEFT:
					keybuff&=~LEFT_PRESSED;
					break;
				case KeyEvent.VK_RIGHT:
					keybuff&=~RIGHT_PRESSED;
					break;
				}
			}
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
			}
		}
	}
	
	

	// Server Message를 수신해서 화면에 표시 [받기]
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {
					User user;
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
						users.put(cm.getName(), new User( cm.getData() ));
						System.out.println("100받음");
						break;
					case "101":
						//x
						user = users.get(cm.getName());
						user.setX(cm.getCode());
						break;
					case "102":
						//y
						user = users.get(cm.getName());
						user.setY(cm.getCode());
						break;
					case "103":
						//이미지
						user = users.get(cm.getName());
						user.setImg(cm.getImg());
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

	//중력 : 나중에 네트워크 스레드에 추가 [사실 클라 시간 스레드는 필요없지 않을까? ]
	class DebugTime extends Thread{
		@Override
		public void run(){
			try
			{
				while(true){
					pretime=System.currentTimeMillis();

					contentPane.repaint();//화면 리페인트
					//process();//각종 충돌 처리
					//keyprocess();//키 처리

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
		public void windowClosing(WindowEvent e) {
			Window wnd = e.getWindow();
			
			//온전하게 로그아웃 됐다고 표시
			MapleStoryMsg obcm = new MapleStoryMsg("400");
			SendObject(obcm);
			
			wnd.setVisible(false);
			wnd.dispose();
			System.exit(0);
		}
	}
	
}

