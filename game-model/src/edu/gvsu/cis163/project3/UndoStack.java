package edu.gvsu.cis163.project3;

import java.util.ArrayList;
import java.util.Arrays;


/** **************************************************
 * easy-48 - edu.gvsu.prestongarno - by Preston Garno on 3/14/17
 * ***************************************************/
public class UndoStack {
	
	
	/**The instance of the movement stack*/
	private static UndoStack instance;
	
	/** the arraylist of prev. boards */
	private ArrayList<int[][]> moves;
	
	/*****************************************
	 * Private constructor
	 ****************************************/
	private UndoStack() {
		this.moves = new ArrayList<>(10);
	}
	
	/*****************************************
	 * @return the UndoStack instance
	 ****************************************/
	public static UndoStack getInstance() {
		return instance == null ? instance = new UndoStack() : instance;
	}
	
	/*****************************************
	 * Notifies the stack to store the state of the game
	 * @param numberGame the game instance
	 ****************************************/
	public void notifyMoved(NumberGame numberGame) {
		int[][] move = new int[numberGame.getRows()][numberGame.getColumns()];
		Arrays.stream(move).forEach(ints -> {
			final int index = getIndex(move, ints);
			for (int i = 0; i < ints.length; i++) {
				move[index][i] = numberGame.getAt(index, i);
			}
		});
		moves.add(0, move);
	}
	
	private static int getIndex(int[][] ref, int[] ints) {
		for (int i = 0; i < ref.length; i++) {
			if (ref[i] == ints) return i;
		}
		throw new IllegalArgumentException();
	}
	
	/*****************************************
	 * Request a previous move
	 * @return the previous state of the game board
	 ****************************************/
	public int[][] popStack() {
		if (moves.isEmpty()) throw new IllegalStateException();
		else return moves.remove(0);
	}
	
	/**
	 * Clears the stack
	 */
	public void clearStack() {
		this.moves.clear();
	}
}
