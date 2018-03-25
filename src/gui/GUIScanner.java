package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ListSelectionModel;

import scanner.Manager;
import scanner.Stato;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;


import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;

public class GUIScanner {

	private JFrame frame;
	LinkedList<JLabel> lblNewLabel;

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
		
		JButton btnNewButton = new JButton("Stato");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Manager m = new Manager();
				Iterator i =  Manager.set.keySet().iterator();
				int count = 0;
				
				
				while(i.hasNext()) {
					count ++;
					String key = (String) i.next();
					Stato b = Manager.set.get(key);
					JLabel  l = find(key);
					if( l != null) {
						lblNewLabel.removeFirstOccurrence(l);
						frame.getContentPane().remove(l);
						System.out.println("del");
					}
					
					JLabel tmpLabel = new JLabel(key);
					lblNewLabel.add(tmpLabel);
					System.out.println("Alive: "+b.isAlive());
					if(b.isConnected())
						tmpLabel.setForeground(Color.GREEN);
					else if(b.isAlive())
						tmpLabel.setForeground(Color.BLUE);
					else {
						tmpLabel.setForeground(Color.GRAY);
						System.out.println(tmpLabel.getText());
					
					}
					
					tmpLabel.setBounds(23, 31+(count*10), 104, 20+(count*10));
					frame.getContentPane().add(tmpLabel);
					
					frame.repaint();
					
					
				}
				
			}
			
		});
		btnNewButton.setBounds(192, 11, 89, 23);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnScan = new JButton("Scan");
		btnScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Manager manager = new Manager();
				manager.scanNetwork();
				manager.handleResponseUDP();
			}
		});
		btnScan.setBounds(35, 11, 89, 23);
		frame.getContentPane().add(btnScan);
		
		
		
		
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
