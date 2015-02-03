package com.game.ttt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/*
* Represents a tic-tac-toe game
*/
public class TicTacToe {

	private int ROWS = 3;
	private int COLS = 3;

	Board board;

	int[][] rowsSum = new int[ROWS][2];
	int[][] colsSum = new int[COLS][2];
	int[][] diagonalsSum = new int[2][2];

	List<Integer> freeSpaces;
	List<Integer> nextPossibleMoves;

	Player computer, user;

	private Player winner;

	public TicTacToe() {
		initialize();
	}

	/*
	 Initializes the resources required to start the game 
	*/
	public void initialize() {
		
		board = new Board(ROWS, COLS);
		
		for(int i =0; i<ROWS; i++)
		{
			for(int j =0; j<2; j++)
			{
				rowsSum[i][j] = 0;
			}
		}
		
		
		for(int i =0; i<COLS; i++)
		{
			for(int j =0; j<2; j++)
			{
				colsSum[i][j] = 0;
			}
		}
		
		for(int i =0; i<2; i++)
		{
			for(int j =0; j<2; j++)
			{
				diagonalsSum[i][j] = 0;
			}
		}
		
		freeSpaces = new LinkedList<Integer>();

		
		for (int i = 0; i < (ROWS * COLS); i++)
			freeSpaces.add(i);

		
		computer = new Player(0, 'O', "computer");
		user = new Player(1, 'X', "user");
		winner = null;		

	}

	/*
	 * 1. If game is not finished, Sets the choice the user has made
	 * 2. If game is not finished, computes and sets the computer choice
	 * 
	 * returns the computer choice
	 */
	public String playTicTacToe(int choice) {

		if (!isGameFinished()) {
			setUserChoice(choice);
		}

		int computerChoice = -1;

		if (!isGameFinished()) {
			computerChoice = setComputerChoice();
		}

		return "" + computerChoice;

	}

	/*
	 * Gets the winner of the game
	 */
	public Player getWinner() {
		return winner;
	}

	/*
	 * Sets the winner of the game
	 */
	public void setWinner(Player winner) {
		this.winner = winner;
	}

	/*
	 * Sets the user choice
	 */
	public void setUserChoice(int choice) {

		int rowIndex = choice / ROWS;
		int colIndex = choice % COLS;

		board.setGridCell(rowIndex, colIndex, 'X');

		addToCounts(rowIndex, colIndex, user);

		freeSpaces.remove(new Integer(choice));
	}

	/*
	 * Calculates and sets the computer choice
	 */
	public int setComputerChoice() {
		Random random = new Random();

		//Select a random cell (either center or any one of the corners) on the board as it is the first move
		if (freeSpaces.size() == ROWS * COLS) {
			
			int[] priorityList = {0, COLS-1, (ROWS / 2) * ROWS + (COLS / 2), ROWS-1, ROWS*COLS-1};
			int nextMove = priorityList[random.nextInt(priorityList.length)];
			return setChoice(computer, nextMove);
			
		} 
		/* 1. Check if computer can win in this chance. If so, make the move and win it
		 * 2. If not, Check if user can win in the next chance. If so, block the user from winning
		 * 3. If not, check if computer can make a move such that it can win for sure in the following chance
		 */
		else if (findSquaresForNextMove(2, 0) || findSquaresForNextMove(0, 2) || IsDoubleWinMovePossible(computer)) {

            
			int nextMove = nextPossibleMoves.get(random.nextInt(nextPossibleMoves.size()));
			return setChoice(computer, nextMove);

		} 
		/*
		 * Select any cell in a row, column or diagonal which already has one cell in it set by the computer
		 * But after selecting check if it is a safe move, if not, try if we can find another cell which is safe
		 */
		else if (findSquaresForNextMove(1, 0)) {
			
			int nextMove = (ROWS / 2) * ROWS + (COLS / 2); //center			
			
			List<Integer> nextPossibleMovesCopy = new LinkedList<Integer>(nextPossibleMoves);						
			Collections.shuffle(nextPossibleMovesCopy);
			
			ListIterator<Integer> iterator = nextPossibleMovesCopy.listIterator();

			while (iterator.hasNext()) {
				nextMove = iterator.next();
				if (checkIfSafeMove(nextMove))
					break;
			}

			return setChoice(computer, nextMove);
		
		} 
		/*
		 * Select the center of the board if available
		 */
		else if (!isSet(ROWS / 2, COLS / 2)) {
		
			return setChoice(computer, (ROWS / 2) * ROWS + (COLS / 2));
		
		}
		/*
		 * Select any of the the corner cells if available
		 */
		else if (isCornerSquareAvailable()) {
			
			int nextMove = nextPossibleMoves.get(random.nextInt(nextPossibleMoves.size()));			
			return setChoice(computer, nextMove);
			
		}
		/*
		 * Select any of the remaining cells
		 */
		else {
			
			int nextMove = freeSpaces.get(random.nextInt(freeSpaces.size()));
			return setChoice(computer, nextMove);
		
		}

	}

