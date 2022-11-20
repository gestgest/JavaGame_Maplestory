

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

public class LoginClient extends JFrame {

	private JTextField txtUserName;
	private JTextField txtIpAddress;
	private JTextField txtPortNumber;

	ImageIcon backgroundIcon = new ImageIcon("src/res/img/LoginBackground.jpg");
	Image backgroundImg = backgroundIcon.getImage();
	
	//해상도, 높이가 900이상이면 높은 해상도, 아니면 800, 600해상도
	private int width, height;
	double screenWidth , screenHeight;
	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public LoginClient() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = dim.getWidth();	
		screenHeight = dim.getHeight();
		
		if(screenHeight < 900) {
			width = 800; height = 600;
		}
		else {
			width = 1200; height = 900;
		}
		int x = (int) ((screenWidth - width) / 2);
		int y = (int) ((screenHeight - height) / 2);
		
		setBounds(x, y, width, height);
		setResizable(false);
		
		LoginPanel contentPane = new LoginPanel();
		setContentPane(contentPane);
		
	}
	
	class LoginPanel extends JPanel
	{
		private int px, py;
		public LoginPanel()
		{
			
			px = width * 19 / 32;
			py = height * 7 / 24;
			
			setBorder(new EmptyBorder(5, 5, 5, 5));
			setLayout(null);
			
			
			JLabel lblNewLabel = new JLabel("User Name");
			lblNewLabel.setBounds(12+px, 39+py, 82, 33);
			add(lblNewLabel);
			
			
			txtUserName = new JTextField();
			txtUserName.setHorizontalAlignment(SwingConstants.CENTER);
			txtUserName.setBounds(101+px, 39+py, 116, 33);
			add(txtUserName);
			txtUserName.setColumns(10);
			
			JLabel lblIpAddress = new JLabel("IP Address");
			lblIpAddress.setBounds(12+px, 100+py, 82, 33);
			add(lblIpAddress);
			
			txtIpAddress = new JTextField();
			txtIpAddress.setHorizontalAlignment(SwingConstants.CENTER);
			txtIpAddress.setText("127.0.0.1");
			txtIpAddress.setColumns(10);
			txtIpAddress.setBounds(101+px, 100+py, 116, 33);
			add(txtIpAddress);
			
			JLabel lblPortNumber = new JLabel("Port Number");
			lblPortNumber.setBounds(12+px, 163+py, 82, 33);
			add(lblPortNumber);
			
			txtPortNumber = new JTextField();
			txtPortNumber.setText("30000");
			txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
			txtPortNumber.setColumns(10);
			txtPortNumber.setBounds(101+px, 163+py, 116, 33);
			add(txtPortNumber);
			
			JButton btnConnect = new JButton("Connect");
			btnConnect.setBounds(12+px, 223+py, 205, 38);
			add(btnConnect);

			
			Myaction action = new Myaction();
			btnConnect.addActionListener(action);
			txtUserName.addActionListener(action);
			txtIpAddress.addActionListener(action);
			txtPortNumber.addActionListener(action);
			
			repaint();
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(backgroundImg,0,0,width,height,this);
		}
	}
	
	class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			String username = txtUserName.getText().trim();
			String ip_addr = txtIpAddress.getText().trim();
			String port_no = txtPortNumber.getText().trim();
			MapleStoryView view = new MapleStoryView(username, ip_addr, port_no);
			setVisible(false);
		}
	}
}