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
	public Board(Board other){
		board = other.copyBoard();
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
				if ((p.isWhite() && row >= 4) || (!p.isWhite() && row <=3)){
					Piece other;
					if (p.getOppositeColor().equals(occupiedBy(row,col+1))
							&& (other = getPieceAt(row,col+1)).getType() == PieceType.PAWN
							&& other.getLastMoved() == 0) locs.add(new Location((p.isWhite() ? row+1 : row - 1),col+1));
					if (p.getOppositeColor().equals(occupiedBy(row,col-1))
							&& (other = getPieceAt(row,col-1)).getType() == PieceType.PAWN
							&& other.getLastMoved() == 0) locs.add(new Location((p.isWhite() ? row+1 : row - 1),col-1));
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
				filterKing(locs, getOppositeColor(p.getColor()));
				
				//now for castling... yay!
				if(!p.hasMoved()){
					int r = loc.getRow();
					int c = loc.getCol();
					Piece rook = (Piece)getPieceAt(new Location(r,c-3));
					if(rook.getType()==PieceType.ROOK && !rook.hasMoved())
						if(getPieceAt(new Location(r,c-1))==null && getPieceAt(new Location(r, c-2))==null){
							String enemyColor=getOppositeColor(p.getColor());
							if(!isBeingAttacked(enemyColor, new Location(r,c))
									&&!isBeingAttacked(enemyColor,new Location(r,c-1)) && !isBeingAttacked(enemyColor,new Location(r,c-2)))
								locs.add(new Location(r,c-2));
						}
					rook = (Piece)getPieceAt(new Location(r, c+4));
					if(rook.getType()==PieceType.ROOK && !rook.hasMoved())
						if(getPieceAt(new Location(r,c+1))==null && getPieceAt(new Location(r, c+2))==null
								&& getPieceAt(new Location(r, c+3))==null){
							String enemyColor=getOppositeColor(p.getColor());
							if(!isBeingAttacked(enemyColor, new Location(r,c))&&!isBeingAttacked(enemyColor,new Location(r,c+1)) 
									&& !isBeingAttacked(enemyColor,new Location(r,c+2)) && !isBeingAttacked(enemyColor,new Location(r,c+3)))
								locs.add(new Location(r,c+2));
						}
				}
					
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
		//vertical
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
		//horizontal
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
	private void filterKing(List<Location> locs, String enemyColor){
		for(int i =0; i<locs.size();i++){
			if(isBeingAttacked(enemyColor, locs.get(i))){
				locs.remove(i);
				i--;
			}
		}
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
	public void putPieceAt(Location loc, GamePiece p) {
		board[loc.getRow()][loc.getCol()]=(Piece)p;
	}
	public GamePiece removePieceAt(Location loc){
		Piece p = board[loc.getRow()][loc.getCol()];
		board[loc.getRow()][loc.getCol()] = null;
		return p;
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
	public boolean isBeingAttacked(String color, Location loc)
	{
		Piece dummy;
		if(board[loc.getRow()][loc.getCol()]!=null)
			dummy=new Piece(board[loc.getRow()][loc.getCol()]);
		else
			dummy=null;
		board[loc.getRow()][loc.getCol()]= new Piece("D",getOppositeColor(color));
		for(int r =0; r<8; r++)
			for(int c = 0; c<8; c++){
				GamePiece p = getPieceAt(new Location(r,c));
				if(p!=null && p.getColor().equals(color)){
					if(p.getType() != PieceType.KING){
						for(Location l :getValidMovesForLocation(new Location(r,c)))
							if(loc.equals(l)){
								board[loc.getRow()][loc.getCol()]=dummy;
								return true;
							}
					}
					else{ //the piece is a King and we don't want to call getValidMovesForLocation on it or else an infinite loop starts
						if(getValidAdjacents(r,c).contains(loc)){
							board[loc.getRow()][loc.getCol()]=dummy;
							return true;
						}
							
					}
				}
				
			}
		board[loc.getRow()][loc.getCol()]=dummy;
		return false;
	}
	/*
	 * Is king in check?
	 * Can king move?
	 * If not, can another piece block the check?
	 * If not, can another piece eliminate the piece putting the king in check?
	 * (non-Javadoc)
	 * @see productivity.chess.model.GameBoard#isCheckmate(boolean)
	 */
	public boolean isCheckmate(boolean isWhite)
	{
		//king must already be in check!
		//get location of your king and enemy pieces
		String color = isWhite ? "white" : "black";
		Location kingLocation = null;
		List<Location> enemyLocs = new LinkedList<Location>();
		for(int r = 0; r<8; r++)
			for(int c = 0; c<8; c++){
				Location loc = new Location(r,c);
				GamePiece piece = getPieceAt(loc);
				if(piece !=null && piece.getColor().equals(getOppositeColor(color)))
					enemyLocs.add(loc);
				else if(piece!=null && piece.getType()==PieceType.KING 
						&& piece.getColor().equals(color))
					kingLocation = new Location(r,c);
			}
		//if king can move, it's not checkmate
		if(getValidMovesForLocation(kingLocation).size()>0)
			return false;
		//get all of the pieces which are attacking the king
		List<Location> attackingLocs = new LinkedList<Location>();
		for(Location loc : enemyLocs){
			if(getValidMovesForLocation(loc).contains(kingLocation))
				attackingLocs.add(loc);
		}
		//if the king can't move and there is more than one enemy attacking him, it's checkmate
		if(attackingLocs.size()>1)
			return true;
		//now add all of the squares you can "block" to stop checkmate into attackingLocs
		Location attackLoc = attackingLocs.get(0);
		Piece piece = (Piece)getPieceAt(attackLoc);
		//if it's a queen, decide whether to treat it like a bishop or a rook attacking the king
		boolean queenLikeRook = false;
		boolean queenLikeBishop=false;
		if(piece.getType()==PieceType.QUEEN){
			if(kingLocation.getRow()!=attackLoc.getRow() && kingLocation.getCol()!= attackLoc.getCol())
				queenLikeBishop=true;
			else
				queenLikeRook=true;
		}
		if(piece.getType()==PieceType.BISHOP || queenLikeBishop){
			int kingR=kingLocation.getRow();
			int kingC=kingLocation.getCol();
			int bishR=attackLoc.getRow();
			int bishC=attackLoc.getCol();
			if(kingR<bishR && kingC<bishC){
				//up and left
				int c = bishC-1;
				for(int r = bishR-1; r>=0; --r){
					if(isOccupied(r,c)){
						if (piece.getOppositeColor().equals(occupiedBy(r,c))) attackingLocs.add(new Location(r,c));
						break;
					}
					else{
						 if(isValidLocation(r,c))
							 attackingLocs.add(new Location(r,c));
					}
					c--;
				}
			}
			if(kingR>bishR && kingC<bishC){
				//down and left
				int c = bishC-1;
				for(int r = bishR+1; r<=7; ++r){
					if(isOccupied(r,c)){
						if (piece.getOppositeColor().equals(occupiedBy(r,c))) attackingLocs.add(new Location(r,c));
						break;
					}
					else{
						 if(isValidLocation(r,c))
							 attackingLocs.add(new Location(r,c));
					}
					c--;
				}
			}
			if(kingR<bishR && kingC>bishC){
				//up and right
				int c = bishC+1;
				for(int r = bishR-1; r>=0; --r){
					if(isOccupied(r,c)){
						if (piece.getOppositeColor().equals(occupiedBy(r,c))) attackingLocs.add(new Location(r,c));
						break;
					}
					else{
						 if(isValidLocation(r,c))
							 attackingLocs.add(new Location(r,c));
					}
					c++;
				}
			}
			if(kingR>bishR && kingC>bishC){
				//down and right
				int c = bishC+1;
				for(int r = bishR+1; r<=8; ++r){
					if(isOccupied(r,c)){
						if (piece.getOppositeColor().equals(occupiedBy(r,c))) attackingLocs.add(new Location(r,c));
						break;
					}
					else{
						 if(isValidLocation(r,c))
							 attackingLocs.add(new Location(r,c));
					}
					c++;
				}
			}
			//TODO
		}
		else if(piece.getType()==PieceType.ROOK || queenLikeRook){
			//find out if row or column is different
			if(kingLocation.getRow()==attackLoc.getRow()){
				int kingC = kingLocation.getCol();
				int rookC = attackLoc.getCol();
				for(int c = rookC<kingC ? rookC : kingC; c< (rookC<kingC ? kingC : rookC); c++)
					attackingLocs.add(new Location(kingLocation.getRow(), c));
			}
			else{
				int kingR = kingLocation.getRow();
				int rookR = attackLoc.getRow();
				for(int r = rookR<kingR ? rookR : kingR; r< (rookR<kingR ? kingR : rookR); r++)
					attackingLocs.add(new Location(r, kingLocation.getCol()));
			}
			//TODO
	}
		System.out.println(attackingLocs);
		//finally, see if any of your pieces can move into any of attackingLocs
		for(int r = 0; r<8; r++)
			for(int c = 0; c<8; c++){
				Location loc = new Location(r,c);
				if(isOccupied(r,c)&& getPieceAt(r,c).getColor().equals(color)&&getPieceAt(r,c).getType()!=PieceType.KING){
					List<Location> valids = getValidMovesForLocation(loc);
					valids.retainAll(attackingLocs);
					if(valids.size()>0)
						return false;
				}
			}
		
		
		return true;
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
	
	@Override
	public boolean equals(Object obj){
		if (!(obj instanceof Board)) return false;
		Piece[][] oBoard = ((Board)obj).getBoard();
		if (board.length != oBoard.length)return false; 
		for (int r = 0; r<board.length; r++){
			if (board[r].length != oBoard[r].length)return false; 
			for (int c = 0; c<board[r].length; c++){
				if (board[r][c] == null){
					if (oBoard[r][c] == null) continue;
					return false;
				}
				else if (!board[r][c].equals(oBoard[r][c])) return false;
			}
		}
		return true;
	}
	
	public Piece[][] copyBoard(){
		Piece[][] newRay = new Piece[8][8];
		for (int r = 0; r<board.length; r++){
			for (int c = 0; c<board[r].length; c++){
				newRay[r][c] = board[r][c];
			}
		}
		return newRay;
	}
	
}
