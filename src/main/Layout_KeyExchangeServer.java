package main;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import tool.PublicString;
import tool.ServerManager;
import tool.util.StringProcessor;
import tool.crypto.DiffieHellmanEncryptor;
import tool.crypto.Text_Encryptor;
import tool.layout.AbstractGridBagPanel;

public class Layout_KeyExchangeServer extends AbstractGridBagPanel implements ActionListener {

	/**
	 * 
	 */
	private static final String ENCRYPT_SIGNAL = "【本消息已加密】";
	private static final String DECRYPT_SIGNAL = "【本消息已解密】";
	private static final long serialVersionUID = -1780213938608030771L;
	private JLabel text_result = new JLabel();
	private JTextArea edit_console = new JTextArea();
	// private JTextField edit_address = new JTextField();
	private JTextField edit_pubKey = new JTextField();
	private JTextField edit_prvKey = new JTextField();
	private JTextField edit_localKey = new JTextField();
	private JTextField edit_msg = new JTextField();

	// private JButton bt_try = new JButton(PublicString.CONNECT);
	private JButton bt_generate = new JButton(PublicString.GET_KEY);
	private JButton bt_send = new JButton(PublicString.SEND);
	private JButton bt_start = new JButton("启动");
	private JButton bt_shutdown = new JButton("关闭");
	private JButton bt_getLocalKey = new JButton("获取" + PublicString.LOCAL_KEY);

	private JScrollPane panel_console = new JScrollPane(edit_console);
	private JCheckBox cb_needEncrypt = new JCheckBox(PublicString.ENCRYPT, false);


	private boolean needAutoEncrypt = false;
	private boolean isOnline = false;

	private ServerManager server = new ServerManager() {

		@Override
		public synchronized void printConsole(String str) {
			// TODO Auto-generated method stub
			edit_console.append("\n" + str);
			if (needAutoEncrypt && !edit_localKey.getText().trim().equals("")) {
				if (str.indexOf(ENCRYPT_SIGNAL) != -1) {
					try {
						edit_console.append(DECRYPT_SIGNAL + Text_Encryptor.textProcess(
								str.substring(str.indexOf(ENCRYPT_SIGNAL) + ENCRYPT_SIGNAL.length()), false,
								Text_Encryptor.AES_ENCRYPT, edit_localKey.getText().trim()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						edit_console.append("自动解密失败");
						e.printStackTrace();
					}
				}
			}
		}

	};

	private Thread tr_server = new Thread(server);
	// 监听器部分

	private ItemListener cb_listen = new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			needAutoEncrypt = cb_needEncrypt.isSelected();
			text_result.setText("设置为" + (needAutoEncrypt ? "自动加密" : "手动加密") + "模式");
		}
	};

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(bt_start)) {
			if (isOnline || tr_server.isAlive()) {
				text_result.setText("服务器已启动");
				return;
			}
			tr_server.start();
			isOnline = true;
		} else if (e.getSource().equals(bt_shutdown)) {
			try {
				if (!isOnline || !tr_server.isAlive()) {
					text_result.setText("服务器已关闭");
					return;
				}
				server.shutdown();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getSource().equals(bt_send)) {
			try {
				if (needAutoEncrypt&&!edit_localKey.getText().equals(""))
					server.sendBroardcast(ENCRYPT_SIGNAL+Text_Encryptor.textProcess(edit_msg.getText().trim(), true,
							Text_Encryptor.AES_ENCRYPT, edit_localKey.getText().trim()));
				else
					server.sendBroardcast(edit_msg.getText().trim());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				server.sendBroardcast(edit_msg.getText().trim());
				e1.printStackTrace();
			}

		} else if (e.getSource().equals(bt_generate)) {
			try {
				Dialog_DH dialog = new Dialog_DH(true, this.getX(), this.getY(), 420) {

					/**
					 * 
					 */
					private static final long serialVersionUID = 8194890054088219625L;

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

	public Layout_KeyExchangeServer() {
		super();
		// panel_console.add(new JScrollPane(edit_console));

		constraints.fill = GridBagConstraints.HORIZONTAL;// GridBagConstraints.BOTH;//
		constraints.anchor = GridBagConstraints.CENTER;
		addComponent(bt_generate, 11, 1, 1, 1);
		addComponent(bt_send, 11, 2, 1, 1);
		addComponent(bt_shutdown, 11, 0, 1, 1);
		addComponent(bt_start, 11, 3, 1, 1);
		addComponent(bt_getLocalKey, 6, 3, 1, 1);

		constraints.fill = GridBagConstraints.BOTH;

		addComponent(new JLabel(PublicString.PUBLIC_KEY), 4, 0, 1, 1);
		addComponent(new JLabel(PublicString.PRIVATE_KEY), 4, 2, 1, 1);
		addComponent(edit_pubKey, 5, 0, 2, 1);
		addComponent(edit_prvKey, 5, 2, 2, 1);
		addComponent(edit_localKey, 7, 0, 4, 1);
		addComponent(new JLabel(PublicString.LOCAL_KEY), 6, 0, 1, 1);
		addComponent(new JLabel(PublicString.INFORMATION), 8, 0, 1, 1);
		addComponent(cb_needEncrypt, 8, 3, 1, 1);

		addComponent(text_result, 10, 1, 2, 1);
		addComponent(edit_msg, 9, 0, 4, 1);
		constraints.ipady = 100;
		addComponent(panel_console, 0, 0, 4, 3);

		bt_generate.addActionListener(this);
		bt_send.addActionListener(this);
		bt_shutdown.addActionListener(this);
		bt_start.addActionListener(this);
		bt_getLocalKey.addActionListener(this);
		cb_needEncrypt.addItemListener(cb_listen);
	}
}
