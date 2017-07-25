package main;

import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.event.*;
import java.math.BigInteger;

import javax.crypto.IllegalBlockSizeException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import tool.PublicString;
import tool.crypto.RSA_Encryptor;
import tool.layout.AbstractGridBagPanel;

/**
 * @author 李 鑫悦
 *这个RSA基于密码学原理课程设计的要求完成，不在通常情况下建议使用
 *
 */
public class Layout_RSAEdu extends AbstractGridBagPanel  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9051168281994644831L;

	private JTextArea text_inMsg = new JTextArea();
	private JTextArea text_outMsg = new JTextArea();
	private JTextField text_publicKey = new JTextField();
	private JTextField text_privateKey = new JTextField();
	private JTextField text_primeA = new JTextField();
	private JTextField text_primeB = new JTextField();

	private JButton bt_encrypt = new JButton(PublicString.ENCRYPT);
	private JButton bt_decrypt = new JButton(PublicString.DECRYPT);
	private JButton bt_getKey = new JButton(PublicString.GET_KEY);
	private JButton bt_getPrime = new JButton(PublicString.GEN_PRIME);
	private JButton bt_checkPrime = new JButton(PublicString.CHECK_PRIME);


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Object e = arg0.getSource();
		if (e.equals(bt_getPrime)) {
			text_primeA.setText(RSA_Encryptor.genaratePrime().toString());
			text_primeB.setText(RSA_Encryptor.genaratePrime().toString());
		} else if (e.equals(bt_getKey)) {
			try {
				if (text_primeA.getText().trim().equals("") || text_primeB.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, PublicString.INPUT_EMPTY, PublicString.ERROR,
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				text_privateKey.setText(RSA_Encryptor.getPrivateKeyString(new BigInteger(text_primeA.getText().trim()),
						new BigInteger(text_primeB.getText().trim())));
				text_publicKey.setText(RSA_Encryptor.getPublicKeyString(new BigInteger(text_primeA.getText().trim()),
						new BigInteger(text_primeB.getText().trim())));
			} catch(java.security.spec.InvalidKeySpecException e1){
				JOptionPane.showMessageDialog(null, "选取参数太短，安全性错误 "+e1.getMessage(), PublicString.ERROR,
						JOptionPane.ERROR_MESSAGE);
				text_primeA.setText("");
				text_primeB.setText("");
			}catch (HeadlessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.equals(bt_checkPrime)) {
			if (text_primeA.getText().trim().equals("") || text_primeB.getText().trim().equals("")) {
				JOptionPane.showMessageDialog(null, PublicString.INPUT_EMPTY, PublicString.WARNING,
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (!RSA_Encryptor.isTwoPrime(new BigInteger(text_primeA.getText().trim()),
					new BigInteger(text_primeB.getText().trim()))) {
				JOptionPane.showMessageDialog(null, PublicString.NOT_PRIME, PublicString.ERROR,
						JOptionPane.WARNING_MESSAGE);
				text_primeA.setText("");
				text_primeB.setText("");
			} else
				JOptionPane.showMessageDialog(null, PublicString.IS_PRIME, PublicString.INFORMATION,
						JOptionPane.INFORMATION_MESSAGE);
		} else if (e.equals(bt_encrypt)) {
			try {
				if (text_inMsg.getText().trim().equals("") || text_publicKey.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, PublicString.INPUT_EMPTY, PublicString.WARNING,
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				text_outMsg.setText(RSA_Encryptor.Encrypt(text_inMsg.getText().trim(), text_publicKey.getText().trim()));
			} catch(IllegalBlockSizeException e1){
				JOptionPane.showMessageDialog(null, "需要加密的数据太大，不适用于RSA算法 "+e1.getMessage(), PublicString.ERROR,
						JOptionPane.ERROR_MESSAGE);
			}catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(e.equals(bt_decrypt)){
			try {
				if (text_outMsg.getText().trim().equals("") || text_privateKey.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, PublicString.INPUT_EMPTY, PublicString.ERROR,
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				text_inMsg.setText(RSA_Encryptor.Decrypt(text_outMsg.getText().trim(), text_privateKey.getText().trim()));
			} catch(IllegalBlockSizeException e1){
				JOptionPane.showMessageDialog(null, "需要解密的数据太大，不适用于RSA算法 "+e1.getMessage(), PublicString.ERROR,
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	
		}
	}


	public Layout_RSAEdu() {
		// 初始化图形界面
		super();

		constraints.fill = GridBagConstraints.HORIZONTAL;// GridBagConstraints.BOTH;//
		// 设置所有组件都是居中
		constraints.anchor = GridBagConstraints.WEST;

		addComponent(new JLabel(PublicString.ORIGIN_MESSAGE), 0, 0, 1, 1);
		addComponent(new JLabel(PublicString.PUBLIC_KEY), 4, 0, 1, 1);
		addComponent(new JLabel(PublicString.PRIVATE_KEY), 4, 3, 1, 1);
		addComponent(new JLabel(PublicString.ENCRYPTED_MESSAGE), 6, 0, 1, 1);
		addComponent(new JLabel(PublicString.BIG_PRIME + " A"), 2, 0, 1, 1);
		addComponent(new JLabel(PublicString.BIG_PRIME + " B"), 2, 3, 1, 1);

		constraints.anchor = GridBagConstraints.CENTER;
		addComponent(bt_decrypt, 8, 1, 1, 1);
		addComponent(bt_checkPrime, 8, 3, 1, 1);
		addComponent(bt_getKey, 8, 2, 1, 1);
		addComponent(bt_encrypt, 8, 4, 1, 1);
		addComponent(bt_getPrime, 2, 5, 1, 1);

		constraints.fill = GridBagConstraints.BOTH;
		addComponent(text_inMsg, 1, 0, 6, 1);
		addComponent(text_outMsg, 7, 0, 6, 1);
		addComponent(text_publicKey, 5, 0, 3, 1);
		addComponent(text_privateKey, 5, 3, 3, 1);
		addComponent(text_primeA, 3, 0, 3, 1);
		addComponent(text_primeB, 3, 3, 3, 1);

		// 监听器初始化
		bt_encrypt.addActionListener(this);
		bt_decrypt.addActionListener(this);
		bt_getKey.addActionListener(this);
		bt_getPrime.addActionListener(this);
		bt_checkPrime.addActionListener(this);
	}

}
