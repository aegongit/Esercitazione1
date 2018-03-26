package core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Device {


	private final String multicastAddress = "224.0.0.1";
	private final int PORT = 7777;
	private final int MAX = 65507;
	
	public Device() {
		handlerUDP();
		//handlerTcp();
	}
	
	
	private void handlerUDP() {
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
							System.out.println("Inviato");
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//sock.leaveGroup(addr);
			//sock.close();
		}

		
	}
	
	public static void  main(String [] s) {
		
		Device d= new Device();
		

	}

}
