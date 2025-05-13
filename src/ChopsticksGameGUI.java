
import javax.swing.*;
        import java.awt.*;
        import java.awt.event.*;
        import java.awt.geom.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Random;

public class ChopsticksGameGUI {
    private ChopsticksGame game;
    private Minmax ai;
    private boolean vsComputer;
    private int aiDifficulty;


    private JFrame frame;
    private HandPanel p1LeftHand, p1RightHand, p2LeftHand, p2RightHand;
    private JLabel statusLabel;
    private JButton newGameButton;



    private Timer animationTimer;
    private float animationAlpha = 0.0f;
    private boolean animationIncreasing = true;


    private static final Color BACKGROUND_COLOR = new Color(25, 35, 60);
    private static final Color PANEL_COLOR = new Color(40, 50, 80);
    private static final Color ACCENT_COLOR = new Color(65, 165, 245);
    private static final Color BRIGHT_ACCENT = new Color(100, 210, 255);
    private static final Color TEXT_COLOR = new Color(230, 240, 255);
    private static final Color SELECTION_COLOR = new Color(90, 180, 230);



    public ChopsticksGameGUI() {
        game = new ChopsticksGame();
        ai = new Minmax();
        vsComputer = true;
        aiDifficulty = 2;


        createAndShowGUI();
        startAnimationTimer();
    }


    private void startAnimationTimer() {
        animationTimer = new Timer(50, e -> {
            if (animationIncreasing) {
                animationAlpha += 0.05f;
                if (animationAlpha >= 1.0f) {
                    animationAlpha = 1.0f;
                    animationIncreasing = false;
                }
            } else {
                animationAlpha -= 0.05f;
                if (animationAlpha <= 0.3f) {
                    animationAlpha = 0.3f;
                    animationIncreasing = true;
                }
            }


            p1LeftHand.repaint();
            p1RightHand.repaint();
            p2LeftHand.repaint();
            p2RightHand.repaint();
        });
        animationTimer.start();
    }


