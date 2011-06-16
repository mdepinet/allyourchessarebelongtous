package productivity.chess.view;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import productivity.chess.model.Board;
import productivity.chess.model.GameBoard;
import productivity.chess.model.GamePiece;
import productivity.chess.model.Location;

public class GameCanvas extends Canvas implements MouseListener {
	private static final long serialVersionUID = 1L;
	private GameBoard board;
	private List<Location> moves;
	private String coordinates;
	//private Image king, queen, bishop, knight, rook, pawn;
	
	public GameCanvas() {
	  setBackground (Color.WHITE);
	  moves = new LinkedList<Location>();
	  board = new Board(); //Default board
	  coordinates = "";
	  System.out.print(board);
	  addMouseListener(this);
	}
	 public void paint (Graphics g) {
	   Graphics2D g2;
	   g2 = (Graphics2D) g;
	   g2.setColor(Color.BLACK);
	   Image background = null;
	   try {
		   background = ImageIO.read(new File("resources/images/board.png"));
		}
		catch(Throwable t) {}
		g2.drawImage(background, 50, 50, this);
	   for(int i = 0; i < 8; i++)
	   {
		   for(int j = 0; j < 8; j++)
		   {
			   GamePiece p = board.getPieceAt(new Location(i,j));
			   if(p!=null)
			   {
				   Image img = null;
				   try {
					    img = ImageIO.read(new File("resources/images/" + p.getColor()+p.getType()+".png"));
					}
					catch(Throwable t) {}
				   g2.drawImage(img, 50+(i*30), 52+(j*30), this);
			   }
		   }
	   }
	   if(!coordinates.equals(""))
	   {
		   g2.drawString(coordinates, 20, 300);
	   }
	   g2.setColor(new Color(1.0f,1.0f,0.0f,0.5f));
	   for(Location loc: moves)
	   {
		   g2.fillRect(51+(loc.getRow()*30), 50+(loc.getCol()*30), 29, 29);
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
    	boolean repaint = false;
    	Location loc = locationForClick(e.getX(), e.getY());
		if(!moves.isEmpty())
		{
			repaint = true;
			clearMoves();
		}
    	if(loc!=null && board.isValidLocation(loc.getRow(), loc.getCol())){
    		moves = board.getValidMovesForLocation(loc);
    		coordinates = "(" + loc.getCol() + ", " + loc.getRow() + ")";
    		if(!moves.isEmpty())
    			repaint=true;
    	}
    	else
    		coordinates = "";
    	if(repaint)
    		repaint();
    }
    public Location locationForClick(int x, int y)
    {
    	x = x-50;
    	y = y-50;
    	if(x>=0 && y>=0)
    	{
    		return new Location(x/30,y/30);
    	}
    	return null;
    }
	 
	public void setBoard(Board board) {
		this.board = board;
		repaint();
	}

	public GameBoard getBoard() {
		return board;
	}
	public void clearMoves()
	{
		moves.clear();
	}
	public List<Location> getMoves() {
		return moves;
	}
	public void setMoves(List<Location> moves) {
		this.moves = moves;
	}
}
