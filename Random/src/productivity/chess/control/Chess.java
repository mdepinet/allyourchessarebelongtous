package productivity.chess.control;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import productivity.chess.model.Board;
import productivity.chess.model.GameBoard;
import productivity.chess.model.GamePiece;
import productivity.chess.model.Location;
import productivity.chess.model.Piece;
import productivity.chess.model.PieceType;
import productivity.chess.view.GameFrame;

public class Chess implements MouseListener{
	private GameBoard board;
	private GameFrame frame;
	private List<GamePiece> whites;
	private List<GamePiece> aas;
	private List<Location> moves;
	private Location selected;
	private boolean isWhiteTurn;
	
	public Chess()
	{
		board = new Board();
		frame = new GameFrame("Chess", board, (MouseListener) this);
		moves = new LinkedList<Location>();
		frame.getCanvas().setMoves(moves);
		selected = null;
		isWhiteTurn=true;
		whites= new ArrayList<GamePiece>();
		aas = new ArrayList<GamePiece>();
		for(int r = 0; r<8; r++)
			for(int c =0; c<8; c++){
				GamePiece piece = board.getPieceAt(new Location(r,c));
				if(piece.getColor().equals("white"))
					whites.add(piece);
				else
					aas.add(piece);
			}
	}
	public static void main(String[] args)
	{
		new Chess();
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
    		boolean correctTurn = isWhiteTurn==(board.getPieceAt(loc).getColor().equals("white"));
	    	
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
	    	if(repaint)
	    		frame.getCanvas().repaint();
	    	
	    	if (board.getPieceAt(loc) != null && correctTurn) selected = loc;
    	}
    	else{
    		//System.out.println(((Board)board).canMove("white"));
    		Location loc = locationForClick(e.getY(), e.getX());
    		if (moves.contains(loc)){
    			GamePiece taken = board.movePiece(selected,loc);
    			updateStatus(selected, loc, taken);
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
    		return new Location(y/30,x/30);
    	}
    	return null;
    }
    public void updateStatus(Location prev, Location curr, GamePiece taken)
    {
    	whites.remove(taken);
    	aas.remove(taken);
    	Piece piece =(Piece) board.getPieceAt(curr);
    	boolean isWhite = piece.getColor().equals("white");
    	switch(piece.getType()){
    	case PAWN:
    		break;
    	case KING:
    		if(isWhite && whites.size()==1)
    			piece.incMovesAlone();
    		else if(!isWhite && aas.size()==1)
    			piece.incMovesAlone();
    		if(piece.getMovesAlone()>50)
    			stalemate();
    	case ROOK:
    		piece.setHasMoved();
    		break;
    	}
    	isWhiteTurn=!isWhiteTurn;
    	if (!board.canMove(isWhite ? "black" : "white")){
    		if ((isWhite && isInCheck("black")) || (!isWhite && isInCheck("white"))) checkmate(isWhite);
    		else stalemate();
    	}
    	//TODO
    	//increments ints for how long each pawn has been stationary, updates hasMoved for kings and rooks, check En passant
    }
    public void checkmate(boolean isWhite)
    {
    	//TODO
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
    	return board.isInCheck(color);
    	
    }
}


