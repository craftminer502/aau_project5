import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public abstract class Server extends Thread implements InterfaceServer {
	
	private ServerSocket serverSock;
	protected Socket client = null;
	private String host = null;
	private String id = null;
	private int serverPort;
	private DataOutputStream outStream = null;
	
	public Server(String host, int serverPort) {
		this.host = host;
		this.serverPort = serverPort;
	}
	
	public void onConnect() {
		// TODO Auto-generated method stub
		this.id = this.client.getLocalAddress().toString();
		this.addMessage(this.client.getInetAddress().getHostAddress()+ " has connected!");
	}
	
	public void onDisconnect() {
		// TODO Auto-generated method stub
		
	}
	
	public void onMessage(int msg) {
		// TODO Auto-generated method stub
		this.addMessage(this.id +": "+ msg);
		
	}
	
	public void addMessage(String msg) {
		if(this instanceof ServerPLC) {
			DualServer.addPLCMessage(msg);
		} else if(this instanceof ServerUR) {
			DualServer.addURMessage(msg);
		}
	}
	
	protected void send(int msg) {
		try {
			this.outStream.write(msg);
			this.outStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void sendStr(String msg) {
		try {
			this.outStream.writeBytes(msg);
			this.outStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void create() {
		try {
			this.addMessage("Creating ServerSocket...");
			this.serverSock = new ServerSocket(this.serverPort, 5, InetAddress.getByName(this.host));
			this.addMessage("Socket created at: "+ this.serverSock.getLocalSocketAddress().toString());
			this.addMessage("Waiting for client request...");
			this.client = this.serverSock.accept();
			DualServer.clients.add(this);
			//this.client.setSoTimeout(20000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		InputStream inStream = null;
        //DataOutputStream outStream = null;
        BufferedReader buffer = null;
        
		this.create();
		this.onConnect();
		
        try {
            inStream = this.client.getInputStream();
            buffer = new BufferedReader(new InputStreamReader(inStream));
            outStream = new DataOutputStream(this.client.getOutputStream());
        } catch (IOException e) {
            return;
        }
        
        int value;
		while(DualServer.isRunning()) {
			try {
				while((value = buffer.read()) != -1) {
					System.out.println(value);
					if(value > 0) {
						this.onMessage(value);
					}
				}
			} catch(SocketTimeoutException e) {
				this.addMessage("Read timedout - assuming disconnect!");
				try {
					this.client.close();
					DualServer.stop();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
