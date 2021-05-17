package com.internshala.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 80;

	private static final String discColour1 = "24303E";
	private static final String discColour2 = "4CAA88";

	private  String PLAYER_ONE = "Player One";
	private  String PLAYER_TWO = "Player Two";

	private boolean isPlayerOneTurn = true;

	private Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS];

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscPane;

	@FXML
	public Label playerTurn;

	@FXML
	public TextField playerOneName;

	@FXML
	public TextField playerTwoName;

	@FXML
	public Button setNames;

	private boolean isAllowed = true;


	public void createPlayground() {

		setNames.setOnAction(event -> {
			PLAYER_ONE = playerOneName.getText();
			PLAYER_TWO = playerTwoName.getText();
			playerTurn.setText(PLAYER_ONE);
		});

		Shape rectangleWithHoles = createGameStructuralGrid();
		rootGridPane.add(rectangleWithHoles, 0, 1);

		List<Rectangle> clickRectangle = createClickableColumns();
		for (Rectangle click:clickRectangle)
		{
			rootGridPane.add(click,0,1);
		}

	}


	private Shape createGameStructuralGrid() {

		Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);

		for (int row = 0; row < ROWS; row++) {

			for (int col = 0; col < COLUMNS; col++) {
				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER / 2);
				circle.setCenterX(CIRCLE_DIAMETER / 2);
				circle.setCenterY(CIRCLE_DIAMETER / 2);

				circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				circle.setSmooth(true);

				rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
			}
		}

		rectangleWithHoles.setFill(Color.WHITE);

		return rectangleWithHoles;
	}

	private List<Rectangle> createClickableColumns()
	{
		List<Rectangle> rectangleList = new ArrayList<>();

		for (int i = 0; i < COLUMNS; i++)
		{
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER,(ROWS + 1) * CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(i * (CIRCLE_DIAMETER+5) + CIRCLE_DIAMETER / 4);

			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

			final int column = i;
			rectangle.setOnMouseClicked(event -> {
				if(isAllowed) {
					isAllowed = false;
					insertDisc(new Disc(isPlayerOneTurn), column);
				}
			});

			rectangleList.add(rectangle);
		}

		return rectangleList;
	}

	private  void insertDisc(Disc disc, int column) {

		int rows = ROWS-1;
		while(rows>=0)
		{
			if(validDisc(rows,column) == null)
				break;

			rows--;
		}

		if(rows<0)
		{
			return;
		}

		insertedDiscsArray[rows][column] = disc;
		insertedDiscPane.getChildren().add(disc);

		disc.setTranslateX(column * (CIRCLE_DIAMETER+5) + CIRCLE_DIAMETER / 4);

		int currentRow = rows;
		TranslateTransition translate = new TranslateTransition(Duration.seconds(0.4), disc);
		translate.setToY(rows * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
		translate.setOnFinished(event -> {
			isAllowed = true;
			if(gameEnded(currentRow,column))
			{
				gameOver();
				return;
			}

			isPlayerOneTurn= !isPlayerOneTurn;

			playerTurn.setText(isPlayerOneTurn?PLAYER_ONE:PLAYER_TWO);

		});

		translate.play();

	}

	private void gameOver() {
		String winner= isPlayerOneTurn?PLAYER_ONE: PLAYER_TWO;
		System.out.println("Winner is "+ winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Game Over");
		alert.setHeaderText("!!!CONGRATULATIONS!!!" + winner + ".You are the Winner.");
		alert.setContentText("Do you want to Play again ?");

		ButtonType yes = new ButtonType("Yes, Reset.");
		ButtonType no = new ButtonType("No, Exit");
		ButtonType newBtn = new ButtonType("New Game");
		alert.getButtonTypes().setAll(yes , newBtn ,no);

		Platform.runLater( () -> {
			Optional<ButtonType> btn = alert.showAndWait();
			if(btn.isPresent() && btn.get() == yes)
			{
				resetGame();
			}
			else if(btn.isPresent() && btn.get() == newBtn)
			{
				newGame();
			}
			else
			{
				Platform.exit();
				System.exit(0);
			}
		});
		
	}

	public void resetGame() {


		insertedDiscPane.getChildren().clear();

		for( int row = 0; row< insertedDiscsArray.length ; row ++)
		{
			for (int col = 0; col < insertedDiscsArray[row].length; col++) {

				insertedDiscsArray[row][col]=null;
			}
		}

		isPlayerOneTurn = true;
		playerTurn.setText(PLAYER_ONE);

		createPlayground();


	}

	public void newGame()
	{

		playerOneName.clear();
		playerTwoName.clear();

		insertedDiscPane.getChildren().clear();

		for( int row = 0; row< insertedDiscsArray.length ; row ++)
		{
			for (int col = 0; col < insertedDiscsArray[row].length; col++) {

				insertedDiscsArray[row][col]=null;
			}
		}

		isPlayerOneTurn = true;
		playerTurn.setText(PLAYER_ONE);

		createPlayground();


	}



	private boolean gameEnded(int row, int column)
	{

		List<Point2D> vertical = IntStream.rangeClosed(row - 3,row +3)
				                              .mapToObj(r -> new Point2D(r,column))
				                                   .collect(Collectors.toList());

		List<Point2D> horizontal = IntStream.rangeClosed(column - 3,column +3)
				                              .mapToObj(c -> new Point2D(row,c))
				                                       .collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(row - 3, column +3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6)
				                                     .mapToObj(i -> startPoint1.add(i,-i))
				                                         .collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row - 3, column - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6)
				                           .mapToObj(i -> startPoint2.add(i,i))
				                                  .collect(Collectors.toList());

		boolean isEnded = CheckCombination(vertical) || CheckCombination(horizontal)|| CheckCombination(diagonal1Points) || CheckCombination(diagonal2Points);
		
		return isEnded;
	}

	private boolean CheckCombination(List<Point2D> points) {

		int chain= 0;
		for (Point2D point: points) {

			int rowIndex = (int) point.getX();
			int columnIndex = (int) point.getY();

			Disc disc = validDisc(rowIndex, columnIndex);
			if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn)
			{
				chain ++;
				if (chain == 4)
				return true;
			}
			else
			{
				chain =0;
			}

		}
		return false;
	}

	private Disc validDisc(int row, int column)
	{
		if(row< 0 || row>= ROWS || column< 0 || column >= COLUMNS)
		{
			return null;
		}

		return insertedDiscsArray[row][column];
	}

	private static class Disc extends Circle
	{
		private final boolean isPlayerOneMove;
		public Disc(boolean isPlayerOneMove)
		{
			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER/2);
			setFill(isPlayerOneMove?Color.valueOf(discColour1):Color.valueOf(discColour2));
			setCenterX(CIRCLE_DIAMETER/2);
			setCenterY(CIRCLE_DIAMETER/2);
		}

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
