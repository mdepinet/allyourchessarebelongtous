package productivity.chess.control.networking;

public abstract class Network extends Thread {
	protected Object data;
	public void setData(Object d) { data = d; }
}
