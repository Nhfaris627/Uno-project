/**
 * Represents a single UNO card.
 * "Simplified version for initial implementation"
 * 
 * @author Bhagya Patel 101324150
 * @verison 1.0
 */

public class Card {

    public enum Color{
        RED, BLUE, GREEN, YELLOW,WILD
    }

    public enum Value{
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, SKIP, REVERSE,
        DRAW_TWO, WILD, WILD_DRAW_TWO
    }

    private final Color color;
    private final Value value;

    /**
     * Creates a card with given color and value
     */
    public Card(Color color, Value value){
        this.color = color;
        this.value = value;
    }

    public Color getColor(){
        return color;
    }

    public Value getValue(){
        return value;
    }

    @Override 
    public String toString(){
        if (color == Color.WILD){
            return value.toString();
        }

        return color + " " + value;
    }
}

