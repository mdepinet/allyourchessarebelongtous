package productivity.chess.model;

import java.io.Serializable;

public class Location implements Serializable {
	private static final long serialVersionUID = 1L;
	private int col;
	private int row;
	public Location(int y, int x)
	{
		setRow(y);
		setCol(x);
	}
	public void setCol(int y) {
		this.col = y;
	}
	public int getCol() {
		return col;
	}
	public void setRow(int x) {
		this.row = x;
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
