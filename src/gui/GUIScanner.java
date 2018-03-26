package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import core.Device;
import core.Manager;


public class GUIScanner {

	private JFrame frame;
	private LinkedList<JLabel> lblNewLabel;
	private Manager manager;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIScanner window = new GUIScanner();
					window.frame.setVisible(true);
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
		frame = new JFrame();
		lblNewLabel = new LinkedList<JLabel>();
		frame.setBounds(100, 100, 376, 504);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		manager  = new Manager();
		
		JButton btnNewButton = new JButton("Stato");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				Iterator i =  Manager.set.keySet().iterator();
				int count = 0;
				
				
				while(i.hasNext()) {
					count ++;
					String key = (String) i.next();
					Device device = Manager.set.get(key);
					JLabel  l = find(key);
					if( l != null) {
						lblNewLabel.removeFirstOccurrence(l);
						frame.getContentPane().remove(l);
						
					}
					
					JLabel tmpLabel = new JLabel(key);
					lblNewLabel.add(tmpLabel);
					System.out.println("Last update: "+(System.currentTimeMillis()-device.getLast_update()));
					if(device.isAlive() && !device.isExpired())
						tmpLabel.setForeground(Color.GREEN);
					else {
						tmpLabel.setForeground(Color.GRAY);
						System.out.println(tmpLabel.getText());
					
					}
					
					tmpLabel.setBounds(23, 60+(count*10), 104, 20+(count*10));
					frame.getContentPane().add(tmpLabel);
					
					frame.repaint();
					
					
				}
				
			}
			
		});
		btnNewButton.setBounds(232, 11, 89, 23);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnScan = new JButton("Scan");
		btnScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				manager.scanNetwork();
				manager.handleResponseUDP();
				manager.handlerTCP();
				btnScan.setEnabled(false);
				
			}
		});
		btnScan.setBounds(46, 11, 89, 23);
		frame.getContentPane().add(btnScan);
		
		JLabel lblNewLabel_1 = new JLabel("Ip devices");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(10, 54, 89, 14);
		frame.getContentPane().add(lblNewLabel_1);
		
		JLabel lblAlive = new JLabel("Disconneted");
		lblAlive.setBounds(154, 54, 102, 14);
		frame.getContentPane().add(lblAlive);
		lblAlive.setForeground(Color.GRAY);
		lblAlive.setBackground(Color.BLUE);
		lblAlive.setHorizontalAlignment(SwingConstants.LEFT);
		
		JLabel lblConnected = new JLabel("Connected");
		lblConnected.setHorizontalAlignment(SwingConstants.LEFT);
		lblConnected.setBounds(92, 54, 52, 14);
		frame.getContentPane().add(lblConnected);
		lblConnected.setForeground(Color.GREEN);
		
		JLabel label = new JLabel("/");
		label.setBounds(146, 54, 10, 14);
		frame.getContentPane().add(label);
		
		
		Runnable refreshStatus = new Runnable() {

			@Override
			public void run() {
				while(true) {
				Iterator i =  Manager.set.keySet().iterator();
				int count = 0;
				
				
				while(i.hasNext()) {
					count ++;
					String key = (String) i.next();
					Device device = Manager.set.get(key);
					JLabel  l = find(key);
					if( l != null) {
						lblNewLabel.removeFirstOccurrence(l);
						frame.getContentPane().remove(l);

					}

					JLabel tmpLabel = new JLabel(key);
					lblNewLabel.add(tmpLabel);
					System.out.println("Last update: "+(System.currentTimeMillis()-device.getLast_update()));
					if(device.isAlive() && !device.isExpired())
						tmpLabel.setForeground(Color.GREEN);
					else {
						tmpLabel.setForeground(Color.GRAY);
						System.out.println(tmpLabel.getText());

					}

					tmpLabel.setBounds(23, 60+(count*10), 104, 20+(count*10));
					frame.getContentPane().add(tmpLabel);

					frame.repaint();

					
				}
				
				try {
					Thread.sleep(10000);
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
