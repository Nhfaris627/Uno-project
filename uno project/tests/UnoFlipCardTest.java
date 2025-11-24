import model.Card;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for UNO Flip card functionality.
 * Tests dual-sided card mechanics, flipping, and dark side properties.
 *
 * @author Bhagya Patel, 101324150
 * @version 3.0
 */
public class UnoFlipCardTest {

    private Card lightRedFiveCard;
    private Card lightSkipCard;
    private Card wildCard;

    @BeforeEach
    public void setUp() {
        // Light side: RED 5, Dark side: TEAL 5
        lightRedFiveCard = new Card(Card.Color.RED, Card.Value.FIVE,
                Card.Color.TEAL, Card.Value.FIVE,
                Card.Side.LIGHT);

        // Light side: SKIP, Dark side: SKIP_EVERYONE
        lightSkipCard = new Card(Card.Color.BLUE, Card.Value.SKIP,
                Card.Color.PURPLE, Card.Value.SKIP_EVERYONE,
                Card.Side.LIGHT);

        // WILD cards are same on both sides
        wildCard = new Card(Card.Color.WILD, Card.Value.WILD,
                Card.Color.WILD, Card.Value.WILD,
                Card.Side.LIGHT);
    }

    // Test: Card Initial State (Light Side)

    @Test
    public void testCardInitializesOnLightSide() {
        assertEquals(Card.Side.LIGHT, lightRedFiveCard.getCurrentSide());
        assertEquals(Card.Color.RED, lightRedFiveCard.getColor());
        assertEquals(Card.Value.FIVE, lightRedFiveCard.getValue());
    }

    @Test
    public void testCardInitializesWithCorrectPointValue() {
        // Light side FIVE = 5 points
        assertEquals(5, lightRedFiveCard.getPointValue());
    }

    @Test
    public void testSkipCardInitializesWithCorrectValue() {
        // Light side SKIP = 20 points
        assertEquals(20, lightSkipCard.getPointValue());
    }

    // Test: Card Flip Mechanics

    @Test
    public void testFlipFromLightToDark() {
        lightRedFiveCard.flip();

        assertEquals(Card.Side.DARK, lightRedFiveCard.getCurrentSide());
        assertEquals(Card.Color.TEAL, lightRedFiveCard.getColor());
        assertEquals(Card.Value.FIVE, lightRedFiveCard.getValue());
    }

    @Test
    public void testFlipFromDarkToLight() {
        // Start on light side
        assertEquals(Card.Side.LIGHT, lightRedFiveCard.getCurrentSide());

        // Flip to dark
        lightRedFiveCard.flip();
        assertEquals(Card.Side.DARK, lightRedFiveCard.getCurrentSide());
        assertEquals(Card.Color.TEAL, lightRedFiveCard.getColor());

        // Flip back to light
        lightRedFiveCard.flip();
        assertEquals(Card.Side.LIGHT, lightRedFiveCard.getCurrentSide());
        assertEquals(Card.Color.RED, lightRedFiveCard.getColor());
    }

    @Test
    public void testMultipleFlips() {
        for (int i = 0; i < 5; i++) {
            if (i % 2 == 0) {
                lightRedFiveCard.flip();
                assertEquals(Card.Side.DARK, lightRedFiveCard.getCurrentSide());
                assertEquals(Card.Color.TEAL, lightRedFiveCard.getColor());
            } else {
                lightRedFiveCard.flip();
                assertEquals(Card.Side.LIGHT, lightRedFiveCard.getCurrentSide());
                assertEquals(Card.Color.RED, lightRedFiveCard.getColor());
            }
        }
    }

    // Test: Dark Side Colors

    @Test
    public void testDarkSideColorsMapping() {
        // RED -> TEAL
        Card redCard = new Card(Card.Color.RED, Card.Value.ONE,
                Card.Color.TEAL, Card.Value.ONE,
                Card.Side.LIGHT);
        redCard.flip();
        assertEquals(Card.Color.TEAL, redCard.getColor());

        // BLUE -> PURPLE
        Card blueCard = new Card(Card.Color.BLUE, Card.Value.TWO,
                Card.Color.PURPLE, Card.Value.TWO,
                Card.Side.LIGHT);
        blueCard.flip();
        assertEquals(Card.Color.PURPLE, blueCard.getColor());

        // GREEN -> PINK
        Card greenCard = new Card(Card.Color.GREEN, Card.Value.THREE,
                Card.Color.PINK, Card.Value.THREE,
                Card.Side.LIGHT);
        greenCard.flip();
        assertEquals(Card.Color.PINK, greenCard.getColor());

        // YELLOW -> ORANGE
        Card yellowCard = new Card(Card.Color.YELLOW, Card.Value.FOUR,
                Card.Color.ORANGE, Card.Value.FOUR,
                Card.Side.LIGHT);
        yellowCard.flip();
        assertEquals(Card.Color.ORANGE, yellowCard.getColor());
    }

