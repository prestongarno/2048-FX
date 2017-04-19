package edu.gvsu.prestongarno.gui;

import edu.gvsu.preston.GameManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
	
	
	/*****************************************
	 * @param primaryStage the stage (game GUI)
	 * @throws Exception should have this
	 ****************************************/
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("fxml/GameView.fxml"));
		primaryStage.setTitle("2048fX");
		primaryStage.setScene(new Scene(root, 1100,800));
		primaryStage.show();
		new GameManager(Game.getContext(), 4, 4);
	}
	
	
	/*****************************************
	 * main method
	 * @param args none
	 ****************************************/
	public static void main(String[] args) {
		launch(args);
	}
}
