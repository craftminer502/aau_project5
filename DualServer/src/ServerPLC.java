public class ServerPLC extends Server {
	
	public native int main();
	public native int getMPN();
	private MPN mpnLib;

	public ServerPLC(String host, int serverPort) {
		super(host, serverPort);
		// TODO Auto-generated constructor stub
		System.setProperty("java.library.path=", "/home/momo/Documents/MPNCounter/");
		System.loadLibrary("mpn");
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
			if(!Carrier.contains(msg)) Carrier.carrierMap.put(msg, new Carrier(msg));
		//DualServer.updateTable();
		
		if(Carrier.getById(msg).isEmpty()) {
			this.addMessage("A carrier is empty - loading!");
			for(Server s : DualServer.clients) {
				if(s instanceof ServerUR) {
					s.sendStr("(0)");
					Carrier.getById(msg).reset();
					Carrier.getById(msg).setEmpty(false);
				}
			}
			this.addMessage("Informed UR5!!");
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Update mpn and time
			this.mpnLib = new MPN();
			int main = this.mpnLib.main();
			int mpn = this.mpnLib.getMPN();
			Carrier.getById(msg).setMPN(mpn);
			DualServer.updateTable();
			
			
			if(Carrier.getById(msg).isReady()) {
				this.addMessage("A carrier is ready to be unloaded!");
				for(Server s : DualServer.clients) {
					if(s instanceof ServerUR) {
						s.sendStr("(1)");
						System.out.println("Timeee: "+ Carrier.getById(msg).getTime());
						Carrier.resultList.add(new Carrier(Carrier.getById(msg).getId(), 
								Carrier.getById(msg).getTime(), 
								Carrier.getById(msg).getMPN()));
						Carrier.getById(msg).reset();
						for(int i = 0; i < Carrier.resultList.size(); i++) {
							System.out.println("Times: "+ Carrier.resultList.get(i).getDeadTime());
						}
					}
				}
				this.addMessage("Informed UR5!");
			}
		}
		
		/*switch(msg%2) {
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
		}*/
		
		DualServer.updateTable();
	}
}
