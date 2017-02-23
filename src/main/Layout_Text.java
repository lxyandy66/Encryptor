package main;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigInteger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import tool.PublicString;
import tool.crypto.Text_Encryptor;
import tool.layout.AbstractGridBagPanel;

public class Layout_Text extends AbstractGridBagPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3373864719238675213L;

	private static final String[] method = { "DES", "3DES", "AES", "BASE64", "移位加密", "混沌移位", "仿射加密" };

	private int methodSelect;

	private JTextArea edit_inMsg = new JTextArea();
	private JTextArea edit_outMsg = new JTextArea();
	private JTextField edit_key1 = new JTextField();
	private JTextField edit_key2 = new JTextField();
	private JLabel text_result = new JLabel();
	private JButton bt_encrypt = new JButton(PublicString.ENCRYPT);
	private JButton bt_decrypt = new JButton(PublicString.DECRYPT);
	private JComboBox<String> comb_method = new JComboBox<String>(method);


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(bt_encrypt)) {
			if (!isOperatorLegal(true)) {
				JOptionPane.showMessageDialog(null, PublicString.INPUT_EMPTY, PublicString.ERROR,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				edit_outMsg.setText(Text_Encryptor.textProcess(edit_inMsg.getText().trim(), true, methodSelect,
						edit_key1.getText().trim(), methodSelect == Text_Encryptor.AFFINE_ENCRYPT
								? new BigInteger(edit_key2.getText().trim()) : null));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				text_result.setText("加密失败,异常是 " + e1.getMessage());
			}
		} else if (e.getSource().equals(bt_decrypt)) {
			if (!isOperatorLegal(false)) {
				JOptionPane.showMessageDialog(null, PublicString.INPUT_EMPTY, PublicString.ERROR,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				edit_inMsg.setText(Text_Encryptor.textProcess(edit_outMsg.getText().trim(), false, methodSelect,
						edit_key1.getText().trim(), methodSelect == Text_Encryptor.AFFINE_ENCRYPT
								? new BigInteger(edit_key2.getText().trim()) : null));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				text_result.setText("解密失败,异常是 " + e1.getMessage());
			}
		}
	}

	ItemListener comb_listen = new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			if (e.getStateChange() == ItemEvent.SELECTED) {
				methodSelect = comb_method.getSelectedIndex();
				text_result.setText("设置为" + method[methodSelect] + "模式");
				switch (methodSelect) {
				case 6:
					edit_key2.setEditable(true);
					break;
				default:
					edit_key2.setEditable(false);
				}
			}

		}
	};

	private boolean isOperatorLegal(boolean isEncrypt) {
		if (!edit_key1.getText().trim().equals("")) {
			if ((isEncrypt && !edit_inMsg.getText().trim().equals(""))
					|| (!isEncrypt && !edit_outMsg.getText().trim().equals("")))
				if (methodSelect == 0)
					return !edit_key1.getText().trim().equals("");
				else
					return true;
			else
				return true;
		} else
			return false;
	}

	public Layout_Text() {
		super();
		

		constraints.fill = GridBagConstraints.HORIZONTAL;// GridBagConstraints.BOTH;//
		// 设置所有组件都是居中
		constraints.anchor = GridBagConstraints.WEST;

		addComponent(new JLabel(PublicString.ORIGIN_MESSAGE), 0, 0, 1, 1);
		addComponent(new JLabel(PublicString.ENCRYPTED_MESSAGE), 4, 0, 1, 1);
		addComponent(new JLabel(PublicString.KEY + " A"), 2, 0, 1, 1);
		addComponent(new JLabel(PublicString.KEY + " B"), 2, 2, 1, 1);

		constraints.anchor = GridBagConstraints.CENTER;
		addComponent(bt_decrypt, 7, 1, 1, 1);
		addComponent(bt_encrypt, 7, 2, 1, 1);
		addComponent(comb_method, 4, 3, 1, 1);

		constraints.fill = GridBagConstraints.BOTH;
		addComponent(edit_inMsg, 1, 0, 6, 1);
		addComponent(edit_outMsg, 5, 0, 6, 1);
		addComponent(edit_key1, 3, 0, 2, 1);
		addComponent(edit_key2, 3, 2, 2, 1);
		addComponent(text_result, 6, 1, 2, 1);

		bt_decrypt.addActionListener(this);
		bt_encrypt.addActionListener(this);
		comb_method.addItemListener(comb_listen);

		edit_key2.setEditable(false);
		text_result.setHorizontalAlignment(SwingConstants.CENTER);
	}
}
