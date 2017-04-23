package edu.gvsu.cis163.project3.interfaces;

import edu.gvsu.cis163.project3.Cell;
import edu.gvsu.cis163.project3.GameStatus;


/** **************************************************
 * project3 - edu.gvsu.cis163.project3.interfaces - by Preston Garno on 3/20/17
 * ***************************************************/
public interface NumberSliderObserver {
	
	
	/*****************************************
	 * Called when a cell moves to another spot
	 * @param oldRow the old row
	 * @param oldColumn the old col
	 * @param currentRow the current row
	 * @param currentColumn the current col
	 ****************************************/
	void onCellMoved(int oldRow, int oldColumn, int currentRow, int currentColumn);
	
	/*****************************************
	 * Method for getting notified when a cell combines with another one
	 ****************************************/
	void onCellValueChanged(int fromRow, int fromColumn, int toRow, int toColumn, int newValue);
	
	/*****************************************
	 * Notifies observers when the slide method is done
	 ****************************************/
	void onSlideComplete();
	
	/*****************************************
	 * notify the game status changed
	 * @param status the current status
	 ****************************************/
	void onGameStatusChanged(GameStatus status);

	/*****************************************
	 * @param lastPlaced used to notify presenter/view
	 *                     of last cell placed before the user loses the game
	 ****************************************/
	void onCellPlaced(Cell lastPlaced);
}
