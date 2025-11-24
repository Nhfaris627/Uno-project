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
}
