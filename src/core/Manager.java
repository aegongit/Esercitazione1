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
	public static  Map<String,Device> set;
	public static final String MULTICASTADDRESS  = "224.0.0.3";
	public final int PORTUDP = 7777;
	public final int PORTTCP = 7778;
	
	
	private ServerSocket serv;
	
	public Manager() {
		if(set == null) {
			set  = new HashMap<String, Device>();
		
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
					addr = InetAddress.getByName(MULTICASTADDRESS);
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				DatagramPacket packet=new DatagramPacket(mess, mess.length, addr, PORTUDP);
				
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
					sock = new DatagramSocket(PORTUDP);
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
						System.out.println("Packet UDP Received from : "+packet.getAddress().toString());
						
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
									while(true) {
									try {
										BufferedReader brd = new BufferedReader(new InputStreamReader(sock.getInputStream(),"UTF-8"));
										String s = brd.readLine();
										
										System.out.println("Risposta :"+s);
										if(s.equals("ALIVE"))
											synchronized (Manager.set) {
												if (Manager.set.containsKey(sock.getInetAddress().toString()))
												{
													System.out.println("Aggiorna");
													Manager.set.get(sock.getInetAddress().toString()).setLast_update(System.currentTimeMillis()); // aggiorna solo il ttl
												}
												else
													Manager.set.put(sock.getInetAddress().toString(),
															new Device(System.currentTimeMillis())); // aggiunge uno nuovo
												Manager.set.notifyAll();
											}

										

									}catch(IOException exc) {
										System.out.println("Eccezzione I/O:"+exc);
										Manager.set.get(sock.getInetAddress().toString()).setAlive(false); // aggiorna solo l'attributo alive
										break;
										
									}finally {
										//System.out.println("Connessione tcp chisa");
										//try {sock.close();}
										//catch(IOException exc2) {}
									}
									}

								}

							};



							Thread td = new Thread(server);
							td.start();
						} catch (IOException e) {
							System.out.println(e.getMessage());
						}

					}

				}

			};
			Thread threadM = new Thread(runnableM);
			threadM.start();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}
	


	public static void main(String[] args) {
		Manager manager = new Manager();
		manager.scanNetwork();
		manager.handleResponseUDP();
		manager.handlerTCP();
	}
}
