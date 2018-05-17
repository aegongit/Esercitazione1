package manager;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;



public class Manager {
    public static  Map<String,DeviceInfo> setDevices;
    public static final String MULTICAST_ADDRESS  = "224.0.0.1";
    public final int UDP_PORT = 7777;
    public final int TCP_PORT = 7778;
    public final int MAX = 65507;
    public final String MSG_DEVICE_TCP = "STILL ALIVE";
    public final String MSG_MANAGER_TCP = "OK";


    private ServerSocket serverSocket;


    public Manager() {
        if(setDevices == null) {
            setDevices  = new HashMap<>();
            
        }
    }

    /**
     * Il metodo scanNetwork effettua la scansione della rete inviando tramite il protocollo UDP 
     * il messaggio 'SCAN' a tutti i device registrati all'indirizzo MULTICAST_ADDRESS
     */
	public void scanNetwork() {
		MulticastSocket sockScan = null;
		try {

			sockScan = new MulticastSocket();

			byte[] mess = { 'S', 'C', 'A', 'N' };
			InetAddress addr = InetAddress.getByName(MULTICAST_ADDRESS);

			DatagramPacket packet = new DatagramPacket(mess, mess.length, addr, UDP_PORT);

			sockScan.send(packet);

		} catch (UnknownHostException e1) {
			e1.printStackTrace();

		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

		} finally {
			sockScan.close();
		}

	}

	/**
	 * Il metodo handleResponseUDP si occupa di ricevere le risposte UDP da parte dei device.
	 * In particolare viene lanciato un nuovo thread per far sì che il manager sia in continua attesa
	 * delle risposte UDP dei device.
	 */
	public void handleResponseUDP() {
		try {
			final DatagramSocket sock = new DatagramSocket(UDP_PORT);

			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					byte[] mess = new byte[MAX];
					DatagramPacket packet = new DatagramPacket(mess, mess.length);

					while (true) {
						try {
							sock.receive(packet);
							System.out.println("UDP Packet Received from : " + packet.getAddress().toString());
						} catch (IOException e) {
							e.printStackTrace();
							sock.close();
						}
					}
				}
			};

			Thread t = new Thread(runnable);
			t.start();

		} catch (SocketException e1) {
			e1.printStackTrace();
		}
	}


	/**
	 * Il metodo handlerTCP lancia un primo thread che sarà in continua attesa di richieste di connessioni TCP
	 * da parte dei device. Dopodichè per ogni device che invia una richiesta di connessione TCP, viene lanciato un nuovo thread
	 * che si occuperà di gestire la specifica connessione.
	 * Lo specifico thread è in attesa del messaggio periodico STILL_ALIVE da parte del dispositivo.
	 * In corrispondenza dell'arrivo del messaggio TCP dal device viene effettuato l'accesso sincronizzato alla HashMap setDevices
	 * per aggiungere il dispositivo alla map o per aggiornare il campo LastUpdate se il dispositivo è già presente.
	 * Inoltre è stato aggiunto il messaggio MSG_MANAGER_TCP che viene inviato dal Manager al device
	 * per comunicare che il Manager è ancora online.
	 */
    public void handlerTCP() {
		try {
			serverSocket = new ServerSocket(TCP_PORT);

			Runnable runnableTCP = new Runnable() {

				@Override
				public void run() {
					while (true) {
						try {
							Socket sock = serverSocket.accept();
							

							Runnable server = new Runnable() {
								@Override
								public void run() {
									while (true) {
										try {
                                            BufferedReader brd = new BufferedReader(new InputStreamReader(sock.getInputStream(),"UTF-8"));
                                            String s = brd.readLine();
                                            
                                            
                                            System.out.println("Risposta :"+s);
                                            if(!s.isEmpty() && s.equals(MSG_DEVICE_TCP)){
                                                synchronized (setDevices) {
                                                        if (Manager.setDevices.containsKey(sock.getInetAddress().toString()))
                                                        {
                                                            Manager.setDevices.get(sock.getInetAddress().toString()).setLast_update(System.currentTimeMillis());                                                             Manager.setDevices.get(sock.getInetAddress().toString()).setAlive(true);
                                                        }
													else
														Manager.setDevices.put(sock.getInetAddress().toString(),new DeviceInfo(System.currentTimeMillis())); // aggiunge uno nuovo
													Manager.setDevices.notifyAll();
												}
                                            }
                                            // Inviamo la stringa, usando un PrintWriter
                                            OutputStream os = sock.getOutputStream();
                                            Writer wr = new OutputStreamWriter(os, "UTF-8");
                                            PrintWriter prw = new PrintWriter(wr);
                                            prw.println(MSG_MANAGER_TCP);
                                            prw.flush();
										} catch (IOException exc) {
											try {
												sock.close();
											} catch (IOException exc2) {
												exc2.printStackTrace();
											}

											exc.printStackTrace();
											break;
                                        }
                                    }
                                }
							};
							Thread threadServer = new Thread(server);
							threadServer.start();
						} catch (IOException e) {
							System.out.println(e.getMessage());
							
						}
					}
				}
			};
			Thread threadTCP = new Thread(runnableTCP);
			threadTCP.start();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
    
    /**
     * Metodo che si occupa di chiudere la socket del server alla chiusura della finestra  GUI
     */
    public void shutdown() {
        try {
        	
        	serverSocket.close();
        	
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.scanNetwork();
        manager.handleResponseUDP();
        manager.handlerTCP();
    }
}
