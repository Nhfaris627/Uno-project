package model;

import java.io.Serializable;

/**
 * Represents a single UNO card with serialization support
 *
 * @author Bhagya Patel 101324150
 * @author Faris Hassan 101300683
 * @author Ivan Arkhipov 101310636
 * @author Nicky Fang 101304731
 * @version 2.0 - Added Serializable for Milestone 4
 */
public class Card implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Color {
        RED, BLUE, GREEN, YELLOW, WILD,
        TEAL, PURPLE, PINK, ORANGE
    }

    public enum Value {
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

    private final Color lightColor;
    private final Value lightValue;
    private final Color darkColor;
    private final Value darkValue;

    public Card(Color color, Value value) {
        this(color, value, color, value, Side.LIGHT);
    }

    public Card(Color lightColor, Value lightValue, Color darkColor, Value darkValue, Side initialSide) {
        this.lightColor = lightColor;
        this.lightValue = lightValue;
        this.darkColor = darkColor;
        this.darkValue = darkValue;
        this.currentSide = initialSide;

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

    public Side getCurrentSide() {
        return currentSide;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Value getValue() {
        return value;
    }

    public Color getLightColor() {
        return lightColor;
    }

    public Value getLightValue() {
        return lightValue;
    }

    public Color getDarkColor() {
        return darkColor;
    }

    public Value getDarkValue() {
        return darkValue;
    }

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
            case FLIP: return 20;
            case DRAW_FIVE: return 20;
            case SKIP_EVERYONE: return 30;
            case WILD_DRAW_COLOR: return 60;
            default: return 0;
        }
    }

    @Override
    public String toString() {
        if (color == Color.WILD) {
            return value.toString();
        }
        return color + " " + value + " [" + currentSide + " ]";
    }
}