import model.Card;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the model.Card class
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

    @Test
    public void testGetPointValueVariousCards() {
        assertEquals(20, new Card(Card.Color.RED, Card.Value.SKIP).getPointValue());
        assertEquals(1, new Card(Card.Color.BLUE, Card.Value.ONE).getPointValue());
        assertEquals(5, new Card(Card.Color.GREEN, Card.Value.FIVE).getPointValue());
        assertEquals(20, new Card(Card.Color.BLUE, Card.Value.REVERSE).getPointValue());
        assertEquals(10, new Card(Card.Color.GREEN, Card.Value.DRAW_ONE).getPointValue());
        assertEquals(40, new Card(Card.Color.WILD, Card.Value.WILD).getPointValue());
        assertEquals(50, new Card(Card.Color.WILD, Card.Value.WILD_DRAW_TWO).getPointValue());
    }

    @org.junit.Test
    @Test
    public void testToStringWild() {
        Card card = new Card(Card.Color.WILD, Card.Value.WILD);
        assertEquals("WILD", card.toString());
    }
}
