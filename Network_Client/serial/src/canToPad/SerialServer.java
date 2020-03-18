package canToPad;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import msg.Msg;

public class SerialServer implements SerialPortEventListener {
	CommPortIdentifier commPortIdentifier;
	CommPort commPort;
	InputStream in;
	BufferedInputStream bin;
	OutputStream out;

	Socket socket;
	String cid;
	Sender sender;
	Thread temp;

	boolean flag = false;

	public SerialServer() {	}
	public SerialServer(String portName) throws Exception {
		commPortIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		System.out.println("Identified Com Port!");
		connect();
		System.out.println("Connect Com Port!");
		new Thread(new SerialWrite()).start();
		System.out.println("Start CAN Network!!!");
	}// SerialConnect constructor

	public void connect() throws Exception {
		if (commPortIdentifier.isCurrentlyOwned()) {
			System.out.println("Port is currently in use....");
		} else {
			commPort = commPortIdentifier.open(this.getClass().getName(), 5000);
			// 네트워크랑 비슷
			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.addEventListener(this);
				serialPort.notifyOnDataAvailable(true);
				serialPort.setSerialPortParams(921600, // 통신속도
						SerialPort.DATABITS_8, // 데이터 비트
						SerialPort.STOPBITS_1, // stop 비트
						SerialPort.PARITY_NONE); // 패리티
				in = serialPort.getInputStream();
				bin = new BufferedInputStream(in);
				out = serialPort.getOutputStream();
			} else {
				System.out.println("this port is not serial ");
			} // serial에서만 연결을 위해서
				// serial이 외에도 다른 연결 방식이 존재하기 때문에
		} // commPortIdentifier가 사용할 수 있는지 하드웨어는 동시에 제어가 되지 않느다.
			// 점유가 되어있는 상태인지를 확인
	}// connect method

	@Override
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			byte[] readBuffer = new byte[128];
			try {
				while (bin.available() > 0) {
					int numBytes = bin.read(readBuffer);
				}

				String ss = new String(readBuffer);
				System.out.println("Receive Low Data:" + ss + "||");
				ss = ss.substring(27,29 ); 
				System.out.println(ss);
				
				if (flag) {
					try {
						Msg msg = new Msg(cid, ss, null);
						sender.setMsg(msg);
						Thread.sleep(1000);
						new Thread(sender).start();
						System.out.println(msg.getTxt());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}// serialEvent method

	public void send(String msg) {
		new Thread(new SerialWrite(msg)).start();
	}//send method
	
	class SerialWrite implements Runnable{
		String data;
		//모든 데이터는 String
		
		public SerialWrite(){
			this.data = ":G11A9\r";
		}
		public SerialWrite(String msg){
			this.data = convertData(msg);
		}
		public String convertData(String msg) {
			msg = msg.toUpperCase();
			msg = "W28" + msg;
			//W28 00000000 0000000000000000
			char[] c = msg.toCharArray();
			int checkSum = 0;
			for(char ch:c) {
				checkSum+=ch;
			}
			checkSum = (checkSum & 0xFF);
			String result = ":";
			result += msg + 
					Integer.toHexString(checkSum).toUpperCase()+
					"\r";
			System.out.println(result);
			return result;
		}
		
		@Override
		public void run() {
			byte[] outData = data.getBytes();
			try {
				out.write(outData);//이렇게 data를 CAN Network Area에 쏜다.
			} catch (IOException e) {
				e.printStackTrace();
			}
		}//run method
		
	}//SerialWrite class 
		

	public  void connectPad(String address, int port){
		cid = "Device3";
		try {
			socket = new Socket(address, port);

		} catch (Exception e) {
			while (true) {
				System.out.println("Retry..");
				try {
					Thread.sleep(1000);
					socket = new Socket(address, port);
					break;
				} catch (Exception e1) {
					// e1.printStackTrace();
				}
			}
		}
		System.out.println("Connected Server:" + address);
		try {
			sender = new Sender(socket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Msg msg = new Msg(cid, null, null);
		sender.setMsg(msg);
		new Thread(sender).start();
		try {
			new Receiver(socket).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class Sender implements Runnable {
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
			if (oos != null) {
				try {
					oos.writeObject(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	
	class Receiver extends Thread {
		InputStream is;
		ObjectInputStream ois;

		public Receiver(Socket socket) throws IOException {
			is = socket.getInputStream();
			ois = new ObjectInputStream(is);
		}

		@Override
		public void run() {
			while (ois != null) {
				Msg msg = null;
				try {
					msg = (Msg) ois.readObject();
					System.out.println(msg.getTxt());
					if (msg.getTxt().equals("1")) {
							flag = true;
							System.out.println("새로운 스레드 생성");
							String id = "10000001";
							String data = "0000000000000000";
							try {
								send(id+data);
							} catch (Exception e) {
								e.printStackTrace();
							}

					} else if (msg.getTxt().equals("0")) {
						flag = false;
						String id = "10000001";
						String data = "1000000000000000";
						try {
							send(id+data);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} catch (Exception e) {
					System.out.println("Server Die");
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
	
	public static void main(String[] args) {
		SerialServer sc = null;
		try {
			sc = new SerialServer("COM11");
			sc.connectPad("70.12.231.215",8888);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}// main method
}// SerialConnect class
