package productivity.chess.model;

import java.io.Serializable;

public interface GameBoard extends Serializable{
	GamePiece getPieceAt(Location loc);
}
