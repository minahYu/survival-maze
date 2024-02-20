import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.Image;
import javax.swing.SwingConstants;

public class GameInfoPanel extends JPanel {
	private ImageIcon p1Icon = new ImageIcon("images/player1.png"); // player1
	private Image p1 = p1Icon.getImage();
	private Image plresize = p1.getScaledInstance(22, 22, p1.SCALE_SMOOTH);
	private ImageIcon p1rIcon = new ImageIcon(plresize);
	private JLabel player1Label = new JLabel(p1rIcon);
	
	private ImageIcon p2Icon = new ImageIcon("images/player2.png"); // player2
	private Image p2 = p2Icon.getImage();
	private Image p2resize = p2.getScaledInstance(22, 22, p2.SCALE_SMOOTH);
	private ImageIcon p2rIcon = new ImageIcon(p2resize);
	private JLabel player2Label = new JLabel(p2rIcon);
	
	private ImageIcon p3Icon = new ImageIcon("images/player3.png"); // player3
	private Image p3 = p3Icon.getImage();
	private Image p3resize = p3.getScaledInstance(22, 22, p3.SCALE_SMOOTH);
	private ImageIcon p3rIcon = new ImageIcon(p3resize);
	private JLabel player3Label = new JLabel(p3rIcon);
	
	private ImageIcon p4Icon = new ImageIcon("images/player4.png"); // player4
	private Image p4 = p4Icon.getImage();
	private Image p4resize = p4.getScaledInstance(22, 22, p4.SCALE_SMOOTH);
	private ImageIcon p4rIcon = new ImageIcon(p4resize);
	private JLabel player4Label = new JLabel(p4rIcon);
	
	private ImageIcon heartIcon = new ImageIcon("images/heart.png");
	private Image heart = heartIcon.getImage();
	private Image heartresize = heart.getScaledInstance(18, 18, heart.SCALE_SMOOTH);
	private ImageIcon heartrIcon = new ImageIcon(heartresize);
	
	private ImageIcon coinIcon = new ImageIcon("images/coin.png");
	private Image coin = coinIcon.getImage();
	private Image coinresize = coin.getScaledInstance(18, 18, coin.SCALE_SMOOTH);
	private ImageIcon coinrIcon = new ImageIcon(coinresize);
	public static JLabel[] pointLabel = new JLabel[4];
	
	private int time = 60;
	
	public static JLabel timerLabel; // 남은시간
	
	/*private String player1Name;
	private String player2Name;
	private String player3Name;
	private String player4Name;*/
	
	
	private static String[] playerName = new String[4];
	
	public static JLabel player1NameLabel = new JLabel();
	public static JLabel player2NameLabel = new JLabel();
	public static JLabel player3NameLabel = new JLabel();
	public static JLabel player4NameLabel = new JLabel();
	
	private static JLabel heart1[] = new JLabel[3];
	private static JLabel heart2[] = new JLabel[3];
	private static JLabel heart3[] = new JLabel[3];
	private static JLabel heart4[] = new JLabel[3];
	
	private JLabel point1 = new JLabel("0");
	private JLabel point2 = new JLabel("0");
	private JLabel point3 = new JLabel("0");
	private JLabel point4 = new JLabel("0");
	
	public static boolean heartCnt[] = new boolean[4];
	public static int point[] = new int[4];
	
	private Player1Info player1Info;
	private Player2Info player2Info;
	private Player3Info player3Info;
	private Player4Info player4Info;
	
	private static String UserName;
	private static int takeUserNum;
	
	public static boolean gameover = false;
	private static int n = 0;
	
	public GameInfoPanel(String userName, int userNum) {
		setLayout(null);
	
		UserName = userName;
		takeUserNum = userNum;
		
		player1Info = new Player1Info();
		player2Info = new Player2Info();
		player3Info = new Player3Info();
		player4Info = new Player4Info();
	}
	
	public static void setPoint(int userNum, int coin) {
		switch(userNum) {
		case 0:
			point[0] = coin;
			pointLabel[0].setText(String.valueOf(coin));
			pointLabel[0].repaint();
			pointLabel[0].revalidate();
			break;
		case 1:
			point[1] = coin;
			pointLabel[1].setText(String.valueOf(coin));
			pointLabel[1].repaint();
			pointLabel[1].revalidate();
			break;
		case 2:
			point[2] = coin;
			pointLabel[2].setText(String.valueOf(coin));
			pointLabel[2].repaint();
			pointLabel[2].revalidate();
			break;
		case 3:
			point[3] = coin;
			pointLabel[3].setText(String.valueOf(coin));
			pointLabel[3].repaint();
			pointLabel[3].revalidate();
			break;
		}
	}
	
