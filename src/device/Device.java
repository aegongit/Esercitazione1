package device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private final int TIMEOUT = 10000;
    public final String MSG_DEVICE_UDP = "I'M HERE";
    public final String MSG_DEVICE_TCP = "STILL ALIVE";
    

    private Socket sockTCP = null;
    private InetAddress ipManager = null;

    /**
     * Il metodo deviceHandler UDP si occupa di ricevere il messaggio UDP da parte del Manager.
     * In particolare il dispositivo si registra all'indirizzo di Multicast e viene lanciato un nuovo thread
     * che sarà in attesa del messaggio UDP 'SCAN' del Manager. Ricevuto il messaggio, il device risponde a sua volta con il messaggio UDP "I'M HERE".
     */
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
                            String msg = MSG_DEVICE_UDP;
                            byte [] mess  = new byte[MAX];
                            DatagramPacket packet = new DatagramPacket(mess,mess.length);
                            System.out.println("Waiting for receive...");
                            multiCastSock.receive(packet);
                            System.out.println("Ricevuti: "+packet.getData().toString()+" byte");
                            ipManager = packet.getAddress();
                            sendDatagram(ipManager, msg);
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
        }

    }
    
    /**
     * Il metodo deviceHandlerTCP si occupa di creare la connessione TCP con il Manager.
     * All'interno del thread il device invia periodicamente il messaggio STILL_ALIVE al Manager
     * Riceve il messaggio OK del Manager che lo informa del fatto che il Manager è ancora online,infatti qualora il cliente 
     * non ricevesse nulla da parte del manager per un certo intervallo di tempo definito da TIMEOUT si cosidererà
     * il manager morto.
     */
    public void deviceHandlerTCP() {
    	
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						
						if (ipManager != null) {
							// Inizio connessione TCP
							// Creazione Socket
							String ipServer = ipManager.getHostAddress();

							if (sockTCP == null) {
								sockTCP = new Socket(ipServer, TCP_PORT);
								sockTCP.setSoTimeout(TIMEOUT);
							}

							// Inviamo la stringa, usando un PrintWriter
							OutputStream os = sockTCP.getOutputStream();
							Writer wr = new OutputStreamWriter(os, "UTF-8");
							PrintWriter prw = new PrintWriter(wr);
							prw.println(MSG_DEVICE_TCP);
							prw.flush();

							System.out.println("TCP Packet (Alive) sent to:" + sockTCP.getInetAddress());
							
							BufferedReader brd = new BufferedReader(new InputStreamReader(sockTCP.getInputStream(),"UTF-8"));
                            String s = brd.readLine();
                            System.out.println("Response from the manager: "+s);

						}

					} catch (IOException e) {
						System.out.println(e.getMessage());
						try {
							sockTCP.close();
							sockTCP = null;
							ipManager = null;
						} catch (IOException e1) {
							System.out.println(e1.getMessage());
						}

					}
				}

			}
		};

		Thread tTCP = new Thread(runnable);
		tTCP.start();
    	
   
    	
    }
    
    /**
     * Metodo per l'invio di un datagramma UDP
     * @param ipManager ip del destinatario
     * @param msg Messaggio da inoltrare
     * @throws IOException
     */
    private void sendDatagram(InetAddress ipManager, String msg) throws IOException {
    	DatagramSocket datagramSocket = new DatagramSocket();
        datagramSocket.connect(ipManager, UDP_PORT);
        DatagramPacket toSend = new DatagramPacket(msg.getBytes(), msg.length(),ipManager, UDP_PORT);
        datagramSocket.send(toSend);
        datagramSocket.close();
    	
    }

	public static void main(String[] s) {
		Device d = new Device();
		d.deviceHandlerUDP();
		d.deviceHandlerTCP();
	}

}
