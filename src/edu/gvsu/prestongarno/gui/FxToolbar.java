package edu.gvsu.prestongarno.gui;

import edu.gvsu.preston.interfaces.GameToolbar;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

import java.io.IOException;


/** **************************************************
 * project3 - edu.gvsu.prestongarno.gui - by Preston Garno on 3/19/17
 * ***************************************************/
public class FxToolbar extends ToolBar implements GameToolbar{
	
	
	/*****************************************
	 * the toolbar
	 ****************************************/
	public FxToolbar(){
		FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/customtoolbar.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.setFocusTraversable(false);
		this.setBackground(new Background(new BackgroundFill(Game.getGame().getGameThemeColor(), null, null)));
		this.toFront();
	}
	
	@Override
	public void addButton(String resourceName, String ID, Runnable action) {
		Image icon = loadImage("/resources/" + resourceName);
		Button button;
		if(icon != null) {
			ImageView graphic = new ImageView(icon);
			graphic.setFitHeight(40);
			graphic.setFitWidth(40);
			button = new Button("", graphic);
			button.setTooltip(new Tooltip(resourceName));
			button.setMaxSize(40,40);
		}  else {
			button = new Button(resourceName);
			button.setPrefHeight(50);
		}
		button.setId(ID);
		button.setOnAction(event -> {
			button.setDisable(true);
			action.run();
			button.setDisable(false);
		});
		button.setFocusTraversable(false);
		button.setDisable(true);
		getItems().add(button);
	}
	
	private Image loadImage(String resourceURL) {
		try {
			return new Image(resourceURL);
		} catch (IllegalArgumentException ie) {
			return null;
		}
	}
	
	@Override
	public void setDisabled(String ID, boolean disable) {
		 Button btn = (Button) this.getItems()
				 .stream()
				 .filter(node -> node.getId().equals(ID))
				 .findAny().orElse(null);
		 if(btn != null) {
		 	btn.setDisable(disable);
		 }
	}
	
	@Override
	public void disableAll(boolean disabled) {
		this.getItems().stream()
				.filter(node -> node instanceof Button).map(node -> ((Button) node))
				.forEach(button -> button.setDisable(disabled));
	}
}
