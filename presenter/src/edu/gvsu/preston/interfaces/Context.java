package edu.gvsu.preston.interfaces;

import java.util.HashMap;
import static edu.gvsu.preston.interfaces.GameAction.*;

/** **************************************************
 *  Central GUI class - ties together all parts of the game and
 *  performs component agnostic tasks
 * ***************************************************/
public interface Context {
	
	
	/*****************************************
	 * @return true if paused
	 ****************************************/
	boolean isPaused();
	
	/*****************************************
	 * pauses the "gui" part of the game
	 ****************************************/
	void pause();
	
	/*****************************************
	 * resumes the "gui" part of the game
	 ****************************************/
	void resume();
	
	/*****************************************
	 * @return the GameView (board) interface instance
	 ****************************************/
	GameView getGameView();
	
	/*****************************************
	 * @return the toolbar instancea
	 ****************************************/
	GameToolbar getGameToolbar();
	
	/*****************************************
	 * @return the HUD instance
	 ****************************************/
	HUD getHud();
	
	/*****************************************
	 * Show information in context of the application
	 * @param title the title
	 * @param message the message content
	 ****************************************/
	void showInformation(String title, String message);
	
	/*****************************************
	 * Show information in context of the application
	 * @param title the title
	 * @param message the message content
	 ****************************************/
	void showWarning(String title, String message);
	
	/*****************************************
	 * Show information in context of the application
	 * @param title the title
	 * @param message the message content
	 ****************************************/
	void showError(String title, String message);
	
	/*****************************************
	 *  get User input from a popup message
	 * @param title the title of the message
	 * @param header the header
	 * @param content the message content
	 * @param inputs Hashmap with keys representing the ID of the content that will be
	 *                 returned on user submitting, and the value is the user prompt for the info
	 * @param listener the action to perform on user submission
	 ****************************************/
	void getUserInput(
			String title,
			String header,
			String content,
			HashMap<String, String> inputs,
			UserInputListener listener);
	
	
	/*****************************************
	 * Show information in context of the application
	 * @param title the title
	 * @param message the message content
	 * @param confirmQuestion the question to ask
	 * @param listener the action to perform on confirmation
	 ****************************************/
	void confirmDialog(String title, String message, String confirmQuestion, ConfirmListener listener);
	
	/*****************************************
	 * binds a key to and action
	 * @param keyCode the key to bind
	 * @param listener the action to perform on the key event
	 ****************************************/
	void bindKey(KeyInputListener listener, String... keyCode);
}
