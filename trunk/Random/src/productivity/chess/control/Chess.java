package productivity.chess.control;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import productivity.chess.model.Board;
import productivity.chess.model.GameBoard;
import productivity.chess.model.GamePiece;
import productivity.chess.model.Location;
import productivity.chess.model.Piece;
import productivity.chess.model.PieceType;
import productivity.chess.view.GameFrame;


public class Chess implements MouseListener{
	private Board board;
	private GameFrame frame;
	private List<GamePiece> whites;
	private List<GamePiece> aas;
	private List<Location> moves;
	private Location selected;
	private boolean isWhiteTurn;
	private boolean flipped;
//	private Server server;
	
	public Chess(String frameTitle)
	{
		board = new Board();
		this.flipped = (frameTitle.equals("Server"));
		frame = new GameFrame(frameTitle, board, (MouseListener) this);
		moves = new LinkedList<Location>();
		frame.getCanvas().setMoves(moves);
		selected = null;
		isWhiteTurn=true;
//		try {
//			server = new Server();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		whites= new ArrayList<GamePiece>();
		aas = new ArrayList<GamePiece>();
		for(int r = 0; r<8; r++) for(int c =0; c<8; c++){
			GamePiece piece = board.getPieceAt(new Location(r,c));
			if (piece != null){
				((Piece)piece).setLocation(r,c);
				if (piece.getColor().equals("white")) whites.add(piece);
				else aas.add(piece);
			}
		}
	}
	
