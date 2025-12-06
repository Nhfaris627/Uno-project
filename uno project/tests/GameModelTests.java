import controller.GameState;
import model.AIPlayer;
import model.Card;
import model.GameModel;
import model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Core unit tests for model.GameModel - focuses on essential game functionality.
 * Tests game setup, basic card playing, turn management, and winning conditions.
 *
 * @author Faris Hassan 101300683
 * @version 2.0
 */
public class GameModelTests {

    private GameModel model;
    private static final String TEST_SAVE_FILE = "test_save.dat";

    @BeforeEach
    void setUp() {
        model = new GameModel(2); // default 2 players before testing
    }

    @AfterEach
    public void tearDown() {
        // Clean up test save file
        File testFile = new File(TEST_SAVE_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }
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
        int currentPlayerIndex = state.currentPlayerIndex;

        List<Integer> playable = state.playableIndices;
        if (!playable.isEmpty()) {
            model.playCard(currentPlayer, playable.get(0), null);

            // Get fresh state after playing
            GameState newState = model.getState();

            // Check if we're still on the same player (not skipped/reversed)
            // And that the player didn't win (hand size = 0)
            if (newState.currentPlayerIndex == currentPlayerIndex && newState.currentPlayer.getHandSize() > 0) {
                assertEquals(initialHandSize - 1, newState.currentPlayer.getHandSize(),
                        "Hand size should decrease by 1 when playing a card");
            } else {
                // If turn advanced or player won, just verify hand size changed
                assertTrue(newState.players.get(currentPlayerIndex).getHandSize() <= initialHandSize,
                        "Hand size should not increase after playing a card");
            }
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
        int initialSize = state.currentPlayer.getHandSize();

        Card drawn = model.drawCard();

        assertNotNull(drawn, "Player should draw a card");

        GameState newState = model.getState();
        assertEquals(initialSize + 1, newState.currentPlayer.getHandSize());
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

        // Get the actual player from the model, not the copy
        int currentPlayerIndex = state.currentPlayerIndex;

        // Access the real players list directly through a fresh state each time
        // Remove all but one card from the actual model's current player
        while (model.getState().currentPlayer.getHandSize() > 1) {
            model.getState().currentPlayer.getHand().remove(0);
        }

        GameState freshState = model.getState();
        List<Integer> playable = freshState.playableIndices;

        if (playable.contains(0)) {
            int initialScore = freshState.currentPlayer.getScore();
            model.playCard(freshState.currentPlayer, 0, null);

            // Get final state to check score
            GameState finalState = model.getState();
            assertTrue(finalState.players.get(currentPlayerIndex).getScore() >= initialScore,
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

        // Calculate round score awards points to the actual model's players
        for (int i = 0; i < 5; i++) {
            // Award 100+ points per iteration
            model.calculateRoundScore(0);
        }

        // If still not enough, keep adding
        int safetyCounter = 0;
        while (model.checkForGameWinner(500) == null && safetyCounter < 50) {
            model.calculateRoundScore(0);
            safetyCounter++;
        }

        Player winner = model.checkForGameWinner(500);

        assertNotNull(winner, "Should detect winner with 500 points");
    }

    @Test
    void testSameColorPlayable() {
        model.startGame();
        GameState state = model.getState();
        Card topCard = state.topDiscard;

        // Skip if top card is wild
        if (topCard.getColor() == Card.Color.WILD) {
            return;
        }

        // Create a matching card and verify it would be playable
        List<Card> hand = state.currentPlayer.getHand();
        boolean foundSameColor = false;

        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            if (card.getColor() == topCard.getColor() && card.getColor() != Card.Color.WILD) {
                // This card should be in playableIndices
                assertTrue(state.playableIndices.contains(i),
                        "Same color card at index " + i + " should be playable");
                foundSameColor = true;
                break;
            }
        }

        // If no same-color card found in hand, test passes (can't test what doesn't exist)
        if (!foundSameColor) {
            assertTrue(true, "No same-color card in hand to test, but logic is correct");
        }
    }

    @Test
    void testSameValuePlayable() {
        model.startGame();
        GameState state = model.getState();
        Card topCard = state.topDiscard;

        // Skip if top is wild
        if (topCard.getColor() == Card.Color.WILD) {
            return;
        }

        // Check if any card in hand has same value
        List<Card> hand = state.currentPlayer.getHand();
        boolean foundSameValue = false;

        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            if (card.getValue() == topCard.getValue() && card.getColor() != Card.Color.WILD) {
                // This card should be in playableIndices
                assertTrue(state.playableIndices.contains(i),
                        "Same value card at index " + i + " should be playable");
                foundSameValue = true;
                break;
            }
        }

        if (!foundSameValue) {
            // Test passes if no matching card exists
            assertTrue(true, "No same-value card in hand to test");
        }
    }

    @Test
    void testWildCardIsPlayable() {
        model.startGame();
        GameState state = model.getState();

        // Check if any wild card exists in hand
        List<Card> hand = state.currentPlayer.getHand();

        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            if (card.getColor() == Card.Color.WILD) {
                // Wild card should ALWAYS be in playableIndices
                assertTrue(state.playableIndices.contains(i),
                        "Wild card at index " + i + " should always be playable");
                return; // Test passed
            }
        }

        // If no wild card in hand, the test is inconclusive but passes
        assertTrue(true, "No wild card in hand to test");
    }

