import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class GameServer extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTextArea textArea;
	private JTextField txtPortNumber;
	
	private ServerSocket socket;
	private Socket client_socket;
	private Vector UserVec = new Vector();
	private static final int BUF_LEN = 128;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameServer frame = new GameServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public GameServer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 338, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 300, 298);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(13, 318, 87, 26);
		contentPane.add(lblNewLabel);

		txtPortNumber = new JTextField();
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setText("30000");
		txtPortNumber.setBounds(112, 318, 199, 26);
		contentPane.add(txtPortNumber);
		txtPortNumber.setColumns(10);

		JButton btnServerStart = new JButton("Server Start");
		btnServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
				} catch (NumberFormatException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AppendText("Chat Server Running..");
				btnServerStart.setText("Chat Server Running..");
				btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
				txtPortNumber.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}
	
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
					AppendText("새로운 참가자 from " + client_socket);
					// User 당 하나씩 Thread 생성
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // 새로운 참가자 배열에 추가
					new_user.start(); // 만든 객체의 스레드 실행
					AppendText("현재 참가자 수 " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}
	
	public void AppendText(String str) {
		// textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}
	
	public void AppendObject(GameMsg msg) {
		// textArea.append("사용자로부터 들어온 object : " + str+"\n");
		textArea.append("code = " + msg.code + "\n");
		textArea.append("id = " + msg.UserName + "\n");
		textArea.append("data = " + msg.data + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}
	
	class UserService extends Thread {
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;

		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector user_vc;
		public String UserName = "";
		public String UserStatus;
		public int UserNum;
		public String UserNInfo;
		
		public String[] playersName = new String[4];
		public boolean num0 = false;
		public boolean num1 = false;
		public boolean num2 = false;
		public boolean num3 = false;

		public UserService(Socket client_socket) {
			// TODO Auto-generated constructor stub
			// 매개변수로 넘어온 자료 저장
			this.client_socket = client_socket;
			this.user_vc = UserVec;
			try {
//				is = client_socket.getInputStream();
//				dis = new DataInputStream(is);
//				os = client_socket.getOutputStream();
//				dos = new DataOutputStream(os);

				oos = new ObjectOutputStream(client_socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(client_socket.getInputStream());

				// line1 = dis.readUTF();
				// /login user1 ==> msg[0] msg[1]
//				byte[] b = new byte[BUF_LEN];
//				dis.read(b);		
//				String line1 = new String(b);
//
//				//String[] msg = line1.split(" ");
//				//UserName = msg[1].trim();
//				UserStatus = "O"; // Online 상태
//				Login();
				checkNumOfUser();
			} catch (Exception e) {
				AppendText("userService error");
			}
		}
		
		public void Login() {
			AppendText("새로운 참가자 " + UserName + "(" + UserNum + ") 입장.");
			WriteOne("Welcome to Java chat server\n");
			WriteOne(UserName + "님 환영합니다.\n"); // 연결된 사용자에게 정상접속을 알림
			String msg = "[" + UserName + "]님이 입장 하였습니다.\n";
			if(UserNum == 0) {
				playersName[0] = UserName;
			}
			else if(UserNum == 1) {
				playersName[1] = UserName;
			}
			else if(UserNum == 2) {
				playersName[2] = UserName;
			}
			else if(UserNum == 3) {
				playersName[3] = UserName;
			}
			WriteOthers(msg); // 아직 user_vc에 새로 입장한 user는 포함되지 않았다.
		}

		public void Logout() {
			String msg = "[" + UserName + "(" + UserNum + ")" + "]님이 퇴장 하였습니다.\n";
			UserVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
			WriteAll(msg); // 나를 제외한 다른 User들에게 전송
			AppendText("사용자 " + "[" + UserName + "] 퇴장. 현재 참가자 수 " + UserVec.size());
		}
		
		// 모든 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void WriteAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				//if (user.UserStatus == "O")
					user.WriteOne(str);
			}
		}
		// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public void WriteAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				//if (user.UserStatus == "O")
					user.WriteOneObject(ob);
			}
		}
		
		// User들에게 나의 위치 방송.
		public void LocationWriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				/*if (user != this && user.UserStatus == "O")*/
					user.LocationWriteOne(str);
			}
		}
		
		public void FinishWriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				/*if (user != this && user.UserStatus == "O")*/
					user.FinishWriteOne(str);
			}
		}
		
		// 그려줄 코인 위치 방송
		public void LocationCoinWriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				/*if (user != this && user.UserStatus == "O")*/
				user.createCoinWriteOne(str);
			}
		}
		
		public void CoinEatWriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.CoinEatWriteOne(str);
			}
		}
		
		public void SetPointWriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.SetPointWriteOne(str);
			}
		}
		
		public void AddHeartWriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.AddHeartWriteOne(str);
			}
		}
		
		public void SetHeartWriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.SetHeartWriteOne(str);
			}
		}
		
		public void SetTimeWriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.SetTimeWriteOne(str);
			}
		}
		
		public void nameWriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.nameWriteOne(str);
			}
		}
		
		// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
				public void WriteOthers(String str) {
					for (int i = 0; i < user_vc.size(); i++) {
						UserService user = (UserService) user_vc.elementAt(i);
						/*if (user != this && user.UserStatus == "O")*/
							user.WriteOne(str);
					}
				}
		
		/*public void makeCharacter() {
			for(int i=0; i<UserVec.size(); i++) {
				System.out.println(UserVec.size());
				UserService user = (UserService) user_vc.elementAt(i);
				
				switch(i%4) {
				case 0:
					UserNum = 0;
					UserNInfo = UserNum + " " + UserName;
					user.whoAmI(UserNInfo);
					System.out.println("userInfo1 " + UserNInfo);
					break;
				case 1:
					UserNum = 1;
					UserNInfo = UserNum + " " + UserName;
					user.whoAmI(UserNInfo);
					System.out.println("userInfo2 " + UserNInfo);
					break;
				case 2:
					UserNum = 2;
					UserNInfo = UserNum + " " + UserName;
					user.whoAmI(UserNInfo);
					System.out.println("userInfo3 " + UserNInfo);
					break;
				case 3:
					UserNum = 3;
					UserNInfo = UserNum + " " + UserName;
					user.whoAmI(UserNInfo);
					System.out.println("userInfo4 " + UserNInfo);
					break;
				}
			}
		}*/
				

				public void makeCharacter() {
					for(int i=0; i<UserVec.size(); i++) {
						UserService user = (UserService) user_vc.elementAt(i);
						
						switch(i%4) {
						case 0:
							UserNum = 0;
							user.whoAmI("0");
							break;
						case 1:
							UserNum = 1;
							user.whoAmI("1");
							break;
						case 2:
							UserNum = 2;
							user.whoAmI("2");
							break;
						case 3:
							UserNum = 3;
							user.whoAmI("3");
							break;
						}
					}
				}

		
		public void checkNumOfUser() {
			//try {
				if(UserVec.size() == 4) { // 현재 참여인원이 4의 배수이면
					makeCharacter();
					String msg = Integer.toString(UserVec.size()); // 참여자 수
					GameMsg obgm = new GameMsg("SERVER", "101", msg);
					//oos.writeObject(obgm);
					WriteAllObject(obgm);
					AppendText("현재 참가자 수는 " + UserVec.size() + "명으로 게임시작.");
					//makeCharacter();
				}
				else { // 참여인원이 부족하면
					AppendText("현재 참가자 수는 " + UserVec.size() + "명으로 게임시작 불가.");
				}
				
			/*} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}*/
		}
		
		// UserService Thread가 담당하는 Client 에게 참가자 중 해당하는 번호를 보내고 그에 따라 맵에서 자신인지 아닌지 알아볼 수 있음.
				public void whoAmI(String msg) {
					try {
								// dos.writeUTF(msg);
//								byte[] bb;
//								bb = MakePacket(msg);
//								dos.write(bb, 0, bb.length);
						//System.out.println("msg " + msg);
						GameMsg obcm = new GameMsg(UserName, "601", msg);
						oos.writeObject(obcm);
					} catch (IOException e) {
						AppendText("dos.writeObject() error");
						try {
//									dos.close();
//									dis.close();
							ois.close();
							oos.close();
							client_socket.close();
							client_socket = null;
							ois = null;
							oos = null;
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Logout(); // 에러가난 현재 객체를 벡터에서 지운다
					}
				}
		
		// UserService Thread가 담당하는 Client 에게 1:1 전송
		public void WriteOne(String msg) {
			try {
						// dos.writeUTF(msg);
//						byte[] bb;
//						bb = MakePacket(msg);
//						dos.write(bb, 0, bb.length);
				GameMsg obcm = new GameMsg(UserName, "200", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
//							dos.close();
//							dis.close();
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
				
		public void WriteOneObject(Object ob) {
			try {
			    oos.writeObject(ob);
			} 
			catch (IOException e) {
				AppendText("oos.writeObject(ob) error");		
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout();
			}
		}
		
		// 정보창에 점수
		public void SetPointWriteOne(String msg) {
			try {
				GameMsg obcm = new GameMsg(UserName, "301", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.CreateCoinWriteObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		
		public void AddHeartWriteOne(String msg) {
			try {
				GameMsg obcm = new GameMsg(UserName, "500", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.CreateCoinWriteObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		
		public void SetHeartWriteOne(String msg) {
			try {
				GameMsg obcm = new GameMsg(UserName, "501", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.CreateCoinWriteObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		
		public void SetTimeWriteOne(String msg) { // timer
			try {
				GameMsg obcm = new GameMsg(UserName, "701", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.CreateCoinWriteObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		public void nameWriteOne(String msg) { // timer
			try {
				GameMsg obcm = new GameMsg(UserName, "602", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.CreateCoinWriteObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		
		
		// 먹은 코인 사라지게
		public void CoinEatWriteOne(String msg) {
			try {
				GameMsg obcm = new GameMsg(UserName, "205", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.CreateCoinWriteObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		
		// 코인 위치 플레이어들에게 전달(생성)
		public void createCoinWriteOne(String msg) {
			try {
				GameMsg obcm = new GameMsg(UserName, "203", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.CreateCoinWriteObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		
		public void FinishWriteOne(String msg) {
			try {
				GameMsg obcm = new GameMsg(UserName, "104", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.CreateCoinWriteObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		
		public void LocationWriteOne(String msg) {
			try {
						// dos.writeUTF(msg);
//						byte[] bb;
//						bb = MakePacket(msg);
//						dos.write(bb, 0, bb.length);
				GameMsg obcm = new GameMsg(UserName, "201", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.LocationWriteObject() error");
				try {
//							dos.close();
//							dis.close();
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					// String msg = dis.readUTF();
//					byte[] b = new byte[BUF_LEN];
//					int ret;
//					ret = dis.read(b);
//					if (ret < 0) {
//						AppendText("dis.read() < 0 error");
//						try {
//							dos.close();
//							dis.close();
//							client_socket.close();
//							Logout();
//							break;
//						} catch (Exception ee) {
//							break;
//						} // catch문 끝
//					}
//					String msg = new String(b, "euc-kr");
//					msg = msg.trim(); // 앞뒤 blank NULL, \n 모두 제거
					Object obcm = null;
					String msg = null;
					GameMsg gm = null;
					if (socket == null)
						break;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					if (obcm == null)
						break;
					if (obcm instanceof GameMsg) {
						gm = (GameMsg) obcm;
						AppendObject(gm);
					} else
						continue;
					if (gm.code.matches("100")) {
						UserName = gm.UserName;
						//UserStatus = "O"; // Online 상태
						Login();
						checkNumOfUser();
						
					}
					else if(gm.code.matches("103")) { 
						msg = String.format("[%s] %s", gm.UserName, gm.data);
						msg = String.format(gm.data); // LocationMsg. userNum과 x y 위치
						
						FinishWriteOthers(msg); // 사용자들에게 내 번호와 위치 보내기
					} 
					else if(gm.code.matches("200")) { 
						msg = String.format("[%s] %s", gm.UserName, gm.data);
						msg = String.format(gm.data); // LocationMsg. userNum과 x y 위치
						
						LocationWriteOthers(msg); // 사용자들에게 내 번호와 위치 보내기
					} 
					else if(gm.code.matches("202")) {
						msg = String.format("[%s] %s", gm.UserName, gm.data);
						msg = String.format(gm.data); // coinCreateMsg. 코인의 x y 위치
						
						LocationCoinWriteOthers(msg);
					}
					else if(gm.code.matches("204")) {
						msg = String.format("[%s] %s", gm.UserName, gm.data);
						msg = String.format(gm.data); // coinEatMsg. userNum과 코인의 번호, 코인의 x y 위치
						
						CoinEatWriteOthers(msg);
					}
					else if(gm.code.matches("300")) {
						msg = String.format("[%s] %s", gm.UserName, gm.data);
						msg = String.format(gm.data); // pointMsg.
						
						SetPointWriteOthers(msg);
					}
					else if(gm.code.matches("500")) {
						msg = String.format("[%s] %s", gm.UserName, gm.data);
						msg = String.format(gm.data); // pointMsg.
						
						AddHeartWriteOthers(msg);
					}
					else if(gm.code.matches("501")) {
						msg = String.format("[%s] %s", gm.UserName, gm.data);
						msg = String.format(gm.data); // pointMsg.
						
						SetHeartWriteOthers(msg);
					}
					else if(gm.code.matches("700")) {
						msg = String.format("[%s] %s", gm.UserName, gm.data);
						msg = String.format(gm.data); // timeMsg.
						
						SetTimeWriteOthers(msg);
					}
					else if(gm.code.matches("602")) {
						msg = String.format("[%s] %s", gm.UserName, gm.data);
						msg = String.format(gm.data); // timeMsg.
						
						nameWriteOthers(msg);
					}
					/*else if (gm.code.matches("200")) {
						msg = String.format("[%s] %s", gm.UserName, gm.data);
						AppendText(msg); // server 화면에 출력
						String[] args = msg.split(" "); // 단어들을 분리한다.
						if (args.length == 1) { // Enter key 만 들어온 경우 Wakeup 처리만 한다.
							UserStatus = "O";
						} else if (args[1].matches("/exit")) {
							Logout();
							break;
						} else if (args[1].matches("/list")) {
							WriteOne("User list\n");
							WriteOne("Name\tStatus\n");
							WriteOne("-----------------------------\n");
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								WriteOne(user.UserName + "\t" + user.UserStatus + "\n");
							}
							WriteOne("-----------------------------\n");
						} else if (args[1].matches("/sleep")) {
							UserStatus = "S";
						} else if (args[1].matches("/wakeup")) {
							UserStatus = "O";
						} else if (args[1].matches("/to")) { // 귓속말
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								if (user.UserName.matches(args[2]) && user.UserStatus.matches("O")) {
									String msg2 = "";
									for (int j = 3; j < args.length; j++) {// 실제 message 부분
										msg2 += args[j];
										if (j < args.length - 1)
											msg2 += " ";
									}
								}
							}
						} else { // 일반 채팅 메시지
							UserStatus = "O";
							//WriteAll(msg + "\n"); // Write All
							WriteAllObject(cm);
						}
					} */ 
					else if (gm.code.matches("400")) { // logout message 처리
						Logout();
						break;
					} else { // 300, 500, ... 기타 object는 모두 방송한다.
						WriteAllObject(gm);
					} 
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
//						dos.close();
//						dis.close();
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // 에러가난 현재 객체를 벡터에서 지운다
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			} // while
		} // run
	}
}
