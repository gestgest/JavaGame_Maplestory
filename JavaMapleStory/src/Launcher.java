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

//System.out.println("");
//리스트형식 변수를 오브젝트에 보내지 마라 - 됐다 안됐다 그럼