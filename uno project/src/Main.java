import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║         WELCOME TO UNO!            ║");
        System.out.println("╚════════════════════════════════════╝\n");

        // Get number of players
        int numPlayers = getPlayerCount(scanner);

        // Get player names
        List<String> playerNames = getPlayerNames(scanner, numPlayers);

        // Create and start game
        UnoGame game = new UnoGame(playerNames);
        game.startGame();

        System.out.println("\nGame is ready! First player: " + game.getCurrentPlayer().getName());
        System.out.println("Goal: First to reach 500 points wins!\n");

        // Main game loop
        playGame(game);

        // Display final scores
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║          FINAL SCORES              ║");
        System.out.println("╚════════════════════════════════════╝");
        game.displayScores();
        System.out.println("\nThanks for playing UNO!\n");

        scanner.close();
    }

    /**
     * Gets valid player count from user
     */
    private static int getPlayerCount(Scanner scanner) {
        int numPlayers = 0;
        while (numPlayers < 2 || numPlayers > 4) {
            System.out.print("Enter number of players (2-4): ");
            try {
                numPlayers = scanner.nextInt();
                scanner.nextLine();
                if (numPlayers < 2 || numPlayers > 4) {
                    System.out.println("Please enter a number between 2 and 4.\n");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.\n");
                scanner.nextLine();
            }
        }
        return numPlayers;
    }

    /**
     * Gets player names from user
     */
    private static List<String> getPlayerNames(Scanner scanner, int numPlayers) {
        List<String> names = new ArrayList<>();
        System.out.println();
        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Enter name for Player " + i + ": ");
            String name = scanner.nextLine().trim();
            names.add(name.isEmpty() ? "Player " + i : name);
        }
        return names;
    }

    /**
     * Main game loop
     */
    private static void playGame(UnoGame game) {
        while (true) {
            // Play turn
            boolean continueGame = game.playTurn();

            if (!continueGame) {
                break;
            }

            // Check for round winner (someone with 0 cards)
            for (int i = 0; i < game.getPlayers().size(); i++) {
                Player player = game.getPlayers().get(i);
                if (player.getHandSize() == 0) {
                    System.out.println("\n" + player.getName() + " wins the round!");
                    game.calculateRoundScore(i);

                    // Check for game winner
                    Player winner = game.checkForGameWinner(500);
                    if (winner != null) {
                        game.displayGameWinner(winner);
                        return;
                    }

                    System.out.println("\nRound complete. New rounds not yet implemented.");
                    return;
                }
            }

            // Display state after turn
            game.displayResultantState();
            System.out.println("\n" + "-".repeat(50));
        }
    }
}
