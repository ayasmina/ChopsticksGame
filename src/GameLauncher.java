import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.awt.geom.*;

public class GameLauncher {
    private static final Color BACKGROUND_COLOR = new Color(25, 35, 60);
    private static final Color PANEL_COLOR = new Color(40, 50, 80);
    private static final Color ACCENT_COLOR = new Color(65, 165, 245);
    private static final Color BRIGHT_ACCENT = new Color(100, 210, 255);
    private static final Color TEXT_COLOR = new Color(230, 240, 255);
    private static Timer pulseTimer;
    private static float pulseAlpha = 0.0f;
    private static boolean pulseIncreasing = true;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame pickFrame = new JFrame("Game Selection");
        pickFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pickFrame.setSize(450, 500);
        pickFrame.setLocationRelativeTo(null);
        pickFrame.getContentPane().setBackground(BACKGROUND_COLOR);

        Font gameFont = new Font("Segoe UI", Font.PLAIN, 14);
        try {
            gameFont = Font.createFont(Font.TRUETYPE_FONT, new File("PressStart2P-Regular.ttf")).deriveFont(12f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(gameFont);
        } catch (FontFormatException | IOException e) {
            System.out.println("Custom font not found. Using Segoe UI.");
        }

        JPanel mainPanel = new RoundedPanel(20);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        mainPanel.setBackground(PANEL_COLOR);

        JLabel titleLabel = new JLabel("Choose a Game");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(BRIGHT_ACCENT);
        mainPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Select an option to play");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_COLOR);
        mainPanel.add(subtitleLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        GameButton option1 = new GameButton("Play Chopsticks vs AI");
        GameButton option2 = new GameButton("2 Players vs AI");
        GameButton option3 = new GameButton("Coming Soon");
        GameButton option4 = new GameButton("Coming Soon");

        GameButton[] buttons = {option1, option2, option3, option4};

        for (GameButton button : buttons) {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(300, 50));
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            mainPanel.add(button);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        option3.setEnabled(false);
        option4.setEnabled(false);

        option1.addActionListener(e -> {
            pickFrame.dispose();
            new ChopsticksGameGUI();
        });

        option2.addActionListener(e -> {
            pickFrame.dispose();

            new ChopsticksGameGUI();
        });

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(BACKGROUND_COLOR);
        wrapperPanel.add(mainPanel);

        pickFrame.add(wrapperPanel);
        pickFrame.setVisible(true);

        startPulseAnimation();
    }

    private static void startPulseAnimation() {
        pulseTimer = new Timer(50, e -> {
            if (pulseIncreasing) {
                pulseAlpha += 0.05f;
                if (pulseAlpha >= 1.0f) {
                    pulseAlpha = 1.0f;
                    pulseIncreasing = false;
                }
            } else {
                pulseAlpha -= 0.05f;
                if (pulseAlpha <= 0.3f) {
                    pulseAlpha = 0.3f;
                    pulseIncreasing = true;
                }
            }


            for (Window window : Window.getWindows()) {
                window.repaint();
            }
        });
        pulseTimer.start();
    }


    static class GameButton extends JButton {
        private boolean isHovering = false;

        public GameButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(TEXT_COLOR);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) {
                        isHovering = true;
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovering = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();


            if (isEnabled()) {
                Color baseColor = isHovering ? BRIGHT_ACCENT : ACCENT_COLOR;
                int red = baseColor.getRed();
                int green = baseColor.getGreen();
                int blue = baseColor.getBlue();

                if (isHovering) {
                    float glowIntensity = 0.3f + (0.7f * pulseAlpha);
                    Color glowColor = new Color(
                            Math.min(255, (int)(red + (255 - red) * glowIntensity)),
                            Math.min(255, (int)(green + (255 - green) * glowIntensity * 0.7)),
                            Math.min(255, (int)(blue + (255 - blue) * glowIntensity * 0.3))
                    );
                    g2d.setColor(glowColor);
                } else {
                    g2d.setColor(baseColor);
                }
            } else {
                g2d.setColor(new Color(80, 90, 120));
            }

            RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, width, height, 15, 15);
            g2d.fill(roundedRectangle);

            if (isEnabled()) {
                g2d.setColor(isHovering ? Color.WHITE : BRIGHT_ACCENT);
                g2d.setStroke(new BasicStroke(2f));
            } else {
                g2d.setColor(new Color(60, 70, 100));
                g2d.setStroke(new BasicStroke(1f));
            }
            g2d.draw(roundedRectangle);

            // Draw text
            FontMetrics fm = g2d.getFontMetrics();
            Rectangle2D textBounds = fm.getStringBounds(getText(), g2d);

            int textX = (int) (width - textBounds.getWidth()) / 2;
            int textY = (int) (height - textBounds.getHeight()) / 2 + fm.getAscent();

            if (isEnabled()) {
                g2d.setColor(isHovering ? BACKGROUND_COLOR : TEXT_COLOR);
            } else {
                g2d.setColor(new Color(120, 130, 160));
            }
            g2d.drawString(getText(), textX, textY);

            // Add a shine effect when hovering
            if (isHovering && isEnabled()) {
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(255, 255, 255, 100),
                        0, height/2, new Color(255, 255, 255, 10)
                );
                g2d.setPaint(gp);
                g2d.fill(new Rectangle2D.Float(0, 0, width, height/2));
            }
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            return new Dimension(Math.max(size.width, 200), Math.max(size.height, 50));
        }
    }

    static class RoundedPanel extends JPanel {
        private int cornerRadius;

        public RoundedPanel(int radius) {
            super();
            this.cornerRadius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            RoundRectangle2D roundedRect = new RoundRectangle2D.Float(
                    0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);

            g2.setColor(getBackground());
            g2.fill(roundedRect);

            g2.setColor(ACCENT_COLOR);
            g2.setStroke(new BasicStroke(2f));
            g2.draw(roundedRect);

            g2.dispose();
        }
    }
}