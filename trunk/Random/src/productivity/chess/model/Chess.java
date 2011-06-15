package productivity.chess.model;

public class Chess {
	private Board board;
	public Chess()
	{
		board = new Board();
		//System.out.print(board);
	}
	public static void main(String[] args)
	{
		new Chess();
	}
	
}
