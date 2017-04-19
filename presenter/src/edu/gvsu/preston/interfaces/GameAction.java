package edu.gvsu.preston.interfaces;


import java.util.HashMap;

/** **************************************************
 * easy-48 - edu.gvsu.prestongarno.gui.interfaces
 *
 * Miscellaneous Game interfaces
 * ***************************************************/
public final class GameAction {
	
	
	/** **************************************************
	 * Standard on requesting user confirmation
	 * ***************************************************/
	public interface ConfirmListener {
		void onConfirm(boolean confirmed);
	}
	
	/*****************************************
	 * Get user input, HashMap keys are the same as call
	 * to view getUserInput() call to specify input fields
	 ****************************************/
	public interface UserInputListener {
		void onUserInput(HashMap<String,String> results);
	}
	
	/*****************************************
	 * Interface for subscribing to key input from the user
	 ****************************************/
	public interface KeyInputListener {
		void onKeyInput(String KeyCode);
	}
	
}
