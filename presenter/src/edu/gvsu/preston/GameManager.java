package edu.gvsu.preston;


import edu.gvsu.cis163.project3.*;
import edu.gvsu.cis163.project3.interfaces.NumberSlider;
import edu.gvsu.cis163.project3.interfaces.NumberSliderObserver;
import edu.gvsu.preston.interfaces.*;

import java.util.EnumSet;
import java.util.HashMap;


/**
 * *************************************************
 * 2048 - edu.gvsu.prestongarno
 ****************************************/
public class GameManager implements NumberSliderObserver {


	/**
	 * the model
	 */
	private final NumberSlider slider;
	/**
	 * the context of the game
	 */
	private final Context      context;
	/**
	 * the game board
	 */
	private       GameView     view;
	/**
	 * the game toolbar instance
	 */
	private       GameToolbar  toolbar;
	/**
	 * The info display instance
	 */
	private       HUD          infoDisplay;
	/**
	 * the winning value
	 */
	private       int          winningValue;

	/*****************************************
	 * Constructor for the game presenter class
	 * @param context the context of the GUI application
	 * @param rows the number of rows
	 * @param columns the number of columns
	 ****************************************/
	public GameManager(Context context, int rows, int columns) {
		context.getGameView().initialize(rows, columns);
		this.slider = new NumberGame(rows, columns, this);
		this.context = context;
		this.view = context.getGameView();
		this.toolbar = context.getGameToolbar();
		this.infoDisplay = context.getHud();
		infoDisplay.displayWinningValue(winningValue = 2048);
		this.buildToolbar(toolbar);
		this.setStartMenu();
		this.bindKeys();
	}

	private void setStartMenu() {

		HashMap<String, Runnable> message = new HashMap<>(1);

		message.put("Start Game", () -> {
			context.resume();
			infoDisplay.displayScore("" + ((NumberGame) slider).getScore());
			placeAll();
		});

		view.displayGameMessage("2048-FX", message);

	}

	/*****************************************
	 * @param toolbar the toolbar to dosplay actions to
	 ****************************************/
	private void buildToolbar(GameToolbar toolbar) {

		toolbar.addButton("pause.png", "pause_btn", () -> {
			if (context.isPaused()) {
				context.resume();
			} else {
				context.pause();
				toolbar.setDisabled("pause_btn", false);
			}
		});

		toolbar.addButton("undo.png", "undo_button", () -> context.confirmDialog(
				"Undo",
				"Undo last move.",
				"Are you sure?", confirmed -> {
					if (confirmed) this.undoState();
				}));

		toolbar.addButton("Restart Game", "restart_game", this::reset);

		toolbar.addButton("Resize Board", "resize_board", () -> context.getUserInput(
				"Resize Board",
				"Resize Board",
				"Enter the number of rows and columns:",
				getResizeInputStructure(),
				results -> {
					try {
						int rows = Integer.valueOf(results.get("NUM_ROWS"));
						int col  = Integer.valueOf(results.get("NUM_COLUMNS"));
						slider.resizeBoard(rows, col, this.winningValue);
						view.changeBoardSize(rows, col);
					} catch (NumberFormatException nf) {
						context.showError("Invalid input", "Input must be a number!");
					}
				}));

		toolbar.addButton(
				"Win conditions",
				"win_conditions",
				() -> {
					final HashMap<String, String> values = new HashMap<>();
					values.put("WIN_VALUE", "Winning value: ");
					context.getUserInput("Settings", "Change Settings", "Game rules", values, results -> {
						try {
							final Integer newValue = Integer.valueOf(results.get("WIN_VALUE"));
							if (newValue < 0) throw new IllegalArgumentException();
							slider.resizeBoard(((NumberGame) slider).getRows(),
													 ((NumberGame) slider).getColumns(),
													 newValue);
							infoDisplay.setWinningValue("" + newValue);
						} catch (NumberFormatException nf) {
							context.showError("Invalid", "Invalid Input! Must be a number.");
						} catch (IllegalArgumentException ig) {
							context.showError("Invalid", "Number must be a positive multiple of 2!");
						}
					});
				});
	}

