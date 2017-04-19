package edu.gvsu.prestongarno.gui;

import com.sun.istack.internal.Nullable;
import edu.gvsu.preston.interfaces.GameView;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

import static javafx.scene.layout.Priority.*;


/** **************************************************
 * project3 - edu.gvsu.prestongarno.gui - by Preston Garno on 3/19/17
 * ***************************************************/
public class GameTilePane extends GridPane implements GameView {
	
	
	/** * True when animations are going on to prevent threading issues */
	private boolean ANIMATING;
	/** * the padding around the cells */
	private static final int PADDING = 7;
	/** * Duration of the slide transition */
	private static final javafx.util.Duration TRANSLATE_DURATION = new Duration(200);
	/** * the slide transition */
	private ParallelTransition SLIDE_TRANSITION;
	/** * the duration of the fade in animation */
	private final Duration FADE_IN_DURATION = new Duration(175);
	/** * the overlay to perform animations on without grid constraints */
	private Pane viewOverlay;
	/** * the number of rows */
	private int rows;
	/** * The number of columns */
	private int columns;
	
	/*****************************************
	 * public constructor
	 ****************************************/
	public GameTilePane() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/customtilepane.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.setFocusTraversable(true);
		this.requestFocus();
	}
	
	/*****************************************
	 * Initializes the GUI (Constructor called by injection before this can be done
	 * @param rows the num of rows
	 * @param columns the num columns
	 ****************************************/
	@Override
	public void initialize(int rows, int columns) {
		this.ANIMATING = false;
		this.viewOverlay = Game.getGame().getOverlay();
		viewOverlay.toFront();
		viewOverlay.setBackground(new Background(new
				BackgroundFill(new Color(.0, .0, .0, .0), null, null)));
		changeBoardSize(rows, columns);
		setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		setHgap(8);
		setVgap(8);
		setBackground(new Background(
				new BackgroundFill(Color.LIGHTBLUE.desaturate(), null, null)));
		
		SLIDE_TRANSITION = new ParallelTransition();
	}
	
	/*****************************************
	 * Change the GUI board size
	 * @param rows the num of rows
	 * @param columns the num columns
	 ****************************************/
	@Override
	public void changeBoardSize(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		
		getColumnConstraints().clear();
		for (int c = 0; c < columns; c++) {
			ColumnConstraints columnConstraints = new ColumnConstraints();
			columnConstraints.setHgrow(ALWAYS);
			double value = (1 / (1.0 * columns)) * 100;
			columnConstraints.setPercentWidth(value);
			columnConstraints.setHalignment(HPos.CENTER);
			columnConstraints.setFillWidth(true);
			
			getColumnConstraints().add(columnConstraints);
		}
		
		getRowConstraints().clear();
		
		for (int r = 0; r < rows; r++) {
			RowConstraints rowConstraints = new RowConstraints();
			rowConstraints.setVgrow(ALWAYS);
			rowConstraints.setFillHeight(true);
			rowConstraints.setValignment(VPos.CENTER);
			rowConstraints.setPercentHeight((1 / (1.0 * rows)) * 100);
			
			getRowConstraints().add(rowConstraints);
		}
		
	}
	
	/*****************************************
	 * Clear the tiles
	 ****************************************/
	@Override
	public void clear() {
		getChildren().clear();
	}
	
	/*****************************************
	 * Place a cell
	 * @param r the row
	 * @param c the column
	 * @param value the value
	 ****************************************/
	@Override
	public void placeCell(int r, int c, int value) {
		CellView cell = new CellView(value);
		
		cell.setEffect(new Reflection(0.01, 0.4, 0.25, 0.0));
		fadeInToFull(cell);
		replaceCell(cell, r, c);
	}
	
	/*****************************************
	 * put a cell back on the grid after animating
	 * it on the transparent view overlay
	 * @param cell the CellView
	 * @param r the row
	 * @param c the column
	 * @return
	 ****************************************/
	protected CellView replaceCell(CellView cell, int r, int c) {
		cell.setTranslateY(0);
		cell.setTranslateX(0);
		setHalignment(cell, HPos.CENTER);
		setValignment(cell, VPos.CENTER);
		cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		setVgrow(cell, ALWAYS);
		setHgrow(cell, ALWAYS);
		if (c > this.columns | r > this.rows) {
			throw new RuntimeException();
		}
		add(cell, c, r);
		cell.autosize();
		return cell;
	}
	
	/*****************************************
	 * @param oldR the old row
	 * @param oldC the old col
	 * @param toRow move to
	 * @param toColumn move to
	 ****************************************/
	@Override
	public void moveCell(int oldR, int oldC, int toRow, int toColumn) {
		CellView toMove = getCell(oldR, oldC);
		getChildren().remove(toMove);
		final TranslateTransition transition
				= createCellMoveAnimation(toMove, toRow, toColumn);
		
		transition.setOnFinished(event -> {
			viewOverlay.getChildren().remove(toMove);
			replaceCell(toMove, toRow, toColumn);
			ANIMATING = false;
		});
		
		SLIDE_TRANSITION.getChildren().add(transition);
	}
	
	/*****************************************
	 * Called to animate a cell being combined
	 * @param fromRow  from the row
	 * @param fromColumn from the column
	 * @param toRow to the row
	 * @param toColumn to column
	 * @param newValue the new Value
	 ****************************************/
	@Override
	public void moveAndMerge(int fromRow, int fromColumn, int toRow, int toColumn, int newValue) {
		CellView toMove = getCell(fromRow, fromColumn);
		
		
		final TranslateTransition ts = createCellMoveAnimation(toMove, toRow, toColumn);
		final FadeTransition fadeOut = new FadeTransition(SCALE_DURATION.multiply(2), toMove);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.setAutoReverse(false);
		fadeOut.setCycleCount(1);
		
		ts.setOnFinished(event -> {
			CellView moveTo = getCell(toRow, toColumn);
			moveTo.setValue(newValue);
			final ScaleTransition anim = createValueChangeAnim(moveTo);
			anim.setOnFinished(event1 -> this.ANIMATING = false);
			Platform.runLater(anim::play);
		});
		
		fadeOut.setOnFinished(event -> viewOverlay.getChildren().remove(toMove));
		
		final SequentialTransition seq = new SequentialTransition(ts, fadeOut);
		
		SLIDE_TRANSITION.getChildren().add(seq);
	}
	
	private final Duration SCALE_DURATION = new Duration(50);
	
	/*****************************************
	 * Creates an animation for the value changing on a cell
	 * @param toScale the CellView
	 * @return a ScaleAnimation
	 ****************************************/
	private ScaleTransition createValueChangeAnim(CellView toScale) {
		ScaleTransition sts = new ScaleTransition(SCALE_DURATION, toScale);
		sts.setToX(1.1);
		sts.setToY(1.1);
		sts.setCycleCount(2);
		sts.setInterpolator(Interpolator.EASE_BOTH);
		sts.setAutoReverse(true);
		
		return sts;
	}
	
	/*****************************************
	 * @param r the row
	 * @param c the column
	 * @return the CellView at that location
	 ****************************************/
	@Nullable
	private CellView getCell(int r, int c) {
		return (CellView) getChildren().stream()
				.filter(node ->
						GridPane.getColumnIndex(node) == c
								&& GridPane.getRowIndex(node) == r)
				.findAny().orElse(null);
	}
	
	/*****************************************
	 * @param toMove the CellView to move
	 * @param toRow move to this row
	 * @param toColumn move to this column
	 * @return an animation showing the cell moving across the board
	 ****************************************/
	private TranslateTransition createCellMoveAnimation(CellView toMove,
																		 int toRow,
																		 int toColumn) {
		double xPos = toMove.getLayoutX();
		double yPos = toMove.getLayoutY();
		
		double height = toMove.getHeight();
		double width = toMove.getWidth();
		
		double toXPos, toYPos;
		
			if (toRow == GridPane.getRowIndex(toMove)) {
				toYPos = 0;
				toXPos = xPos - toColumn * (width + PADDING);
			} else {
				toXPos = 0;
				toYPos = yPos - toRow * (height + PADDING);
			}
		
		toMove.setPrefHeight(height);
		toMove.setPrefWidth(width);
		getChildren().remove(toMove);
		this.viewOverlay.getChildren().add(toMove);
		viewOverlay.requestLayout();
		
		TranslateTransition transition = new TranslateTransition(TRANSLATE_DURATION, toMove);
		transition.setNode(toMove);
		transition.setInterpolator(Interpolator.EASE_OUT);
		transition.setToX(GridPane.getColumnIndex(toMove)
				< toColumn ? Math.abs(toXPos) : -1 * Math.abs(toXPos));
		transition.setToY(GridPane.getRowIndex(toMove)
				< toRow ? Math.abs(toYPos) : -1 * Math.abs(toYPos));
		
		return transition;
	}
	
	/*****************************************
	 * animates a node fading in
	 * @param node the node to "fade in"
	 ****************************************/
	private void fadeInToFull(Node node) {
		FadeTransition transition = new FadeTransition(FADE_IN_DURATION, node);
		transition.setNode(node);
		transition.setFromValue(0.0);
		transition.setToValue(1.0);
		transition.setAutoReverse(false);
		transition.setCycleCount(1);
		Platform.runLater(transition::play);
	}
	
	/*****************************************
	 * Method called when the Slide method is complete
	 ****************************************/
	@Override
	public void onSlideComplete() {
		ANIMATING = true;
		SLIDE_TRANSITION.setOnFinished(event ->
				SLIDE_TRANSITION.getChildren().clear());
		Platform.runLater(() -> SLIDE_TRANSITION.play());
	}
	
	
	/*****************************************
	 * @return true if animating, checked before slide method is called
	 ****************************************/
	@Override
	public boolean isAnimating() {
		return ANIMATING;
	}
	
	/*****************************************
	 * Displays a message on an overlay over the screen
	 * @param message the message to display
	 * @param buttonOptions the options for the buttons with actions to perform on selection
	 ****************************************/
	@Override
	public void displayGameMessage(String message, HashMap<String, Runnable> buttonOptions) {
		Rectangle rectangle = new Rectangle(viewOverlay.getWidth(), viewOverlay.getHeight());
		rectangle.setFill(Color.BLACK);
		FadeTransition transition = new FadeTransition(new Duration(600), rectangle);
		transition.setFromValue(0.0);
		transition.setToValue(0.5);
		transition.setAutoReverse(false);
		transition.setCycleCount(1);
		transition.setOnFinished(event -> {
			GridPane pane = new GridPane();
			pane.setAlignment(Pos.CENTER);
			pane.setVgap(20);
			Label labelMessage = new Label(message);
			Font font = new Font(Game.getGame().getGameFont().getName(), 60);
			labelMessage.setTextFill(Color.WHITE.darker());
			labelMessage.setEffect(new DropShadow(7, Color.BLACK));
			labelMessage.setFont(font);
			setHalignment(labelMessage, HPos.CENTER);
			labelMessage.setTextAlignment(TextAlignment.CENTER);
			pane.add(labelMessage, 0, 0);
			
			Iterator<Map.Entry<String, Runnable>> options =
					buttonOptions.entrySet().iterator();
			
			Font buttonFont = new Font(font.getName(), 30);
			
			while (options.hasNext()) {
				Map.Entry<String, Runnable> next = options.next();
				final Button button = new Button(next.getKey());
				button.setOnAction(event1 -> next.getValue().run());
				button.setBackground(
						new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
				button.setFont(buttonFont);
				final Border unfocused = new Border(new BorderStroke(Color.DARKGREY,
						BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3)));
				final Border focused = new Border(new BorderStroke(Color.ORANGE,
							BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(3)));
				button.setBorder(unfocused);
				button.setOnMouseEntered(event1 -> button.setBorder(focused));
				button.setOnMouseExited(event1 -> button.setBorder(unfocused));
				pane.add(button, 0, pane.getChildren().size());
				setHalignment(button, HPos.CENTER);
			}
			
			((StackPane) getParent()).getChildren().add(pane);
			fadeInToFull(pane);
		});
		((StackPane) getParent()).getChildren().add(rectangle);
		Platform.runLater(transition::play);
	}
	
	/*****************************************
	 * removes the overlay message from the game
	 ****************************************/
	@Override
	public void removeGameMessage() {
		List<Node> mark = new LinkedList<>();
		((StackPane) getParent()).getChildren().forEach(node -> {
			if (node != this && node != viewOverlay) {
				mark.add(node);
			}
		});
		mark.forEach(node -> ((StackPane)
				getParent()).getChildren().remove(node));
	}
}
