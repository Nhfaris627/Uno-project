package model;

import java.util.*;
import controller.GameModelListener;
import controller.GameState;

/**
 * Core model for the UNO game
 * manages game state, rules, and notifies listeners of changes.
 * this class has no UI or I/O dependencies, all interactions happen through
 * the controller.GameModelListener observer pattern
 * 
 * @author Bhagya Patel, 101324150
 * @author Faris Hassan, 101300683
 * @version 2.0
 */

public class GameModel {

    private List<Player> players;
    private List<Card> discardPile;
    private Deck deck;
    private int currentPlayerIndex;
    private boolean isClockwise;
    private List<GameModelListener> listeners;
    private static final int TARGET_SCORE = 500;
    private static final int INITIAL_HAND_SIZE = 7;
    private boolean currentTurnTaken = false;
    private Card.Side currentSide = Card.Side.LIGHT;  // Track which side is active

    /**
     * Creates a new model.GameModel with specified player names.
     * initializes the deck, discard pile. and sets up the game state.
     *
     * @param playerCount List of player names (must be 2-4 players)
     * @throws IllegalArgumentException if player count is not between 2 and 4
     */
    public GameModel(int playerCount) {
        if (playerCount == 0 || playerCount < 2 || playerCount > 4) {
            throw new IllegalArgumentException("Game requires 2-4 players");
        }

        this.players = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            Player player = new Player("Player " + (i + 1));
            this.players.add(player);
        }

