import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Carrier {
	
	public static HashMap<Integer, Carrier> carrierMap = new HashMap<Integer, Carrier>();
	public static ArrayList<Carrier> resultList = new ArrayList<Carrier>();
	
	private int id;
	private boolean empty;
	private long time;
	private int mpn = 0;
	
	public Carrier(int id) {
		this.id = id;
		this.empty = true;
		this.time = System.currentTimeMillis();
		this.mpn = 0;
	}
	
	public Carrier(int id, long time, int mpn) {
		this.id = id;
		this.time = time;
		this.mpn = mpn;
	}
	
	public int getId() {
		return this.id;
	}
	
	public boolean isEmpty() {
		return this.empty;
	}
	
	public void setEmpty(boolean b) {
		this.empty = b;
	}
	
	public long getTime() {
		if(this.isEmpty()) return 0;
		return System.currentTimeMillis() - this.time;
	}
	
	public long getDeadTime() {
		return this.time;
	}
	
	public int getMPN() {
		return this.mpn;
	}
	
	public void setMPN(int mpn) {
		this.mpn = mpn;
	}
	
	public boolean isReady() {
		if(this.getTime() > 1*60*1000) return true;
		if(this.getMPN() > 500) return true;
		return false;
	}
	
	public static Carrier getById(int msg) {
		for(Map.Entry c : carrierMap.entrySet()) {
			if(((Integer)c.getKey() == msg)) return ((Carrier)c.getValue());
		}
		return null;
	}
	
	public void reset() {
		this.empty = true;
		this.time = System.currentTimeMillis();
		this.mpn = 0;
	}
	
	public static boolean contains(int msg) {
		if(Carrier.carrierMap.containsKey(msg)) return true; return false;
	}
}
