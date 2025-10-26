import java.lang.invoke.CallSite;

/**
 * Represents a single UNO card.
 * "Simplified version for initial implementation"
 * 
 * @author Bhagya Patel 101324150
 * @author Faris Hassan 101300683
 * @author Ivan Arkhipov 101310636
 * @author Nicky Fang 101304731
 * @version 1.0
 */

public class Card {

    public enum Color{
        RED, BLUE, GREEN, YELLOW, WILD
    }

    public enum Value{
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, SKIP, REVERSE,
        DRAW_ONE, WILD, WILD_DRAW_TWO
    }

    private Color color;
    private final Value value;

    /**
     * Creates a card with given color and value
     */
    public Card(Color color, Value value){
        this.color = color;
        this.value = value;
    }

    /**
     * Get the color of a card
     * @return the color of the card
     */
    public Color getColor(){
        return color;
    }

    /**
     * Set the color of a card
     * @param color The color to be set to
     */
    public void setColor( Color color) {
        this.color = color;
    }

    /**
     * Get the value of a card
     * @return the value of a card
     */
    public Value getValue(){
        return value;
    }

    /**
     * Gets the point of value of the specific card for scoring
     * @return the point value of a card
     */
    public int getPointValue() {
        switch(value) {
            case ZERO: return 0;
            case ONE: return 1;
            case TWO: return 2;
            case THREE: return 3;
            case FOUR: return 4;
            case FIVE: return 5;
            case SIX: return 6;
            case SEVEN: return 7;
            case EIGHT: return 8;
            case NINE: return 9;
            case SKIP: return 20;
            case REVERSE: return 20;
            case DRAW_ONE: return 10;
            case WILD: return 40;
            case WILD_DRAW_TWO: return 50;
            default: return 0;
        }
    }

    @Override 
    public String toString(){
        if (color == Color.WILD){
            return value.toString();
        }

        return color + " " + value;
    }
}

