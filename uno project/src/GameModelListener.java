/**
 *
 * Listener interface for GameModel events
 * Implements the observer pattern to notify views and controllers of game state changes
 * @author Nicky Fang 101304731
 *
 */

public interface GameModelListener {

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
     * called when turn advances to next
     * @param state current game state
     */
    public void onTurnAdvanced(GameState state);

    /**
     * called when player wins a round (empty hand)
     * @param winnerName name of player who won round
     * @param pointsAwarded the points awwarded to winner
     * @param state current game state
     */
    public void onRoundWon(String winnerName, int pointsAwarded, GameState state);

    /**
     * callled when player wins entire game (reached score)
     * @param winnerName name of player who won round
     * @param finalScore points awarded to winner
     * @param state final game state
     */
    public void onGameWon(String winnerName, int finalScore, GameState state);
}

