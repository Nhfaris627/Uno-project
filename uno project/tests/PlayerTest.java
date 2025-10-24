import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the Player class
 *
 * @author Faris Hassan 101300683
 * @version 1.0
 */
public class PlayerTest {

    private Player player;

    @BeforeEach
    public void setUp() {
        player = new Player("Alice");
    }

    @Test
    public void testPlayerConstructor() {
        assertEquals("Alice", player.getName());
        assertEquals(0, player.getHandSize());
        assertNotNull(player.getHand());
    }

    @Test
    public void testGetName() {
        assertEquals("Alice", player.getName());
    }

    @Test
    public void testGetHand() {
        assertNotNull(player.getHand());
        assertTrue(player.getHand().isEmpty());
    }

    @Test
    public void testGetHandSize() {
        assertEquals(0, player.getHandSize());

        player.drawCard(new Card(Card.Color.RED, Card.Value.FIVE));
        assertEquals(1, player.getHandSize());

        player.drawCard(new Card(Card.Color.BLUE, Card.Value.SKIP));
        assertEquals(2, player.getHandSize());
    }

    @Test
    public void testDrawCard() {
        Card card = new Card(Card.Color.GREEN, Card.Value.SEVEN);
        player.drawCard(card);

        assertEquals(1, player.getHandSize());
        assertTrue(player.getHand().contains(card));
    }

    @Test
    public void testDrawCardNull() {
        player.drawCard(null);
        assertEquals(0, player.getHandSize());
    }

    @Test
    public void testDrawMultipleCards() {
        Card card1 = new Card(Card.Color.RED, Card.Value.THREE);
        Card card2 = new Card(Card.Color.BLUE, Card.Value.SKIP);
        Card card3 = new Card(Card.Color.WILD, Card.Value.WILD);

        player.drawCard(card1);
        player.drawCard(card2);
        player.drawCard(card3);

        assertEquals(3, player.getHandSize());
        assertTrue(player.getHand().contains(card1));
        assertTrue(player.getHand().contains(card2));
        assertTrue(player.getHand().contains(card3));
    }

    @Test
    public void testToString() {
        assertEquals("Alice (0 cards)", player.toString());

        player.drawCard(new Card(Card.Color.RED, Card.Value.FIVE));
        assertEquals("Alice (1 cards)", player.toString());

        player.drawCard(new Card(Card.Color.BLUE, Card.Value.TWO));
        player.drawCard(new Card(Card.Color.GREEN, Card.Value.NINE));
        assertEquals("Alice (3 cards)", player.toString());
    }
}