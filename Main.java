package de.main;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Main extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField tF_decimal;
	private JTextField tF_binary;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JButton btnSubmit;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		init();
		initListener();
	}

	private String IPToBinary(String IP) {
		try {
			String[] strings = IP.split("\\.", 4);
			String text = "";
			for (int i = 0; i < strings.length; i++) {
				if (Integer.parseInt(strings[i]) < 256) {
					text += " " + String.format("%8s", Integer.toBinaryString(Integer.parseInt(strings[i]))).replace(" ", "0");
				} else {
					return tF_binary.getText();
				}
			}
			return text.substring(1);
		} catch (Exception e) {
			return tF_binary.getText();
		}
	}

	private String BinarytoIP(String binary) {
		try {
			String[] bytes = binary.split(" ", 4);
			String ip = "";
			for (String current : bytes) {
				if (!(current.length() <= 8)) {
					ip += "." + Integer.parseInt(current.substring(0, 8), 2);
					continue;
				}
				ip += "." + Integer.parseInt(current, 2);
			}
			return ip.substring(1);
		} catch (Exception e) {
			return tF_decimal.getText();
		}
	}

	private void initListener() {
		tF_binary.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				tFBinaryChange();

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				tFBinaryChange();

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				tFBinaryChange();

			}
		});

		tF_decimal.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				tFDecimalChange();

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				tFDecimalChange();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				tFDecimalChange();
			}
		});
		
		btnSubmit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String ip = textField.getText();
				String net = textField_1.getText();
				
				try {
					if (net.startsWith("/")) {
						int n = Integer.parseInt(net.substring(1));
						if (n < 33 && n > 0) {
							String a = "11111111111111111111111111111111"; //32 Einsen hintereinander
							String b = "00000000000000000000000000000000"; //32 Nullen hintereinander
							net = a.substring(0, n) + b.substring(n, 32);
						} else {
							net = "11111111111111111111111111111111"; //32 Einsen hintereinander
						}
					} else if (net.contains(".")) {
						net = IPToBinary(net);
					}
					
					net = net.replace(" ", "");
					
					if (net.length() != 32) {
						netIDError("Falsche Subnetzmaske");
					}
					
					boolean one = true;
					for (char c : net.toCharArray()) {
						if ((one && c == '1') || (!one && c == '0')) {
							continue;
						} else if(one && c == '0') {
							one = false;
						} else {
							netIDError("Falsche Subnetzmaske");
							return;
						}
					}
					
					if (ip.contains(".")) {
						ip = IPToBinary(ip);
					}
					ip = ip.replace(" ", "");
					
					if (ip.length() != 32) {
						netIDError("Falsche IPv4-Adresse");
					}
					
					String netID = "";
					char[] ipBit = ip.toCharArray();
					char[] netBit = net.toCharArray();
					
					for (int i = 0; i < ip.length(); i++) {
						char currentIP = ipBit[i];
						char currentNet = netBit[i];
						if (currentIP == '1' && currentNet == '1') {
							netID += "1";
						} else {
							netID += "0";
						}
						if ((i - 7) % 8 == 0) {
							netID += " ";
						}
					}
					
					textField_2.setText(BinarytoIP(netID));
					textField_3.setText(netID);
					
				} catch (Exception e2) {
					netIDError("Error");
				}
			}
		});
	}
	
	private void netIDError(String error) {
		textField_2.setText(error);
		textField_3.setText(error);
	}

	private void tFDecimalChange() {
		try {
			tF_binary.setText(IPToBinary(tF_decimal.getText()));
		} catch (Exception e) {
			return;
		}
	}

	private void tFBinaryChange() {
		try {
			tF_decimal.setText(BinarytoIP(tF_binary.getText()));
		} catch (Exception e) {
			return;
		}
	}

	private void init() {
		setTitle("IP \u00DCbersetzer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 370, 460);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		initTranslator();
		initMask();

	}
	
	private void initMask() {
		JPanel panel = new JPanel();
		panel.setBounds(0, 148, 350, 262);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Netz-ID");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(0, 0, 350, 25);
		panel.add(lblNewLabel);
		
		JLabel lbl_decimal_1 = new JLabel("IPv4");
		lbl_decimal_1.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_decimal_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lbl_decimal_1.setBounds(0, 36, 75, 20);
		panel.add(lbl_decimal_1);
		
		textField = new JTextField();
		textField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		textField.setColumns(10);
		textField.setBounds(75, 37, 275, 20);
		panel.add(textField);
		
		JLabel lbl_binary_1 = new JLabel("Nezmaske");
		lbl_binary_1.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_binary_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lbl_binary_1.setBounds(0, 67, 75, 20);
		panel.add(lbl_binary_1);
		
		textField_1 = new JTextField();
		textField_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		textField_1.setColumns(10);
		textField_1.setBounds(75, 67, 275, 20);
		panel.add(textField_1);
		
		JLabel lbl_binary_1_1 = new JLabel("Netz-ID");
		lbl_binary_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_binary_1_1.setFont(new Font("Tahoma", Font.BOLD, 13));
		lbl_binary_1_1.setBounds(0, 132, 350, 20);
		panel.add(lbl_binary_1_1);
		
		textField_2 = new JTextField();
		textField_2.setEditable(false);
		textField_2.setFont(new Font("Tahoma", Font.PLAIN, 13));
		textField_2.setColumns(10);
		textField_2.setBounds(75, 163, 275, 20);
		panel.add(textField_2);
		
		JLabel lbl_binary_1_2 = new JLabel("Bin\u00E4r");
		lbl_binary_1_2.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_binary_1_2.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lbl_binary_1_2.setBounds(0, 195, 75, 20);
		panel.add(lbl_binary_1_2);
		
		textField_3 = new JTextField();
		textField_3.setEditable(false);
		textField_3.setFont(new Font("Tahoma", Font.PLAIN, 13));
		textField_3.setColumns(10);
		textField_3.setBounds(75, 195, 275, 20);
		panel.add(textField_3);
		
		JLabel lbl_binary_1_2_1 = new JLabel("Dezimal");
		lbl_binary_1_2_1.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_binary_1_2_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lbl_binary_1_2_1.setBounds(0, 163, 75, 20);
		panel.add(lbl_binary_1_2_1);
		
		JTextArea txtrIpvAlsDezimal = new JTextArea();
		txtrIpvAlsDezimal.setText("IPv4: Als Dezimal oder Bin\u00E4r angeben\r\nNetzmaske: Als Dezimal, Bin\u00E4r oder /x angeben");
		txtrIpvAlsDezimal.setLineWrap(true);
		txtrIpvAlsDezimal.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtrIpvAlsDezimal.setEditable(false);
		txtrIpvAlsDezimal.setBounds(10, 226, 340, 36);
		panel.add(txtrIpvAlsDezimal);
		
		btnSubmit = new JButton("Netz-ID berechnen");
		btnSubmit.setBounds(0, 98, 350, 23);
		panel.add(btnSubmit);
	}

	private void initTranslator() {
		JPanel p_binaryDecimalTranslator = new JPanel();
		p_binaryDecimalTranslator.setBounds(0, 0, 350, 150);
		contentPane.add(p_binaryDecimalTranslator);
		p_binaryDecimalTranslator.setLayout(null);

		JLabel lbl_decimal = new JLabel("IPv4");
		lbl_decimal.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_decimal.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lbl_decimal.setBounds(0, 40, 75, 20);
		p_binaryDecimalTranslator.add(lbl_decimal);

		JLabel lbl_binary = new JLabel("Bin\u00E4r-IP");
		lbl_binary.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lbl_binary.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_binary.setBounds(0, 70, 75, 20);
		p_binaryDecimalTranslator.add(lbl_binary);

		tF_decimal = new JTextField();
		tF_decimal.setFont(new Font("Tahoma", Font.PLAIN, 13));
		tF_decimal.setBounds(75, 40, 275, 20);
		p_binaryDecimalTranslator.add(tF_decimal);
		tF_decimal.setColumns(10);

		tF_binary = new JTextField();
		tF_binary.setFont(new Font("Tahoma", Font.PLAIN, 13));
		tF_binary.setBounds(75, 70, 275, 20);
		p_binaryDecimalTranslator.add(tF_binary);
		tF_binary.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Dezimal & Bin\u00E4r\u00FCbersetzer");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(0, 4, 350, 25);
		p_binaryDecimalTranslator.add(lblNewLabel_2);

		JTextArea txtrIpMitPunkten = new JTextArea();
		txtrIpMitPunkten.setLineWrap(true);
		txtrIpMitPunkten.setEditable(false);
		txtrIpMitPunkten.setText("IPv4: mit Punkten angeben\r\nBin\u00E4r-IP: mit Leerzeichen angeben | Nur 0 und 1");
		txtrIpMitPunkten.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtrIpMitPunkten.setBounds(10, 101, 340, 38);
		p_binaryDecimalTranslator.add(txtrIpMitPunkten);
	}
}