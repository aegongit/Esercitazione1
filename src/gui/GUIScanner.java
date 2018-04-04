package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import device.Device;
import manager.DeviceInfo;
import manager.Manager;

import java.awt.Font;
import javax.swing.JSeparator;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class GUIScanner {

	private JFrame frmIpScanner;
	private LinkedList<JLabel> lblNewLabel;
	private HashMap<String,JLabel> mapSec;
	private Manager manager;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIScanner window = new GUIScanner();
					window.frmIpScanner.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUIScanner() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmIpScanner = new JFrame();
		frmIpScanner.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				manager.shutdown();
				System.exit(0);
			}
		});
		frmIpScanner.setTitle("Ip Scanner");
		frmIpScanner.setResizable(false);
		lblNewLabel = new LinkedList<JLabel>();
		mapSec = new HashMap<String,JLabel>();
		frmIpScanner.setBounds(100, 100, 376, 536);
		frmIpScanner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmIpScanner.getContentPane().setLayout(null);
		manager  = new Manager();
		manager.scanNetwork();
		manager.handleResponseUDP();
		manager.handlerTCP();
		JButton btnNewButton = new JButton("Stato");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				Iterator i =  Manager.set.keySet().iterator();
				int count = 0;
				
				
				while(i.hasNext()) {
					count ++;
					String key = (String) i.next();
					DeviceInfo device = Manager.set.get(key);
					JLabel  l = find(key);
					if( l != null) {
						lblNewLabel.removeFirstOccurrence(l);
						JLabel tmpLabel = mapSec.remove(l.getText());
						frmIpScanner.getContentPane().remove(tmpLabel);
						frmIpScanner.getContentPane().remove(l);
						
					}
					
					JLabel tmpLabel = new JLabel(key);
					
					lblNewLabel.add(tmpLabel);
					System.out.println("Last update: "+(System.currentTimeMillis()-device.getLast_update()));
					if(device.isAlive() && !device.isExpired())
						tmpLabel.setForeground(Color.BLUE);
					else {
						tmpLabel.setForeground(Color.GRAY);
						System.out.println(tmpLabel.getText());
					
					}
					String timeLast = String.valueOf((System.currentTimeMillis()-device.getLast_update())/1000);
					JLabel tmpLabelLast = new JLabel(timeLast+" sec");
					mapSec.put(tmpLabel.getText(),tmpLabelLast );
					tmpLabel.setBounds(23, 60+(count*10), 104, 20+(count*10));
					
					tmpLabelLast.setBounds(127,60+(count*10),200,20+(count*10));
					frmIpScanner.getContentPane().add(tmpLabelLast);
					frmIpScanner.getContentPane().add(tmpLabel);
					
					frmIpScanner.repaint();
					
					
				}
				
			}
			
		});
		btnNewButton.setBounds(232, 11, 89, 23);
		frmIpScanner.getContentPane().add(btnNewButton);
		
		JButton btnScan = new JButton("Scan");
		btnScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				manager.scanNetwork();
				
				//btnScan.setEnabled(false);
				
			}
		});
		btnScan.setBounds(46, 11, 89, 23);
		frmIpScanner.getContentPane().add(btnScan);
		
		JLabel lblNewLabel_1 = new JLabel("Ip devices");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(10, 54, 89, 14);
		frmIpScanner.getContentPane().add(lblNewLabel_1);
		
		JLabel lblAlive = new JLabel("Disconneted");
		lblAlive.setBounds(248, 482, 102, 14);
		frmIpScanner.getContentPane().add(lblAlive);
		lblAlive.setForeground(Color.GRAY);
		lblAlive.setBackground(Color.BLUE);
		lblAlive.setHorizontalAlignment(SwingConstants.LEFT);
		
		JLabel lblConnected = new JLabel("Connected");
		lblConnected.setHorizontalAlignment(SwingConstants.LEFT);
		lblConnected.setBounds(175, 482, 57, 14);
		frmIpScanner.getContentPane().add(lblConnected);
		lblConnected.setForeground(Color.BLUE);
		
		JLabel label = new JLabel("/");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(231, 482, 14, 14);
		frmIpScanner.getContentPane().add(label);
		
		JLabel lblLastAlive = new JLabel("Last ALIVE");
		lblLastAlive.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
		lblLastAlive.setBounds(131, 54, 95, 14);
		frmIpScanner.getContentPane().add(lblLastAlive);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 37, 47, -3);
		frmIpScanner.getContentPane().add(separator);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 45, 340, 14);
		frmIpScanner.getContentPane().add(separator_1);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(10, 71, 340, 11);
		frmIpScanner.getContentPane().add(separator_2);
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setOrientation(SwingConstants.VERTICAL);
		separator_3.setBounds(122, 48, 2, 420);
		frmIpScanner.getContentPane().add(separator_3);
		
		JSeparator separator_4 = new JSeparator();
		separator_4.setBounds(10, 467, 350, 2);
		frmIpScanner.getContentPane().add(separator_4);
		
		
		Runnable refreshStatus = new Runnable() {

			@Override
			public void run() {
				while(true) {
					Iterator i =  Manager.set.keySet().iterator();
					int count = 0;
					
					
					while(i.hasNext()) {
						count ++;
						String key = (String) i.next();
						DeviceInfo device = Manager.set.get(key);
						JLabel  l = find(key);
						if( l != null) {
							lblNewLabel.removeFirstOccurrence(l);
							JLabel tmpLabel = mapSec.remove(l.getText());
							frmIpScanner.getContentPane().remove(tmpLabel);
							frmIpScanner.getContentPane().remove(l);
							
						}
						
						JLabel tmpLabel = new JLabel(key);
						
						lblNewLabel.add(tmpLabel);
						System.out.println("Last update: "+(System.currentTimeMillis()-device.getLast_update()));
						if(device.isAlive() && !device.isExpired())
							tmpLabel.setForeground(Color.BLUE);
						else {
							tmpLabel.setForeground(Color.GRAY);
							System.out.println(tmpLabel.getText());
						
						}
						String timeLast = String.valueOf((System.currentTimeMillis()-device.getLast_update())/1000);
						JLabel tmpLabelLast = new JLabel(timeLast+" sec");
						mapSec.put(tmpLabel.getText(),tmpLabelLast );
						tmpLabel.setBounds(23, 60+(count*10), 104, 20+(count*10));
						
						tmpLabelLast.setBounds(127,60+(count*10),200,20+(count*10));
						frmIpScanner.getContentPane().add(tmpLabelLast);
						frmIpScanner.getContentPane().add(tmpLabel);
						
						frmIpScanner.repaint();
						
						
					}
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				
			}};
			
			Thread refreshStatusT = new Thread(refreshStatus);
			refreshStatusT.start();
		
		
		
	}
	
	
	public JLabel find(String l) {
		Iterator<JLabel> i = lblNewLabel.iterator();
		while(i.hasNext()) {
			JLabel tmpLabel = i.next();
			if(tmpLabel.getText().equals(l))
				return tmpLabel;
		}
		return null;
	}
}
