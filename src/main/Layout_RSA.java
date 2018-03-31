package main;

import java.awt.GridBagConstraints;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.IllegalBlockSizeException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import tool.PublicString;
import tool.crypto.RSA_Encryptor;
import tool.layout.AbstractGridBagPanel;
import tool.util.StringProcessor;

public class Layout_RSA extends AbstractGridBagPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9051168281994644831L;

	private JTextArea text_inMsg = new JTextArea();
	private JTextArea text_outMsg = new JTextArea();
	private JTextField text_publicKey = new JTextField();
	private JTextField text_privateKey = new JTextField();
	// private JTextField text_primeA = new JTextField();
	// private JTextField text_primeB = new JTextField();
	private JTextField text_keySeed = new JTextField();

	private JButton bt_encrypt = new JButton(PublicString.ENCRYPT);
	private JButton bt_decrypt = new JButton(PublicString.DECRYPT);
	private JButton bt_getKey = new JButton(PublicString.GET_KEY);

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Object e = arg0.getSource();
		// if (e.equals(bt_getPrime)) {
		// text_primeA.setText(RSA_EncryptorEdu.genaratePrime().toString());
		// text_primeB.setText(RSA_EncryptorEdu.genaratePrime().toString());
		// } else
		if (e.equals(bt_getKey)) {
			String seed = JOptionPane.showInputDialog(null, "请输入生成密钥的激励", PublicString.GET_KEY,
					JOptionPane.INFORMATION_MESSAGE);
			if (seed.trim().equals("")) {
				JOptionPane.showMessageDialog(null, PublicString.INPUT_EMPTY, PublicString.WARNING,
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			Map<String, byte[]> keyPair;
			try {
				keyPair = RSA_Encryptor.generateKeyPair(seed.getBytes("UTF-8"));
				text_privateKey.setText(StringProcessor.byteToBase64(keyPair.get(RSA_Encryptor.PRIVATE_KEY)));
				text_publicKey.setText(StringProcessor.byteToBase64(keyPair.get(RSA_Encryptor.PUBLIC_KEY)));
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (e.equals(bt_encrypt)) {
			try {
				if (text_inMsg.getText().trim().equals("") || text_publicKey.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, PublicString.INPUT_EMPTY, PublicString.WARNING,
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				text_outMsg.setText(Base64.encode(RSA_Encryptor
						.encrypt(text_inMsg.getText().trim().getBytes("UTF-8"), Base64.decode(text_publicKey.getText().trim()))));
			} catch (IllegalBlockSizeException e1) {
				JOptionPane.showMessageDialog(null, "需要加密的数据太大，不适用于RSA算法 " + e1.getMessage(), PublicString.ERROR,
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.equals(bt_decrypt)) {
			try {
				if (text_outMsg.getText().trim().equals("") || text_privateKey.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, PublicString.INPUT_EMPTY, PublicString.ERROR,
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				text_inMsg
						.setText(new String(RSA_Encryptor.decrypt(StringProcessor.base64ToByte(text_outMsg.getText().trim()),
								Base64.decode(text_privateKey.getText().trim()))));
			} catch (IllegalBlockSizeException e1) {
				JOptionPane.showMessageDialog(null, "需要解密的数据太大，不适用于RSA算法 " + e1.getMessage(), PublicString.ERROR,
						JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

	public Layout_RSA() {
		// 初始化图形界面
		super();

		constraints.fill = GridBagConstraints.HORIZONTAL;// GridBagConstraints.BOTH;//
		// 设置所有组件都是居中
		constraints.anchor = GridBagConstraints.WEST;

		addComponent(new JLabel(PublicString.ORIGIN_MESSAGE), 0, 0, 1, 1);
		addComponent(new JLabel(PublicString.PUBLIC_KEY), 4, 0, 1, 1);
		addComponent(new JLabel(PublicString.PRIVATE_KEY), 4, 3, 1, 1);
		addComponent(new JLabel(PublicString.ENCRYPTED_MESSAGE), 6, 0, 1, 1);

		constraints.anchor = GridBagConstraints.CENTER;
		addComponent(bt_decrypt, 8, 1, 1, 1);
		addComponent(bt_getKey, 8, 2, 2, 1);
		addComponent(bt_encrypt, 8, 4, 1, 1);

		constraints.fill = GridBagConstraints.BOTH;
		addComponent(text_inMsg, 1, 0, 6, 1);
		addComponent(text_outMsg, 7, 0, 6, 1);
		addComponent(text_publicKey, 5, 0, 3, 1);
		addComponent(text_privateKey, 5, 3, 3, 1);

		// 监听器初始化
		bt_encrypt.addActionListener(this);
		bt_decrypt.addActionListener(this);
		bt_getKey.addActionListener(this);
	}

}
