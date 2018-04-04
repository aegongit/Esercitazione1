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

//Mettere tutto in un packet?
//Capire dove bisogna fare close
//Eliminare == null nel costruttore?

public class Manager {
    public static  Map<String,DeviceInfo> set;
    public static final String MULTICAST_ADDRESS  = "224.0.0.2";
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
        //Si potrebbe splittare questo metodo e fare l'init della socket in un metodo
        //e invio del messaggio in un altro metodo.
        //Così poi dall GUI chiamiamo solo l'invio del messaggio 
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                MulticastSocket sock = null;
                try {
                    sock = new MulticastSocket();
                } catch (IOException e1) {
                    System.out.println("scanNetwork --- Socket exception");
                    e1.printStackTrace();
                    sock.close();
                }

                // TODO Auto-generated method stub
                byte[] mess = {'S', 'C', 'A', 'N'};
                InetAddress addr = null;
                try {
                    addr = InetAddress.getByName(MULTICAST_ADDRESS);
                } catch (UnknownHostException e1) {                       
                    System.out.println("scanNetwork --- unknown host exception");
                    e1.printStackTrace();
                }

                DatagramPacket packet=new DatagramPacket(mess, mess.length, addr, UDP_PORT);

                while (true) {
                    try {
                        sock.send(packet);
                        Thread.sleep(10000);
                        System.out.println("passati i 10 sec");
                    } catch (IOException e) {
                        System.out.println("scanNetwork --- send IO except");
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        System.out.println("scanNetwork --- send except");
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
                    byte[] mess = new byte[MAX];
                    DatagramPacket packet=new DatagramPacket(mess, mess.length);

                    while (true) {
                        try {
                            sock.receive(packet);
                            System.out.println("UDP Packet Received from : "+packet.getAddress().toString());
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
            // TODO Auto-generated catch block
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
                    while(true) {
                        try {
                            Socket sock = serv.accept();

                            Runnable server = new Runnable() {
                                //Questo thread interno sarebbe il thread delegato alla gestione di 1 singolo client?
                                @Override
                                public void run() {
                                    while(true) {
                                        try {
                                            BufferedReader brd = new BufferedReader(new InputStreamReader(sock.getInputStream(),"UTF-8"));
                                            String s = brd.readLine();
                                            //if(s==null)
                                            	 //Manager.set.get(sock.getInetAddress().toString()).setAlive(false); // aggiorna solo l'attributo alive
                                            
                                            System.out.println("Risposta :"+s);
                                            if(!s.isEmpty() && s.equals("I'm Alive")){ //aggiunto controllo isEmpty
                                                synchronized (set) {
                                                        if (Manager.set.containsKey(sock.getInetAddress().toString()))
                                                        {
                                                            System.out.println("Aggiorna");
                                                            Manager.set.get(sock.getInetAddress().toString()).setLast_update(System.currentTimeMillis()); // aggiorna solo il ttl
                                                            Manager.set.get(sock.getInetAddress().toString()).setAlive(true);
                                                        }
                                                        else
                                                            Manager.set.put(sock.getInetAddress().toString(),
                                                            new DeviceInfo(System.currentTimeMillis())); // aggiunge uno nuovo
                                                        Manager.set.notifyAll();
                                                }
                                            }
                                        }catch(IOException exc) {
                                            System.out.println("handleTCP --- IO except lettura risposta "+exc.getMessage());
                                            exc.printStackTrace();
                                           // Manager.set.get(sock.getInetAddress().toString()).setAlive(false); // aggiorna solo l'attributo alive
                                            try {sock.close();}
                                            catch(IOException exc2) {}
                                            break;
                                        }finally {
                                                //System.out.println("Connessione tcp chisa");
                                                
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

    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.scanNetwork();
        manager.handleResponseUDP();
        manager.handlerTCP();
    }
}
