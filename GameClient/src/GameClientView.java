import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class GameClientView extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public static String UserName;
	public static int UserNum;
	
	private Thread moveThread;
	
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;

	private ObjectInputStream ois;
	private static ObjectOutputStream oos;
	
	private GameInfoPanel.Timer timer;
	
	private int nUserNum;
	private String nUserName;
	private int newTime;
	private int pointUserNum;
	private int addCoin;
	private int takeUserNum;
	private int eatUserNum;
	private int takeX;
	private int takeY;
	private int createCoinX;
	private int createCoinY;
	private int removeX;
	private int removeY;
	private int removeCoinsOne; // coins중 지워지는 코인 하나의 번호
	private int removeHeartsOne;
	private int heartRemoveX;
	private int heartRemoveY;
	private int heartUserNum;
	private int lostHeart;
	private int addHeart;
	private int timeUserNum;
	
	public static int[] playersNum = new int[4];
	public static String[] playersName = new String[4];
	
	private GameClientPlay playPanel;
	private GameWaitPanel waitPanel;
	public static GameInfoPanel infoPanel;
	   
	public static JLabel me = new JLabel(new ImageIcon("images/me.png"));
	public static JLabel other = new JLabel(new ImageIcon("images/other.png"));
	
	private static String locationMsg;
	private static String coinCreateMsg;
	private static String coinEatMsg;
	private static String pointMsg;
	private static String timeMsg;
	private static String nameMsg;
	private static String lostHeartMsg;
	private static String addHeartMsg;
	private static String gameoverMsg;

	public GameClientView(String username, String ip_addr, String port_no)  {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Survival Maze");
		setBounds(100, 100, 750, 470);
		
		waitPanel = new GameWaitPanel();
		waitPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		getContentPane().add(waitPanel); // homePanel 부착
		waitPanel.setLayout(null);
		
		UserName = username;
		
		setVisible(true);
		
		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			// SendMessage("/login " + UserName);
			GameMsg obcm = new GameMsg(UserName, "100", "Hello");
			SendObject(obcm);

			ListenNetwork net = new ListenNetwork();
			net.start();
			
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("connect error");
		}
	}
	
	private void SplitPane() { // 화면 나누기
		JSplitPane hPane = new JSplitPane();
		add(hPane, BorderLayout.CENTER);
		hPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		hPane.setDividerLocation(440);
		hPane.setEnabled(false);
		hPane.setDividerSize(0);
		hPane.setLeftComponent(playPanel);
		hPane.setRightComponent(infoPanel); // hPane오른쪽에 pPane 넣기
	}
	
	class ListenNetwork extends Thread {
		public void run() {
			while(true) {
				try {
					Object obcm = null;
					String msg = null;
					GameMsg gm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof GameMsg) {
						gm = (GameMsg) obcm;
						msg = String.format("[%s]\n%s", gm.UserName, gm.data);
					} else
						continue;
					switch (gm.code) {
					case "101": // 101 : 게임 시작 여부(로딩에서 게임시작화면으로 넘어갈지 말지) // 인원이 4명이 되면
						// map1으로 화면전환되면서 게임시작	
						playPanel = new GameClientPlay(UserName, takeUserNum);
						infoPanel = new GameInfoPanel(UserName, takeUserNum);
						playPanel.setVisible(true);
						SplitPane();
						waitPanel.setVisible(false);
						playPanel.setFocusable(true);
						playPanel.requestFocus();
						break;
					case "601":
		                  msg = String.format(gm.code + " " + gm.data);
		                  System.out.println(msg);
		                  String[] args0 = msg.split(" "); // 단어들을 분리
		                  takeUserNum = Integer.parseInt(args0[1]); // userNum 저장
		                  infoPanel.setUserName(takeUserNum, UserName);
		                  break;
					case "201": // server로 부터 i번 user의 num과 x, y위치 받기
						msg = String.format(gm.code + " " + gm.data); // 201(코드)과 userNum과 x y 위치
						String[] args = msg.split(" "); // 단어들을 분리
						takeUserNum = Integer.parseInt(args[1]); // userNum 저장
						takeX = Integer.parseInt(args[2]); // user의 x 저장
						takeY = Integer.parseInt(args[3]); // user의 y 저장
						playPanel.OtherCharacterMove(takeUserNum, takeX, takeY);
						break;
					case "203":
						msg = String.format(gm.code + " " + gm.data); // 203(코드)과 x y 위치
						String[] args2 = msg.split(" "); // 단어들을 분리
						createCoinX = Integer.parseInt(args2[1]); // user의 x 저장
						createCoinY = Integer.parseInt(args2[2]); // user의 y 저장
						playPanel.drawCoin(createCoinX, createCoinY);
						break;
					case "205":
						msg = String.format(gm.code + " " + gm.data); // 205(코드)과 userNum 그리고 x y 위치
						String[] args3 = msg.split(" "); // 단어들을 분리
						eatUserNum = Integer.parseInt(args3[1]); // userNum 저장
						removeCoinsOne = Integer.parseInt(args3[2]); // coin의 번호
						removeX = Integer.parseInt(args3[3]); // x 저장
						removeY = Integer.parseInt(args3[4]); // y 저장
						playPanel.removeCoin(eatUserNum, removeCoinsOne, removeX, removeY);
						//System.out.println("removeCoin : " + eatUserNum + " " + removeCoinsOne + " " + removeX + " " + removeY);
						break;
					case "301": // 코인얻기
						msg = String.format(gm.code + " " + gm.data); // 205(코드)과 userNum 그리고 x y 위치
						String[] args4 = msg.split(" "); // 단어들을 분리
						pointUserNum = Integer.parseInt(args4[1]); // userNum 저장
						addCoin = Integer.parseInt(args4[2]); // player가 얻은 코인의 수 
 						infoPanel.setPoint(pointUserNum, addCoin);
						break;
					case "500":
						msg = String.format(gm.code + " " + gm.data);
						String[] args5 = msg.split(" ");
						heartUserNum = Integer.parseInt(args5[1]);
						removeHeartsOne = Integer.parseInt(args5[2]); // 배열에 몇 번째에 있는 하트인지
						heartRemoveX = Integer.parseInt(args5[3]);
						heartRemoveY = Integer.parseInt(args5[4]);

						playPanel.removeHeart(heartUserNum, removeHeartsOne, heartRemoveX, heartRemoveY);
						break;
					case "501":
						msg = String.format(gm.code + " " + gm.data);
						String[] args6 = msg.split(" ");
						heartUserNum = Integer.parseInt(args6[1]);
						lostHeart = Integer.parseInt(args6[2]);
						if(lostHeart >= 0)
							infoPanel.setRemoveHeart(heartUserNum, lostHeart);
						break;
					case "701":
						msg = String.format(gm.code + " " + gm.data);
						String[] args7 = msg.split(" ");
						//System.out.println(msg);
						timeUserNum = Integer.parseInt(args7[1]);
						newTime = Integer.parseInt(args7[2]);
						infoPanel.setNewTime(timeUserNum, newTime);
					case "104":
						msg = String.format(gm.code + " " + gm.data);
						
						if(infoPanel.gameover) {
							infoPanel.winner();
							waitPanel.setVisible(true);
							playPanel.setVisible(false);
						}
						break;
					case "602":
						msg = String.format(gm.code + " " + gm.data);
						String[] args8 = msg.split(" ");
						System.out.println(msg);
						nUserNum = Integer.parseInt(args8[1]);
						nUserName = args8[2];
						infoPanel.setUserName(nUserNum, nUserName);
						break;
					}
						
				} catch (IOException e) {
					System.out.println("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						socket.close();

						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝

			}
		}
	}
	
	public static void sendCoinEat(int UserNum, int removeCoinNum, int x, int y) {
		coinEatMsg = UserNum + " " + removeCoinNum + " " + x + " " + y;
		GameMsg obcmsend = new GameMsg(UserName, "204", coinEatMsg); // 204  UserNum과 지워지는 코인의 번호(coins에서의 번호), 코인위치
		SendObject(obcmsend);
	}
	
	public static void sendCoinCreate(int x, int y) {
		coinCreateMsg = x + " " + y;
		GameMsg obcmsend = new GameMsg(UserName, "202", coinCreateMsg); // 202 코인위치
		SendObject(obcmsend);
	}
	
	public static void sendLocation(int userNum, int x, int y) { // server에게 player의 위치보내기
		locationMsg = userNum + " " + x + " " + y;
		GameMsg obcmsend = new GameMsg(UserName, "200", locationMsg);
		SendObject(obcmsend);
	}
	
	public static void sendPoint(int userNum, int coin) {
		pointMsg = userNum + " " + coin;
		GameMsg obcmsend = new GameMsg(UserName, "300", pointMsg);
		SendObject(obcmsend);
	}
	
	public static void sendAddHeart(int userNum, int heart, int x, int y) {
		addHeartMsg = userNum + " " + heart + " " + x + " " + y;
		GameMsg obcmsend = new GameMsg(UserName, "500", addHeartMsg);
		SendObject(obcmsend);
	}
	
	public static void sendRemoveHeart(int userNum, int heart) {
		lostHeartMsg = userNum + " " + heart;
		GameMsg obcmsend = new GameMsg(UserName, "501", lostHeartMsg);
		SendObject(obcmsend);
	}
	
	public static void sendTime(int userNum, int time) {
		if(userNum == 0) {
			timeMsg = userNum + " " + time;
			GameMsg obcmsend = new GameMsg(UserName, "700", timeMsg);
			SendObject(obcmsend);
		}
	}
	
	public static void sendName(int userNum, String name) {
		nameMsg = userNum + " " + name;
		GameMsg obcmsend = new GameMsg(UserName, "602", nameMsg);
		SendObject(obcmsend);
	}
	
	public static void sendGameOver() {
		if(infoPanel.gameover) {
			gameoverMsg = "gameover";
			GameMsg obcmsend = new GameMsg(UserName, "103", gameoverMsg);
			SendObject(obcmsend);
		}
	}
	
	public static void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("메세지 송신 에러!!\n");
			System.out.println("SendObject Error");
		}
	}
}
