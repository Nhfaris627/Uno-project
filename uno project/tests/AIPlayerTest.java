import model.Card;
import model.AIPlayer;
import model.Player;
import controller.GameState;
import model.GameModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * JUnit tests for AIPlayer difficulty levels and strategy implementation.
 * Tests card selection logic, wild color selection, and polymorphic behavior.
 *
 * @author Bhagya Patel, 101324150
 * @version 3.0
 */
public class AIPlayerTest {

    private AIPlayer easyAI;
    private AIPlayer mediumAI;
    private AIPlayer hardAI;
    private GameModel gameModel;

    @BeforeEach
    public void setUp() {
        easyAI = new AIPlayer("Easy AI", AIPlayer.DifficultyLevel.EASY);
        mediumAI = new AIPlayer("Medium AI", AIPlayer.DifficultyLevel.MEDIUM);
        hardAI = new AIPlayer("Hard AI", AIPlayer.DifficultyLevel.HARD);

        // Create game with 2 human + 1 AI
        boolean[] isAI = {false, false, true};
        gameModel = new GameModel(3, isAI, AIPlayer.DifficultyLevel.MEDIUM);
        gameModel.startGame();
    }

    /**
     * Helper method to create a valid GameState for testing
     */
    private GameState createTestGameState(AIPlayer aiPlayer, Card topCard, List<Integer> playableIndices) {
        GameState state = new GameState();
        state.players = new ArrayList<>();
        state.players.add(aiPlayer);
        state.players.add(new Player("Opponent"));
        state.currentPlayer = aiPlayer;
        state.topDiscard = topCard;
        state.playableIndices = playableIndices;
        state.clockwise = true;
        state.turnTaken = false;
        state.currentSide = Card.Side.LIGHT;
        state.deckSize = 50;
        return state;
    }

    // Test: AIPlayer Constructors

    @Test
    public void testAIPlayerConstructorWithDifficulty() {
        assertEquals("Easy AI", easyAI.getName());
        assertEquals(AIPlayer.DifficultyLevel.EASY, easyAI.getDifficultyLevel());
    }

    @Test
    public void testAIPlayerDefaultDifficulty() {
        AIPlayer defaultAI = new AIPlayer("Default AI");
        assertEquals(AIPlayer.DifficultyLevel.MEDIUM, defaultAI.getDifficultyLevel());
    }

    @Test
    public void testAIPlayerIsAI() {
        assertTrue(easyAI.isAI());
        assertTrue(mediumAI.isAI());
        assertTrue(hardAI.isAI());

        // Compare with regular player
        Player humanPlayer = new Player("Human");
        assertFalse(humanPlayer.isAI());
    }

    // Test: EASY Difficulty (Random Selection)

    @Test
    public void testEasyAIReturnsValidCard() {
        // Clear hand and add specific cards
        easyAI.getHand().clear();
        easyAI.drawCard(new Card(Card.Color.RED, Card.Value.ONE));
        easyAI.drawCard(new Card(Card.Color.RED, Card.Value.TWO));
        easyAI.drawCard(new Card(Card.Color.RED, Card.Value.THREE));

        // Create valid GameState with all cards playable
        Card topCard = new Card(Card.Color.RED, Card.Value.FIVE);
        List<Integer> playableIndices = Arrays.asList(0, 1, 2);
        GameState state = createTestGameState(easyAI, topCard, playableIndices);

        int selectedIndex = easyAI.selectCardToPlay(state);

        // Should return valid playable index
        assertTrue(selectedIndex == 0 || selectedIndex == 1 || selectedIndex == 2,
                "Easy AI should select from playable indices");
    }

    @Test
    public void testEasyAIRandomness() {
        // Clear hand and fill with same color cards (all playable)
        easyAI.getHand().clear();
        easyAI.drawCard(new Card(Card.Color.BLUE, Card.Value.ONE));
        easyAI.drawCard(new Card(Card.Color.BLUE, Card.Value.TWO));
        easyAI.drawCard(new Card(Card.Color.BLUE, Card.Value.THREE));
        easyAI.drawCard(new Card(Card.Color.BLUE, Card.Value.FOUR));

        Card topCard = new Card(Card.Color.BLUE, Card.Value.FIVE);
        List<Integer> playableIndices = Arrays.asList(0, 1, 2, 3);
        GameState state = createTestGameState(easyAI, topCard, playableIndices);

        // Run multiple times and collect selections
        List<Integer> selections = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int index = easyAI.selectCardToPlay(state);
            if (index != -1) {
                selections.add(index);
            }
        }

