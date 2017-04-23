package edu.gvsu.cis163.project3;

import com.sun.istack.internal.Nullable;
import edu.gvsu.cis163.project3.interfaces.NumberSlider;
import edu.gvsu.cis163.project3.interfaces.NumberSliderObserver;

import java.util.*;

import static edu.gvsu.cis163.project3.GameStatus.*;
import static java.util.concurrent.ThreadLocalRandom.current;


/***************************************************
 * easy-48 - edu.gvsu.prestongarno - by Preston Garno
 ****************************************/
public class NumberGame implements NumberSlider {


	/*****************************************
	 * the game status
	 ****************************************/
	private GameStatus status;
	/*****************************************
	 * The amount of rows and columns
	 ****************************************/
	private int        rows, columns;
	/*****************************************
	 * the game board
	 ****************************************/
	private int[][]                    board;
	/*****************************************
	 * winning value
	 ****************************************/
	private int                        winningVal;
	private List<NumberSliderObserver> observers;
	private int                        score;
	private int                        cellCount;

	/*****************************************
	 * Creates a new board
	 * @param rows the number of rows
	 * @param columns the number of columns
	 ****************************************/
	public NumberGame(int rows, int columns) {
		this(rows, columns, null);
	}

	/*****************************************
	 * Creates a new board
	 * @param rows the number of rows
	 * @param columns the number of columns
	 * @param observer start the game with an observer, nullable
	 ****************************************/
	public NumberGame(int rows, int columns, @Nullable NumberSliderObserver observer) {
		this.observers = new ArrayList<>(1);
		if (observer != null) observers.add(observer);

		status = IN_PROGRESS;

		this.rows = rows;
		this.columns = columns;
		this.cellCount = 0;

		board = new int[rows][columns];
		this.score = 0;
		this.winningVal = 1024;
	}

	/*****************************************
	 * Static factory
	 * @param rows the rows to be set
	 * @param columns the columns to be set
	 * @return a new NumberSlider instance
	 ****************************************/
	public static NumberSlider createInstance(int rows, int columns) {
		return new NumberGame(rows, columns);
	}

	/*****************************************
	 * @param c the x index
	 * @param r the y index
	 * @return the value at the location
	 * @throws IllegalArgumentException if out of bounds
	 ****************************************/
	public int getAt(int r, int c) {
		if (r < 0 | r > rows || c < 0 | c > columns)
			throw new IllegalArgumentException();
		return board[r][c];
	}

	/*****************************************
	 * Getter for property 'rows'.
	 * @return Value for property 'rows'.
	 ****************************************/
	public int rows() {
		return rows;
	}

	/*****************************************
	 * Getter for property 'columns'.
	 * @return Value for property 'columns'.
	 ****************************************/
	public int columns() {
		return columns;
	}

	/*****************************************
	 * @param height the number of rows in the board
	 * @param width the number of columns in the board
	 * @param winningValue the value that must appear on the board to
	 *                     win the game
	 * @throws IllegalArgumentException when the winning value is not power of two
	 *  or negative
	 ****************************************/
	@Override
	public void resizeBoard(int width, int height, int winningValue) {
		int ii = 2;
		for (int i = 2; i <= winningValue; i *= 2) {
			ii = i;
		}
		if (ii != winningValue | width < 2 | height < 2) throw new IllegalArgumentException();

		this.score = 0;
		this.cellCount = 0;
		this.winningVal = winningValue;
		this.rows = width;
		this.columns = height;
		this.board = new int[rows][columns];

		int[][] prev = UndoStack.getInstance().peek();

		this.setStatus(IN_PROGRESS);

		if (prev == null) {
			placeRandomValue();
			placeRandomValue();
			return;
		}

		for (int r = 0; r < width; r++) {
			for (int c = 0; c < height; c++) {
				if (r < width && c < height) {
					cellCount++;
					board[r][c] = prev[r][c];
				}
			}
		}
		if (this.score == 0) {
			this.placeRandomValue();
			this.placeRandomValue();
		}
	}

