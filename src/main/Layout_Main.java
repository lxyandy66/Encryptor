package main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import tool.PublicString;

public class Layout_Main extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6705359625636658428L;
	private JMenuBar menuBar = new JMenuBar();
	private JMenu menu_about = new JMenu(PublicString.ABOUT);
	private JMenuItem item_owner = new JMenuItem(PublicString.OWNER);
	private JButton bt_exit = new JButton("Exit");

	private Container Layout_container;
	private BorderLayout Layout_main = new BorderLayout(10, 10);
	private Layout_SymmetricOperator panel_file = new Layout_SymmetricOperator();
	private Layout_Text panel_text = new Layout_Text();
	private Layout_RSAEdu panel_rsaedu = new Layout_RSAEdu();
	private Layout_RSA panel_rsa=new Layout_RSA();
	private Layout_Hash panel_hash = new Layout_Hash();
	private Layout_Sign panel_sign = new Layout_Sign();
	private Layout_KeyExchangeServer panel_exchange = new Layout_KeyExchangeServer();
	private ImageIcon icon=new ImageIcon("icon.png");


	private JTabbedPane tabPane = new JTabbedPane();

	public Layout_Main(int posX, int posY, int width, int high) {
		setTitle(PublicString.APP_NAME);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(posX, posY);
		setSize(width, high);
		setJMenuBar(menuBar);
		setIconImage(icon.getImage());
		menu_about.add(item_owner);
		menuBar.add(menu_about);
		tabPane.addTab(PublicString.FILE, null, panel_file, "File Tab");
		tabPane.addTab(PublicString.TEXT, null, panel_text, "Text Tab");
		tabPane.addTab(PublicString.RSA_ALGORITHM+"edu", null, panel_rsaedu, "RSA_edu Tab");
		tabPane.addTab(PublicString.RSA_ALGORITHM, null,panel_rsa,"RSA Tab");//这里到时候根据main里面的参数来判断
		tabPane.addTab(PublicString.HASH, null, panel_hash, "Hash Tab");
		tabPane.addTab(PublicString.SIGN, null, panel_sign, "Sign Tab");
		tabPane.addTab(PublicString.KEY_EXCHANGE, null, panel_exchange, "Key Exchange Tab");

		Layout_container = getContentPane();
		Layout_container.setLayout(Layout_main);
		Layout_container.add(tabPane, BorderLayout.CENTER);
		Layout_container.add(bt_exit, BorderLayout.SOUTH);// 这里还能搞得好看点

		bt_exit.addActionListener(this);
		item_owner.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object event = e.getSource();
		if (event.equals(bt_exit)) {
			System.out.println("Exit Button pressed ,System exit");
			System.exit(0);
		} else if (event.equals(item_owner)) {
			System.out.println("Menu select.");
			JOptionPane.showMessageDialog(null, PublicString.DEV_INFO, PublicString.ABOUT,
					JOptionPane.INFORMATION_MESSAGE,icon);
		}
	}

}
