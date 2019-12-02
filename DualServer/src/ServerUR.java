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
		//super.onMessage(msg);
		// TODO Auto-generated method stub
		switch(msg) {
		case Message.ZERO+48:
			//this.addMessage("Sendding sttufss!");
			//this.sendStr("(0)");
			//this.addMessage("Dones!");
			break;
		case Message.ONE:
			this.sendStr("(1)");
			break;
		case Message.TEST_VALUE_69:
			//this.sendStr("(69)");
			break;
		default:
			this.addMessage("Woops something almost impossible happened ;(" + msg);
			break;
		}
	}
}

