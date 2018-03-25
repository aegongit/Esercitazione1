package scanner;
import java.awt.List;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class Manager {
	public static  Map<String,Stato> set;
	public static final String multicastAddress  = "224.0.0.2";
	public final int PORT = 7777;
	
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
						if(Manager.set != null)
							Manager.set.put(packet.getAddress().toString(), new Stato(true,false));
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
	
	public void statusNetwork() {
		
	}
	
	public static void main(String[] args) {
		Manager manager = new Manager();
		manager.scanNetwork();
		manager.handleResponseUDP();
	}
}
