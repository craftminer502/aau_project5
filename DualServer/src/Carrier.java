import java.util.ArrayList;

public class Carrier {
	
	public static ArrayList<Carrier> list = new ArrayList<Carrier>();
	
	private int id;
	private boolean empty;
	private long time;
	private double mpn = 123.54;
	
	public Carrier(int id) {
		this.id = id;
		this.time = System.currentTimeMillis();
		this.mpn = 0;
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
		return System.currentTimeMillis() - this.time;
	}
	
	public double getMPN() {
		return this.mpn;
	}
	
	public boolean isReady() {
		if(this.getTime() > 1*60*1000) return true;
		if(this.getMPN() > 10) return true;
		return false;
	}
	
	public static Carrier getById(int msg) {
		for(Carrier c : Carrier.list) {
			if(c.id==msg) return c;
		}
		return null;
	}
	
	public static boolean contains(int msg) {
		for(Carrier c : Carrier.list) {
			if(c.id==msg) return true;
		}
		return false;
	}
}