	public void mousePressed(MouseEvent e) {
	       
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    	if (selected == null){
    		Location loc = locationForClick(e.getY(), e.getX());
    		//is it your turn?
    		boolean correctTurn=false;
    		if(board.getPieceAt(loc)!=null)
    			correctTurn = isWhiteTurn==(board.getPieceAt(loc).getColor().equals("white"));
	    	
    		boolean repaint = false;
			if(!moves.isEmpty())
			{
				repaint = true;
				frame.getCanvas().clearMoves();
			}
	    	if(loc!=null && board.isValidLocation(loc.getRow(), loc.getCol()) && correctTurn){
	    		moves.clear();
	    		moves.addAll(board.getValidMovesForLocation(loc));
	    		if(!moves.isEmpty())
	    			repaint=true;
	    	}
	    	if(repaint) {
	    		frame.getCanvas().repaint();
	    		//frame.getCanvas().setTurn(!isWhiteTurn);
	    	}
	    	
	    	if (board.getPieceAt(loc) != null && correctTurn) selected = loc;
    	}
    	else{
    		Location loc = locationForClick(e.getY(), e.getX());
    		if (moves.contains(loc)){
    			GamePiece temp = board.getPieceAt(selected);
    			GamePiece taken = board.movePiece(selected,loc);
    			if(isInCheck(isWhiteTurn?"white":"black")) {
    				((Board)board).putPieceAt(selected,temp);
    				((Board)board).putPieceAt(loc, taken);
    			}
    			else {
    				((Board)board).putPieceAt(selected,temp);
    				((Board)board).putPieceAt(loc, taken);
	    			updateLoc(temp,loc);
	    			updateStatus(selected, loc, taken);
	    			board.movePiece(selected,loc);
    			}
//    			server.setBoard((Board)board);
    		}
	    	moves.clear();
	    	frame.getCanvas().repaint();
    		selected = null;
    	}
    }
    public Location locationForClick(int y, int x)
    {
    	x = x-50;
    	y = y-50;
    	if(x>=0 && y>=0)
    	{
    		return new Location((flipped)? 7-(y/30) : (y/30),(flipped)? 7-(x/30) : (x/30));
    	}
    	return null;
    }
    public void updateStatus(Location prev, Location curr, GamePiece taken)
    {
    	whites.remove(taken);
    	aas.remove(taken);
    	for (GamePiece p : whites){
    		if (p.getType() == PieceType.PAWN) p.incLastMoved();
    	}
    	for (GamePiece p : aas){
    		if (p.getType() == PieceType.PAWN) p.incLastMoved();
    	}
    	Piece piece =(Piece) board.getPieceAt(prev);
    	boolean isWhite = piece.getColor().equals("white");
    	switch(piece.getType()){
    	case PAWN:
    		piece.resetLastMoved();
    		if (prev.getCol() != curr.getCol() && taken == null){
    			board.removePieceAt(new Location(piece.isWhite() ? curr.getRow()-1 : curr.getRow()+1,curr.getCol()));
    			if (piece.isWhite()) aas.remove(taken);
    			else whites.remove(taken);
    		}
    		if(curr.getRow()==0 || curr.getRow()==7)
    			pawnUpgrade(prev);
    		break;
    	case KING:
    		if(!piece.hasMoved())
    			piece.setHasMoved();
    		if(isWhite && whites.size()==1)
    			piece.incMovesAlone();
    		else if(!isWhite && aas.size()==1)
    			piece.incMovesAlone();
    		if(piece.getMovesAlone()>50)
    			stalemate();
    		//castling
    		if(Math.abs(prev.getCol()-curr.getCol())>1){
    			boolean rookAt0 = prev.getCol()>curr.getCol();
    			castle(rookAt0,isWhite);
    		}
    	case ROOK:
    		piece.setHasMoved();
    		break;
    	}
    	//isWhiteTurn=!isWhiteTurn;
    	//System.out.println(board.isInCheck("white"));
    	String color = isWhiteTurn ? "black" : "white";
    	if(isInCheck(color)){
    		if(board.isCheckmate(isWhiteTurn)) {
    			System.out.println(color +" loses");
    			frame.getCanvas().setMate(true);
    		}
    	}
    	else if (!board.canMove(isWhiteTurn ? "black" : "white")){
    		//if ((isWhite && isInCheck("black")) || (!isWhite && isInCheck("white"))) checkmate(isWhite); 
    			stalemate();
    	}
    	//TODO
    	//increments ints for how long each pawn has been stationary, updates hasMoved for kings and rooks, check En passant
    }
    public void pawnUpgrade(Location curr)
    {
    	String type = JOptionPane.showInputDialog("Enter a piece type").toUpperCase();
    	if(type.equals("QUEEN")|| type.equals("KNIGHT")|| type.equals("BISHOP")
    			|| type.equals("ROOK"))
    		board.getPieceAt(curr).setType(PieceType.valueOf(type));
    	else
    		pawnUpgrade(curr);
    }
    public boolean checkmate(boolean isWhite)
    {
//    	if(board.isCheckmate(isWhite)) frame.getCanvas().setMate(true);
//    	return board.isCheckmate(isWhite);
    	//Get the king
    	GamePiece king = null; 
    	if(!isWhite) {
    		if(isInCheck("white")) {
		    	for(int i=0;i<whites.size();i++) {
		    		if(whites.get(i).getType()==PieceType.KING) {
		    			king=whites.get(i);
		    			break;
		    		}
		    	}
		    	//Ensure king can't move
		    	if(board.getValidMovesForLocation(((Piece)king).getLocation()).size()>0)
					return false;
		    	//Get all enemies that are attacking the king. If multiple enemies, then checkmate
		    	ArrayList<GamePiece> enemiesAttack = new ArrayList<GamePiece>();
		    	for(GamePiece piece:aas) {
		    		if(board.getValidMovesForLocation(((Piece)piece).getLocation()).contains(((Piece)king).getLocation()))
						enemiesAttack.add(piece);
		    	}
		    	if(enemiesAttack.size()>1) {
		    		frame.getCanvas().setMate(true);
		    		return true;
		    	}
		    	//Determine whether or not a move can be made by player to block/capture enemy piece
		    	//Also determine path attacking piece takes to put king in check
		    	GamePiece enemy = enemiesAttack.get(0);
		    	if(enemy.getType()==PieceType.KNIGHT) {
		    		ArrayList<GamePiece> allyAttack = new ArrayList<GamePiece>();
		        	for(GamePiece piece:whites) {
		        		if(board.getValidMovesForLocation(((Piece)piece).getLocation()).contains(((Piece)enemiesAttack.get(0)).getLocation()))
		    				allyAttack.add(piece);
		        	}
		        	if(allyAttack.size()==0) {
		        		frame.getCanvas().setMate(true);
		        		return true;
		        	}
		    	}
		    	if(enemy.getType()==PieceType.QUEEN || enemy.getType()==PieceType.BISHOP || enemy.getType()==PieceType.PAWN) {
		    		ArrayList<Location> betweenPath = new ArrayList<Location>();
		    		int row = ((Piece)enemy).getLocation().getRow();
		    		for(int col = ((Piece)enemy).getLocation().getCol();col>((Piece)king).getLocation().getCol();col=(col>((Piece)king).getLocation().getCol()?col-1:col+1)) {
		    			betweenPath.add(new Location(((Piece)enemy).getLocation().getRow()>((Piece)king).getLocation().getRow()?row--:row++,col));
		    		}
		    		ArrayList<GamePiece> allyAttack = new ArrayList<GamePiece>();
		        	for(GamePiece piece:whites) {
		        		for(Location spot:betweenPath)
			        		if(board.getValidMovesForLocation(((Piece)piece).getLocation()).contains(spot))
			    				allyAttack.add(piece);
		        	}
		        	if(allyAttack.size()==0) {
		        		frame.getCanvas().setMate(true);
		        		return true;
		        	}
		    	}
		    	if(enemy.getType()==PieceType.QUEEN || enemy.getType()==PieceType.ROOK) {
		    		ArrayList<Location> betweenPath = new ArrayList<Location>();
		    		if(((Piece)enemy).getLocation().getRow()!=((Piece)king).getLocation().getRow()) 
			    		for(int row = ((Piece)enemy).getLocation().getRow();row>((Piece)king).getLocation().getRow();row=(row>((Piece)king).getLocation().getRow()?row-1:row+1)) 
			    			betweenPath.add(new Location(row,((Piece)enemy).getLocation().getCol()));
			    	else if(((Piece)enemy).getLocation().getRow()!=((Piece)king).getLocation().getRow()) 
			    		for(int col = ((Piece)enemy).getLocation().getCol();col>((Piece)king).getLocation().getCol();col=(col>((Piece)king).getLocation().getCol()?col-1:col+1)) 
			    			betweenPath.add(new Location(((Piece)enemy).getLocation().getRow(),col));	
		    		ArrayList<GamePiece> allyAttack = new ArrayList<GamePiece>();
		        	for(GamePiece piece:whites) {
		        		for(Location spot:betweenPath)
			        		if(board.getValidMovesForLocation(((Piece)piece).getLocation()).contains(spot))
			    				allyAttack.add(piece);
		        	}
		        	if(allyAttack.size()==0) {
		        		frame.getCanvas().setMate(true);
		        		return true;
		        	}
		    	}
    		}
    	}
    	else {
    		if(isInCheck("black")) {
	    		for(int i=0;i<aas.size();i++) {
		    		if(aas.get(i).getType()==PieceType.KING) {
		    			king=aas.get(i);
		    			break;
		    		}
		    	}
		    	//Ensure king can't move
		    	if(board.getValidMovesForLocation(((Piece)king).getLocation()).size()>0)
					return false;
		    	//Get all enemies that are attacking the king. If multiple enemies, then checkmate
		    	ArrayList<GamePiece> enemiesAttack = new ArrayList<GamePiece>();
		    	for(GamePiece piece:whites) {
		    		if(board.getValidMovesForLocation(((Piece)piece).getLocation()).contains(((Piece)king).getLocation()))
						enemiesAttack.add(piece);
		    	}
		    	if(enemiesAttack.size()>1) {
		    		frame.getCanvas().setMate(true);
		    		return true;
		    	}
		    	//Determine whether or not a move can be made by player to block/capture enemy piece
		    	//Also determine path attacking piece takes to put king in check
		    	GamePiece enemy = enemiesAttack.get(0);
		    	if(enemy.getType()==PieceType.KNIGHT) {
		    		ArrayList<GamePiece> allyAttack = new ArrayList<GamePiece>();
		        	for(GamePiece piece:aas) {
		        		if(board.getValidMovesForLocation(((Piece)piece).getLocation()).contains(((Piece)enemiesAttack.get(0)).getLocation()))
		    				allyAttack.add(piece);
		        	}
		        	if(allyAttack.size()==0) {
		        		frame.getCanvas().setMate(true);
		        		return true;
		        	}
		    	}
		    	if(enemy.getType()==PieceType.QUEEN || enemy.getType()==PieceType.BISHOP || enemy.getType()==PieceType.PAWN) {
		    		ArrayList<Location> betweenPath = new ArrayList<Location>();
		    		int row = ((Piece)enemy).getLocation().getRow();
		    		for(int col = ((Piece)enemy).getLocation().getCol();col>((Piece)king).getLocation().getCol();col=(col>((Piece)king).getLocation().getCol()?col-1:col+1)) {
		    			betweenPath.add(new Location(((Piece)enemy).getLocation().getRow()>((Piece)king).getLocation().getRow()?row--:row++,col));
		    		}
		    		ArrayList<GamePiece> allyAttack = new ArrayList<GamePiece>();
		        	for(GamePiece piece:aas) {
		        		for(Location spot:betweenPath)
			        		if(board.getValidMovesForLocation(((Piece)piece).getLocation()).contains(spot))
			    				allyAttack.add(piece);
		        	}
		        	if(allyAttack.size()==0) {
		        		frame.getCanvas().setMate(true);
		        		return true;
		        	}
		    	}
		    	if(enemy.getType()==PieceType.QUEEN || enemy.getType()==PieceType.ROOK) {
		    		ArrayList<Location> betweenPath = new ArrayList<Location>();
		    		if(((Piece)enemy).getLocation().getRow()!=((Piece)king).getLocation().getRow()) 
			    		for(int row = ((Piece)enemy).getLocation().getRow();row>((Piece)king).getLocation().getRow();row=(row>((Piece)king).getLocation().getRow()?row-1:row+1)) 
			    			betweenPath.add(new Location(row,((Piece)enemy).getLocation().getCol()));
			    	else if(((Piece)enemy).getLocation().getRow()!=((Piece)king).getLocation().getRow()) 
			    		for(int col = ((Piece)enemy).getLocation().getCol();col>((Piece)king).getLocation().getCol();col=(col>((Piece)king).getLocation().getCol()?col-1:col+1)) 
			    			betweenPath.add(new Location(((Piece)enemy).getLocation().getRow(),col));	
		    		ArrayList<GamePiece> allyAttack = new ArrayList<GamePiece>();
		        	for(GamePiece piece:aas) {
		        		for(Location spot:betweenPath)
			        		if(board.getValidMovesForLocation(((Piece)piece).getLocation()).contains(spot))
			    				allyAttack.add(piece);
		        	}
		        	if(allyAttack.size()==0) {
		        		frame.getCanvas().setMate(true);
		        		return true;
		        	}
		    	}
    		}
    	}
    	return false;
    }
    public void stalemate()
    {
    	//TODO
    }
    public GamePiece getBlackKing()
    {
    	for(GamePiece b: aas)
    		if(b.getType().equals(PieceType.KING))
    			return b;
    	return null;
    }
    public GamePiece getWhiteKing()
    {
    	for(GamePiece b: whites)
    		if(b.getType().equals(PieceType.KING))
    			return b;
    	return null;
    }
    public boolean isInCheck(String color)
    {
//    	frame.getCanvas().setCheck(board.isInCheck(color),color);
//    	return board.isInCheck(color);
    	if(color.equals("black")) {
			for(GamePiece p:whites) {
				for(Location loc :board.getValidMovesForLocation(((Piece)p).getLocation()))
					if(board.getPieceAt(loc)!=null && board.getPieceAt(loc).getType()==PieceType.KING) {
						frame.getCanvas().setCheck(true,color);
						return true;
					}
			}
		}
    	if(color.equals("white")) {
			for(GamePiece p:aas) {
				for(Location loc :board.getValidMovesForLocation(((Piece)p).getLocation()))
					if(board.getPieceAt(loc)!=null && board.getPieceAt(loc).getType()==PieceType.KING){
						frame.getCanvas().setCheck(true,color);
						return true;
					}
			}
		}
    	frame.getCanvas().setCheck(false,color);
    	return false;
    	
    }
    
