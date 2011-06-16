package productivity.chess.model;

public class Location {
	private int col;
	private int row;
	public Location(int x, int y)
	{
		setCol(x);
		setRow(y);
	}
	public boolean isValid()
	{
		return (col>0&&col<=8&&row>0&&row<=8);
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
}