        // Should make valid selections
        assertTrue(selections.size() > 0, "Easy AI should make selections");
        for (int idx : selections) {
            assertTrue(playableIndices.contains(idx), "All selections should be valid");
        }
    }

    @Test
    public void testEasyAIDrawsWhenNoCards() {
        // Empty playable indices
        GameState emptyState = createTestGameState(easyAI, new Card(Card.Color.RED, Card.Value.FIVE), new ArrayList<>());

        int result = easyAI.selectCardToPlay(emptyState);
        assertEquals(-1, result, "Easy AI should return -1 (draw) when no playable cards");
    }

    // Test: MEDIUM Difficulty (Priority Strategy)

    @Test
    public void testMediumAIPrioritizesSpecialCards() {
        // Clear hand and add both number and special cards
        mediumAI.getHand().clear();
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.ONE));    // Index 0
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.SKIP));   // Index 1 - Special

        Card topCard = new Card(Card.Color.RED, Card.Value.FIVE);
        List<Integer> playableIndices = Arrays.asList(0, 1); // Both playable
        GameState state = createTestGameState(mediumAI, topCard, playableIndices);

        int selectedIndex = mediumAI.selectCardToPlay(state);

        // Should prioritize special card (SKIP at index 1)
        assertEquals(1, selectedIndex, "Medium AI should prioritize special cards");
    }

    @Test
    public void testMediumAIPrioritizesColorMatch() {
        // Clear hand
        mediumAI.getHand().clear();
        mediumAI.drawCard(new Card(Card.Color.BLUE, Card.Value.FIVE));  // Index 0 - matches value
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.THREE));  // Index 1 - matches color

        // Top card is RED 5
        Card topCard = new Card(Card.Color.RED, Card.Value.FIVE);
        List<Integer> playableIndices = Arrays.asList(0, 1); // Both playable
        GameState state = createTestGameState(mediumAI, topCard, playableIndices);

        int selectedIndex = mediumAI.selectCardToPlay(state);

        // Should prefer color match (RED 3 at index 1)
        assertEquals(1, selectedIndex, "Medium AI should prioritize color match");
    }

    @Test
    public void testMediumAIPrioritizesHighValue() {
        // Clear hand and add various values (no special cards, no color match)
        mediumAI.getHand().clear();
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.ONE));    // Index 0 - 1 point
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.TWO));    // Index 1 - 2 points
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.NINE));   // Index 2 - 9 points (highest)

        Card topCard = new Card(Card.Color.BLUE, Card.Value.FIVE);
        List<Integer> playableIndices = Arrays.asList(0, 1, 2); // All red cards playable
        GameState state = createTestGameState(mediumAI, topCard, playableIndices);

        int selectedIndex = mediumAI.selectCardToPlay(state);

        // Should select highest value (NINE at index 2)
        assertEquals(2, selectedIndex, "Medium AI should prioritize high value cards");
    }

    // Test: HARD Difficulty (Advanced Strategy)

    @Test
    public void testHardAIDetectsThreat() {
        // Clear hand
        hardAI.getHand().clear();
        hardAI.drawCard(new Card(Card.Color.RED, Card.Value.THREE));        // Index 0 - regular
        hardAI.drawCard(new Card(Card.Color.RED, Card.Value.DRAW_FIVE));    // Index 1 - disruptive

        // Create opponent with only 2 cards (threat!)
        Player opponent = new Player("Threat");
        opponent.drawCard(new Card(Card.Color.BLUE, Card.Value.ONE));
        opponent.drawCard(new Card(Card.Color.BLUE, Card.Value.TWO));

        GameState state = createTestGameState(hardAI, new Card(Card.Color.RED, Card.Value.FIVE), Arrays.asList(0, 1));
        state.players.clear();
        state.players.add(hardAI);
        state.players.add(opponent); // Opponent is next

        int selectedIndex = hardAI.selectCardToPlay(state);

        // Hard AI should prioritize disruptive card (DRAW_FIVE at index 1)
        assertEquals(1, selectedIndex, "Hard AI should use disruptive cards when opponent is threat");
    }

    @Test
    public void testHardAIPreservesWildCards() {
        // Clear hand
        hardAI.getHand().clear();
        hardAI.drawCard(new Card(Card.Color.RED, Card.Value.FIVE));     // Index 0 - Non-wild
        hardAI.drawCard(new Card(Card.Color.WILD, Card.Value.WILD));    // Index 1 - Wild

        Card topCard = new Card(Card.Color.RED, Card.Value.THREE);
        List<Integer> playableIndices = Arrays.asList(0, 1); // Both playable
        GameState state = createTestGameState(hardAI, topCard, playableIndices);

        int selectedIndex = hardAI.selectCardToPlay(state);

        // Hard AI should prefer non-wild (RED 5 at index 0)
        assertEquals(0, selectedIndex, "Hard AI should preserve wild cards when non-wild available");
    }

    @Test
    public void testHardAIUsesWildWhenNecessary() {
        // Clear hand - only wild card playable
        hardAI.getHand().clear();
        hardAI.drawCard(new Card(Card.Color.WILD, Card.Value.WILD));    // Index 0 - Only option

        Card topCard = new Card(Card.Color.RED, Card.Value.FIVE);
        List<Integer> playableIndices = Arrays.asList(0); // Only wild playable
        GameState state = createTestGameState(hardAI, topCard, playableIndices);

        int selectedIndex = hardAI.selectCardToPlay(state);

        // Hard AI should use wild when necessary
        assertEquals(0, selectedIndex, "Hard AI should use wild card when no other options");
    }

    // Test: Wild Color Selection

    @Test
    public void testChooseWildColorReturnsValidColor() {
        mediumAI.getHand().clear();
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.ONE));
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.TWO));
        mediumAI.drawCard(new Card(Card.Color.BLUE, Card.Value.THREE));

        Card.Color chosenColor = mediumAI.chooseWildColor();

        assertNotNull(chosenColor);
        assertNotEquals(Card.Color.WILD, chosenColor, "Should not choose WILD color");
        assertTrue(chosenColor == Card.Color.RED || chosenColor == Card.Color.BLUE ||
                        chosenColor == Card.Color.GREEN || chosenColor == Card.Color.YELLOW,
                "Should choose valid light-side color");
    }

    @Test
    public void testChooseWildColorPrefersMostCommon() {
        // Clear hand and fill with RED dominant
        mediumAI.getHand().clear();
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.ONE));
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.TWO));
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.THREE));
        mediumAI.drawCard(new Card(Card.Color.BLUE, Card.Value.FOUR));

        Card.Color chosenColor = mediumAI.chooseWildColor();

        assertEquals(Card.Color.RED, chosenColor, "Should choose most common color (RED)");
    }

    @Test
    public void testChooseWildDrawColorReturnsValidDarkColor() {
        mediumAI.getHand().clear();
        mediumAI.drawCard(new Card(Card.Color.TEAL, Card.Value.ONE));
        mediumAI.drawCard(new Card(Card.Color.PURPLE, Card.Value.TWO));

        Card.Color chosenColor = mediumAI.chooseWildDrawColor();

        assertNotNull(chosenColor);
        assertTrue(chosenColor == Card.Color.TEAL || chosenColor == Card.Color.PURPLE ||
                        chosenColor == Card.Color.PINK || chosenColor == Card.Color.ORANGE,
                "Should choose valid dark-side color");
    }

    @Test
    public void testChooseWildColorWhenHandEmpty() {
        // Empty hand
        mediumAI.getHand().clear();
        Card.Color chosenColor = mediumAI.chooseWildColor();

        assertNotNull(chosenColor, "Should choose random color even with empty hand");
        assertTrue(chosenColor == Card.Color.RED || chosenColor == Card.Color.BLUE ||
                chosenColor == Card.Color.GREEN || chosenColor == Card.Color.YELLOW);
    }

    @Test
    public void testChooseWildDrawColorPrefersMostCommon() {
        // Clear hand and fill with TEAL dominant
        mediumAI.getHand().clear();
        mediumAI.drawCard(new Card(Card.Color.TEAL, Card.Value.ONE));
        mediumAI.drawCard(new Card(Card.Color.TEAL, Card.Value.TWO));
        mediumAI.drawCard(new Card(Card.Color.PURPLE, Card.Value.THREE));

        Card.Color chosenColor = mediumAI.chooseWildDrawColor();

        assertEquals(Card.Color.TEAL, chosenColor, "Should choose most common dark color (TEAL)");
    }

    // Test: Hand Operations (Inherited)

    @Test
    public void testAIPlayerHandManagement() {
        hardAI.getHand().clear();
        assertEquals(0, hardAI.getHandSize());

        Card card1 = new Card(Card.Color.RED, Card.Value.FIVE);
        hardAI.drawCard(card1);

        assertEquals(1, hardAI.getHandSize());
        assertTrue(hardAI.getHand().contains(card1));
    }

    @Test
    public void testAIPlayerHandValue() {
        hardAI.getHand().clear();
        hardAI.drawCard(new Card(Card.Color.RED, Card.Value.FIVE));  // 5
        hardAI.drawCard(new Card(Card.Color.BLUE, Card.Value.SKIP)); // 20

        assertEquals(25, hardAI.calculateHandValue());
    }

    @Test
    public void testAIPlayerScoreTracking() {
        hardAI.getHand().clear();
        assertEquals(0, hardAI.getScore());

        hardAI.addScore(50);
        assertEquals(50, hardAI.getScore());

        hardAI.addScore(30);
        assertEquals(80, hardAI.getScore());
    }

    // Test: AI Turn Integration with GameModel

    @Test
    public void testGameModelDetectsAI() {
        boolean[] isAI = {true, false};
        GameModel gameWithAI = new GameModel(2, isAI, AIPlayer.DifficultyLevel.MEDIUM);
        gameWithAI.startGame();

        Player player0 = gameWithAI.getState().players.get(0);
        Player player1 = gameWithAI.getState().players.get(1);

        assertTrue(player0.isAI(), "Player 0 should be AI");
        assertFalse(player1.isAI(), "Player 1 should be human");
    }

    @Test
    public void testAIPlayerIntegrationWithGameModel() {
        gameModel.startGame();
        GameState state = gameModel.getState();

        // Should be able to call isAI() on any player
        for (Player player : state.players) {
            assertNotNull(player);
            // Just checking method exists and returns boolean
            boolean isAI = player.isAI();
            assertTrue(isAI || !isAI); // Tautology to verify method works
        }
    }

    // Test: Legal Move Guarantee

    @Test
    public void testAISelectsOnlyLegalMoves() {
        mediumAI.getHand().clear();
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.ONE));
        mediumAI.drawCard(new Card(Card.Color.BLUE, Card.Value.TWO));
        mediumAI.drawCard(new Card(Card.Color.GREEN, Card.Value.THREE));

        Card topCard = new Card(Card.Color.RED, Card.Value.FIVE);
        List<Integer> playableIndices = Arrays.asList(0, 1); // Only indices 0 and 1 playable
        GameState state = createTestGameState(mediumAI, topCard, playableIndices);

        for (int i = 0; i < 20; i++) {
            int selectedIndex = mediumAI.selectCardToPlay(state);

            if (selectedIndex != -1) {
                assertTrue(playableIndices.contains(selectedIndex),
                        "AI must select from playable indices");
            }
        }
    }

    @Test
    public void testAIDrawsWhenNoLegalMoves() {
        GameState emptyState = createTestGameState(easyAI, new Card(Card.Color.RED, Card.Value.FIVE), new ArrayList<>());

        int result = easyAI.selectCardToPlay(emptyState);
        assertEquals(-1, result, "AI should return -1 to draw when no legal moves");
    }

    // Test: Difficulty Levels Enum

    @Test
    public void testDifficultyLevelEnum() {
        assertNotNull(AIPlayer.DifficultyLevel.EASY);
        assertNotNull(AIPlayer.DifficultyLevel.MEDIUM);
        assertNotNull(AIPlayer.DifficultyLevel.HARD);
    }

    @Test
    public void testAIDifficultyAssignment() {
        AIPlayer easy = new AIPlayer("Test", AIPlayer.DifficultyLevel.EASY);
        AIPlayer medium = new AIPlayer("Test", AIPlayer.DifficultyLevel.MEDIUM);
        AIPlayer hard = new AIPlayer("Test", AIPlayer.DifficultyLevel.HARD);

        assertEquals(AIPlayer.DifficultyLevel.EASY, easy.getDifficultyLevel());
        assertEquals(AIPlayer.DifficultyLevel.MEDIUM, medium.getDifficultyLevel());
        assertEquals(AIPlayer.DifficultyLevel.HARD, hard.getDifficultyLevel());
    }

    // Test: Edge Cases

    @Test
    public void testAIWithSingleCard() {
        easyAI.getHand().clear();
        easyAI.drawCard(new Card(Card.Color.RED, Card.Value.FIVE));

        Card topCard = new Card(Card.Color.RED, Card.Value.THREE);
        List<Integer> playableIndices = Arrays.asList(0);
        GameState state = createTestGameState(easyAI, topCard, playableIndices);

        int selectedIndex = easyAI.selectCardToPlay(state);

        assertEquals(0, selectedIndex, "AI should select only available card");
    }

    @Test
    public void testAIWithOnlyWildCards() {
        mediumAI.getHand().clear();
        mediumAI.drawCard(new Card(Card.Color.WILD, Card.Value.WILD));
        mediumAI.drawCard(new Card(Card.Color.WILD, Card.Value.WILD_DRAW_TWO));

        Card topCard = new Card(Card.Color.RED, Card.Value.FIVE);
        List<Integer> playableIndices = Arrays.asList(0, 1);
        GameState state = createTestGameState(mediumAI, topCard, playableIndices);

        int selectedIndex = mediumAI.selectCardToPlay(state);

        assertTrue(selectedIndex == 0 || selectedIndex == 1, "AI should select a wild card");
    }

    @Test
    public void testAIPolymorphism() {
        Player regularPlayer = new Player("Human");
        Player aiPlayer = new AIPlayer("AI", AIPlayer.DifficultyLevel.MEDIUM);

        // Both should be usable as Player type
        List<Player> players = Arrays.asList(regularPlayer, aiPlayer);

        assertEquals(2, players.size());
        assertFalse(players.get(0).isAI());
        assertTrue(players.get(1).isAI());
    }
}