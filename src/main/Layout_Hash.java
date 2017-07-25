package main;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import tool.PublicString;
import tool.crypto.HashProcessor;
import tool.layout.AbstractGridBagPanel;
import tool.layout.AbstractProcessDialog;

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
	
	private GridLayout layout_hashText=new GridLayout(3, 1);
	private GridLayout layout_hashResult=new GridLayout(3, 1);
	
	private JPanel panel_hashText=new JPanel(layout_hashText);
	private JPanel panel_hashResult=new JPanel(layout_hashResult);

	// 文件操作部分
	private File file_input;

	private DropTarget dropTarget = super.initFileDropTarget(edit_input, new DropReactor() {
		
		@Override
		public void onFileDrop(List<File> list) {
			// TODO Auto-generated method stub
			if(list.isEmpty())
				throw new NullPointerException("在拖拽文件过程中"+PublicString.INPUT_EMPTY);
			cb_fromFile.setSelected(true);
			isFromFile=true;
			file_input=list.get(0);
			edit_input.setText(file_input.getAbsolutePath());
			text_result.setText("Selected input file : " + file_input.getName());
		}
	});

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
			if (fileChooser.showDialog(new JLabel("OK"), PublicString.OK) == JFileChooser.CANCEL_OPTION)
				return;
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
			if (!isInputLegal())
				return;
			final AbstractProcessDialog dialog_process = AbstractProcessDialog.showProgress("处理中", "正在处理，可能需要较长时间",
					null, Layout_Hash.this);
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						dialog_process.setVisible(true);
						if (isFromFile)
							setHashResult(doHash(file_input));
						else
							setHashResult(doHash(edit_input.getText().trim()));
						dialog_process.dispose();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // true 表示为加密过程
				}
			}).start();

		}
	}

	public Layout_Hash() {
		super();
		
		dropTarget.setActive(true);
		super.setDropTarget(dropTarget);
		
		// 初始化相关控件
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileHidingEnabled(false);
		text_result.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < edit_hash.length; i++) {
			edit_hash[i] = new JTextField();
			panel_hashResult.add(edit_hash[i]);
			text_method[i] = new JLabel(HashProcessor.method[i]);
			panel_hashText.add(text_method[i]);
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

		constraints.insets = new Insets(3, 0, 3, 0);// insets用于控制间距

		addComponent(panel_hashText, 2, 0, 1, 3);
		addComponent(panel_hashResult, 2, 1, 3, 3);//使用panel不会因为edit的长度变化改变布局
		
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
