

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;

public class GameClientPlay extends JPanel /*implements Runnable*/ {	
	public static int myX; // 나의 x값
	public static int myY; // 나의 y값
	
	public static int otherX; // 다른 유저들의 x값
	public static int otherY; // 다른 유저들의 y값
	
	private int enemyX; // 적의 x값
	private int enemyY; // 적의 y값
	
	// 플레이어 이동
	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	private static final int UP = 2;
	private static final int DOWN = 3;
	
	private int direction;
	
	// player1: 0 / player2: 1 / player3: 2 / player4: 3
	public static int takeUserNum; // player마다 부여 받은 번호
	private int eatUserNum; // coin을 먹은 player의 번호
	private int heartUserNum;
	
	public static String UserName; // player의 이름
	
	//private boolean wall = false;
	
	private ImageIcon enemyIcon = new ImageIcon("images/enemy.png");
	private Image e = enemyIcon.getImage();
	private Image eresize = e.getScaledInstance(22, 22, e.SCALE_SMOOTH);
	private ImageIcon erIcon = new ImageIcon(eresize);
	
	private JLabel lrEnemy1 = new JLabel(erIcon); // 적 1 (3, 6)
	private JLabel lrEnemy2 = new JLabel(erIcon); // 적 2 (3, 17)
	
	private JLabel udEnemy1 = new JLabel(erIcon); // 적 3 (5, 11)
	private JLabel udEnemy2 = new JLabel(erIcon); // 적 4 (18, 12)
	
	private ImageIcon wallIcon = new ImageIcon("images/wall.png"); // 벽
	private Image w = wallIcon.getImage();
	private Image wresize = w.getScaledInstance(21, 21, w.SCALE_SMOOTH);
	private ImageIcon wrIcon = new ImageIcon(wresize);
	private JLabel wallLabel;
	
	private ImageIcon heartIcon = new ImageIcon("images/heart.png"); // 벽
	private Image h = heartIcon.getImage();
	private Image hresize = h.getScaledInstance(21, 21, h.SCALE_SMOOTH);
	private ImageIcon hrIcon = new ImageIcon(hresize);
	private JLabel heartLabel[] = new JLabel[2];
	
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
	
	private ImageIcon coinIcon = new ImageIcon("images/coin.png"); // coin
	private Image coin = coinIcon.getImage();
	private Image coinresize = coin.getScaledInstance(22, 22, coin.SCALE_SMOOTH);
	private ImageIcon coinrIcon = new ImageIcon(coinresize);
	private JLabel coinLabel;
	
	private static ArrayList<JLabel> coins = new ArrayList<JLabel>();
	
	private int[] rx = new int[60];
	private int[] ry = new int[60];
	
	private Player1 player1;
	private Player2 player2;
	private Player3 player3;
	private Player4 player4;
	
	private Thread enemyLRThread;
	private Thread enemyUDThread;
	
	private int p1Heart = 3;
	private int p2Heart = 3;
	private int p3Heart = 3;
	private int p4Heart = 3;
	
	public static int player1Point = 0;
	public static int player2Point = 0;
	public static int player3Point = 0;
	public static int player4Point = 0;
	
	private int ranNumCnt = 0;
	
	/*public static int[] playersNum = new int[4];
	public static String[] playersName = new String[4];*/
	
	private int iCoin=0; // 코인의 번호(coins[i]에서 i에 들어갈)
	private int removeCoinNumber = 0; // 지워져야할 코인의 번호 (coins[i]에서 i에 들어갈)
	private int removeHeartNumber = 0;
	private int heartXLocation[] = new int[2];
	private int heartYLocation[] = new int[2];
	
	private int addHeartCnt = 0;
	
	public static boolean start = false;
	private JLabel winner;
	
	public static boolean[] live = new boolean[4];
	
	public static int drawcnt = 0;
	
