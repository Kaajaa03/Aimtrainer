import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class StartScreen extends JFrame {
    public StartScreen() {
        setTitle("Aim Trainer - Startbildschirm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);  // Zentriert das Fenster
        setLayout(new GridLayout(3, 1, 10, 10)); // Grid-Layout für 3 Buttons

        JButton startGameButton = new JButton("Grid Shot");
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AimTrainer aimTrainer = new AimTrainer();
                aimTrainer.setVisible(true);
                dispose();  // Schließt den Startbildschirm, wenn das Spiel gestartet wird
            }
        });

        JButton startTrackingButton = new JButton("Tracking starten");
        startTrackingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrackingGame trackingGame = new TrackingGame();
                trackingGame.setVisible(true);
                dispose();  // Schließt den Startbildschirm, wenn das Tracking-Spiel gestartet wird
            }
        });

        JButton placeholder2 = new JButton("Modus 3 (In Entwicklung)");

        add(startGameButton);
        add(startTrackingButton);
        add(placeholder2);

        setVisible(true);
    }

    public static void main(String[] args) {
        new StartScreen();
    }
}

class TrackingGame extends JFrame {
    private JPanel targetPanel;
    private int score = 0;
    private int totalClicks = 0;
    private JLabel scoreLabel;
    private JLabel accuracyLabel;
    private int targetX, targetY;
    private int targetSize = 20;
    private Random random = new Random();
    private Timer movementTimer;

    public TrackingGame() {
        setTitle("Tracking Game");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        scoreLabel = new JLabel("Punkte: 0");
        accuracyLabel = new JLabel("Trefferquote: 0%");
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(scoreLabel);
        topPanel.add(accuracyLabel);
        getContentPane().add(topPanel, BorderLayout.NORTH);

        targetPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.RED);
                g.fillOval(targetX, targetY, targetSize, targetSize);
            }
        };
        targetPanel.setLayout(null);
        targetPanel.setBackground(Color.WHITE);
        targetPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                totalClicks++;
                if (isTargetHit(e.getX(), e.getY())) {
                    score++;
                    scoreLabel.setText("Punkte: " + score);
                }
                updateAccuracy();
            }
        });
        targetPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        getContentPane().add(targetPanel, BorderLayout.CENTER);

        // Ensure the panel is visible and has dimensions before starting movement
        SwingUtilities.invokeLater(this::initializeGame);
    }

    private void initializeGame() {
        // Set up the timer to move the target
        moveTarget(); // Initial placement
        movementTimer = new Timer(500, e -> moveTarget());
        movementTimer.start();
    }

    private void moveTarget() {
        if (targetPanel.getWidth() > 0 && targetPanel.getHeight() > 0) {
            targetSize = 20 + random.nextInt(31); // Target size changes between 20 and 50 pixels
            targetX = random.nextInt(targetPanel.getWidth() - targetSize);
            targetY = random.nextInt(targetPanel.getHeight() - targetSize);
            targetPanel.repaint();
            movementTimer.setDelay(500 + random.nextInt(1001)); // Change speed between 0.5 and 1.5 seconds
        }
    }

    private boolean isTargetHit(int mouseX, int mouseY) {
        return Math.sqrt(Math.pow(mouseX - (targetX + targetSize / 2), 2) +
                Math.pow(mouseY - (targetY + targetSize / 2), 2)) <= targetSize / 2;
    }

    private void updateAccuracy() {
        if (totalClicks > 0) {
            double accuracy = (double) score / totalClicks * 100;
            accuracyLabel.setText(String.format("Trefferquote: %.2f%%", accuracy));
        }
    }
}



class AimTrainer extends JFrame {
    private JPanel targetPanel;
    private int score = 0;
    private int totalClicks = 0;
    private JLabel scoreLabel;
    private JLabel accuracyLabel;
    private int targetX, targetY;
    private int targetSize;
    private Random random = new Random();

    public AimTrainer() {
        setTitle("Aim Trainer");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        scoreLabel = new JLabel("Punkte: 0");
        accuracyLabel = new JLabel("Trefferquote: 0%");
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(scoreLabel);
        topPanel.add(accuracyLabel);
        getContentPane().add(topPanel, BorderLayout.NORTH);

        targetPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.RED);
                g.fillOval(targetX, targetY, targetSize, targetSize);
            }
        };
        targetPanel.setLayout(null);
        targetPanel.setBackground(Color.WHITE);
        targetPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                totalClicks++;
                if (isTargetHit(e.getX(), e.getY())) {
                    score++;
                    scoreLabel.setText("Punkte: " + score);
                }
                updateAccuracy();
                moveTarget();
                targetPanel.repaint();
            }
        });
        targetPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        getContentPane().add(targetPanel, BorderLayout.CENTER);

        // Verzögere das Positionieren des Ziels, bis das Fenster vollständig angezeigt wird
        SwingUtilities.invokeLater(this::moveTarget);
    }

    private void moveTarget() {
        targetSize = 10 + random.nextInt(31); // Ziele zwischen 10 und 40 Pixeln Durchmesser
        targetX = random.nextInt(targetPanel.getWidth() - targetSize);
        targetY = random.nextInt(targetPanel.getHeight() - targetSize);
    }

    private boolean isTargetHit(int mouseX, int mouseY) {
        return Math.sqrt(Math.pow(mouseX - (targetX + targetSize / 2), 2) +
                Math.pow(mouseY - (targetY + targetSize / 2), 2)) <= targetSize / 2;
    }

    private void updateAccuracy() {
        if (totalClicks > 0) {
            double accuracy = (double) score / totalClicks * 100;
            accuracyLabel.setText(String.format("Trefferquote: %.2f%%", accuracy));
        }
    }
}
