package core;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
	public static  Map<String,DeviceInfo> set;
	public static final String MULTICASTADDRESS  = "224.0.0.1";
	public final int UDP_PORT = 7777;
	public final int TCP_PORT = 7778;
	
	
	private ServerSocket serv;
	
	public Manager() {
		if(set == null) {
			set  = new HashMap<String, DeviceInfo>();
		
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
					e1.printStackTrace();
					sock.close();
				}

				// TODO Auto-generated method stub
				byte[] mess = {'S', 'C', 'A', 'N'};
				InetAddress addr = null;
				try {
					addr = InetAddress.getByName(MULTICASTADDRESS);
				} catch (UnknownHostException e1) {
					
					e1.printStackTrace();
				}
				
				DatagramPacket packet=new DatagramPacket(mess, mess.length, addr, UDP_PORT);
				
				while (true) {
					try {
						sock.send(packet);
						Thread.sleep(10000);
						System.out.println("passati i 10 sec");
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
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
		try {
		final DatagramSocket sock = new DatagramSocket(UDP_PORT);
		
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				

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
		
	} catch (SocketException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	}
	
	
	public void handlerTCP() {

		try {
			serv = new ServerSocket(TCP_PORT);

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
															new DeviceInfo(System.currentTimeMillis())); // aggiunge uno nuovo
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