	/*****************************************
	 * Getter for property 'resizeInputStructure'.
	 *
	 * @return Value for property 'resizeInputStructure'.
	 ****************************************/
	private HashMap<String, String> getResizeInputStructure() {

		HashMap<String, String> map = new HashMap<>();
		map.put("NUM_ROWS", "Rows:");
		map.put("NUM_COLUMNS", "Columns:");
		return map;
	}

	/*****************************************
	 * bind keys to perform game actions
	 ****************************************/
	private void bindKeys() {
		context.bindKey(keycode -> {
			if (!view.isAnimating() && !context.isPaused()) {
				for (SlideDirection sd : EnumSet.allOf(SlideDirection.class)) {
					if (sd.toString().compareToIgnoreCase(keycode) == 0) {
						this.onSlide(sd);
					}
				}
			}
		}, "UP", "DOWN", "LEFT", "RIGHT");

		context.bindKey(KeyCode -> context.pause(), "P");

		context.bindKey(KeyCode -> undoState(), "U");
	}

	private void placeAll() {
		for (int r = 0; r < slider.rows(); r++) {
			for (int c = 0; c < slider.columns(); c++) {
				int at = slider.getAt(r, c);
				if(at >0) view.placeCell(r, c, at);
			}
		}
	}

	/*****************************************
	 * Undo game state and update GUI
	 ****************************************/
	private void undoState() {
		try {
			slider.undo();
			view.clear();
			infoDisplay.decrementMoveCount();
			infoDisplay.displayScore("" + ((NumberGame) slider).getScore());
			placeAll();
		} catch (IllegalStateException is) {
			this.context.showError("Illegal move", "Already at last change!");
		}
	}

	/*****************************************
	 * @param direction the direction to slide
	 ****************************************/
	private void onSlide(SlideDirection direction) {
		if (slider.slide(direction)) infoDisplay.incrementMoveCount();
	}

	/*****************************************
	 * Reset the game
	 ****************************************/
	protected void reset() {
		context.confirmDialog("Reset Game",
									 "Warning! You will lose all game progress!",
									 "Reset game?",
									 confirmed -> {
										 if (confirmed) {
											 view.removeGameMessage();
											 slider.reset();
											 toolbar.disableAll(false);
											 infoDisplay.resetMoveCount();
											 view.clear();
											 infoDisplay.displayScore("" + ((NumberGame) slider).getScore());
											 placeAll();
										 }
									 });
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCellMoved(
			int oldRow,
			int oldColumn,
			int currentRow,
			int currentColumn) {
		view.moveCell(oldRow, oldColumn, currentRow, currentColumn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCellValueChanged(
			int fromRow,
			int fromColumn,
			int toRow,
			int toColumn,
			int newValue) {
		view.moveAndMerge(fromRow, fromColumn, toRow, toColumn, newValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onGameStatusChanged(GameStatus status) {
		HashMap<String, Runnable> options = new HashMap<>(1);
		switch (status) {
			case USER_WON:
				toolbar.disableAll(true);

				options.put("Continue playing", () -> {
					((NumberGame) slider).continuePlaying(2);
					context.resume();
				});
				options.put("Restart", this::reset);

				view.displayGameMessage("Congratulations, you've won!\nIt took you "
														+ infoDisplay.getMoveCount()
														+ " moves\nto get a score of\n"
														+ (((NumberGame) slider).getScore() - 4), options);
				break;

			case USER_LOST:
				toolbar.disableAll(true);
				options.put("Restart", this::reset);
				view.displayGameMessage("Game Over\nIt took you "
														+ infoDisplay.getMoveCount()
														+ " moves\nto get a score of\n"
														+ (((NumberGame) slider).getScore() - 4), options);
		}
	}

	/*****************************************
	 * Notified when the slide action is complete
	 ****************************************/
	@Override
	public void onSlideComplete() {
		infoDisplay.displayScore("" + ((NumberGame) slider).getScore());
		Cell nextCell = slider.placeRandomValue();
		if (nextCell == null) {
			onGameStatusChanged(GameStatus.USER_LOST);
		} else {
			view.placeCell(nextCell.row, nextCell.column, nextCell.value);
			view.update();
		}
		System.out.println(((NumberGame) slider).prettyPrintBoard());
	}
}
