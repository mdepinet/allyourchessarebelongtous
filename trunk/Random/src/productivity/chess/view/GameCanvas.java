package productivity.chess.view;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import productivity.chess.model.Board;
import productivity.chess.model.GameBoard;
import productivity.chess.model.GamePiece;
import productivity.chess.model.Location;

public class GameCanvas extends Canvas {
	private static final long serialVersionUID = 1L;
	private GameBoard board;
	private List<Location> moves;
	private boolean flipped;
	private boolean whitesMove;
	private String pcolor;
	public GameCanvas(boolean flipped) {
	  setBackground (Color.WHITE);
	  whitesMove=true;
	  this.flipped = flipped;
	  pcolor="";
	  moves = new LinkedList<Location>();
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
		g2.drawString(((whitesMove && pcolor.equals("White")) || (!whitesMove && pcolor.equals("Black"))?"Your move":""), 25, 25);
		g2.drawImage(background, 50, 50, this);
		
	   for(int i = 0; i < 8; i++)
	   {
		   for(int j = 0; j < 8; j++)
		   {
			   GamePiece p = board.getPieceAt(new Location((flipped ? 7 : 2*i)-i,(flipped ? 7 : 2*j)-j));
			   if(p!=null)
			   {
				   Image img = null;
				   try {
					    img = ImageIO.read(new File("resources/images/" + p.getColor()+p.getType()+".png"));
					}
					catch(Throwable t) {}
				   g2.drawImage(img, 50+(j*30), 52+(i*30), this);
			   }
		   }
	   }
	   g2.setColor(new Color(1.0f,1.0f,0.0f,0.5f));
	   for(Location loc: moves)
	   {
		   g2.fillRect(51+(((flipped ? 7 : 2*loc.getCol())-loc.getCol())*30), 50+(((flipped ? 7 : 2*loc.getRow())-loc.getRow())*30), 29, 29);
	   }
	   //board=new Board(((Board)board).flipBoard());
	 }
	 
	public void setBoard(GameBoard board) {
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
	public void setTurn(boolean turn, String pcolor) {
		whitesMove = turn;
		this.pcolor=pcolor;
	}
	public boolean getTurn() {
		return whitesMove;
	}
}
