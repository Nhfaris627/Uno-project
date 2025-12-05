package model;

import java.io.*;
import java.util.*;
import controller.GameModelListener;
import controller.GameState;

/**
 * Core model for the UNO game with serialization support
 * manages game state, rules, and notifies listeners of changes.
 *
 * @author Bhagya Patel, 101324150
 * @author Faris Hassan, 101300683
 * @author Nicky Fang, 101304731
 * @version 4.0 - Added serialization support for Milestone 4
 */
public class GameModel {

    private List<Player> players;
    private List<Card> discardPile;
    private Deck deck;
    private int currentPlayerIndex;
    private boolean isClockwise;
    private transient List<GameModelListener> listeners; // Don't serialize listeners
    private static final int TARGET_SCORE = 500;
    private static final int INITIAL_HAND_SIZE = 7;
    private boolean currentTurnTaken = false;
    private Card.Side currentSide = Card.Side.LIGHT;
    private Stack<GameState> undoStack = new Stack<>();
    private Stack<GameState> redoStack = new Stack<>();

    /**
     * Saves the current game state to a file
     * @param filename The file path to save to
     * @throws IOException If file writing fails
     */
    public void saveGame(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            out.writeObject(this.getState());
            System.out.println("Game saved successfully to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Loads a game state from a file
     * @param filename The file path to load from
     * @return The loaded GameModel
     * @throws IOException If file reading fails
     * @throws ClassNotFoundException If deserialization fails
     */
    public static GameState loadGame(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(filename))) {
            GameState state = (GameState) in.readObject();
            System.out.println("Game loaded successfully from " + filename);
            return state;
        } catch (FileNotFoundException e) {
            System.err.println("Save file not found: " + filename);
            throw e;
        } catch (IOException e) {
            System.err.println("Error loading game: " + e.getMessage());
            throw e;
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid save file format");
            throw e;
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Number of players does not match");
            throw e;
        }
    }

    public GameModel(int playerCount) {
        this(playerCount, new boolean[playerCount], AIPlayer.DifficultyLevel.MEDIUM);
    }

    public GameModel(int playerCount, boolean[] isAI, AIPlayer.DifficultyLevel difficultyLevel) {
        if (playerCount == 0 || playerCount < 2 || playerCount > 4) {
            throw new IllegalArgumentException("Game requires 2-4 players");
        }

        if (isAI != null && isAI.length != playerCount) {
            throw new IllegalArgumentException("isAI array must match player count");
        }

        this.players = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            Player player;
            if (isAI != null && isAI[i]) {
                player = new AIPlayer("AI Player " + (i + 1), difficultyLevel);
            } else {
                player = new Player("Player " + (i + 1));
            }
            this.players.add(player);
        }

