import javax.swing.*;
import java.awt.*;

public class MainGui {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameView view = new GameView();
            JFrame frame = new JFrame("UNO");

            int playercount = view.promptPlayerCount(frame);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// expose root JPanel
            frame.setContentPane(view.getRoot());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }


}
