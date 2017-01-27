package Mechanics;

public class Position {

	private int row;
	private int column;
	
	public Position(int row, int column) {
		this.row = row;
		this.column = column;
		
	}
	
	public int getRow() {
		return this.row;
		
	}
	
	public int getColumn() {
		return this.column;
		
	}
	
	public String toString() {
		return this.row + " " + this.column;
		
	}

	public static boolean isSame(Position position, Position chosenMove) {
		return position.row == chosenMove.row && position.column == chosenMove.column;
		
	}
	
	
}
