package productivity.chess.model;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.Serializable;

public class Board implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String fileName = "default.layout";
	private Piece board[][];
	
	public Board()
	{
		board = new Piece[8][8];
		loadDefaultBoard();
	}
	public void loadDefaultBoard()
	{
		try {
			FileInputStream fis = null;
			ObjectInputStream in = null;
			try {
				fis = new FileInputStream(fileName);
				in = new ObjectInputStream(fis);
	            setBoard(((Board)in.readObject()).getBoard());
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
			catch(ClassNotFoundException ex) {
				ex.printStackTrace();
			}
			finally {
				try{in.close();}catch(Throwable t){}
			}
		}
		catch(Exception e) {}
	}
	public String toString()
	{
		String s = "";
		for(int i = 0; i < 8; i++)
		{
			for(int j = 0; j < 8; j++)
			{
				if(board[i][j]!=null)
					s+= board[i][j].getType() + " ";
				else
					s+="X ";
			}
			s+="\n";
		}
		return s;
	}
	public void saveBoard()
	{
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try
		{
		    fos = new FileOutputStream(fileName);
		    out = new ObjectOutputStream(fos);
		    out.writeObject(this);
		 }
		 catch(IOException ex)
		 {
		    ex.printStackTrace();
		 }
		 finally{
			 try {
				out.close();
			} catch (IOException e) {
			}
		 }
	}
	public ArrayList<Location> getValidMovesForLocation(Location loc)
	{
		Piece p = board[loc.getCol()][loc.getRow()];
		ArrayList<Location> locs = new ArrayList<Location>();
		switch(p.getType())
		{
			case PAWN:
				
				break;
			case ROOK:
				break;
			case BISHOP:
				break;
			case KNIGHT:
				break;
			case KING:
				break;
			case QUEEN:
				break;
			default:
				break;
		}
		
		return locs;
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
		Color color = Color.black;
		for(int i = 0; i < 8;i++)
		{
			for(int j = 0; j < 8; j++)
			{
				String c = scan.next();
				if(c.equals("X"))
					color = Color.white;
				else
					board[i][j] = new Piece(c, color);
			}
			if(scan.hasNextLine())
				scan.nextLine();
		}
	}
	public Piece[][] getBoard() {
		return board;
	}
	public void setBoard(Piece[][] board) {
		this.board = board;
	}
}
