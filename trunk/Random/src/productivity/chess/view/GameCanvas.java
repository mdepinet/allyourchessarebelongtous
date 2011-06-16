package productivity.chess.view;

import java.awt.*;
import java.io.File;

import javax.imageio.ImageIO;

import productivity.chess.model.*;

public class GameCanvas extends Canvas {
	private static final long serialVersionUID = 1L;
	private Board board;
	private Image king, queen, bishop, knight, rook, pawn;
	
	public GameCanvas() {
	  setBackground (Color.WHITE);
	  board = new Board();
	  System.out.print(board);
	}
	 public void paint (Graphics g) {
	   Graphics2D g2;
	   g2 = (Graphics2D) g;
	   
	   for(int i = 0; i < 8; i++)
	   {
		   for(int j = 0; j < 8; j++)
		   {
			   g2.drawRect(50+(i*30), 50+(j*30), 30, 30);
			   if(i%2==0)
			   {
				   if(j%2==0)
					   g2.fillRect(50+(i*30), 50+(j*30), 30, 30);
			   }
			   else
			   {
				   if(j%2==1)
					   g2.fillRect(50+(i*30), 50+(j*30), 30, 30);
			   }
			   Piece p = board.getPieceAt(new Location(i,j));
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
	 }

	public void setBoard(Board board) {
		this.board = board;
		repaint();
	}

	public Board getBoard() {
		return board;
	}
}
