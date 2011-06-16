package productivity.chess.model;

import java.io.Serializable;

public interface GamePiece extends Serializable {
	PieceType getType();
	String getColor();
}
