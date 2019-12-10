public class ServerUR extends Server {

	public ServerUR(String host, int serverPort) {
		super(host, serverPort);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onConnect() {
		super.onConnect();
		// TODO Auto-generated method stub
		//DualServer.addURMessage(this.client.getInetAddress().getHostAddress()+ " has connected!");
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(int msg) {
		super.onMessage(msg);
		// TODO Auto-generated method stub
	}
}

