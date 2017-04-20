package edu.gvsu.prestongarno.gui;

import edu.gvsu.preston.interfaces.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;

import java.util.*;

import static edu.gvsu.preston.interfaces.GameAction.*;


/**
 * The Main Game class
 ****************************************/
public class Game implements Context {
	
	private boolean IS_PAUSED;
	
	public StackPane stackPane;
	@FXML
	private Pane overlay;
	@FXML
	private
	GameTilePane gameBoard;
	@FXML
	private
	FxToolbar actionMenu;
	@FXML
	private
	FlowPane statsPane;
	@FXML
	public BorderPane borderPane;
	private ScoreBoard hud;
	private HashMap<String, KeyInputListener> KEY_BINDINGS;
	
	private static Context context;
	private static Game game;
	private Color currentColor;
	private javafx.scene.text.Font gameFont;

	/*****************************************
	 * bind view
	 ****************************************/
	public Game() {
		game = this;
		context = this;
		this.currentColor = new Color(0.0, 0.4, 0.40, 1.0);
		this.gameFont = new Font("Ubuntu", 16);
	}
	
	public void initialize() {
		IS_PAUSED = true;
		this.hud = new ScoreBoard(this.statsPane);
		this.KEY_BINDINGS = new HashMap<>();
		EventHandler<InputEvent> handler = event -> {
			if (event instanceof KeyEvent && event.getEventType().equals(KeyEvent.KEY_RELEASED)) {
				KEY_BINDINGS.entrySet().stream()
						.filter(entry -> (((KeyEvent) event)
								.getCode().toString())
								.compareToIgnoreCase(entry.getKey()) == 0)
						.forEach(entry ->
								entry.getValue()
										.onKeyInput(((KeyEvent) event)
												.getCode().toString()));
			}
		};
		borderPane.addEventHandler(KeyEvent.KEY_RELEASED, handler);
		this.stackPane.setSnapToPixel(false);
	}
	
	/*****************************************
	 * Getter for property 'overlay'.
	 *
	 * @return Value for property 'overlay'.
	 ****************************************/
	public Pane getOverlay() {
		return overlay;
	}
	
	/*****************************************
	 * Getter for property 'stackPane'.
	 *
	 * @return Value for property 'stackPane'.
	 ****************************************/
	public StackPane getStackPane() {
		return stackPane;
	}
	
	/*****************************************
	 * @return this, as context (presentation layer)
	 ****************************************/
	public static Context getContext() {
		return context;
	}
	
	/*****************************************
	 * @return this, as class instance
	 ****************************************/
	public static Game getGame() {
		return game;
	}
	
	/*****************************************
	 * @return the theme color
	 ****************************************/
	public Color getGameThemeColor() {
		return this.currentColor;
	}
	
	/*****************************************
	 * @param color sets the theme color
	 ****************************************/
	public void setGameThemeColor(Color color) {
		this.currentColor = color;
	}
	
	/*****************************************
	 * @return true if paused
	 ****************************************/
	@Override
	public boolean isPaused() {
		return IS_PAUSED;
	}
	
	/*****************************************
	 * performs all tasks that pause the GUI
	 ****************************************/
	@Override
	public void pause() {
		this.IS_PAUSED = true;
		final HashMap<String, Runnable> PauseScreenSetup = new HashMap<>(1);
		PauseScreenSetup.put("resume", () -> context.resume());
		
		getGameToolbar().disableAll(true);
		
		getGameView().displayGameMessage("Game Paused...", PauseScreenSetup);
	}
	
	/*****************************************
	 * resumes GUI components
	 ****************************************/
	@Override
	public void resume() {
		getGameToolbar().disableAll(false);
		this.IS_PAUSED = false;
		getGameView().removeGameMessage();
	}
	
	/*****************************************
	 * @return GameView instance
	 ****************************************/
	@Override
	public GameView getGameView() {
		return gameBoard;
	}
	
	/*****************************************
	 * @return the toolbar
	 ****************************************/
	@Override
	public GameToolbar getGameToolbar() {
		return actionMenu;
	}
	
	/*****************************************
	 * @return the HUD
	 ****************************************/
	@Override
	public HUD getHud() {
		return this.hud;
	}
	
	
	/*****************************************
	 * Binds a key to an action
	 * @param listener the action to perform on the key event
	 * @param keyCode the key to bind
	 ****************************************/
	@Override
	public void bindKey(KeyInputListener listener, String... keyCode) {
		if (keyCode != null) {
			for (String s : keyCode) {
				this.KEY_BINDINGS.put(s, listener);
			}
		}
	}
	
