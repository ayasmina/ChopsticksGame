
// ChopsticksGameGUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChopsticksGameGUI {
    private ChopsticksGame game;
    private ChopsticksAI ai;
    private boolean vsComputer;
    private int aiDifficulty;  // 1=easy, 2=medium, 3=hard

    // GUI components
    private JFrame frame;
    private HandPanel p1LeftHand, p1RightHand, p2LeftHand, p2RightHand;
    private JLabel statusLabel;
    private JButton newGameButton, splitButton;

    // Constructor
    public ChopsticksGameGUI() {
        game = new ChopsticksGame();
        ai = new ChopsticksAI();
        vsComputer = true;
        aiDifficulty = 2;  // Medium difficulty by default

        // Create and setup the GUI
        createAndShowGUI();
    }

    // Create and setup the GUI
    private void createAndShowGUI() {
        // Create main frame
        frame = new JFrame("Chopsticks Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(500, 400);

        // Create game settings panel
        JPanel settingsPanel = createSettingsPanel();
        frame.add(settingsPanel, BorderLayout.NORTH);

        // Create game board panel
        JPanel gamePanel = createGamePanel();
        frame.add(gamePanel, BorderLayout.CENTER);

        // Create status panel
        JPanel statusPanel = createStatusPanel();
        frame.add(statusPanel, BorderLayout.SOUTH);

        // Display the window
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Update the UI to match the game state
        updateUI();
    }

    // Create settings panel
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        // Game mode selection
        JRadioButton pvpButton = new JRadioButton("Player vs Player");
        JRadioButton pvcButton = new JRadioButton("Player vs Computer", true);
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(pvpButton);
        modeGroup.add(pvcButton);

        pvpButton.addActionListener(e -> vsComputer = false);
        pvcButton.addActionListener(e -> vsComputer = true);

        // Difficulty selection
        JLabel difficultyLabel = new JLabel("AI Difficulty:");
        JComboBox<String> difficultyComboBox = new JComboBox<>(
                new String[]{"Easy", "Medium", "Hard"}
        );
        difficultyComboBox.setSelectedIndex(1);  // Medium by default
        difficultyComboBox.addActionListener(e -> {
            aiDifficulty = difficultyComboBox.getSelectedIndex() + 1;
        });

        // New game button
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> resetGame());

        // Split button
        splitButton = new JButton("Split");
        splitButton.addActionListener(e -> showSplitDialog());

        // Add components to panel
        panel.add(pvpButton);
        panel.add(pvcButton);
        panel.add(difficultyLabel);
        panel.add(difficultyComboBox);
        panel.add(newGameButton);
        panel.add(splitButton);

        return panel;
    }

    // Create game panel
    private JPanel createGamePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create hand panels
        p1LeftHand = new HandPanel(1, "Left");
        p1RightHand = new HandPanel(1, "Right");
        p2LeftHand = new HandPanel(2, "Left");
        p2RightHand = new HandPanel(2, "Right");

        // Add hand panels to the game panel
        panel.add(p2LeftHand);
        panel.add(p2RightHand);
        panel.add(p1LeftHand);
        panel.add(p1RightHand);

        return panel;
    }

    // Create status panel
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        statusLabel = new JLabel("Player 1's turn");
        panel.add(statusLabel);
        return panel;
    }

    // Update the UI to match the game state
    private void updateUI() {
        // Update hand displays
        p1LeftHand.updateHand(game.getP1Left());
        p1RightHand.updateHand(game.getP1Right());
        p2LeftHand.updateHand(game.getP2Left());
        p2RightHand.updateHand(game.getP2Right());

        // Update status label
        if (game.isGameOver()) {
            statusLabel.setText("Game Over! Player " + game.getWinner() + " wins!");
        } else {
            statusLabel.setText("Player " + game.getCurrentPlayer() + "'s turn");
        }

        // Enable/disable split button based on current player
        splitButton.setEnabled(!game.isGameOver());
    }

    // Reset the game
    private void resetGame() {
        game.resetGame();
        updateUI();
    }

    // Show dialog for splitting fingers
    private void showSplitDialog() {
        if (game.isGameOver()) {
            return;
        }

        int currentPlayer = game.getCurrentPlayer();
        int totalFingers;

        if (currentPlayer == 1) {
            totalFingers = game.getP1Left() + game.getP1Right();
        } else {
            totalFingers = game.getP2Left() + game.getP2Right();
        }

        if (totalFingers <= 0) {
            JOptionPane.showMessageDialog(frame, "You don't have any fingers to split!");
            return;
        }

        String input = JOptionPane.showInputDialog(frame,
                "Enter the number of fingers for your left hand (0-" + (totalFingers) + "):",
                "Split Fingers", JOptionPane.QUESTION_MESSAGE
        );

        if (input == null) {
            return;  // User cancelled
        }

        try {
            int leftAmount = Integer.parseInt(input);
            int rightAmount = totalFingers - leftAmount;

            if (leftAmount < 0 || rightAmount < 0 || leftAmount >= 5 || rightAmount >= 5) {
                JOptionPane.showMessageDialog(frame, "Invalid split! Try again.");
                return;
            }

            boolean success = game.splitFingers(leftAmount, rightAmount);
            if (!success) {
                JOptionPane.showMessageDialog(frame, "Invalid split! Try again.");
                return;
            }

            updateUI();

            // If playing against computer and it's the computer's turn, make the computer move
            if (vsComputer && game.getCurrentPlayer() == 2 && !game.isGameOver()) {
                makeComputerMove();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid number.");
        }
    }

    // Make computer move
    private void makeComputerMove() {
        if (game.isGameOver() || game.getCurrentPlayer() != 2) {
            return;
        }

        // Add a small delay to make the computer move more natural
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] move;

                // Choose move based on difficulty
                switch (aiDifficulty) {
                    case 1:  // Easy - Random moves
                        move = ai.getRandomMove(game);
                        break;
                    case 2:  // Medium - Look ahead 2 moves
                        move = ai.getBestMove(game, 2);
                        break;
                    case 3:  // Hard - Look ahead 4 moves
                        move = ai.getBestMove(game, 4);
                        break;
                    default:
                        move = ai.getRandomMove(game);
                }

                if (move != null) {
                    game.makeMove(move[0], move[1]);
                    updateUI();
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Hand panel class
    private class HandPanel extends JPanel {
        private int player;
        private String hand;
        private int fingerCount;
        private boolean isSelected;

        public HandPanel(int player, String hand) {
            this.player = player;
            this.hand = hand;
            this.fingerCount = 1;
            this.isSelected = false;

            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setPreferredSize(new Dimension(100, 100));

            // Add mouse listener for hand selection
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleClick();
                }
            });
        }

        // Update hand display
        public void updateHand(int count) {
            this.fingerCount = count;
            repaint();
        }

        // Handle click on hand
        private void handleClick() {
            if (game.isGameOver()) {
                return;
            }

            int currentPlayer = game.getCurrentPlayer();

            // Don't allow clicking on hands for computer player if in vs computer mode
            if (vsComputer && currentPlayer == 2) {
                return;
            }

            // Don't allow clicking on hands for non-current player
            if (this.player != currentPlayer) {
                // This is a target hand
                if (HandSelectionManager.hasSelectedHand()) {
                    String targetHand = this.hand.toLowerCase();
                    String sourceHand = HandSelectionManager.getSelectedHand().toLowerCase();

                    boolean success = game.makeMove(sourceHand, targetHand);
                    if (success) {
                        HandSelectionManager.clearSelectedHand();
                        updateUI();

                        // If playing against computer and it's the computer's turn, make the computer move
                        if (vsComputer && game.getCurrentPlayer() == 2 && !game.isGameOver()) {
                            makeComputerMove();
                        }
                    }
                }
            } else {
                // This is a source hand
                if (this.fingerCount > 0) {
                    HandSelectionManager.setSelectedHand(this.hand);
                    // Update all hand panels to show selection
                    p1LeftHand.repaint();
                    p1RightHand.repaint();
                    p2LeftHand.repaint();
                    p2RightHand.repaint();
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Create a Graphics2D object for better rendering
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw background
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Draw hand outline
            if (player == game.getCurrentPlayer() && hand.equalsIgnoreCase(HandSelectionManager.getSelectedHand())) {
                // Highlight selected hand
                g2d.setColor(Color.YELLOW);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(3));
            } else {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
            }

            // Draw hand shape
            int palmX = getWidth() / 2;
            int palmY = getHeight() / 2;
            int palmSize = Math.min(getWidth(), getHeight()) / 3;

            // Draw palm
            g2d.drawOval(palmX - palmSize/2, palmY - palmSize/2, palmSize, palmSize);

            // Draw fingers
            if (fingerCount > 0) {
                double angleStep = Math.PI / (fingerCount + 1);
                double startAngle = hand.equalsIgnoreCase("Left") ? Math.PI : 0;

                for (int i = 0; i < fingerCount; i++) {
                    double angle = startAngle + angleStep * (i + 1);
                    int fingerX = (int) (palmX + Math.cos(angle) * palmSize);
                    int fingerY = (int) (palmY - Math.sin(angle) * palmSize);

                    g2d.drawLine(palmX, palmY, fingerX, fingerY);
                    g2d.fillOval(fingerX - 3, fingerY - 3, 6, 6);
                }
            }

            // Draw player and hand info
            g2d.drawString("P" + player + " " + hand + ": " + fingerCount, 5, 15);
        }
    }

    // Static class to manage hand selection
    private static class HandSelectionManager {
        private static String selectedHand = null;

        public static void setSelectedHand(String hand) {
            selectedHand = hand;
        }

        public static String getSelectedHand() {
            return selectedHand;
        }

        public static boolean hasSelectedHand() {
            return selectedHand != null;
        }

        public static void clearSelectedHand() {
            selectedHand = null;
        }
    }
    public static void main(String[] args) {
        // Use the Event Dispatch Thread for Swing applications
        SwingUtilities.invokeLater(() -> {
            new ChopsticksGameGUI();
        });
    }
}

//add a