import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the Deck class
 *
 * @author Faris Hassan 101300683
 * @version 1.0
 */
public class DeckTest {

    private Deck deck;

    @BeforeEach
    public void setUp() {
        deck = new Deck();
    }

    @Test
    public void testDeckConstructor() {
        assertEquals(108, deck.size());
        assertFalse(deck.isEmpty());
    }

    @Test
    public void testDrawCard() {
        int initialSize = deck.size();
        Card card = deck.drawCard();

        assertNotNull(card);
        assertEquals(initialSize - 1, deck.size());
    }

    @Test
    public void testDrawCardWhenEmpty() {
        // Draw all cards
        while (!deck.isEmpty()) {
            deck.drawCard();
        }

        assertTrue(deck.isEmpty());
        assertNull(deck.drawCard());
    }

    @Test
    public void testShuffle() {
        // Draw first 5 cards before shuffle
        Card[] beforeShuffle = new Card[5];
        for (int i = 0; i < 5; i++) {
            beforeShuffle[i] = deck.drawCard();
        }

        // Create new deck and draw first 5 cards after shuffle
        Deck newDeck = new Deck();
        Card[] afterShuffle = new Card[5];
        for (int i = 0; i < 5; i++) {
            afterShuffle[i] = newDeck.drawCard();
        }

        // At least one card should be different (very high probability with shuffle)
        boolean foundDifference = false;
        for (int i = 0; i < 5; i++) {
            if (!beforeShuffle[i].toString().equals(afterShuffle[i].toString())) {
                foundDifference = true;
                break;
            }
        }

        assertTrue(foundDifference, "Shuffle should change card order");
    }

    @Test
    public void testSize() {
        assertEquals(108, deck.size());

        deck.drawCard();
        assertEquals(107, deck.size());

        deck.drawCard();
        deck.drawCard();
        assertEquals(105, deck.size());
    }

    @Test
    public void testIsEmpty() {
        assertFalse(deck.isEmpty());

        // Draw all cards
        while (deck.size() > 0) {
            deck.drawCard();
        }

        assertTrue(deck.isEmpty());
    }
}