        this.deck = new Deck();
        this.discardPile = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.isClockwise = true;
        this.listeners = new ArrayList<>();
        this.undoStack = new Stack<GameState>();
        this.redoStack = new Stack<GameState>();
    }

    public void startGame() {
        for (Player player : players) {
            for (int i = 0; i < INITIAL_HAND_SIZE; i++) {
                Card card = deck.drawCard();
                if (card != null) {
                    player.drawCard(card);
                }
            }
        }

        Card firstCard = deck.drawCard();
        while (firstCard != null && isSpecialCard(firstCard)) {
            deck.drawCard();
            firstCard = deck.drawCard();
        }

        if (firstCard != null) {
            discardPile.add(firstCard);
        }

        fireModelInit();
    }

    public void playCard(Player player, int handIndex, Card.Color chosenColor) {
        saveStateOnMove();
        Player currentPlayer = players.get(currentPlayerIndex);

        if (handIndex < 0 || handIndex >= currentPlayer.getHandSize()) {
            fireError("Invalid card index: " + handIndex);
            return;
        }

        Card playedCard = currentPlayer.getHand().get(handIndex);

        if (!isCardPlayable(playedCard)) {
            fireError("Cannot play " + playedCard + " on " + getTopDiscardCard());
            return;
        }

        currentPlayer.getHand().remove(handIndex);
        discardPile.add(playedCard);

        if (playedCard.getColor() == Card.Color.WILD && chosenColor != null) {
            playedCard.setColor(chosenColor);
        }

        if (playedCard.getValue() == Card.Value.WILD_DRAW_COLOR && chosenColor != null) {
            playedCard.setColor(chosenColor);
        }

        fireStateUpdated();

        if (currentPlayer.getHandSize() == 0) {
            handleRoundWin(currentPlayerIndex);
            return;
        }

        handleSpecialCard(playedCard);
        currentTurnTaken = true;
        fireStateUpdated();
    }

    public Card drawCard() {
        saveStateOnMove();
        Player currentPlayer = players.get(currentPlayerIndex);

        Card drawnCard = deck.drawCard();

        if (drawnCard != null) {
            currentPlayer.drawCard(drawnCard);
            currentTurnTaken = !isCardPlayable(drawnCard);
            fireStateUpdated();
            return drawnCard;
        } else {
            fireError("Deck is empty!");
            return null;
        }
    }

    public void endTurn() {
        saveStateOnMove();
        advanceToNextPlayer();
        currentTurnTaken = false;
        fireTurnAdvanced(players.get(currentPlayerIndex));
    }

    public int calculateRoundScore(int winnerIndex) {
        Player winner = players.get(winnerIndex);
        int totalPoints = 0;

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

    public Player checkForGameWinner(int targetScore) {
        for (Player player : players) {
            if (player.getScore() >= targetScore) {
                return player;
            }
        }
        return null;
    }

    public void addListener(GameModelListener l) {
        if (l != null && !listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void removeListener(GameModelListener l) {
        listeners.remove(l);
    }

    private Card getTopDiscardCard() {
        if (discardPile.isEmpty()) {
            return null;
        }
        return discardPile.get(discardPile.size() - 1);
    }

    private List<Integer> getPlayableIndices() {
        Player currentPlayer = players.get(currentPlayerIndex);
        List<Integer> playableIndices = new ArrayList<>();
        Card topCard = getTopDiscardCard();

        if (topCard == null) {
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

    private boolean isCardPlayable(Card card) {
        Card topCard = getTopDiscardCard();

        if (topCard == null) {
            return true;
        }

        if (card.getColor() == Card.Color.WILD) {
            return true;
        }

        if (card.getColor() == topCard.getColor()) {
            return true;
        }

        if (card.getValue() == topCard.getValue()) {
            return true;
        }

        return false;
    }

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

    private void handleSpecialCard(Card playedCard) {
        switch (playedCard.getValue()) {
            case SKIP:
                advanceToNextPlayer();
                break;

            case REVERSE:
                isClockwise = !isClockwise;
                if (players.size() == 2) {
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
                break;
        }
    }

    private void handleFlipCard() {
        currentSide = (currentSide == Card.Side.LIGHT) ? Card.Side.DARK : Card.Side.LIGHT;

        for (Player player : players) {
            player.flipHand();
        }

        for (Card card : discardPile) {
            card.flip();
        }

        deck.flipAllCards();

        fireStateUpdated();
    }

    private void handleDrawFive() {
        advanceToNextPlayer();
        Player target = players.get(currentPlayerIndex);

        for (int i = 0; i < 5; i++) {
            Card drawnCard = deck.drawCard();
            if (drawnCard != null) {
                target.drawCard(drawnCard);
            } else {
                fireError("Deck exhausted during Draw Five");
                break;
            }
        }
        advanceToNextPlayer();
    }

    private void handleSkipEveryone() {
    }

    private void handleWildDrawColor(Card playedCard) {
        advanceToNextPlayer();
        Player target = players.get(currentPlayerIndex);
        Card.Color targetColor = playedCard.getColor();

        if (targetColor == Card.Color.WILD) {
            fireError("Wild Draw Color requires a color choice");
            return;
        }

        Card drawnCard;
        int cardsDrawn = 0;
        int maxCards = 20;

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

        advanceToNextPlayer();
    }

    private void advanceToNextPlayer() {
        if (isClockwise) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } else {
            currentPlayerIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
        }
    }

    private void handleRoundWin(int winnerIndex) {
        Player winner = players.get(winnerIndex);
        int points = calculateRoundScore(winnerIndex);

        fireRoundWon(winner, points);

        Player gameWinner = checkForGameWinner(TARGET_SCORE);
        if (gameWinner != null) {
            fireGameWon(gameWinner);
            return;
        }

        newRound();
    }

    private void fireModelInit() {
        GameState state = getState();
        for (GameModelListener listener : listeners) {
            listener.onModelInit(state);
        }
    }

    private void fireStateUpdated() {
        GameState state = getState();
        for (GameModelListener listener : listeners) {
            listener.onStateUpdated(state);
        }
    }

    private void fireTurnAdvanced(Player current) {
        GameState state = getState();
        for (GameModelListener listener : listeners) {
            listener.onTurnAdvanced(current, state);
        }
    }

    private void fireRoundWon(Player winner, int points) {
        GameState state = getState();
        for (GameModelListener listener : listeners) {
            listener.onRoundWon(winner, points, state);
        }
    }

    private void fireGameWon(Player winner) {
        GameState state = getState();
        for (GameModelListener listener : listeners) {
            listener.onGameWon(winner, state);
        }
    }

    private void fireError(String msg) {
        for (GameModelListener listener : listeners) {
            listener.onError(msg);
        }
    }

    /**
     * method to get the current gamestate using deep copies
     * @return the gamestate
     */
    public GameState getState() {
        GameState state = new GameState();

        state.players = new ArrayList<>();
        for (Player original : players) {
            Player copy;
            if (original instanceof AIPlayer) {
                AIPlayer aiOriginal = (AIPlayer) original;
                copy = new AIPlayer(aiOriginal.getName(), aiOriginal.getDifficultyLevel());
            } else {
                copy = new Player(original.getName());
            }
            copy.setScore(original.getScore());

            for (Card originalCard : original.getHand()) {
                Card cardCopy = new Card(
                        originalCard.getLightColor(),
                        originalCard.getLightValue(),
                        originalCard.getDarkColor(),
                        originalCard.getDarkValue(),
                        originalCard.getCurrentSide()
                );
                copy.drawCard(cardCopy);
            }
            state.players.add(copy);
        }

        state.currentPlayerIndex = currentPlayerIndex;
        state.currentPlayer = state.players.get(currentPlayerIndex);

        Card top = getTopDiscardCard();
        state.topDiscard = (top != null) ? new Card(
                top.getLightColor(), top.getLightValue(),
                top.getDarkColor(), top.getDarkValue(),
                top.getCurrentSide()
        ) : null;

        state.deckSize = deck.size();
        state.playableIndices = getPlayableIndices();
        state.clockwise = isClockwise;
        state.turnTaken = currentTurnTaken;
        state.currentSide = currentSide;
        state.undoStack = undoStack;
        state.redoStack = redoStack;

        return state;
    }

    public List<GameModelListener> getListeners() {
        return listeners;
    }

    public void newRound() {
        for (Player p : players) {
            p.getHand().clear();
        }

        deck = new Deck();
        discardPile.clear();
        discardPile.add(deck.drawCard());

        for (Player p : players) {
            for (int i = 0; i < 7; i++) {
                Card c = deck.drawCard();
                if (c != null) p.drawCard(c);
            }
        }

        currentPlayerIndex = 0;
        isClockwise = true;
        currentTurnTaken = false;
        currentSide = Card.Side.LIGHT;

        fireStateUpdated();
    }

    public void processAITurn() {
        Player currentPlayer = players.get(currentPlayerIndex);

        if (!(currentPlayer instanceof AIPlayer)) {
            return;
        }

        AIPlayer aiPlayer = (AIPlayer) currentPlayer;

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        GameState state = getState();

        int cardIndex = aiPlayer.selectCardToPlay(state);

        if (cardIndex == -1) {
            Card drawnCard = drawCard();

            if (drawnCard != null && isCardPlayable(drawnCard)) {
                int drawnCardIndex = aiPlayer.getHandSize() - 1;
                handleAICardPlay(aiPlayer, drawnCardIndex);
            } else {
                endTurn();
            }
        } else {
            handleAICardPlay(aiPlayer, cardIndex);
        }
    }

    private void handleAICardPlay(AIPlayer aiPlayer, int cardIndex) {
        saveStateOnMove();
        Card playedCard = aiPlayer.getHand().get(cardIndex);
        Card.Color chosenColor = null;

        if (playedCard.getColor() == Card.Color.WILD) {
            if (playedCard.getValue() == Card.Value.WILD ||
                    playedCard.getValue() == Card.Value.WILD_DRAW_TWO) {
                chosenColor = aiPlayer.chooseWildColor();
            } else if (playedCard.getValue() == Card.Value.WILD_DRAW_COLOR) {
                chosenColor = aiPlayer.chooseWildDrawColor();
            }
        }

        playCard(aiPlayer, cardIndex, chosenColor);

        if (aiPlayer.getHandSize() > 0) {
            if (playedCard.getValue() == Card.Value.SKIP_EVERYONE) {
                currentTurnTaken = false;
                fireStateUpdated();
                processAITurn();
            } else {
                endTurn();
            }
        }
    }

    public void checkAndProcessAITurn() {
        Player currentPlayer = players.get(currentPlayerIndex);
        if (currentPlayer instanceof AIPlayer) {
            processAITurn();
        }
    }

    /**
     * save the game state whenever a move is made
     */
    private void saveStateOnMove() {
        undoStack.push(getState());
        redoStack.clear();
    }

    /**
     * pops off the undo stack when undo is pressed
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(getState());
            GameState prev = undoStack.pop();
            restoreState(prev);
            fireStateUpdated();
        }
    }

    /**
     * pops off the redo stack when redo is pressed
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(getState());
            GameState next = redoStack.pop();
            restoreState(next);
            fireStateUpdated();
        }
    }

    /**
     * Method to restore a gamestate from a given state
     * @param state the state to be restored
     */
    public void restoreState(GameState state) {
        this.currentPlayerIndex = state.currentPlayerIndex;
        this.isClockwise = state.clockwise;
        this.currentTurnTaken = state.turnTaken;
        this.currentSide = state.currentSide;
        this.undoStack = state.undoStack;
        this.redoStack = state.redoStack;

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            player.getHand().clear();

            Player snapshotPlayer = state.players.get(i);
            for (Card snapshotCard : snapshotPlayer.getHand()) {
                Card restoredCard = new Card(
                        snapshotCard.getLightColor(),
                        snapshotCard.getLightValue(),
                        snapshotCard.getDarkColor(),
                        snapshotCard.getDarkValue(),
                        snapshotCard.getCurrentSide()
                );
                player.drawCard(restoredCard);
            }
            player.setScore(snapshotPlayer.getScore());
        }

        discardPile.clear();
        if (state.topDiscard != null) {
            Card topCopy = new Card(
                    state.topDiscard.getLightColor(),
                    state.topDiscard.getLightValue(),
                    state.topDiscard.getDarkColor(),
                    state.topDiscard.getDarkValue(),
                    state.topDiscard.getCurrentSide()
            );
            discardPile.add(topCopy);
        }
    }

    /**
     * Resets player scores and starts a new game
     * Used for replay functionality
     */
    public void restartGame() {
        for (Player p : players) {
            p.setScore(0);
        }
        newRound();
    }

    /**
     * Simple functions to determine if a player can use the undo button
     *
     * @return true if undoStack is not empty
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Simple functions to determine if a player can use the redo button
     *
     * @return true if redoStack is not empty
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
}