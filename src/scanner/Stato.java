package scanner;

public class Stato {

	private final static long EXPIRE = 60000; //un minuto
	private long ttl;
	private Boolean connected;
	
	public Stato(long alive, Boolean connected) {
		this.ttl = alive;
		this.connected = connected;
	}
	public long getAlive() {
		return ttl;
	}
	public void setAlive(long alive) {
		this.ttl = alive;
	}
	public Boolean isConnected() {
		return connected;
	}
	public void setConnected(Boolean connected) {
		this.connected = connected;
	}
	
	/**
	 * Metodo  che  verifica  se il dispositivo è alive
	 * @return true/false rispettivamnete alive/not alive
	 */
	public Boolean isAlive() {
		//System.out.println(System.currentTimeMillis() -this.ttl);
		if ((System.currentTimeMillis() -this.ttl)>=Stato.EXPIRE)
			return false;
		return true;
	}
	
}
