package core;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;



public class Manager {
	public static  Map<String,Stato> set;
	public static final String multicastAddress  = "224.0.0.1";
	public final int PORT = 7777;
	public final int PORTTCP = 7778;
	
	
	private ServerSocket serv;
	
	public Manager() {
		if(set == null) {
			set  = new HashMap<String, Stato>();
		
		}
	}
	
	public void scanNetwork() {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				MulticastSocket sock = null;
				try {
					sock = new MulticastSocket();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					sock.close();
				}

				// TODO Auto-generated method stub
				byte[] mess = {'S', 'C', 'A', 'N'};
				InetAddress addr = null;
				try {
					addr = InetAddress.getByName(multicastAddress);
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				DatagramPacket packet=new DatagramPacket(mess, mess.length, addr, PORT);
				
				while (true) {
					try {
						sock.send(packet);
						Thread.sleep(10000);
						System.out.println("passati i 10 sec");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						sock.close();
					}
				}
			}
			
		};
		Thread t = new Thread(runnable);
		t.start();
	}
	
	public void handleResponseUDP(){
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				DatagramSocket sock = null;
				try {
					sock = new DatagramSocket(PORT);
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// TODO Auto-generated method stub
				byte[] mess = new byte[65000];
				DatagramPacket packet=new DatagramPacket(mess, mess.length);
				
				while (true) {
					try {
						sock.receive(packet);
						System.out.println("Received : "+packet.getAddress().toString());
						if(Manager.set != null) {
							//verifica se è già presente
							synchronized (Manager.set) {
								if (Manager.set.containsKey(packet.getAddress().toString()))
									Manager.set.get(packet.getAddress().toString())
											.setAlive(System.currentTimeMillis()); // aggiorna solo il ttl
								else
									Manager.set.put(packet.getAddress().toString(),
											new Stato(System.currentTimeMillis(), false)); // aggiunge uno nuovo
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						sock.close();
					}
				}
			}
			
		};
		
		Thread t = new Thread(runnable);
		t.start();
	}
	
	
	public void handlerTCP() {

		try {
			serv = new ServerSocket(PORTTCP);

			Runnable runnableM = new Runnable() {

				@Override
				public void run() {
					while(true) {
						try {
							Socket sock = serv.accept();


							Runnable server = new Runnable() {

								@Override
								public void run() {
									try {
										BufferedReader brd = new BufferedReader(new InputStreamReader(sock.getInputStream(),"UTF-8"));
										String s = brd.readLine();

										if(s.equals("ALIVE"))
											synchronized (Manager.set) {
												if (Manager.set.containsKey(sock.getInetAddress().toString()))
													Manager.set.get(sock.getInetAddress().toString()).setAlive(System.currentTimeMillis()); // aggiorna solo il ttl
												else
													Manager.set.put(sock.getInetAddress().toString(),
															new Stato(System.currentTimeMillis(), true)); // aggiunge uno nuovo
											}



									}catch(IOException exc) {
										System.out.println("Eccezzione I/O:"+exc);
									}finally {
										try {sock.close();}
										catch(IOException exc2) {}
									}


								}

							};



							Thread t = new Thread(server);
							t.start();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}

			};
			Thread threadM = new Thread(runnableM);
			threadM.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void refreshAlive() {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				
			}
			
		};
		
		Thread t = new Thread(runnable);
		t.start();
		
	}
	
	public void statusNetwork() {
		
	}
	
	public static void main(String[] args) {
		Manager manager = new Manager();
		manager.scanNetwork();
		manager.handleResponseUDP();
		manager.handlerTCP();
	}
}
