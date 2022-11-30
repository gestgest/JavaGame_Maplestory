//JavaObjServer.java ObjectStream 기반 채팅 Server

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import data.MapleStoryMsg;
import data.User;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class JavaObjServer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTextArea textArea;
	private JTextField txtPortNumber;

	private ServerSocket socket; // 서버소켓
	private Socket client_socket; // accept() 에서 생성된 client 소켓
	private Vector<UserService> UserVec = new Vector(); // 연결된 사용자를 저장할 벡터
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의

	//단축키
	final int LEFT_PRESSED	=0x001;
	final int RIGHT_PRESSED	=0x002;
	/**
	 * Launch the application.
	 */
	//////////////////////////////////////////////////////////
	
	/////////////////////
	//자작변수
	long pretime;
	int delay = 150; //56프레임
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JavaObjServer frame = new JavaObjServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public JavaObjServer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 338, 440);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 300, 298);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(13, 318, 87, 26);
		contentPane.add(lblNewLabel);

		txtPortNumber = new JTextField();
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setText("30000");
		txtPortNumber.setBounds(112, 318, 199, 26);
		contentPane.add(txtPortNumber);
		txtPortNumber.setColumns(10);

		JButton btnServerStart = new JButton("Server Start");
		btnServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
				} catch (NumberFormatException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AppendText("Chat Server Running..");
				btnServerStart.setText("Chat Server Running..");
				btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
				txtPortNumber.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
				
				TimeThread timeThread = new TimeThread();
				timeThread.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	//서버 받는 스레드
	// 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
	class AcceptServer extends Thread 
	{
		//컴파일 오류 체크 X
		@SuppressWarnings("unchecked")
		public void run() 
		{
			while (true) 
			{ // 사용자 접속을 계속해서 받기 위해 while문
				try {
					
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
					AppendText("새로운 참가자 from " + client_socket);
					// User 당 하나씩 Thread 생성
					UserService new_user = new UserService(client_socket);
					
					UserVec.add(new_user); // 새로운 참가자 배열에 추가
					new_user.start(); // 만든 객체의 스레드 실행
					AppendText("현재 참가자 수 " + UserVec.size());
					
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}
	

	public void AppendText(String str) {
		// textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void AppendObject(MapleStoryMsg msg) {
		// textArea.append("사용자로부터 들어온 object : " + str+"\n");
		textArea.append("\n");
		textArea.append("code = " + msg.getCode() + "\n");
		textArea.append("id = " + msg.getName() + "\n");
		//textArea.append("data = " + msg.getData() + "\n");
		textArea.append("\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

///////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	//서버 시간 관리 스레드
	class TimeThread extends Thread 
	{
		@Override
		public void run() 
		{
			while (true) 
			{
				//서버 관리
				pretime=System.currentTimeMillis();
				
				//메세지 뿌리기 [key 버퍼가 1인경우 2인경우]
				moveUser();

				//AppendText("삐약");
				
				try {
					
					if(System.currentTimeMillis()-pretime < delay)
						Thread.sleep(delay - System.currentTimeMillis()+pretime);
				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					AppendText("시간 오류");
				}
			}
		}
		
		//이동함수
		public void moveUser()
		{
			int x;
			for(int i = 0; i < UserVec.size(); i++)
			{
				UserService us = UserVec.get(i);
				User u = us.getUser();
				MapleStoryMsg msg;
				switch(u.getKeybuff()) {
				case LEFT_PRESSED:
					x = u.getX()- 2;
					u.setX(x);
					msg = new MapleStoryMsg("101");
					msg.setX(x);
					msg.setName(u.getName());

					us.WriteAllObject(msg);
					break;
					
				case RIGHT_PRESSED:
					x = u.getX()+ 2;
					u.setX(x);
					msg = new MapleStoryMsg("101");
					msg.setX(x);
					msg.setName(u.getName());

					us.WriteAllObject(msg);
					break;
				}
				
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	//////////////////////////////////////////////////////////////////
	
	
	
	
	
	// 1User 당 생성되는 1Thread
	// Read One 에서 대기 -> Write All
	class UserService extends Thread {

		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector user_vc;
		
		//유저 정보 클래스 하나 넣어야 할듯
		private User user;
		
		//로그인
		public UserService(Socket client_socket) {
			// TODO Auto-generated constructor stub
			// 매개변수로 넘어온 자료 저장
			this.client_socket = client_socket;
			this.user_vc = UserVec;
			
			
			try {
				oos = new ObjectOutputStream(client_socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(client_socket.getInputStream());

			} catch (Exception e) {
				AppendText("userService error");
			}
		}

		//받기 AppendText("ㄹㄹ"); 
		public void run() 
		{
			while (true) 
			{ 
				// 사용자 접속을 계속해서 받기 위해 while문
				try {
					Object obcm = null;
					String msg = null;
					MapleStoryMsg cm = null;
					
					if (socket == null)
						break;

					
					try {
						//100 / 101을 보냈는데 100 / 100이 나옴
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}

					
					if (obcm == null)
						break;
					
					if (obcm instanceof MapleStoryMsg) {
						cm = (MapleStoryMsg) obcm;
						//AppendObject(cm);
					} else
						continue;
					
					//받기
					//login
					switch(cm.getCode()) {
					case "100":
						user = new User(cm.getName());
						user.setX(cm.getX());
						user.setY(cm.getY());
						user.setImg(cm.getImg());
						user.setKeybuff(cm.getKeybuff());
						Login();
						break;
					case "101":
					case "102":
						//AppendText("데이터 : "+cm.getY());
					case "103":
						//WriteAllObject(cm);
						break;
					case "104":
						//이동 처리 함수
						user.setKeybuff(cm.getKeybuff());
						
						//WriteAllObject(cm);
						break;
					case "110":
						//이동 처리 함수
						//WriteAllObject(cm);
						break;
					}
					
					if (cm.getCode().matches("200")) {
					} else if (cm.getCode().matches("300")) {
						WriteAllObject(cm);
					} else if (cm.getCode().matches("400")) { // logout message 처리 로그아웃
						Logout();
						break;
					}  else if (cm.getCode().matches("500")) {
					}  else if (cm.getCode().matches("600")) {
					}  else if (cm.getCode().matches("700")) {
					} 
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // 에러가난 현재 객체를 벡터에서 지운다
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			} // while
		} // run
		
		//로그인
		public void Login() {
			AppendText("새로운 참가자 " + user.getName() + " 입장.");
			
			//만약 유저가 처음이면 user.Init();
			//100 User
			MapleStoryMsg msg = new MapleStoryMsg("100");
			msg.setName(user.getName());
			msg.setX(user.getX());
			msg.setY(user.getY());
			msg.setImg(user.getImg());
			
			//정보 넣기
			WriteAllObject(msg);
		}

		public void Logout() {
			//여기가 문제
			String msg = "[" + user.getName() + "]님이 퇴장 하였습니다.\n";
			UserVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
			//WriteAll(msg); // 나를 제외한 다른 User들에게 전송
			AppendText("사용자 " + "[" + user.getName() + "] 퇴장. 현재 참가자 수 " + UserVec.size());
		}

		// 모든 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void WriteAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
			}
		}
		
		// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public void WriteAllObject(Object ob) {
			
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				AppendText(i+ "번 사람에게 보내기");
				user.WriteOneObject(ob);
			}
		}

		// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void WriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.WriteOne(str);
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
			}
			for (i = 0; i < bb.length; i++)
				packet[i] = bb[i];
			return packet;
		}

		// UserService Thread가 담당하는 Client 에게 1:1 전송
		public void WriteOne(String msg) {
			try {
				// dos.writeUTF(msg);
//				byte[] bb;
//				bb = MakePacket(msg);
//				dos.write(bb, 0, bb.length);
				MapleStoryMsg obcm = new MapleStoryMsg("200");
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
//					dos.close();
//					dis.close();
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}

		// 귓속말 전송
		public void WritePrivate(String msg) {
			try {
				MapleStoryMsg obcm = new MapleStoryMsg("200");
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		public void WriteOneObject(Object ob) {
			try {
			    oos.writeObject(ob);
			} 
			catch (IOException e) {
				AppendText("oos.writeObject(ob) error");		
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout();
			}
		}
		
		public User getUser() { return user; }
		
		//자작 함수들
		public void moveUser() {
			AppendText("키버퍼 : " + user.getKeybuff());
			//AppendText("키버퍼 : " );
		}
		
	}

}
