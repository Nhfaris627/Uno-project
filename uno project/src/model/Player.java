package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the UNO game
 * 
 * @author Bhagya Patel 101324150
 * @author Faris Hassan 101300683
 * @author Ivan Arkhipov 101310636
 * @author Nicky Fang 101304731
 * @version 2.0 - Additional modifications to support polymorphism (AI players)
 */

public class Player {

    private String name;
    private List<Card> hand;
    private int score = 0;

    /**
     * Creates a player with given name
     */
    public Player(String name){
        this.name = name;
        this.hand = new ArrayList<>();
    }

    /**
     * Gets a players name
     * @return the name of the player
     */

    public String getName(){
        return name;
    }

    /**
     * get players hand
     * @return the players hand
     */

    public List<Card> getHand(){
        return hand;
    }

    /**
     * Gets number of cards in hand
     * @return the size of the players hand
     */
    public int getHandSize() {
        return hand.size();
    }

    /**
     * Gets the player's current score
     * @return The player's score
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Adds a card to the player's hand
     */
    public void drawCard(Card card) {
        if (card != null) {
            hand.add(card);
        }
    }

    /**
     * Adds points to the player's score
     * @param points The points to add
     */
    public void addScore(int points) {
        this.score += points;
    }

    /**
     * Calculates the total point value of all cards in hand
     * @return The sum of point values of all cards
     */
    public int calculateHandValue() {
        int total = 0;
        for (Card card : hand) {
            total += card.getPointValue();
        }
        return total;
    }

    /**
     * Flips all cards in the player's hand to their opposite side
     * Used when a FLIP card is played
     */
    public void flipHand() {
        for (Card card : hand) {
            card.flip();
        }
    }

    /**
     * Checks if this player is controlled by AI
     * @return false for human players, overridden in AIPlayer class
     * Added in version 2.0
     */
    public boolean isAI() {
        return false;
    }

    /**
     * Displays the players hand with card indices
     */

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
