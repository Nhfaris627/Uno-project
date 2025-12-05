package view;
/**
 * The view for the uno GUI
 *
 * @author Ivan Arkhipov 101310636
 * @author Nicky Fang 101304731
 * @version 1.0
 */

import controller.GameController;
import controller.GameState;
import model.Card;
import model.Player;
import model.AIPlayer;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
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
    private final JButton drawBtn = new JButton("Draw Card");
    private final JButton undoBtn = new JButton("Undo");
    private final JButton redoBtn = new JButton("Redo");

    //scoreboard
    private final JTextArea scoreArea = new JTextArea(3, 30);

    /**
     * Constructor for the view.GameView class. Sets up panels and layout for the GUI
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

        undoBtn.setActionCommand("UNDO");
        redoBtn.setActionCommand("REDO");
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
        undoBtn.addActionListener(c);
        redoBtn.addActionListener(c);
    }

    private String getPath(Card c) {
        String resourcePath = "";
        Card.Value value = c.getValue();
        Card.Color color = c.getColor();
        Card.Side side = c.getCurrentSide();


        if (side == Card.Side.LIGHT) {
            resourcePath = "view/unoCards/";
        } else {
            resourcePath = "view/DarkSideCards/";
        }

        if (value == Card.Value.WILD || value == Card.Value.WILD_DRAW_TWO || value == Card.Value.WILD_DRAW_COLOR) {
            resourcePath += value + "/" + value + ".png";
        } else {
            resourcePath += value + "/" + color + ".png";
        }

        return resourcePath;
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
        String resourcePath;

        String value = c.getValue().toString().toLowerCase();
        String color = c.getColor().toString().toLowerCase();

        resourcePath = getPath(c);

        System.out.println("Trying to load: " + resourcePath);

        InputStream is = classLoader.getResourceAsStream(resourcePath);

        if (is != null) {
            ImageIcon icon = null;
            try {
                icon = new ImageIcon(ImageIO.read(is));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

        // Only show buttons for human players
        boolean isAIPlayer = s.currentPlayer.isAI();
        if (!isAIPlayer) {
            JPanel leftButtons = new JPanel();
            leftButtons.setLayout(new BoxLayout(leftButtons, BoxLayout.Y_AXIS));
            leftButtons.add(wrapLeft(nextBtn));
            leftButtons.add(Box.createVerticalStrut(5));
            leftButtons.add(wrapLeft(undoBtn));
            handStrip.add(leftButtons);
        }

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

            //disable cards for ai players or if turn is taken
            if (s.turnTaken || isAIPlayer) {
                cardBtn.setEnabled(false);
            }

            //update top card
            topCardText.setIcon(getIcon(s.topDiscard, 160, 240));
        }

        handStrip.add(Box.createHorizontalGlue());

        // only show draw button for human players
        if (!isAIPlayer) {
            JPanel rightButtons = new JPanel();
            rightButtons.setLayout(new BoxLayout(rightButtons, BoxLayout.Y_AXIS));
            rightButtons.add(wrapRight(drawBtn));
            rightButtons.add(Box.createVerticalStrut(5));
            rightButtons.add(wrapRight(redoBtn));
            handStrip.add(rightButtons);
        }

        handStrip.revalidate();
        handStrip.repaint();

        //set button playability - only matters for human players
        if (!isAIPlayer) {
            boolean hasPlayable = !s.playableIndices.isEmpty();
            drawBtn.setEnabled(!s.turnTaken && !hasPlayable);
            nextBtn.setEnabled(s.turnTaken);
        }

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
        if (choice < 0) return null;
        return Card.Color.valueOf(options[choice].toString());
    }

    /**
     * Create a pop up dialogue to prompt the user for the color they want to play after playing a dark wild card
     * @return return the color chosen by the user
     */
    public Card.Color promptDarkWildColor() {
        Object[] options = { "ORANGE", "PINK", "PURPLE", "TEAL" };
        int choice = JOptionPane.showOptionDialog(root, "Choose a color", "Wild",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice < 0) return null;
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

    /**
     * prompt user to select which players are human vs AI
     * @param parent parent component
     * @param playerCount a,mount of players in game
     * @return Array where true = AI, false = Human, or null for error
     */
    public boolean[] promptPlayerTypes(Component parent, int playerCount) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel instruction = new JLabel("Select player types:");
        instruction.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(instruction);
        panel.add(Box.createVerticalStrut(10));

        JCheckBox[] checkboxes = new JCheckBox[playerCount];

        for (int i = 0; i < playerCount; i++) {
            checkboxes[i] = new JCheckBox("Player " + (i + 1) + " is AI");
            checkboxes[i].setAlignmentX(Component.LEFT_ALIGNMENT);

            // player 1 is human, others AI
            if (i > 0) {
                checkboxes[i].setSelected(true);
            }

            panel.add(checkboxes[i]);
            panel.add(Box.createVerticalStrut(5));
        }

        int result = JOptionPane.showConfirmDialog(
                parent,
                panel,
                "Player Setup",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        boolean[] isAI = new boolean[playerCount];
        for (int i = 0; i < playerCount; i++) {
            isAI[i] = checkboxes[i].isSelected();
        }

        return isAI;
    }

    /**
     * promopt user to select AI difficulty
     * @param parent Parent component
     * @return Selected difficulty level, or MEDIUM if cancelled
     */
    public AIPlayer.DifficultyLevel promptAIDifficulty(Component parent) {
        Object[] options = {"Easy", "Medium", "Hard"};
        int choice = JOptionPane.showOptionDialog(
                parent,
                "Select AI difficulty:",
                "AI Difficulty",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]); // default medium

        switch (choice) {
            case 0: return AIPlayer.DifficultyLevel.EASY;
            case 2: return AIPlayer.DifficultyLevel.HARD;
            default: return AIPlayer.DifficultyLevel.MEDIUM;
        }
    }

    public void setRedoEnabled(boolean enabled) {
    }

    public void setUndoEnabled(boolean enabled) {

    }

    /**
     * prompts the user to play again after a game win
     * @param winner the player who won
     * @return true if the user wants to play again, false to quit
     */
    public boolean promptPlayAgain(Player winner) {
        int choice = JOptionPane.showConfirmDialog(
                root,
                "🎉 " + winner.getName() + " WINS THE GAME WITH " + winner.getScore() + " POINTS! 🎉\n\nDo you want to play another game?",
                "Game Over",
                JOptionPane.YES_NO_OPTION
        );
        return choice == JOptionPane.YES_OPTION;
    }
}