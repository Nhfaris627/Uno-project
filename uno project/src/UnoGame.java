import java.util.ArrayList;
import java.util.List;

/**
 * Simple UNO game(steps 1-3 milestone 1) implementatio
 * 
 * @author Bhagya Patel 101324150
 * @author Faris Hassan 101300683
 * @version 1.0
 */
public class UnoGame {
    private List<Player> players;
    private List<Card> discardPile;
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
        this.discardPile = new ArrayList<>();
        
        System.out.println("✓ Game created with " + players.size() + " players");
    }

    /**
     * Starts the game - deals 7 cards to each player
     */
    public void startGame() {
        System.out.println("\nDealing 7 cards to each player...\n");

        Card firstCard = deck.drawCard();
        discardPile.add(deck.drawCard());
        System.out.println("Starting discard pile: " + getTopDiscardCard());
        System.out.println("───────────────────────────────");
        
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
     * Display a specific player's hand
     * @param player The player whose hand to display
     */
    private void displayPlayerHand(Player player) {
        System.out.print("  Cards: ");
        List<Card> hand = player.getHand();
        for (int i = 0; i < hand.size(); i++) {
            System.out.print(hand.get(i));
            if (i < hand.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    /**
     * Current player draws a card and passes turn to next player
     */

    public void passTurn() {
        Player currentPlayer = players.get(currentPlayerIndex);

        Card drawnCard = deck.drawCard();

        if (drawnCard != null) {
            currentPlayer.drawCard(drawnCard);
            System.out.println("\n" + currentPlayer.getName() + " drew a card: " + drawnCard);

            // Simulate playing it to discard pile for observation
            discardPile.add(drawnCard);
            System.out.println("Card added to discard pile: " + drawnCard);
        }
        
        // Draw a card
        if (drawnCard != null) {
            currentPlayer.drawCard(drawnCard);
            System.out.println("\n" + currentPlayer.getName() + " drew a card: " + drawnCard);
        } else {
            System.out.println("\n⚠ Deck is empty! Cannot draw card.");
        }
        
        // Move to next player
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        Player nextPlayer = players.get(currentPlayerIndex);

        System.out.println("\n--- RESULTANT STATE ---");
        System.out.println("Top of discard pile: " + getTopDiscardCard());
        System.out.println("Next player: " + nextPlayer.getName());
        displayPlayerHand(nextPlayer);

    }

    /**
     * gets the card at the top of the discard pile
     * @return the card at the top of the discard pile
     */
    public Card getTopDiscardCard() {
        if (discardPile.isEmpty()) return null;
        return discardPile.get(discardPile.size() - 1);
    }

    /**
     * Calculates and awards points when a player wins a round
     * The winner gets points equal to the sum of all cards in opponents' hands
     * @param winnerIndex The index of the winning player
     */
    public void calculateRoundScore(int winnerIndex) {
        Player winner = players.get(winnerIndex);
        int totalPoints = 0;

        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║       ROUND SCORING RESULTS        ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println("\nWinner: " + winner.getName() + "\n");

        // calculate points from all opponents hands
        for(int i = 0; i < players.size(); i++) {
            if (i != winnerIndex) {
                Player opponent = players.get(i);
                int handValue = opponent.calculateHandValue();
                totalPoints += handValue;

                System.out.println(opponent.getName() + "'s hand value: " + handValue + " points");
                displayPlayerHand(opponent);
            }
        }
        winner.addScore(totalPoints);
        System.out.println("\n" + winner.getName() + " receives " + totalPoints + " points!");
        System.out.println(winner.getName() + "'s total score: " + winner.getScore());
    }


    /**
     * Displays the current scores of all players
     */
    public void displayScores() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║         CURRENT SCORES             ║");
        System.out.println("╚════════════════════════════════════╝");

        for (Player player : players) {
            System.out.printf("  %-15s: %d points\n", player.getName(), player.getScore());
        }
    }

    /**
     * Determines if any player has reached the winning score
     * @param targetScore The score needed to win the game (typically 500)
     * @return The winning player, or null if no one has won yet
     */
    public Player checkForGameWinner(int targetScore) {
        for (Player player : players) {
            if (player.getScore() >= targetScore) {
                return player;
            }
        }
        return null;
    }

    /**
     * Displays the final game winner
     * @param winner The player who won the game
     */
    public void displayGameWinner(Player winner) {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║         GAME OVER!                 ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println("\n🎉 " + winner.getName() + " WINS THE GAME! 🎉");
        System.out.println("Final Score: " + winner.getScore() + " points\n");
        displayScores();
    }

    /**
     * Gets current player
     * @return the index of current player
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    
    /**
     * Gets all players
     * @return the list of players
     */
    public List<Player> getPlayers() {
        return players;
    }
    
    /**
     * Gets number of players
     * @return the number of players in the game
     */
    public int getPlayerCount() {
        return players.size();
    }
}
