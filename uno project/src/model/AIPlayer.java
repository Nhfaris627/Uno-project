package model;

import controller.GameState;
import java.util.List;
import java.util.Random;

/**
 * Represents AI-controlled player in UNO game.
 * Implements a strategy based approach to card selection based on difficulty selected
 *
 * @author Nicky Fang 101304731
 */
public class AIPlayer extends Player {

    private Random random;
    private DifficultyLevel difficulty;

    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }

    public AIPlayer(String name, DifficultyLevel difficulty) {
        super(name);
        this.random = new Random();
        this.difficulty = difficulty;
    }

    /**
     * Default constructor creates a medium difficulty AI
     * @param name The player's name
     */
    public AIPlayer(String name) {
        this(name, DifficultyLevel.MEDIUM);
    }

    /**
     * Selects card to play based on AI difficulty
     * @param state Current game state
     * @return Index of card to play, -1 if draw
     */
    public int selectCardToPlay(GameState state) {
        List<Integer> playableIndices = state.playableIndices;

        // If no playable cards, signal to draw
        if (playableIndices.isEmpty()) {
            return -1;
        }

        // Strategy based on difficulty
        switch (difficulty) {
            case EASY:
                return selectRandomCard(playableIndices);
            case MEDIUM:
                return selectWithBasicStrategy(playableIndices, state);
            default:
                return selectRandomCard(playableIndices);
        }
    }

    /**
     * Selects a random playable card
     * (EASY difficulty)
     */
    private int selectRandomCard(List<Integer> playable) {
        return playable.get(random.nextInt(playable.size()));
    }

    /**
     * Selects card using basic strategy
     * (MEDIUM difficulty)
     * Priority:
     * 1. Special cards (skip, reverse, draw)
     * 2. Cards that match color
     * 3. High-value cards (to reduce hand value)
     * 4. Any card
     */

    private int selectWithBasicStrategy(List<Integer> playableIndices, GameState state) {
        List<Card> hand = this.getHand();
        Card topCard = state.topDiscard;

        // Priority 1: Play special cards
        for (int idx : playableIndices) {
            Card card = hand.get(idx);
            if (isSpecialCard(card)) {
                return idx;
                }
        }

        // Priority 2: Match color if possible
        for (int idx : playableIndices) {
            Card card = hand.get(idx);
            if (card.getColor() == topCard.getColor() &&
                    card.getColor() != Card.Color.WILD) {
                return idx;
            }
        }

        // Priority 3: Play highest value card
        int highestValueIdx = playableIndices.get(0);
        int highestValue = hand.get(highestValueIdx).getPointValue();

        for (int idx : playableIndices) {
            int cardValue = hand.get(idx).getPointValue();
            if (cardValue > highestValue) {
                highestValue = cardValue;
                highestValueIdx = idx;
            }
        }

        return highestValueIdx;
    }

    /**
     * helper method to check if card is special
     */
    private boolean isSpecialCard(Card card) {
        Card.Value value = card.getValue();
        return value == Card.Value.SKIP ||
                value == Card.Value.REVERSE ||
                value == Card.Value.DRAW_ONE ||
                value == Card.Value.WILD ||
                value == Card.Value.WILD_DRAW_TWO ||
                value == Card.Value.FLIP ||
                value == Card.Value.DRAW_FIVE ||
                value == Card.Value.SKIP_EVERYONE ||
                value == Card.Value.WILD_DRAW_COLOR;
    }
}
