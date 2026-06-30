package GUI;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Exceptions.AccNotFound;
import Exceptions.InvalidAmount;
import Exceptions.MaxBalance;
import Exceptions.MaxWithdraw;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.awt.event.ActionEvent;
import Data.FileIO;
import java.awt.SystemColor;

public class TransferAcc extends JFrame implements Serializable {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtFrom;
	private JTextField txtTo;
	private JTextField txtAmount;

	public TransferAcc() {
		setTitle("Transfer Funds");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 320);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.activeCaption);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblTitle = new JLabel("Transfer Funds");
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(10, 11, 414, 36);
		contentPane.add(lblTitle);
		
		JLabel lblFrom = new JLabel("From Account Number:");
		lblFrom.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFrom.setBounds(0, 80, 150, 14);
		contentPane.add(lblFrom);
		
		txtFrom = new JTextField();
		txtFrom.setBounds(165, 77, 211, 20);
		contentPane.add(txtFrom);
		txtFrom.setColumns(10);
		
		JLabel lblTo = new JLabel("To Account Number:");
		lblTo.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTo.setBounds(0, 130, 150, 14);
		contentPane.add(lblTo);
		
		txtTo = new JTextField();
		txtTo.setBounds(165, 127, 211, 20);
		contentPane.add(txtTo);
		txtTo.setColumns(10);
		
		JLabel lblAmount = new JLabel("Amount:");
		lblAmount.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAmount.setBounds(0, 180, 150, 14);
		contentPane.add(lblAmount);
		
		txtAmount = new JTextField();
		txtAmount.setBounds(165, 177, 211, 20);
		contentPane.add(txtAmount);
		txtAmount.setColumns(10);
		
		JButton btnTransfer = new JButton("Transfer");
		btnTransfer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fromAcc = txtFrom.getText();
				String toAcc = txtTo.getText();
				double amt;
				try {
					amt = Double.parseDouble(txtAmount.getText());
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(getComponent(0), "Invalid data entered. Please enter a valid numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
					txtAmount.setText(null);
					return;
				}
				
				int a = JOptionPane.showConfirmDialog(getComponent(0), "Confirm Transfer?");
				if (a == 0) {
					try {
						FileIO.bank.transfer(fromAcc, toAcc, amt);
						JOptionPane.showMessageDialog(getComponent(0), "Transfer Successful");
						GUIForm.UpdateDisplay();
						dispose();
					} catch (MaxBalance e1) {
						JOptionPane.showMessageDialog(getComponent(0), "Insufficient Balance in source account.");
					} catch (MaxWithdraw e1) {
						JOptionPane.showMessageDialog(getComponent(0), "Maximum Withdraw Limit Exceeded on source account.");
					} catch (InvalidAmount e1) {
						JOptionPane.showMessageDialog(getComponent(0), "Invalid Amount entered.");
					} catch (AccNotFound e1) {
						JOptionPane.showMessageDialog(getComponent(0), "Source or destination account not found.");
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(getComponent(0), "Transfer Failed: " + e1.getMessage());
					} finally {
						txtFrom.setText(null);
						txtTo.setText(null);
						txtAmount.setText(null);
					}
				}
			}
		});
		btnTransfer.setBounds(86, 230, 95, 23);
		contentPane.add(btnTransfer);
		
		JButton btnReset = new JButton("Reset");
		btnReset.setBounds(260, 230, 89, 23);
		contentPane.add(btnReset);
		
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtFrom.setText(null);
				txtTo.setText(null);
				txtAmount.setText(null);
			}
		});
	}
}
