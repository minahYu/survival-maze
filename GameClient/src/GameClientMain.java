import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;

import javax.swing.JLabel;
import java.awt.Color;

public class GameClientMain extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtUserName;
	private JTextField txtIpAddress;
	private JTextField txtPortNumber;
	private ImageIcon icon = new ImageIcon("images/main.jpg");
	private Image bg = icon.getImage();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameClientMain frame = new GameClientMain();
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
	
	public GameClientMain() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 750, 470);
		setTitle("Survival Maze");
		
		contentPane = new JPanel() {	
			@Override
			public void paintComponent(Graphics g) { // 배경이미지
				super.paintComponent(g);
				g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
				
			}
		};
		
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("What's your name?");
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("나눔스퀘어 Bold", Font.PLAIN, 17));
		lblNewLabel.setBounds(291, 290, 166, 15);
		contentPane.add(lblNewLabel);
		
		txtUserName = new JTextField();
		txtUserName.setHorizontalAlignment(SwingConstants.CENTER);
		txtUserName.setBounds(269, 310, 210, 38);
		contentPane.add(txtUserName);
		txtUserName.setColumns(10);
		
		JLabel lblIpAddress = new JLabel("IP Address");
		lblIpAddress.setForeground(new Color(255, 255, 255));
		lblIpAddress.setHorizontalAlignment(SwingConstants.CENTER);
		lblIpAddress.setBounds(545, 10, 82, 25);
		contentPane.add(lblIpAddress);
		
		txtIpAddress = new JTextField();
		txtIpAddress.setHorizontalAlignment(SwingConstants.CENTER);
		txtIpAddress.setText("127.0.0.1");
		txtIpAddress.setColumns(10);
		txtIpAddress.setBounds(629, 10, 95, 26);
		contentPane.add(txtIpAddress);
		
		JLabel lblPortNumber = new JLabel("Port Number");
		lblPortNumber.setForeground(new Color(255, 255, 255));
		lblPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		lblPortNumber.setBounds(545, 39, 82, 26);
		contentPane.add(lblPortNumber);
		
		txtPortNumber = new JTextField();
		txtPortNumber.setText("30000");
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setColumns(10);
		txtPortNumber.setBounds(629, 40, 95, 26);
		contentPane.add(txtPortNumber);
		
		JButton btnStart = new JButton("Game Start");
		btnStart.setFont(new Font("나눔스퀘어 ExtraBold", Font.PLAIN, 28));
		btnStart.setBounds(276, 363, 197, 60);
		contentPane.add(btnStart);
		Myaction action = new Myaction();
		btnStart.addActionListener(action);
		txtUserName.addActionListener(action);
		txtIpAddress.addActionListener(action);
		txtPortNumber.addActionListener(action);
	}
	
	class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			String username = txtUserName.getText().trim();
			String ip_addr = txtIpAddress.getText().trim();
			String port_no = txtPortNumber.getText().trim();
			GameClientView view = new GameClientView(username, ip_addr, port_no);
			setVisible(false);
		}
	}
}
