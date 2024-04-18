import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class AimTrainer extends JFrame {
    private JPanel targetPanel;
    private int score = 0;
    private JLabel scoreLabel;
    private int targetX, targetY;
    private final int TARGET_SIZE = 10;

    public AimTrainer() {
        // Fenstereinstellungen
        setTitle("Aim Trainer");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Label für die Punkteanzeige
        scoreLabel = new JLabel("Punkte: 0");
        scoreLabel.setBounds(10, 10, 80, 25);
        add(scoreLabel);

        // Panel, auf dem das Ziel gezeichnet wird
        targetPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.RED);
                g.fillOval(targetX, targetY, TARGET_SIZE, TARGET_SIZE);
            }
        };
        targetPanel.setLayout(null);
        targetPanel.setBounds(0, 0, getToolkit().getScreenSize().width, getToolkit().getScreenSize().height);
        targetPanel.setBackground(Color.WHITE);
        targetPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isTargetHit(e.getX(), e.getY())) {
                    score++;
                    scoreLabel.setText("Punkte: " + score);
                    moveTarget();
                    targetPanel.repaint();
                }
            }
        });
        add(targetPanel);

        // Mache das Fenster sichtbar
        setVisible(true);
        moveTarget();
    }

    private void moveTarget() {
        Random random = new Random();
        targetX = random.nextInt(targetPanel.getWidth() - TARGET_SIZE);
        targetY = random.nextInt(targetPanel.getHeight() - TARGET_SIZE);
    }

    private boolean isTargetHit(int mouseX, int mouseY) {
        return Math.sqrt(Math.pow(mouseX - (targetX + TARGET_SIZE / 2), 2) +
                Math.pow(mouseY - (targetY + TARGET_SIZE / 2), 2)) <= TARGET_SIZE / 2;
    }

    public static void main(String[] args) {
        new AimTrainer();
    }
}
