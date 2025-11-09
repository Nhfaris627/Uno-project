/**
 *
 * Listener interface for GameModel events
 * Implements the observer pattern to notify views and controllers of game state changes
 * @author Nicky Fang 101304731
 *
 */

public interface GameModelListener {
    public void onGameInitialized(GameState state);

    public void onStateUpdated(GameState state);

    public void onTurnAdvanced(GameState state);

    public void onRoundWon(String winnerName, int pointsAwarded, GameState state);

    public void onGameWon(String winnerName, int finalScore, GameState state);

    public void onError(String errorMessage);
}

