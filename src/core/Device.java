package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Device {


	private final String multicastAddress = "224.0.0.1";
	private final int PORT = 7777;
	private final int PORTTCP = 7778;
	private final int MAX = 65507;
	
	private Socket sockTCP = null;
	
	
	private final static long EXPIRE = 60000; //un minuto
	
	private long last_update ;
	private boolean alive;
	
	
	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public Device() {
		last_update= 0;
		this.alive=false;
		
	}
	
	public Device(long t) {
		this.last_update = t;
		this.alive = true;
	}
	

	
	public long getLast_update() {
		return last_update;
	}



	public void setLast_update(long last_update) {
		this.last_update = last_update;
	}
	
	/**
	 * Metodo  che  verifica  se il dispositivo è alive
	 * @return true/false rispettivamnete alive/not alive
	 */
	public Boolean isExpired() {
		
		if ((System.currentTimeMillis() - this.last_update)>=Device.EXPIRE) {
			
			return true;
		}
		
		
		return false;
	}
	
	
	public boolean isAlive() {
		return this.alive;
	}



	public void handlerUDP() {
		try {
			final MulticastSocket sock = new MulticastSocket(PORT);
			InetAddress addr = InetAddress.getByName(multicastAddress);
			sock.joinGroup(addr);




			Runnable runnable = new Runnable() {

				@Override
				public void run() {

					while(true) {
						try {	
							String msg = "Hello";
							byte [] mess  = new byte[MAX];
							DatagramPacket packet = new DatagramPacket(mess,mess.length);
							System.out.println("Before receive");

							sock.receive(packet);

							System.out.println("Ricevuti: "+packet.getData().toString()+" byte");
							DatagramSocket socket = new DatagramSocket();
							InetAddress addr1 = packet.getAddress();
							socket.connect(addr1, PORT);
							DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),addr1, PORT);
							socket.send(hi);
							
							//connessione TCP 
							
							// Innanzitutto creiamo la socket
							
							String ipServer = addr1.getHostAddress();
							if (sockTCP == null)
								sockTCP = new Socket(ipServer,PORTTCP);

							
							// Inviamo la stringa, usando un PrintWriter
							OutputStream os = sockTCP.getOutputStream();
							Writer wr = new OutputStreamWriter(os,"UTF-8");
							PrintWriter prw = new PrintWriter(wr);
							prw.println("ALIVE");
							prw.flush();
							
							
							System.out.println("Inviato");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							try {
								sockTCP.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							sock.close();
						}
					}




				}


			};

			Thread t = new Thread(runnable);
			t.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//sock.leaveGroup(addr);
			//sock.close();
		}

		
	}
	
	public static void  main(String [] s) {
		
		Device d= new Device();
		d.handlerUDP();
		

	}

}
