package productivity.chess.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Board implements GameBoard {
	private static final long serialVersionUID = 1L;
	private static final String fileName = "resources/default.layout";
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
	public List<Location> getValidMovesForLocation(Location loc)
	{
		Piece p = board[loc.getRow()][loc.getCol()];
		List<Location> locs = new LinkedList<Location>();
		if(p==null) return locs;
		int col = loc.getCol(), row = loc.getRow();
		switch(p.getType())
		{
			case PAWN:
				//One square forward
				if (p.isWhite() && !isOccupied(row+1,col)) locs.add(new Location(row+1,col));
				else if (!p.isWhite() && !isOccupied(row-1,col)) locs.add(new Location(row-1,col));
				//Two squares forward
				if (p.isWhite() && row ==1){
					if (!isOccupied(2,col) && !isOccupied(3,col)) locs.add(new Location(3,col));
				}
				else if(!p.isWhite() && row == 6){
					if (!isOccupied(5,col) && !isOccupied(4,col)) locs.add(new Location(4,col));
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
				locs=getRookMoves(row,col);
				break;
			case BISHOP:
				locs = getBishopMoves(row,col);
				break;
			case KNIGHT:
				locs = getValidKnight(row,col);
				for (int i = 0; i<locs.size(); i++){
					if (p.getColor().equals(occupiedBy(locs.get(i).getRow(),locs.get(i).getCol()))){
						locs.remove(i);
						i--;
					}
				}
				break;
			case KING:
				locs = getValidAdjacents(row,col);
				for (int i = 0; i<locs.size(); i++){
					if (p.getColor().equals(occupiedBy(locs.get(i).getRow(),locs.get(i).getCol()))){
						locs.remove(i);
						i--;
					}
				}
				filterKing(locs);
				break;
			case QUEEN:
				locs=getRookMoves(row,col);
				locs.addAll(getBishopMoves(row,col));
				break;
			default:
				break;
		}
		
		return locs;
	}
	private List<Location> getRookMoves(int row, int col)
	{
		List<Location> locs = new LinkedList<Location>();
		Piece p = (Piece)getPieceAt(row,col);
		//horizontal
		for(int r = row+1; r<8; ++r){
			if(isOccupied(r, col)){
				if (p.getOppositeColor().equals(occupiedBy(r,col))) locs.add(new Location(r,col));
				break;
			}
			else{
				 if(isValidLocation(r,col))
					 locs.add(new Location(r,col));
			}
		}
		for(int r = row-1; r>=0; --r){
			if(isOccupied(r, col)){
				if (p.getOppositeColor().equals(occupiedBy(r,col))) locs.add(new Location(r,col));
				break;
			}
			else{
				 if(isValidLocation(r,col))
					 locs.add(new Location(r,col));
			}
		}
		//vertical
		for(int c = col+1; c<8; ++c){
			if(isOccupied(row, c)){
				if (p.getOppositeColor().equals(occupiedBy(row,c))) locs.add(new Location(row,c));
				break;
			}
			else{
				 if(isValidLocation(row,c))
					 locs.add(new Location(row,c));
			}
		}
		for(int c = col-1; c>=0; --c){
			if(isOccupied(row, c)){
				if (p.getOppositeColor().equals(occupiedBy(row,c))) locs.add(new Location(row,c));
				break;
			}
			else{
				 if(isValidLocation(row,c))
					 locs.add(new Location(row,c));
			}
		}
		return locs;
	}
	private List<Location> getBishopMoves(int row, int col)
	{
		List<Location> locs = new LinkedList<Location>();
		Piece p = (Piece)getPieceAt(row,col);
		//down and right
		int c = col+1;
		for(int r = row+1; r<8; ++r){
			if(isOccupied(r,c)){
				if (p.getOppositeColor().equals(occupiedBy(r,c)) && isValidLocation(r,c)) locs.add(new Location(r,c));
				break;
			}
			else{
				 if(isValidLocation(r,c))
					 locs.add(new Location(r,c));
			}
			c++;
		}
		//down and left
		c = col-1;
		for(int r = row+1; r<8; ++r){
			if(isOccupied(r,c)){
				if (p.getOppositeColor().equals(occupiedBy(r,c)) && isValidLocation(r,c)) locs.add(new Location(r,c));
				break;
			}
			else{
				 if(isValidLocation(r,c))
					 locs.add(new Location(r,c));
			}
			c--;
		}
		//up and right
		c = col+1;
		for(int r = row-1; r>=0; --r){
			if(isOccupied(r,c)){
				if (p.getOppositeColor().equals(occupiedBy(r,c)) && isValidLocation(r,c)) locs.add(new Location(r,c));
				break;
			}
			else{
				 if(isValidLocation(r,c))
					 locs.add(new Location(r,c));
			}
			c++;
		}
		//up and left
		c = col-1;
		for(int r = row-1; r>=0; --r){
			if(isOccupied(r,c)){
				if (p.getOppositeColor().equals(occupiedBy(r,c))) locs.add(new Location(r,c));
				break;
			}
			else{
				 if(isValidLocation(r,c))
					 locs.add(new Location(r,c));
			}
			c--;
		}
		return locs;
	}

	private List<Location> getValidAdjacents(int row, int col) {
		List<Location> locs = new LinkedList<Location>();
		int r, c = col-1;
		for (r=row-1;r<=row+1;r++){
			if (isValidLocation(r,c)) locs.add(new Location(r,c));
		}
		c=col+1;
		for (r=row-1;r<=row+1;r++){
			if (isValidLocation(r,c)) locs.add(new Location(r,c));
		}
		if (isValidLocation(row+1,col)) locs.add(new Location(row+1,col));
		if (isValidLocation(row-1,col)) locs.add(new Location(row-1,col));
		return locs;
	}
	private List<Location> getValidKnight(int row, int col){
		//2,1;2,-1;-2,1;-2,-1;1,2;1,-2;-1,2;-1,-2;
		List<Location> locs = new LinkedList<Location>();
		if (isValidLocation(row+2,col+1)) locs.add(new Location(row+2,col+1));
		if (isValidLocation(row+2,col-1)) locs.add(new Location(row+2,col-1));
		if (isValidLocation(row-2,col+1)) locs.add(new Location(row-2,col+1));
		if (isValidLocation(row-2,col-1)) locs.add(new Location(row-2,col-1));
		if (isValidLocation(row+1,col+2)) locs.add(new Location(row+1,col+2));
		if (isValidLocation(row+1,col-2)) locs.add(new Location(row+1,col-2));
		if (isValidLocation(row-1,col+2)) locs.add(new Location(row-1,col+2));
		if (isValidLocation(row-1,col-2)) locs.add(new Location(row-1,col-2));
		return locs;
	}
	private void filterKing(List<Location> locs){
		//TODO
	}
	public boolean isValidLocation(int row, int col){
		return (col>=0&&col<8&&row>=0&&row<8);
	}
	private boolean isOccupied(int row, int col){
		if (!isValidLocation(row, col)) return false;
		Piece p = board[row][col];
		if (p == null) return false;
		else return true;
	}
	private String occupiedBy(int row, int col){
		if (!isValidLocation(row, col)) return null;
		Piece p = board[row][col];
		if (p == null) return null;
		else return p.getColor();
	}
	//horribly inefficient, I know. It should work for now.
	public boolean canMove(String color)
	{
		for(int r = 0; r<8; r++)
			for(int c =0; c<8; c++){
				Piece p = board[r][c];
				if(p!=null && p.getColor().equals(color)){
					if(getValidMovesForLocation(new Location(r,c)).size()>0)
						return true;
				}
			}
		return false;
	}
	public GamePiece getPieceAt(Location loc){
		return board[loc.getRow()][loc.getCol()];
	}
	public Piece getPieceAt(int row, int col){
		return board[row][col];
	}
	public void populateBoard()
	{
		Scanner scan = null;
		try
		{
			scan = new Scanner(new File(fileName));
		}
		catch(IOException e)
		{}
		String color = "white";
		for(int i = 0; i < 8;i++)
		{
			for(int j = 0; j < 8; j++)
			{
				String c = scan.next();
				if(c.equals("X"))
					color = "black";
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
	public boolean isInCheck(String color)
	{
		for(int r =0; r<8; r++)
			for(int c = 0; c<8; c++)
				if(getPieceAt(new Location(r,c))!=null 
						&& getPieceAt(new Location (r,c)).getColor().equals(getOppositeColor(color)))
					for(Location loc :getValidMovesForLocation(new Location(r,c)))
						if(getPieceAt(loc)!=null && getPieceAt(loc).getType()==PieceType.KING)
							return true;
		return false;
	}
	public static String getOppositeColor(String color)
	{
		if(color.equals("white")) return "black";
		else return "white";
	}
	public GamePiece movePiece(Location loc1, Location loc2){
		int r1,c1,r2,c2;
		Piece takenPiece = board[r2 = loc2.getRow()][c2 = loc2.getCol()];
		board[r2][c2] = board[ r1 = loc1.getRow()][c1 = loc1.getCol()];
		board[r1][c1] = null;
		return takenPiece;
	}
}
