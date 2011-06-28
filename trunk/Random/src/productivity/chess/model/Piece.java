package productivity.chess.model;

public class Piece implements GamePiece {
	private static final long serialVersionUID = 1L;
	private PieceType type;
	private String color;
	private int scratch;
	private Location loc;
	private String status;
	
	public Piece(String s, String col)
	{
		setType(getTypeForString(s));
		setColor(col);
		status="";
	}
	public Piece(String s, String col, Location loc)
	{
		setType(getTypeForString(s));
		setColor(col);
		this.loc = loc;
		status="";
	}
	public Piece(Piece p)
	{
		setType(p.getType());
		setColor(p.getColor());
		status="";
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
		else if(s.equals("D"))
			return PieceType.DUMMY;
		else
			return PieceType.KING;
	}
	public void setType(PieceType type) {
		this.type = type;
	}
	public PieceType getType() {
		return type;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getColor() {
		return color;
	}
	public String getOppositeColor() {
		return isWhite() ? "black" : "white";
	}
	public boolean isWhite(){
		return color.equals("white");
	}
	public void setPieceType(PieceType type)
	{
		this.type=type;
	}
	public boolean equals(Object o){
		if(!(o instanceof Piece)) return false;
		Piece p = (Piece)o;
		if(p.getType()!=getType()) return false;
		if(!p.getColor().equals(getColor())) return false;
		return true;
	}
	public void setHasMoved(){
		if (type == PieceType.KING || type == PieceType.ROOK || type == PieceType.PAWN) {
			if(scratch>0) return; 
			scratch =  1;
			}
		else throw new UnsupportedOperationException("hasMoved can only be set for kings and rooks");
	}
	public boolean hasMoved(){
		if (type == PieceType.KING || type == PieceType.ROOK || type == PieceType.PAWN) return scratch != 0;
		else throw new UnsupportedOperationException("hasMoved can only be accessed for kings, rooks, and pawns");
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
	public Location getLocation() {
		return loc;
	}
	public void setLocation(Location loc) {
		this.loc = loc;
	}
	public void setLocation(int row, int col) {
		this.loc = new Location(row,col);
	}
	public void setStatus(String s) {
		status=s;
	}
	public String getStatus() {
		return status;
	}
	public String toString() {
		if(type.equals(PieceType.PAWN)) return color + "PAWN";
		if(type.equals(PieceType.KING)) return color + "KING";
		if(type.equals(PieceType.QUEEN)) return color + "QUEEN";
		if(type.equals(PieceType.BISHOP)) return color + "BISHOP";
		if(type.equals(PieceType.KNIGHT)) return color + "KNIGHT";
		if(type.equals(PieceType.ROOK)) return color + "ROOK";
		if(type.equals(PieceType.DUMMY)) return color + "DUMMY";
		return "";
	}
}
