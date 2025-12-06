package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a deck of UNO Cards with serialization support
 *
 * @author Bhagya Patel 101324150
 * @author Faris Hassan 101300683
 * @author Ivan Arkhipov 101310636
 * @author Nicky Fang 101304731
 * @version 2.0 - Added Serializable for Milestone 4
 */
public class Deck implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        initializeDeck();
        shuffle();
    }

    private void initializeDeck() {
        for (Card.Color color : new Card.Color[]{Card.Color.RED, Card.Color.BLUE,
                Card.Color.GREEN, Card.Color.YELLOW}) {

            Card.Color darkColor = mapLightToDarkColor(color);

            cards.add(new Card(color, Card.Value.ZERO, darkColor, Card.Value.FIVE, Card.Side.LIGHT));

            Card.Value[] numbers = {Card.Value.ONE, Card.Value.TWO, Card.Value.THREE,
                    Card.Value.FOUR, Card.Value.FIVE, Card.Value.SIX,
                    Card.Value.SEVEN, Card.Value.EIGHT, Card.Value.NINE};

            for (Card.Value value : numbers) {
                Card.Value darkValue = mapLightToDarkValue(value);
                cards.add(new Card(color, value, darkColor, darkValue, Card.Side.LIGHT));
                cards.add(new Card(color, value, darkColor, darkValue, Card.Side.LIGHT));
            }

            cards.add(new Card(color, Card.Value.SKIP, darkColor, Card.Value.SKIP_EVERYONE, Card.Side.LIGHT));
            cards.add(new Card(color, Card.Value.SKIP, darkColor, Card.Value.SKIP_EVERYONE, Card.Side.LIGHT));

            cards.add(new Card(color, Card.Value.REVERSE, darkColor, Card.Value.REVERSE, Card.Side.LIGHT));
            cards.add(new Card(color, Card.Value.REVERSE, darkColor, Card.Value.REVERSE, Card.Side.LIGHT));

            cards.add(new Card(color, Card.Value.DRAW_ONE, darkColor, Card.Value.DRAW_FIVE, Card.Side.LIGHT));
            cards.add(new Card(color, Card.Value.DRAW_ONE, darkColor, Card.Value.DRAW_FIVE, Card.Side.LIGHT));

            cards.add(new Card(color, Card.Value.FLIP, darkColor, Card.Value.FLIP, Card.Side.LIGHT));
            cards.add(new Card(color, Card.Value.FLIP, darkColor, Card.Value.FLIP, Card.Side.LIGHT));
        }

        for (int i = 0; i < 4; i++) {
            cards.add(new Card(Card.Color.WILD, Card.Value.WILD,
                    Card.Color.WILD, Card.Value.WILD, Card.Side.LIGHT));
        }

        for (int i = 0; i < 4; i++) {
            cards.add(new Card(Card.Color.WILD, Card.Value.WILD_DRAW_TWO,
                    Card.Color.WILD, Card.Value.WILD_DRAW_COLOR, Card.Side.LIGHT));
        }
    }

    private Card.Color mapLightToDarkColor(Card.Color lightColor) {
        switch (lightColor) {
            case RED: return Card.Color.TEAL;
            case BLUE: return Card.Color.PURPLE;
            case GREEN: return Card.Color.PINK;
            case YELLOW: return Card.Color.ORANGE;
            default: return lightColor;
        }
    }

    private Card.Value mapLightToDarkValue(Card.Value lightValue) {
        return lightValue;
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

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

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}