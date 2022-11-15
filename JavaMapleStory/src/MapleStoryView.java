
// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

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
import java.awt.Color;

import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JToggleButton;
import javax.swing.JList;

public class MapleStoryView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String UserName;
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	
	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	//펜
	private GamePanel contentPane;

	private ImageIcon walk_1 = new ImageIcon("src/res/img/light-191.png");
	private ImageIcon walk_2 = new ImageIcon("src/res/img/light-194.png");
	private ImageIcon walk_3 = new ImageIcon("src/res/img/light-197.png");
	private ImageIcon walk_4 = new ImageIcon("src/res/img/light-200.png");
	
	   //디버그
	private ImageIcon idleIcon =  new ImageIcon("src/res/img/debug.png");
	private Image idleImage = idleIcon.getImage();
	
	private int x = 100,y = 100;

	
	
	/**
	 * Create the frame.
	 */
	//매개변수 String username , String ip_addr, String port_no
	public MapleStoryView(String username, String ip_addr, String port_no) {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 100, 1200, 900);
		contentPane = new GamePanel();
		
		setContentPane(contentPane);
		
		setVisible(true);
		DebugTime debugTime = new DebugTime();
		debugTime.start();
		
		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			
			//로그인 메세지 보내는 기능
			MapleStoryMsg obcm = new MapleStoryMsg(UserName, "100", "Hello");
			SendObject(obcm);
			
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
			
			addKeyListener(new KeyEventEx());

			setFocusable(true);
			requestFocus();
		}
		
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			g.drawImage(idleImage,x,y,46,74,this);
		}
		
		//키 이벤트
		private class KeyEventEx extends KeyAdapter{
			@Override
			public void keyPressed(KeyEvent e)
			{
				
				int keydown = e.getKeyCode();
				switch(keydown) {
				case KeyEvent.VK_LEFT:
					x -= 3;
					contentPane.repaint();
					break;
				case KeyEvent.VK_RIGHT:
					x += 3;
					contentPane.repaint();
					break;
				case KeyEvent.VK_UP:
					break;
				case KeyEvent.VK_DOWN:
					break;
				case KeyEvent.VK_ALT:
					y -= 30;
					contentPane.repaint();
					contentPane.setFocusable(true);
					contentPane.requestFocus();
					break;
				}
			}
		}
	}
	
	

	// Server Message를 수신해서 화면에 표시 [읽기]
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {
					
					
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
						msg = String.format("[%s] %s", cm.getId(), cm.getData());
					} 
					else
						continue;
					//switch () - 프로토콜 분류
					
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
	
	//중력 : 나중에 네트워크 스레드에 추가
	class DebugTime extends Thread{
		int deltatime = 0;
		@Override
		public void run() {
			long time = System.currentTimeMillis();
			while (true) {
				try {
					long aftertime = System.currentTimeMillis();
					if(aftertime - time <= 0) {
						continue;
					}
					deltatime += (int)(aftertime - time);
					//System.out.println(deltatime);
					if(10 <= deltatime)
					{
						deltatime -= 10;
						//디버그
						if(y <= 500)
							y += 4;
						contentPane.repaint();
					}
					time = aftertime;
					
				}catch (Exception e) {
					break;
				}
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

	// Server에게 network으로 전송
	public void SendMessage(String msg) {
		try {
			
			//보내기
			MapleStoryMsg obcm = new MapleStoryMsg(UserName, "200", msg);
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
			// textArea.append("메세지 송신 에러!!\n");
			//에러메세지
		}
	}
}
