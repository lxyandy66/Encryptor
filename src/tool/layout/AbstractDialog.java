package tool.layout;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;


public abstract class AbstractDialog extends JFrame  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7058295238323126351L;

	public AbstractDialog(String title, int posX, int posY, int width) {
		setTitle(title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocation(posX, posY);
		setSize(width, width / 3);
	}

	public AbstractDialog(String title, JPanel parentComponent) {
		setTitle(title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocation(parentComponent.getLocationOnScreen());
		setSize(parentComponent.getWidth()/2, parentComponent.getWidth() / 5);
	}
	
	/**
	 * 用于与主窗口交互数据
	 * @author Mr_Li
	 */
	protected abstract void returnValue();
	
	public static AbstractDialog showProgress(String title,String Message,JPanel parentComponent) {
		JProgressBar progress = new JProgressBar(0, 100);
		JLabel text_msg = new JLabel(Message);
		progress.setIndeterminate(true);
		text_msg.setHorizontalAlignment(SwingConstants.CENTER);
		AbstractDialog dialog_progress = new AbstractDialog(title, parentComponent) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1781796791056677935L;

			@Override
			protected void returnValue() {
				// TODO Auto-generated method stub

			}
		};
		dialog_progress.setLayout(new GridLayout(3, 1, 3, 5));
		dialog_progress.add(text_msg);
		dialog_progress.add(progress);
		return dialog_progress;
	}
	

}
