package productivity.chess.model;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Board {
	private Piece board[][];
	
	public Board()
	{
		board = new Piece[8][8];
		populateBoard();
	}
	public void populateBoard()
	{
		Scanner scan = null;
		try
		{
			scan = new Scanner(new File("default.layout"));
		}
		catch(IOException e)
		{}
		for(int i = 0; i < 8;i++)
		{
			for(int j = 0; j < 8; j++)
			{
				String c = scan.next();
				board[i][j] = new Piece(c);
			}
			scan.nextLine();
		}
	}
}
