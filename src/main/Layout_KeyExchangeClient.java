package main;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import tool.PublicString;
import tool.network.ThreadClient;
import tool.util.StringProcessor;
import tool.crypto.DiffieHellmanEncryptor;
import tool.crypto.Text_Encryptor;

public class Layout_KeyExchangeClient extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8441391126980531130L;

	private static final String ENCRYPT_SIGNAL = "【本消息已加密】";
	private static final String DECRYPT_SIGNAL = "【本消息已解密】";

	private JTextField edit_address = new JTextField();
	private JTextField edit_portNo = new JTextField();
	private JTextField edit_msg = new JTextField();
	private JTextField edit_pubKey = new JTextField();
	private JTextField edit_prvKey = new JTextField();
	private JTextField edit_localKey = new JTextField();
	private JTextArea edit_console = new JTextArea(5, 5);
	private JLabel text_result = new JLabel();
	private JScrollPane panel_console = new JScrollPane(edit_console);

	private JButton bt_generate = new JButton(PublicString.GET_KEY);
	private JButton bt_send = new JButton(PublicString.SEND);
	private JButton bt_start = new JButton("启动");
	private JButton bt_shutdown = new JButton("关闭");
	private JButton bt_getLocalKey = new JButton("获取" + PublicString.LOCAL_KEY);

	private JCheckBox cb_needEncrypt = new JCheckBox(PublicString.ENCRYPT, false);

	private GridBagLayout layout_gridbag = new GridBagLayout();
	private GridBagConstraints constraints = new GridBagConstraints();// 这个类是用来控制GridBag的

	private ThreadClient client;
	private boolean needAutoEncrypt = false;

	private ItemListener cb_listen = new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			needAutoEncrypt = cb_needEncrypt.isSelected();
			text_result.setText("设置为" + (needAutoEncrypt ? "自动加密" : "手动加密") + "模式");
		}
	};

	private void addComponent(Component component, int row, int column, int wid, int high) {
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

	public Layout_KeyExchangeClient(int posX, int posY, int width, int high) {
		setTitle(PublicString.APP_NAME);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(posX, posY);
		setSize(width, high);

		setLayout(layout_gridbag);

		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.insets = new Insets(5, 1, 5, 1);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;

		addComponent(new JLabel(PublicString.ADDRESS), 0, 0, 1, 1);
		addComponent(new JLabel(PublicString.PORT_NO), 0, 2, 1, 1);
		addComponent(new JLabel(PublicString.PUBLIC_KEY), 6, 0, 1, 1);
		addComponent(new JLabel(PublicString.PRIVATE_KEY), 6, 2, 1, 1);
		addComponent(new JLabel(PublicString.INFORMATION), 10, 0, 1, 1);
		addComponent(new JLabel(PublicString.LOCAL_KEY), 8, 0, 1, 1);
		addComponent(text_result, 12, 1, 2, 1);

		addComponent(cb_needEncrypt, 10, 3, 1, 1);
		addComponent(bt_shutdown, 13, 0, 1, 1);
		addComponent(bt_generate, 13, 1, 1, 1);
		addComponent(bt_getLocalKey, 13, 2, 1, 1);
		addComponent(bt_send, 13, 3, 1, 1);
		addComponent(bt_start, 1, 3, 1, 1);

		constraints.fill = GridBagConstraints.BOTH;

		addComponent(edit_address, 1, 0, 2, 1);
		addComponent(edit_portNo, 1, 2, 1, 1);

		addComponent(edit_pubKey, 7, 0, 2, 1);
		addComponent(edit_prvKey, 7, 2, 2, 1);
		addComponent(edit_localKey, 9, 0, 4, 1);
		addComponent(edit_msg, 11, 0, 4, 1);
		constraints.ipady = 100;
		addComponent(panel_console, 2, 0, 4, 3);

		bt_generate.addActionListener(this);
		bt_send.addActionListener(this);
		bt_shutdown.addActionListener(this);
		bt_start.addActionListener(this);
		bt_getLocalKey.addActionListener(this);
		cb_needEncrypt.addItemListener(cb_listen);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(bt_send)) {
			if (client == null) {
				text_result.setText("连接未建立");
				return;
			}
			try {
				if (needAutoEncrypt && !edit_localKey.getText().equals(""))
					client.sendMessage(ENCRYPT_SIGNAL + Text_Encryptor.textProcess(edit_msg.getText().trim(), true,false,
							Text_Encryptor.AES_ENCRYPT, edit_localKey.getText().trim()));
				else
					client.sendMessage(edit_msg.getText().trim());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				client.sendMessage(edit_msg.getText().trim());
				e1.printStackTrace();
			}
		} else if (e.getSource().equals(bt_start)) {
			if (!ThreadClient.isLegalAddress(edit_address.getText().trim())) {
				JOptionPane.showMessageDialog(null, "输入的IP不合法", "失败", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (client != null&&client.isConnect()) {// 这里可能有点问题，多线程服务器终止了之后是不是这个对象还存在
				JOptionPane.showMessageDialog(null, "已经有连接存在", "失败", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				client=null;
				client = new ThreadClient(edit_address.getText().trim(),
						Integer.valueOf(edit_portNo.getText().trim())) {

					@Override
					public synchronized void printMsg(String str) {
						// TODO Auto-generated method stub
						edit_console.append("\nServer: " + str);
						if (str.indexOf(ThreadClient.EXIT_SIGNAL) != -1) {
							client.shutdown();
							client = null;
							return;
						}
						if (needAutoEncrypt && !edit_localKey.getText().trim().equals("")) {
							if (str.indexOf(ENCRYPT_SIGNAL) != -1) {
								try {
									String decryptMsg = Text_Encryptor.textProcess(
											str.substring(str.indexOf(ENCRYPT_SIGNAL) + ENCRYPT_SIGNAL.length()), false,false,
											Text_Encryptor.AES_ENCRYPT, edit_localKey.getText().trim());
									edit_console.append(DECRYPT_SIGNAL + decryptMsg);
									if (decryptMsg.indexOf(ThreadClient.EXIT_SIGNAL) != -1) {
										client.shutdown();
										client = null;
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									edit_console.append("自动解密失败");
									e.printStackTrace();
								}
							}
						}
					}
				};
				new Thread(client).start();
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getSource().equals(bt_shutdown)) {
			if (client == null) {
				text_result.setText("连接未建立");
				System.exit(0);
				return;
			}
			edit_console.append("\nSystem has been Shutdown");
			client.shutdown();
			client = null;
			System.exit(0);
		} else if (e.getSource().equals(bt_generate)) {
			try {
				Dialog_DH dialog = new Dialog_DH(true, this.getX(), this.getY(), 420) {
					/**
					 * 
					 */
					private static final long serialVersionUID = 7049282222384907912L;

					@Override
					public String returnValue(boolean isGetPubKey) throws Exception {
						// TODO Auto-generated method stub
						edit_pubKey.setText(DiffieHellmanEncryptor.getPublicKeyString(
								this.result[2].modPow(this.result[0], this.result[1]), this.result[1], this.result[2]));
						edit_prvKey.setText(DiffieHellmanEncryptor.getPrivateKeyString(this.result[0], this.result[1],
								this.result[2]));
						return edit_pubKey.getText();
					}
				};
				dialog.setVisible(true);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getSource().equals(bt_getLocalKey)) {
			String otherPubKey = JOptionPane.showInputDialog(null, "请输入对方的公钥", "信息需要", JOptionPane.INFORMATION_MESSAGE);
			if (otherPubKey == null || otherPubKey.trim().equals("")) {
				JOptionPane.showMessageDialog(null, PublicString.INPUT_EMPTY, PublicString.ERROR,
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				edit_localKey.setText(StringProcessor.objectToBase64(
						DiffieHellmanEncryptor.getLocalKey((DHPublicKey) StringProcessor.base64ToObject(otherPubKey),
								(DHPrivateKey) StringProcessor.base64ToObject(edit_prvKey.getText().trim()))));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "生成失败", PublicString.ERROR, JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
	}

}
