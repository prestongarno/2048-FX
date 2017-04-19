package edu.gvsu.prestongarno.gui;

import edu.gvsu.preston.interfaces.HUD;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


/** **************************************************
 * project3 - edu.gvsu.prestongarno.gui.fxml - by Preston Garno on 3/21/17
 * ***************************************************/
public class ScoreBoard implements HUD{
	
	
	private int moveCount;
	private Label movesNumber;
	private FlowPane flowPane;
	private Label scoreLabel;
	private Label winningValueLabel;
	
	public ScoreBoard(FlowPane flowPane) {
		this.flowPane = flowPane;
		
		flowPane.setBackground(new Background(
				new BackgroundFill(Game.getGame().getGameThemeColor(), null, null)));
		flowPane.setOrientation(Orientation.VERTICAL);
		flowPane.setMinSize(180,180);
		flowPane.setPadding(new Insets(10));
		
		Label scoreDesc = new Label("Current Score:");
		applyDescLabelStyle(scoreDesc);
		Label winningValDesc = new Label("Target tile value:");
		applyDescLabelStyle(winningValDesc);
		winningValueLabel = new Label("" + 0);
		applyHighlightLabelStyle(winningValueLabel);
		scoreLabel = new Label("" + 0);
		applyHighlightLabelStyle(scoreLabel);
		Label movesLabel = new Label("Moves:");
		applyDescLabelStyle(movesLabel);
		 movesNumber= new Label("0");
		 applyHighlightLabelStyle(movesNumber);
		
		flowPane.getChildren().addAll(
				winningValDesc, winningValueLabel,
				scoreDesc, scoreLabel,
				movesLabel, movesNumber);
	}
	
	private void applyHighlightLabelStyle(Label label) {
		label.setAlignment(Pos.CENTER);
		label.setFont(new Font(Game.getGame().getGameFont().getName(), 48));
		label.setTextFill(Color.ORANGE);
	}
	
	private void applyDescLabelStyle(Label label) {
		flowPane.setAlignment(Pos.CENTER_LEFT);
		label.setAlignment(Pos.CENTER);
		label.setFont(Game.getGame().getGameFont());
		label.setTextFill(Color.LIGHTGRAY);
	}
	
	
	@Override
	public void incrementMoveCount() {
		this.movesNumber.setText("" + (moveCount = moveCount + 1));
	}
	
	@Override
	public void decrementMoveCount() {
		this.movesNumber.setText("" + (moveCount = moveCount - 1));
	}
	
	@Override
	public void resetMoveCount() {
		moveCount = 0;
		this.movesNumber.setText("" + moveCount);
	}
	
	@Override
	public int getMoveCount() {
		return this.moveCount;
	}
	
	@Override
	public void displayScore(String score) {
		this.scoreLabel.setText(score);
	}
	
	@Override
	public void displayWinningValue(int i) {
		winningValueLabel.setText("" + i);
	}
	
	@Override
	public void setWinningValue(String value) {
		this.winningValueLabel.setText("" + value);
	}
}
