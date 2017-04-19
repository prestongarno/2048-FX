package edu.gvsu.prestongarno.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import static javafx.scene.paint.Color.*;


/** **************************************************
 * project3 - edu.gvsu.prestongarno.gui - by Preston Garno on 3/20/17
 *
 * Subclass of Javafx label
 * ***************************************************/
public class CellView extends Label {
	
	
	private static final CornerRadii CORNER_RADIUS = new CornerRadii(2);
	private static final Font FONT = new Font("Ubuntu", 48);
	private int value;
	
	/*****************************************
	 * The GUI representation of a tile in the game
	 * @param value the value to initialize with
	 ****************************************/
	public CellView(int value) {
		this.value = value;
		this.setBackground(new Background(getFill(value)));
		this.setText("" + value);
		this.setAlignment(Pos.CENTER);
		this.setFont(FONT);
	}
	
	/*****************************************
	 * @param value the value to display on the cell
	 ****************************************/
	public void setValue(int value) {
		this.value = value;
		setText("" + value);
		this.setBackground(new Background(getFill(value)));
	}
	
	/*****************************************
	 * @param value the cell value
	 * @return the color cooresponding
	 ****************************************/
	static BackgroundFill getFill(int value) {
		switch (value) {
		case 2: return new BackgroundFill(Color.STEELBLUE, CORNER_RADIUS, null);
		case 4: return new BackgroundFill(DARKGREEN, CORNER_RADIUS, null);
		case 8: return new BackgroundFill(TURQUOISE, CORNER_RADIUS, null);
		case 16: return new BackgroundFill(BROWN, CORNER_RADIUS, null);
		case 32: return new BackgroundFill(BLUE, CORNER_RADIUS, null);
		case 64: return new BackgroundFill(DARKGOLDENROD, CORNER_RADIUS, null);
		case 128: return new BackgroundFill(DEEPPINK, CORNER_RADIUS, null);
		case 256: return new BackgroundFill(LIME, CORNER_RADIUS, null);
		case 512: return new BackgroundFill(LIGHTSLATEGRAY, CORNER_RADIUS, null);
		case 1024: return new BackgroundFill(PURPLE, CORNER_RADIUS, null);
		case 2048: return new BackgroundFill(GOLD, CORNER_RADIUS, null);
		case 4096: return new BackgroundFill(ORANGE, CORNER_RADIUS, null);
		case 4096*2: return new BackgroundFill(DARKORCHID, CORNER_RADIUS, null);
		default: return new BackgroundFill(null, CORNER_RADIUS, null);
		}
	}
	
	public int getValue() {
		return value;
	}
}
