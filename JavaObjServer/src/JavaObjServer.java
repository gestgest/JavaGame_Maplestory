//JavaObjServer.java ObjectStream ��� ä�� Server

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

	private ServerSocket socket; // ��������
	private Socket client_socket; // accept() ���� ������ client ����
	private Vector<UserService> UserVec = new Vector(); // ����� ����ڸ� ������ ����
	private static final int BUF_LEN = 128; // Windows ó�� BUF_LEN �� ����

	//����Ű
	final int LEFT_PRESSED	=0x001;
	final int RIGHT_PRESSED	=0x002;
	/**
	 * Launch the application.
	 */
	//////////////////////////////////////////////////////////

	private final int RESPAWN_TIME = 15000;
	private final int MAX_MONSTER_COUNT = 5; //�ִ� ������ ����
	
	/////////////////////
	//���ۺ���
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
				btnServerStart.setEnabled(false); // ������ ���̻� �����Ű�� �� �ϰ� ���´�
				txtPortNumber.setEnabled(false); // ���̻� ��Ʈ��ȣ ������ �ϰ� ���´�
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

	//���� �޴� ������
	// ���ο� ������ accept() �ϰ� user thread�� ���� �����Ѵ�.
	class AcceptServer extends Thread 
	{
		//������ ���� üũ X
		@SuppressWarnings("unchecked")
		public void run() 
		{
			while (true) 
			{ // ����� ������ ����ؼ� �ޱ� ���� while��
				try {
					
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept�� �Ͼ�� �������� ���� �����
					AppendText("���ο� ������ from " + client_socket);
					// User �� �ϳ��� Thread ����
					UserService new_user = new UserService(client_socket);
					
					UserVec.add(new_user); // ���ο� ������ �迭�� �߰�
					new_user.start(); // ���� ��ü�� ������ ����
					AppendText("���� ������ �� " + UserVec.size());
					
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}
	

	public void AppendText(String str) {
		// textArea.append("����ڷκ��� ���� �޼��� : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void AppendObject(MapleStoryMsg msg) {
		// textArea.append("����ڷκ��� ���� object : " + str+"\n");
		textArea.append("code = " + msg.getCode() + "\n");
		textArea.append("�̸� = " + msg.getName() + "\n");
		//textArea.append("data = " + msg.getData() + "\n");
		textArea.append("\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

///////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	//���� �ð� ���� ������
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
				//���� ����
				pretime=System.currentTimeMillis();
				
				//�޼��� �Ѹ��� [key ���۰� 1�ΰ�� 2�ΰ��]
				monsterRespawn();
				monster_process();
				//AppendText("�߾�");
				
				try {
					
					if(System.currentTimeMillis()-pretime < delay)
						Thread.sleep(delay - System.currentTimeMillis()+pretime);
				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					AppendText("�ð� ����");
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
				//��� �ð��� ������ ������ ������
				if(RESPAWN_TIME <= pretime - spawnStart)
				{
					int count = (int) (Math.random() * 3 + 1);
					isSpawn = false;
					for(int i = 0; i < count && monsters.size() < 5; i++)
					{
						//�̹�����
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
				
				//�ƹ� �ൿ�� ���� ��� [idle���� + ������ �ʴ� ���]
				if(!monster.getIsThinking() && !monster.getIsWalk())
				{
					monster.setThinkStart(pretime);
					monster.setIsThinking(true);
					int time = (int)(Math.random() * 2000 + 3000);
					monster.setThinkTime(time);
				}
				//�ൿ
				else if(!monster.getIsWalk() && monster.getIsThinking())
				{
					//�ൿ ����
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
					//�� ����
					if(monster.getWalkTime() < pretime - monster.getWalkStart())
					{
						monster.setIsWalk(false);
						
					}
					///////////�����̴� �Լ�
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
					AppendText("�ð� ����");
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
	
	
	
	
	
	// 1User �� �����Ǵ� 1Thread
	// Read One ���� ��� -> Write All
	class UserService extends Thread {

		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector<UserService> user_vc;
		
		//���� ���� Ŭ���� �ϳ� �־�� �ҵ�
		private User user;
		
		//�α���
		public UserService(Socket client_socket) {
			// TODO Auto-generated constructor stub
			// �Ű������� �Ѿ�� �ڷ� ����
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

		//�ޱ� AppendText("����"); 
		public void run() 
		{
			while (true) 
			{ 
				// ����� ������ ����ؼ� �ޱ� ���� while��
				try {
					Object obcm = null;
					String msg = null;
					MapleStoryMsg cm = null;
					
					if (socket == null)
						break;

					
					try {
						//100 / 101�� ���´µ� 100 / 100�� ����
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
					
					//�ޱ�
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
						//AppendText("������ : "+cm.getY());
						user.setX(cm.getX());
						break;
					case "102":
						//AppendText("������ : "+cm.getY());
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
						//�̵� ó�� �Լ�
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
						Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
						break;
					} catch (Exception ee) {
						break;
					} // catch�� ��
				} // �ٱ� catch����
			} // while
		} // run
		
		//�α���
		public void Login() {
			AppendText("���ο� ������ " + user.getName() + " ����.");
			
			//���� ������ ó���̸� user.Init();
			//100 User
			MapleStoryMsg msg = new MapleStoryMsg("100");
			msg.setName(user.getName());
			msg.setX(user.getX());
			msg.setY(user.getY());
			///msg.setImg(user.getImg());
			
			//user_vc.elementAt(ABORT)
			
			//���� �ֱ�
			WriteAllObject(msg);
			
			//�ٸ� ������ ���� �ֱ�
			for(int i = 0 ; i < user_vc.size(); i++)
			{
				//������ �޼����� �����ϸ� �ȵȴ�
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
			//���Ⱑ ����
			String msg;
			if(user.getName() == null) {
				msg = "������ ���������� �̸��� null�Դϴ�\n";
			}
			else
				msg = "[" + user.getName() + "]���� ���� �Ͽ����ϴ�.\n";
			MapleStoryMsg msg1 = new MapleStoryMsg("400");
			msg1.setName(user.getName());
			
			UserVec.removeElement(this); // Logout�� ���� ��ü�� ���Ϳ��� �����
			WriteAllObject(msg1); // ���� ������ �ٸ� User�鿡�� ����
			AppendText("����� " + "[" + user.getName() + "] ����. ���� ������ �� " + UserVec.size());
		}

		// ��� User�鿡�� ���. ������ UserService Thread�� WriteONe() �� ȣ���Ѵ�.
		public void WriteAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
			}
		}
		
		// ��� User�鿡�� Object�� ���. ä�� message�� image object�� ���� �� �ִ�
		public void WriteAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.WriteOneObject(ob);
			}
		}

		// ���� ������ User�鿡�� ���. ������ UserService Thread�� WriteONe() �� ȣ���Ѵ�.
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

		// Windows ó�� message ������ ������ �κ��� NULL �� ����� ���� �Լ�
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

		// UserService Thread�� ����ϴ� Client ���� 1:1 ����
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
				Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
			}
		}

		// �ӼӸ� ����
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
				Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
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
		
		//���� �Լ���
		public void moveUser() {
			//AppendText("Ű���� : " );
		}
		
	}

}
