package core;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * The CheckersLogic class represents the logic and game rules for a game of checkers.
 * It manages the state of the game board, players, and their turns.
 * @author Samuel White
 */
public class CheckersLogic {
    Player player1;
    Player player2;
    Player currentPlayersTurn;

    /**
     * Constructs a CheckersLogic object and initializes the game with the standard setup.
     */
    public CheckersLogic(String gameSetup) {
        if (gameSetup.equals("PvP")) {
            standardGameSetup();
        } else {
            computerGameSetup();
        }
    }


    private char[][] boardState = {{'_', 'o', '_', 'o', '_', 'o', '_', 'o'},
            {'o', '_', 'o', '_', 'o', '_', 'o', '_'},
            {'_', 'o', '_', 'o', '_', 'o', '_', 'o'},
            {'_', '_', '_', '_', '_', '_', '_', '_'},
            {'_', '_', '_', '_', '_', '_', '_', '_'},
            {'x', '_', 'x', '_', 'x', '_', 'x', '_'},
            {'_', 'x', '_', 'x', '_', 'x', '_', 'x'},
            {'x', '_', 'x', '_', 'x', '_', 'x', '_'}};






    /**
     * Sets up the standard game configuration with two players and their initial positions.
     * This can be modified later to add more options for game setup or test individual cases.
     */
    public void standardGameSetup() {
        this.player1 = new Player('x');
        this.player2 = new Player('o');
        this.player1.setOtherPlayer(this.player2);
        this.player2.setOtherPlayer(this.player1);
        this.currentPlayersTurn = this.player1;
    }

    /**
     * Sets up the player vs computer game configuration with one player and their initial position.
     * It incorporates a new Computer class as player 2.
     */
    public void computerGameSetup() {
        this.player1 = new Player('x');
        this.player2 = new Computer('o', this);
        this.player1.setOtherPlayer(this.player2);
        this.player2.setOtherPlayer(this.player1);
        this.currentPlayersTurn = this.player1;
    }

    /**
     * Retrieves the current state of the game board.
     *
     * @return The game board state as a 2D char array.
     */
    public char[][] getBoardState() {
        return this.boardState;
    }

    /**
     * Sets the value at a specific location on the game board.
     *
     * @param location The coordinates of the location to set (row, column).
     * @param value The character value to set at the specified location.
     */
    public void setBoardState(int[] location, char value) {
        int row = location[0];
        int column = location[1];
        this.boardState[row][column] = value;
    }

    /**
     * Retrieves the player whose turn is currently active.
     *
     * @return The Player object representing the current player.
     */
    public Player getCurrentPlayersTurn() {
        return this.currentPlayersTurn;
    }

    /**
     * Makes a move in the game based on the provided move string.
     * The move string should be in the format "a1-b2", where a1 is the piece's position and b2 is the destination.
     *
     * @param move The move string representing the piece's current position and the desired destination.
     * @return True if the move was successful, false otherwise.
     */
    public boolean makeMove(String move) throws IllegalArgumentException{
        if (move.length() != 5) throw new IllegalArgumentException();

        int[][] indices = convertToIndices(move);
        if (indices == null) throw new IllegalArgumentException();
        int[] piece = indices[0];
        int[] destination = indices[1];
        if (!moveCommandIsValid(indices)) throw new IllegalArgumentException();

        movePiece(piece, destination);

        if (Math.abs(destination[0] - piece[0]) > 1) {
            jump(piece, destination);
            int[][] jumpOptions = getPossibleJumps(destination);
            doubleJump(destination, jumpOptions);
        }

        return true;
    }

    /**
     * Swaps the turn between the current player and the opponent.
     */
    public void swapTurn() {
        this.currentPlayersTurn = this.currentPlayersTurn.getOtherPlayer();
    }

    /**
     * Checks if the current player has won the game.
     *
     * @return true if the current player has won, false otherwise.
     */
    public boolean checkWin() {
        return !this.canMove(currentPlayersTurn.getOtherPlayer()) || currentPlayersTurn.getOtherPlayer().getPieceCount() == 0;
    }

