package com.internshala.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();

        controller = loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(rootGridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private MenuBar createMenu()
    {
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(event -> controller.newGame());

        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(event -> controller.resetGame());

        SeparatorMenuItem line = new SeparatorMenuItem();

        MenuItem exitGame = new MenuItem("Exit");
        exitGame.setOnAction(event -> off());

        fileMenu.getItems().addAll(newGame,resetGame,line,exitGame);

        Menu helpMenu = new Menu("Help");

        MenuItem aboutGame = new MenuItem("About the Connect4");
        aboutGame.setOnAction(event -> aboutConnect4());

        SeparatorMenuItem separator = new SeparatorMenuItem();

        MenuItem aboutMe = new MenuItem("About the Developer");
        aboutMe.setOnAction(event -> developer());

        helpMenu.getItems().addAll(aboutGame,separator,aboutMe);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu,helpMenu);

        return  menuBar;

    }

	private void developer() {
    	Alert info = new Alert(Alert.AlertType.INFORMATION);
    	info.setTitle("About the developer");
    	info.setHeaderText("Sambit Sankalp");
    	info.setContentText("Games are those applications which help to spend our " +
			    "times in leisure and having a smooth journey. I like games and developing them and " +
			    "playing it is a great excitement.");
    	info.show();
	}

	private void aboutConnect4() {
		Alert dialog = new Alert(Alert.AlertType.INFORMATION);
		dialog.setTitle("About Connect4 Game");
		dialog.setHeaderText("How to Play the Game?");
		dialog.setContentText("Connect Four is a two-player connection game in which the players first " +
				"choose a color and then take turns dropping colored discs from the top into a " +
				"seven-column, six-row vertically suspended grid. The pieces fall straight down, " +
				"occupying the next available space within the column. The objective of the game is " +
				"to be the first to form a horizontal, vertical, or diagonal line of four of one's own " +
				"discs. Connect Four is a solved game. The first player can always win by playing " +
				"the right moves.");
		dialog.show();
	}

	private void off() {

		Platform.exit();
		System.exit(0);
	}


	public static void main(String[] args) {
        launch(args);
    }
}
