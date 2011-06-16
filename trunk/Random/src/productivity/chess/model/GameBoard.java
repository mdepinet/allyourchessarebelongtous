package productivity.chess.model;

import java.io.Serializable;
import java.util.List;

public interface GameBoard extends Serializable{
	GamePiece getPieceAt(Location loc);
	boolean isValidLocation(int row, int col);
	List<Location> getValidMovesForLocation(Location loc);
}
