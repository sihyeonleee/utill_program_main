package comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Network {

	// -DServer.network=FALSE
	
	// private final int BUFFER_SIZE = 1024;

	private String ip;
	private int port;
	private Listener listener;

	private List<Socket> clientList;
	private ServerSocket server;
	private Socket client;
	private DatagramSocket p2p;

	public String serverMode = "CLIENT";
	
	public Map<String, InetAddress> ipList;
	
	public Network(int port) {
		this.port = port;
	}

	public Network(int port, String ip) {
		this(port);
		this.ip = ip;
	}

	public Network(int port, String ip, Listener listener) {
		this(port, ip);
		this.listener = listener;
	}

	public void serverOn() {
		
		if(!main.Main.isNetwork) {
			System.err.println("Network Mode is Disabled");
			return;
		}

		ExecutorService runable = Executors.newCachedThreadPool();

		runable.execute(() -> {

			close();
			serverMode = "SERVER";

			try {

				clientList = new ArrayList<>();

				server = new ServerSocket();
				InetSocketAddress ipep = new InetSocketAddress(port);
				server.bind(ipep);

				System.out.println("IP : " + ip + "// PORT : " + port + "::: SERVER ON");

				while (serverMode.equals("SERVER")) {
					try {

						Socket client = server.accept();
						clientList.add(client);

						System.out.println("Connect Client IP = " + client.getRemoteSocketAddress().toString());
						System.out.println("전체 접속자 :: " + clientList.size() + "명");

						ExecutorService receiver = Executors.newCachedThreadPool();
						
						receiver.execute(() -> {
							try (Socket thisClient = client; InputStream recv = client.getInputStream();) {
								while (serverMode.equals("SERVER")){
									BufferedReader reader = new BufferedReader(new InputStreamReader(recv)); 
									String line = reader.readLine();
									if (listener != null) listener.onMessage(line);
								}
							} catch (Throwable e) {
								e.printStackTrace();
							} finally {
								if(client.isClosed()) clientList.remove(client); 
								System.out.println("전체 접속자 :: " + clientList.size() + "명");
								System.err.println( "Disconnect Client IP Address =" + client.getRemoteSocketAddress().toString());
							}
						});

					} catch (Throwable e) {
						System.err.println("클라이언트 연결 끊김");
					}
				}
			} catch (IOException e1) {
				System.err.println("서버 연결 끊김");
			}

		});

	}

	public void clientOn(int milSec) {
		
		if(!main.Main.isNetwork) {
			System.err.println("Network Mode is Disabled");
			return;
		}
		
		ExecutorService receiver = Executors.newSingleThreadExecutor();

		receiver.execute(() -> {

			close();
			serverMode = "CLIENT";

			while (serverMode.equals("CLIENT")) {
				try {

					client = new Socket();
					InetSocketAddress ipep = new InetSocketAddress(ip, port);
					client.connect(ipep);

					System.out.println("Connect Client IP = " + ip + "// PORT : " + port);

					try (InputStream recv = client.getInputStream();) {
						try {
							while (true) {
								BufferedReader reader = new BufferedReader(new InputStreamReader(recv)); 
								String line = reader.readLine().trim();
								if (listener != null) listener.onMessage(line);
							}
						} catch (Throwable e) {
							System.err.println("클라 > 서버 :: Socket 연결끊김");
							e.printStackTrace();
						}
					}
				} catch (IOException e1) {
					System.err.println("클라 > 서버 :: Socket 연결 실패");
					e1.printStackTrace();
				}

				try {
					Thread.sleep(milSec);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		});
	}
	
	public void udpOn(){
		
		if(!main.Main.isNetwork) {
			System.err.println("Network Mode is Disabled");
			return;
		}
		
		ExecutorService receiver = Executors.newSingleThreadExecutor();
		
		receiver.execute(() -> {
			
			close();
			serverMode = "P2P";
			ipList = new HashMap<>();
			
			try {
				// 기준이 되는 서버 아이피 추가
				ipList.put(ip, InetAddress.getByName(ip));
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
			
			while(serverMode.equals("P2P")) { // 무한루프 통해 메시지 계속 전송받는다
		
				try {
					
					p2p = new DatagramSocket(port);
					
					try {
						while (serverMode.equals("P2P")) {
							byte[] data = new byte[65508]; 
							DatagramPacket dp = new DatagramPacket(data, data.length);
							p2p.receive(dp);
							
							String ip = dp.getAddress().getHostAddress();
							String msg = new String(dp.getData()).trim();
							
							// IP 최초 등록(서버가되는 PC)
							if(!ipList.containsKey(ip)) {
								ipList.put(ip, InetAddress.getByName(ip));
								// 다른사용자에게 IP 공유 >>>>>>>>>>>>>>> 송신
								send(ipList.keySet().toString());
							}
							
							// P2P 사용자의 IP 추가        <<<<<<<<<<<<<<< 수신
							if(msg.matches("\\[.*\\]")) {
								String[] ips = msg.substring(1, msg.length() -1).split(",");
								for(String i : ips) {
									if(!ipList.containsKey(ip)) {
										ipList.put(ip, InetAddress.getByName(i));
									}
								}
							} else if(listener != null) {
								listener.onMessage(msg);
							}
							
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
						
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					p2p.close();
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {

				}
				
			}
			
		});
		
	}
	
	@SuppressWarnings("rawtypes")
	public int connectedUserCnt(){
		
		int cnt = 0;
		
		Iterator it = clientList.iterator();
		
		while(it.hasNext()){
			Socket s = (Socket) it.next();
			if(s.isClosed()){
				clientList.remove(s);
			}else {
				cnt++;
			}
		}
		
		return cnt;
		
	}
	
	
	
	
	

	public void close() {
		if (serverMode.equals("SERVER")) {
			try {
				if (server != null) server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(serverMode.equals("CLIENT")){
			try {
				if (client != null) client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(serverMode.equals("P2P")){
			try {
				if (p2p != null) p2p.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void send(String msg) {
		
		if(!main.Main.isNetwork) return;
		
		if (serverMode.equals("SERVER")) {
			for (Socket client : clientList) sendClient(client, msg);
		} else if(serverMode.equals("CLIENT")){
			sendClient(client, msg);
		} else if(serverMode.equals("P2P")){
			try {
				DatagramSocket ds = new DatagramSocket();
				for(Map.Entry<String, InetAddress> p : ipList.entrySet()){
					InetAddress inetAddress = p.getValue();
					DatagramPacket dp = new DatagramPacket( msg.getBytes(), msg.getBytes().length, inetAddress, port);
					ds.send(dp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean sendClient(Socket socket, String msg) {
		
		boolean isSuccess = false;
		
		OutputStream output = null;
		
		try {
			output = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(output, true); 
			writer.println(msg);

//			send = socket.getOutputStream();
//			send.write("test".getBytes());
//			send.flush();
			isSuccess = true;
			return isSuccess;
		} catch (IOException e) {
			e.printStackTrace();
			return isSuccess;
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public interface Listener {
		public void onMessage(String msg);
	}

}
