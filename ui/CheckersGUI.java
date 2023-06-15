package ui;
import core.CheckersLogic;
import core.Computer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;



/**
 * The CheckersGUI class represents a graphical user interface for a Checkers game.
 * It extends the Application class from JavaFX and provides methods for displaying
 * different screens of the game and handling user input.
 * @author Samuel White
 * @version 1.03
 */
public class CheckersGUI extends Application{

    private Stage primaryStage;
    private CheckersLogic game;
    private char winnerChar;
    private boolean playComputer;
    public boolean gameOver;
    private Label turnStatus = new Label("Greens turn to move");
    private Label warning = new Label("");
    private Label options = new Label("");
    core.Computer computer;
    private final int BOARD_SIZE = 8;

    /**
     * The main method of the CheckersGUI class.
     * It launches the JavaFX application.
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the CheckersGUI application.
     * It sets up the primary stage and shows the opponent choice screen.
     * @param primaryStage the primary stage for the application
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Checkers Game");
        warning.setTextFill(Color.RED);
        showOpponentChoiceScreen();
    }

    /**
     * Displays the opponent choice screen.
     * It allows the user to choose between playing against another player or the computer.
     */
    public void showOpponentChoiceScreen() {
        Button playerButton = new Button();
        Button computerButton = new Button();
        playerButton.setText("Player");
        computerButton.setText("Computer");

        playerButton.setOnAction(e -> {
            playComputer = false;
            game = new CheckersLogic("PvP");
            showMoveScreen();
        });

        computerButton.setOnAction(e -> {
            playComputer = true;
            game = new CheckersLogic("PvC");
            computer = (core.Computer) game.getCurrentPlayersTurn().getOtherPlayer();
            showMoveScreen();
        });

        Label question = new Label("Choose your opponent:");

        GridPane root = new GridPane();
        root.setPadding(new Insets(10)); // Set overall padding
        root.setVgap(10); // Set vertical spacing between nodes
        root.add(question, 0, 0);
        root.add(playerButton, 0, 1);
        root.add(computerButton, 0, 2);

        // Apply CSS styles for margin and line spacing
        question.setStyle("-fx-font-size: 16px; -fx-margin-bottom: 10px;");
        playerButton.setStyle("-fx-margin-bottom: 10px;");
        computerButton.setStyle("-fx-margin-bottom: 10px;");

        transitionToScreen(root, 200, 200);

    }

    /**
     * Displays the move screen.
     * It allows the user to input their move and shows the game board.
     */
    public void showMoveScreen() {
        Label directions = new Label("Choose a cell position of piece to be moved and the new position. e.g., 3a-4b");

        TextField userInput = new TextField();
        String color;
        if (game.getCurrentPlayersTurn().getPlayerIcon() == 'x' || playComputer) {
            color = "Green";
        } else {
            color = "Red";
        }

        turnStatus = new Label(color + "s turn to move");

        Button submit = new Button();
        submit.setText("Submit");
        submit.setOnMouseClicked(e -> {
            String move = userInput.getText();
            handleMove(move);
        });

        GridPane board = buildBoardState();
        VBox controls = new VBox();
        controls.getChildren().setAll(turnStatus, warning, options, directions, userInput,submit);

        BorderPane root = new BorderPane();
        root.setCenter(board);
        root.setBottom(controls);

        transitionToScreen(root, 500, 800);

    }

    /**
     * Displays the end game screen with the winner's information.
     * @param color The color of the winning player.
     */
    public void showEndGameScreen(String color) {
        GridPane board = buildBoardState();
        Label endGame = new Label("GAME OVER");
        Label winner = new Label(color + " wins!");
        Button closeButton = new Button();
        closeButton.setText("Close");
        closeButton.setOnMouseClicked(event -> primaryStage.close());

        VBox root = new VBox();
        root.getChildren().setAll(board, endGame, winner, closeButton);

        transitionToScreen(root, 600,600);
    }

    /**
     * Handles the player's move and checks for game over conditions.
     * @param move The move command provided by the player.
     */
    public void handleMove(String move) {
        try {
            game.makeMove(move);
            if (this.warning.getText().length() > 0) {
                this.warning.setText("");
            }
            if (game.checkWin()) {
                gameOver = true;
                winnerChar = game.getCurrentPlayersTurn().getPlayerIcon();
                if (winnerChar == 'x') {
                    showEndGameScreen("Green");
                    return;
                } else {
                    showEndGameScreen("Red");
                    return;
                }
            }
        } catch (IllegalArgumentException m) {
            this.warning.setText("The move command given has an incorrect format, try again.");
            showMoveScreen();
            return;
        }

        doubleJump();
    }