	public static void setRemoveHeart(int userNum, int heart) {
		switch(userNum) {
		case 0:
			//System.out.println(heart + "remove heart");
			heart1[heart].setVisible(false);
			break;
		case 1:
			//System.out.println(heart + "remove heart");
			heart2[heart].setVisible(false);
			break;
		case 2:
			//System.out.println(heart + "remove heart");
			heart3[heart].setVisible(false);
			break;
		case 3:
			//System.out.println(heart + "remove heart");
			heart4[heart].setVisible(false);
			break;
		}
	}
	
	public static void setAddHeart(int userNum, int heart) {
		System.out.println("heart draw");
		
		switch(userNum) {
		case 0:
			heart1[heart-1].setVisible(true);
			break;
		case 1:
			heart2[heart-1].setVisible(true);
			break;
		case 2:
			heart3[heart-1].setVisible(true);
			break;
		case 3:
			heart4[heart-1].setVisible(true);
			break;
		}
	}
	
	public static void setUserName(int playerNum, String playersName) {
		//playersNum
		if(playerNum == 0) {
			player1NameLabel = new JLabel(playersName);
			player1NameLabel.setLocation(70, 50);
			player1NameLabel.setFont(new Font("나눔고딕", Font.BOLD, 14));
			player1NameLabel.setSize(40,40);
			//GameClientView.sendName(0, playersName);
			//System.out.println("player1Name Label" + playersName);
         }
         else if(playerNum == 1) {
        	//player2Name = new JLabel(playersName);
        	player2NameLabel = new JLabel(playersName);
    		player2NameLabel.setLocation(70, 140);
    		player2NameLabel.setFont(new Font("나눔고딕", Font.BOLD, 14));
    		player2NameLabel.setSize(40,40);
			//GameClientView.sendName(1, playersName);
			//System.out.println("player2Name Label" + playersName);
         }
         else if(playerNum == 2) {
        	 //player3Name = new JLabel(playersName);
            //infoPanel.setUserName(playersNum[2], playersName[2]);
            //System.out.println("2playerName " + playersName[2]);
        	player3NameLabel = new JLabel(playersName);
     		player3NameLabel.setLocation(70, 230);
    		player3NameLabel.setFont(new Font("나눔고딕", Font.BOLD, 14));
    		player3NameLabel.setSize(40,40);
			//GameClientView.sendName(2, playersName);
			//System.out.println("player3Name Label");
         }
         else if(playerNum == 3) {
    		player4NameLabel = new JLabel(playersName);
    		player4NameLabel.setLocation(70, 320);
    		player4NameLabel.setFont(new Font("나눔고딕", Font.BOLD, 14));
    		player4NameLabel.setSize(40,40);

         }
	}
	
	class Player1Info {
		public Player1Info() {
			// 공통
			player1Label.setLocation(20, 50);
			player1Label.setSize(40,40);
			add(player1Label);
			
			player2Label.setLocation(20, 140);
			player2Label.setSize(40,40);
			add(player2Label);
			
			player3Label.setLocation(20, 230);
			player3Label.setSize(40,40);
			add(player3Label);
			
			player4Label.setLocation(20, 320);
			player4Label.setSize(40,40);
			add(player4Label);
			
			
			for(int i=0; i<4; i++) {
				pointLabel[i] = new JLabel(coinrIcon);
				pointLabel[i].setLocation(60, 100+i*90);
				pointLabel[i].setSize(50,50);
				add(pointLabel[i]);
			}	
			
			timerLabel = new JLabel();
			timerLabel.setSize(200, 20);
			timerLabel.setLocation(95, 10);
			timerLabel.setFont(new Font("나눔고딕", Font.BOLD, 14));
			timerLabel.setForeground(Color.BLUE);
			
			add(timerLabel);
			Timer th = new Timer(timerLabel);
			th.start();
			
			repaint();
			revalidate();
			
			add(player1NameLabel);
			add(player2NameLabel);
			add(player3NameLabel);
			add(player4NameLabel);
			
			// 개인
			for(int i=0; i<heart1.length; i++) {
				if(heart1[i] != null) {
					break;
				} else {
					heart1[i] = new JLabel(heartrIcon);
					heart1[i].setSize(20, 20);
					heart1[i].setLocation(70+i*30, 90);
					add(heart1[i]);
				}
			}
		}
	}
	
