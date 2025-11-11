import model.Player;

/**
 *
 * Listener interface for model.GameModel events
 * Implements the observer pattern to notify views and controllers of game state changes
 * @author Bhagya Patel 101324150
 *
 */

public interface GameModelListener {

    /**
     * Called when the game model is initialized and ready to start.
     * This is fired after dealing cards and setting up the initial discard pile
     *
     * ex.
     * <pre>
     *     Public void onModelInit(GameState state){
     *         view.render(state);
     *         view.showMessage("Game started! " + state.currentPlayer.getName + "goes first");
     *     }
     * </pre>
     */
    public void onModelInit(GameState state);

    /**
     * Called when the game is initialized and ready to start
     * @param state initial game state
     */
    public void onGameInitialized(GameState state);


    /**
     * called when game state changes (played card, drawn, etc...)
     * @param state updated game state
     */
    public void onStateUpdated(GameState state);

    /**
     * Called when the turn advances to the next player.
     * This is fired after endTurn() is called or after special card effects that skip players.
     *
     * The current parameter provides direct access to the player whose turn it now is,
     * while the state parameter provides the complete game state.
     *
     * Example usage in controller:
     * <pre>
     * public void onTurnAdvanced(model.Player current, GameState state) {
     *     view.render(state);
     *     view.showMessage("It's now " + current.getName() + "'s turn");
     * }
     * </pre>
     *
     * @param current The player whose turn it now is
     * @param state The current game state
     */
    public void onTurnAdvanced(Player current, GameState state);

    /**
     * called when player wins a round (empty hand)
     * @param  winner The player who won round
     * @param pointsAwarded the points awarded to winner
     * @param state current game state
     */
    public void onRoundWon(Player winner, int pointsAwarded, GameState state);

    /**
     * callled when player wins entire game (reached score)
     * @param winner The player who won the game
     * @param state final game state
     */
    public void onGameWon(Player winner, GameState state);

    /**
     * Called when an error occurs during game operations.
     *
     * Common error scenarios:
     * - Invalid card plays (card not playable on current discard)
     * - Playing out of turn
     * - model.Deck exhaustion (no cards left to draw)
     * - Invalid card index
     *
     * Example usage in controller:
     * <pre>
     * public void onError(String message) {
     *     view.showMessage("Error: " + message);
     * }
     * </pre>
     *
     * @param message A descriptive error message
     */
    void onError(String message);
}

