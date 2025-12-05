package controller;

import model.Card;
import model.GameModel;
import model.Player;
import view.GameView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * COntroller for uno game implementing MVC pattern
 * Mediates between model.GameModel and view.GameView
 * Handles user input via ActionListener and updates the view via controller.GameModelListener
 *
 * @author Nicky Fang 101304731
 *
 * @author Bhagya Patel 101324150
 * @version 3.0
 * @brief added methods to support ai players
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
        //view.bindController(this);
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
            } else if (command.equals("UNDO")) {
                onUndo();
            } else if (command.equals("REDO")) {
                onRedo();
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
        GameState gameState = model.getState();
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
            Card.Color chosenColor = null;
            if (playedCard.getCurrentSide() == Card.Side.DARK) {
                chosenColor = view.promptDarkWildColor();
            } else {
                chosenColor = view.promptWildColor();
            }
            if (chosenColor == null) {
                view.showMessage("A color is required");
            } else {
                model.playCard(currentPlayer,cardIndex, chosenColor);
            }
        }
        else {
            model.playCard(currentPlayer,cardIndex, null);
        }
    }

    //handles drawing card from deck
    private void onDrawCard()
    {
        model.drawCard();
    }

    //Handles ending current player turn and advances to next player.
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
        //if first player is an ai
        if (state.currentPlayer.isAI()) {
            SwingUtilities.invokeLater(() -> {
                model.checkAndProcessAITurn();
            });
        }
    }

    /**
     * Called when the game is initialized.
     * Refreshes the view with the initial state and shows a welcome message.
     *
     * @param state The initial game state
     */
    @Override
    public void onModelInit(GameState state) {
        view.render(state);
        view.showMessage("Game started! " + state.currentPlayer.getName() + " goes first.");

        if (state.currentPlayer.isAI()) {
            SwingUtilities.invokeLater(() -> {
                model.checkAndProcessAITurn();
            });
        }
    }


    /**
     * Called whenever the game state changes
     * Refreshes the view to show current state
     */
    @Override
    public void onStateUpdated(GameState state) {
        view.render(state);
        view.setUndoEnabled(model.canUndo());
        view.setRedoEnabled(model.canRedo());
    }

    /**
     * Called when the turn advances to next player
     */
    @Override
    public void onTurnAdvanced(Player current, GameState state) {
        view.render(state);

        if (current.isAI()) {
            SwingUtilities.invokeLater(() -> {
                model.checkAndProcessAITurn();
            });
        }

    }

    public void onUndo() {
        model.undo();
    }

    public void onRedo() {
        model.redo();
    }
    /**
     * Called when a player wins round
     */
    @Override
    public void onRoundWon(Player winner, int pointsAwarded, GameState state) {
        view.render(state);
        view.showMessage(winner + " wins the round and scores " + pointsAwarded + " points!");
    }

    /**
     * called when a player wins game
     */
    @Override
    public void onGameWon(Player winner, GameState state) {
        view.render(state);

        // ask to prompt user to play again after game won
        boolean playAgain = view.promptPlayAgain(winner);
        if (playAgain) {
            model.restartGame();
        }
        else {
            System.exit(0);
        }
        view.showMessage(" 🎊 " + winner + "  WINS THE GAME WITH " + winner.getScore() + " POINTS! 🎊 ");
    }

    /**
     * Called when an error occurs in the game model.
     * Displays the error message to the user.
     *
     * @param message The error message
     */
    @Override
    public void onError(String message) {
        view.showMessage("Error: " + message);
    }

}