        this.deck = new Deck();
        this.discardPile = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.isClockwise = true;
        this.listeners = new ArrayList<>();
    }

    /**
     * Starts a new game by dealing cards and setting up the initial discard pile.
     * Fires onGameInitialized event to all listeners
     */
    public void startGame() {
        // Deal initial cards to each player
        for (Player player : players) {
            for (int i = 0; i < INITIAL_HAND_SIZE; i++) {
                Card card = deck.drawCard();
                if (card != null) {
                    player.drawCard(card);
                }
            }
        }

        // Place first card on discard pile (avoid starting with special cards)
        Card firstCard = deck.drawCard();
        while (firstCard != null && isSpecialCard(firstCard)) {
            deck.drawCard(); // Put it back somehow, for simplicity just draw another
            firstCard = deck.drawCard();
        }

        if (firstCard != null) {
            discardPile.add(firstCard);
        }

        fireModelInit();
    }

    /**
     * Plays a card from the current player's hand.
     * Handles wild card color selection and special card effects.
     *
     * @param player      The player playing the card (unused in this simplified version)
     * @param handIndex   Index of the card in the current player's hand
     * @param chosenColor Color chosen for wild cards (null for non-wild cards)
     */
    public void playCard(Player player, int handIndex, Card.Color chosenColor) {
        Player currentPlayer = players.get(currentPlayerIndex);

        if (handIndex < 0 || handIndex >= currentPlayer.getHandSize()) {
            fireError("Invalid card index: " + handIndex);
            return;
        }

        Card playedCard = currentPlayer.getHand().get(handIndex);

        // Validate the card can be played
        if (!isCardPlayable(playedCard)) {
            fireError("Cannot play " + playedCard + " on " + getTopDiscardCard());
            return;
        }

        // Remove card from hand and add to discard pile
        currentPlayer.getHand().remove(handIndex);
        discardPile.add(playedCard);

        // Handle wild card color choice
        if (playedCard.getColor() == Card.Color.WILD && chosenColor != null) {
            playedCard.setColor(chosenColor);
        }

        // Handle Wild Draw Color
        if (playedCard.getValue() == Card.Value.WILD_DRAW_COLOR && chosenColor != null) {
            playedCard.setColor(chosenColor);
        }

        fireStateUpdated();

        // Check for round winner
        if (currentPlayer.getHandSize() == 0) {
            handleRoundWin(currentPlayerIndex);
            return;
        }

        // Handle special card effects
        handleSpecialCard(playedCard);
        currentTurnTaken = true;
        fireStateUpdated();
    }

    /**
     * Current player draws a card from the deck
     *
     * @return The drawn card, or null if deck is empty
     */
    public Card drawCard() {
        Player currentPlayer = players.get(currentPlayerIndex);

        Card drawnCard = deck.drawCard();

        if (drawnCard != null) {
            currentPlayer.drawCard(drawnCard);
            currentTurnTaken = !isCardPlayable(drawnCard);
            fireStateUpdated();
            return drawnCard;
        } else {
            fireError("model.Deck is empty!");
            return null;
        }
    }

    /**
     * Ends the current player's turn and advances to the next player.
     */
    public void endTurn() {
        advanceToNextPlayer();
        currentTurnTaken = false;
        fireTurnAdvanced(players.get(currentPlayerIndex));
    }

    /**
     * Calculates and awards points when a player wins a round.
     * The winner gets points equal to the sum of all cards in opponents' hands.
     *
     * @param winnerIndex The index of the winning player
     * @return The total points awarded
     */
    public int calculateRoundScore(int winnerIndex) {
        Player winner = players.get(winnerIndex);
        int totalPoints = 0;

        // Calculate points from all opponents' hands
        for (int i = 0; i < players.size(); i++) {
            if (i != winnerIndex) {
                Player opponent = players.get(i);
                int handValue = opponent.calculateHandValue();
                totalPoints += handValue;
            }
        }

        winner.addScore(totalPoints);
        return totalPoints;
    }

    /**
     * Checks if any player has reached the target score to win the game.
     *
     * @param targetScore The score needed to win the game (typically 500)
     * @return The winning player, or null if no winner yet
     */
    public Player checkForGameWinner(int targetScore) {
        for (Player player : players) {
            if (player.getScore() >= targetScore) {
                return player;
            }
        }
        return null;
    }

    /**
     * Adds a listener to receive game events.
     *
     * @param l The listener to add
     */
    public void addListener(GameModelListener l) {
        if (l != null && !listeners.contains(l)) {
            listeners.add(l);
        }
    }

    /**
     * Removes a listener from receiving game events.
     *
     * @param l The listener to remove
     */
    public void removeListener(GameModelListener l) {

        listeners.remove(l);
    }

    //HELPER METHODS

    /**
     * Gets the top card of the discard pile.
     *
     * @return The top card, or null if discard pile is empty
     */
    private Card getTopDiscardCard() {
        if (discardPile.isEmpty()) {
            return null;
        }
        return discardPile.get(discardPile.size() - 1);
    }

    /**
     * Gets indices of playable cards in current player's hand.
     *
     * @return List of indices representing playable cards
     */
    private List<Integer> getPlayableIndices() {
        Player currentPlayer = players.get(currentPlayerIndex);
        List<Integer> playableIndices = new ArrayList<>();
        Card topCard = getTopDiscardCard();

        if (topCard == null) {
            // All cards playable if no top card
            for (int i = 0; i < currentPlayer.getHandSize(); i++) {
                playableIndices.add(i);
            }
            return playableIndices;
        }

        for (int i = 0; i < currentPlayer.getHandSize(); i++) {
            Card card = currentPlayer.getHand().get(i);
            if (isCardPlayable(card)) {
                playableIndices.add(i);
            }
        }

        return playableIndices;
    }

    /**
     * Checks if a card can be played on the current discard pile.
     *
     * @param card The card to check
     * @return true if the card is playable, false otherwise
     */
    private boolean isCardPlayable(Card card) {
        Card topCard = getTopDiscardCard();

        if (topCard == null) {
            return true;
        }

        // Wild cards can always be played
        if (card.getColor() == Card.Color.WILD) {
            return true;
        }

        // Same color
        if (card.getColor() == topCard.getColor()) {
            return true;
        }

        // Same value
        if (card.getValue() == topCard.getValue()) {
            return true;
        }

        return false;
    }

    /**
     * Checks if a card is a special action card.
     *
     * @param card The card to check
     * @return true if the card is special, false otherwise
     */
    private boolean isSpecialCard(Card card) {
        Card.Value value = card.getValue();
        return value == Card.Value.SKIP ||
                value == Card.Value.REVERSE ||
                value == Card.Value.DRAW_ONE ||
                value == Card.Value.WILD ||
                value == Card.Value.WILD_DRAW_TWO ||
                value == Card.Value.FLIP ||
                value == Card.Value.DRAW_FIVE ||
                value == Card.Value.SKIP_EVERYONE ||
                value == Card.Value.WILD_DRAW_COLOR;
    }

    /**
     * Handles the effects of special cards.
     *
     * @param playedCard The special card that was played
     */
    private void handleSpecialCard(Card playedCard) {
        switch (playedCard.getValue()) {
            case SKIP:
                advanceToNextPlayer(); // Skip next player
                break;

            case REVERSE:
                isClockwise = !isClockwise;
                if (players.size() == 2) {
                    // In 2-player game, reverse acts like skip
                    advanceToNextPlayer();
                }
                break;

            case DRAW_ONE:
                advanceToNextPlayer();
                Player drawOneTarget = players.get(currentPlayerIndex);
                Card drawn = deck.drawCard();
                if (drawn != null) {
                    drawOneTarget.drawCard(drawn);
                }
                break;

            case WILD_DRAW_TWO:
                advanceToNextPlayer();
                Player drawTwoTarget = players.get(currentPlayerIndex);
                for (int i = 0; i < 2; i++) {
                    Card drawnCard = deck.drawCard();
                    if (drawnCard != null) {
                        drawTwoTarget.drawCard(drawnCard);
                    }
                }
                break;

            case WILD:
                // Color already set in playCard method
                break;

            case FLIP:
                handleFlipCard();
                break;

            case DRAW_FIVE:
                handleDrawFive();
                break;

            case SKIP_EVERYONE:
                handleSkipEveryone();
                break;

            case WILD_DRAW_COLOR:
                handleWildDrawColor(playedCard);
                break;

            default:
                // Regular number card, no special effect
                break;
        }
    }

    /**
     * Handles the FLIP card effect
     * Flips all cards in the game to their opposite side.
     */
    private void handleFlipCard() {
        currentSide = (currentSide == Card.Side.LIGHT) ? Card.Side.DARK : Card.Side.LIGHT;

        // Loop through all cards in all players' hands
        for (Player player : players) {
            player.flipHand();
        }

        // Flip all cards in discard pile
        for (Card card : discardPile) {
            card.flip();
        }

        // Flip all cards in deck
        deck.flipAllCards();

        fireStateUpdated();
    }

    /**
     * Handles the DRAW FIVE card effect.
     * Next player draws 5 cards and skips their turn.
     */
    private void handleDrawFive() {
        advanceToNextPlayer();
        Player target = players.get(currentPlayerIndex);

        // Draw 5 cards
        for (int i = 0; i < 5; i++) {
            Card drawnCard = deck.drawCard();
            if (drawnCard != null) {
                target.drawCard(drawnCard);
            } else {
                fireError("Deck exhausted during Draw Five");
                break;
            }
        }
        // Skip the target player's turn
        advanceToNextPlayer();
    }

    /**
     * Handles the SKIP EVERYONE card effect.
     * All other players are skipped, current player gets another turn.
     */
    private void handleSkipEveryone() {
        // Current player gets another turn, don't advance
        // Skips all other players
        // No action needed, turn stays with current player
    }

    /**
     * Handles the WILD DRAW COLOR card effect.
     * Next player draws cards until they get one of the chosen color.
     *
     * @param playedCard The Wild Draw Color card with chosen color
     */
    private void handleWildDrawColor(Card playedCard) {
        advanceToNextPlayer();
        Player target = players.get(currentPlayerIndex);
        Card.Color targetColor = playedCard.getColor();

        if (targetColor == Card.Color.WILD) {
            fireError("Wild Draw Color requires a color choice");
            return;
        }

        // Draw cards until getting the target color
        Card drawnCard;
        int cardsDrawn = 0;
        int maxCards = 20; // Safety limit to prevent infinite loop

        do {
            drawnCard = deck.drawCard();
            if (drawnCard != null) {
                target.drawCard(drawnCard);
                cardsDrawn++;
            } else {
                fireError("Deck exhausted during Wild Draw Color");
                break;
            }

            if (cardsDrawn >= maxCards) {
                fireError("Maximum cards drawn for Wild Draw Color");
                break;
            }

        } while (drawnCard != null && drawnCard.getColor() != targetColor);

        // Skip the target player's turn
        advanceToNextPlayer();
    }


    
    /**
     * Advances the turn to the next player based on direction.
     */
    private void advanceToNextPlayer() {
        if (isClockwise) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } else {
            currentPlayerIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
        }
    }
    
    /**
     * Handles round win scoring and checks for game winner.
     * 
     * @param winnerIndex Index of the player who won the round
     */
    private void handleRoundWin(int winnerIndex) {
        Player winner = players.get(winnerIndex);
        int points = calculateRoundScore(winnerIndex);
        
        fireRoundWon(winner, points);
        
        // Check for game winner
        Player gameWinner = checkForGameWinner(TARGET_SCORE);
        if (gameWinner != null) {
            fireGameWon(gameWinner);
        }

        newRound();
    }

    //EVENT FIRING METHODS
    
    /**
     * Notifies all listeners that the game model has been initialized.
     */
    private void fireModelInit() {
        GameState state = getState();
        for (GameModelListener listener : listeners) {
            listener.onModelInit(state);
        }
    }
    
    /**
     * Notifies all listeners that the game state has been updated.
     */
    private void fireStateUpdated() {
        GameState state = getState();
        for (GameModelListener listener : listeners) {
            listener.onStateUpdated(state);
        }
    }
    
    /**
     * Notifies all listeners that the turn has advanced to the next player.
     * 
     * @param current The player whose turn it now is
     */
    private void fireTurnAdvanced(Player current) {
        GameState state = getState();
        for (GameModelListener listener : listeners) {
            listener.onTurnAdvanced(current, state);
        }
    }
    
    /**
     * Notifies all listeners that a player has won a round.
     * 
     * @param winner The player who won the round
     * @param points Points awarded to the winner
     */
    private void fireRoundWon(Player winner, int points) {
        GameState state = getState();
        for (GameModelListener listener : listeners) {
            listener.onRoundWon(winner, points, state);
        }
    }
    
    /**
     * Notifies all listeners that a player has won the game.
     * 
     * @param winner The player who won the game
     */
    private void fireGameWon(Player winner) {
        GameState state = getState();
        for (GameModelListener listener : listeners) {
            listener.onGameWon(winner, state);
        }
    }
    
    /**
     * Notifies all listeners of an error condition.
     * 
     * @param msg Error message
     */
    private void fireError(String msg) {
        for (GameModelListener listener : listeners) {
            listener.onError(msg);
        }
    }



    public GameState getState()
    {
        GameState state = new GameState();
        state.players = new ArrayList<>(players);
        state.currentPlayer = players.get(currentPlayerIndex);
        state.topDiscard = getTopDiscardCard();
        state.deckSize = deck.size();
        state.playableIndices = getPlayableIndices();
        state.clockwise = isClockwise;
        state.turnTaken = currentTurnTaken;
        state.currentSide = currentSide;  // Include current side
        return state;
    }

    public List<GameModelListener> getListeners() {
        return listeners;
    }

    public void newRound() {
        // Clear all hands
        for (Player p : players) {
            p.getHand().clear();
        }

        // Reset deck and discard pile
        deck = new Deck();
        discardPile.clear();
        discardPile.add(deck.drawCard());  // new top card

        // Deal 7 cards to each player
        for (Player p : players) {
            for (int i = 0; i < 7; i++) {
                Card c = deck.drawCard();
                if (c != null) p.drawCard(c);
            }
        }

        // Reset turn state
        currentPlayerIndex = 0;  // or rotate starting player
        isClockwise = true;
        currentTurnTaken = false;
        currentSide = Card.Side.LIGHT;

        fireStateUpdated();
    }
}
