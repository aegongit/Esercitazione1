package device;

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

    private final String MULTICASTADDRESS = "224.0.0.2";
    private final int UDP_PORT = 7777;
    private final int TCP_PORT = 7778;
    private final int MAX = 65507;

    private Socket sockTCP = null;
    private InetAddress ipManager = null;

    
    public void deviceHandlerUDP() {
        try {
            final MulticastSocket multiCastSock = new MulticastSocket(UDP_PORT);
            InetAddress addrGroup = InetAddress.getByName(MULTICASTADDRESS);
            multiCastSock.joinGroup(addrGroup);

            Runnable runnable = new Runnable() {

                @Override
                public void run() {

                    while(true) {
                        try {	
                            String msg = "I'm Here";
                            byte [] mess  = new byte[MAX];
                            DatagramPacket packet = new DatagramPacket(mess,mess.length);
                            System.out.println("Waiting for receive...");

                            multiCastSock.receive(packet);

                            System.out.println("Ricevuti: "+packet.getData().toString()+" byte");
                            
                            DatagramSocket datagramSocket = new DatagramSocket();
                            ipManager = packet.getAddress();
                            datagramSocket.connect(ipManager, UDP_PORT);
                            DatagramPacket toSend = new DatagramPacket(msg.getBytes(), msg.length(),ipManager, UDP_PORT);
                            datagramSocket.send(toSend);
                            datagramSocket.close();
                     
                         
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                            multiCastSock.close();
                        }
                    }
                }
            };
            Thread tUDP = new Thread(runnable);
            tUDP.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("DeviceHandler --- IOExcept");
            
        }

    }
    
    public void deviceHandlerTCP() {
    	
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						System.out.println("********************** " + ipManager);
						if (ipManager != null) {
							// Inizio connessione TCP
							// Creazione Socket
							String ipServer = ipManager.getHostAddress();

							if (sockTCP == null)
								sockTCP = new Socket(ipServer, TCP_PORT);

							// Inviamo la stringa, usando un PrintWriter
							OutputStream os = sockTCP.getOutputStream();
							Writer wr = new OutputStreamWriter(os, "UTF-8");
							PrintWriter prw = new PrintWriter(wr);
							prw.println("I'm Alive");
							prw.flush();

							System.out.println("TCP Packet (Alive) sent to:" + sockTCP.getInetAddress());

						}

					} catch (IOException e) {
						System.out.println(e.getMessage());
						System.out.println("DeviceHandler --- IOExcept (TCP thread)");
						try {
							sockTCP.close();
							sockTCP = null;
							ipManager = null;
						} catch (IOException e1) {
							System.out.println(e1.getMessage());
							System.out.println("DeviceHandler --- IOExcept (TCP sock close)");
						}

					}
				}

			}
		};

		Thread tTCP = new Thread(runnable);
		tTCP.start();
    	
   
    	
    }

	public static void main(String[] s) {
		Device d = new Device();
		d.deviceHandlerUDP();
		d.deviceHandlerTCP();
	}

}
