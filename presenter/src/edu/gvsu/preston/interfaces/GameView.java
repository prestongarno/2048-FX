package edu.gvsu.preston.interfaces;

import java.util.HashMap;


/** **************************************************
 * easy-48 - edu.gvsu.prestongarno.gui - by Preston Garno on 3/17/17
 *
 * The Game board interface
 * ***************************************************/
public interface GameView {
	
	
	/*****************************************
	 * Change the board size on the gui
	 * @param numRows number of rows
	 * @param numColumns num of columns
	 ****************************************/
	void changeBoardSize(int numRows, int numColumns);
	
	/*****************************************
	 * place a cell on the view
	 * @param r the row
	 * @param c the column
	 * @param value the value
	 ****************************************/
	void placeCell(int r, int c, int value);
	
	/*****************************************
	 * Initialize the GUI
	 * @param numRows the number of rows
	 * @param numColumns the number of columns
	 ****************************************/
	void initialize(int numRows, int numColumns);
	
	/*****************************************
	 * @param oldR
	 * @param oldC
	 * @param toRow
	 * @param toColumn
	 ****************************************/
	void moveCell(int oldR, int oldC, int toRow, int toColumn);
	
	void moveAndMerge(int fromRow, int fromColumn, int toRow, int toColumn, int newValue);
	
	/*****************************************
	 * Clear the gui
	 ****************************************/
	void clear();
	
	/*****************************************
	 * @return true if the view is animating
	 ****************************************/
	boolean isAnimating();
	
	/*****************************************
	 * Used because of the way the animations work,
	 * some cleanup has to happen on the overlay used to mimic a slide animation
	 ****************************************/
	void update();
	
	/*****************************************
	 * removes the overlay message from the game
	 ****************************************/
	void removeGameMessage();
	
	/*****************************************
	 * Displays a message on an overlay over the screen
	 * @param message the message to display
	 * @param buttonOptions the options for the buttons with actions to perform on selection
	 ****************************************/
	void displayGameMessage(String message, HashMap<String, Runnable> buttonOptions);
}
