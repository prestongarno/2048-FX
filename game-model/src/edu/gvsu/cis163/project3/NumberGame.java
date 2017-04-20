package edu.gvsu.cis163.project3;

import com.sun.istack.internal.Nullable;
import edu.gvsu.cis163.project3.interfaces.NumberSlider;
import edu.gvsu.cis163.project3.interfaces.NumberSliderObserver;

import java.util.*;
import java.util.stream.Collectors;

import static edu.gvsu.cis163.project3.GameStatus.*;
import static java.util.concurrent.ThreadLocalRandom.current;


/***************************************************
 * easy-48 - edu.gvsu.prestongarno - by Preston Garno
 ****************************************/
public class NumberGame implements NumberSlider {


	/**
	 * the game status
	 */
	private GameStatus status;
	/**
	 * The amount of rows and columns
	 */
	private int        rows, columns;
	/**
	 * the game board
	 */
	private int[][]                    board;
	/**
	 * winning value
	 */
	private int                        winningVal;
	private List<NumberSliderObserver> observers;
	private int                        score;

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

		board = new int[rows][columns];
		this.score = 0;
		this.winningVal = 1024;
		colTracker = new boolean[board.length];
		rowTracker = new boolean[board[0].length];
		placeRandomValue();
		placeRandomValue();
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

	@Override
	public int rows() {
		return this.rows;
	}

	@Override
	public int columns() {
		return this.columns;
	}

	/*****************************************
	 * Getter for property 'rows'.
	 * @return Value for property 'rows'.
	 ****************************************/
	public int getRows() {
		return rows;
	}

	/*****************************************
	 * Getter for property 'columns'.
	 * @return Value for property 'columns'.
	 ****************************************/
	public int getColumns() {
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
		if (ii != winningValue) throw new IllegalArgumentException();

		ArrayList<Cell> cells = this.getNonEmptyTiles();
		this.setStatus(IN_PROGRESS);
		this.winningVal = winningValue;
		this.rows = width;
		this.columns = height;
		this.board = new int[rows][columns];
		colTracker = new boolean[board.length];
		rowTracker = new boolean[board[0].length];

		cells.stream()
				.filter(cell -> cell.row < rows && cell.column < columns)
				.forEach(cell -> {
					board[cell.row][cell.column] = cell.value;
				});
		if (this.score == 0) {
			this.placeRandomValue();
		}
	}

	/*****************************************
	 * reset board
	 ****************************************/
	@Override
	public void reset() {
		this.board = new int[rows][columns];
		this.status = IN_PROGRESS;
		UndoStack.getInstance().clearStack();
		placeRandomValue();
		placeRandomValue();
	}

	//test recursion
	@SuppressWarnings("Duplicates")
	private static int getRandomRow(int lo, int hi, boolean[] flagged) {
		int rand;
		if (lo >= hi && hi == flagged.length-1) {return hi;}
		if (hi == lo) {
			rand = lo;
			hi = flagged.length+1;
			lo = lo + 1;
		} else rand = current().nextInt(lo, hi);

		if (!flagged[rand]) return rand;
		flagged[rand] = true;
		return getRandomRow(lo, hi - 1, flagged);
	}

	private static int getRandomColumn(int lo, int hi, boolean[] flagged) {
		int rand;

		if (lo >= hi && hi == flagged.length-1) {return hi;}
		if (hi == lo) {
			rand = lo;
			hi = flagged.length;
			lo = lo + 1;
		} else rand = current().nextInt(lo, hi);

		if (!flagged[rand]) return rand;

		flagged[rand] = true;
		return getRandomColumn(lo, hi - 1, flagged);
	}

	/*****************************************
	 * @return a Cell object with its row, column, and value attributes
	 *  initialized properly
	 ****************************************/
	@Nullable
	@Override
	public Cell placeRandomValue() {
		boolean transpose = current().nextBoolean();
		// either searching on a row or a column first based off of a binary rand selection
		boolean[] scanner = !transpose ? rowTracker : colTracker;

		// tracks which index (line) we're at
		boolean[] altScanner = (transpose) ? rowTracker : colTracker;

		int value; // value at index holder
		// main search loop
		for (int i = 0; i < scanner.length; i++) {
			// loop:: while tracker at index hasn't been flagged get next rand integer
			int randIndex;
			randIndex = transpose ?
							getRandomColumn(0, scanner.length-1, scanner) :
							getRandomRow(0, scanner.length-1, scanner);
			// flag this index on the 2d array as visited
			scanner[randIndex] = true;

			//===== > actual value part

			int max = altScanner.length - 1;
			// get an index in the row/column we're on
			int randIndexIndex = nextInt(max);
			// flag this indexIndex as visited

			int     curr;
			boolean increment = randIndexIndex != max;
			int     hi;
			int     lo;
			hi = lo = randIndexIndex;

			for (boolean b : altScanner) {
				// if the spot on the board is empty, set and return, else flag visited and incr/decr
				curr = (transpose ? board[randIndex][randIndexIndex] : board[randIndexIndex][randIndex]);

				if (curr == 0 && transpose) {   // standard
					board[randIndex][randIndexIndex] = (current().nextInt(2, 6) == 5) ? 4 : 2;
					this.resetScanners();
					return new Cell(randIndex, randIndexIndex, board[randIndex][randIndexIndex]);
				} else if (curr == 0) {
					board[randIndexIndex][randIndex] = (current().nextInt(2, 6) == 5) ? 4 : 2;
					this.resetScanners();
					return new Cell(randIndexIndex, randIndex, board[randIndexIndex][randIndex]);
				}

				// expand outward
				if (increment && hi < max) {
					randIndexIndex = hi = hi + 1;
				} else if (!increment && lo > 0) {
					randIndexIndex = lo = lo - 1;
				}
				increment = !increment;
			}
		}
		this.status = USER_LOST;
		return null;
	}

private void resetScanners() {
	// reset flag arrays
	for (int i = 0; i < colTracker.length; i++) {
		colTracker[i] = false;
	}
	for (int i = 0; i < rowTracker.length; i++) {
		rowTracker[i] = false;
	}

}
	private boolean[] rowTracker;
	private boolean[] colTracker;

