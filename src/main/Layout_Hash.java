package main;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import commonTool.HashProcessor;
import commonTool.AbstractGridBagPanel;
import commonTool.PublicString;

public class Layout_Hash extends AbstractGridBagPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8724598702386970327L;
	private JCheckBox cb_fromFile = new JCheckBox(PublicString.FROM_FILE, false);// 默认为从文件读取咯
	private JButton bt_process = new JButton(PublicString.OK);
	private JButton bt_selectFile = new JButton(PublicString.SELECT_FILE);
	private JTextField edit_input = new JTextField();

	private JLabel[] text_method = new JLabel[HashProcessor.method.length];

	private JTextField[] edit_hash = new JTextField[HashProcessor.method.length];

	private JLabel text_result = new JLabel();

	private JFileChooser fileChooser = new JFileChooser();

	// 文件操作部分
	private File file_input;

	// 其它控制变量
	private boolean isFromFile = false;
	private ItemListener cb_listen = new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			isFromFile = cb_fromFile.isSelected();
			edit_input.setText("");
			edit_input.setEditable(!isFromFile);// 反应迟钝？？？
			bt_selectFile.setEnabled(isFromFile);
		}
	};


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(bt_selectFile)) {
			fileChooser.showDialog(new JLabel("OK"), PublicString.OK);
			file_input = fileChooser.getSelectedFile();
			if (file_input != null) {
				text_result.setText("Selected input file : " + file_input.getName());
				edit_input.setText(file_input.getAbsolutePath());
			} else {
				// JOptionPane.showMessageDialog(null, "Input File is
				// Error",
				// "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} else if (e.getSource().equals(bt_process)) {
			text_result.setText("");
			if (!isInputLegal()) {
				return;
			}
			try {
				if (isFromFile)
					setHashResult(doHash(file_input));
				else
					setHashResult(doHash(edit_input.getText().trim()));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				text_result.setText(PublicString.ENCRYPTION_FAIL + " : " + e1.getMessage());
				e1.printStackTrace();
			}
		}
	}

	public Layout_Hash() {
		super();
		// 初始化相关控件
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileHidingEnabled(false);
		text_result.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < edit_hash.length; i++) {
			edit_hash[i] = new JTextField();
			text_method[i] = new JLabel(HashProcessor.method[i]);
		}
		bt_selectFile.setEnabled(isFromFile);

		
		constraints.fill = GridBagConstraints.HORIZONTAL;// 
		// 设置所有组件都是居中
		addComponent(bt_process, 6, 1, 2, 1);
		
		constraints.anchor = GridBagConstraints.EAST;
		addComponent(bt_selectFile, 0, 3, 1, 1);
		addComponent(cb_fromFile, 0, 2, 1, 1);
		
		
		constraints.anchor = GridBagConstraints.CENTER;
		
		addComponent(new JLabel("    "), 0, 3, 1, 1);
		addComponent(new JLabel(PublicString.INFORMATION), 0, 0, 2, 1);
		constraints.fill = GridBagConstraints.BOTH;
		
		constraints.insets=new Insets(3,0,3,0);//insets用于控制间距
		for (int i = 0; i < HashProcessor.method.length; i++) {
			text_method[i].setHorizontalAlignment(SwingConstants.LEFT);
			addComponent(text_method[i], 2 + i, 0, 1, 1);
			addComponent(edit_hash[i], 2 + i, 1, 3, 1);
		}
		addComponent(edit_input, 1, 0, 4, 1);
		addComponent(text_result, 5, 1, 2, 1);

		// 监听器注册
		bt_process.addActionListener(this);
		bt_selectFile.addActionListener(this);
		cb_fromFile.addItemListener(cb_listen);
	}

	private String getHashCode(String str, int methodSelect) throws Exception {
		return HashProcessor.getHash(str, methodSelect);
	}

	private String getHashCode(File file, int methodSelect) throws Exception {
		return HashProcessor.getHash(file, methodSelect);
	}

	private String[] doHash(String str) throws Exception {
		String[] hashArray = new String[HashProcessor.method.length];
		for (int i = 0; i < hashArray.length; i++)
			hashArray[i] = getHashCode(str, i);
		return hashArray;
	}

	private String[] doHash(File file) throws Exception {
		String[] hashArray = new String[HashProcessor.method.length];
		for (int i = 0; i < hashArray.length; i++)
			hashArray[i] = getHashCode(file, i);
		return hashArray;
	}

	private void setHashResult(String[] hashArray) {
		for (int i = 0; i < hashArray.length; i++)
			edit_hash[i].setText(hashArray[i].trim());
	}

	private boolean isInputLegal() {
		return ((isFromFile && file_input.isFile()) || (!isFromFile && !edit_input.getText().trim().equals("")));
	}

}
