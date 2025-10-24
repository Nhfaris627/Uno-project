import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the Card class
 *
 * @author Faris Hassan 101300683
 * @version 1.0
 */

public class CardTest {

    @Test
    public void testCardConstructorAndGettersColored() {
        Card card = new Card(Card.Color.RED, Card.Value.FIVE);
        assertEquals(Card.Color.RED, card.getColor());
        assertEquals(Card.Value.FIVE, card.getValue());
    }

    @Test
    public void testCardConstructorAndGettersWild() {
        Card card = new Card(Card.Color.WILD, Card.Value.WILD_DRAW_TWO);
        assertEquals(Card.Color.WILD, card.getColor());
        assertEquals(Card.Value.WILD_DRAW_TWO, card.getValue());
    }

    @Test
    public void testToStringColored() {
        Card card = new Card(Card.Color.BLUE, Card.Value.SKIP);
        assertEquals("BLUE SKIP", card.toString());
    }

    @org.junit.Test
    @Test
    public void testToStringWild() {
        Card card = new Card(Card.Color.WILD, Card.Value.WILD);
        assertEquals("WILD", card.toString());
    }
}
