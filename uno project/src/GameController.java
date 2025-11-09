import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * COntroller for uno game implementing MVC pattern
 * Mediates between GameModel and GameView
 * Handles user input via ActionListener and updates the view via GameModelListener
 *
 * @author Nicky Fang 101304731
 *
 */
public class GameController implements ActionListener, GameModelListener {

    //
    private GameModel model;
    private GameView view;

    /**
     *
     * @param model The game model
     * @param view The game view
     */
    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;

        //add this controller to the model

        model.addListener(this);

        //add this controller to the view
        view.bindController(this);
    }

    /**
     * Handles button clicks and other actions from the view
     * Sends commands to appropriate handler method
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        try {
            if (command.startsWith("PLAY:"))
            {
                //extract card index from command
                int cardIndex = Integer.parseInt(command.substring(5));
                onPlayCard(cardIndex);
            }
        }

    }

    private void onPlayCard(int cardIndex) {
        //get game state
        GameState gameState = model.getGameState();

    }
}
