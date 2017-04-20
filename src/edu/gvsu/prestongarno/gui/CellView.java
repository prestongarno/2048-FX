package edu.gvsu.prestongarno.gui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import static javafx.scene.paint.Color.*;


/**
 * *************************************************
 * project3 - edu.gvsu.prestongarno.gui - by Preston Garno on 3/20/17
 * <p>
 * Subclass of Javafx label
 ***************************************************/
public class CellView extends Label {

	/**
	 * Duration of the slide transition
	 */
	private final Duration TRANSLATE_DURATION = new Duration(200);
	/**
	 * Duration of the slide transition
	 */
	private final Duration SCALE_DURATION     = new Duration(50);

	private final CornerRadii CORNER_RADIUS = new CornerRadii(2);
	private final Font        FONT          = new Font("Ubuntu", 48);

	private final ScaleTransition       onValueChangedAnim;
	private int valueProperty;
	/**
	 * the current transition
	 ****************************************/
	private final FadeTransition        fade;
	private final TranslateTransition   moveAnim;

	private final Runnable fader;
	private final Runnable translater;

	/*****************************************
	 * The GUI representation of a tile in the game
	 * @param value the valueProperty to initialize with
	 ****************************************/
	public CellView(int value) {

		// property valueProperty
		this.valueProperty = value;
		setText(Integer.toString(value));

		// valueProperty changed animation
		onValueChangedAnim = new ScaleTransition(SCALE_DURATION.multiply(2.0f), this);
		onValueChangedAnim.setToX(1.1);
		onValueChangedAnim.setToY(1.1);
		onValueChangedAnim.setCycleCount(2);
		onValueChangedAnim.setInterpolator(Interpolator.EASE_OUT);
		onValueChangedAnim.setAutoReverse(true);

		// assign fading animation
		fade = new FadeTransition(SCALE_DURATION, this);
		fade.setAutoReverse(false);
		fade.setCycleCount(1);

		//bind valueProperty to actions

		// visual
		this.setBackground(getFill(value));
		this.setAlignment(Pos.CENTER);
		this.setFont(FONT);

		// performance - related stuff
		this.setCache(true);
		this.setCacheShape(true);
		this.setCacheHint(CacheHint.SPEED);
		moveAnim = new TranslateTransition(TRANSLATE_DURATION, this);
		moveAnim.setInterpolator(Interpolator.EASE_OUT);
		translater = moveAnim::playFromStart;
		fader = fade::playFromStart;
	}

	public Runnable translate(double toX, double toY, Runnable onComplete) {
		requestLayout();
		//moveAnim.setFromY(getLayoutY());
		//moveAnim.setFromX(getLayoutX());
		moveAnim.setToX(toX);
		moveAnim.setToY(toY);
		moveAnim.setOnFinished(event -> Platform.runLater(onComplete));
		return translater;
	}

	public void translate(double toX, double toY) {
		this.translate(toX, toY, null);
	}

	private int getValueProperty() {
		return valueProperty;
	}


	public final Runnable fadeOut() {
		fade.setFromValue(1.0);
		fade.setToValue(0.0);
		return this.fader;
	}

	public final Runnable fadeIn() {
		fade.setFromValue(0.0);
		fade.setToValue(1.0);
		return this.fader;
	}
	/*****************************************
	 * Set the label, background, and animate the cell changing
	 * @param value the valueProperty to display on the cell
	 ****************************************/
	public Transition setValueProperty(int value) {
		this.valueProperty = value;
		setBackground(getFill(value));
		setText(Integer.toString(value));
		return this.onValueChangedAnim;
	}

	boolean isAnimating() {
		return this.moveAnim.getCurrentRate() != 0.0d
				| this.onValueChangedAnim.getCurrentRate() != 0.0d
				| this.moveAnim.getCurrentRate() != 0.0d;
	}

	/*****************************************
	 * @param value the cell valueProperty
	 * @return the color cooresponding
	 ****************************************/
	private Background getFill(int value) {
		switch (value) {
			case 0: return null;
			case 2: return BG_ON_2;
			case 4: return BG_ON_4;
			case 8: return BG_ON_8;
			case 16: return BG_ON_16;
			case 32: return BG_ON_32;
			case 64: return BG_ON_64;
			case 128: return BG_ON_128;
			case 256: return BG_ON_256;
			case 512: return BG_ON_512;
			case 1024: return BG_ON_1024;
			case 2048: return BG_ON_2048;
			case 4096: return BG_ON_4096;
			case 4096 * 2: return BG_ON_MAX;
			default: throw new IllegalStateException();
		}
	}


	private final BackgroundFill ON_2    = new BackgroundFill(LIGHTYELLOW, CORNER_RADIUS, null);
	private final BackgroundFill ON_4    = new BackgroundFill(DARKGOLDENROD, CORNER_RADIUS, null);
	private final BackgroundFill ON_8    = new BackgroundFill(BLUEVIOLET, CORNER_RADIUS, null);
	private final BackgroundFill ON_16   = new BackgroundFill(BROWN, CORNER_RADIUS, null);
	private final BackgroundFill ON_32   = new BackgroundFill(new Color(0.0, 0.4, 0.40, 1.0), CORNER_RADIUS, null);
	private final BackgroundFill ON_64   = new BackgroundFill(GREEN, CORNER_RADIUS, null);
	private final BackgroundFill ON_128  = new BackgroundFill(DEEPPINK, CORNER_RADIUS, null);
	private final BackgroundFill ON_256  = new BackgroundFill(LIME, CORNER_RADIUS, null);
	private final BackgroundFill ON_512  = new BackgroundFill(ORCHID, CORNER_RADIUS, null);
	private final BackgroundFill ON_1024 = new BackgroundFill(PURPLE, CORNER_RADIUS, null);
	private final BackgroundFill ON_2048 = new BackgroundFill(GOLD, CORNER_RADIUS, null);
	private final BackgroundFill ON_4096 = new BackgroundFill(ORANGE, CORNER_RADIUS, null);
	private final BackgroundFill ON_MAX  = new BackgroundFill(BLUEVIOLET, CORNER_RADIUS, null);

	private final Background BG_ON_2    = new Background(ON_2);
	private final Background BG_ON_4    = new Background(ON_4);
	private final Background BG_ON_8    = new Background(ON_8);
	private final Background BG_ON_16   = new Background(ON_16);
	private final Background BG_ON_32   = new Background(ON_32);
	private final Background BG_ON_64   = new Background(ON_64);
	private final Background BG_ON_128  = new Background(ON_128);
	private final Background BG_ON_256  = new Background(ON_256);
	private final Background BG_ON_512  = new Background(ON_512);
	private final Background BG_ON_1024 = new Background(ON_1024);
	private final Background BG_ON_2048 = new Background(ON_2048);
	private final Background BG_ON_4096 = new Background(ON_4096);
	private final Background BG_ON_MAX  = new Background(ON_MAX);
}