	/*
	 * Checks if any of the corner squares are available
	 */
	public boolean isCornerSquareAvailable() {
		nextPossibleMoves = new LinkedList<Integer>(Arrays.asList(0, ROWS - 1,
				(ROWS - 1) * COLS, (ROWS * COLS) - 1));

		Iterator<Integer> iterator = nextPossibleMoves.iterator();

		while (iterator.hasNext()) {
			int next = iterator.next();

			if (!freeSpaces.contains(next))
				iterator.remove();

		}

		return nextPossibleMoves.size() > 0;
	}

	/*
	 * Checks if the choice made is a safe one.
	 */
	public boolean checkIfSafeMove(int nextMove) {
		setChoice(computer, nextMove);

		boolean safeMove = true;

		if (findSquaresForNextMove(2, 0)) {
			int userMove = nextPossibleMoves.get(0);
			setChoice(user, userMove);

			if (findSquaresForNextMove(0, 2) && nextPossibleMoves.size() >1) {
				safeMove = false;
			}

			resetLastChoice(user, userMove);
		}

		resetLastChoice(computer, nextMove);

		return safeMove;

	}

	/*
	 * Double Win Move : By making this move, the player1 is setting up the game such that the 
	 * player2 will not be able to block player 1 from winning in the following move
	 */
	public boolean IsDoubleWinMovePossible(Player player) {
		List<Integer> freeSpacesCopy = new LinkedList<Integer>(freeSpaces);

		Iterator<Integer> iterator = freeSpacesCopy.iterator();

		boolean doubleWinMoveNotPossible = true;

		while (iterator.hasNext() && doubleWinMoveNotPossible) {
			int nextMove = iterator.next();
			setChoice(player, nextMove);

			if (findSquaresForNextMove(2, 0) && nextPossibleMoves.size() > 1) {
				doubleWinMoveNotPossible = false;
			}

			resetLastChoice(player, nextMove);

		}

		return !doubleWinMoveNotPossible;
	}

	/*
	 * Resets the last choice that was made as it is not a safe move
	 */
	public void resetLastChoice(Player player, int nextMove) {
		int rowIndex = nextMove / COLS;
		int colIndex = nextMove % COLS;

		board.setGridCell(rowIndex, colIndex, '-');

		subtractFromCounts(rowIndex, colIndex, player);

		freeSpaces.add(nextMove);
	}

	/*
	 * Subtracts the counts from the sum arrays as the calculated move is not a safe one
	 */
	public void subtractFromCounts(int row, int col, Player player) {

		rowsSum[row][player.ID] -= 1;
		colsSum[col][player.ID] -= 1;

		if (row == col)
			diagonalsSum[0][player.ID] -= 1;

		if (Math.abs(row - col) == 2 || (row == 1 && col == 1))
			diagonalsSum[1][player.ID] -= 1;
	}

	/*
	 * Sets the choice made by the player
	 */
	public int setChoice(Player player, int nextMove) {

		int rowIndex = nextMove / COLS;
		int colIndex = nextMove % COLS;

		board.setGridCell(rowIndex, colIndex, player.getPlaceHolder());

		addToCounts(rowIndex, colIndex, player);

		freeSpaces.remove(new Integer(rowIndex * ROWS + colIndex));

		return nextMove;
	}

