package controller;

import model.Card;
import model.Player;

import java.util.*;

/**
 * Represents the current state of the game including Uno Flip information.
 * This immutable snapshot is passed to views and controllers.
 *
 * @author Ivan Arkhipov 101310636
 * @author Faris Hassan 101300683
 * @version 3.0 - Added Uno Flip support
 */
public class GameState {
    public List<Player> players;
    public Player currentPlayer;
    public int currentPlayerIndex;
    public Card topDiscard;
    public int deckSize;
    public List<Integer> playableIndices;
    public boolean clockwise;
    public boolean turnTaken;
    public Card.Side currentSide;
}
