package main;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import commonTool.HashProcessor;
import commonTool.AbstractGridBagPanel;
import commonTool.PublicString;
import commonTool.RSA_Encryptor;
import commonTool.StringProcessor;

public class Layout_Sign extends AbstractGridBagPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1175852772682245265L;

	private JTextField edit_input = new JTextField();
	private JTextField edit_key = new JTextField();
	private JTextField edit_sign = new JTextField();
	private JButton bt_selectFile = new JButton(PublicString.FROM_FILE);
	private JButton bt_sign = new JButton(PublicString.SIGN);
	private JButton bt_check = new JButton(PublicString.CHECK);
	private JLabel text_result = new JLabel();

	private JComboBox<String> comb_hashMethod = new JComboBox<String>(HashProcessor.method);


	private JFileChooser fileChooser = new JFileChooser();

	// 文件操作部分
	private File file_input;
	private int methodSelect = 0;

	// 监听器部分
	private ItemListener comb_listen = new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			if (e.getStateChange() == ItemEvent.SELECTED) {
				methodSelect = comb_hashMethod.getSelectedIndex();
				text_result.setText("选择以" + HashProcessor.method[methodSelect] + "方式生成Hash值");
			}
		}
	};

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(bt_selectFile)) {
			fileChooser.showDialog(new JLabel("OK"), PublicString.OK);
			file_input = fileChooser.getSelectedFile();
			if (file_input != null) {
				edit_input.setText("");
				text_result.setText("Selected input file : " + file_input.getName());
				edit_input.setText(file_input.getAbsolutePath());
				edit_input.setEnabled(false);
			}
		} else if (e.getSource().equals(bt_sign)) {
			if (file_input == null || edit_key.getText().trim().equals("")||edit_key.getText()==null) {
				JOptionPane.showMessageDialog(null, PublicString.INPUT_EMPTY, PublicString.ERROR,
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				edit_sign.setText(getSign(file_input, edit_key.getText().trim(), true));
				text_result.setText("签名完成!");
			} catch(IllegalBlockSizeException e2){
				JOptionPane.showMessageDialog(null, "需要处理的数据太大，不适用于RSA算法 "+e2.getMessage(), PublicString.ERROR,
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception e2) {
				// TODO: handle exception
				text_result.setText("签名失败，异常是: " + e2.getMessage());
				e2.printStackTrace();
			}
		} else if (e.getSource().equals(bt_check)) {
			if (file_input == null || edit_key.getText().trim().equals("") || edit_sign.getText().trim().equals("")) {
				JOptionPane.showMessageDialog(null, PublicString.INPUT_EMPTY, PublicString.ERROR,
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				String originSign = JOptionPane.showInputDialog("请输入该文件要验证的签名");
				if (originSign == null || originSign.trim().equals("")) {
					JOptionPane.showMessageDialog(null, PublicString.INPUT_EMPTY, PublicString.ERROR,
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				String pubKey = JOptionPane.showInputDialog("请输入验证的签名的公钥");
				if (pubKey == null || pubKey.trim().equals("")) {
					JOptionPane.showMessageDialog(null, PublicString.INPUT_EMPTY, PublicString.ERROR,
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (RSA_Encryptor.Decrypt(originSign, (RSAPublicKey)StringProcessor.base64ToObject(pubKey))
						.equals(HashProcessor.getHash(file_input, methodSelect))){
					JOptionPane.showMessageDialog(null, "文件签名是来自授权者", "完成", JOptionPane.INFORMATION_MESSAGE);
					}
				else
					JOptionPane.showMessageDialog(null, "文件签名不是来自该公钥所有者！", "失败", JOptionPane.ERROR_MESSAGE);
			} catch (BadPaddingException e1) {
				// TODO Auto-generated catch block
				text_result.setText("验证失败");
				JOptionPane.showMessageDialog(null, "文件签名不是来自该公钥所有者！", "失败", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			} catch(IllegalBlockSizeException e1){
				text_result.setText("验证失败");
				JOptionPane.showMessageDialog(null, "需要处理的数据太大，不适用于RSA算法 "+e1.getMessage(), PublicString.ERROR,
						JOptionPane.ERROR_MESSAGE);
			}catch (Exception e1) {
				// TODO Auto-generated catch block
				text_result.setText("验证失败");
				JOptionPane.showMessageDialog(null, "文件签名不是来自该公钥所有者！", "失败", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}

	}

	private String getSign(File f, String base64Key, boolean isSign) throws Exception {
		if (isSign) {
			return RSA_Encryptor.Encrypt(HashProcessor.getHash(f, methodSelect),
					(RSAPrivateKey)StringProcessor.base64ToObject(base64Key));
		} else
			return RSA_Encryptor.Encrypt(HashProcessor.getHash(f, methodSelect),
					(RSAPublicKey)StringProcessor.base64ToObject(base64Key));
	}

	public Layout_Sign() {
		// 初始化图形界面
		super();

		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		addComponent(new JLabel(PublicString.FILE), 0, 0, 1, 1);
		addComponent(new JLabel(PublicString.RSA_ALGORITHM + PublicString.KEY), 2, 0, 1, 1);
		addComponent(new JLabel(PublicString.SIGN), 4, 0, 1, 1);

		addComponent(edit_sign, 5, 1, 3, 1);
		addComponent(edit_input, 1, 0, 4, 1);
		addComponent(edit_key, 3, 0, 4, 1);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		addComponent(bt_selectFile, 0, 3, 1, 1);
		text_result.setHorizontalAlignment(SwingConstants.CENTER);
		addComponent(text_result, 6, 1, 2, 1);
		addComponent(bt_check, 7, 1, 1, 1);
		addComponent(bt_sign, 7, 2, 1, 1);
		addComponent(comb_hashMethod, 5, 0, 1, 1);

		// 注册监听器
		bt_selectFile.addActionListener(this);
		bt_check.addActionListener(this);
		bt_sign.addActionListener(this);
		comb_hashMethod.addItemListener(comb_listen);

	}

}
