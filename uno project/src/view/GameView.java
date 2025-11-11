package view; /**
 * The view for the uno GUI
 *
 * @author Ivan Arkhipov 101310636
 * @version 1.0
 */

import controller.GameController;
import controller.GameState;
import model.Card;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public final class GameView {
    private GameController controller;

    //root panel
    private final JPanel root = new JPanel();

    //top card
    private final JLabel topCardText = new JLabel("", SwingConstants.CENTER);

    //status info
    private final JLabel currentLabel = new JLabel("Current: -");
    private final JLabel statusLabel = new JLabel("Status: -");

    //hand section
    private final JPanel handStrip = new JPanel(); // BoxLayout.X_AXIS
    private final JScrollPane handScroll = new JScrollPane(handStrip);
    private final JButton nextBtn = new JButton("Next");
    private final JButton drawBtn = new JButton("Draw model.Card");

    //scoreboard
    private final JTextArea scoreArea = new JTextArea(3, 30);

    /**
     * Constructor for the view.GameView class. Sets up panels and layout for the GUI
     */
    public GameView() {
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        //top card section
        JPanel top = titled("Top model.Card");
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
     * Get an imageicon from the card directory, and scale it to specified x and y
     * @param c The card to get icon
     * @param x the width of the scaled image
     * @param y the height of the scaled image
     * @return ImageIcon
     */
    private ImageIcon getIcon(Card c,  int x, int y) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL imageURL = null;

        if (c.getValue() == Card.Value.WILD || c.getValue() == Card.Value.WILD_DRAW_TWO) {
            imageURL = classLoader.getResource("unoCards/" + c.getValue() + "/" + c.getValue() + ".png");
        } else {
            imageURL = classLoader.getResource("unoCards/" + c.getValue() + "/" + c.getColor() + ".png");
        }

        if (imageURL != null) {
            ImageIcon icon = new ImageIcon(imageURL);
            Image image = icon.getImage();
            return new ImageIcon(image.getScaledInstance(x, y, Image.SCALE_SMOOTH));
        }

        return null;
    }

    /**
     * Get an icon that is 70% opacity and small light grey overlay
     * @param icon the icon to modify
     * @return the disabled icon
     */
    private ImageIcon getDisabledIcon(ImageIcon icon) {
        Image image = icon.getImage();

        BufferedImage buffered = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = buffered.createGraphics();

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g.drawImage(image, 0, 0, null);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g.setColor(new Color(200, 200, 200));
        g.fillRect(0, 0, buffered.getWidth(), buffered.getHeight());
        g.dispose();

        return new ImageIcon(buffered);
    }

    /**
     * Render the entire screen based on the controller.GameState object, emitted by the model
     * @param s the controller.GameState to render
     */
    public void render(GameState s) {
        //status section
        currentLabel.setText("Current Player: " + s.currentPlayer.getName() + (s.clockwise ? "  →" : "  ←"));
        statusLabel.setText("Status: ");

        if (s.topDiscard.getValue() == Card.Value.WILD || s.topDiscard.getValue() == Card.Value.WILD_DRAW_TWO) {
            statusLabel.setText("Status: WILD " + s.topDiscard.getColor());
        }

        //build hand section panel
        handStrip.removeAll();
        handStrip.add(wrapLeft(nextBtn));

        //fill the hand strip with cards from the players' hand
        List<Card> hand = s.currentPlayer.getHand();
        for (int i = 0; i < hand.size(); i++) {
            Card c = hand.get(i);
            System.out.println(c);
            JButton cardBtn = new JButton();
            cardBtn.setIcon(getIcon(c, 80, 120));
            cardBtn.setDisabledIcon(getDisabledIcon(Objects.requireNonNull(getIcon(c, 80, 120))));
            cardBtn.setSize(80, handStrip.getHeight());
            final int idx = i;
            boolean playable = s.playableIndices.contains(i);
            if (playable) cardBtn.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));

            //controller handles playing card
            cardBtn.setActionCommand("PLAY:" + idx);
            cardBtn.addActionListener(controller);
            handStrip.add(Box.createHorizontalStrut(16));
            handStrip.add(cardBtn);

            if (s.turnTaken) {
                cardBtn.setEnabled(false);
            }

            //update top card
            topCardText.setIcon(getIcon(s.topDiscard, 160, 240));
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