import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

/**
 * JUnit tests for the UnoGame class
 *
 * @author Faris Hassan 101300683
 * @version 1.0
 */
public class UnoGameTest {

    private UnoGame game;
    private List<String> playerNames;

    @BeforeEach
    public void setUp() {
        playerNames = Arrays.asList("Alice", "Bob", "Charlie");
        game = new UnoGame(playerNames);
    }

    @Test
    public void testUnoGameConstructorValid() {
        assertNotNull(game);
        assertEquals(3, game.getPlayerCount());
        assertNotNull(game.getPlayers());
        assertNotNull(game.getCurrentPlayer());
    }

    @Test
    public void testUnoGameConstructorTwoPlayers() {
        UnoGame twoPlayerGame = new UnoGame(Arrays.asList("Player1", "Player2"));
        assertEquals(2, twoPlayerGame.getPlayerCount());
    }

    @Test
    public void testUnoGameConstructorFourPlayers() {
        UnoGame fourPlayerGame = new UnoGame(Arrays.asList("P1", "P2", "P3", "P4"));
        assertEquals(4, fourPlayerGame.getPlayerCount());
    }

    @Test
    public void testUnoGameConstructorTooFewPlayers() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new UnoGame(Arrays.asList("OnlyOne"));
        });
        assertTrue(exception.getMessage().contains("Game requires 2-4 players"));
    }

    @Test
    public void testUnoGameConstructorTooManyPlayers() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new UnoGame(Arrays.asList("P1", "P2", "P3", "P4", "P5"));
        });
        assertTrue(exception.getMessage().contains("Game requires 2-4 players"));
    }

    @Test
    public void testStartGame() {
        game.startGame();

        for (Player player : game.getPlayers()) {
            assertEquals(7, player.getHandSize());
        }
    }

    @Test
    public void testGetCurrentPlayer() {
        Player currentPlayer = game.getCurrentPlayer();
        assertNotNull(currentPlayer);
        assertEquals("Alice", currentPlayer.getName());
    }

    @Test
    public void testGetPlayers() {
        List<Player> players = game.getPlayers();
        assertNotNull(players);
        assertEquals(3, players.size());
        assertEquals("Alice", players.get(0).getName());
        assertEquals("Bob", players.get(1).getName());
        assertEquals("Charlie", players.get(2).getName());
    }

    @Test
    public void testGetPlayerCount() {
        assertEquals(3, game.getPlayerCount());
    }

    @Test
    public void testPassTurn() {
        game.startGame();

        Player firstPlayer = game.getCurrentPlayer();
        assertEquals("Alice", firstPlayer.getName());
        int initialHandSize = firstPlayer.getHandSize();

        game.passTurn();

        // First player should have drawn a card
        assertEquals(initialHandSize + 1, firstPlayer.getHandSize());

        // Current player should have changed to Bob
        Player secondPlayer = game.getCurrentPlayer();
        assertEquals("Bob", secondPlayer.getName());
    }

    @Test
    public void testPassTurnCycles() {
        game.startGame();

        // Pass through all players
        game.passTurn(); // Alice -> Bob
        assertEquals("Bob", game.getCurrentPlayer().getName());

        game.passTurn(); // Bob -> Charlie
        assertEquals("Charlie", game.getCurrentPlayer().getName());

        game.passTurn(); // Charlie -> Alice (cycles back)
        assertEquals("Alice", game.getCurrentPlayer().getName());
    }
}