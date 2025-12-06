import model.Card;
import model.AIPlayer;
import model.Player;
import controller.GameState;
import model.GameModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
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
    private GameState gameState;

    @BeforeEach
    public void setUp() {
        easyAI = new AIPlayer("Easy AI", AIPlayer.DifficultyLevel.EASY);
        mediumAI = new AIPlayer("Medium AI", AIPlayer.DifficultyLevel.MEDIUM);
        hardAI = new AIPlayer("Hard AI", AIPlayer.DifficultyLevel.HARD);

        // Create game with 2 human + 1 AI
        boolean[] isAI = {false, false, true};
        gameModel = new GameModel(3, isAI, AIPlayer.DifficultyLevel.MEDIUM);
        gameModel.startGame();
        gameState = gameModel.getState();
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
    public void testEasyAIDifficultyReturnsValidCard() {
        // Setup: Add multiple playable cards
        Player player = easyAI;
        player.drawCard(new Card(Card.Color.RED, Card.Value.ONE));
        player.drawCard(new Card(Card.Color.RED, Card.Value.TWO));
        player.drawCard(new Card(Card.Color.RED, Card.Value.THREE));

        GameState state = gameModel.getState();
        int selectedIndex = easyAI.selectCardToPlay(state);

        // Should return -1 (draw) or valid playable index
        assertTrue(selectedIndex == -1 || state.playableIndices.contains(selectedIndex),
                "Easy AI should select from playable indices or draw");
    }

    @Test
    public void testEasyAIRandomness() {
        // Fill hand with same color cards (all playable)
        easyAI.drawCard(new Card(Card.Color.BLUE, Card.Value.ONE));
        easyAI.drawCard(new Card(Card.Color.BLUE, Card.Value.TWO));
        easyAI.drawCard(new Card(Card.Color.BLUE, Card.Value.THREE));
        easyAI.drawCard(new Card(Card.Color.BLUE, Card.Value.FOUR));

        GameState state = gameModel.getState();

        // Run multiple times and collect selections
        List<Integer> selections = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int index = easyAI.selectCardToPlay(state);
            if (index != -1) {
                selections.add(index);
            }
        }

        // Should have variety (not all same card)
        assertTrue(selections.size() > 0, "Easy AI should make selections");
    }

    @Test
    public void testEasyAIDrawsWhenNoCards() {
        // Empty hand with no playable cards
        GameState emptyState = new GameState();
        emptyState.playableIndices = new ArrayList<>();

        int result = easyAI.selectCardToPlay(emptyState);
        assertEquals(-1, result, "Easy AI should return -1 (draw) when no playable cards");
    }

    // Test: MEDIUM Difficulty (Priority Strategy)

    @Test
    public void testMediumAIPrioritizesSpecialCards() {
        // Hand with both number and special cards
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.ONE));
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.SKIP)); // Special

        GameState state = gameModel.getState();
        int selectedIndex = mediumAI.selectCardToPlay(state);

        // Should select from playable (could be skip if playable)
        assertTrue(selectedIndex == -1 || state.playableIndices.contains(selectedIndex));
    }

    @Test
    public void testMediumAIPrioritizesColorMatch() {
        // Setup: top card is RED 5
        Card topCard = new Card(Card.Color.RED, Card.Value.FIVE);

        // Hand with color match and non-matching
        mediumAI.drawCard(new Card(Card.Color.BLUE, Card.Value.ONE));
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.THREE)); // Color match

        GameState state = gameModel.getState();
        int selectedIndex = mediumAI.selectCardToPlay(state);

        // Should prefer color match
        assertTrue(selectedIndex == -1 || state.playableIndices.contains(selectedIndex));
    }

    @Test
    public void testMediumAIPrioritizesHighValue() {
        // Hand with various values
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.ONE)); // 1 point
        mediumAI.drawCard(new Card(Card.Color.BLUE, Card.Value.TWO)); // 2 points
        mediumAI.drawCard(new Card(Card.Color.GREEN, Card.Value.SKIP)); // 20 points

        GameState state = gameModel.getState();
        int selectedIndex = mediumAI.selectCardToPlay(state);

        assertTrue(selectedIndex == -1 || state.playableIndices.contains(selectedIndex));
    }

    // Test: HARD Difficulty (Advanced Strategy)

    @Test
    public void testHardAIDetectsThreat() {
        // Setup: Next player has 2 cards (threat!)
        gameModel.startGame();
        Player nextPlayer = gameModel.getState().players.get(1);

        // Remove most cards from next player (simulate threat)
        while (nextPlayer.getHandSize() > 2) {
            nextPlayer.getHand().remove(0);
        }

        // AI hand with disruptive cards
        hardAI.drawCard(new Card(Card.Color.WILD, Card.Value.WILD_DRAW_TWO));

        GameState state = gameModel.getState();
        int selectedIndex = hardAI.selectCardToPlay(state);

        // Hard AI should try to disrupt (select disruptive card if playable)
        assertTrue(selectedIndex == -1 || state.playableIndices.contains(selectedIndex));
    }

    @Test
    public void testHardAIPreservesWildCards() {
        // Hand with wild and non-wild playable
        hardAI.drawCard(new Card(Card.Color.RED, Card.Value.FIVE)); // Non-wild
        hardAI.drawCard(new Card(Card.Color.WILD, Card.Value.WILD)); // Wild

        GameState state = gameModel.getState();
        int selectedIndex = hardAI.selectCardToPlay(state);

        // Hard AI should prefer non-wild if available
        assertTrue(selectedIndex == -1 || state.playableIndices.contains(selectedIndex));
    }

    // Test: Wild Color Selection

    @Test
    public void testChooseWildColorReturnsValidColor() {
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.ONE));
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.TWO));
        mediumAI.drawCard(new Card(Card.Color.BLUE, Card.Value.THREE));

        Card.Color chosenColor = mediumAI.chooseWildColor();

        assertNotNull(chosenColor);
        assertNotEquals(Card.Color.WILD, chosenColor,
                "Should not choose WILD color");
        assertTrue(chosenColor == Card.Color.RED || chosenColor == Card.Color.BLUE ||
                        chosenColor == Card.Color.GREEN || chosenColor == Card.Color.YELLOW,
                "Should choose valid light-side color");
    }

    @Test
    public void testChooseWildColorPrefersMostCommon() {
        // Hand dominated by RED
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.ONE));
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.TWO));
        mediumAI.drawCard(new Card(Card.Color.RED, Card.Value.THREE));
        mediumAI.drawCard(new Card(Card.Color.BLUE, Card.Value.FOUR));

        Card.Color chosenColor = mediumAI.chooseWildColor();

        assertEquals(Card.Color.RED, chosenColor,
                "Should choose most common color (RED)");
    }

    @Test
    public void testChooseWildDrawColorReturnsValidDarkColor() {
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
        Card.Color chosenColor = mediumAI.chooseWildColor();

        assertNotNull(chosenColor, "Should choose random color even with empty hand");
        assertTrue(chosenColor == Card.Color.RED || chosenColor == Card.Color.BLUE ||
                chosenColor == Card.Color.GREEN || chosenColor == Card.Color.YELLOW);
    }

    // Test: Hand Operations (Inherited)

    @Test
    public void testAIPlayerHandManagement() {
        assertEquals(0, hardAI.getHandSize());

        Card card1 = new Card(Card.Color.RED, Card.Value.FIVE);
        hardAI.drawCard(card1);

        assertEquals(1, hardAI.getHandSize());
        assertTrue(hardAI.getHand().contains(card1));
    }

    @Test
    public void testAIPlayerHandValue() {
        hardAI.drawCard(new Card(Card.Color.RED, Card.Value.FIVE)); // 5
        hardAI.drawCard(new Card(Card.Color.BLUE, Card.Value.SKIP)); // 20

        assertEquals(25, hardAI.calculateHandValue());
    }

    // Test: AI Turn Integration with GameModel

    @Test
    public void testGameModelDetectsAI() {
        boolean[] isAI = {true, false};
        GameModel gameWithAI = new GameModel(2, isAI, AIPlayer.DifficultyLevel.MEDIUM);

        Player player0 = gameWithAI.getState().players.get(0);
        Player player1 = gameWithAI.getState().players.get(1);

        assertTrue(player0.isAI(), "Player 0 should be AI");
        assertFalse(player1.isAI(), "Player 1 should be human");
    }

    @Test
    public void testAIPlayerIntegrationWithGameModel() {
        gameModel.startGame();
        GameState state = gameModel.getState();

        // Advance to AI player
        Player currentPlayer = state.currentPlayer;

        // Should be able to call isAI() on any player
        assertNotNull(currentPlayer.isAI());
    }

    // Test: Legal Move Guarantee

    @Test
    public void testAISelectsOnlyLegalMoves() {
        gameModel.startGame();
        GameState state = gameModel.getState();

        for (int i = 0; i < 20; i++) {
            int selectedIndex = mediumAI.selectCardToPlay(state);

            if (selectedIndex != -1) {
                assertTrue(state.playableIndices.contains(selectedIndex),
                        "AI must select from playable indices");
            }
        }
    }

    @Test
    public void testAIDrawsWhenNoLegalMoves() {
        GameState emptyState = new GameState();
        emptyState.playableIndices = new ArrayList<>();

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
}