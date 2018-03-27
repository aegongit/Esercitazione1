package core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class Device {


	private final String MULTICASTADDRESS = "224.0.0.1";
	private final int UDP_PORT = 7777;
	private final int TCP_PORT = 7778;
	private final int MAX = 65507;
	
	private Socket sockTCP = null;
	
	
	
	
	

	
	
	

	




	public void deviceHandler() {
		try {
			final MulticastSocket sock = new MulticastSocket(UDP_PORT);
			InetAddress addr = InetAddress.getByName(MULTICASTADDRESS);
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
							socket.connect(addr1, UDP_PORT);
							DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),addr1, UDP_PORT);
							socket.send(hi);
							socket.close();
							//connessione TCP 
							
							// Innanzitutto creiamo la socket
							
							String ipServer = addr1.getHostAddress();
							if (sockTCP == null)
								sockTCP = new Socket(ipServer,TCP_PORT);

							
							// Inviamo la stringa, usando un PrintWriter
							OutputStream os = sockTCP.getOutputStream();
							Writer wr = new OutputStreamWriter(os,"UTF-8");
							PrintWriter prw = new PrintWriter(wr);
							prw.println("ALIVE");
							prw.flush();
							
							
							System.out.println("Packet UDP ALIVE sended to:"+sockTCP.getInetAddress());
						} catch (IOException e) {
							System.out.println(e.getMessage());
							try {
								sockTCP.close();
							} catch (IOException e1) {
								System.out.println(e1.getMessage());
							}
							sock.close();
						}
					}

				}

			};

			Thread t = new Thread(runnable);
			t.start();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			//sock.leaveGroup(addr);
			//sock.close();
		}

		
	}
	
	public static void  main(String [] s) {
		
		Device d= new Device();
		d.deviceHandler();
		

	}

}
