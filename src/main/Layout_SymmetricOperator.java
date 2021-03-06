package main;

import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.io.File;
import java.util.List;

import javax.swing.*;

import tool.PublicString;
import tool.crypto.Encryptor;
import tool.layout.AbstractGridBagPanel;
import tool.layout.AbstractProcessDialog;

public class Layout_SymmetricOperator extends AbstractGridBagPanel {

	// 这个界面实现对称加密和流密码加密

	/**
	 * 
	 */
	private static final long serialVersionUID = -5903807095990857410L;

	// GUI及图形控件部分
	private JTextField edit_inputFile = new JTextField("");
	private JTextField edit_key = new JTextField("");

	private JButton bt_encrypt = new JButton(PublicString.ENCRYPT);
	private JButton bt_decrypt = new JButton(PublicString.DECRYPT);
	private JButton bt_selectFile = new JButton(PublicString.SELECT_FILE);
	private JButton bt_selectKey = new JButton(PublicString.SELECT_FILE);

	private JRadioButton rb_des = new JRadioButton(PublicString.DES_ALGORITHM, true);// 默认为DES
	private JRadioButton rb_rc4 = new JRadioButton(PublicString.RC4_ALGORITHM, false);
	private JRadioButton rb_aes = new JRadioButton(PublicString.AES_ALGORITHM, false);
	private JRadioButton rb_3des = new JRadioButton(PublicString.DESede_ALGORITHM, false);
	private ButtonGroup rb_group = new ButtonGroup();

	private JCheckBox cb_fromFile = new JCheckBox(PublicString.FROM_FILE, false);// 默认密钥为不文件读取咯

	private JLabel text_result = new JLabel();

	private JFileChooser fileChooser = new JFileChooser();

	// 文件操作部分
	private File[] file_input;
	// private File file_output; //输出全部丢给FileOperator类算了
	private File file_key;

	private DropTarget dropPicker = super.initFileDropTarget(edit_inputFile, new DropReactor() {

		@Override
		public void onFileDrop(List<File> list) {
			// TODO Auto-generated method stub
			// for(File temp:list)
			// edit_inputFile.setText(temp.getAbsolutePath());
			if (!list.isEmpty()) {
				String fileName = "";
				for (File temp : list)
					fileName += temp.getAbsolutePath() + " ; ";
				file_input = (File[]) list.toArray();
				isFromFile = false;
				edit_inputFile.setText(fileName);
				edit_inputFile.setEnabled(false);
				text_result.setText("Selected " + file_input.length + " input file ");
			} else
				throw new NullPointerException("在拖拽文件过程中" + PublicString.INPUT_EMPTY);
		}
	});

	// 其它控制变量
	private boolean isFromFile = false;
	private String cryptoAlgorithm = PublicString.DES_ALGORITHM;

