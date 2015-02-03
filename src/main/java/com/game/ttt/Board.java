package com.game.ttt;

/*
 * Represents a game board
 */
public class Board {

	private char[][] grid;
	
	private int ROWS;
	
	private int COLS;
	
	/*
	 * Creates a new game board
	 */
	public Board(int rows, int cols)
	{
		ROWS = rows;
		COLS = cols;
		grid = new char[ROWS][COLS];
		initialize();
	}
	
	/*
	 * Initializes the new game board
	 */
	public void initialize()
	{
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				grid[i][j] = '-';
			}
		}
	}

	/*
	 * Gets the value of the particular cell on the board
	 */
	public char getGridCell(int x, int y) {
		
		if(x<0 || x>=ROWS || y<0 && y>=COLS)
		{
			throw new IllegalArgumentException("coordinates not in the board");
		}
		
		return grid[x][y];
	}

	/*
	 * Sets the value of the particular cell on the board
	 */
	public void setGridCell(int x, int y, char ch) {
		
		if(x<0 || x>=ROWS || y<0 && y>=COLS)
		{
			throw new IllegalArgumentException("coordinates not in the board");
		}
		
		grid[x][y] = ch;
	}
	
	
}
