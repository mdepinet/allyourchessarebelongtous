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
		int col = loc.getCol(), row = loc.getRow();
		switch(p.getType())
		{
			case PAWN:
				//One square forward
				if (p.isWhite() && !isOccupied(row+1,col)) locs.add(new Location(row+1,col));
				else if (!p.isWhite() && !isOccupied(row-1,col)) locs.add(new Location(row-1,col));
				//Two squares forward
				if (p.isWhite() && row == 2){
					if (!isOccupied(3,col) && !isOccupied(4,col)) locs.add(new Location(4,col));
				}
				else if(!p.isWhite() && row == 7){
					if (!isOccupied(6,col) && !isOccupied(5,col)) locs.add(new Location(5,col));
				}
				//Taking opponent
				if (p.isWhite()){
					if (p.getOppositeColor().equals(occupiedBy(row+1,col+1))) locs.add(new Location(row+1,col+1));
					if (p.getOppositeColor().equals(occupiedBy(row+1,col-1))) locs.add(new Location(row+1, col-1));
				}
				else{
					if (p.getOppositeColor().equals(occupiedBy(row-1,col+1))) locs.add(new Location(row-1,col+1));
					if (p.getOppositeColor().equals(occupiedBy(row-1,col-1))) locs.add(new Location(row-1, col-1));
				}
				//En passant
				if ((p.isWhite() && row >= 5) || (!p.isWhite() && row <=4)){
					Piece other;
					if (p.getOppositeColor().equals(occupiedBy(row,col+1))
							&& (other = getPieceAt(row,col+1)).getType() == PieceType.PAWN
							&& other.getLastMoved() == 0) locs.add(new Location(row,col+1));
					if (p.getOppositeColor().equals(occupiedBy(row,col-1))
							&& (other = getPieceAt(row,col-1)).getType() == PieceType.PAWN
							&& other.getLastMoved() == 0) locs.add(new Location(row,col-1));
				}
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
	
	private boolean isValidLocation(int row, int col){
		return (col>0&&col<=8&&row>0&&row<=8);
	}
	private boolean isOccupied(int row, int col){
		if (!isValidLocation(row, col)) return false;
		Piece p = board[col][row];
		if (p == null) return false;
		else return true;
	}
	private Color occupiedBy(int row, int col){
		if (!isValidLocation(row, col)) return null;
		Piece p = board[col][row];
		if (p == null) return null;
		else return p.getColor();
	}
	
	public Piece getPieceAt(Location loc){
		return board[loc.getCol()][loc.getRow()];
	}
	public Piece getPieceAt(int row, int col){
		return board[col][row];
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
		Color color = Color.WHITE;
		for(int i = 0; i < 8;i++)
		{
			for(int j = 0; j < 8; j++)
			{
				String c = scan.next();
				if(c.equals("X"))
					color = Color.BLACK;
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
