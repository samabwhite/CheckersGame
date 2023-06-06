package ui;
import core.CheckersLogic;
import java.util.Scanner;


/**
 * The CheckersTextConsole class represents a text-based UI console for the Checkers game.
 * @author Samuel White
 */
public class CheckersTextConsole {

    private boolean gameOver = false;
    public char winnerChar;

    /**
     * The main method is the entry point of the CheckersTextConsole program.
     *
     * @param args The commandline arguments passed to the program.
     */
    public static void main(String[] args) {
        CheckersTextConsole game = new CheckersTextConsole();

        if (game.playComputer()) {
            CheckersLogic rules = new CheckersLogic("PvC");
            core.Computer computer = (core.Computer) rules.getCurrentPlayersTurn().getOtherPlayer();
            while (!game.gameOver) {
                game.printBoardState(rules);
                game.displayTurn(rules);
                game.userMove(rules);
                if (rules.checkWin()) {
                    game.gameOver = true;
                    game.winnerChar = rules.getCurrentPlayersTurn().getPlayerIcon();
                    game.displayResults();
                    break;
                }
                rules.swapTurn();
                String computerCommand = computer.takeTurn();
                rules.makeMove(computerCommand);
                if (rules.checkWin()) {
                    game.gameOver = true;
                    game.winnerChar = rules.getCurrentPlayersTurn().getPlayerIcon();
                    game.printBoardState(rules);
                    game.displayResults();
                    break;
                }
                rules.swapTurn();
            }
        } else {
            CheckersLogic rules = new CheckersLogic("PvP");
            while (!game.gameOver) {
                game.printBoardState(rules);
                game.displayTurn(rules);
                game.userMove(rules);
                if (rules.checkWin()) {
                    game.gameOver = true;
                    game.winnerChar = rules.getCurrentPlayersTurn().getPlayerIcon();
                    game.displayResults();
                    break;
                }
                rules.swapTurn();
            }
        }
    }


    public boolean playComputer() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Begin Game. Enter ‘P’ if you want to play against another player; enter ‘C’ to play against computer. ");
        boolean passed = false;
        String userInput = scanner.next();
        if (userInput.charAt(0) == 'C') {
            return true;
        } else if (userInput.charAt(0) == 'P') {
            return false;
        } else {
            System.out.println("Incorrect format, please try again:");
            return playComputer();
        }
    }


    /**
     * Prints the current state of the game board to the console.
     *
     * @param rules The CheckersLogic object representing the game rules and state.
     */
    public void printBoardState(CheckersLogic rules) {
        char[][] boardState = rules.getBoardState();
        System.out.print("\n");
        for (int i = 0; i < boardState.length; i++) {
            System.out.print(8 - i + " |");
            for (int n = 0; n < boardState[0].length; n++) {
                System.out.print(" " + boardState[i][n] + " |");
            }
            System.out.print("\n");
        }
        System.out.print("    a   b   c   d   e   f   g   h \n\n");
    }

    /**
     * Displays the current player's turn and prompts the user for a move input.
     *
     * @param rules The CheckersLogic object representing the game rules and state.
     */
    public void displayTurn(CheckersLogic rules) {
        System.out.println("Player " + rules.getCurrentPlayersTurn().getPlayerIcon() + " - your turn.");
        System.out.println("Choose a cell position of piece to be moved and the new position. e.g., 3a-4b");

    }


    /**
     * Handles the user's move input and updates the game state accordingly.
     *
     * @param rules The CheckersLogic object representing the game rules and state.
     */
    public void userMove(CheckersLogic rules) {
        boolean pass = false;
        Scanner scanner = new Scanner(System.in);

        while (!pass) {
            System.out.print("Your Move: ");
            String move = scanner.next();
            pass = rules.makeMove(move);
            if (rules.getCurrentPlayersTurn().goAgain) {
                if (rules.getCurrentPlayersTurn() instanceof core.Computer) {
                    rules.selectDoubleOption(1);
                    return;
                }
                System.out.println("There are double jump options, please select one by typing it's respective number. i.e. 1");
                System.out.println(rules.getCurrentPlayersTurn().doubleJumpOptions);
                int doubleJumpSelection = scanner.nextInt();
                rules.selectDoubleOption(doubleJumpSelection);
            }
            if (!pass) {
                System.out.println("The move command given has an incorrect format, try again.");
            }
        }
    }

    /**
     * Displays the winner of the game.
     */
    public void displayResults() {
        System.out.print("Player " + winnerChar + " Wins!");
    }
}
