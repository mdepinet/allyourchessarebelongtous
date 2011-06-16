package productivity.chess.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import productivity.chess.model.*;

public class GameCanvas extends Canvas implements MouseListener {
	private static final long serialVersionUID = 1L;
	private Board board;
	private ArrayList<Location> moves;
	//private Image king, queen, bishop, knight, rook, pawn;
	
	public GameCanvas() {
	  setBackground (Color.WHITE);
	  moves = new ArrayList<Location>();
	  board = new Board(); //Default board
	  System.out.print(board);
	}
	 public void paint (Graphics g) {
	   Graphics2D g2;
	   g2 = (Graphics2D) g;
	   g2.setColor(Color.BLACK);
	   for(int i = 0; i < 8; i++)
	   {
		   for(int j = 0; j < 8; j++)
		   {
			   g2.drawRect(50+(i*30), 50+(j*30), 30, 30);
			   if(i%2==0)
			   {
				   if(j%2==1)
					   g2.fillRect(50+(i*30), 50+(j*30), 30, 30);
			   }
			   else
			   {
				   if(j%2==0)
					   g2.fillRect(50+(i*30), 50+(j*30), 30, 30);
			   }
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
	   g2.setColor(new Color(1.0f,1.0f,0.0f,0.5f));
	   for(Location loc: moves)
	   {
		   g2.fillRect(50+(loc.getRow()*30), 50+(loc.getCol()*30), 30, 30);
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
    	Location loc = new Location((e.getX()-50)/30,(e.getY()-50)/30);
    	if(loc.isValid())
    		moves = board.getValidMovesForLocation(loc);
       repaint();
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
		moves = new ArrayList<Location>();
	}
	public ArrayList<Location> getMoves() {
		return moves;
	}
	public void setMoves(ArrayList<Location> moves) {
		this.moves = moves;
	}
}
