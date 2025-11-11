import controller.GameState;
import model.Card;
import model.GameModel;
import model.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Core unit tests for model.GameModel - focuses on essential game functionality.
 * Tests game setup, basic card playing, turn management, and winning conditions.
 *
 * @author Faris Hassan 101300683
 * @version 2.0
 */
public class GameModelTests {

    private GameModel model;

    @BeforeEach
    void setUp() {
        model = new GameModel(3); // default 3 players before testing
    }

    @Test
    void testGameAcceptsTwoPlayers() {
        GameModel game = new GameModel(2);
        assertNotNull(game);
        assertEquals(2, game.getState().players.size());
    }
    @Test
    void testGameRejectsOnePlayer() {
        assertThrows(IllegalArgumentException.class, () -> new GameModel(1));
    }

    @Test
    void testInitialDeal() {
        model.startGame();
        GameState state = model.getState();

        for (Player player : state.players) {
            assertEquals(7, player.getHandSize(),
                    player.getName() + " should have exactly 7 cards");
        }
    }

    @Test
    void testDiscardPileCreated() {
        model.startGame();
        GameState state = model.getState();

        assertNotNull(state.topDiscard, "Discard pile must have a starting card");
    }

    @Test
    void testPlayCardRemovesFromHand() {
        model.startGame();
        GameState state = model.getState();
        Player currentPlayer = state.currentPlayer;
        int initialHandSize = currentPlayer.getHandSize();

        List<Integer> playable = state.playableIndices;
        if (!playable.isEmpty()) {
            model.playCard(currentPlayer, playable.get(0), null);
            assertEquals(initialHandSize - 1, currentPlayer.getHandSize());
        }
    }

    @Test
    void testPlayCardUpdatesDiscard() {
        model.startGame();
        GameState state = model.getState();
        Player currentPlayer = state.currentPlayer;

        List<Integer> playable = state.playableIndices;
        if (!playable.isEmpty()) {
            Card playedCard = currentPlayer.getHand().get(playable.get(0));
            model.playCard(currentPlayer, playable.get(0), null);

            // Verify the card is now on top of discard
            GameState newState = model.getState();
            assertEquals(playedCard.getValue(), newState.topDiscard.getValue());
        }
    }

    @Test
    void testDrawCard() {
        model.startGame();
        GameState state = model.getState();
        Player currentPlayer = state.currentPlayer;
        int initialSize = currentPlayer.getHandSize();

        Card drawn = model.drawCard();

        assertNotNull(drawn, "model.Player Should draw a card");
        assertEquals(initialSize + 1, currentPlayer.getHandSize());
    }

    @Test
    void testTurnGoesToNextPlayer() {
        model.startGame();
        Player firstPlayer = model.getState().currentPlayer;

        model.endTurn();

        Player secondPlayer = model.getState().currentPlayer;
        assertNotEquals(firstPlayer.getName(), secondPlayer.getName(),
                "Turn should advance to the next player");
    }

    @Test
    void testInitialDirectionIsClockwise() {
        model.startGame();
        assertTrue(model.getState().clockwise, "Game should start in clockwise direction");
    }

    @Test
    void testRoundWinCondition() {
        model.startGame();
        GameState state = model.getState();
        Player currentPlayer = state.currentPlayer;

        // Remove all but one card
        while (currentPlayer.getHandSize() > 1) {
            currentPlayer.getHand().remove(0);
        }

        // Play last card if possible
        List<Integer> playable = model.getState().playableIndices;
        if (playable.contains(0)) {
            int initialScore = currentPlayer.getScore();
            model.playCard(currentPlayer, 0, null);

            // Score should increase (round win)
            assertTrue(currentPlayer.getScore() >= initialScore,
                    "Winning player should receive points");
        }
    }

    @Test
    void testRoundScoring() {
        model.startGame();
        GameState state = model.getState();

        // Calculate expected score
        int expectedScore = 0;
        for (int i = 1; i < state.players.size(); i++) {
            expectedScore += state.players.get(i).calculateHandValue();
        }

        int actualScore = model.calculateRoundScore(0);

        assertEquals(expectedScore, actualScore,
                "Round score should equal sum of opponents' hand values");
    }

