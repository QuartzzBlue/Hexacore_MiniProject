//��ε�ĳ��Ʈ

package server;

import java.awt.Button;
import java.awt.Frame;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;
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
import java.util.Scanner;
import java.util.Set;

import msg.Msg;

public class Server {

	HashMap<String, ObjectOutputStream> maps = new HashMap<>();
	ServerSocket serverSocket;
	boolean aflag = true;
	String tapip = "/70.12.226.134";

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
						System.out.println(socket.getInetAddress()); // �º��� ���ӵǸ� ip ����

						if (socket.getInetAddress().toString().equals(tapip))
							makeOut(socket);
						else {
							new Receiver(socket).start();
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		new Thread(r).start();
	}

	public void makeOut(Socket socket) throws IOException {
		OutputStream os;
		ObjectOutputStream oos;
		os = socket.getOutputStream();
		oos = new ObjectOutputStream(os);
		maps.put(socket.getInetAddress().toString(), oos);
		System.out.println("�����ڼ�:" + maps.size());
	}

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
			System.out.println(socket.getInetAddress().toString());
			maps.put(socket.getInetAddress().toString(), oos);

			list = new ArrayList<String>();
			list = convert(maps);
			System.out.println("�����ڼ�:" + list.size());
		}

		@Override
		public void run() {
			while (ois != null) {
				Msg msg = null;
				try {
					msg = (Msg) ois.readObject();
					System.out.println("ID : " + msg.getId() + ", msg : " + msg.getTxt() + ", tid : " + msg.getTid());
					sendMsg(msg);
				} catch (Exception e) {
					e.printStackTrace();
					maps.remove(socket.getInetAddress().toString());
					System.out.println(socket.getInetAddress() + ":Exit ..");
					System.out.println("�����ڼ�:" + maps.size());
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

//	public void serverStart() {
//	
//		while (true) {
//		
//			ArrayList<String> list = new ArrayList<String>();
//			list = convert(maps);
//			
//			Msg msg = null;
//			if (ip == null || ip.equals("")) {
//				msg = new Msg("<��ü����>", txt, null,list);
//			} else {
//				System.out.println("IP:" + ip);
//				msg = new Msg("<�Ӹ�>", txt, ip.trim());
//			}
//
//			sendMsg(msg);
//		}
//		sc.close();
//	}

	// ��ο��� ���� ��
	class Sender extends Thread {
		Msg msg;

		public Sender(Msg msg) {
			this.msg = msg;
		}

		@Override
		public void run() {
			// HashMap �� �ִ� oos �� ���� ����
			// for���� ������ ����
			Collection<ObjectOutputStream> cols = maps.values();
			Iterator<ObjectOutputStream> its = cols.iterator(); // JDBC-resultset �� ����� ����

			while (its.hasNext()) {
				try {
					its.next().writeObject(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	// Ư�� ��󿡰� ���� ��
	class Sender2 extends Thread {
		Msg msg;

		public Sender2(Msg msg) {
			this.msg = msg;
		}

		@Override
		public void run() {

			try {
				if (msg.getTid() != null || msg.getTid().equals("")) {
					System.out.println("IP of Target Client : " + msg.getTid() + " control : " + msg.getTxt());
					
				}else{
					System.out.println("All control : " + msg.getTxt());
				}
				maps.get(tapip).writeObject(msg);
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
			// server.serverStart(); //�����ڰ� �޽���, ���̵� �Է� �޽��� : ������ Java App
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
