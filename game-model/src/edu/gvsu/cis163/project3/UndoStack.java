package edu.gvsu.cis163.project3;


/** **************************************************
 * easy-48 - edu.gvsu.prestongarno - by Preston Garno on 3/14/17
 * ***************************************************/
public class UndoStack {
	
	
	/**The instance of the movement stack*/
	private static UndoStack instance;
	
	/** the arraylist of prev. boards */
	private Entry tail;

	/*****************************************
	 * Private constructor
	 ***************************************
	 * @param entry*/
	private UndoStack(Entry entry) {
		this.tail = entry;
	}
	
	/*****************************************
	 * @return the UndoStack instance
	 ****************************************/
	public static UndoStack getInstance() {
		return instance == null ? instance = new UndoStack(new Entry(null, null)) : instance;
	}
	
	/*****************************************
	 * Notifies the stack to store the state of the game
	 * @param numberGame the game instance
	 ****************************************/
	public void notifyMoved(NumberGame numberGame) {
		int[][] move = new int[numberGame.rows()][numberGame.columns()];
		for (int r = 0; r < move.length; r++) {
			for (int c = 0; c < move[0].length; c++) {
				move[r][c] = numberGame.getAt(r, c);
			}
		}
		Entry crr = tail;
		tail = new Entry(move, crr);
	}
	
	/*****************************************
	 * Request a previous move
	 * @return the previous state of the game board
	 ****************************************/
	public int[][] popStack() {
		if (tail.previous == null) throw new IllegalStateException();
		int[][] prev = tail.entry;
		tail = tail.previous;
		return prev;
	}
	
	/**
	 * Clears the stack
	 */
	public void clearStack() {
		this.tail = new Entry(null, null);
	}

	public int[][] peek() {
		return this.tail.entry;
	}

	private static final class Entry {
		final int[][] entry;

		Entry previous;

		private Entry(int[][] entry, Entry previous) {
			this.entry = entry;
			this.previous = previous;
		}
	}
}
