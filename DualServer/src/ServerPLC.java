public class ServerPLC extends Server {

	public ServerPLC(String host, int serverPort) {
		super(host, serverPort);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onConnect() {
		super.onConnect();
		// TODO Auto-generated method stub
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(int msg) {
		//super.onMessage(outStream, msg);
		// TODO Auto-generated method stub
		if(msg != 0) 
			if(!Carrier.contains(msg)) Carrier.list.add(new Carrier(msg));
		DualServer.updateTable();
		
		if(Carrier.getById(msg).isEmpty()) {
			for(Server s : DualServer.clients) {
				if(s instanceof ServerUR) {
					s.sendStr("(1)");
					Carrier.getById(msg).setEmpty(true);
				}
			}
		} else {
			if(Carrier.getById(msg).isReady()) {
				this.addMessage("A tray is ready!");
				for(Server s : DualServer.clients) {
					if(s instanceof ServerUR) {
						s.sendStr("(0)");
						Carrier.getById(msg).setEmpty(true);
					}
				}
				this.addMessage("Informed UR5!");
			}
		}
		
		switch(msg%2) {
		case Message.ZERO:
			this.send(0);;
			break;
		case Message.ONE:
			this.send(1);
			break;
		case Message.TEST_VALUE_69:
			this.send(msg);
			break;
		default:
			this.addMessage("Woops something almost impossible happened ;(");
			break;
		}
	}
}
