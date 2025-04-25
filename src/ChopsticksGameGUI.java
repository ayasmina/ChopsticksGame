
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
        aiDifficulty = 3;  //medium by default

        createAndShowGUI();
    }

    private void createAndShowGUI() {
        // Create main frame
        frame = new JFrame("Chopsticks Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(600, 500);   //fix

        JPanel settingsPanel = createSettingsPanel();
        frame.add(settingsPanel, BorderLayout.NORTH);

        JPanel gamePanel = createGamePanel();
        frame.add(gamePanel, BorderLayout.CENTER);

        JPanel statusPanel = createStatusPanel();
        frame.add(statusPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        updateUI();
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JRadioButton pvpButton = new JRadioButton("Player vs Player");
        JRadioButton pvcButton = new JRadioButton("Player vs Computer", true);
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(pvpButton);
        modeGroup.add(pvcButton);

        pvpButton.addActionListener(e -> vsComputer = false);
        pvcButton.addActionListener(e -> vsComputer = true);

        JLabel difficultyLabel = new JLabel(" Difficulty lvl:");
        JComboBox<String> difficultyComboBox = new JComboBox<>(
                new String[]{"Easy", "Medium", "Hard"}
        );
        difficultyComboBox.setSelectedIndex(1);  // Medium by default
        difficultyComboBox.addActionListener(e -> {
            aiDifficulty = difficultyComboBox.getSelectedIndex() + 1;
        });

        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> resetGame());


        splitButton = new JButton("Split");
        splitButton.addActionListener(e -> showSplitDialog());


        panel.add(pvpButton);
        panel.add(pvcButton);
        panel.add(difficultyLabel);
        panel.add(difficultyComboBox);
        panel.add(newGameButton);
        panel.add(splitButton);

        return panel;
    }


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


    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        statusLabel = new JLabel("Player 1's turn");
        panel.add(statusLabel);
        return panel;
    }


    private void updateUI() {

        p1LeftHand.updateHand(game.getP1Left());
        p1RightHand.updateHand(game.getP1Right());
        p2LeftHand.updateHand(game.getP2Left());
        p2RightHand.updateHand(game.getP2Right());


        if (game.isGameOver()) {
            statusLabel.setText("Game Over! Player " + game.getWinner() + " wins!");
        } else {
            statusLabel.setText("Player " + game.getCurrentPlayer() + "'s turn");
        }


        splitButton.setEnabled(!game.isGameOver());
    }


    private void resetGame() {
        game.resetGame();
        updateUI();
    }


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
            JOptionPane.showMessageDialog(frame, "No fingers for you to split!");
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

            if (vsComputer && game.getCurrentPlayer() == 2 && !game.isGameOver()) {
                makeComputerMove();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid number.");
        }
    }

    private void makeComputerMove() {
        if (game.isGameOver() || game.getCurrentPlayer() != 2) {
            return;
        }

        Timer timer = new Timer(1000, new ActionListener() {  // Changed from 500 to 1000 ms

            @Override
            public void actionPerformed(ActionEvent e) {
                String[] move;

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

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleClick();
                }
            });
        }

        public void updateHand(int count) {
            this.fingerCount = count;
            repaint();
        }

        private void handleClick() {
            if (game.isGameOver()) {
                return;
            }

            int currentPlayer = game.getCurrentPlayer();

            if (vsComputer && currentPlayer == 2) {
                return;
            }

            if (this.player != currentPlayer) {
                if (HandSelectionManager.hasSelectedHand()) {
                    String targetHand = this.hand.toLowerCase();
                    String sourceHand = HandSelectionManager.getSelectedHand().toLowerCase();

                    boolean success = game.makeMove(sourceHand, targetHand);
                    if (success) {
                        HandSelectionManager.clearSelectedHand();
                        updateUI();

                        if (vsComputer && game.getCurrentPlayer() == 2 && !game.isGameOver()) {
                            makeComputerMove();
                        }
                    }
                }
            } else {
                if (this.fingerCount > 0) {
                    HandSelectionManager.setSelectedHand(this.hand);
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

            Graphics2D g1d = (Graphics2D )g;
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            if (player == game.getCurrentPlayer() && hand.equalsIgnoreCase(HandSelectionManager.getSelectedHand())) {
                // Highlight selected hand
                g1d.setColor(Color.GRAY);
                g1d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(Color.PINK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(3));
            } else {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
            }

            int palmX = getWidth() / 2;
            int palmY = getHeight() / 2;
            int palmSize = Math.min(getWidth(), getHeight()) / 3;

            g2d.drawOval(palmX - palmSize/2, palmY - palmSize/2, palmSize, palmSize);

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

            g2d.drawString("P" + player + " " + hand + ": " + fingerCount, 5, 15);
        }
    }

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
        SwingUtilities.invokeLater(() -> {
            new ChopsticksGameGUI();
        });
    }
}

//add a