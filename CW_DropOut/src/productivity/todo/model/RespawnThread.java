package productivity.todo.model;

public class RespawnThread extends Thread {
	private long millis;
	private Player p;
	private GameMap gm;
	private boolean indexZero;
	
	public RespawnThread(GameMap gm, Player p, boolean indexZero, long millis){
		this.millis = millis;
		this.p = p;
		this.gm = gm;
		this.indexZero = indexZero;
	}
	
	public void run(){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (indexZero) {
			Player player = new Player("player1");
			player.setTeam(1);
			player.setType(PlayerType.PERSON);
			gm.getPlayers().add(0, player);
			gm.spawn(player);
		}
		else {
			gm.getPlayers().add(p);
			gm.spawn(p);
		}
	}
}