    @Test
    void testNoWinnerYet() {
        model.startGame();

        Player winner = model.checkForGameWinner(500);

        assertNull(winner, "Should be no winner at game start");
    }

    @Test
    void testGameWinner() {
        model.startGame();
        GameState state = model.getState();
        Player player = state.players.get(0);

        player.addScore(500);

        Player winner = model.checkForGameWinner(500);

        assertNotNull(winner, "Should detect winner with 500 points");
        assertEquals(player.getName(), winner.getName());
    }

    @Test
    void testSameColorPlayable() {
        model.startGame();
        GameState state = model.getState();
        Card topCard = state.topDiscard;
        Player currentPlayer = state.currentPlayer;

        // Skip if top card is wild
        if (topCard.getColor() == Card.Color.WILD) {
            return;
        }

        // Add card with same color
        Card sameColorCard = new Card(topCard.getColor(), Card.Value.THREE);
        currentPlayer.getHand().add(sameColorCard);

        // Get new state and check playability
        GameState newState = model.getState();
        int addedIndex = currentPlayer.getHandSize() - 1;

        assertTrue(newState.playableIndices.contains(addedIndex),
                "Same color card should be playable");
    }

    @Test
    void testSameValuePlayable() {
        model.startGame();
        GameState state = model.getState();
        Card topCard = state.topDiscard;
        Player currentPlayer = state.currentPlayer;

        // Skip if top is wild
        if (topCard.getColor() == Card.Color.WILD) {
            return;
        }

        // Add card with same value, different color
        Card.Color differentColor = (topCard.getColor() == Card.Color.RED) ?
                Card.Color.BLUE : Card.Color.RED;
        Card sameValueCard = new Card(differentColor, topCard.getValue());
        currentPlayer.getHand().add(sameValueCard);

        GameState newState = model.getState();
        int addedIndex = currentPlayer.getHandSize() - 1;

        assertTrue(newState.playableIndices.contains(addedIndex),
                "Same value card should be playable");
    }

    @Test
    void testWildCardIsPlayable() {
        model.startGame();
        GameState state = model.getState();
        Player currentPlayer = state.currentPlayer;

        // Add wild card
        Card wildCard = new Card(Card.Color.WILD, Card.Value.WILD);
        currentPlayer.getHand().add(wildCard);

        GameState newState = model.getState();
        int addedIndex = currentPlayer.getHandSize() - 1;

        assertTrue(newState.playableIndices.contains(addedIndex),
                "Wild card should always be playable");
    }

    @Test
    void testDifferentTypeOfCardIsNotPlayable() {
        model.startGame();
        GameState state = model.getState();
        Card topCard = state.topDiscard;
        Player currentPlayer = state.currentPlayer;

        // Skip if top is wild
        if (topCard.getColor() == Card.Color.WILD) {
            return;
        }

        // Create completely different card
        Card.Color diffColor = (topCard.getColor() == Card.Color.RED) ?
                Card.Color.BLUE : Card.Color.RED;
        Card.Value diffValue = (topCard.getValue() == Card.Value.FIVE) ?
                Card.Value.SEVEN : Card.Value.FIVE;

        Card differentCard = new Card(diffColor, diffValue);
        currentPlayer.getHand().add(differentCard);

        GameState newState = model.getState();
        int addedIndex = currentPlayer.getHandSize() - 1;

        assertFalse(newState.playableIndices.contains(addedIndex),
                "Completely different card should not be playable");
    }

    @Test
    void testGameStateCurrentPlayer() {
        model.startGame();
        GameState state = model.getState();

        assertNotNull(state.currentPlayer);
        assertTrue(state.players.contains(state.currentPlayer));
    }

    @Test
    void testGameStateDeckSize() {
        model.startGame();
        GameState state = model.getState();
        int deckSize = state.deckSize;

        // Draw a card
        model.drawCard();
        GameState newState = model.getState();

        assertEquals(deckSize - 1, newState.deckSize,
                "model.Deck size should decrease after drawing");
    }

    @Test
    void testPlayableIndicesValid() {
        model.startGame();
        GameState state = model.getState();

        for (int index : state.playableIndices) {
            assertTrue(index >= 0, "Playable index should be non-negative");
            assertTrue(index < state.currentPlayer.getHandSize(),
                    "Playable index should be within hand size");
        }
    }

}