	public static int[][] map1 = { 
			// 0-3: 플레이어, 4: 벽, 5: 통로, 6: 적(좌우로 이동), 7: 적(위아래 이동), 8: 코인, 9: 생명, 10: 점수바꾸기
			{4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4},
			{4,0,5,4,4,5,5,5,5,5,5,4,4,5,4,4,4,5,5,5,3,4},
			{4,5,5,5,4,4,4,4,4,5,5,5,5,5,5,5,5,5,5,5,5,4},
			{4,6,6,6,6,6,6,4,4,4,4,4,6,6,6,6,6,6,4,4,4,4},
			{4,5,5,5,5,4,4,4,4,5,5,5,5,5,5,4,4,4,5,5,5,4},
			{4,5,5,5,5,4,4,4,4,5,5,5,5,4,4,4,4,5,5,5,5,4},
			{4,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,4},
			{4,4,4,5,5,5,4,4,4,5,5,4,4,4,5,5,5,5,4,5,4,4},
			{4,4,4,4,5,5,5,5,5,5,5,5,5,5,5,5,9,4,4,4,4,4},
			{4,5,5,5,4,4,4,4,5,5,5,5,4,5,5,5,5,4,4,5,5,4},
			{4,5,5,5,4,4,5,4,5,5,5,4,4,5,5,5,5,4,4,5,5,4},
			{4,5,5,5,4,7,4,4,5,5,9,5,4,5,5,4,4,5,5,5,5,4},
			{4,5,5,5,5,7,4,4,4,4,5,5,5,5,4,7,5,5,5,5,5,4},
			{4,4,4,5,5,7,5,5,5,5,5,4,4,4,5,7,5,5,5,4,4,4},
			{4,5,5,5,5,7,5,5,5,5,5,5,5,5,5,7,5,5,5,4,5,4},
			{4,5,5,5,5,7,5,5,5,5,5,5,5,5,5,7,5,5,5,5,5,4},
			{4,5,5,5,5,4,4,4,5,5,5,5,4,4,5,7,5,5,5,5,5,4},
			{4,5,5,5,5,4,4,5,5,5,5,5,5,4,4,4,5,5,5,5,5,4},
			{4,5,5,5,4,4,4,5,5,5,4,4,4,4,5,5,5,5,5,5,5,4},
			{4,5,5,5,5,4,4,4,4,5,5,5,5,5,4,4,5,5,5,5,5,4},
			{4,1,5,5,5,4,4,4,5,5,4,4,4,5,5,5,5,5,5,5,2,4},
			{4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4},
	};
	
	public GameClientPlay(String userName, int userNum) {
		setBackground(new Color(0, 0, 0));
		setLayout(null);
		UserName = userName;
		takeUserNum = userNum;
		
		player1 = new Player1();
		player1.draw();
		
		direction = LEFT;
		this.addKeyListener(new MyKeyListener());
		
		player2 = new Player2();
		player3 = new Player3();
		player4 = new Player4();
		
		for(int i=0; i<4; i++) {
			live[i] = true;
		}
		
		enemyLRThread = new MoveLREnemy();
		enemyLRThread.start();
		enemyUDThread = new MoveUDEnemy();
		enemyUDThread.start();
	}
	
