package productivity.chess.model;

public class Location {
	private int col;
	private int row;
	public Location(int x, int y)
	{
		setRow(x);
		setCol(y);
	}
	public void setCol(int x) {
		this.col = x;
	}
	public int getCol() {
		return col;
	}
	public void setRow(int y) {
		this.row = y;
	}
	public int getRow() {
		return row;
	}
	public String toString()
	{
		return "Row: "+row+" Col: "+col;
	}
	
	@Override
	public boolean equals(Object obj){
		if (!(obj instanceof Location)) return false;
		Location loc = (Location)obj;
		return getRow() == loc.getRow() && getCol() == loc.getCol();
	}
}
