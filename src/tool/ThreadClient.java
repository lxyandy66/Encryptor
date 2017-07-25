package tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class ThreadClient implements Runnable {
	private Socket socket;
	private String server_address;
	private int portNo;
	private BufferedReader br;
	private PrintWriter pw;
	public static final String EXIT_SIGNAL = "OVER";

	public abstract void printMsg(String str);

	public static boolean isLegalAddress(String str) {
		return true;//*IP地址的合法性检测应该要考虑*//
//		return str
//				.matches("(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)) {3}");
	}
	
	public boolean isConnect(){
		return socket.isConnected();
	}
	
	public ThreadClient(String address,int port) throws Exception {
		// TODO Auto-generated constructor stub
		if(!isLegalAddress(address))
			throw new Exception("IP地址错误");
		server_address=address;
		portNo=port;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			socket = new Socket(server_address, portNo);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(socket.getOutputStream(), true);
			String msg;
			while ((msg = br.readLine()) != null) {
				printMsg(msg);
				if (msg.trim().equals(EXIT_SIGNAL)) {
					sendMessage(EXIT_SIGNAL);
					shutdown();
					break;
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendMessage(String str) {
		printMsg("\nClient: " + str);
		pw.println(str);
		pw.flush();
	}

	public void shutdown() {
		try {
			if (pw != null)
				pw.close();
			if (br != null)
				br.close();
			if (socket != null || !socket.isClosed())
				socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