    /**
     * Checks if the current player can make a valid move at all. If there are no available moves, the player has lost the game.
     *
     * @param currentPlayer the current player.
     * @return true if the player can make a valid move at all, false otherwise.
     */
    public boolean canMove(Player currentPlayer) {
        char playerIcon = currentPlayer.getPlayerIcon();
        char enemyIcon = currentPlayer.getOtherPlayer().getPlayerIcon();
        int direction;

        if (playerIcon == 'x') {
            direction = -1;
        } else {
            direction = 1;
        }

        int[] rightMove;
        int[] leftMove;
        int[] rightJump;
        int[] leftJump;

        for (int y = 0; y < this.boardState.length; y++) {
            for (int x = 0; x < this.boardState[0].length; x++) {
                if (this.getPiece(new int[] {y, x}) == playerIcon) {
                    rightMove = new int[] {y + direction, x + 1};
                    leftMove = new int[] {y + direction, x - 1};
                    if (canMoveHere(rightMove) || canMoveHere(leftMove)) {
                        return true;
                    }
                    if (withinBoard(rightMove) && this.getPiece(rightMove) == enemyIcon) {
                        rightJump = new int[]{rightMove[0] + direction, rightMove[1] + 1};
                        if (canMoveHere(rightJump)) {
                            return true;
                        }
                    }
                    else if (withinBoard(leftMove) && this.getPiece(leftMove) == enemyIcon) {
                        leftJump = new int[] {leftMove[0] + direction, leftMove[1] - 1};
                        if (canMoveHere(leftJump)) {
                            return true;
                        }
                    }
                }
            }
        }
    return false;

    }

    /**
     * Checks if a move to the specified location is valid.
     *
     * @param location the location to check.
     * @return true if the move is valid, false otherwise.
     */
    public boolean canMoveHere(int[] location) {
        return withinBoard(location) && spotIsOpen(location);
    }

    /**
     * Performs a double jump action with the specified destination and jump options.
     * If there is only one jump option available, the piece is moved and jumped automatically.
     * If there are two jump options available, the current player's turn is updated to allow for a double jump. And the user will be prompted which double jump they will play.
     *
     * @param destination   The destination coordinates for the piece after the jump.
     * @param jumpOptions   The available jump options for the piece.
     */
    public void doubleJump(int[] destination, int[][] jumpOptions) {
        int numJumpOptions = jumpOptions.length;

        if (numJumpOptions == 1) {
            movePiece(destination, jumpOptions[0]);
            jump(destination, jumpOptions[0]);
        } else if (numJumpOptions == 2) {
            currentPlayersTurn.goAgain = true;
            currentPlayersTurn.doubleJumpOptions = createJumpOptionsString(jumpOptions);
            currentPlayersTurn.doubleJumpLocations = jumpOptions;
            currentPlayersTurn.doubleJumpPiece = destination;
        }
    }

    /**
     * Selects the double jump option with the specified selection index.
     * Moves the piece and performs the jump accordingly.
     * Resets the current player's turn for double jump actions.
     *
     * @param selection The index of the selected double jump option.
     */
    public void selectDoubleOption(int selection) throws IndexOutOfBoundsException{
        try {
            int[] selectedDoubleJump = currentPlayersTurn.doubleJumpLocations[selection - 1];
            int[] pieceLocation = currentPlayersTurn.doubleJumpPiece;
            movePiece(pieceLocation, selectedDoubleJump);
            jump(pieceLocation, selectedDoubleJump);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException();
        }
        currentPlayersTurn.goAgain = false;
        currentPlayersTurn.doubleJumpOptions = null;
        currentPlayersTurn.doubleJumpLocations = null;
        currentPlayersTurn.doubleJumpPiece = null;
    }

    /**
     * Creates a string representation of the jump options.
     *
     * @param jumpOptions The available jump options for the piece.
     * @return The formatted string representation of the jump options.
     */
    public String createJumpOptionsString(int[][] jumpOptions) {
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

        int count = 1;
        for (int[] index : jumpOptions) {
            outputString += "Option " + count + ": " + (8 - index[0]) + hashmap.get(index[1]) + "\n";
            count++;
        }

        return outputString;
    }

