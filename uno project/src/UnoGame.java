import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * @author Bhagya Patel 101324150
 * @author Faris Hassan 101300683
 * @author Nicky Fang 101304731
 * @version 1.0
 */
public class UnoGame {
    private List<Player> players;
    private List<Card> discardPile;
    private Deck deck;
    private int currentPlayerIndex;
    private boolean isClockwise = true;
    private Scanner scanner = new Scanner(System.in);

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

    /**
     * Gets valid card choice from player with keyboard inputs
     * @param player Current player
     * @param scanner Scanner for keyboard input
     * @param playableCards List of playable cards
     * @return index of chosen card, or -1 to draw from deck
     */
    private int getValidCardChoice(Player player, Scanner scanner, List<Integer> playableCards) {
        int handSize = player.getHandSize();

        while(true)
        {
            System.out.println("\nChoose an action:");
            if (!playableCards.isEmpty()) {
                System.out.println("    Enter card number [0-" + (handSize - 1) + "] to play a card");
            }
            System.out.println("Enter -1 to draw from deck");
            System.out.print("Your choice: ");

            try {
                int choice = scanner.nextInt();

                if (choice == -1) {
                    // Draw from deck
                    return -1;
                }

                if (choice >= 0 && choice < handSize) {
                    if (playableCards.contains(choice)) {
                        return choice;
                    }
                    else {
                        Card chosenCard = player.getHand().get(choice);
                        Card topCard = getTopDiscardCard();
                        System.out.println("Invalid play! " + chosenCard + " cannot be played on " +
                                topCard);
                    }
                }
                else
                {
                    System.out.println("Invalid choice. Try again. Enter a number between 0 and " + (handSize - 1) +
                            " or -1 to draw from deck");
                }
            }
            catch (Exception e) {
                System.out.println("Invalid choice. Try again. Enter a number between 0 and " + (handSize - 1) +
                        " or -1 to draw from deck");
                scanner.next();
            }
        }
    }

    /**
     * Gets a list of indices of playable cards in player's hand
     * @param player Current player
     * @return Indices of playable cards
     */
    private List<Integer> getPlayableCards(Player player) {
        List<Integer> playableCards = new ArrayList<>();
        Card topCard =  getTopDiscardCard();

        if (topCard == null) {
            // No top card on discard pile, any card can be played
            for (int i = 0; i < player.getHandSize(); i++) {
                playableCards.add(i);
            }
            return playableCards;
        }

        for (int i = 0; i < player.getHandSize(); i++) {
            Card card = player.getHand().get(i);
            if (isCardPlayable(card)) {
                playableCards.add(i);
            }
        }
        return playableCards;
    }

    /**
     * Checks if card can be played on current discard pile
     * @param card Card in player's hand
     * @return true if card is playable, false otherwise
     */
    private boolean isCardPlayable(Card card){

        Card topCard =  getTopDiscardCard();

        if (topCard == null) {
            // No card in discard pile, any card is playable
            return true;
        }

        // WILD cards can always be played
        if (card.getColor() == Card.Color.WILD) {
            return true;
        }

        // Same colour cards can be played
        if (card.getColor() == topCard.getColor()) {
            return true;
        }

        // Same value cards can be played
        if (card.getValue() == topCard.getValue()) {
            return true;
        }

        // WILD cards on top of discard pile allow any card to be played
        if (topCard.getColor() == Card.Color.WILD) {
            return true;
        }

        return false;
    }

    /**
     * Formats playable cards for display
     */
    private String formatPlayableCards(List<Integer> playableCards, Player player) {
        if (playableCards.isEmpty()) {
            return "None - you must draw a card";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\nPlayable Cards:\n");
        sb.append("═══════════════════════════════════\n");

        for (int i = 0; i < playableCards.size(); i++) {
            int cardIndex = playableCards.get(i);
            sb.append("  [").append(cardIndex).append("] ").append(player.getHand()
                    .get(cardIndex)).append("\n");
        }

        sb.append("═══════════════════════════════════\n");
        sb.append("Total playable: ").append(playableCards.size()).append(" cards");
        return sb.toString();
    }

    /**
     * Handle's a player turn - allows them to play a card or draw from deck
     * @return true if game continues, false if game ends
     */
    public boolean playTurn() {
        Scanner scanner = new Scanner(System.in);
        Player currentPlayer = getCurrentPlayer();

        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║              NEW TURN              ║");
        System.out.println("╚════════════════════════════════════╝");

        // Display game state
        Card topCard = getTopDiscardCard();
        System.out.println("Top of discard pile: " + getTopDiscardCard());
        System.out.println("Deck has " + deck.size() + " cards remaining.");

        // Display current player's hand
        currentPlayer.displayHand();

        // Display playable cards
        List<Integer> playableCards = getPlayableCards(currentPlayer);
        System.out.println(formatPlayableCards(playableCards, currentPlayer));

        // Get valid card choice from player
        int cardIndex = getValidCardChoice(currentPlayer, scanner, playableCards);

        // Player chose to draw from deck
        if (cardIndex == -1) {
            Card drawnCard = deck.drawCard();
            if (drawnCard != null) {
                currentPlayer.drawCard(drawnCard);
                System.out.println("\n " + currentPlayer.getName() + " drew: " + drawnCard);

                // Check if drawn card can be played
                if (isCardPlayable(drawnCard)) {
                    System.out.println("You can play your drawn card immediately! Play it? (y/n): " );

                    try {
                        String playDrawn = scanner.next().toLowerCase();
                        if (playDrawn.equals("y")) {
                            currentPlayer.getHand().remove(drawnCard); // Remove drawn card from hand
                            discardPile.add(drawnCard);
                            System.out.println(currentPlayer.getName() + " played: " + drawnCard);

                            handleSpecialCard(drawnCard, scanner);

                            if (currentPlayer.getHand().isEmpty()) {
                                return false;
                            }
                        }
                    }
                    catch (Exception e) {
                        System.out.println("Invalid input. Please try again. (y/n)");
                    }
                }
            }
            else {
                System.out.println("\n Deck is empty! No card drawn.");
            }
        }
        else {
            // Player chose to play a card
            Card playedCard = currentPlayer.getHand().remove(cardIndex);
            discardPile.add(playedCard);
            System.out.println("\n" + currentPlayer.getName() + " played: " + playedCard);
            Player recentPlayer = currentPlayer;

            handleSpecialCard(playedCard, scanner);

            // Check if player won
            if (recentPlayer.getHand().isEmpty()) {
                return false;
            }

        }
        // Move onto next player
        advanceToNextPlayer();
        return true;
    }

    /**
     * Advance to the next player based on the direction of rotation
     */
    private void advanceToNextPlayer() {
        if (isClockwise) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } else {
            currentPlayerIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
        }
    }

