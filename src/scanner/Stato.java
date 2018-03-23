package scanner;

public class Stato {

	private Boolean alive;
	private Boolean connected;
	
	public Stato(Boolean alive, Boolean connected) {
		this.alive = alive;
		this.connected = connected;
	}
	public Boolean getAlive() {
		return alive;
	}
	public void setAlive(Boolean alive) {
		this.alive = alive;
	}
	public Boolean getConnected() {
		return connected;
	}
	public void setConnected(Boolean connected) {
		this.connected = connected;
	}
	
}
