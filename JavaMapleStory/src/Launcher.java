import java.awt.EventQueue;

//클라
public class Launcher {
	public static void main(String args[])
	{
		EventQueue.invokeLater(new Runnable() {
		public void run() {
			try {
				LoginClient frame = new LoginClient();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});
	}

}
