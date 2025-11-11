import javax.swing.*;
import java.awt.*;

public class MainGui {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameView view = new GameView();
            JFrame frame = new JFrame("UNO");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(view.getRoot());
            frame.pack();
            frame.setLocationRelativeTo(null);

            int count = view.promptPlayerCount(frame);
            if (count < 2 || count > 4) {
                frame.dispose();
                return;
            }

            GameModel model = new GameModel(count);
            GameController controller = new GameController(model, view);
            view.bindController(controller);

            frame.setSize(1200, 800);
            frame.setVisible(true);

            model.startGame();
        });
    }
}
