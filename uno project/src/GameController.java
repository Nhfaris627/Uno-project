import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * COntroller for uno game implementing MVC pattern
 * Mediates between GameModel and GameView
 * Handles user input via ActionListener and updates the view via GameModelListener
 *
 * @author Nicky Fang 101304731
 *
 */
public class GameController implements ActionListener, GameModelListener {

    //
    private GameModel model;
    private GameView view;

    /**
     *
     * @param model The game model
     * @param view The game view
     */
    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;

        //add this controller to the model

        model.addListener(this);

        //add this controller to the view
        view.bindController(this);
    }

    /**
     * Handles button clicks and other actions from the view
     * Sends commands to appropriate handler method
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        try {
            if (command.startsWith("PLAY:"))
            {
                //extract card index from command
                int cardIndex = Integer.parseInt(command.substring(5));
                onPlayCard(cardIndex);
            }
            else if (command.equals("DRAW"))
            {
                onDrawCard();
            }
            else if (command.equals("NEXT"))
            {
                onEndTurn();
            }
            //this is only used during development in case a new command is added but not implemented here
            else
            {
                view.showMessage("Unknown command: " + command);
            }
        }

        catch (Exception ex) {
            view.showMessage("Error: " + ex.getMessage());
        }
    }

    /**
     * Handles playing card from current hand
     * verifies card index before sending to model
     * @param cardIndex
     */
    private void onPlayCard(int cardIndex) {

        //get game state
        GameState gameState = model.getGameState();
        Player currentPlayer = gameState.currentPlayer;

        //check card index
        if (cardIndex < 0 || cardIndex >= currentPlayer.getHandSize()) {
            view.showMessage("Invalid card index: " + cardIndex);
            return;
        }

        //check if car d playable
        if (!gameState.playableIndices.contains(cardIndex)) {
            Card card = currentPlayer.getHand().get(cardIndex);
            view.showMessage("Cannot play " + card + " on " + gameState.topDiscard);
            return;
        }

        //get current card player will play
        Card playedCard = currentPlayer.getHand().get(cardIndex);

        // check if its wild because if it is we need to prompt for color
        if (playedCard.getColor() == Card.Color.WILD) {
            Card.Color chosenColor = view.promptWildColor();
            model.playCard(cardIndex, chosenColor);
        }
        else {
            model.playCard(cardIndex, null);

        }
    }

    //handles drawing card from deck
    private void onDrawCard()
    {
        model.drawCard();
    }

    //Handles ending current player turn and advnaces to next player.
    private void onEndTurn()
    {
        model.endTurn();
    }

    /**
     * Called when the game initialized
     * refreshes the view with initial state.
     */
    @Override
    public void onGameInitialized(GameState state) {
        view.render(state);
        view.showMessage("Game started! " + state.currentPlayer.getName() + " goes first.");
    }

    /**
     * Called whenever the game state changes
     * Refreshes the view to show current state
     */
    @Override
    public void onStateUpdated(GameState state) {
        view.render(state);
    }

    /**
     * Called when the turn advances to next player
     */
    @Override
    public void onTurnAdvanced(GameState state) {
        view.render(state);
    }

    /**
     * Called when a player wins round
     */
    @Override
    public void onRoundWon(String winnerName, int pointsAwarded, GameState state) {
        view.render(state);
        view.showMessage(winnerName + " wins the round and scores " + pointsAwarded + " points!");
    }

    /**
     * called when a player wins game
     */
    @Override
    public void onGameWon(String winnerName, int finalScore, GameState state) {
        view.render(state);
        view.showMessage(" 🎊 " + winnerName + "  WINS THE GAME WITH " + finalScore + " POINTS! 🎊 ");
    }
}
