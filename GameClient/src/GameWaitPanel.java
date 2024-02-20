import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class GameWaitPanel extends JPanel {
	/**
	 * Create the panel.
	 */
	
	public GameWaitPanel() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Loading...");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(310, 231, 114, 23);
		add(lblNewLabel);
		
		JButton close = new JButton("종료");
		close.setHorizontalAlignment(SwingConstants.CENTER);
		close.setBounds(310, 291, 114, 23);
		add(close);
		
		close.addActionListener(new ActionListener() { // btn버튼 누르면 화면 사라짐
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		setVisible(true);
	}

}
