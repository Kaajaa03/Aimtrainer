import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class AimTrainer extends JFrame {
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

        setVisible(true);
        moveTarget();
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

    public static void main(String[] args) {
        new AimTrainer();
    }
}