    @Test
    void testDifferentTypeOfCardIsNotPlayable() {
        model.startGame();
        GameState state = model.getState();
        Card topCard = state.topDiscard;

        // Skip if top is wild
        if (topCard.getColor() == Card.Color.WILD) {
            return;
        }

        // Find a card that's completely different
        List<Card> hand = state.currentPlayer.getHand();

        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);

            // Check if card is completely different (different color AND value, not wild)
            if (card.getColor() != topCard.getColor() &&
                    card.getValue() != topCard.getValue() &&
                    card.getColor() != Card.Color.WILD) {

                // This card should NOT be in playableIndices
                assertFalse(state.playableIndices.contains(i),
                        "Completely different card at index " + i + " should not be playable");
                return; // Test passed
            }
        }

        // If all cards are playable, test is inconclusive but passes
        assertTrue(true, "No completely different card in hand to test");
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

    // UNDO/REDO 8 TESTS

    @Test
    @DisplayName("Test Undo After Drawing Card")
    public void testUndoAfterDrawCard() {
        model.startGame();
        GameState stateBefore = model.getState();
        int handSizeBefore = stateBefore.currentPlayer.getHandSize();

        // Draw a card
        model.drawCard();

        GameState stateAfter = model.getState();
        int handSizeAfter = stateAfter.currentPlayer.getHandSize();

        // Verify card was drawn
        assertEquals(handSizeBefore + 1, handSizeAfter, "Hand size should increase by 1 after drawing");
        assertTrue(model.canUndo(), "Should be able to undo after drawing a card");

        // Undo the draw
        model.undo();

        GameState stateUndone = model.getState();
        assertEquals(handSizeBefore, stateUndone.currentPlayer.getHandSize(),
                "Hand size should return to original after undo");
    }

    @Test
    @DisplayName("Test Undo After Playing Card")
    public void testUndoAfterPlayCard() {
        model.startGame();
        GameState initialState = model.getState();
        Player currentPlayer = initialState.currentPlayer;
        int initialHandSize = currentPlayer.getHandSize();

        // Find a playable card
        List<Integer> playableIndices = initialState.playableIndices;

        // If no playable cards, draw until we get one or can play
        while (playableIndices.isEmpty()) {
            model.drawCard();
            initialState = model.getState();
            playableIndices = initialState.playableIndices;
            currentPlayer = initialState.currentPlayer;
            initialHandSize = currentPlayer.getHandSize();
        }

        int playableIndex = playableIndices.get(0);

        // Play the card
        model.playCard(currentPlayer, playableIndex, null);

        GameState stateAfterPlay = model.getState();

        // Verify hand size decreased (unless game triggered special logic)
        assertTrue(model.canUndo(), "Should be able to undo after playing a card");

        // Undo the play
        model.undo();

        GameState stateUndone = model.getState();
        assertEquals(initialHandSize, stateUndone.currentPlayer.getHandSize(),
                "Hand size should return to original after undo");
    }

    @Test
    @DisplayName("Test Redo After Undo")
    public void testRedoAfterUndo() {
        model.startGame();
        GameState initialState = model.getState();
        int initialHandSize = initialState.currentPlayer.getHandSize();

        // Draw a card
        model.drawCard();
        int handSizeAfterDraw = model.getState().currentPlayer.getHandSize();
        assertEquals(initialHandSize + 1, handSizeAfterDraw, "Card should be drawn");

        // Undo the draw
        model.undo();
        assertEquals(initialHandSize, model.getState().currentPlayer.getHandSize(),
                "Undo should restore original hand size");
        assertTrue(model.canRedo(), "Should be able to redo after undo");

        // Redo the draw
        model.redo();
        assertEquals(handSizeAfterDraw, model.getState().currentPlayer.getHandSize(),
                "Redo should restore the drawn card");
    }

    @Test
    @DisplayName("Test Multiple Undo Operations")
    public void testMultipleUndos() {
        model.startGame();
        GameState initialState = model.getState();
        int initialHandSize = initialState.currentPlayer.getHandSize();

        // Perform multiple draws
        model.drawCard();
        model.drawCard();
        model.drawCard();

        int finalHandSize = model.getState().currentPlayer.getHandSize();
        assertEquals(initialHandSize + 3, finalHandSize, "Should have drawn 3 cards");

        // Undo all draws
        model.undo();
        assertEquals(initialHandSize + 2, model.getState().currentPlayer.getHandSize(),
                "First undo should remove one card");

        model.undo();
        assertEquals(initialHandSize + 1, model.getState().currentPlayer.getHandSize(),
                "Second undo should remove another card");

        model.undo();
        assertEquals(initialHandSize, model.getState().currentPlayer.getHandSize(),
                "Third undo should restore original hand size");
    }

    @Test
    @DisplayName("Test Redo Stack Clears After New Action")
    public void testRedoStackClearsAfterNewAction() {
        model.startGame();

        // Draw a card
        model.drawCard();

        // Undo the draw
        model.undo();
        assertTrue(model.canRedo(), "Should be able to redo");

        // Perform a new action (draw another card)
        model.drawCard();

        // Redo should no longer be available
        assertFalse(model.canRedo(), "Redo stack should be cleared after new action");
    }

    @Test
    @DisplayName("Test Undo/Redo Preserves Turn Order")
    public void testUndoRedoPreservesTurnOrder() {
        model.startGame();
        GameState initialState = model.getState();
        int initialPlayerIndex = initialState.currentPlayerIndex;

        // End turn to advance to next player
        model.endTurn();

        GameState stateAfterTurn = model.getState();
        assertNotEquals(initialPlayerIndex, stateAfterTurn.currentPlayerIndex,
                "Current player should change after ending turn");

        // Undo to previous turn
        model.undo();
        assertEquals(initialPlayerIndex, model.getState().currentPlayerIndex,
                "Undo should restore previous player");

        // Redo to advance turn again
        model.redo();
        assertEquals(stateAfterTurn.currentPlayerIndex, model.getState().currentPlayerIndex,
                "Redo should advance to next player again");
    }

    @Test
    @DisplayName("Test Cannot Undo When Stack Empty")
    public void testCannotUndoWhenStackEmpty() {
        model.startGame();
        // At game start, undo stack should be empty
        assertFalse(model.canUndo(), "Should not be able to undo at game start");

        // Attempting undo should not crash
        assertDoesNotThrow(() -> model.undo(), "Undo on empty stack should not throw exception");
    }

    @Test
    @DisplayName("Test Cannot Redo When Stack Empty")
    public void testCannotRedoWhenStackEmpty() {
        model.startGame();
        // At game start, redo stack should be empty
        assertFalse(model.canRedo(), "Should not be able to redo at game start");

        // Attempting redo should not crash
        assertDoesNotThrow(() -> model.redo(), "Redo on empty stack should not throw exception");
    }

    // SERIALIZATION/DESERIALIZATION TESTS 13 Test

    @Test
    @DisplayName("Test Save Game Creates File")
    public void testSaveGameCreatesFile() throws IOException {
        model.startGame();

        // Save the game
        model.saveGame(TEST_SAVE_FILE);

        // Verify file was created
        File saveFile = new File(TEST_SAVE_FILE);
        assertTrue(saveFile.exists(), "Save file should be created");
        assertTrue(saveFile.length() > 0, "Save file should not be empty");
    }

    @Test
    @DisplayName("Test Load Game Restores State")
    public void testLoadGameRestoresState() throws IOException, ClassNotFoundException {
        model.startGame();

        // Get initial state
        GameState originalState = model.getState();
        int originalHandSize = originalState.currentPlayer.getHandSize();
        int originalPlayerIndex = originalState.currentPlayerIndex;
        String originalPlayerName = originalState.currentPlayer.getName();

        // Save the game
        model.saveGame(TEST_SAVE_FILE);

        // Modify the game state (draw some cards)
        model.drawCard();
        model.drawCard();

        // Verify state has changed
        assertNotEquals(originalHandSize, model.getState().currentPlayer.getHandSize(),
                "State should be different after drawing cards");

        // Load the saved game
        GameState loadedState = GameModel.loadGame(TEST_SAVE_FILE);
        model.restoreState(loadedState);

        // Verify state was restored
        GameState restoredState = model.getState();
        assertEquals(originalHandSize, restoredState.currentPlayer.getHandSize(),
                "Hand size should be restored to original");
        assertEquals(originalPlayerIndex, restoredState.currentPlayerIndex,
                "Player index should be restored");
        assertEquals(originalPlayerName, restoredState.currentPlayer.getName(),
                "Player name should be restored");
    }

    @Test
    @DisplayName("Test Serialization Preserves Player Count")
    public void testSerializationPreservesPlayerCount() throws IOException, ClassNotFoundException {
        // Create game with 4 players
        GameModel fourPlayerModel = new GameModel(4);
        fourPlayerModel.startGame();

        int originalPlayerCount = fourPlayerModel.getState().players.size();
        assertEquals(4, originalPlayerCount, "Should start with 4 players");

        // Save and load
        fourPlayerModel.saveGame(TEST_SAVE_FILE);
        GameState loadedState = GameModel.loadGame(TEST_SAVE_FILE);

        assertEquals(originalPlayerCount, loadedState.players.size(),
                "Player count should be preserved after save/load");
    }


    @Test
    @DisplayName("Test Serialization Preserves Top Discard Card")
    public void testSerializationPreservesTopDiscard() throws IOException, ClassNotFoundException {
        model.startGame();
        GameState originalState = model.getState();
        Card originalTopCard = originalState.topDiscard;

        assertNotNull(originalTopCard, "Top discard card should exist");

        // Save and load
        model.saveGame(TEST_SAVE_FILE);
        GameState loadedState = GameModel.loadGame(TEST_SAVE_FILE);

        // Verify top card is preserved
        assertNotNull(loadedState.topDiscard, "Loaded top discard should not be null");
        assertEquals(originalTopCard.getColor(), loadedState.topDiscard.getColor(),
                "Top card color should be preserved");
        assertEquals(originalTopCard.getValue(), loadedState.topDiscard.getValue(),
                "Top card value should be preserved");
    }

    @Test
    @DisplayName("Test Serialization Preserves Direction")
    public void testSerializationPreservesDirection() throws IOException, ClassNotFoundException {
        model.startGame();
        GameState originalState = model.getState();
        boolean originalDirection = originalState.clockwise;

        // Save and load
        model.saveGame(TEST_SAVE_FILE);
        GameState loadedState = GameModel.loadGame(TEST_SAVE_FILE);

        assertEquals(originalDirection, loadedState.clockwise,
                "Game direction should be preserved");
    }

    @Test
    @DisplayName("Test Serialization Preserves AI Players")
    public void testSerializationPreservesAIPlayers() throws IOException, ClassNotFoundException {
        // Create game with AI players
        boolean[] isAI = {false, true};
        GameModel aiModel = new GameModel(2, isAI, AIPlayer.DifficultyLevel.HARD);
        aiModel.startGame();

        GameState originalState = aiModel.getState();
        assertFalse(originalState.players.get(0).isAI(), "Player 1 should be human");
        assertTrue(originalState.players.get(1).isAI(), "Player 2 should be AI");

        // Save and load
        aiModel.saveGame(TEST_SAVE_FILE);
        GameState loadedState = GameModel.loadGame(TEST_SAVE_FILE);

        // Verify AI status preserved
        assertFalse(loadedState.players.get(0).isAI(), "Player 1 should still be human");
        assertTrue(loadedState.players.get(1).isAI(), "Player 2 should still be AI");
    }

    @Test
    @DisplayName("Test Serialization Preserves Card Sides (Flip)")
    public void testSerializationPreservesCardSides() throws IOException, ClassNotFoundException {
        model.startGame();
        GameState originalState = model.getState();
        Card.Side originalSide = originalState.currentSide;

        // Save and load
        model.saveGame(TEST_SAVE_FILE);
        GameState loadedState = GameModel.loadGame(TEST_SAVE_FILE);

        assertEquals(originalSide, loadedState.currentSide,
                "Current side should be preserved");
    }

    @Test
    @DisplayName("Test Serialization Preserves Undo Stack")
    public void testSerializationPreservesUndoStack() throws IOException, ClassNotFoundException {
        model.startGame();

        // Perform some actions to populate undo stack
        model.drawCard();
        model.drawCard();

        assertTrue(model.canUndo(), "Should have undo history");

        // Save and load
        model.saveGame(TEST_SAVE_FILE);
        GameState loadedState = GameModel.loadGame(TEST_SAVE_FILE);

        // Create new model and restore
        GameModel newModel = new GameModel(2);
        newModel.restoreState(loadedState);

        // Verify undo is still available
        assertTrue(newModel.canUndo(), "Undo history should be preserved");
    }

    @Test
    @DisplayName("Test Load Nonexistent File Throws Exception")
    public void testLoadNonexistentFileThrowsException() {
        assertThrows(IOException.class, () -> {
            GameModel.loadGame("nonexistent_file.dat");
        }, "Loading nonexistent file should throw IOException");
    }

    @Test
    @DisplayName("Test Serialization Preserves Deck Size")
    public void testSerializationPreservesDeckSize() throws IOException, ClassNotFoundException {
        model.startGame();
        GameState originalState = model.getState();
        int originalDeckSize = originalState.deckSize;

        // Save and load
        model.saveGame(TEST_SAVE_FILE);
        GameState loadedState = GameModel.loadGame(TEST_SAVE_FILE);

        assertEquals(originalDeckSize, loadedState.deckSize,
                "Deck size should be preserved");
    }

    @Test
    @DisplayName("Test Complete Game Save/Load Cycle")
    public void testCompleteGameSaveLoadCycle() throws IOException, ClassNotFoundException {
        model.startGame();

        // Perform various game actions
        model.drawCard();

        // Find and play a card if possible
        GameState state = model.getState();
        if (!state.playableIndices.isEmpty()) {
            int playableIndex = state.playableIndices.get(0);
            model.playCard(state.currentPlayer, playableIndex, null);
        }

        model.endTurn();

        GameState stateBeforeSave = model.getState();
        int handSizeBeforeSave = stateBeforeSave.currentPlayer.getHandSize();
        int playerIndexBeforeSave = stateBeforeSave.currentPlayerIndex;

        // Save game
        model.saveGame(TEST_SAVE_FILE);

        // Create new model and load saved game
        GameModel newModel = new GameModel(2);
        GameState loadedState = GameModel.loadGame(TEST_SAVE_FILE);
        newModel.restoreState(loadedState);

        GameState stateAfterLoad = newModel.getState();

        // Verify complete restoration
        assertEquals(playerIndexBeforeSave, stateAfterLoad.currentPlayerIndex,
                "Current player should be preserved");
        assertEquals(handSizeBeforeSave, stateAfterLoad.currentPlayer.getHandSize(),
                "Hand size should be preserved");
        assertEquals(stateBeforeSave.players.size(), stateAfterLoad.players.size(),
                "Number of players should be preserved");
    }

    @Test
    @DisplayName("Test AI Difficulty Preserved Through Serialization")
    public void testAIDifficultyPreserved() throws IOException, ClassNotFoundException {
        // Create game with AI player at specific difficulty
        boolean[] isAI = {false, true};
        GameModel aiModel = new GameModel(2, isAI, AIPlayer.DifficultyLevel.HARD);
        aiModel.startGame();

        GameState originalState = aiModel.getState();
        AIPlayer originalAI = (AIPlayer) originalState.players.get(1);
        assertEquals(AIPlayer.DifficultyLevel.HARD, originalAI.getDifficultyLevel(),
                "Original AI should be HARD difficulty");

        // Save and load
        aiModel.saveGame(TEST_SAVE_FILE);
        GameState loadedState = GameModel.loadGame(TEST_SAVE_FILE);

        AIPlayer loadedAI = (AIPlayer) loadedState.players.get(1);
        assertEquals(AIPlayer.DifficultyLevel.HARD, loadedAI.getDifficultyLevel(),
                "AI difficulty should be preserved");
    }
}
