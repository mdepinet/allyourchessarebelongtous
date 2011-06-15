package productivity.chess.model;
import java.awt.Color;
import java.io.Serializable;

public class Piece implements Serializable {
	private static final long serialVersionUID = 1L;
	private PieceType type;
	private Color color;
	
	public Piece(String s, Color col)
	{
		setType(getTypeForString(s));
		setColor(col);
	}
	public PieceType getTypeForString(String s)
	{
		if(s.equals("X"))
			return null;
		else if(s.equals("P"))
			return PieceType.PAWN;
		else if(s.equals("R"))
			return PieceType.ROOK;
		else if(s.equals("K"))
			return PieceType.KNIGHT;
		else if(s.equals("B"))
			return PieceType.BISHOP;
		else if(s.equals("Q"))
			return PieceType.QUEEN;
		else
			return PieceType.KING;
	}
	public void setType(PieceType type) {
		this.type = type;
	}
	public PieceType getType() {
		return type;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public Color getColor() {
		return color;
	}
}
