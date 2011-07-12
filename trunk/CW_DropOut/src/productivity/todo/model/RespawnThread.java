package productivity.todo.model;

public class RespawnThread extends Thread {
	private long millis;
	private Player p;
	
	public RespawnThread(Player p, long millis){
		this.millis = millis;
		this.p = p;
	}
	
	public void run(){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		p.setHealth(100);
	}
}
