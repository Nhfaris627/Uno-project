package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the UNO game with serialization support
 *
 * @author Bhagya Patel 101324150
 * @author Faris Hassan 101300683
 * @author Ivan Arkhipov 101310636
 * @author Nicky Fang 101304731
 * @version 3.0 - Added Serializable for Milestone 4
 */
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private List<Card> hand;
    private int score = 0;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public int getHandSize() {
        return hand.size();
    }

    public int getScore() {
        return score;
    }

    public void drawCard(Card card) {
        if (card != null) {
            hand.add(card);
        }
    }

    public void addScore(int points) {
        this.score += points;
    }

    public void setScore(int points) {
        this.score = points;
    }

    public int calculateHandValue() {
        int total = 0;
        for (Card card : hand) {
            total += card.getPointValue();
        }
        return total;
    }

    public void flipHand() {
        for (Card card : hand) {
            card.flip();
        }
    }

    public boolean isAI() {
        return false;
    }

    public void displayHand() {
        System.out.println("\n" + name + "'s Hand:");
        System.out.println("═══════════════════════════════════");

        if (hand.isEmpty()) {
            System.out.println("  (No cards)");
        } else {
            for (int i = 0; i < hand.size(); i++) {
                System.out.printf("  [%d] %s\n", i, hand.get(i));
            }
        }
        System.out.println("═══════════════════════════════════");
        System.out.println("Total cards: " + hand.size());
    }

    @Override
    public String toString() {
        return name + " (" + hand.size() + " cards)";
    }
}