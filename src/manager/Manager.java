package manager;
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
    public static final String MULTICAST_ADDRESS  = "224.0.0.1";
    public final int UDP_PORT = 7777;
    public final int TCP_PORT = 7778;
    public final int MAX = 65507;


    private ServerSocket serv;

    public Manager() {
        if(set == null) {
            set  = new HashMap<>();
        }
    }

	public void scanNetwork() {
		MulticastSocket sock = null;
		try {

			sock = new MulticastSocket();

			byte[] mess = { 'S', 'C', 'A', 'N' };
			InetAddress addr = InetAddress.getByName(MULTICAST_ADDRESS);

			DatagramPacket packet = new DatagramPacket(mess, mess.length, addr, UDP_PORT);

			sock.send(packet);

		} catch (UnknownHostException e1) {
			System.out.println("scanNetwork --- unknown host exception");
			e1.printStackTrace();

		} catch (IOException e) {
			// System.out.println("scanNetwork --- send IO except");
			System.out.println(e.getMessage());
			e.printStackTrace();

		} finally {
			sock.close();
		}

	}

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
							// TODO Auto-generated catch block
							System.out.println("handleResponseUDP --- receive except");
							e.printStackTrace();
							sock.close();
						}
					}
				}
			};

			Thread t = new Thread(runnable);
			t.start();

		} catch (SocketException e1) {
			System.out.println("handleResponseUDP --- socket except");
			e1.printStackTrace();
		}
	}


    public void handlerTCP() {
		try {
			serv = new ServerSocket(TCP_PORT);

			Runnable runnableTCP = new Runnable() {

				@Override
				public void run() {
					while (true) {
						try {
							Socket sock = serv.accept();

							Runnable server = new Runnable() {
								// Questo thread interno sarebbe il thread delegato alla gestione di 1 singolo
								// client?
								@Override
								public void run() {
									while (true) {
										try {
                                            BufferedReader brd = new BufferedReader(new InputStreamReader(sock.getInputStream(),"UTF-8"));
                                            String s = brd.readLine();
                                            
                                            
                                            System.out.println("Risposta :"+s);
                                            if(!s.isEmpty() && s.equals("I'm Alive")){ //aggiunto controllo isEmpty
                                                synchronized (set) {
                                                        if (Manager.set.containsKey(sock.getInetAddress().toString()))
                                                        {
                                                            System.out.println("Aggiorna");
                                                            Manager.set.get(sock.getInetAddress().toString()).setLast_update(System.currentTimeMillis());                                                             Manager.set.get(sock.getInetAddress().toString()).setAlive(true);
                                                        }
													else
														Manager.set.put(sock.getInetAddress().toString(),new DeviceInfo(System.currentTimeMillis())); // aggiunge uno nuovo
													Manager.set.notifyAll();
												}
											}
										} catch (Exception exc) {
											System.out.println(
													"handleTCP --- IO except lettura risposta " + exc.getMessage());

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
							System.out.println("handleTCP --- IO except (runnable server)");
						}
					}
				}
			};
			Thread threadTCP = new Thread(runnableTCP);
			threadTCP.start();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("handleTCP --- IO except (runnable TCP)");
		}
	}
    
    public void shutdown() {
        try {
        	serv.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.scanNetwork();
        manager.handleResponseUDP();
        manager.handlerTCP();
    }
}
