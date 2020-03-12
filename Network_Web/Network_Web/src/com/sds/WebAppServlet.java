package com.sds;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import msg.Msg;
import tcpip.Client;

@WebServlet({ "/WebAppServlet", "/webapp" })
public class WebAppServlet extends HttpServlet {

	Socket socket;
//	Sender sender;
	boolean flag = false;
//	SendData sendData;
	String cid="Browser";
	String address = "70.12.113.200";
	int port = 8888;
	private static final long serialVersionUID = 1L;

	public WebAppServlet() {
		super();
	}

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Client client = null;
		try {
			client = new Client(address, port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String ip = request.getParameter("ip");
		String txt = request.getParameter("txt");
		System.out.println(ip+" "+txt);
		Msg msg = new Msg(cid, txt, ip);
		client.setMsg(msg);
		
		client.sender.setMsg(msg);
		
		new Thread(client.sender).start();
		

		//sender.setMsg(msg);
		//new Thread(sender).start();

		//new Receiver(socket).start();
		//sendData = new SendData();
		
		

	}


//	class Sender implements Runnable {
//
//		OutputStream os;
//		ObjectOutputStream oos;
//		Msg msg;
//
//		public Sender(Socket socket) throws IOException {
//			os = socket.getOutputStream();
//			oos = new ObjectOutputStream(os);
//		}
//
//		public void setMsg(Msg msg) {
//			this.msg = msg;
//		}
//
//		@Override
//		public void run() {
//			if (oos != null) {
//				try {
//					oos.writeObject(msg);
//					System.out.println(msg.getId()+" "+msg.getTxt());
//				} catch (IOException e) {
//					// e.printStackTrace();
//					if (oos != null) {
//						try {
//							oos.close();
//						} catch (IOException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
//						try {
//							new Client(address, 8888);
//						} catch (IOException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
//						return;
//					}
//
//				}
//			}
//		}
//
//	}
//
//	class SendData implements Runnable {
//		boolean flag = false;
//
//		public void setFlag(boolean flag) {
//			this.flag = flag;
//		}
//
//		@Override
//		public void run() {
//			while (true) {
//				if (this.flag == false) {
//					continue;
//				}
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				System.out.println("Random...");
//				Random r = new Random();
//				int data = r.nextInt(100);
//				Msg msg = new Msg(cid, data + "");
//				sender.setMsg(msg);
//				new Thread(sender).start();
//			}
//		}
//
//	}

}