	/*****************************************
	 * Shows a dialog with an option to confirm
	 * @param title the title
	 * @param description
	 * @param question
	 * @param listener the action to perform on confirmation
	 ****************************************/
	@Override
	public void confirmDialog(String title, String description, String question, ConfirmListener listener) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.initStyle(StageStyle.DECORATED);
		alert.setTitle(title);
		alert.setHeaderText(description);
		alert.setContentText(question);
		Optional<ButtonType> result = alert.showAndWait();
		if (!result.isPresent()) {
			listener.onConfirm(false);
		} else {
			listener.onConfirm(result.get().getText().equalsIgnoreCase("ok"));
		}
	}
	
	
	/*****************************************
	 * Shows dialog popup with "info" icon
	 * @param title the title
	 * @param message the message content
	 ****************************************/
	public void showInformation(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Information");
		alert.setHeaderText(title);
		alert.setContentText(message);
		
		alert.showAndWait();
	}
	
	/*****************************************
	 * Gets user input
	 * @param title the title of the message
	 * @param header the header
	 * @param content the message content
	 * @param inputs Hashmap with keys representing the ID of the content that will be
	 *                 returned on user submitting, and the value is the user prompt for the info
	 * @param listener the action to perform on user submission
	 ****************************************/
	@Override
	public void getUserInput(String title,
									 String header,
									 String content,
									 HashMap<String, String> inputs,
									 UserInputListener listener) {
		
		CustomTextInputDialog dialog =
				new CustomTextInputDialog(title, header, content, inputs);
		
		dialog.showAndWait().ifPresent(listener::onUserInput);
		
	}
	
	/*****************************************
	 * Dynamic, custom text input dialog
	 ****************************************/
	private static final class CustomTextInputDialog
			extends Dialog<HashMap<String, String>> {
		
		GridPane grid;
		public CustomTextInputDialog(
				String title,
				String header,
				String content,
				HashMap<String, String> inputs) {
			
				setTitle(title);
				setHeaderText(header);
				setContentText(content);
				
				grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);
				grid.setPadding(new Insets(20, 150, 10, 10));
				
				Iterator<Map.Entry<String, String>> fields = inputs.entrySet().iterator();
				
				for (int i = 0; i < inputs.size(); i++) {
					Map.Entry<String, String> e = fields.next();
					Label label = new Label(e.getValue());
					label.setPadding(new Insets(10));
					grid.add(label, 0, i);
					TextField field = new TextField();
					field.setId(e.getKey());
					grid.add(field, 1, i);
				}
				getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
				
				getDialogPane().setContent(grid);
				
				final  Button btnCancel = (Button) getDialogPane().lookupButton(ButtonType.CANCEL);
				btnCancel.setOnAction(Event::consume);
				
				final Button btOk = (Button) getDialogPane().lookupButton(ButtonType.OK);
				btOk.addEventFilter(ActionEvent.ACTION, event -> {
					
					TextField textField = grid.getChildren()
							.stream()
							.filter(node -> node instanceof TextField)
							.map(node -> (TextField) node)
							.filter(field -> field.getText()
									.isEmpty())
							.findAny().orElse(null);
					if(textField != null) {
						textField.requestFocus();
						textField.setBorder(new Border(new BorderStroke(
								Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(4), null)));
						event.consume();
					}
				});
			
				HashMap<String, String> results = new HashMap<>();
			
				setResultConverter(param -> {
					if(param.equals(ButtonType.CANCEL)) return null;
					grid.getChildren().stream().filter(node -> node instanceof TextField).forEach(node ->
							results.put(node.getId(), ((TextField) node).getText()));
					return results;
				});
			}
	}
	
	/*****************************************
	 * @param title the title
	 * @param message the message content
	 ****************************************/
	public void showWarning(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Warning");
		alert.setHeaderText(title);
		alert.setContentText(message);
		
		alert.showAndWait();
	}
	
	/*****************************************
	 *
	 * @param title the title
	 * @param message the message content
	 ****************************************/
	public void showError(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Error");
		alert.setHeaderText(title);
		alert.setContentText(message);
		
		alert.showAndWait();
	}
	
	/*****************************************
	 * @return the game font
	 ****************************************/
	public Font getGameFont() {
		return this.gameFont;
	}
}
