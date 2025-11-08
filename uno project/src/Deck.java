import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Represents a deck of UNO Cards.
 * 
 * @author Bhagya Patel 101324150
 * @author Faris Hassan 101300683
 * @author Ivan Arkhipov 101310636
 * @author Nicky Fang 101304731
 * @version 1.0
 */

public class Deck {

    private List<Card> cards;

    /**
     * Creates and initializes a standard UNO deck
     */

    public Deck(){
        cards = new ArrayList<>();
        initializeDeck();
        shuffle();
    }

    /**
     * Initializes a deck with 108 UNO cards
     */
    private void initializeDeck() {
        // Add number cards for each color
        for (Card.Color color : new Card.Color[]{Card.Color.RED, Card.Color.BLUE, 
                                                   Card.Color.GREEN, Card.Color.YELLOW}) {
            // One zero per color
            cards.add(new Card(color, Card.Value.ZERO));
            
            // Two of each 1-9 per color
            for (Card.Value value : new Card.Value[]{Card.Value.ONE, Card.Value.TWO, 
                    Card.Value.THREE, Card.Value.FOUR, Card.Value.FIVE, Card.Value.SIX,
                    Card.Value.SEVEN, Card.Value.EIGHT, Card.Value.NINE}) {
                cards.add(new Card(color, value));
                cards.add(new Card(color, value));
            }
            
            // Two of each action card per color
            cards.add(new Card(color, Card.Value.SKIP));
            cards.add(new Card(color, Card.Value.SKIP));
            cards.add(new Card(color, Card.Value.REVERSE));
            cards.add(new Card(color, Card.Value.REVERSE));
        }
        
        // Add 4 Wild cards and 4 Wild Draw Two cards
        for (int i = 0; i < 4; i++) {
            cards.add(new Card(Card.Color.WILD, Card.Value.WILD));
            cards.add(new Card(Card.Color.WILD, Card.Value.WILD_DRAW_TWO));
        }
    }

    /**
     * Shuffles the deck
     */
    public void shuffle(){
        Collections.shuffle(cards);
    }

    /**
     * Draws a card from the deck
     * @return The drawn card, or null if empty
     */
    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(cards.size() - 1);
    }
    
    /**
     * Gets number of cards remaining
     */
    public int size() {
        return cards.size();
    }
    
    public boolean isEmpty() {
        return cards.isEmpty();
    }


}
