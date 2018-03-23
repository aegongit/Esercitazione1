package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JList;
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

import javax.swing.JLabel;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUIScanner {

	private JFrame frame;

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
		
		frame.setBounds(100, 100, 376, 504);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("Stato");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Manager m = new Manager();
				Iterator i =  Manager.set.keySet().iterator();
				int count = 0;
				LinkedList<JLabel> lblNewLabel = new LinkedList<JLabel>();
				while(i.hasNext()) {
					count ++;
					String key = (String) i.next();
					Stato b = Manager.set.get(key);
					
					
					lblNewLabel.add(new JLabel(key));
					if(b.getConnected())
						lblNewLabel.peekLast().setForeground(Color.GREEN);
					else if(b.getAlive())
						lblNewLabel.peekLast().setForeground(Color.blue);
					
					lblNewLabel.getLast().setBounds(23, 31+(count*10), 104, 20+(count*10));
					frame.getContentPane().add(lblNewLabel.getLast());
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
}
