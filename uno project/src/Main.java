import controller.GameController;
import model.GameModel;
import view.GameView;
import model.Player;
import model.AIPlayer;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameView view = new GameView();
            JFrame frame = new JFrame("UNO FLIP");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(view.getRoot());
            frame.pack();
            frame.setLocationRelativeTo(null);

            // get number of players
            int count = view.promptPlayerCount(frame);
                frame.dispose();
                if (count < 2 || count > 4) {
                return;
            }

            // get player types (either human or ai)
            boolean[] isAI = view.promptPlayerTypes(frame, count);
            if (isAI == null) {
                frame.dispose();
                return;
            }

            // check if AI players exist
            boolean hasAI = false;
            for (boolean ai : isAI) {
                if (ai) {
                    hasAI = true;
                    break;
                }
            }

            // if there are AI players, ask for difficulty
            AIPlayer.DifficultyLevel difficulty = AIPlayer.DifficultyLevel.MEDIUM;
            if (hasAI) {
                difficulty = view.promptAIDifficulty(frame);
            }

            GameModel model = new GameModel(count, isAI, difficulty);
            GameController controller = new GameController(model, view);
            view.bindController(controller);

            frame.setSize(1200, 800);
            frame.setVisible(true);

            model.startGame();
        });
    }
}