    /**
     * Performs a jump action from the given piece location to the specified destination.
     * Removes the captured piece based on the direction of the jump.
     * Removes the piece of the opponent player.
     *
     * @param piece      The current location of the piece.
     * @param destination The destination coordinates for the piece after the jump.
     */
    public void jump(int[] piece, int[] destination) {
        int direction;
        if (this.currentPlayersTurn.getPlayerIcon() == 'x') {
            direction = -1;
        } else {
            direction = 1;
        }
        // had my indices swapped so I was creating an inverted jump
        // if pieces position - destinations position > 0 it must have moved to the right
        if (piece[1] - destination[1] < 0) {
            int newY = piece[0] + direction;
            int newX = piece[1] + 1;
            removePiece(new int[]{newY, newX});
        } else {
            int newY = piece[0] + direction;
            int newX = piece[1] - 1;
            removePiece(new int[]{newY, newX});
        }
        this.currentPlayersTurn.getOtherPlayer().removePiece();
    }


    /**
     * Moves a game piece from the given piece location to the specified destination.
     * Updates the board state by setting the destination cell to the piece's icon
     * and removing the piece from its original location.
     *
     * @param piece       The current location of the game piece.
     * @param destination The desired destination location for the game piece.
     */
    public void movePiece(int[] piece, int[] destination) {
        char pieceIcon = this.currentPlayersTurn.getPlayerIcon();
        setBoardState(destination, pieceIcon);
        removePiece(piece);
    }

    /**
     * Removes a game piece from the board by setting the specified index to an empty cell.
     *
     * @param index The location of the piece to be removed.
     */
    public void removePiece(int[] index) {
        setBoardState(index, '_');
    }


    /**
     * Checks if a given move command is valid.
     * A move command is considered valid if the specified indices are not null and
     * the move is legal according to the game rules.
     *
     * @param indices The indices representing the piece and its destination.
     * @return {@code true} if the move command is valid, {@code false} otherwise.
     */
    public boolean moveCommandIsValid(int[][] indices) {

        if (indices == null) {
            return false;
        }
        int[] piece = indices[0];
        int[] location = indices[1];

        if (!moveIsLegal(piece, location)) {
            return false;
        }

        return true;
    }

    /**
     * Converts a move string into indices representing the piece and its destination on the board.
     * The move string should follow the format "xy-uv", where (x, y) represents the piece's current location,
     * and (u, v) represents the destination location.
     *
     * @param move The move string to be converted.
     * @return An array of two arrays representing the piece and destination indices, or {@code null}
     * if the move string is invalid or contains characters not found in the hashmap.
     * @throws NullPointerException If the move string is null.
     */
    public int[][] convertToIndices(String move) throws NullPointerException {
        HashMap<Character, Integer> hashmap = new HashMap<>();
        hashmap.put('a', 0);
        hashmap.put('b', 1);
        hashmap.put('c', 2);
        hashmap.put('d', 3);
        hashmap.put('e', 4);
        hashmap.put('f', 5);
        hashmap.put('g', 6);
        hashmap.put('h', 7);

        int pieceRow = 8 - Character.getNumericValue(move.charAt(0));
        int pieceColumn;
        int locationRow = 8 - Character.getNumericValue(move.charAt(3));
        int locationColumn;

        try {
            pieceColumn = hashmap.get(move.charAt(1));
            locationColumn = hashmap.get(move.charAt(4));
        } catch (NullPointerException e) {
            System.out.println("Invalid move: Character not found in board coordinates.");
            return null;
        }

        return new int[][]{{pieceRow, pieceColumn}, {locationRow, locationColumn}};
    }

