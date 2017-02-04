package commonTool;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

public abstract class AbstractGridBagPanel extends JPanel implements ActionListener {

	//项目中所有布局以此为基础
	/**
	 * 
	 */
	private static final long serialVersionUID = 317499279116558548L;
	
	protected GridBagLayout layout_gridbag = new GridBagLayout();
	protected GridBagConstraints constraints = new GridBagConstraints();// 这个类是用来控制GridBag的
	
	public AbstractGridBagPanel(){
		constraints.weightx = 1;
		constraints.weighty = 1;
		setLayout(layout_gridbag);
	}
	
	protected void addComponent(Component component, int row, int column, int wid, int high) {
		// 用来添加控件，原来这个函数要自己写？？？******还有这个能不能变成个工具函数
		if (row < 0 || column < 0 || wid < 0 || high < 0)
			// throw new Exception("Arg is Wrong when add " +
			// component.toString() + " : must be non-negative number");
			return;// 懒得丢异常了，反正是private
		constraints.gridx = column;
		constraints.gridy = row;
		constraints.gridwidth = wid;
		constraints.gridheight = high;
		layout_gridbag.setConstraints(component, constraints);
		add(component);
	}
}
