package model;

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
     * Initializes a deck with UNO flip cards light and dark sides
     */
    private void initializeDeck() {
        // Light side cards
        for (Card.Color color : new Card.Color[]{Card.Color.RED, Card.Color.BLUE, 
                                                   Card.Color.GREEN, Card.Color.YELLOW}) {

            // Map light colors to dark colors
            Card.Color darkColor = mapLightToDarkColor(color);

            // One zero per color
            cards.add(new Card(color, Card.Value.ZERO, darkColor, Card.Value.FIVE, Card.Side.LIGHT));

            // Two of each 1-9 per color
            Card.Value[] numbers = {Card.Value.ONE, Card.Value.TWO, Card.Value.THREE,
                    Card.Value.FOUR, Card.Value.FIVE, Card.Value.SIX,
                    Card.Value.SEVEN, Card.Value.EIGHT, Card.Value.NINE};

            for (Card.Value value : numbers) {
                // Each number maps to a dark side number
                Card.Value darkValue = mapLightToDarkValue(value);
                cards.add(new Card(color, value, darkColor, darkValue, Card.Side.LIGHT));
                cards.add(new Card(color, value, darkColor, darkValue, Card.Side.LIGHT));
            }

            // Two skip cards per color and per side, light and dark
            cards.add(new Card(color, Card.Value.SKIP, darkColor, Card.Value.SKIP_EVERYONE, Card.Side.LIGHT));
            cards.add(new Card(color, Card.Value.SKIP, darkColor, Card.Value.SKIP_EVERYONE, Card.Side.LIGHT));

            // Two reverse cards per color and side
            cards.add(new Card(color, Card.Value.REVERSE, darkColor, Card.Value.REVERSE, Card.Side.LIGHT));
            cards.add(new Card(color, Card.Value.REVERSE, darkColor, Card.Value.REVERSE, Card.Side.LIGHT));

            // Two draw one cards per color (maps to DRAW_FIVE on dark side)
            cards.add(new Card(color, Card.Value.DRAW_ONE, darkColor, Card.Value.DRAW_FIVE, Card.Side.LIGHT));
            cards.add(new Card(color, Card.Value.DRAW_ONE, darkColor, Card.Value.DRAW_FIVE, Card.Side.LIGHT));

            // Two flip cards per color (maps to FLIP on dark side)
            cards.add(new Card(color, Card.Value.FLIP, darkColor, Card.Value.FLIP, Card.Side.LIGHT));
            cards.add(new Card(color, Card.Value.FLIP, darkColor, Card.Value.FLIP, Card.Side.LIGHT));
        }
        
        // Add 4 Wild cards (map to WILD on dark side)
        for (int i = 0; i < 4; i++) {
            cards.add(new Card(Card.Color.WILD, Card.Value.WILD,
                        Card.Color.WILD, Card.Value.WILD, Card.Side.LIGHT));

        }

        // Add 4 Wild Draw Two cards (map to WILD_DRAW_COLOR on dark side)
        for (int i = 0; i < 4; i++) {
            cards.add(new Card(Card.Color.WILD, Card.Value.WILD_DRAW_TWO,
                        Card.Color.WILD, Card.Value.WILD_DRAW_COLOR, Card.Side.LIGHT));
        }
    }


    /**
     * Maps a light side color to its corresponding dark side color
     */
    private Card.Color mapLightToDarkColor(Card.Color lightColor) {
        switch (lightColor) {
            case RED: return Card.Color.TEAL;
            case BLUE: return Card.Color.PURPLE;
            case GREEN: return Card.Color.PINK;
            case YELLOW: return Card.Color.ORANGE;
            default: return lightColor;
        }
    }

    /**
     * Maps a light side number value to its corresponding dark side value
     */
    private Card.Value mapLightToDarkValue(Card.Value lightValue) {
        // Simple mapping: same numbers on both sides
        return lightValue;
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

    public void flipAllCards() {
        for (Card card : cards) {
            card.flip();
        }
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