    // Test: UNO Flip Card Types

    @Test
    public void testFlipCardValue() {
        Card flipCard = new Card(Card.Color.RED, Card.Value.FLIP,
                Card.Color.TEAL, Card.Value.FLIP,
                Card.Side.LIGHT);
        assertEquals(Card.Value.FLIP, flipCard.getValue());
        assertEquals(20, flipCard.getPointValue());
    }

    @Test
    public void testDrawFiveCardValue() {
        Card drawFiveCard = new Card(Card.Color.BLUE, Card.Value.DRAW_ONE,
                Card.Color.PURPLE, Card.Value.DRAW_FIVE,
                Card.Side.LIGHT);

        // Light side: DRAW_ONE (10 points)
        assertEquals(10, drawFiveCard.getPointValue());

        // Dark side: DRAW_FIVE (20 points)
        drawFiveCard.flip();
        assertEquals(20, drawFiveCard.getPointValue());
    }

    @Test
    public void testSkipEveryoneCardValue() {
        Card skipEveryoneCard = new Card(Card.Color.GREEN, Card.Value.SKIP,
                Card.Color.PINK, Card.Value.SKIP_EVERYONE,
                Card.Side.LIGHT);

        // Light side: SKIP (20 points)
        assertEquals(20, skipEveryoneCard.getPointValue());

        // Dark side: SKIP_EVERYONE (30 points)
        skipEveryoneCard.flip();
        assertEquals(30, skipEveryoneCard.getPointValue());
    }

    @Test
    public void testWildDrawColorCardValue() {
        Card wildDrawColorCard = new Card(Card.Color.WILD, Card.Value.WILD_DRAW_TWO,
                Card.Color.WILD, Card.Value.WILD_DRAW_COLOR,
                Card.Side.LIGHT);

        // Light side: WILD_DRAW_TWO (50 points)
        assertEquals(50, wildDrawColorCard.getPointValue());

        // Dark side: WILD_DRAW_COLOR (60 points)
        wildDrawColorCard.flip();
        assertEquals(60, wildDrawColorCard.getPointValue());
    }

    // Test: toString with Sides

    @Test
    public void testToStringIncludesSide() {
        String lightString = lightRedFiveCard.toString();
        assertTrue(lightString.contains("LIGHT"), "toString should include side");

        lightRedFiveCard.flip();
        String darkString = lightRedFiveCard.toString();
        assertTrue(darkString.contains("DARK"), "toString should show DARK after flip");
    }

    // Test: WildCard Side Agnostic

    @Test
    public void testWildCardSameOnBothSides() {
        assertEquals(Card.Color.WILD, wildCard.getColor());
        assertEquals(Card.Value.WILD, wildCard.getValue());

        wildCard.flip();

        assertEquals(Card.Color.WILD, wildCard.getColor());
        assertEquals(Card.Value.WILD, wildCard.getValue());
    }

    // Test: Card Constructor Variants

    @Test
    public void testCardConstructorWithBothSides() {
        Card card = new Card(Card.Color.YELLOW, Card.Value.NINE,
                Card.Color.ORANGE, Card.Value.NINE,
                Card.Side.DARK);

        assertEquals(Card.Side.DARK, card.getCurrentSide());
        assertEquals(Card.Color.ORANGE, card.getColor());
        assertEquals(Card.Value.NINE, card.getValue());
    }

    @Test
    public void testSimpleCardConstructor() {
        Card simpleCard = new Card(Card.Color.RED, Card.Value.FIVE);

        assertEquals(Card.Side.LIGHT, simpleCard.getCurrentSide());
        assertEquals(Card.Color.RED, simpleCard.getColor());
        assertEquals(Card.Value.FIVE, simpleCard.getValue());
    }
}


