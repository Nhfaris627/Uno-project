import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the UNO game
 * 
 * @author Bhagya Patel 101324150
 * @version 1.0
 */

public class Player {

    private String name;
    private List<Card> hand;

    /**
     * Creates a player with given name
     */
    public Player(String name){
        this.name = name;
        this.hand =new ArrayList<>();
    }

    /**
     * Gets a players name
     */

    public String getName(){
        return name;
    }

    /**
     * get players hand
     */

    public List<Card> getHand(){
        return hand;
    }

    /**
     * Gets number of cards in hand
     */
    public int getHandSize() {
        return hand.size();
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