	class Player2Info {
		public Player2Info() {
			// 개인
			for(int i=0; i<heart2.length; i++) {
				if(heart2[i] != null) {
					break;
				} else {
					heart2[i] = new JLabel(heartrIcon);
					heart2[i].setSize(20, 20);
					heart2[i].setLocation(70+i*30, 180);
					add(heart2[i]);
				}
			}
		}
	}
	
	class Player3Info {
		public Player3Info() {
			// 개인
			for(int i=0; i<heart3.length; i++) {
				if(heart3[i] != null) {
					break;
				} else {
					heart3[i] = new JLabel(heartrIcon);
					heart3[i].setSize(20, 20);
					heart3[i].setLocation(70+i*30, 270);
					add(heart3[i]);
				}
			}
		}
	}
	
	class Player4Info {
		public Player4Info() {
			// 개인
			for(int i=0; i<heart4.length; i++) {
				if(heart4[i] != null) {
					break;
				} else {
					heart4[i] = new JLabel(heartrIcon);
					heart4[i].setSize(20, 20);
					heart4[i].setLocation(70+i*30, 360);
					add(heart4[i]);
				}
			}
		}
	}
	
	public static void winner() {
		int temp;
		int num[] = new int[4];
		int ranking[] = new int[4];
		
		for(int i=0; i<4; i++) {
			num[i] = point[i];
		}
		
		for(int k=0; k<4; k++) {
			for(int j=0; j<3; j++) { // 점수비교
				temp = num[j];
				num[j] = num[j+1];
				num[j+1] = temp;
			}
		}
		for(int i=0; i<4; i++) {
			for(int j=0; j<4; j++) {
				if(num[i] == GameInfoPanel.point[j]) {
					ranking[j] = i; // j번째 플레이어의 순위는 i
				}
			}
		}
		
		for(int i=0; i<4; i++) {
			if(ranking[i] == 0) {
				if(i==0) {
					JLabel win = new JLabel("WINNER");
					win.setLocation(70, 50);
					win.setForeground(Color.RED);
					win.setFont(new Font("나눔스퀘어 Bold", Font.PLAIN, 20));
					win.setSize(120, 50);
				}
				else if(i==0) {
					JLabel win = new JLabel("WINNER");
					win.setLocation(70, 140);
					win.setForeground(Color.RED);
					win.setFont(new Font("나눔스퀘어 Bold", Font.PLAIN, 20));
					win.setSize(120, 50);
				}
				else if(i==1) {
					JLabel win = new JLabel("WINNER");
					win.setLocation(70, 230);
					win.setForeground(Color.RED);
					win.setFont(new Font("나눔스퀘어 Bold", Font.PLAIN, 20));
					win.setSize(120, 50);
				}
				else if(i==2) {
					JLabel win = new JLabel("WINNER");
					win.setLocation(70, 320);
					win.setForeground(Color.RED);
					win.setFont(new Font("나눔스퀘어 Bold", Font.PLAIN, 20));
					win.setSize(120, 50);
				}
			}
		}
	}
	
	public static void setNewTime(int userNum, int newTime) {
		
		if(userNum == 0) {
			timerLabel.setText("남은시간   " + Integer.toString(newTime));
			if(newTime == 0) {
				GameClientPlay.start = false;
				gameover = true;
				GameClientView.sendGameOver();
				for(int i=0; i<4; i++) {
					if(GameClientPlay.live[i]) { // 살아있으면
						point[i]+= 200; // 추가점수 200점
					}
				}
			}
		}
	}
	
	class Timer extends Thread {
		private static JLabel timerLabel;
		private boolean flag = false;
		
		public Timer(JLabel timerLabel) {
			this.timerLabel = timerLabel;
		}
		
		@Override
		public void run() {
			time = 600;
			GameClientPlay.start = true;
			while(true) {
				time--;
				try {
					GameClientView.sendTime(takeUserNum, time);
					sleep(1000);
					if(flag == true) {
						return;
					}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(time <= 0) {
					flag = true;
					break;
				}
			}
		}
	}

}
