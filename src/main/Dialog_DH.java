package main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import tool.PublicString;
import tool.crypto.RSA_Encryptor;

public abstract class Dialog_DH extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7058295238323126351L;

	public abstract String returnValue(boolean isGetPubKey) throws Exception;

	public BigInteger[] result = new BigInteger[3];
	private boolean isGetPubKey;
	private JTextField edit_x = new JTextField();
	private JTextField edit_p = new JTextField();
	private JTextField edit_g = new JTextField();
	private JButton bt_genPrime = new JButton(PublicString.GEN_PRIME);
	private JButton bt_genarator = new JButton(PublicString.GET_KEY);
	private JPanel panel_center = new JPanel(new GridLayout(2, 6, 5, 5));
	private JPanel panel_button = new JPanel(new FlowLayout(FlowLayout.CENTER));

	public Dialog_DH(boolean isGetPubKey, int posX, int posY,  int width) {
		setTitle("获取" + (isGetPubKey ? PublicString.PUBLIC_KEY : PublicString.PRIVATE_KEY));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocation(posX, posY);
		setSize(width, width/3);
		setLayout(new BorderLayout());

		panel_center.add(new JLabel("X值(私钥的随机数)"));
		panel_center.add(new JLabel("P值(大素数)"));
		panel_center.add(new JLabel("G值(本原元)"));
		panel_center.add(edit_x);
		panel_center.add(edit_p);
		panel_center.add(edit_g);
		panel_button.add(bt_genPrime);
		panel_button.add(bt_genarator);
		add(panel_center, BorderLayout.CENTER);
		add(panel_button, BorderLayout.SOUTH);

		this.isGetPubKey = isGetPubKey;

		bt_genarator.addActionListener(this);
		bt_genPrime.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(bt_genPrime)) {
			edit_p.setText(RSA_Encryptor.genaratePrime().toString());
		} else if (e.getSource().equals(bt_genarator)) {
			if (edit_g.getText().trim().equals("") || edit_p.getText().trim().equals("")
					|| edit_x.getText().trim().equals(""))
				JOptionPane.showMessageDialog(null, PublicString.INPUT_EMPTY, PublicString.ERROR,
						JOptionPane.ERROR_MESSAGE);
			else {
				try {
					result[1] = new BigInteger(edit_p.getText().trim());
					result[2] = new BigInteger(edit_g.getText().trim());
					result[0] = new BigInteger(edit_x.getText().trim());
					returnValue(isGetPubKey);
//					 System.exit(0);
					this.dispose();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}
	}

}
