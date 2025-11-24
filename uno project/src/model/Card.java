package model;

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
        RED, BLUE, GREEN, YELLOW, WILD,
        TEAL, PURPLE, PINK, ORANGE // flip side colors
    }

    public enum Value{
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, SKIP, REVERSE,
        DRAW_ONE, WILD, WILD_DRAW_TWO,
        FLIP, DRAW_FIVE, SKIP_EVERYONE, WILD_DRAW_COLOR
    }

    public enum Side {
        LIGHT, DARK
    }

    private Color color;
    private Value value;
    private Side currentSide;

    // Store both sides of the card for flipping
    private final Color lightColor;
    private final Value lightValue;
    private final Color darkColor;
    private final Value darkValue;

    /**
     * Creates a card with given color and value
     */
    public Card(Color color, Value value) {
        this(color, value, color, value, Side.LIGHT);
    }

    /**
     * Creates a card with both light and dark sides
     * @param lightColor Color on light side
     * @param lightValue Value on light side
     * @param darkColor Color on dark side
     * @param darkValue Value on dark side
     * @param initialSide Which side is initially facing up
     */
    public Card(Color lightColor, Value lightValue, Color darkColor, Value darkValue, Side initialSide) {
        this.lightColor = lightColor;
        this.lightValue = lightValue;
        this.darkColor = darkColor;
        this.darkValue = darkValue;
        this.currentSide = initialSide;

        // Set current color and value based on initial side
        if (initialSide == Side.LIGHT) {
            this.color = lightColor;
            this.value = lightValue;
        } else {
            this.color = darkColor;
            this.value = darkValue;
        }
    }

    public void flip() {
        if (currentSide == Side.LIGHT) {
            currentSide = Side.DARK;
            this.color = darkColor;
            this.value = darkValue;
        } else {
            currentSide = Side.LIGHT;
            this.color = lightColor;
            this.value = lightValue;
        }
    }

    /**
     * Get the current side of the card
     * @return the current side (LIGHT or DARK)
     */
    public Side getCurrentSide() {
        return currentSide;
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
            // Uno flip card values
            case FLIP: return 20;
            case DRAW_FIVE: return 20;
            case SKIP_EVERYONE: return 30;
            case WILD_DRAW_COLOR: return 60;
            default: return 0;
        }
    }

    @Override 
    public String toString(){
        if (color == Color.WILD) {
            return value.toString();
        }
        return color + " " + value + " [" + currentSide + " ]";
    }
}

