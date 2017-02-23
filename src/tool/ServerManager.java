package tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ServerManager implements Runnable {
	private ServerSocket server;
	private int portNo = 5230;

	// 存储所有客户端Socket连接对象
	protected static List<Socket> list_client = new ArrayList<Socket>();
	// 线程池
	private ExecutorService es;

	// 用以覆盖跟Swing空间交互
	public abstract void printConsole(String str);

	public void run() {
		try {
			// 设置服务器端口
			server = new ServerSocket(portNo);
			// 创建一个线程池
			es = Executors.newCachedThreadPool();
			printConsole("start...");
			// 用来临时保存客户端连接的Socket对象
			Socket client = null;
			while (true) {
				// 接收客户连接并添加到list中
				client = server.accept();
				list_client.add(client);
				// 开启一个客户端线程
				es.execute(new ThreadServer(client));// 就这里一句话特么我就要一个类啊!
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendBroardcast(String msg) {
		// 这个发送信息对于整个list中的socket
		PrintWriter pw_send;
		for (Socket s : list_client) {
			try {
				pw_send = new PrintWriter(s.getOutputStream(), true);
				pw_send.println(msg);
				// printConsole(s.getInetAddress() + " " +
				// s.getInetAddress().getHostName() + " " + "Send<< " + msg);
				printConsole("Send<<  " + msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				printConsole(s.getInetAddress() + "   " + s.getInetAddress().getHostName() + "   " + "Fail to send\n");
			}
		}
	}

	public synchronized void shutdown() throws Exception {
		if (list_client.isEmpty()) {
			printConsole("连接已断开");
			return;
		}
		// for (Socket s : list_client)
		// {用增强for循环对list进行遍历来删除会抛异常，因为remove之后list的数量不同
		// if (s != null && s.isConnected()) {
		// printConsole(s.getInetAddress() + s.getInetAddress().getHostName() +
		// " will close.\n");
		// s.close();
		// list_client.remove(s);
		// }
		// }用增强for循环对list进行遍历来删除会抛异常，因为remove之后list的数量不同
		Iterator<Socket> i = list_client.iterator();
		while (i.hasNext()) {
			Socket s = i.next();
			if (s != null && s.isConnected()) {
				printConsole(s.getInetAddress() + s.getInetAddress().getHostName() + " will close.\n");
				s.close();
				i.remove();
			}
		}
		es.shutdownNow();
	}

	class ThreadServer implements Runnable {// 觉得这个还可以再精简一点
		private Socket tr_socket;
		private BufferedReader tr_br;
		private PrintWriter tr_pw;
		private String str_pub;

		public ThreadServer(Socket socket) throws IOException {
			this.tr_socket = socket;
			tr_br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			str_pub = "user:" + this.tr_socket.getInetAddress() + " come total:" + ServerManager.list_client.size();
			sendMessage();
		}// 构造函数，从ServerManager中接收Socket

		public void run() {
			try {
				String str_msg = "";
				while (true) {
					if ((str_msg = tr_br.readLine()) != null) {
						printConsole("\nClient>> " + str_msg);
					} else {
						shutdown();
					}
				}
			} catch (Exception e) {
				printConsole("\nClient>> " + "链接终止");
				e.printStackTrace();
			}
		}

		// 发送消息给该客户端
		public void sendMessage() throws IOException {
			printConsole("Send<< " + str_pub);
			for (Socket client : ServerManager.list_client) {
				tr_pw = new PrintWriter(client.getOutputStream(), true);// 告诉我这样new没问题
				tr_pw.println(str_pub);
				tr_pw.flush();
			}
		}

		public void sendMessage(String str_send) throws IOException {
			printConsole("Send<< " + str_send);
			for (Socket client : ServerManager.list_client) {
				tr_pw = new PrintWriter(client.getOutputStream(), true);
				tr_pw.println(str_send);
				tr_pw.flush();
			}
		}

		protected void disconnect() throws IOException {
			ServerManager.list_client.remove(tr_socket);
			tr_br.close();
			tr_pw.close();
			str_pub = "user:" + this.tr_socket.getInetAddress() + " exit total:" + ServerManager.list_client.size();
			tr_socket.close();
			sendMessage();
		}
	};
}
