package productivity.chess.model;

import java.io.Serializable;
import java.util.List;

public interface GameBoard extends Serializable{
	public boolean canMove(String color);
	GamePiece getPieceAt(Location loc);
	GamePiece removePieceAt(Location lco);
	boolean isValidLocation(int row, int col);
	List<Location> getValidMovesForLocation(Location loc);
	GamePiece movePiece(Location loc1, Location loc2);
	boolean isInCheck(String color);
	boolean isBeingAttacked(String color, Location loc);
	boolean isCheckmate(boolean isWhite);
	GamePiece[][] copyBoard();
}