	// 눌리는 방향키에 따라 player 이동
	class MyKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
				if(start) {
					if(takeUserNum == 0) {
						switch(e.getKeyCode()) {
						case KeyEvent.VK_LEFT:
							direction = LEFT;
							if(p1Heart > 0)
								player1.move(direction);
							break;
						case KeyEvent.VK_RIGHT:
							direction = RIGHT;
							if(p1Heart > 0)
								player1.move(direction);
							break;
						case KeyEvent.VK_UP:
							direction = UP;
							if(p1Heart > 0)
								player1.move(direction);
							break;
						case KeyEvent.VK_DOWN:
							direction = DOWN;
							if(p1Heart > 0)
								player1.move(direction);
							break;
						}
					}
					
					if(takeUserNum == 1) {
						switch(e.getKeyCode()) {
						case KeyEvent.VK_LEFT:
							direction = LEFT;
							if(p2Heart > 0)
								player2.move(direction);
							break;
						case KeyEvent.VK_RIGHT:
							direction = RIGHT;
							if(p2Heart > 0)
								player2.move(direction);
							break;
						case KeyEvent.VK_UP:
							direction = UP;
							if(p2Heart > 0)
								player2.move(direction);
							break;
						case KeyEvent.VK_DOWN:
							direction = DOWN;
							if(p2Heart > 0)
								player2.move(direction);
							break;
						}
					}
					
					if(takeUserNum == 2) {
						switch(e.getKeyCode()) {
						case KeyEvent.VK_LEFT:
							direction = LEFT;
							if(p3Heart > 0)
								player3.move(direction);
							break;
						case KeyEvent.VK_RIGHT:
							direction = RIGHT;
							if(p3Heart > 0)
								player3.move(direction);
							break;
						case KeyEvent.VK_UP:
							direction = UP;
							if(p3Heart > 0)
								player3.move(direction);
							break;
						case KeyEvent.VK_DOWN:
							direction = DOWN;
							if(p3Heart > 0)
								player3.move(direction);
							break;
						}
					}
					
					if(takeUserNum == 3) {
						switch(e.getKeyCode()) {
						case KeyEvent.VK_LEFT:
							direction = LEFT;
							if(p4Heart > 0)
								player4.move(direction);
							break;
						case KeyEvent.VK_RIGHT:
							direction = RIGHT;
							if(p4Heart > 0)
								player4.move(direction);
							break;
						case KeyEvent.VK_UP:
							direction = UP;
							if(p4Heart > 0)
								player4.move(direction);
							break;
						case KeyEvent.VK_DOWN:
							direction = DOWN;
							if(p4Heart > 0)
								player4.move(direction);
							break;
						}
					}
				}
			}
	}
	
	public void touchOtherPlayer() { // 다른 플레이어와 닿을경우 코인이 더 적은 플레이어가 부딪힌 플레이어의 코인을 뺏어올 수 있음(10만큼). 코인의 수가 같을 경우 그냥 지나감
		// player1이 다른 플레이어와 만날 때
		if(player1Label.getX() == player2Label.getX() && player1Label.getY() == player2Label.getY()) { // player1 player2
			if(player1Point > player2Point) { // player1이 코인을 더 많이 가지고 있을 경우
				player1Point -= 10;
				player2Point += 10;
			} else if(player1Point < player2Point) { // player2가 코인을 더 많이 가지고 있을 경우
				player1Point += 10;
				player2Point -= 10;
			}/* else {
				player1Point = player1Point;
				player2Point = player2Point;
			}*/
			GameClientView.sendPoint(0, player1Point);
			GameClientView.sendPoint(1, player2Point);
		}
		if(player1Label.getX() == player3Label.getX() && player1Label.getY() == player3Label.getY()) { // player1 player3
			if(player1Point > player3Point) { // player1이 코인을 더 많이 가지고 있을 경우
				player1Point -= 10;
				player3Point += 10;
			} else if(player1Point < player3Point) { // playe3가 코인을 더 많이 가지고 있을 경우
				player1Point += 10;
				player3Point -= 10;
			}/* else {
				player1Point = player1Point;
				player3Point = player3Point;
			}*/
			GameClientView.sendPoint(0, player1Point);
			GameClientView.sendPoint(2, player3Point);
		}
		if(player1Label.getX() == player4Label.getX() && player1Label.getY() == player4Label.getY()) { // player1 player4
			if(player1Point > player4Point) { // player1이 코인을 더 많이 가지고 있을 경우
				player1Point -= 10;
				player4Point += 10;
			} else if(player1Point < player4Point) { // playe4가 코인을 더 많이 가지고 있을 경우
				player1Point += 10;
				player4Point -= 10;
			}/* else {
				player1Point = player1Point;
				player4Point = player4Point;
			}*/
			GameClientView.sendPoint(0, player1Point);
			GameClientView.sendPoint(3, player4Point);
		}
		
		
		// player2이 다른 플레이어와 만날 때
		if(player2Label.getX() == player3Label.getX() && player2Label.getY() == player3Label.getY()) { // player1 player3
			if(player2Point > player3Point) { // player1이 코인을 더 많이 가지고 있을 경우
				player2Point -= 10;
				player3Point += 10;
			} else if(player2Point < player3Point) { // playe3가 코인을 더 많이 가지고 있을 경우
				player2Point += 10;
				player3Point -= 10;
			}/* else {
				player3Point = player3Point;
				player2Point = player2Point;
			}*/
			GameClientView.sendPoint(1, player2Point);
			GameClientView.sendPoint(2, player3Point);
		}
		if(player2Label.getX() == player4Label.getX() && player2Label.getY() == player4Label.getY()) { // player1 player4
			if(player2Point > player4Point) { // player1이 코인을 더 많이 가지고 있을 경우
				player2Point -= 10;
				player4Point += 10;
			} else if(player2Point < player4Point) { // playe4가 코인을 더 많이 가지고 있을 경우
				player2Point += 10;
				player4Point -= 10;
			}/* else {
				player4Point = player4Point;
				player2Point = player2Point;
			}*/
			GameClientView.sendPoint(1, player2Point);
			GameClientView.sendPoint(3, player4Point);
		}
		
		// player3이 다른 플레이어와 만날 때
		if(player3Label.getX() == player4Label.getX() && player3Label.getY() == player4Label.getY()) { // player1 player4
			if(player3Point > player4Point) { // player1이 코인을 더 많이 가지고 있을 경우
				player3Point -= 10;
				player4Point += 10;
			} else if(player3Point < player4Point) { // playe4가 코인을 더 많이 가지고 있을 경우
				player3Point += 10;
				player4Point -= 10;
			}/* else {
				player3Point = player3Point;
				player4Point = player4Point;
			}*/
			GameClientView.sendPoint(2, player3Point);
			GameClientView.sendPoint(3, player4Point);
		}
	}
	
	// 화면에서 player들의 움직임을 동기화
	public void OtherCharacterMove(int userNum, int x, int y) {
		if(userNum == 0) {
			player1Label.setLocation(x, y);
		}
		else if(userNum == 1) {
			player2Label.setLocation(x, y);
		}
		else if(userNum == 2) {
			player3Label.setLocation(x, y);
		}
		else if(userNum == 3) {
			player4Label.setLocation(x, y);
		}
	}

	class MoveUDEnemy extends Thread { // 좌우로 움직이는 적
		private int i;
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			udEnemy1.setSize(30, 30);
			udEnemy1.setLocation(95, 209);// 적 1 (5, 11) (x, y)
			add(udEnemy1);
			
			udEnemy2.setSize(30, 30);
			udEnemy2.setLocation(285, 228); // 적 2 (15, 12) (x, y)
			add(udEnemy2);
			
			while(true) {
				for(i=0; i<=4; i++) {
					try {
						touchEnemy();
						udEnemy1.setLocation(5*19, (11+i)*19);
						udEnemy2.setLocation(15*19, (12+i)*19);
						enemyUDThread.sleep(100);

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				for(i=3; i>=1; i--) {
					try {
						touchEnemy();
						udEnemy1.setLocation(5*19, (11+i)*19);
						udEnemy2.setLocation(15*19, (12+i)*19);
						enemyUDThread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	class MoveLREnemy extends Thread { // 좌우로 움직이는 적	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			lrEnemy1.setSize(30, 30);
			lrEnemy1.setLocation(19, 57);// 적 1 (1, 3) (x, y)
			add(lrEnemy1);
			
			lrEnemy2.setSize(30, 30);
			lrEnemy2.setLocation(228, 57); // 적 2 (12, 3) (x, y)
			add(lrEnemy2);
			
			while(true) {
				for(int i=0; i<=5; i++) {
					try {
						touchEnemy();
						lrEnemy1.setLocation((1+i)*19, 3*19);
						lrEnemy2.setLocation((12+i)*19, 3*19);
						enemyLRThread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				for(int i=4; i>=1; i--) {
					try {
						touchEnemy();
						lrEnemy1.setLocation((1+i)*19, 3*19);
						lrEnemy2.setLocation((12+i)*19, 3*19);
						enemyLRThread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void createCoin() { // 생성
		int coinCnt = 0; // 코인 갯수
	    int yLength = 0;
	    int xLength = 0;
	    int randx, randy;
	      
	    for(int y=0; y<map1.length; y++) {
	       for(int x=0; x<map1[y].length; x++) {
	        	xLength = map1[y].length;
	        	yLength = map1.length;
	       }
	    }
	      
		// 처음에는 randx randy 무조건 넣어주기
	    rx[0] = (int)(Math.random()*(xLength - 1) + 1);
	    ry[0] = (int)(Math.random()*(yLength - 1) + 1);
	      
	    for(int i=0; ranNumCnt<60; i++) { //  ranNumCnt가 60이 될 때까지 랜덤한 값 생성해서 배열에 넣어줌.
	    	randx = (int)(Math.random()*(xLength - 1) + 1); // 랜덤한 x값 생성해서 randx에 넣기
	    	randy = (int)(Math.random()*(yLength - 1) + 1); // 랜덤한 y값 생선해서 randy에 넣기
        	  
	    	if(map1[randy][randx] == 5) {
	          	if(!(randx == rx[ranNumCnt] && randy == ry[ranNumCnt])) { // randx randy가 둘 다 rx, ry에 있는게 아니라면 저장
	          		rx[ranNumCnt] = randx;
	          		ry[ranNumCnt] = randy;
	          		GameClientView.sendCoinCreate(rx[ranNumCnt]*19, ry[ranNumCnt]*19);
	          		ranNumCnt++;
	          	} else { 
	          		continue;
	          	}
	    	}
	    }
	}
	
	public void drawCoin(int x, int y) {
		if(map1[y/19][x/19] == 5) { // map1에서 ry rx위치가 5이면
			coinLabel = new JLabel(coinrIcon);
		  	coins.add(coinLabel);
		  	add(coins.get(iCoin));
		  	coins.get(iCoin).setSize(30, 30);
		    coins.get(iCoin).setLocation(x, y);
		}
		iCoin++;
	}
	
	public void DrawMap() { // 움직이지 않는 것 그리기
		int hDrawNum = 0;
		
		for(int y=0; y<map1.length; y++) {
			for(int x=0; x<map1[y].length; x++) {
				switch(map1[y][x]) {
				case 4: // 4이면 벽 그리기
					wallLabel = new JLabel(wrIcon);
					add(wallLabel);
					wallLabel.setSize(30, 30);
					wallLabel.setLocation(x*19, y*19); 
					break;
				case 9: // 9이면 하트아이템 그리기
					heartLabel[addHeartCnt] = new JLabel(hrIcon);
					add(heartLabel[addHeartCnt]);
					heartLabel[addHeartCnt].setSize(30, 30);
					heartLabel[addHeartCnt].setLocation(x*19, y*19);

					addHeartCnt++;
					break;
				}
			}
		}
	}
	
	public void touchEnemy() { // 적과 닿았을 경우
		// player1이 적들과 위치가 같을 때
		if((player1Label.getY() == lrEnemy1.getY() && player1Label.getX() == lrEnemy1.getX())
				 || (player1Label.getY() == lrEnemy2.getY() && player1Label.getX() == lrEnemy2.getX())
				 || (player1Label.getY() == udEnemy1.getY() && player1Label.getX() == udEnemy1.getX())
				 || (player1Label.getY() == udEnemy2.getY() && player1Label.getX() == udEnemy2.getX())) {
			
			if(p1Heart > 0) { // 하트가 0보다 크면
				p1Heart--; // 목숨 하나 잃음.
				GameClientView.sendRemoveHeart(0, p1Heart);
				GameClientView.sendLocation(0, 19, 19); // 초기위치로 돌아감
			}
			else if(p1Heart == 0) { // 하트가 0보다 작으면
				live[0] = false;
			}
		}
		
		if((player2Label.getY() == lrEnemy1.getY() && player2Label.getX() == lrEnemy1.getX())
				 || (player2Label.getY() == lrEnemy2.getY() && player2Label.getX() == lrEnemy2.getX())
				 || (player2Label.getY() == udEnemy1.getY() && player2Label.getX() == udEnemy1.getX())
				 || (player2Label.getY() == udEnemy2.getY() && player2Label.getX() == udEnemy2.getX())) {
			
			if(p2Heart > 0) { // 하트가 0보다 크면
				p2Heart--; // 목숨 하나 잃음.
				GameClientView.sendRemoveHeart(1, p2Heart);
				GameClientView.sendLocation(1, 19, 380); // 초기위치로 돌아감
			}
			else if(p2Heart == 0) { // 하트가 0보다 작으면
				live[1] = false;
			}
		}
		
		if((player3Label.getY() == lrEnemy1.getY() && player3Label.getX() == lrEnemy1.getX())
				 || (player3Label.getY() == lrEnemy2.getY() && player3Label.getX() == lrEnemy2.getX())
				 || (player3Label.getY() == udEnemy1.getY() && player3Label.getX() == udEnemy1.getX())
				 || (player3Label.getY() == udEnemy2.getY() && player3Label.getX() == udEnemy2.getX())) {
			
			if(p3Heart > 0) { // 하트가 0보다 크면
				p3Heart--; // 목숨 하나 잃음.
				GameClientView.sendRemoveHeart(2, p3Heart);
				GameClientView.sendLocation(2, 380, 19); // 초기위치로 돌아감
			}
			else if(p3Heart == 0) { // 하트가 0보다 작으면
				live[2] = false;
			}
		}
		
		if((player4Label.getY() == lrEnemy1.getY() && player4Label.getX() == lrEnemy1.getX())
				 || (player4Label.getY() == lrEnemy2.getY() && player4Label.getX() == lrEnemy2.getX())
				 || (player4Label.getY() == udEnemy1.getY() && player4Label.getX() == udEnemy1.getX())
				 || (player4Label.getY() == udEnemy2.getY() && player4Label.getX() == udEnemy2.getX())) {		
			
			if(p4Heart > 0) { // 하트가 0보다 크면
				p4Heart--; // 목숨 하나 잃음.
				GameClientView.sendRemoveHeart(3, p4Heart);
				GameClientView.sendLocation(3, 380, 380); // 초기위치로 돌아감
			}
			else if(p4Heart == 0) { // 하트가 0보다 작으면
				live[3] = false;
			}
		}
	}
	
	public void removeCoin(int userNum, int removeCoinNum, int x, int y) { // 코인 지우기
		coins.get(removeCoinNum).setSize(0, 0);
		coins.get(removeCoinNum).setBounds(0, 0, 0, 0);
		coins.get(removeCoinNum).setVisible(false);
		repaint();
		revalidate();
	}
	
	public void removeHeart(int userNum, int removeHeartNum, int x, int y) { // 하트지우기
		System.out.println("heart remove");
		
		heartLabel[removeHeartNum].setSize(0, 0);
		heartLabel[removeHeartNum].setBounds(0, 0, 0, 0);
		heartLabel[removeHeartNum].setVisible(false);
		repaint();
		revalidate();
	}
	
	public void touchHeart() { // 하트(아이템)를 먹었을 때
		for(int i=0; i<2; i++) {
			
			if((player1Label.getY() == heartLabel[i].getY()) && (player1Label.getX() == heartLabel[i].getX())) {
				if(p1Heart < 3) {
					System.out.println("heart eat");
					System.out.println("heart location: " + heartLabel[i].getX() + " " + heartLabel[i].getY());
					removeHeartNumber = i;
					heartUserNum = 0;
					++p1Heart;
					GameInfoPanel.setAddHeart(0, p1Heart);
					GameClientView.sendAddHeart(heartUserNum, removeHeartNumber, heartLabel[i].getX(), heartLabel[i].getY());
				}
				break;
			}
		}
		
		for(int i=0; i<2; i++) {
			System.out.println(player2Label.getX() + " " + player2Label.getX() + " " + heartLabel[i].getX() + " " + heartLabel[i].getY());
			if(player2Label.getY() == heartLabel[i].getY() && player2Label.getX() == heartLabel[i].getX()) {
				System.out.println("heart eat1");
				if(p2Heart < 3) {
					System.out.println("heart eat2");
					System.out.println("heart location: " + heartLabel[i].getX() + " " + heartLabel[i].getY());
					removeHeartNumber = i;
					heartUserNum = 1;
					++p2Heart;
					GameInfoPanel.setAddHeart(1, p2Heart);
					GameClientView.sendAddHeart(heartUserNum, removeHeartNumber, heartLabel[i].getX(), heartLabel[i].getY());
				}
				break;
			}
		}
		
		for(int i=0; i<2; i++) {
			if(player3Label.getY() == heartLabel[i].getY() && player3Label.getX() == heartLabel[i].getX()) {
				if(p3Heart < 3) {
					System.out.println("heart eat");
					System.out.println("heart location: " + heartLabel[i].getX() + " " + heartLabel[i].getY());
					removeHeartNumber = i;
					heartUserNum = 2;
					++p3Heart;
					GameInfoPanel.setAddHeart(2, p3Heart);
					GameClientView.sendAddHeart(heartUserNum, removeHeartNumber, heartLabel[i].getX(), heartLabel[i].getY());
				}
				break;
			}
		}
		
		for(int i=0; i<2; i++) {
			if(player4Label.getY() == heartLabel[i].getY() && player4Label.getX() == heartLabel[i].getX()) {
				if(p4Heart < 3) {
					System.out.println("heart eat");
					System.out.println("heart location: " + heartLabel[i].getX() + " " + heartLabel[i].getY());
					removeHeartNumber = i;
					heartUserNum = 3;
					++p4Heart;
					GameInfoPanel.setAddHeart(3, p4Heart);
					GameClientView.sendAddHeart(heartUserNum, removeHeartNumber, heartLabel[i].getX(), heartLabel[i].getY());
				}
				break;
			}
		}
		
	}
	
	public void touchCoin() { // 코인과 닿았을 때
		for(int i=0; i<coins.size(); i++) {
			if((player1Label.getY() == coins.get(i).getY() && player1Label.getX() == coins.get(i).getX())) {
				eatUserNum = 0;
				removeCoinNumber = i;
				++player1Point;
				repaint();
				revalidate();
				GameClientView.sendPoint(0, player1Point);
				GameClientView.sendCoinEat(eatUserNum, removeCoinNumber, coins.get(removeCoinNumber).getX(), coins.get(removeCoinNumber).getY());
				
				System.out.println(player1Label.getX() + " " + player1Label.getY() + "\n" + coins.get(i).getX() + " " + coins.get(i).getY() + " ");
				break;
			}
		}
		
		for(int i=0; i<coins.size(); i++) {
			if((player2Label.getY() == coins.get(i).getY() && player2Label.getX() == coins.get(i).getX())) {
				eatUserNum = 1;
				removeCoinNumber = i;
				++player2Point;
				repaint();
				revalidate();
				GameClientView.sendPoint(1, player2Point);
				GameClientView.sendCoinEat(eatUserNum, removeCoinNumber, coins.get(removeCoinNumber).getX(), coins.get(removeCoinNumber).getY());
				
				System.out.println(player2Label.getX() + " " + player2Label.getY() + "\n" + coins.get(i).getX() + " " + coins.get(i).getY() + " ");
				break;
			}
		}
		
		for(int i=0; i<coins.size(); i++) {
			if((player3Label.getY() == coins.get(i).getY() && player3Label.getX() == coins.get(i).getX())) {
				eatUserNum = 2;
				removeCoinNumber = i;
				++player3Point;
				repaint();
				revalidate();
				GameClientView.sendPoint(2, player3Point);
				GameClientView.sendCoinEat(eatUserNum, removeCoinNumber, coins.get(removeCoinNumber).getX(), coins.get(removeCoinNumber).getY());
				
				System.out.println(player3Label.getX() + " " + player3Label.getY() + "\n" + coins.get(i).getX() + " " + coins.get(i).getY() + " ");
				break;
			}
		}
		
		for(int i=0; i<coins.size(); i++) {
			if((player4Label.getY() == coins.get(i).getY() && player4Label.getX() == coins.get(i).getX())) {
				eatUserNum = 3;
				removeCoinNumber = i;
				++player4Point;
				repaint();
				revalidate();
				GameClientView.sendPoint(3, player4Point);
				GameClientView.sendCoinEat(eatUserNum, removeCoinNumber, coins.get(removeCoinNumber).getX(), coins.get(removeCoinNumber).getY());
				
				System.out.println(player4Label.getX() + " " + player4Label.getY() + "\n" + coins.get(i).getX() + " " + coins.get(i).getY() + " ");
				break;
			}
		}
	}
	
	class Player1 {
		private int x;
		private int y;
		
		public Player1() {
			player1Label.setSize(30, 30);
			player1Label.setLocation(19, 19); // (1, 1)
			add(player1Label);
		}
		
		public void draw() {
			DrawMap();
			
			if(takeUserNum == 0) {
				createCoin();
				repaint();
				revalidate();
			}
		}
		
		public void move(int direction) {
			switch(direction) {
			case GameClientPlay.LEFT:
				if(map1[(player1Label.getY()/19)][player1Label.getX()/19-1] == 4) {
					player1Label.setLocation(player1Label.getX(), player1Label.getY());
				}
				else {
					GameClientView.sendLocation(0, player1Label.getX()-19, player1Label.getY()); // player1의 위치 GameClientView에 전달
					touchEnemy();
					touchCoin();
					touchHeart();
					touchOtherPlayer();
				}
				break;
			case GameClientPlay.RIGHT:
				if(map1[(player1Label.getY()/19)][player1Label.getX()/19+1] == 4) {
					player1Label.setLocation(player1Label.getX(), player1Label.getY());
				}
				else {
					GameClientView.sendLocation(0, player1Label.getX()+19, player1Label.getY()); // player1의 위치 GameClientView에 전달
					touchEnemy();
					touchCoin();
					touchHeart();
					touchOtherPlayer();
				}
				break;
			case GameClientPlay.UP:
				if(map1[player1Label.getY()/19-1][player1Label.getX()/19] == 4) {
					player1Label.setLocation(player1Label.getX(), player1Label.getY());
				}
				else {
					GameClientView.sendLocation(0, player1Label.getX(), player1Label.getY()-19); // player1의 위치 GameClientView에 전달
					touchEnemy();
					touchCoin();
					touchHeart();
					touchOtherPlayer();
				}
				break;
			case GameClientPlay.DOWN:
				if(map1[player1Label.getY()/19+1][player1Label.getX()/19] == 4) {
					player1Label.setLocation(player1Label.getX(), player1Label.getY());
				}
				else {
					GameClientView.sendLocation(0, player1Label.getX(), player1Label.getY()+19); // player1의 위치 GameClientView에 전달
					touchEnemy();
					touchCoin();
					touchHeart();
					touchOtherPlayer();
				}
				break;
			}
		}
	}
	
	class Player2 {
		public Player2() {
			// player2
			add(player2Label);
			player2Label.setSize(30, 30);
			player2Label.setLocation(19, 380); // (1, 20)
		}
		
		public void move(int direction) {
			switch(direction) {
			case GameClientPlay.LEFT:
				if(map1[player2Label.getY()/19][player2Label.getX()/19-1] == 4) {
					player2Label.setLocation(player2Label.getX(), player2Label.getY());
				}
				else {
					GameClientView.sendLocation(1, player2Label.getX()-19, player2Label.getY()); // player2의 위치 GameClientView에 전달
					touchEnemy();
					touchCoin();
					touchHeart();
					touchOtherPlayer();
				}
				break;
			case GameClientPlay.RIGHT:
				if(map1[player2Label.getY()/19][player2Label.getX()/19+1] == 4) {
					player2Label.setLocation(player2Label.getX(), player2Label.getY());
				}
				 else {
					GameClientView.sendLocation(1, player2Label.getX()+19, player2Label.getY()); // player2의 위치 GameClientView에 전달
					touchEnemy();
					touchCoin();
					touchHeart();
					touchOtherPlayer();
				}
				break;
			case GameClientPlay.UP:
				if(map1[player2Label.getY()/19-1][player2Label.getX()/19] == 4) {
					player2Label.setLocation(player2Label.getX(), player2Label.getY());
				}
				else {
					GameClientView.sendLocation(1, player2Label.getX(), player2Label.getY()-19); // player2의 위치 GameClientView에 전달
					touchEnemy();
					touchCoin();
					touchHeart();
					touchOtherPlayer();
				}
				break;
			case GameClientPlay.DOWN:
				if(map1[player2Label.getY()/19+1][player2Label.getX()/19] == 4) {
					player2Label.setLocation(player2Label.getX(), player2Label.getY());
				}
				else {
					GameClientView.sendLocation(1, player2Label.getX(), player2Label.getY()+19); // player2의 위치 GameClientView에 전달
					touchEnemy();
					touchCoin();
					touchHeart();
					touchOtherPlayer();
				}
				break;
			}
		}
	}
	
	class Player3 {
		public Player3() {
			// player3
			add(player3Label);
			player3Label.setSize(30, 30);
			player3Label.setLocation(380, 19); // (20, 1)
		}
		
		public void move(int direction) {
			switch(direction) {
			case GameClientPlay.LEFT:
				if(map1[player3Label.getY()/19][player3Label.getX()/19-1] == 4) {
					player3Label.setLocation(player3Label.getX(), player3Label.getY());
				}
				else {
					GameClientView.sendLocation(2, player3Label.getX()-19, player3Label.getY()); // player3의 위치 GameClientView에 전달
					touchEnemy();
					touchCoin();
					touchHeart();
					touchOtherPlayer();
				}
				break;
			case GameClientPlay.RIGHT:
				if(map1[player3Label.getY()/19][player3Label.getX()/19+1] == 4) {
					player3Label.setLocation(player3Label.getX(), player3Label.getY());
				}
				 else {
					GameClientView.sendLocation(2, player3Label.getX()+19, player3Label.getY()); // player3의 위치 GameClientView에 전달
					touchEnemy();
					touchCoin();
					touchHeart();
					touchOtherPlayer();
				}
				break;
			case GameClientPlay.UP:
				if(map1[player3Label.getY()/19-1][player3Label.getX()/19] == 4) {
					player3Label.setLocation(player3Label.getX(), player3Label.getY());
				}
				else {
					GameClientView.sendLocation(2, player3Label.getX(), player3Label.getY()-19); // player3의 위치 GameClientView에 전달
					touchEnemy();
					touchCoin();
					touchHeart();
					touchOtherPlayer();
				}
				break;
			case GameClientPlay.DOWN:
				if(map1[player3Label.getY()/19+1][player3Label.getX()/19] == 4) {
					player3Label.setLocation(player3Label.getX(), player3Label.getY());
				}
				else {
					GameClientView.sendLocation(2, player3Label.getX(), player3Label.getY()+19); // player3의 위치 GameClientView에 전달
					touchEnemy();
					touchCoin();
					touchHeart();
					touchOtherPlayer();
				}
				break;
			}
		}
	}
	
	class Player4 {
		public Player4() {
			// player4
			add(player4Label);
			player4Label.setSize(30, 30);
			player4Label.setLocation(380, 380); // (20, 20)
		}
		
		public void move(int direction) {
			switch(direction) {
			case GameClientPlay.LEFT:
				if(map1[player4Label.getY()/19][player4Label.getX()/19-1] == 4) {
					player4Label.setLocation(player4Label.getX(), player4Label.getY());
				}
				else {
					GameClientView.sendLocation(3, player4Label.getX()-19, player4Label.getY()); // player4의 위치 GameClientView에 전달
					touchEnemy();
					touchCoin();
					touchHeart();
					touchOtherPlayer();
				}
				break;
			case GameClientPlay.RIGHT:
				if(map1[player4Label.getY()/19][player4Label.getX()/19+1] == 4) {
					player4Label.setLocation(player4Label.getX(), player4Label.getY());
				}
				else {
					GameClientView.sendLocation(3, player4Label.getX()+19, player4Label.getY()); // player4의 위치 GameClientView에 전달
					touchEnemy();
					touchCoin();
					touchHeart();
					touchOtherPlayer();
				}
				break;
			case GameClientPlay.UP:
				if(map1[player4Label.getY()/19-1][player4Label.getX()/19] == 4) {
					player4Label.setLocation(player4Label.getX(), player4Label.getY());
				}
				else {
					GameClientView.sendLocation(3, player4Label.getX(), player4Label.getY()-19); // player4의 위치 GameClientView에 전달
					touchEnemy();
					touchCoin();
					touchHeart();
					touchOtherPlayer();
				}
				break;
			case GameClientPlay.DOWN:
				if(map1[player4Label.getY()/19+1][player4Label.getX()/19] == 4) {
					player4Label.setLocation(player4Label.getX(), player4Label.getY());
				}
				else {
					GameClientView.sendLocation(3, player4Label.getX(), player4Label.getY()+19); // player4의 위치 GameClientView에 전달
					touchEnemy();
					touchCoin();
					touchHeart();
					touchOtherPlayer();
				}
				break;
			}
		}
	}
}
