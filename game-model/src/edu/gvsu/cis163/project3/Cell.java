package edu.gvsu.cis163.project3;

/**
 * Created by Hans Dulimarta (Summer 2014)
 */
public class Cell implements Comparable<Cell> {
	public int row, column, value;
	
	public Cell()
	{
		this(0,0,0);
	}
	public Cell (int r, int c, int v)
	{
		row = r;
		column = c;
		value = v;
	}
	
	@Override
	public String toString() {
		return "Cell{" +
				"row=" + row +
				", column=" + column +
				", value=" + value +
				'}';
	}
	
	@Override
	public int compareTo (Cell other) {
		if (this.row < other.row) return -1;
		if (this.row > other.row) return +1;

        /* break the tie using column */
		if (this.column < other.column) return -1;
		if (this.column > other.column) return +1;
		
		return 0; //this.value - other.value;
	}
}
