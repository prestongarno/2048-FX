package edu.gvsu.preston.interfaces;

/** **************************************************
 * project3 - edu.gvsu.preston.interfaces
 *
 * The Heads up display showing stats, etc
 * ***************************************************/
public interface HUD {
	
	
	/*****************************************
	 * increment move count
	 ****************************************/
	void incrementMoveCount();
	
	
	/*****************************************
	 * decrement move count
	 ****************************************/
	void decrementMoveCount();
	
	/*****************************************
	 * reset move count
	 ****************************************/
	void resetMoveCount();
	
	/*****************************************
	 * Getter for property 'moveCount'.
	 *
	 * @return Value for property 'moveCount'.
	 ****************************************/
	int getMoveCount();
	
	/*****************************************
	 * Setter for property 'currentScore'.
	 *
	 * @param score Value to set for property 'currentScore'.
	 ****************************************/
	void displayScore(String score);
	
	/*****************************************
	 * @param i the value to display
	 ****************************************/
	void displayWinningValue(int i);
	
	/*****************************************
	 * @param value the value to display
	 ****************************************/
	void setWinningValue(String value);
}
