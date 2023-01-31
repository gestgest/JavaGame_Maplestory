//JavaObjServer.java ObjectStream 기반 채팅 Server

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import data.MapleStoryMsg;
import data.Monster;
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

	private final int RESPAWN_TIME = 15000;
	private final int MAX_MONSTER_COUNT = 5; //최대 슬라임 개수
	
	/////////////////////
	//자작변수
	long pretime;
	int delay = 30; 

	private Vector<Monster> monsters = new Vector<Monster>(MAX_MONSTER_COUNT);
	
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
				
				//TimeThread timeThread = new TimeThread();
				//timeThread.start();
				//SendThread sendThread = new SendThread();
				//sendThread.start();
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
		textArea.append("code = " + msg.getCode() + "\n");
		textArea.append("이름 = " + msg.getName() + "\n");
		//textArea.append("data = " + msg.getData() + "\n");
		textArea.append("\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

///////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	//서버 시간 관리 스레드
	class TimeThread extends Thread 
	{
		private boolean isSpawn = false;
		private long spawnStart;
		private final int animationTime = 125;
		@Override
		public void run() 
		{
			while (true) 
			{
				//서버 관리
				pretime=System.currentTimeMillis();
				
				//메세지 뿌리기 [key 버퍼가 1인경우 2인경우]
				monsterRespawn();
				monster_process();
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
					else if(800 * 100 - 4600 < x)
						x = 800 * 100 - 4600;
					
					monster.setX(x);
				}

			}
			
		}
	}
	
	private class SendThread extends Thread{
		@Override
		public void run() 
		{
			while (true) 
			{
				
				sendObject();
				try {
					
					if(System.currentTimeMillis()-pretime < 100)
						Thread.sleep(100 - System.currentTimeMillis()+pretime);
				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					AppendText("시간 오류");
				}
			}
		}
		
		private void sendObject() {
			MapleStoryMsg msg  = new MapleStoryMsg("300");
			
			
			for(int i = 0; i < UserVec.size(); i++) {
				UserService userservice = UserVec.get(i);
				
				for(int j = 0; j < monsters.size(); j++) {
					userservice.WriteOneObject(msg);
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
		private Vector<UserService> user_vc;
		
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
						AppendObject(cm);
					} else
						continue;
					
					//받기
					//login
					switch(cm.getCode()) {
					case "100":

						user = new User(cm.getName());
						user.setX(cm.getX());
						user.setY(cm.getY());
						//user.setImg(cm.getImg());
						user.setKeybuff(cm.getKeybuff());
						Login();
						break;
					case "101":
						//AppendText("데이터 : "+cm.getY());
						user.setX(cm.getX());
						break;
					case "102":
						//AppendText("데이터 : "+cm.getY());
						user.setY(cm.getY());
						break;
					case "103":
						//WriteAllObject(cm);
						user.setX(cm.getX());
						user.setY(cm.getY());
						//AppendText(user.getX() + " " + user.getY());
						WriteOhtersObject(cm);
						break;
					case "104":
						//이동 처리 함수
						user.setKeybuff(cm.getKeybuff());
						//WriteAllObject(cm);
						break;
					case "105":
					case "106":
					case "107":
					case "108":
					case "109":
						WriteOhtersObject(cm);
						break;
					case "110":
						WriteOhtersObject(cm);
						break;
					case "113":
						WriteOhtersObject(cm);
						break;
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
			///msg.setImg(user.getImg());
			
			//user_vc.elementAt(ABORT)
			
			//정보 넣기
			WriteAllObject(msg);
			
			//다른 유저도 정보 넣기
			for(int i = 0 ; i < user_vc.size(); i++)
			{
				//무조건 메세지는 재탕하면 안된다
				MapleStoryMsg msg1 = new MapleStoryMsg("100");
				User searchuser = user_vc.get(i).getUser();
				
				msg1.setName(searchuser.getName());
				msg1.setX(searchuser.getX());
				msg1.setY(searchuser.getY());
				
				
				WriteOneObject(msg1);
			}

			
			
			//
		}

		public void Logout() {
			//여기가 문제
			String msg;
			if(user.getName() == null) {
				msg = "누군가 퇴장했지만 이름이 null입니다\n";
			}
			else
				msg = "[" + user.getName() + "]님이 퇴장 하였습니다.\n";
			MapleStoryMsg msg1 = new MapleStoryMsg("400");
			msg1.setName(user.getName());
			
			UserVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
			WriteAllObject(msg1); // 나를 제외한 다른 User들에게 전송
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
		public void WriteOhtersObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				
				UserService user = (UserService) user_vc.elementAt(i);
				if(!user.getUser().getName().equals(this.user.getName()))
					user.WriteOneObject(ob);
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
			//AppendText("키버퍼 : " );
		}
		
	}

}
