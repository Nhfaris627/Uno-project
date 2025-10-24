import java.util.ArrayList;
import java.util.List;

/**
 * Simple UNO game(steps 1-3 milestone 1) implementatio
 * 
 * @author Bhagya Patel 101324150
 * @version 1.0
 */
public class UnoGame {
    private List<Player> players;
    private Deck deck;
    private int currentPlayerIndex;

    /**
     * Creates game with 2-4 players
     * @param playerNames List of player names(must be 2-4)
     */
    public UnoGame(List<String> playerNames){
        //validate player count 2-4
        if (playerNames.size() < 2 || playerNames.size() > 4) {
            throw new IllegalArgumentException("Game requires 2-4 players. You provided: " + playerNames.size());
        }

        this.players = new ArrayList<>();
        for (String name : playerNames) {
            players.add(new Player(name));
        }
        
        this.deck = new Deck();
        this.currentPlayerIndex = 0;
        
        System.out.println("✓ Game created with " + players.size() + " players");
    }

    /**
     * Starts the game - deals 7 cards to each player
     */
    public void startGame() {
        System.out.println("\nDealing 7 cards to each player...\n");
        
        // Deal 7 cards to each player
        for (Player player : players) {
            for (int i = 0; i < 7; i++) {
                Card card = deck.drawCard();
                player.drawCard(card);
            }
        }
        
        System.out.println("✓ Cards dealt! Game started!");
    }

    /**
     * Displays all players and their card counts
     */
    public void displayAllPlayers() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║         CURRENT GAME STATE         ║");
        System.out.println("╚════════════════════════════════════╝");
        
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            String marker = (i == currentPlayerIndex) ? " ← CURRENT TURN" : "";
            System.out.println("  " + p + marker);
        }
        System.out.println("\nDeck: " + deck.size() + " cards remaining");
    }

    /**
     * shows current player's hand
     */
    public void displayCurrentPlayerHand() {
        Player currentPlayer = players.get(currentPlayerIndex);
        currentPlayer.displayHand();
    }

    /**
     * Current player draws a card and passes turn to next player
     */

    public void passTurn() {
        Player currentPlayer = players.get(currentPlayerIndex);
        
        // Draw a card
        Card drawnCard = deck.drawCard();
        if (drawnCard != null) {
            currentPlayer.drawCard(drawnCard);
            System.out.println("\n" + currentPlayer.getName() + " drew a card: " + drawnCard);
        } else {
            System.out.println("\n⚠ Deck is empty! Cannot draw card.");
        }
        
        // Move to next player
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        
        System.out.println("Turn passed to: " + players.get(currentPlayerIndex).getName());
    }

    /**
     * Gets current player
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    
    /**
     * Gets all players
     */
    public List<Player> getPlayers() {
        return players;
    }
    
    /**
     * Gets number of players
     */
    public int getPlayerCount() {
        return players.size();
    }
}
