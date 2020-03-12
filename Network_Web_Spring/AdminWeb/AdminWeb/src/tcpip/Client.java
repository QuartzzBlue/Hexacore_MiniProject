package tcpip;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import msg.Msg;

public class Client {

	Socket socket;
	public Sender sender;
	boolean flag = false;
//	SendData sendData;
	String cid;
	Msg msg;
	
	
	public Client() {}
	
	public void setMsg (Msg msg) {
		this.msg = msg;
	}
	
	
	public Client(String address,int port) throws IOException {
				try {
			socket = new Socket(address, port);
		
		}catch(Exception e) {
			while(true) {
				System.out.println("Retry..");
				try {
					Thread.sleep(1000);
					socket = new Socket(address, port);
					break;
				} catch (Exception e1) {
					//e1.printStackTrace();
				}
			}
		}
		
		System.out.println("Connected Server:"+address);
		
		sender = new Sender(socket);
	}
	
	
	public class Sender implements Runnable{

		OutputStream os;
		ObjectOutputStream oos;
		Msg msg;
		
		public Sender(Socket socket) throws IOException {
			os = socket.getOutputStream();
			oos = new ObjectOutputStream(os);
		}
		public void setMsg(Msg msg) {
			this.msg = msg;
		}
		@Override
		public void run() {
			if(oos != null) {
				try {
					oos.writeObject(msg);
				} catch (IOException e) {
					//e.printStackTrace();
					if(oos != null) {
						try {
							oos.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							new Client("70.12.113.200", 8888);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						return;
					}
					
				}
			}
		}
		
	}
	
}