    private void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }


        frame = new JFrame("Chopsticks Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setSize(700, 600);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);


        JPanel settingsPanel = createSettingsPanel();
        frame.add(settingsPanel, BorderLayout.NORTH);


        JPanel gamePanel = createGamePanel();
        frame.add(gamePanel, BorderLayout.CENTER);


        JPanel statusPanel = createStatusPanel();
        frame.add(statusPanel, BorderLayout.SOUTH);


        applyGameStyle(frame);


        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        updateUI();
    }


    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JRadioButton pvpButton = new JRadioButton("Player vs Player");
        JRadioButton pvcButton = new JRadioButton("Player vs Computer", true);


        pvpButton.setForeground(TEXT_COLOR);
        pvcButton.setForeground(TEXT_COLOR);
        pvpButton.setBackground(PANEL_COLOR);
        pvcButton.setBackground(PANEL_COLOR);
        pvpButton.setFocusPainted(false);
        pvcButton.setFocusPainted(false);


        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(pvpButton);
        modeGroup.add(pvcButton);


        pvpButton.addActionListener(e -> vsComputer = false);
        pvcButton.addActionListener(e -> vsComputer = true);


        JLabel difficultyLabel = new JLabel(" Difficulty:");
        difficultyLabel.setForeground(TEXT_COLOR);


        JComboBox<String> difficultyComboBox = new JComboBox<>(
                new String[]{"Easy", "Medium", "Hard"}
        );
        difficultyComboBox.setSelectedIndex(1);
        difficultyComboBox.setBackground(ACCENT_COLOR);
        difficultyComboBox.setForeground(Color.WHITE);
        ((JLabel)difficultyComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);


        difficultyComboBox.addActionListener(e -> {
            aiDifficulty = difficultyComboBox.getSelectedIndex() + 1;
        });


        newGameButton = new JButton("New Game");
        newGameButton.setBackground(ACCENT_COLOR);
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setFocusPainted(false);
        newGameButton.setBorder(BorderFactory.createRaisedBevelBorder());
        newGameButton.setCursor(new Cursor(Cursor.HAND_CURSOR));


        newGameButton.addActionListener(e -> resetGame());


        panel.add(pvpButton);
        panel.add(pvcButton);
        panel.add(difficultyLabel);
        panel.add(difficultyComboBox);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(newGameButton);


        return panel;
    }


    private JPanel createGamePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(BACKGROUND_COLOR);


        p1LeftHand = new HandPanel(1, "Left");
        p1RightHand = new HandPanel(1, "Right");
        p2LeftHand = new HandPanel(2, "Left");
        p2RightHand = new HandPanel(2, "Right");


        panel.add(p2LeftHand);
        panel.add(p2RightHand);
        panel.add(p1LeftHand);
        panel.add(p1RightHand);


        return panel;
    }


    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        statusLabel = new JLabel("Player 1's turn");
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));


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
            statusLabel.setForeground(BRIGHT_ACCENT);
        } else {
            statusLabel.setText("Player " + game.getCurrentPlayer() + "'s turn");
            statusLabel.setForeground(TEXT_COLOR);
        }
    }




    private void resetGame() {
        game.resetGame();
        updateUI();
    }


    private void makeComputerMove() {
        if (game.isGameOver() || game.getCurrentPlayer() != 2) return;

        statusLabel.setText("Computer is thinking...");

        Timer timer = new Timer(1000, e -> {
            String[] move;
            switch (aiDifficulty) {
                case 1 -> move = ai.getRandomMove(game);
                case 2 -> move = ai.getBestMove(game, 2);
                case 3 -> move = ai.getBestMove(game, 4);
                default -> move = ai.getRandomMove(game);
            }

            if (move != null) {
                game.makeMove(move[0], move[1]);
                updateUI();
                animateMove(move[0], move[1]);
            } else {
                // AI has no valid moves - player wins!
                game.checkWinner(); // Force winner check
                updateUI();
                if (game.isGameOver() && game.getWinner() == 1) {
                    showConfettiPopup();
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showConfettiPopup() {
        JDialog popup = new JDialog(frame, "Victory!", true);
        popup.setLayout(new BorderLayout());
        popup.setSize(400, 300);
        popup.setLocationRelativeTo(frame);
        popup.getContentPane().setBackground(BACKGROUND_COLOR);

        JLabel message = new JLabel("You Win! AI has no valid moves!", SwingConstants.CENTER);
        message.setFont(new Font("Segoe UI", Font.BOLD, 20));
        message.setForeground(BRIGHT_ACCENT);
        popup.add(message, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.setBackground(ACCENT_COLOR);
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> popup.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.add(closeButton);
        popup.add(buttonPanel, BorderLayout.SOUTH);

        // Create confetti panel
        ConfettiPanel confettiPanel = new ConfettiPanel();
        popup.add(confettiPanel, BorderLayout.NORTH);

        popup.setVisible(true);
    }


    private class ConfettiPanel extends JPanel {
        private final int NUM_CONFETTI = 100;
        private final Confetti[] confetti = new Confetti[NUM_CONFETTI];
        private final Random random = new Random();
        private Timer animationTimer;

        public ConfettiPanel() {
            setPreferredSize(new Dimension(400, 150));
            setBackground(new Color(0, 0, 0, 0)); // Transparent

            for (int i = 0; i < NUM_CONFETTI; i++) {
                confetti[i] = new Confetti(
                        random.nextInt(getWidth()),
                        random.nextInt(getHeight()),
                        random.nextInt(10) + 5,
                        random.nextInt(5) + 1,
                        new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0.7f)
                );
            }


            animationTimer = new Timer(30, e -> {
                for (Confetti c : confetti) {
                    c.update();
                }
                repaint();
            });
            animationTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (Confetti c : confetti) {
                g2d.setColor(c.color);
                g2d.fillRect(c.x, c.y, c.size, c.size);
            }
        }

        private class Confetti {
            int x, y;
            final int size;
            final int speed;
            final Color color;
            int direction = 1;

            public Confetti(int x, int y, int size, int speed, Color color) {
                this.x = x;
                this.y = y;
                this.size = size;
                this.speed = speed;
                this.color = color;
            }

            public void update() {
                y += speed;
                x += direction;

                if (y > getHeight()) {
                    y = -size;
                    x = random.nextInt(getWidth());
                }

                if (x < 0 || x > getWidth()) {
                    direction *= -1;
                }
            }
        }
    }


    private void animateMove(String sourceHand, String targetHand) {
        HandPanel source = getHandPanelByName(sourceHand);
        HandPanel target = getHandPanelByName(targetHand);


        if (source != null && target != null) {
            source.startMoveAnimation();


            Timer delayTimer = new Timer(300, e -> {
                if (target != null) {
                    target.startMoveAnimation();
                }
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        }
    }


    private HandPanel getHandPanelByName(String handName) {
        if (handName.equalsIgnoreCase("left")) {
            return game.getCurrentPlayer() == 1 ? p1LeftHand : p2LeftHand;
        } else if (handName.equalsIgnoreCase("right")) {
            return game.getCurrentPlayer() == 1 ? p1RightHand : p2RightHand;
        }
        return null;
    }


    private void applyGameStyle(Component component) {
        if (component instanceof JComponent jc) {
            jc.setBackground(BACKGROUND_COLOR);
            jc.setForeground(TEXT_COLOR);
            jc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }


        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyGameStyle(child);
            }
        }
    }


    private class HandPanel extends JPanel {
        private int player;
        private String hand;
        private int fingerCount;
        private boolean isMoving = false;
        private Timer moveAnimationTimer;
        private float moveAnimationAlpha = 0.0f;


        public HandPanel(int player, String hand) {
            this.player = player;
            this.hand = hand;
            this.fingerCount = 1;


            setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2, true));
            setPreferredSize(new Dimension(150, 150));
            setBackground(PANEL_COLOR);
            setCursor(new Cursor(Cursor.HAND_CURSOR));


            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isMoving && canInteract()) {
                        setBorder(BorderFactory.createLineBorder(BRIGHT_ACCENT, 3, true));
                        repaint();
                    }
                }


                @Override
                public void mouseExited(MouseEvent e) {
                    if (!isMoving) {
                        setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2, true));
                        repaint();
                    }
                }


                @Override
                public void mouseClicked(MouseEvent e) {
                    handleClick();
                }
            });
        }


        private boolean canInteract() {
            if (game.isGameOver()) return false;
            int currentPlayer = game.getCurrentPlayer();
            if (vsComputer && currentPlayer == 2) return false;


            if (this.player == currentPlayer) {
                return fingerCount > 0;
            } else {
                return HandSelectionManager.hasSelectedHand() && fingerCount >= 0;
            }
        }


        public void updateHand(int count) {
            this.fingerCount = count;
            repaint();
        }


        public void startMoveAnimation() {
            isMoving = true;
            moveAnimationAlpha = 0.0f;


            if (moveAnimationTimer != null && moveAnimationTimer.isRunning()) {
                moveAnimationTimer.stop();
            }


            moveAnimationTimer = new Timer(30, e -> {
                moveAnimationAlpha += 0.1f;
                if (moveAnimationAlpha >= 1.0f) {
                    moveAnimationAlpha = 0.0f;
                    isMoving = false;
                    ((Timer)e.getSource()).stop();
                }
                repaint();
            });
            moveAnimationTimer.start();
        }


        private void handleClick() {
            if (game.isGameOver() || isMoving) return;

            int currentPlayer = game.getCurrentPlayer();

            if (vsComputer && currentPlayer == 2) return;

            if (this.player != currentPlayer) {
                if (HandSelectionManager.hasSelectedHand()) {
                    String sourceHand = HandSelectionManager.getSelectedHand().toLowerCase();
                    String targetHand = this.hand.toLowerCase();

                    boolean success = game.makeMove(sourceHand, targetHand);
                    if (success) {
                        HandPanel source = getHandPanelByName(sourceHand);
                        if (source != null) {
                            source.startMoveAnimation();
                        }

                        Timer delayTimer = new Timer(300, evt -> {
                            startMoveAnimation();
                            HandSelectionManager.clearSelectedHand();
                            updateUI();

                            // Check if player just won by leaving AI with no moves
                            if (vsComputer && game.isGameOver() && game.getWinner() == 1) {
                                showConfettiPopup();
                            } else if (vsComputer && game.getCurrentPlayer() == 2 && !game.isGameOver()) {
                                makeComputerMove();
                            }
                        });
                        delayTimer.setRepeats(false);
                        delayTimer.start();
                    }
                }
            } else {
                if (this.fingerCount > 0) {
                    HandSelectionManager.setSelectedHand(this.hand);
                    repaintAllHands();
                }
            }
        }


        private void repaintAllHands() {
            p1LeftHand.repaint();
            p1RightHand.repaint();
            p2LeftHand.repaint();
            p2RightHand.repaint();
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);



            g2d.setColor(PANEL_COLOR);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);



            if (player == game.getCurrentPlayer() &&
                    hand.equalsIgnoreCase(HandSelectionManager.getSelectedHand())) {
                g2d.setColor(new Color(90, 180, 230, (int)(100 + 155 * animationAlpha)));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(BRIGHT_ACCENT);
                g2d.setStroke(new BasicStroke(3));
            }



            if (isMoving) {
                float alpha = Math.min(1.0f, moveAnimationAlpha * 2);
                if (moveAnimationAlpha > 0.5f) {
                    alpha = 1.0f - ((moveAnimationAlpha - 0.5f) * 2);
                }


                Color flashColor = new Color(
                        BRIGHT_ACCENT.getRed(),
                        BRIGHT_ACCENT.getGreen(),
                        BRIGHT_ACCENT.getBlue(),
                        (int)(alpha * 180)
                );


                g2d.setColor(flashColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }


            int palmX = getWidth() / 2;
            int palmY = getHeight() / 2;
            int palmSize = Math.min(getWidth(), getHeight()) / 3;

            g2d.setColor(ACCENT_COLOR);
            g2d.fillOval(palmX - palmSize / 2, palmY - palmSize / 2, palmSize, palmSize);


            g2d.setColor(BRIGHT_ACCENT);
            g2d.drawOval(palmX - palmSize / 2, palmY - palmSize / 2, palmSize, palmSize);


            if (fingerCount > 0) {
                double angleStep = Math.PI / (fingerCount + 1);
                double startAngle = hand.equalsIgnoreCase("Left") ? Math.PI : 0;


                for (int i = 0; i < fingerCount; i++) {
                    double angle = startAngle + angleStep * (i + 1);
                    int fingerX = (int) (palmX + Math.cos(angle) * palmSize);
                    int fingerY = (int) (palmY - Math.sin(angle) * palmSize);


                    g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.setColor(ACCENT_COLOR);
                    g2d.drawLine(palmX, palmY, fingerX, fingerY);


                    g2d.setColor(BRIGHT_ACCENT);
                    g2d.fillOval(fingerX - 5, fingerY - 5, 10, 10);
                }
            }


            g2d.setColor(TEXT_COLOR);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));


            String playerText = player == 1 ? "Player" : (vsComputer ? "Computer" : "Player 2");
            String handText = hand + " Hand: " + fingerCount;


            g2d.drawString(playerText, 10, 20);
            g2d.drawString(handText, 10, getHeight() - 10);
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
        SwingUtilities.invokeLater(ChopsticksGameGUI::new);
    }
}

