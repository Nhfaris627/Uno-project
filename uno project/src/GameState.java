import java.util.*;

public class GameState {
    public List<Player> players;
    public Player currentPlayer;
    public Card topDiscard;
    public int deckSize;
    public List<Integer> playableIndices;
    public boolean clockwise;
    public boolean turnTaken;
}
