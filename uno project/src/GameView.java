/**
 * The view for the uno GUI
 *
 * @author Ivan Arkhipov 101310636
 * @version 1.0
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public final class GameView {
    private GameController controller;

    //root panel
    private final JPanel root = new JPanel();

    //top card
    private final JLabel topCardText = new JLabel("-", SwingConstants.CENTER);

    //status info
    private final JLabel currentLabel = new JLabel("Current: -");
    private final JLabel statusLabel = new JLabel("Status: -");

    //hand section
    private final JPanel handStrip = new JPanel(); // BoxLayout.X_AXIS
    private final JScrollPane handScroll = new JScrollPane(handStrip);
    private final JButton nextBtn = new JButton("Next");
    private final JButton drawBtn = new JButton("Draw Card");

    //scoreboard
    private final JTextArea scoreArea = new JTextArea(3, 30);

    /**
     * Constructor for the GameView class. Sets up panels and layout for the GUI
     */
    public GameView() {
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        //top card section
        JPanel top = titled("Top Card");
        top.setLayout(new BorderLayout());
        top.add(topCardText, BorderLayout.CENTER);
        root.add(top);

        //status section
        JPanel status = titled("Status");
        status.setLayout(new GridLayout(2,1));
        status.add(currentLabel);
        status.add(statusLabel);
        root.add(status);

        //hand section
        JPanel hand = titled("Your Hand");
        hand.setLayout(new BorderLayout());
        handStrip.setLayout(new BoxLayout(handStrip, BoxLayout.X_AXIS));
        handScroll.setBorder(null);
        hand.add(handScroll, BorderLayout.CENTER);
        root.add(hand);

        //scoreboard section
        JPanel score = titled("Scoreboard");
        score.setLayout(new BorderLayout());
        scoreArea.setEditable(false);
        score.add(new JScrollPane(scoreArea), BorderLayout.CENTER);
        root.add(score);

        //set action commands for controller
        nextBtn.setActionCommand("NEXT");
        drawBtn.setActionCommand("DRAW");
    }

    /**
     * Helper method to create a titled section as per the example
     * @param title the title of the section
     * @return a JPanel object with a set title
     */
    private JPanel titled(String title) {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(title));
        return p;
    }

    /**
     * Get the root pane from the view
     * @return the root pane
     */
    public JPanel getRoot() { return root; }

    /**
     * Add the controller as an action listener
     * @param c the controller
     */
    public void bindController(GameController c) {
        this.controller = c;
        drawBtn.addActionListener(c);
        nextBtn.addActionListener(c);
    }

    /**
     * Render the entire screen based on the GameState object, emitted by the model
     * @param s the GameState to render
     */
    public void render(GameState s) {
        //top card
        topCardText.setText(s.topDiscard == null ? "-" : s.topDiscard.toString());

        //status section
        currentLabel.setText("Current Player: " + s.currentPlayer.getName() +
                " | Deck: " + s.deckSize + (s.clockwise ? "  →" : "  ←"));
        statusLabel.setText("Status: ");

        //build hand section panel
        handStrip.removeAll();
        handStrip.add(wrapLeft(nextBtn));

        //fill the hand strip with cards from the players' hand
        List<Card> hand = s.currentPlayer.getHand();
        for (int i = 0; i < hand.size(); i++) {
            Card c = hand.get(i);
            JButton cardBtn = new JButton(c.toString());
            final int idx = i;
            boolean playable = s.playableIndices.contains(i);
            if (playable) cardBtn.setBorder(BorderFactory.createLineBorder(java.awt.Color.GREEN, 2));

            //controller handles playing card
            cardBtn.setActionCommand("PLAY:" + idx);
            cardBtn.addActionListener(controller);
            handStrip.add(Box.createHorizontalStrut(16));
            handStrip.add(cardBtn);

            if (s.turnTaken) {
                cardBtn.setEnabled(false);
            }
        }

        handStrip.add(Box.createHorizontalGlue());
        handStrip.add(wrapRight(drawBtn));

        handStrip.revalidate();
        handStrip.repaint();

        //set button playability
        boolean hasPlayable = !s.playableIndices.isEmpty();
        drawBtn.setEnabled(!s.turnTaken && !hasPlayable);
        nextBtn.setEnabled(s.turnTaken);

        //scoreboard info
        StringBuilder sb = new StringBuilder("Scoreboard:\n");
        for (Player p : s.players) sb.append(p.getName()).append(": ").append(p.getScore()).append("\n");
        scoreArea.setText(sb.toString());
    }

    /**
     * Helper method to wrap a component to the left using borderLayout
     * @param c the component to wrap
     * @return the wrapped component
     */
    private JComponent wrapLeft(JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(c, BorderLayout.WEST);
        return p;
    }

    /**
     * Helper method to wrap a component to the right using borderLayout
     * @param c the component to wrap
     * @return the wrapped component
     */
    private JComponent wrapRight(JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(c, BorderLayout.EAST);
        return p;
    }

    /**
     * Create a pop up dialogue to prompt the user for the color they want to play after playing wild card
     * @return return the color chosen by the user
     */
    public Card.Color promptWildColor() {
        Object[] options = { "RED", "YELLOW", "GREEN", "BLUE" };
        int choice = JOptionPane.showOptionDialog(root, "Choose a color", "Wild",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice < 0) return Card.Color.RED;
        return Card.Color.valueOf(options[choice].toString());
    }

    /**
     * Show message to the user
     * @param msg the message to show
     */
    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(root, msg, "UNO", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Create a pop up dialogue to prompt the user for the number of players
     * @return the number of players
     */
    public int promptPlayerCount(Component parent) {
        Object[] options = {2, 3, 4};
        Object choice = JOptionPane.showInputDialog(
                parent,
                "Select number of players (2–4):",
                "UNO Setup",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        if (choice == null) return -1;
        return (Integer) choice;
    }
}