    /**
     * Swaps the turn between players and handles the computer's move if playing against the computer.
     */
    public void swapTurn() {
        game.swapTurn();

        if (playComputer) {
            String computerCommand = computer.takeTurn();
            game.makeMove(computerCommand);
            if (game.checkWin()) {
                gameOver = true;
                winnerChar = game.getCurrentPlayersTurn().getPlayerIcon();
                if (winnerChar == 'x') {
                    showEndGameScreen("Green");
                    return;
                } else {
                    showEndGameScreen("Red");
                    return;
                }
            }
            game.swapTurn();
        }

        showMoveScreen();
    }

    /**
     * Handles the double jump scenario, allowing the player to select the desired option.
     * If playing against the computer, the computer automatically selects the option.
     */
    public void doubleJump() {
        if (game.getCurrentPlayersTurn().goAgain) {
            if (game.getCurrentPlayersTurn() instanceof core.Computer) {
                game.selectDoubleOption(1);
                return;
            }
            options.setText(game.getCurrentPlayersTurn().doubleJumpOptions);
            Button option1 = new Button();
            Button option2 = new Button();
            option1.setText("Option 1");
            option2.setText("Option 2");

            option1.setOnMouseClicked(event -> {
                game.selectDoubleOption(1);
                if (game.checkWin()) {
                    options.setText("");
                    gameOver = true;
                    winnerChar = game.getCurrentPlayersTurn().getPlayerIcon();
                    if (winnerChar == 'x') {
                        showEndGameScreen("Green");
                        return;
                    } else {
                        showEndGameScreen("Red");
                        return;
                    }
                }
                swapTurn();
            });

            option2.setOnMouseClicked(event -> {
                game.selectDoubleOption(2);
                if (game.checkWin()) {
                    options.setText("");
                    gameOver = true;
                    winnerChar = game.getCurrentPlayersTurn().getPlayerIcon();
                    if (winnerChar == 'x') {
                        showEndGameScreen("Green");
                        return;
                    } else {
                        showEndGameScreen("Red");
                        return;
                    }
                }
                swapTurn();
            });

            GridPane board = buildBoardState();

            Label directions = new Label("There are double jump options, please select one by selecting it's respective button.");

            VBox controls = new VBox();
            controls.getChildren().setAll(turnStatus, warning, directions, options, option1, option2);

            BorderPane root = new BorderPane();
            root.setCenter(board);
            root.setBottom(controls);
            transitionToScreen(root, 600,700);
        } else {
            swapTurn();
        }
    }

    /**
     * Builds the graphical representation of the current state of the game board.
     * @return The GridPane representing the game board.
     */
    public GridPane buildBoardState() {
        char[] horizontalIndices = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        GridPane board = new GridPane();
        char[][] boardState = game.getBoardState();

        for (int i = 1; i <= BOARD_SIZE; i++) {
            board.add(new Label(String.valueOf(horizontalIndices[i-1])), i, 0);
            board.add(new Label(String.valueOf(9-i)), 0, i);
        }
        int color = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            color++;
            for (int j = 0; j < BOARD_SIZE; j++) {
                StackPane newStackPane = new StackPane();
                Rectangle square = new Rectangle(60,60);
                if (color % 2 == 0) {
                    square.setFill(Color.BLACK);
                } else {
                    square.setFill(Color.WHITE);
                }
                if (boardState[i][j] == 'x') {
                    Circle piece = new Circle(20, Color.GREEN);
                    newStackPane.getChildren().addAll(square,piece);
                } else if (boardState[i][j] == 'o') {
                    Circle piece = new Circle(20, Color.RED);
                    newStackPane.getChildren().addAll(square,piece);
                } else {
                    newStackPane.getChildren().add(square);
                }
                board.add(newStackPane, j+1, i+1);
                color++;
            }
        }

        return board;
    }


    /**
     * Transitions to a new screen with the specified root element, width, and height.
     * @param root   The root element of the screen.
     * @param width  The width of the screen.
     * @param height The height of the screen.
     */
    private void transitionToScreen(Parent root, int width, int height) {
        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
