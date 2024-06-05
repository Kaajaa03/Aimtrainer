import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class AimTrainer {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}

class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Aim Trainer - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, Color.BLUE, getWidth(), getHeight(), Color.CYAN);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Aim Trainer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);

        JButton gridShotButton = createStyledButton("Grid Shot", e -> {
            new GridShotGame().setVisible(true);
            dispose();
        });

        JButton trackingButton = createStyledButton("Tracking Game", e -> {
            new TrackingGame().setVisible(true);
            dispose();
        });

        JButton comingSoonButton = createStyledButton("Mode 3 (Coming Soon)", null);

        mainPanel.add(titleLabel);
        mainPanel.add(gridShotButton);
        mainPanel.add(trackingButton);
        mainPanel.add(comingSoonButton);

        add(mainPanel);
        setVisible(true);
    }

    private JButton createStyledButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 24));
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        if (actionListener != null) {
            button.addActionListener(actionListener);
        }
        return button;
    }
}

abstract class AimGame extends JFrame {
    protected JPanel targetPanel;
    protected int score = 0;
    protected int totalClicks = 0;
    protected JLabel scoreLabel;
    protected JLabel accuracyLabel;
    protected JLabel timerLabel;
    protected Timer gameTimer;
    protected int timeLeft = 45;
    protected long targetAppearedTime;

    public AimGame(String title) {
        setTitle(title);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(1, 5));
        topPanel.setBackground(new Color(30, 30, 30));
        topPanel.setPreferredSize(new Dimension(getWidth(), 50));

        scoreLabel = createStyledLabel("0");
        accuracyLabel = createStyledLabel("0%");
        timerLabel = createStyledLabel("45");

        topPanel.add(createPanelWithLabel("Score: ", scoreLabel));
        topPanel.add(createPanelWithLabel("Accuracy: ", accuracyLabel));
        topPanel.add(createPanelWithLabel("Time: ", timerLabel));

        JButton backButton = createStyledButton("Back", e -> {
            stopGame();
            new MainMenu().setVisible(true);
            dispose();
        });
        topPanel.add(backButton);

        getContentPane().add(topPanel, BorderLayout.NORTH);

        targetPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.RED);
                g.fillOval(getTargetX(), getTargetY(), getTargetSize(), getTargetSize());
            }
        };
        targetPanel.setLayout(null);
        targetPanel.setBackground(Color.WHITE);
        targetPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        targetPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                totalClicks++;
                if (isTargetHit(e.getX(), e.getY())) {
                    score++;
                    scoreLabel.setText(String.valueOf(score));
                    long reactionTime = System.currentTimeMillis() - targetAppearedTime;
                    if (AimGame.this instanceof GridShotGame) {
                        ((GridShotGame) AimGame.this).updateReactionTimeLabel(reactionTime);
                    }
                }
                moveTarget();
                targetPanel.repaint();
                updateAccuracy();
            }
        });
        getContentPane().add(targetPanel, BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::moveTarget);

        gameTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText(String.valueOf(timeLeft));
            if (timeLeft <= 0) {
                stopGame();
                showEndDialog();
            }
        });
        gameTimer.start();
    }

    protected abstract void moveTarget();

    protected abstract int getTargetX();

    protected abstract int getTargetY();

    protected abstract int getTargetSize();

    protected boolean isTargetHit(int mouseX, int mouseY) {
        int centerX = getTargetX() + getTargetSize() / 2;
        int centerY = getTargetY() + getTargetSize() / 2;
        return Math.sqrt(Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2)) <= getTargetSize() / 2;
    }

    protected void updateAccuracy() {
        if (totalClicks > 0) {
            double accuracy = (double) score / totalClicks * 100;
            accuracyLabel.setText(String.format("%.2f%%", accuracy));
        }
    }

    private void showEndDialog() {
        int option = JOptionPane.showOptionDialog(this,
                "Game Over! Your score: " + score + "\nDo you want to return to the main menu?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Yes", "No"},
                "Yes");

        if (option == JOptionPane.YES_OPTION) {
            new MainMenu().setVisible(true);
            dispose();
        } else {
            System.exit(0);
        }
    }

    protected JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 24));
        label.setForeground(Color.WHITE);
        return label;
    }

    protected JButton createStyledButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        if (actionListener != null) {
            button.addActionListener(actionListener);
        }
        return button;
    }

    protected JPanel createPanelWithLabel(String labelText, JLabel valueLabel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(30, 30, 30));
        JLabel label = createStyledLabel(labelText);
        panel.add(label);
        panel.add(valueLabel);
        return panel;
    }

    protected void stopGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }
}