	/*
	 * finds all possible cells for the next move
	 *  @param - sumO - sum of cells that player1(computer) has set in any row, column or diagonal
	 *  @param - sumX - sum of cells that player2(user) has set in any row, column or diagonal
	 */
	public boolean findSquaresForNextMove(int sumO, int sumX) {
		nextPossibleMoves = new ArrayList<Integer>();

		findSquaresInRows(sumO, sumX);

		findSquaresInCols(sumO, sumX);

		findSquaresInDiagonals(sumO, sumX);

		return !nextPossibleMoves.isEmpty();

	}

	/*
	 * checks for the sums in all the rows. If anything is found, add it to the nextPossibleMoves
	 */
	public void findSquaresInRows(int sumO, int sumX) {
		for (int i = 0; i < ROWS; i++) {
			if (rowsSum[i][computer.ID] == sumO && rowsSum[i][user.ID] == sumX) {
				for (int j = 0; j < COLS; j++) {
					if (!isSet(i, j))
						nextPossibleMoves.add(i * COLS + j);
				}

			}
		}
	}

	/*
	 * checks for the sums in all the columns. If anything is found, add it to the nextPossibleMoves
	 */
	public void findSquaresInCols(int sumO, int sumX) {
		for (int i = 0; i < COLS; i++) {
			if (colsSum[i][computer.ID] == sumO && colsSum[i][user.ID] == sumX) {
				for (int j = 0; j < ROWS; j++) {
					if (!isSet(j, i))
						nextPossibleMoves.add(j * ROWS + i);
				}
			}
		}
	}

	/*
	 * checks for the sums in all the diagonals. If anything is found, add it to the nextPossibleMoves
	 */
	public void findSquaresInDiagonals(int sumO, int sumX) {

		// 1st diagonal
		if (diagonalsSum[0][computer.ID] == sumO
				&& diagonalsSum[0][user.ID] == sumX) {
			for (int i = 0, j = 0; i < 3 && j < 3; i++, j++) {
				if (!isSet(i, j))
					nextPossibleMoves.add(i * COLS + j);
			}
		}

		// 2nd diagonal
		if (diagonalsSum[1][computer.ID] == sumO
				&& diagonalsSum[1][user.ID] == sumX) {
			for (int i = 0, j = 2; i < 3 && j >= 0; i++, j--) {
				if (!isSet(i, j))
					nextPossibleMoves.add(i * ROWS + j);
			}
		}

	}

	/*
	 * BookKeeping - Maintain the count of choices the player has made in a particular row, column, diagonal
	 */
	public void addToCounts(int x, int y, Player player) {
		rowsSum[x][player.ID] += 1;
		colsSum[y][player.ID] += 1;

		if (x == y)
			diagonalsSum[0][player.ID] += 1;

		if (Math.abs(x - y) == 2 || (x == 1 && y == 1))
			diagonalsSum[1][player.ID] += 1;

	}

	/*
	 * Checks if a particular grid cell is already Set
	 */
	public boolean isSet(int x, int y) {
		return (board.getGridCell(x, y)== computer.getPlaceHolder() || board.getGridCell(x, y) == user
				.getPlaceHolder());
	}

	/*
	 * Checks if the game is finished
	 */
	public boolean isGameFinished() {

		if (checkIfGameIsWon(3, 0)) {
			winner = computer;
			return true;
		}

		if (checkIfGameIsWon(0, 3)) {
			winner = user;
			return true;
		}

		if (isGameDrawn()) {
			return true;
		}

		return false;
	}

	/*
	 * Checks if the game is won by one of the two players
	 */
	public boolean checkIfGameIsWon(int sumO, int sumX) {

		for (int i = 0; i < ROWS; i++) {
			if (rowsSum[i][computer.ID] == sumO && rowsSum[i][user.ID] == sumX) {
				return true;
			}
		}

		for (int i = 0; i < COLS; i++) {
			if (colsSum[i][computer.ID] == sumO && colsSum[i][user.ID] == sumX) {
				return true;
			}
		}

		if (diagonalsSum[0][computer.ID] == sumO
				&& diagonalsSum[0][user.ID] == sumX) {
			return true;
		}

		if (diagonalsSum[1][computer.ID] == sumO
				&& diagonalsSum[1][user.ID] == sumX) {
			return true;

		}

		return false;
	}

	/*
	 * Checks if the game is a draw
	 */
	public boolean isGameDrawn() {
		return freeSpaces.size() == 0;
	}

}