	// 监听器统一处理
	private ItemListener cb_Listener = new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			if (e.getSource().equals(cb_fromFile)) {
				System.out.println("CheckBox FromFile is Changed");
				isFromFile = cb_fromFile.isSelected();
				edit_key.setEditable(!isFromFile);
				bt_selectKey.setEnabled(isFromFile);
				cleanKey();// 切换状态之后清空一下输入框
			} else if (e.getSource().equals(rb_des)) {
				cryptoAlgorithm = PublicString.DES_ALGORITHM;
				text_result.setText("Now Choosing DES Mode.");
			} else if (e.getSource().equals(rb_rc4)) {
				cryptoAlgorithm = PublicString.RC4_ALGORITHM;
				text_result.setText("Now Choosing RC4 Mode.");
			} else if (e.getSource().equals(rb_aes)) {
				cryptoAlgorithm = PublicString.AES_ALGORITHM;
				text_result.setText("Now Choosing AES Mode.");
			} else if (e.getSource().equals(rb_3des)) {
				cryptoAlgorithm = PublicString.DESede_ALGORITHM;
				text_result.setText("Now Choosing 3DES Mode.");
			}

		}
	};

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		try {
			Object event = e.getSource();
			if (event.equals(bt_selectFile)) {
				cleanInput();
				if (fileChooser.showDialog(new JLabel("OK"), PublicString.OK) == JFileChooser.CANCEL_OPTION)
					return;
				file_input = fileChooser.getSelectedFiles();
				if (file_input != null) {
					text_result.setText("Selected " + file_input.length + " input file ");
					String fileName = "";
					for (File temp : file_input)
						fileName += temp.getAbsolutePath() + " ; ";
					edit_inputFile.setText(fileName);
					edit_inputFile.setEnabled(false);
				} else {
					return;
				}
			} else if (event.equals(bt_selectKey)) {
				cleanKey();
				// fileChooser.
				fileChooser.showDialog(new JLabel("OK"), PublicString.OK);
				file_key = fileChooser.getSelectedFile();
				if (file_key != null) {
					text_result.setText("Selected Key file : " + file_key.getName());
					edit_key.setText(file_key.getAbsolutePath());
					edit_key.setEnabled(false);
				} else {
					// JOptionPane.showMessageDialog(null, "Key File is Error",
					// "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			} else if (event.equals(bt_encrypt)) {
				System.out.println("bt_encrypt is Pressed.");
				if (!isOperatorLegal()) {
					JOptionPane.showMessageDialog(null, PublicString.FILE_ERROR, PublicString.ERROR,
							JOptionPane.ERROR_MESSAGE);
					return;
				} // 检测合法性
				final AbstractProcessDialog dialog_process = AbstractProcessDialog.showProgress("处理中", "正在处理，可能需要较长时间",
						"", Layout_SymmetricOperator.this);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							dialog_process.setVisible(true);
							setResult(doEncrypt(true, dialog_process), true);
							dialog_process.dispose();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} // true 表示为加密过程
					}
				}).start();

			} else if (event.equals(bt_decrypt)) {
				System.out.println("bt_decrypt is Pressed.");
				if (!isOperatorLegal()) {
					JOptionPane.showMessageDialog(null, PublicString.FILE_ERROR, PublicString.ERROR,
							JOptionPane.ERROR_MESSAGE);
					return;
				} // 检测合法性

				final AbstractProcessDialog dialog_process = AbstractProcessDialog.showProgress("处理中", "正在处理，可能需要较长时间",
						"", Layout_SymmetricOperator.this);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							dialog_process.setVisible(true);
							setResult(doEncrypt(false, dialog_process), false);
							dialog_process.dispose();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} // true 表示为加密过程
					}
				}).start();
			}
		} catch (HeadlessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private boolean doEncrypt(boolean isEncrypt, AbstractProcessDialog dialog) throws Exception {
		if (isFromFile) {
			for (int i = 0; i < file_input.length; i++) {
				dialog.setHint("正在进行第 " + i + " 个,共 " + file_input.length + " 个");
				Encryptor.encrypt_Process(file_input[i], file_key, isEncrypt, cryptoAlgorithm);
			}
		} else {
			for (int i = 0; i < file_input.length; i++) {
				dialog.setHint("正在进行第 " + i + " 个,共 " + file_input.length + " 个");
				Encryptor.encrypt_Process(file_input[i], edit_key.getText().trim(), isEncrypt, cryptoAlgorithm);
			}
		}
		return true;
	}

	// private boolean doEncrypt(boolean isEncrypt) throws Exception {
	// return isFromFile ? Encryptor.encrypt_Process(file_input, file_key,
	// isEncrypt, cryptoAlgorithm)
	// : Encryptor.encrypt_Process(file_input, edit_key.getText().trim(),
	// isEncrypt, cryptoAlgorithm);// 文件密钥还没写
	//
	// }

	private void setResult(boolean isSuccess, boolean isEncrypt) {
		try {
			if (isEncrypt)
				text_result.setText(isSuccess ? PublicString.ENCRYPTION_SUCCESS : PublicString.ENCRYPTION_FAIL);
			else
				text_result.setText(isSuccess ? PublicString.DECRYPTION_SUCCESS : PublicString.DECRYPTION_FAIL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (isEncrypt)
				text_result.setText(PublicString.ENCRYPTION_FAIL + "  " + e.getMessage());
			else
				text_result.setText(PublicString.DECRYPTION_FAIL + "  " + e.getMessage());
		}
	}

	private boolean isOperatorLegal() {
		return ((isFromFile && file_input != null && file_key != null && file_key.canRead()) || (!isFromFile
				&& file_input != null && edit_key.getText() != null && !edit_key.getText().trim().equals("")));
	}

	public Layout_SymmetricOperator() {
		// 初始化图形界面
		super();

		dropPicker.setActive(true);// 激活拖拽文件的监听
		constraints.insets = new Insets(3, 2, 3, 2);// insets用于控制间距

		constraints.fill = GridBagConstraints.HORIZONTAL;// GridBagConstraints.BOTH;//
		// 设置所有组件都是居中
		constraints.anchor = GridBagConstraints.WEST;
		addComponent(new JLabel(PublicString.FILE), 0, 0, 1, 1);
		addComponent(new JLabel(PublicString.KEY), 2, 0, 1, 1);

		constraints.anchor = GridBagConstraints.EAST;
		addComponent(bt_selectFile, 0, 3, 1, 1);
		addComponent(bt_selectKey, 2, 3, 1, 1);// 同一个对象只能add一遍

		constraints.anchor = GridBagConstraints.CENTER;
		addComponent(cb_fromFile, 2, 2, 1, 1);
		addComponent(bt_encrypt, 6, 1, 1, 1);
		addComponent(bt_decrypt, 6, 2, 1, 1);

		constraints.fill = GridBagConstraints.BOTH;
		addComponent(rb_rc4, 5, 0, 1, 1);
		addComponent(rb_des, 5, 1, 1, 1);
		addComponent(rb_3des, 5, 2, 1, 1);
		addComponent(rb_aes, 5, 3, 1, 1);

		addComponent(edit_inputFile, 1, 0, 4, 1);
		addComponent(edit_key, 3, 0, 4, 1);
		addComponent(text_result, 7, 0, 4, 1);

		// 初始化相关控件
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setFileHidingEnabled(false);
		edit_key.setEditable(true);
		text_result.setHorizontalAlignment(SwingConstants.CENTER);
		rb_group.add(rb_des);
		rb_group.add(rb_aes);
		rb_group.add(rb_rc4);
		rb_group.add(rb_3des);

		// 监听器设置
		bt_selectFile.addActionListener(this);
		bt_selectKey.addActionListener(this);
		bt_decrypt.addActionListener(this);
		bt_encrypt.addActionListener(this);
		cb_fromFile.addItemListener(cb_Listener);
		rb_des.addItemListener(cb_Listener);
		rb_rc4.addItemListener(cb_Listener);
		rb_aes.addItemListener(cb_Listener);
		rb_3des.addItemListener(cb_Listener);

	}

	private void cleanInput() {
		file_input = null;// 设置成点了button之后就重置file_input内容，*****是不是要改一改单独设置一个按钮
		edit_inputFile.setText("");
		edit_inputFile.setEnabled(true);
		text_result.setText("");
	}

	private void cleanKey() {
		file_key = null;
		edit_key.setText("");
		text_result.setText("");
	}

}
