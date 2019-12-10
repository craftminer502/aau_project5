import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
	private static JTable tab1;
	private static DefaultTableModel tabModel1 = new DefaultTableModel();
	private static JTable tab2;
	private static DefaultTableModel tabModel2 = new DefaultTableModel();
	private static JPanel tablePnale;
	private static BufferedImage img;
	private static ImageIcon icon;
	private static JLabel lbl;
	
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
				plc.setPreferredSize(new Dimension(600, 0));
				plc.setEditable(false);
				plc.setFont(new Font(null, Font.PLAIN, 16));
				ur.setPreferredSize(new Dimension(600, 0));
				ur.setEditable(false);
				ur.setFont(new Font(null, Font.PLAIN, 16));
				tab1 = new JTable(tabModel1);
				tabModel1.addColumn("Carrier ID");
				tabModel1.addColumn("Time");
				tabModel1.addColumn("Empty");
				tabModel1.addColumn("Ready");
				tabModel1.addColumn("MPN");
				tab2 = new JTable(tabModel2);
				tabModel2.addColumn("Carrier ID");
				tabModel2.addColumn("Time");
				//tabModel2.addColumn("Empty");
				//tabModel2.addColumn("Ready");
				tabModel2.addColumn("MPN");
				JScrollPane scr1 = new JScrollPane(plc);
				JScrollPane scr2 = new JScrollPane(ur);
				JScrollPane scr3 = new JScrollPane(tab1);
				JScrollPane scr4 = new JScrollPane(tab2);
				frame.add(scr1, BorderLayout.WEST);
				frame.add(scr2, BorderLayout.CENTER);
				
				tablePnale = new JPanel();
				tablePnale.setLayout(new BorderLayout());
				scr3.setPreferredSize(new Dimension(0, 360));
				scr4.setPreferredSize(new Dimension(0, 360));
				tablePnale.add(scr3, BorderLayout.NORTH);
				tablePnale.add(scr4, BorderLayout.SOUTH);
				//tablePnale.remove(((BorderLayout)tablePnale.getLayout()).getLayoutComponent(BorderLayout.CENTER));
				tablePnale.setPreferredSize(new Dimension(640, 0));
				lbl = new JLabel();
				tablePnale.add(lbl, BorderLayout.CENTER);
				frame.add(tablePnale, BorderLayout.EAST);
				//frame.add(scr3, BorderLayout.EAST);
				new Thread(new ServerPLC("172.20.66.65", 2565)).start();
				new Thread(new ServerUR("192.168.1.137", 2566)).start();
			}
		});
	}
	
	public static DualServer getDualServer() {
		return dualServer;
	}
	
	public static void updateTable() {
		while(Carrier.carrierMap.size() > tabModel1.getRowCount()) {
			tabModel1.addRow(new Object[] {});
		}
		
		while(Carrier.resultList.size() > tabModel2.getRowCount()) {
			tabModel2.addRow(new Object[] {});
		}
		
		for(int i = 0; i < Carrier.carrierMap.size(); i++){
			tabModel1.setValueAt(((Carrier)Carrier.carrierMap.values().toArray()[i]).getId(), i, 0);
			tabModel1.setValueAt(((((Carrier)Carrier.carrierMap.values().toArray()[i]).getTime())/1000) +"s", i, 1);
			tabModel1.setValueAt(((Carrier)Carrier.carrierMap.values().toArray()[i]).isEmpty(), i, 2);
			tabModel1.setValueAt(((Carrier)Carrier.carrierMap.values().toArray()[i]).isReady(), i, 3);
			tabModel1.setValueAt(((Carrier)Carrier.carrierMap.values().toArray()[i]).getMPN(), i, 4);
		}
		try {
			//lbl.setIcon(icon);
			ImageIcon icon = new ImageIcon("/home/momo/Downloads/img.jpg");
            icon.getImage().flush();
            lbl.setIcon( icon );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < Carrier.resultList.size(); i++){
			System.out.println("Debugs: "+ Carrier.resultList.size() +"\t"+ i);
			tabModel2.setValueAt(Carrier.resultList.get(i).getId(), i, 0);
			tabModel2.setValueAt(Carrier.resultList.get(i).getDeadTime()/1000 +"s", i, 1);
			tabModel2.setValueAt(Carrier.resultList.get(i).getMPN(), i, 2);
			//Carrier.resultList.get(i).getDeadTime()
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
