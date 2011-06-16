package productivity.chess.control;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;

import productivity.chess.model.Board;
import productivity.chess.model.GameBoard;
import productivity.chess.model.Location;
import productivity.chess.view.GameFrame;

public class Chess implements MouseListener{
	private GameBoard board;
	private GameFrame frame;
	private List<Location> moves;
	private Location selected;
	
	public Chess()
	{
		board = new Board();
		frame = new GameFrame("Chess", board, (MouseListener) this);
		moves = new LinkedList<Location>();
		frame.getCanvas().setMoves(moves);
		selected = null;
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
	    	boolean repaint = false;
	    	Location loc = locationForClick(e.getY(), e.getX());
			if(!moves.isEmpty())
			{
				repaint = true;
				frame.getCanvas().clearMoves();
			}
	    	if(loc!=null && board.isValidLocation(loc.getRow(), loc.getCol())){
	    		moves.clear();
	    		moves.addAll(board.getValidMovesForLocation(loc));
	    		if(!moves.isEmpty())
	    			repaint=true;
	    	}
	    	if(repaint)
	    		frame.getCanvas().repaint();
	    	
	    	if (board.getPieceAt(loc) != null) selected = loc;
    	}
    	else{
    		//System.out.println(((Board)board).canMove("white"));
    		Location loc = locationForClick(e.getY(), e.getX());
    		if (moves.contains(loc)) board.movePiece(selected,loc);
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
	
}