    /**
     * Handle a special card if it is played.
     * @param playedCard The card most recently played
     * @param scanner Scanner object
     */
    private void handleSpecialCard(Card playedCard, Scanner scanner) {
        if (playedCard.getValue() == Card.Value.DRAW_ONE) {
            int nextPlayerIndex = (currentPlayerIndex + (isClockwise ? 1 : -1)) % players.size();

            Player nextPlayer = players.get(nextPlayerIndex);
            Card drawn = deck.drawCard();
            if (drawn != null) {
                nextPlayer.drawCard(drawn);
            }
            System.out.println("\n" + nextPlayer.getName() + " drew a card and their turn is skipped!");
            currentPlayerIndex = nextPlayerIndex;

        } else if (playedCard.getValue() == Card.Value.WILD) {
            playedCard.setColor(promptForColorChoice(scanner));

        } else if (playedCard.getValue() == Card.Value.WILD_DRAW_TWO) {
            playedCard.setColor(promptForColorChoice(scanner));

            int nextPlayerIndex = (currentPlayerIndex + (isClockwise ? 1 : -1)) % players.size();

            Player nextPlayer = players.get(nextPlayerIndex);
            Card card1 = deck.drawCard();
            Card card2 = deck.drawCard();
            if (card1 != null) nextPlayer.drawCard(card1);
            if (card2 != null) nextPlayer.drawCard(card2);

            System.out.println("\n" + nextPlayer.getName() + " drew 2 cards and their turn is skipped!");

            currentPlayerIndex = nextPlayerIndex;

        } else if (playedCard.getValue() == Card.Value.SKIP) {
            System.out.println("\n Next player's turn is skipped!");

            currentPlayerIndex = (currentPlayerIndex + (isClockwise ? 1 : -1)) % players.size();
        } else if (playedCard.getValue() == Card.Value.REVERSE) {
            isClockwise = !isClockwise;
            System.out.println("\n Direction reversed!");
        }
    }

    /**
     * Prompts the player to choose a color after a wild card is played
     * @param scanner Scanner object
     * @return Color entered by the player
     */
    private Card.Color promptForColorChoice(Scanner scanner) {
        System.out.println("\nWILD card played! Choose a color: (RED, YELLOW, GREEN, BLUE)");

        Card.Color chosenColor = null;

        while (chosenColor == null) {
            try {
                String input = scanner.next().toUpperCase().trim();
                chosenColor = Card.Color.valueOf(input);

                if (chosenColor == Card.Color.WILD) {
                    System.out.println("Cannot choose WILD as the color. Please enter RED, YELLOW, GREEN, or BLUE:");
                    chosenColor = null;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid color. Please enter RED, YELLOW, GREEN, or BLUE:");
            }
        }

        System.out.println("Color set to " + chosenColor);
        return chosenColor;
    }

    /**
     * Display the resultant state of the game
     */
    public void displayResultantState()
    {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║          RESULTANT STATE           ║");
        System.out.println("╚════════════════════════════════════╝");

        // Display current top of discard pile
        Card topCard = getTopDiscardCard();
        System.out.println("Top of discard pile: " + topCard);

        // Display deck status
        System.out.println("Cards remaining in deck: " + deck.size());

        // Display next player information
        Player nextPlayer = players.get(currentPlayerIndex);
        System.out.println("Next player: " + nextPlayer.getName());
        System.out.println("Cards in next player's hand: " + nextPlayer.getHandSize());

        System.out.print("Next player's cards: \n");
        nextPlayer.displayHand();
    }
}
