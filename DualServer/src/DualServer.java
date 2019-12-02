import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class DualServer {
	
	private static boolean running = true;
	static DualServer dualServer;
	public static ArrayList<Server> clients = new ArrayList<Server>();
	private static JFrame frame;
	private static JTextArea plc, ur;
	private static JTable tab;
	private static DefaultTableModel tabModel = new DefaultTableModel();
	
	public DualServer() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame = new JFrame("Project 5");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
				frame.setVisible(true);
				frame.setLayout(new BorderLayout());
				plc = new JTextArea();
				ur = new JTextArea();
				plc.setPreferredSize(new Dimension(680, 0));
				plc.setEditable(false);
				plc.setFont(new Font(null, Font.PLAIN, 16));
				ur.setPreferredSize(new Dimension(680, 0));
				ur.setEditable(false);
				ur.setFont(new Font(null, Font.PLAIN, 16));
				tab = new JTable(tabModel);
				tabModel.addColumn("ID");
				tabModel.addColumn("Time");
				tabModel.addColumn("Empty");
				tabModel.addColumn("Ready");
				tabModel.addColumn("MPN");
				JScrollPane scr1 = new JScrollPane(plc);
				JScrollPane scr2 = new JScrollPane(ur);
				JScrollPane scr3 = new JScrollPane(tab);
				frame.add(scr1, BorderLayout.WEST);
				frame.add(scr2, BorderLayout.CENTER);
				frame.add(scr3, BorderLayout.EAST);
				new Thread(new ServerPLC("172.20.66.65", 2565)).start();
				new Thread(new ServerUR("192.168.1.137", 2566)).start();
			}
		});
	}
	
	public static DualServer getDualServer() {
		return dualServer;
	}
	
	public static void updateTable() {
		while(Carrier.list.size() != tabModel.getRowCount()) tabModel.addRow(new Object[] {});
		for(int i = 0; i < Carrier.list.size(); i++){
			tabModel.setValueAt(Carrier.list.get(i).getId(), i, 0);
			tabModel.setValueAt(((double)Carrier.list.get(i).getTime())/1000, i, 1);
			tabModel.setValueAt(Carrier.list.get(i).isEmpty(), i, 2);
			tabModel.setValueAt(Carrier.list.get(i).isReady(), i, 3);
			tabModel.setValueAt(Carrier.list.get(i).getMPN(), i, 4);
		}
	}
	
	//public static void add
	
	public static void addPLCMessage(String msg) {
		plc.append("["+ System.currentTimeMillis() + "] "+ msg +"\n");
	}
	
	public static void addURMessage(String msg) {
		ur.append("["+ System.currentTimeMillis() + "] "+ msg +"\n");
	}
	
	public synchronized static boolean isRunning() {
		return running;
	} 
	
	public synchronized static void stop() {
		running = false;
	}
	
	public static void main(String[] args) {
		dualServer = new DualServer();
	}

}