	private static int nextInt(int max) {
		return current().nextInt(0, max);
	}

	/*****************************************
	 * @param ref the array to copy
	 ****************************************/
	@Override
	public void setValues(int[][] ref) {
		resizeBoard(ref.length, ref[0].length, winningVal);

		Arrays.stream(ref).forEach(ints -> {
			int         index = getIndex(ref, ints);
			final int[] x     = {-1};
			Arrays.stream(ints).forEachOrdered(value -> {
				board[index][++x[0]] = value;
			});
		});
	}

	/*****************************************
	 * Utility for locating the current index in the stream
	 * @param ref values arr
	 * @param ints the current array
	 * @return the index that that ints is in ref
	 ****************************************/
	private int getIndex(int[][] ref, int[] ints) {
		for (int i = 0; i < ref.length; i++) {
			if (ref[i] == ints) return i;
		}
		throw new IllegalArgumentException();
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

	/*****************************************
	 * Shift values on the board.  1 slide method, 4 lines of direction logic
	 * @param board the game board
	 * @param swap true if left or right swipe, false if up or down
	 * @param rev true if reversing (shift to end of array) false otherwise
	 * @return true if the board moved, false if not
	 ****************************************/
	public boolean slide(int[][] board, int pivot, int target, boolean swap, boolean rev) {
		this.score = 0;
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
					this.score = score + val * val;
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
						if (get(line, pivot, swap) == winningVal) this.status = USER_WON;
						hasMoved = true;
						canMerge = false;

						// notify observers
						Cell fromCell = getFor(line, next, swap, get(line, next, swap));
						Cell toCell   = getFor(line, pivot, swap, get(line, pivot, swap));
						this.score = score + toCell.value * toCell.value;
						this.notifyValueChanged(fromCell, toCell);

					} else { // else increment the pivot and set the value
						pivot = shift(pivot, rev);

						if (pivot != next) {
							set(line, pivot, get(line, next, swap), swap);
							set(line, next, 0, swap);
							hasMoved = true;
							canMerge = true;
							// notify observers
							final int val = get(line, pivot, swap);
							score = score + (val * val);
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

		setStatus(status);

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

	/*****************************************
	 * @return an arraylist of Cells. Each cell holds the (row,column) and
	 * value of a tile
	 ****************************************/
	public ArrayList<Cell> getNonEmptyTiles() {
		return (ArrayList<Cell>) Arrays.stream(board)
				.flatMap(ints -> {
					List<Cell> cellsInRow = new LinkedList<>();
					int        row        = this.getIndex(this.board, ints);
					for (int i = 0; i < ints.length; i++) {
						if (ints[i] > 0) {
							cellsInRow.add(new Cell(row, i, ints[i]));
						}
					}
					return cellsInRow.stream();
				}).collect(Collectors.toList());
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
	public void setStatus(GameStatus status) {
		this.status = status;
		this.observers.forEach(observer -> observer.onGameStatusChanged(status));
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

	/*****************************************
	 * @return true if a move is available
	 ****************************************/
	private boolean canMove() {
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[0].length - 1; c++) {
				if (board[r][c] == board[r][c + 1] | board[r][c] == 0) return true;
			}
		}
		for (int c = 0; c < board[0].length; c++) {
			for (int r = 0; r < board.length - 1; r++) {
				if (board[r][c] == board[r + 1][c]) return true;
			}
		}
		return false;
	}

	public ArrayList<Cell> getEmptyTiles() {
		ArrayList<Cell> empty = new ArrayList<>();
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[0].length; c++) {
				if (board[r][c] == 0) {
					empty.add(new Cell(r, c, 0));
				}
			}
		}
		return empty;
	}

	public int getScore() {
		return score;
	}
}