    public GameBoard getBoard(){
    	return board;
    }
    public GameBoard getBoardCopy(){
    	return new Board((Board)board);
    }
    public void setBoard(GameBoard board){
    	this.board = (Board)board;
    	moves.clear();
    	whites.clear();
		aas.clear();
		for(int r = 0; r<8; r++) for(int c =0; c<8; c++){
			GamePiece piece = board.getPieceAt(new Location(r,c));
			if (piece != null){
				if (piece.getColor().equals("white")) whites.add(piece);
				else aas.add(piece);
			}
		}
    	frame.getCanvas().setBoard(board);
    	frame.getCanvas().repaint();
    }
    public void setWhiteTurn(boolean whiteTurn, String pcolor){
    	isWhiteTurn = whiteTurn;
    	//if(moved) {
    		frame.getCanvas().setTurn(whiteTurn, pcolor);
    	   	frame.getCanvas().repaint();
    	//}
    }
    public void castle(boolean rookAt0,boolean isWhite)
    {
    	//checks are done in the king's validMoveLocations method
    	if(rookAt0 && isWhite)
    		board.movePiece(new Location(0,0), new Location(0,2));
    	else if(!rookAt0 && isWhite)
        	board.movePiece(new Location(0,7), new Location(0,4));
    	else if(rookAt0 && !isWhite)
        	board.movePiece(new Location(7,0), new Location(7,2));
    	else if(!rookAt0 && !isWhite)
        	board.movePiece(new Location(7,7), new Location(7,4));
    	
    }
    public void updateLoc(GamePiece p, Location loc) {
    	if(((Piece)p).getColor().equals("white")) {
    		((Piece)whites.get(whites.indexOf(p))).setLocation(loc);
    	}
    	else if(((Piece)p).getColor().equals("black")) {
    		((Piece)aas.get(aas.indexOf(p))).setLocation(loc);
    	}
    }

	public GameFrame getFrame() {
		return frame;
	}
}


