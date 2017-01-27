package Mechanics;

import Players.Player;

public class Game {

	public final static int DIMENSION = 10;
	private final Cell[][] board = new Cell[DIMENSION][DIMENSION];
	
	public Game() {
		reset();

	}
	
	public Game(Cell[][] b) {
		for(int i = 0; i < Game.DIMENSION; i++) {        
			for(int j = 0; j < Game.DIMENSION; j++) {
				if(b[i][j] == Cell.OFFBOARD) board[i][j] = Cell.OFFBOARD;
				if(b[i][j] == Cell.WHITE) board[i][j] = Cell.WHITE;
				if(b[i][j] == Cell.BLACK) board[i][j] = Cell.BLACK;
				if(b[i][j] == Cell.EMPTY) board[i][j] = Cell.EMPTY;
				
			}
		}
		
	}
	
	public Cell[][] getBoard() {
		Cell[][] b = new Cell[DIMENSION][DIMENSION];
		for(int i = 0; i < Game.DIMENSION; i++) {        
			for(int j = 0; j < Game.DIMENSION; j++) {
				b[i][j] = board[i][j];
				
			}
		}
		return b;
		
	}
	
	public boolean isLegal(int row, int col, Cell color) {
		return legalMove(row, col, color, false);
		
	}
	
	public void move(int row, int col, Cell color) {
		legalMove(row, col, color, true);
		board[row][col] = color;
		
	}
	
	public boolean legalMove(int r, int c, Cell color, boolean flip) {
		// Initialize boolean legal as false
		boolean legal = false;

		// If the cell is empty, begin the search
		// If the cell is not empty there is no need to check anything 
		// so the algorithm returns boolean legal as is
		if(board[r][c] == Cell.EMPTY) {
			// Initialize variables
			int posX;
			int posY;
			boolean found;
			Cell current;

			// Searches in each direction
			// x and y describe a given direction in 9 directions
			// 0, 0 is redundant and will break in the first check
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					// Variables to keep track of where the algorithm is and
					// whether it has found a valid move
					posX = c + x;
					posY = r + y;
					found = false;
					current = board[posY][posX];

					// Check the first cell in the direction specified by x and y
					// If the cell is empty, out of bounds or contains the same color
					// skip the rest of the algorithm to begin checking another direction
					if(current == Cell.OFFBOARD || current == Cell.EMPTY || current == color) {
						continue;
					}

					// Otherwise, check along that direction
					while (!found)
					{
						posX += x;
						posY += y;
//						System.out.println(r + " " + c);
//						System.out.println(x + " " + y);
//						System.out.println(posX + " " + posY);
						current = board[posY][posX];

						// If the algorithm finds another piece of the same color along a direction
						// end the loop to check a new direction, and set legal to true
						if (current == color)
						{
							found = true;
							legal = true;

							// If flip is true, reverse the directions and start flipping until 
							// the algorithm reaches the original location
							if (flip) {
								posX -= x;
								posY -= y;
								current = board[posY][posX];

								while(current != Cell.EMPTY) {
									board[posY][posX] = color;
									posX -= x;
									posY -= y;
									current = board[posY][posX];
								}
							}
						}
						// If the algorithm reaches an out of bounds area or an empty space
						// end the loop to check a new direction, but do not set legal to true yet
						else if (current == Cell.OFFBOARD || current == Cell.EMPTY)
						{
							found = true;
						}
					}
				}
			}
		}

		return legal;
	}
	
	public void reset() {
		for(int i = 0; i < DIMENSION; i++) {     
			board[i][0] = Cell.OFFBOARD;
			board[i][DIMENSION - 1] = Cell.OFFBOARD;
			board[0][i] = Cell.OFFBOARD;
			board[DIMENSION - 1][i] = Cell.OFFBOARD;

		}

		for(int i = 1; i < DIMENSION - 1; i++) {   
			for(int j = 1; j < DIMENSION - 1; j++) {   
				board[i][j] = Cell.EMPTY;

			}

		}
		
		board[4][4] = Cell.WHITE;
		board[5][5] = Cell.WHITE;
		board[4][5] = Cell.BLACK;
		board[5][4] = Cell.BLACK;
	}
	
	public Player.Type getWinner() {
		int black = 0;
		int white = 0;
		
		for(int i = 1; i < Game.DIMENSION - 1; i++) {        
			for(int j = 1; j < Game.DIMENSION - 1; j++) {
				if(board[i][j] == Cell.BLACK) black++;
				if(board[i][j] == Cell.WHITE) white++;
			}
		}
		
		if(black == white) return Player.Type.NOBODY;
		if(black > white) return Player.Type.BLACK;
		return Player.Type.WHITE;
		
	}
	
	public void printBoard()
	{
		for (int i = 1; i <= DIMENSION - 2; i++)
		{
			for (int j = 1; j <= DIMENSION - 2; j++)
			{
				System.out.print("[" + board[i][j] + "]");
			}
			System.out.println();
		}
	}
	
	
}