    /**
     * Checks if a move from the current piece location to the specified location is legal.
     * A move is considered legal if it satisfies the following conditions:
     * - The current player's piece is located at the specified piece location.
     * - The piece and destination locations are within the bounds of the board.
     * - The destination location is not the same as the piece location.
     * - The destination location is an empty cell.
     * - The move direction is correct for the current player's piece (forward direction).
     * - The move is either a single diagonal move or a jump move to a valid location.
     *
     * @param piece    The current location of the game piece.
     * @param location The desired destination location for the game piece.
     * @return {@code true} if the move is legal, {@code false} otherwise.
     */
    public boolean moveIsLegal(int[] piece, int[] location) {
        if (currentPlayersTurn.getPlayerIcon() != getBoardState()[piece[0]][piece[1]]) return false;


        if (!withinBoard(piece)) return false;
        if (!withinBoard(location)) return false;

        if (Arrays.equals(piece, location)) return false;

        if (!(spotIsOpen(location))) return false;

        if (currentPlayersTurn.getPlayerIcon() == 'x' && !(location[0] - piece[0] <= -1)) {
            return false;
        } else if (currentPlayersTurn.getPlayerIcon() == 'o' && !(location[0] - piece[0] >= 1)) {
            return false;
        }

        if (Math.abs(piece[0] - location[0]) == 1 && Math.abs(piece[1] - location[1]) == 1) {
            return true;
        }

        int[][] possibleJumps = getPossibleJumps(piece);

        return arrayInside2dArray(location, possibleJumps);

    }

    /**
     * Checks if the target array is present inside the given two-dimensional array.
     *
     * @param targetArray The target array to be searched.
     * @param twoDArray   The two-dimensional array to search within.
     * @return {@code true} if the target array is found, {@code false} otherwise.
     */
    public boolean arrayInside2dArray(int[] targetArray, int[][] twoDArray) {
        for (int[] array : twoDArray) {
            if (Arrays.equals(array, targetArray)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Retrieves the possible jump moves for a given piece.
     * The piece is specified by its location on the board.
     *
     * @param piece The current location of the game piece.
     * @return A two-dimensional array representing the possible jump moves for the piece.
     */
    public int[][] getPossibleJumps(int[] piece) {
        int y = piece[0];
        int x = piece[1];
        char playerPiece = currentPlayersTurn.getPlayerIcon();
        ArrayList<int[]> list = new ArrayList<>();
        int direction;

        if (playerPiece == 'x') {
            direction = -1;
        } else {
            direction = 1;
        }

        int[] rightPiece = {y + direction, x + 1};
        int[] leftPiece = {y + direction, x - 1};
        int[] rightJump = {y + (2 * direction), x + 2};
        int[] leftJump = {y + (2 * direction), x - 2};

        if (withinBoard(rightJump) &&
                getPiece(rightPiece) != playerPiece &&
                getPiece(rightPiece) != '_' &&
                spotIsOpen(rightJump)) list.add(rightJump);

        if (withinBoard(leftJump) &&
                getPiece(leftPiece) != playerPiece &&
                getPiece(leftPiece) != '_' &&
                spotIsOpen(leftJump)) list.add(leftJump);

        return convertToArray(list);
    }


    /**
     * Converts an ArrayList of integer arrays to a two-dimensional integer array.
     *
     * @param list The ArrayList of integer arrays to be converted.
     * @return A two-dimensional integer array containing the elements from the ArrayList.
     */
    public int[][] convertToArray(ArrayList<int[]> list) {
        int[][] array = new int[list.size()][2];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    /**
     * Retrieves the game piece at the specified index on the board.
     *
     * @param index The index representing the location of the game piece.
     * @return The character representing the game piece at the specified index.
     */
    public char getPiece(int[] index) {
        return getBoardState()[index[0]][index[1]];
    }

    /**
     * Checks if the specified location is within the bounds of the game board.
     *
     * @param location The location to be checked.
     * @return {@code true} if the location is within the board bounds, {@code false} otherwise.
     */
    public boolean withinBoard(int[] location) {
        int locationRow = location[0];
        int locationColumn = location[1];

        return locationRow >= 0 && locationRow < 8 && locationColumn >= 0 && locationColumn < 8;
    }

    /**
     * Checks if the specified spot on the board is open (empty).
     *
     * @param spot The spot on the board to be checked.
     * @return {@code true} if the spot is open, {@code false} otherwise.
     */
    public boolean spotIsOpen(int[] spot) {
        return getBoardState()[spot[0]][spot[1]] == '_';
    }
}


