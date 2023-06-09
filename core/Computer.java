package core;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
/**
 * The Computer class represents the Computer/AI player you can select to play against.
 * @author Samuel White
 */
public class Computer extends Player{
    private CheckersLogic game;

    /**
     * Constructs a new Computer player with the specified computerIcon and game.
     * @param computerIcon the icon representing the computer player.
     * @param game the CheckersLogic instance representing the game.
     */
    public Computer(char computerIcon, CheckersLogic game) {
        super(computerIcon);
        this.game = game;
    }

    /**
     * Makes the computer player take its turn.
     * @return a string representing the move command for the computer player.
     */
    public String takeTurn() {
        int[][] movablePieces = allPieces();
        int[][] move = assessMoves(movablePieces);
        String moveCommand = indexConversion(move);
        return moveCommand;
    }

    /**
     * Retrieves all possible pieces that the computer player can move.
     * @return a 2D array containing the coordinates of all movable pieces.
     */
    public int[][] allPieces() {
        int direction = 1;
        ArrayList<int[]> output = new ArrayList<>();

        int[] rightMove;
        int[] leftMove;
        int[] rightJump;
        int[] leftJump;

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int[] currentIndex = {y, x};
                if (game.getPiece(new int[] {y, x}) == 'o') {
                    rightMove = new int[] {y + direction, x + 1};
                    leftMove = new int[] {y + direction, x - 1};
                    if (game.canMoveHere(rightMove) || game.canMoveHere(leftMove)) {
                        output.add(currentIndex);
                        continue;
                    }
                    if (game.withinBoard(rightMove) && game.getPiece(rightMove) == 'x') {
                        rightJump = new int[]{rightMove[0] + direction, rightMove[1] + 1};
                        if (game.canMoveHere(rightJump)) {
                            output.add(currentIndex);
                            continue;
                        }
                    }
                    else if (game.withinBoard(leftMove) && game.getPiece(leftMove) == 'x') {
                        leftJump = new int[] {leftMove[0] + direction, leftMove[1] - 1};
                        if (game.canMoveHere(leftJump)) {
                            output.add(currentIndex);
                            continue;
                        }
                    }
                }
            }
        }

        return game.convertToArray(output);
    }

    /**
     * Assesses the available moves for the computer player and selects the best move.
     * @param movablePieces a 2D array containing the coordinates of all movable pieces.
     * @return a 2D array representing the selected move coordinates.
     */
    public int[][] assessMoves(int[][] movablePieces) {
        for (int[] piece : movablePieces) {
            int[][] possibleJumps = game.getPossibleJumps(piece);
            if (possibleJumps.length != 0) {
                return new int[][] {piece, possibleJumps[0]};
            }
        }
        Random randomGenerator = new Random();
        int[] selectedPiece = movablePieces[randomGenerator.nextInt(movablePieces.length)];
        int y = selectedPiece[0];
        int x = selectedPiece[1];
        int[] rightMove = new int[] {y + 1, x + 1};
        int[] leftMove = new int[] {y + 1, x - 1};
        if (game.canMoveHere(rightMove)) {
            return new int[][] {selectedPiece, rightMove};
        } else if (game.canMoveHere(leftMove)) {
            return new int[][] {selectedPiece, leftMove};
        }
        return null;
    }

    /**
     * Converts the move coordinates from the index format to a string representation.
     * @param index a 2D array representing the move coordinates in index format.
     * @return a string representing the move command.
     */
    public String indexConversion(int[][] index) {
        String outputString = "";
        HashMap<Integer, Character> hashmap = new HashMap<>();
        hashmap.put(0, 'a');
        hashmap.put(1, 'b');
        hashmap.put(2, 'c');
        hashmap.put(3, 'd');
        hashmap.put(4, 'e');
        hashmap.put(5, 'f');
        hashmap.put(6, 'g');
        hashmap.put(7, 'h');
        String l1 = String.valueOf(8 - index[0][0]);
        String l2 = "" + hashmap.get(index[0][1]);
        String l3 = String.valueOf(8 - index[1][0]);
        String l4 = "" + hashmap.get(index[1][1]);
        String output = l1 + l2 + "-" + l3 + l4;
        return output;
    }

}