	/*****************************************
	 * Test method only -- Do not touch
	 ****************************************/
	public void setAt(int r, int c, int v) {
		this.board[r][c] = v;
	}

	/*****************************************
	 * reset board
	 ****************************************/
	@Override
	public void reset() {
		this.board = new int[rows][columns];
		this.status = IN_PROGRESS;
		UndoStack.getInstance().clearStack();
		this.cellCount = 0;
		this.score = 0;
		placeRandomValue();
		placeRandomValue();
	}

	/*****************************************
	 * @return a Cell object with its row, column, and value attributes
	 *  initialized properly
	 ****************************************/
	@Nullable
	@Override
	public Cell placeRandomValue() {
		int  rRand, cRand;
		Cell placed = null;
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[0].length; c++) {

				rRand = current().nextInt(0, board.length);
				cRand = current().nextInt(0, board[0].length);

				if (board[rRand][cRand] == 0) {
					int set = (current().nextInt(2, 6) == 5) ? 4 : 2;
					this.board[rRand][cRand] = set;
					placed = new Cell(rRand, cRand, set);
					notifyPlaced(placed);
					break;
				}
			}
			if(placed!=null) break;
		}
		if(placed == null) placed = placeDetermined();

		if (++cellCount >= ( board.length ) * ( board[0].length ) &&
				!hasMoveAt(placed.row, placed.column)) { // no nulls, will end game before this happens
			setStatus(placed, USER_LOST);
		}
		return placed;
	}

	void setCellCount(int count) {
		this.cellCount = count;
	}

	private Cell placeDetermined() {
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[0].length; c++) {
				if (board[r][c] == 0) {
					int set = (current().nextInt(2, 6) == 5) ? 4 : 2;
					this.board[r][c] = set;
					Cell cell = new Cell(r, c, set);
					notifyPlaced(cell);
					return cell;
				}
			}
		}
		throw new IllegalStateException();
	}

	private boolean hasMoveAt(int r, int c) {
		return (r != 0 && board[r - 1][c] == board[r][c]) ||
				(r != board.length - 1 && board[r + 1][c] == board[r][c]) ||
				(c != 0 && board[r][c - 1] == board[r][c]) ||
				(c != board[0].length -1 && board[r][c + 1] == board[r][c]) ||
						hasMove();
	}

	private boolean hasMove() {
		for (int r = 0; r <board.length-1; r++) {
			for (int c = 0; c < board[0].length-1; c++) {
				if(board[r][c] == board[r][c+1]) return true;
			}
		}
		for (int c = 0; c <board[0].length-1; c++) {
			for (int r = 0; r < board.length-1; r++) {
				if(board[r+1][c] == board[r][c]) return true;
			}
		}
		return false;
	}


	private static int nextInt(int max) {
		return current().nextInt(0, max);
	}

	/*****************************************
	 * Walk all the tiles in the board in the requested direction
	 * @param dir move direction of the tiles
	 *
	 * @return true when the board changes
	 ****************************************/
	@Override
	public boolean slide(SlideDirection dir) {
		switch (dir) {
			case UP:
				return slide(board, 0, board.length - 1, true, false);
			case DOWN:
				return slide(board, board.length - 1, 0, true, true);
			case LEFT:
				return slide(board, 0, board[0].length - 1, false, false);
			case RIGHT:
				return slide(board, board[0].length - 1, 0, false, true);
		}
		throw new IllegalStateException();
	}

	public void saveState() {
		UndoStack.getInstance().notifyMoved(this);
	}

	/*****************************************
	 * Shift values on the board.  1 slide method, 4 lines of direction logic
	 * @param board the game board
	 * @param swap true if left or right swipe, false if up or down
	 * @param rev true if reversing (shift to end of array) false otherwise
	 * @return true if the board moved, false if not
	 ****************************************/
	public boolean slide(int[][] board, int pivot, int target, boolean swap, boolean rev) {
		System.out.println("Before = \n" + prettyPrintBoard());
		boolean   hasMoved   = false;
		int       lineTarget = swap ? board[0].length : board.length;
		final int begin      = pivot;
		for (int line = 0; line < lineTarget; line++) {
			int next;
			pivot = begin;
			boolean canMerge = true;
			//get the first value to the start
			if (!((next = getNext(pivot, line, target, swap, rev)) == -1)) {

				///===============//
				// / Get the first cell to establish pivot
				///===============//
				if (get(line, begin, swap) == 0) {
					set(line, begin, get(line, next, swap), swap);
					set(line, next, 0, swap);
					int val = get(line, begin, swap);
					hasMoved = true;

					// notify observers
					for (NumberSliderObserver obs : observers) {
						if (swap) {
							obs.onCellMoved(next, line, begin, line);
						} else obs.onCellMoved(line, next, line, begin);
					}

				}
				next = getNext(shift(pivot, rev), line, target, swap, rev);
				//while the row can shift

				//============================// the slide from 0 index -> end
				while (next != -1) {
					// if beginning of row or if values equal
					if (canMerge & get(line, pivot, swap) == get(line, next, swap)) {

						set(line, pivot, get(line, pivot, swap) + get(line, next, swap), swap);
						set(line, next, 0, swap);
						this.score = score + get(line, pivot, swap) * get(line, pivot, swap);
						if (get(line, pivot, swap) == winningVal) this.status = USER_WON;
						hasMoved = true;
						canMerge = false;

						// notify observers
						cellCount--;
						Cell fromCell = getFor(line, next, swap, get(line, next, swap));
						Cell toCell   = getFor(line, pivot, swap, get(line, pivot, swap));
						notifyValueChanged(fromCell, toCell);

					} else { // else increment the pivot and set the value
						pivot = shift(pivot, rev);

						if (pivot != next) {
							set(line, pivot, get(line, next, swap), swap);
							set(line, next, 0, swap);
							hasMoved = true;
							canMerge = true;
							// notify observers
							final int val = get(line, pivot, swap);
							this.notifyCellMoved(swap, line, next, line, pivot);
						}

					}

					next = getNext(shift(pivot, rev), line, target, swap, rev);
				}
			}
		}

		if (hasMoved) {
			UndoStack.getInstance().notifyMoved(this);
			for (NumberSliderObserver ns : observers)
				ns.onSlideComplete();
		}

		return hasMoved;
	}

	private void notifyValueChanged(Cell from, Cell to) {
		for (NumberSliderObserver obs : this.observers) {
			obs.onCellValueChanged(from.row, from.column, to.row, to.column, to.value);
		}
	}

	private void notifyCellMoved(boolean swap, int line, int pivot, int toLine, int toPivot) {
		for (NumberSliderObserver obs : this.observers) {
			if (swap) obs.onCellMoved(pivot, line, toPivot, toLine);
			else obs.onCellMoved(line, pivot, toLine, toPivot);
		}
	}

	private Cell getFor(int line, int pivot, boolean swap, int value) {
		return new Cell(swap ? pivot : line, swap ? line : pivot, value);
	}

	/*****************************************
	 * Set a cell at a location
	 * @param s1 array[s1]
	 * @param s2 array[?][s2]
	 * @param swap true if s1 should be in s2's spot
	 * @return the value at that location on the board
	 ****************************************/
	private int set(int s1, int s2, int value, boolean swap) {
		if (swap) return board[s2][s1] = value;
		else return board[s1][s2] = value;
	}


	/*****************************************
	 * Get a cell at a location
	 * @param s1 array[s1]
	 * @param s2 array[?][s2]
	 * @param swap true if s1 should be in s2's spot
	 * @return the value at that location on the board
	 ****************************************/
	private int get(int s1, int s2, boolean swap) {
		return swap ? board[s2][s1] : board[s1][s2];
	}

	/*****************************************
	 * adapter for the 2 get next methods for different directionsf
	 * @param pivot the current location
	 * @param target the target (i.e. the end of the
	 *                 array, depending on slide direction)
	 * @param swap true if up or down, false if left right
	 * @param reverse reverse through array if true
	 * @return shift index of value, or -1
	 ****************************************/
	private int getNext(int pivot, int line, int target, boolean swap, boolean reverse) {
		if (swap) return getNextSwapped(this.board, line, pivot, target, reverse);
		else return getNextStandard(this.board[line], pivot, target, reverse);
	}

	/*****************************************
	 * get the next non-zero cell
	 * @param ints the array
	 * @param pivot the current location
	 * @param target the target (i.e. the end of the
	 *                 array, depending on slide direction)
	 * @param rev reverse through array if true
	 * @return shift index of value, or -1
	 ****************************************/
	private static int getNextStandard(int[] ints, int pivot, int target, boolean rev) {
		for (int i = pivot; i != shift(target, rev); i = shift(i, rev)) {
			if (ints[i] > 0) return i;
		}
		return -1;
	}

	private static int getNextSwapped(int[][] board, int line, int pivot, int target, boolean rev) {
		for (int i = pivot; i != shift(target, rev); i = shift(i, rev)) {
			if (board[i][line] > 0) return i;
		}
		return -1;
	}

	private int get(SlideDirection dir, int r, int c) {
		try {
			switch (dir) {
				case DOWN:
					return board[r][c - 1];
				case UP:
					return board[r][c + 1];
				case RIGHT:
					return board[r + 1][c];
				case LEFT:
					return board[r - 1][c];
			}
			throw new IllegalStateException();
		} catch (IndexOutOfBoundsException i) {
			return -1;
		}
	}

	/*****************************************
	 * Continue playing with the winning value raised
	 * @param nextFactor the factor to increase it by
	 ****************************************/
	public void continuePlaying(int nextFactor) {
		if (status == USER_LOST) throw new IllegalStateException();

		this.status = IN_PROGRESS;

		this.winningVal = winningVal * nextFactor;
	}

	public int getWinningValue() {
		return this.winningVal;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		NumberGame that = (NumberGame) o;

		if (rows != that.rows) return false;
		if (columns != that.columns) return false;
		if (winningVal != that.winningVal) return false;
		if (score != that.score) return false;
		if (status != that.status) return false;
		return Arrays.deepEquals(board, that.board);
	}

	/*****************************************
	 * Return the current state of the game
	 * @return one of the possible values of GameStatus enum
	 ****************************************/
	@Override
	public GameStatus getStatus() {
		return status;
	}

	/*****************************************
	 * @param status the current status
	 ****************************************/
	public void setStatus(Cell lastPlaced, GameStatus status) {
		this.status = status;
		this.observers.forEach(observer -> {
			observer.onCellPlaced(lastPlaced);
			observer.onGameStatusChanged(status);
		});
	}

	/*****************************************
	 * @param status the current status
	 ****************************************/
	public void setStatus(GameStatus status) {
		this.status = status;
		this.observers.forEach(observer -> {
			observer.onGameStatusChanged(status);
		});
	}

	protected void notifyPlaced(Cell c) {
		for (NumberSliderObserver o:
			  this.observers) {
			o.onCellPlaced(c);
		}
	}

	private static int shift(int current, boolean reverse) {
		return reverse ? current - 1 : current + 1;
	}

	/*****************************************
	 * @return a pretty-printed representation of the game board
	 ****************************************/
	public String prettyPrintBoard() {
		StringBuilder sb = new StringBuilder(rows * columns);
		Arrays.stream(board).forEach(ints -> {
			sb.append(String.format("%n"));
			Arrays.stream(ints).boxed()
					.map(Object::toString)
					.map(s -> " " + ((Integer.parseInt(s) > 0) ? s : "_") + " ")
					.forEach(sb::append);
		});
		return sb.toString();
		//throw new UnsupportedOperationException("Stream incompatibility!");
	}

	/*****************************************
	 * Pop the undo stack and set values
	 * @throws IllegalStateException when undo is not possible
	 ****************************************/
	@Override
	public void undo() {
		if (status == USER_LOST | status == USER_WON)
			throw new IllegalStateException();

		this.board = UndoStack.getInstance().popStack();
		this.status = IN_PROGRESS;
	}

	public int getScore() {
		return score;
	}

	public int getCellCount() {
		return cellCount;
	}
}
