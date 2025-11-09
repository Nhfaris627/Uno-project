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
        //add this controller to the view
        model.addListener(this);
        view.bindController(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
