package core;

/**
 * The Player class represents a player in the game.
 * @author Samuel White
 */
public class Player {
    private final char playerIcon;
    private Player otherPlayer;
    public boolean goAgain = false;
    public String doubleJumpOptions;
    public int[][] doubleJumpLocations;
    public int[] doubleJumpPiece;

    private int pieceCount = 12;

    /**
     * Constructs a Player object with the specified player icon.
     *
     * @param playerIcon The character representing the player's icon.
     */
    public Player(char playerIcon) {
        this.playerIcon = playerIcon;
    }

    /**
     * Retrieves the player's icon.
     *
     * @return The character representing the player's icon.
     */
    public char getPlayerIcon() {
        return this.playerIcon;
    }

    /**
     * Sets the other player in the game.
     *
     * @param otherPlayer The other player object.
     */
    public void setOtherPlayer(Player otherPlayer) {
        this.otherPlayer = otherPlayer;
    }

    /**
     * Removes a game piece from the player.
     */
    public void removePiece() {
        this.pieceCount--;
    }

    /**
     * Retrieves the other player in the game.
     *
     * @return The other player object.
     */
    public Player getOtherPlayer() {
        return this.otherPlayer;
    }

    /**
     * Retrieves the count of remaining game pieces for the player.
     *
     * @return The count of remaining game pieces.
     */
    public int getPieceCount() {
        return this.pieceCount;
    }

}
