import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> names = new ArrayList<String>();
        names.add("John");
        names.add("Billy");
        names.add("Chungus");
        UnoGame game = new UnoGame(names);

        Player john = game.getPlayers().get(0);
        Card skip = new Card(Card.Color.RED, Card.Value.SKIP);
        Card reverse = new Card(Card.Color.RED, Card.Value.REVERSE);
        Card draw_one = new Card(Card.Color.RED, Card.Value.DRAW_ONE);
        Card wild = new Card(Card.Color.WILD, Card.Value.WILD);
        Card wild_draw_two = new Card(Card.Color.WILD, Card.Value.WILD_DRAW_TWO);

        john.drawCard(skip);
        john.drawCard(reverse);
        john.drawCard(draw_one);
        john.drawCard(wild);
        john.drawCard(wild_draw_two);

        game.startGame();

        while (game.checkForGameWinner(500) == null) {
            game.playTurn();
        }
    }
}
