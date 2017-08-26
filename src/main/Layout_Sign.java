package main;

import java.awt.GridBagConstraints;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import tool.PublicString;
import tool.util.FileOperator;
import tool.util.StringProcessor;
import tool.crypto.HashProcessor;
import tool.crypto.RSA_Encryptor;
import tool.layout.AbstractGridBagPanel;

public class Layout_Sign extends AbstractGridBagPanel {

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
	private JButton bt_export = new JButton(PublicString.EXPORT);
	private JLabel text_result = new JLabel();

	private JComboBox<String> comb_hashMethod = new JComboBox<String>(HashProcessor.method);

	private JFileChooser fileChooser = new JFileChooser();

	private String[] fileNames;

	// 文件操作部分
	private File[] file_input;
	private int methodSelect = 0;

	// 定义拖拽对象
	private DropTarget dropTarget = super.initFileDropTarget(edit_input, new DropReactor() {

		@Override
		public void onFileDrop(List<File> list) {
			// TODO Auto-generated method stub
			file_input = (File[]) list.toArray();
			receiveFileProcess(file_input);
		}
	});

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
		if (e.getSource().equals(bt_selectFile)) {// 选取文件
			if (fileChooser.showDialog(new JLabel("OK"), PublicString.OK) == JFileChooser.CANCEL_OPTION)
				return;
			file_input = fileChooser.getSelectedFiles();
			if (file_input != null) {
				receiveFileProcess(file_input);
			}
		} else if (e.getSource().equals(bt_sign)) {// 签名操作
			if (!legalCheck())
				return;
			if (file_input.length > 1)
				JOptionPane.showConfirmDialog(null, "检测到处理的文件多于一个，继续将仅处理第一个文件，若需多个文件签名请使用批量导出功能。是否继续?", "警告",
						JOptionPane.YES_NO_OPTION);
			try {
				edit_sign.setText(getSign(file_input[0], edit_key.getText().trim(), true));
				text_result.setText("签名完成!");
			} catch (IllegalBlockSizeException e2) {
				JOptionPane.showMessageDialog(null, "需要处理的数据太大，不适用于RSA算法 " + e2.getMessage(), PublicString.ERROR,
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception e2) {
				// TODO: handle exception
				text_result.setText("签名失败，异常是: " + e2.getMessage());
				e2.printStackTrace();
			}
		} else if (e.getSource().equals(bt_check)) {// 验证签名操作
			if (!legalCheck())
				return;
			if (file_input.length > 1)
				JOptionPane.showConfirmDialog(null, "检测到处理的文件多于一个，继续将仅处理第一个文件，若需多个文件签名请使用批量导出功能。是否继续?", "警告",
						JOptionPane.YES_NO_OPTION);
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
				if (RSA_Encryptor.Decrypt(originSign, (RSAPublicKey) StringProcessor.base64ToObject(pubKey))
						.equals(HashProcessor.getHash(file_input[0], methodSelect))) {
					JOptionPane.showMessageDialog(null, "文件签名是来自授权者", "完成", JOptionPane.INFORMATION_MESSAGE);
				} else
					JOptionPane.showMessageDialog(null, "文件签名不是来自该公钥所有者！", "失败", JOptionPane.ERROR_MESSAGE);
			} catch (BadPaddingException e1) {
				// TODO Auto-generated catch block
				text_result.setText("验证失败");
				JOptionPane.showMessageDialog(null, "文件签名不是来自该公钥所有者！", "失败", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			} catch (IllegalBlockSizeException e1) {
				text_result.setText("验证失败");
				JOptionPane.showMessageDialog(null, "需要处理的数据太大，不适用于RSA算法 " + e1.getMessage(), PublicString.ERROR,
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				text_result.setText("验证失败");
				JOptionPane.showMessageDialog(null, "文件签名不是来自该公钥所有者！", "失败", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		} else if (e.getSource().equals(bt_export)) {// 批量导出签名操作
			if (!legalCheck())
				return;
			File exportFile;
			JFileChooser locationChooser = new JFileChooser();
			locationChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			
			if (locationChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				exportFile = locationChooser.getSelectedFile();
			} else
				return;
			if (exportFile!=null&&exportFile.exists()&&exportFile.length()>1) 
				if (JOptionPane.showConfirmDialog(null, "文件已存在，是否覆盖？", PublicString.WARNING,
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION)
					return;
			FileOperator fileOperator = new FileOperator(exportFile);
			try {
				
				System.out.println(fileOperator.getCurrentFilePath());
				fileOperator.writeToFile(exportSign(methodSelect, edit_key.getText().trim(), file_input),false);//直接写入文件
				JOptionPane.showMessageDialog(null, "导出完成!", PublicString.INFORMATION, JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, PublicString.FILE_ERROR + e1.getMessage(), PublicString.ERROR,
						JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}

	}

	private String getSign(File f, String base64Key, boolean isSign) throws Exception {
		if (isSign) {
			return RSA_Encryptor.Encrypt(HashProcessor.getHash(f, methodSelect),
					(RSAPrivateKey) StringProcessor.base64ToObject(base64Key));
		} else
			return RSA_Encryptor.Encrypt(HashProcessor.getHash(f, methodSelect),
					(RSAPublicKey) StringProcessor.base64ToObject(base64Key));
	}

	public Layout_Sign() {
		// 初始化图形界面
		super();

		// 文件操作初始化
		fileChooser.setMultiSelectionEnabled(true);

		// 支持拖拽选项
		dropTarget.setActive(true);
		super.setDropTarget(dropTarget);

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
		addComponent(bt_export, 4, 3, 1, 1);
		addComponent(comb_hashMethod, 5, 0, 1, 1);

		// 注册监听器
		bt_selectFile.addActionListener(this);
		bt_check.addActionListener(this);
		bt_sign.addActionListener(this);
		bt_export.addActionListener(this);
		comb_hashMethod.addItemListener(comb_listen);

	}

	private String[] exportSign(int hashMethod, String key, File[] file_input) throws Exception {
		String[] signResult = new String[file_input.length];
		for (int i = 0; i < signResult.length; i++) {
			signResult[i] = file_input[i].getName() + ":\r\n" + HashProcessor.method[hashMethod] + " 值:\r\n"
					+ HashProcessor.getHash(file_input[i], hashMethod) + "\r\n签名:\r\n" + getSign(file_input[i], key, true)
					+ "\r\n\n";
		}
		return signResult;
	}

	/**
	 * 用以处理接收到的文件，适用于拖拽操作以及文件选择器中的操作
	 * 
	 * @param fileReceive
	 *            传入的文件数组
	 */
	private void receiveFileProcess(File[] fileReceive) {
		fileNames = new String[fileReceive.length];
		for (int i = 0; i < fileReceive.length; i++) {
			fileNames[i] = fileReceive[i].getAbsolutePath();
		}
		edit_input.setText("");
		text_result.setText(fileReceive.length == 1 ? "Selected input file : " + fileReceive[0].getName()
				: "Selected " + fileReceive.length + "input files ");
		edit_input.setText(StringProcessor.stringArrayToDialog(fileNames, "    ; "));
		edit_input.setEnabled(false);
	}

	/**
	 * 检查输入的合法性
	 * 
	 * @return
	 */
	private boolean legalCheck() {
		if (file_input == null || file_input.length == 0 || edit_key.getText().trim().equals("")
				|| edit_key.getText() == null) {
			JOptionPane.showMessageDialog(null, PublicString.INPUT_EMPTY, PublicString.ERROR,
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

}
