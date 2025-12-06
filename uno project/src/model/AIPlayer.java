package model;

import controller.GameState;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Random;

/**
 * Represents AI controlled player in UNO game with serialization support
 * Implements a strategy based approach to card selection based on difficulty selected
 *
 * @author Nicky Fang 101304731
 * @version 2.0 - Added Serializable for Milestone 4
 */
public class AIPlayer extends Player {

    private static final long serialVersionUID = 1L;

    private transient Random random; // Transient because Random is not reliably serializable
    private DifficultyLevel difficulty;

    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }

    public AIPlayer(String name, DifficultyLevel difficulty) {
        super(name);
        this.random = new Random();
        this.difficulty = difficulty;
    }

    public AIPlayer(String name) {
        this(name, DifficultyLevel.MEDIUM);
    }

    /**
     * Reinitialize transient Random field after deserialization
     */
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.random = new Random(); // Reinitialize after deserialization
    }

    public int selectCardToPlay(GameState state) {
        List<Integer> playableIndices = state.playableIndices;

        if (playableIndices.isEmpty()) {
            return -1;
        }

        switch (difficulty) {
            case EASY:
                return selectRandomCard(playableIndices);
            case MEDIUM:
                return selectWithBasicStrategy(playableIndices, state);
            case HARD:
                return selectWithAdvancedStrategy(playableIndices, state);
            default:
                return selectRandomCard(playableIndices);
        }
    }

    private int selectRandomCard(List<Integer> playable) {
        if (random == null) random = new Random(); // Safety check
        return playable.get(random.nextInt(playable.size()));
    }

    private int selectWithBasicStrategy(List<Integer> playableIndices, GameState state) {
        List<Card> hand = this.getHand();
        Card topCard = state.topDiscard;

        for (int idx : playableIndices) {
            Card card = hand.get(idx);
            if (isSpecialCard(card)) {
                return idx;
            }
        }

        for (int idx : playableIndices) {
            Card card = hand.get(idx);
            if (card.getColor() == topCard.getColor() &&
                    card.getColor() != Card.Color.WILD) {
                return idx;
            }
        }

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

    private int selectWithAdvancedStrategy(List<Integer> playableIndices, GameState state) {
        List<Card> hand = this.getHand();
        Card topCard = state.topDiscard;

        Player nextPlayer = getNextPlayer(state);
        boolean nextPlayerLowCards = (nextPlayer != null && nextPlayer.getHandSize() <= 2);

        if (nextPlayerLowCards) {
            for (int idx : playableIndices) {
                Card card = hand.get(idx);
                Card.Value value = card.getValue();
                if (value == Card.Value.DRAW_ONE ||
                        value == Card.Value.DRAW_FIVE ||
                        value == Card.Value.WILD_DRAW_TWO ||
                        value == Card.Value.WILD_DRAW_COLOR ||
                        value == Card.Value.SKIP ||
                        value == Card.Value.SKIP_EVERYONE) {
                    return idx;
                }
            }
        }

        boolean hasNonWildPlayable = false;
        for (int idx : playableIndices) {
            if (hand.get(idx).getColor() != Card.Color.WILD) {
                hasNonWildPlayable = true;
                break;
            }
        }

        if (hasNonWildPlayable) {
            List<Integer> nonWildIndices = new java.util.ArrayList<>();
            for (int idx : playableIndices) {
                if (hand.get(idx).getColor() != Card.Color.WILD) {
                    nonWildIndices.add(idx);
                }
            }
            return selectWithBasicStrategy(nonWildIndices, state);
        }

        return selectWithBasicStrategy(playableIndices, state);
    }

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

    private Player getNextPlayer(GameState state) {
        int currentIdx = state.players.indexOf(state.currentPlayer);
        int nextIdx;

        if (state.clockwise) {
            nextIdx = (currentIdx + 1) % state.players.size();
        } else {
            nextIdx = (currentIdx - 1 + state.players.size()) % state.players.size();
        }

        return state.players.get(nextIdx);
    }

    public Card.Color chooseWildColor() {
        if (random == null) random = new Random(); // Safety check

        List<Card> hand = this.getHand();
        int[] colorCounts = new int[4];

        for (Card card : hand) {
            switch (card.getColor()) {
                case RED:
                    colorCounts[0]++;
                    break;
                case BLUE:
                    colorCounts[1]++;
                    break;
                case GREEN:
                    colorCounts[2]++;
                    break;
                case YELLOW:
                    colorCounts[3]++;
                    break;
            }
        }

        int maxCount = 0;
        int maxIndex = 0;
        for (int i = 0; i < 4; i++) {
            if (colorCounts[i] > maxCount) {
                maxCount = colorCounts[i];
                maxIndex = i;
            }
        }

        if (maxCount == 0) {
            maxIndex = random.nextInt(4);
        }

        Card.Color[] colors = {Card.Color.RED, Card.Color.BLUE,
                Card.Color.GREEN, Card.Color.YELLOW};
        return colors[maxIndex];
    }

    public Card.Color chooseWildDrawColor() {
        if (random == null) random = new Random(); // Safety check

        List<Card> hand = this.getHand();
        int[] colorCounts = new int[4];

        for (Card card : hand) {
            switch (card.getColor()) {
                case TEAL:
                    colorCounts[0]++;
                    break;
                case PURPLE:
                    colorCounts[1]++;
                    break;
                case PINK:
                    colorCounts[2]++;
                    break;
                case ORANGE:
                    colorCounts[3]++;
                    break;
            }
        }

        int maxCount = 0;
        int maxIndex = 0;
        for (int i = 0; i < 4; i++) {
            if (colorCounts[i] > maxCount) {
                maxCount = colorCounts[i];
                maxIndex = i;
            }
        }

        if (maxCount == 0) {
            maxIndex = random.nextInt(4);
        }

        Card.Color[] colors = {Card.Color.TEAL, Card.Color.PURPLE,
                Card.Color.PINK, Card.Color.ORANGE};
        return colors[maxIndex];
    }

    @Override
    public boolean isAI() {
        return true;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficulty;
    }
}