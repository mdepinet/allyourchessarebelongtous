package productivity.todo.model;

public class ReloadThread extends Thread {
	private long millis;
	private Weapon w;
	
	public ReloadThread(Weapon w, long millis){
		this.millis = millis;
		this.w = w;
	}
	
	public void run(){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		w.setClipSize(w.getMaxClipSize());
		w.setClipCount(w.getClipCount()-1);
	}
}
