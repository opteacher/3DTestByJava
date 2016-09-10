package mn;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class CFWGlobal extends JFrame {

	public CFWGlobal()	{
		CFWView vwTst = new CFWView();
		
		this.add(vwTst);
		this.addMouseListener(vwTst);
		this.addMouseMotionListener(vwTst);
		this.addMouseWheelListener(vwTst);
		this.setSize( 800, 600);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		CFWGlobal glTst = new CFWGlobal();
	}
}