class GridShotGame extends AimGame {
    private int targetX, targetY;
    private final int targetSize = 50;
    private final Random random = new Random();
    private JLabel reactionTimeLabel;

    public GridShotGame() {
        super("Grid Shot");
        reactionTimeLabel = createStyledLabel("0 ms");
        JPanel topPanel = (JPanel) getContentPane().getComponent(0);
        topPanel.add(createPanelWithLabel("Reaction Time: ", reactionTimeLabel));
    }

    @Override
    protected void moveTarget() {
        targetX = random.nextInt(targetPanel.getWidth() - targetSize);
        targetY = random.nextInt(targetPanel.getHeight() - targetSize);
        targetAppearedTime = System.currentTimeMillis();
    }

    @Override
    protected int getTargetX() {
        return targetX;
    }

    @Override
    protected int getTargetY() {
        return targetY;
    }

    @Override
    protected int getTargetSize() {
        return targetSize;
    }

    protected void updateReactionTimeLabel(long reactionTime) {
        reactionTimeLabel.setText(reactionTime + " ms");
    }
}

class TrackingGame extends AimGame {
    private int targetX, targetY;
    private final int targetSize = 50;
    private final int targetSpeed = 3;
    private int directionX = 1;
    private int directionY = 1;
    private final Random random = new Random();
    private Timer movementTimer;

    public TrackingGame() {
        super("Tracking Game");
        movementTimer = new Timer(30, e -> moveTarget());
        movementTimer.start();
        targetPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClicked(e);
            }
        });
    }

    private void handleMouseClicked(MouseEvent e) {
        totalClicks++;
        if (isTargetHit(e.getX(), e.getY())) {
            score++;
            scoreLabel.setText(String.valueOf(score));
            moveTarget();
            targetPanel.repaint();
        }
        updateAccuracy();
    }

    @Override
    protected void moveTarget() {
        if (targetPanel.getWidth() > 0 && targetPanel.getHeight() > 0) {
            targetX += targetSpeed * directionX;
            targetY += targetSpeed * directionY;

            if (random.nextDouble() < 0.005) {
                directionX *= -1;
            }
            if (random.nextDouble() < 0.005) {
                directionY *= -1;
            }

            if (targetX < 10) {
                directionX = 1;
                targetX = 10;
            }
            if (targetX > targetPanel.getWidth() - targetSize - 10) {
                directionX = -1;
                targetX = targetPanel.getWidth() - targetSize - 10;
            }
            if (targetY < 10) {
                directionY = 1;
                targetY = 10;
            }
            if (targetY > targetPanel.getHeight() - targetSize - 10) {
                directionY = -1;
                targetY = targetPanel.getHeight() - targetSize - 10;
            }

            targetPanel.repaint();
        }
    }

    @Override
    protected int getTargetX() {
        return targetX;
    }

    @Override
    protected int getTargetY() {
        return targetY;
    }

    @Override
    protected int getTargetSize() {
        return targetSize;
    }

    @Override
    protected void stopGame() {
        super.stopGame();
        if (movementTimer != null) {
            movementTimer.stop();
        }
    }

    @Override
    protected boolean isTargetHit(int mouseX, int mouseY) {
        int centerX = getTargetX() + getTargetSize() / 2;
        int centerY = getTargetY() + getTargetSize() / 2;
        return Math.sqrt(Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2)) <= getTargetSize() / 2;
    }

    @Override
    protected void updateAccuracy() {
        if (totalClicks > 0) {
            double accuracy = (double) score / totalClicks * 100;
            accuracyLabel.setText(String.format("%.2f%%", accuracy));
        }
    }
}
