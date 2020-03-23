//브로드캐스트

package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import msg.Msg;

public class Server {

	HashMap<String, ObjectOutputStream> maps = new HashMap<>();
	ServerSocket serverSocket;
	boolean aflag = true;
	HashMap<String,String> FLipNid = new HashMap<String,String>();
	

	public ArrayList<String> convert(HashMap<String, ObjectOutputStream> sendMaps) {
		Set<String> keys = sendMaps.keySet();
		keys = sendMaps.keySet();
		Iterator<String> its = keys.iterator();
		ArrayList<String> list = new ArrayList<>();
		while (its.hasNext()) {
			list.add(its.next());
		}
		return list;
	}

	public Server() {
	}

	public Server(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("Start Server");
		Runnable r = new Runnable() {
			@Override
			public void run() {
				while (aflag) {
					Socket socket = null;
					try {
						System.out.println("Server Ready..");
						socket = serverSocket.accept();
						
							//makeOut(socket);
							new Receiver(socket).start();
						
					} catch (IOException e) {
						//e.printStackTrace();
					}
				}
			}
		};
		new Thread(r).start();
	}
/*
	public void makeOut(Socket socket) throws IOException {
		OutputStream os;
		ObjectOutputStream oos;
		os = socket.getOutputStream();
		oos = new ObjectOutputStream(os);
		maps.put(socket.getInetAddress().toString(), oos);
		System.out.println("패드 접속");
		tapip = socket.getInetAddress().toString();
		System.out.println("접속자수:" + maps.size());
	}
*/

	class Receiver extends Thread {

		InputStream is;
		ObjectInputStream ois;

		OutputStream os;
		ObjectOutputStream oos;

		ArrayList<String> list;

		Socket socket;

		public Receiver(Socket socket) throws IOException {
			this.socket = socket;
			is = socket.getInputStream();
			ois = new ObjectInputStream(is);

			
			os = socket.getOutputStream();
			oos = new ObjectOutputStream(os);
		
			maps.put(socket.getInetAddress().toString(), oos);

			list = new ArrayList<String>();
			list = convert(maps);
			System.out.println("접속 : " + socket.getInetAddress().toString());
			System.out.println("접속자수:" + list.size());
		}

		@Override
		public void run() {
			while (ois != null) {

				Msg msg = null;
				try {
		
							msg = (Msg) ois.readObject();
							
							if(msg.getId()!=null&&!msg.getId().equals("")) {
								if(msg.getId().substring(0,2).equals("FL")) {
									FLipNid.put(socket.getInetAddress().toString(),msg.getId());
									System.out.println("FL Added : "+msg.getId() + ", FLidNip List : " + FLipNid.toString());	
								}
								else {
									System.out.println("id : "+msg.getId() + ", txt : " + msg.getTxt() + ", tid : " + msg.getTid());
									sendMsg(msg);
								}
							}
							
							else {
								
								System.out.println("id : "+msg.getId() + ", txt : " + msg.getTxt() + ", tid : " + msg.getTid());
								sendMsg(msg);
							}
							
							

						
				} catch (Exception e) {
					//e.printStackTrace();
					maps.remove(socket.getInetAddress().toString());
					if(FLipNid.containsKey(socket.getInetAddress().toString())) FLipNid.remove(socket.getInetAddress().toString());
					System.out.println("FLidNip List : " + FLipNid.toString());
					//tapips.remove(socket.getInetAddress().toString());
					System.out.println(socket.getInetAddress() + ":Exit ..");
					System.out.println("접속자수:" + maps.size());
					// sendMsg(msg);
					break;
				}
			}
			try {
				if (ois != null) {
					ois.close();
				}
				if (socket != null) {
					socket.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	// 모두에게 보낼 때
	class Sender extends Thread {
		Msg msg;

		public Sender(Msg msg) {
			this.msg = msg;
		}

		@Override
		public void run() {
			// HashMap 에 있는 oos 를 꺼낸 다음
			// for문을 돌리며 전송
			Collection<ObjectOutputStream> cols = maps.values();
			Iterator<ObjectOutputStream> its = cols.iterator(); // JDBC-resultset 과 비슷한 구조

			while (its.hasNext()) {
				try {
					its.next().writeObject(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	// 특정 대상에게 보낼 때
	class Sender2 extends Thread {
		Msg msg;

		public Sender2(Msg msg) {
			this.msg = msg;
		}

		@Override
		public void run() {

			try {
				if (msg.getTid() != null || !msg.getTid().equals("")) {
					System.out.println("id : "+msg.getId() + ", txt  : " + msg.getTxt()+", tid : "+msg.getTid());
					
				}else{
					System.out.println("All control : " + msg.getTxt());
				}
				
				//if(maps.containsKey(tapip))
				if(FLipNid.containsValue(msg.getTid())) {
					//maps.get(tapip).writeObject(msg);
					//maps.get(FLipNid.get(msg.getTid())).writeObject(msg);
					
					//FL (ip,id) 에서 id 로 ip 를  찾아 그 ip 의 oos 로 writeibject
					for(String ip : FLipNid.keySet()) {
						if(msg.getTid().equals(FLipNid.get(ip)))
							 maps.get(ip).writeObject(msg);
					}
				
				}
				else {
					System.out.println("탭서버(" + msg.getTid() + ") 에 연결 되어 있지 않습니다.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void sendMsg(Msg msg) {
		Sender2 sender2 = new Sender2(msg);
		sender2.start();
		System.out.println("sender2 start");
	}

	public static void main(String[] args) {
		Server server = null;
		try {
			server = new Server(8888);
			// server.serverStart(); //관리자가 메시지, 아이디 입력 메시지 : 제어할 Java App
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
