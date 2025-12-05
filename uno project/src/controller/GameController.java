package controller;

import model.Card;
import model.GameModel;
import model.Player;
import view.GameView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Controller for uno game implementing MVC pattern with Save/Load functionality
 * Mediates between GameModel and GameView
 * Handles user input via ActionListener and updates the view via GameModelListener
 *
 * @author Nicky Fang 101304731
 * @author Bhagya Patel 101324150
 * @version 4.0 - Added Save/Load functionality for Milestone 4
 */
public class GameController implements ActionListener, GameModelListener {

    private GameModel model;
    private GameView view;
    private static final String SAVE_FILE = "uno_save.dat";

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
        model.addListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        try {
            if (command.startsWith("PLAY:")) {
                int cardIndex = Integer.parseInt(command.substring(5));
                onPlayCard(cardIndex);
            }
            else if (command.equals("DRAW")) {
                onDrawCard();
            }
            else if (command.equals("NEXT")) {
                onEndTurn();
            }
            else if (command.equals("UNDO")) {
                onUndo();
            }
            else if (command.equals("REDO")) {
                onRedo();
            }
            else if (command.equals("SAVE")) {
                onSaveGame();
            }
            else if (command.equals("LOAD")) {
                onLoadGame();
            }
            else {
                view.showMessage("Unknown command: " + command);
            }
        }
        catch (Exception ex) {
            view.showMessage("Error: " + ex.getMessage());
        }
    }

    private void onPlayCard(int cardIndex) {
        GameState gameState = model.getState();
        Player currentPlayer = gameState.currentPlayer;

        if (cardIndex < 0 || cardIndex >= currentPlayer.getHandSize()) {
            view.showMessage("Invalid card index: " + cardIndex);
            return;
        }

        if (!gameState.playableIndices.contains(cardIndex)) {
            Card card = currentPlayer.getHand().get(cardIndex);
            view.showMessage("Cannot play " + card + " on " + gameState.topDiscard);
            return;
        }

        Card playedCard = currentPlayer.getHand().get(cardIndex);

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
                model.playCard(currentPlayer, cardIndex, chosenColor);
            }
        }
        else {
            model.playCard(currentPlayer, cardIndex, null);
        }
    }

    private void onDrawCard() {
        model.drawCard();
    }

    private void onEndTurn() {
        model.endTurn();
    }

    private void onUndo() {
        model.undo();
    }

    private void onRedo() {
        model.redo();
    }

    /**
     * Handles saving the game state to a file
     */
    private void onSaveGame() {
        try {
            model.saveGame(SAVE_FILE);
            view.showMessage("Game saved successfully!");
        } catch (IOException ex) {
            view.showMessage("Failed to save game: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Handles loading a saved game from a file
     */
    private void onLoadGame() {
        try {
            // Remove this controller from old model
            model.removeListener(this);

            // Load the saved model
            this.model = GameModel.loadGame(SAVE_FILE);

            // Add this controller to the new model
            model.addListener(this);

            // Update the view
            view.render(model.getState());
            view.showMessage("Game loaded successfully!");

            // Check if it's an AI player's turn
            if (model.getState().currentPlayer.isAI()) {
                SwingUtilities.invokeLater(() -> {
                    model.checkAndProcessAITurn();
                });
            }
        } catch (IOException ex) {
            view.showMessage("Failed to load game: " + ex.getMessage());
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            view.showMessage("Invalid save file format");
            ex.printStackTrace();
        }
    }

    /**
     * Returns the current model (useful after loading)
     */
    public GameModel getModel() {
        return model;
    }

    @Override
    public void onGameInitialized(GameState state) {
        view.render(state);
        view.showMessage("Game started! " + state.currentPlayer.getName() + " goes first.");
        if (state.currentPlayer.isAI()) {
            SwingUtilities.invokeLater(() -> {
                model.checkAndProcessAITurn();
            });
        }
    }

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

    @Override
    public void onStateUpdated(GameState state) {
        view.render(state);
        view.setUndoEnabled(model.canUndo());
        view.setRedoEnabled(model.canRedo());
    }

    @Override
    public void onTurnAdvanced(Player current, GameState state) {
        view.render(state);

        if (current.isAI()) {
            SwingUtilities.invokeLater(() -> {
                model.checkAndProcessAITurn();
            });
        }
    }

    @Override
    public void onRoundWon(Player winner, int pointsAwarded, GameState state) {
        view.render(state);
        view.showMessage(winner + " wins the round and scores " + pointsAwarded + " points!");
    }

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

    @Override
    public void onError(String message) {
        view.showMessage("Error: " + message);
    }
}