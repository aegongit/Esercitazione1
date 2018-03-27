package core;

public class DeviceInfo {
	private long last_update ;
	private boolean alive;
	
	
	private final static long EXPIRE = 60000; //un minuto
	
	
	public DeviceInfo() {
		last_update= 0;
		this.alive=false;
		
	}
	
	public DeviceInfo(long t) {
		this.last_update = t;
		this.alive = true;
	}
	
	
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	
	public long getLast_update() {
		return last_update;
	}



	public void setLast_update(long last_update) {
		this.last_update = last_update;
	}
	
	/**
	 * Metodo  che  verifica  se il dispositivo è alive
	 * @return true/false rispettivamnete alive/not alive
	 */
	public boolean isExpired() {
		
		if ((System.currentTimeMillis() - this.last_update)>=DeviceInfo.EXPIRE)
			return true;
		return false;
	}
	
	
	public boolean isAlive() {
		return this.alive;
	}
}
