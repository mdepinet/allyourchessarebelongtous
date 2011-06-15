package productivity.chess.model;
import java.awt.Color;
import java.io.Serializable;

public class Piece implements Serializable {
	private static final long serialVersionUID = 1L;
	private PieceType type;
	private Color color;
	private int scratch;
	
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
	public Color getOppositeColor() {
		return isWhite() ? Color.BLACK : Color.WHITE;
	}
	public boolean isWhite(){
		return color.equals(Color.WHITE);
	}
	
	public void setHasMoved(boolean moved){
		if (type == PieceType.KING) scratch = moved ? 1 : 0;
		else throw new UnsupportedOperationException("hasMoved can only be set for kings");
	}
	public boolean hasMoved(){
		if (type == PieceType.KING) return scratch != 0;
		else throw new UnsupportedOperationException("hasMoved can only be accessed for kings");
	}
	public int getMovesAlone(){
		if (type == PieceType.KING) return scratch;
		else throw new UnsupportedOperationException("movesAlone can only be accessed for kings");
	}
	public void incMovesAlone(){
		if (type == PieceType.KING) scratch++;
		else throw new UnsupportedOperationException("movesAlone can only be set for kings");
	}
	public void setAlone(){
		if (type == PieceType.KING) scratch = 0;
		else throw new UnsupportedOperationException("movesAlone can only be set for kings");
	}
	public int getLastMoved(){
		if (type == PieceType.PAWN) return scratch;
		else throw new UnsupportedOperationException("lastMoved can only be accessed for pawns");
	}
	public void resetLastMoved(){
		if (type == PieceType.PAWN) scratch = 0;
		else throw new UnsupportedOperationException("lastMoved can only be set for pawns");
	}
	public void incLastMoved(){
		if (type == PieceType.PAWN) scratch++;
		else throw new UnsupportedOperationException("lastMoved can only be set for pawns");
	}
}
