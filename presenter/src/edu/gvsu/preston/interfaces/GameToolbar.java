package edu.gvsu.preston.interfaces;

/** **************************************************
 * project3 - edu.gvsu.preston.interfaces
 *
 * The game toolbar interface for promoting actions
 * ***************************************************/
public interface GameToolbar {
	
	
	/*****************************************
	 * Add a button and handler to the toolbar
	 * @param resourceName the resource name of the icon
	 * @param ID the ID for the button
	 * @param action the action to perform to handle the button click event
	 ****************************************/
	void addButton(String resourceName, String ID, Runnable action);
	
	
	/*****************************************
	 * @param ID the id of the button to disable
	 * @param disable true if disabled
	 ****************************************/
	void setDisabled(String ID, boolean disable);
	
	/**
	 * @param is if true, disables all toolbar buttons
	 */
	void disableAll(boolean is);
	
}
