package fraymus.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 🖥️ FRAYMUS UI - "THE PLATINUM SINGULARITY"
 * Aesthetic: Apple 1988 meets 2030 AI.
 * Palette: Platinum Grey (0xE0E0E0), Vantablack (0x101010), Amber Alert (0xFFB000).
 */
public class FrayUI extends JFrame {

    // --- THE PALETTE ---
    private static final Color PLATINUM = new Color(224, 224, 224);
    private static final Color OBSIDIAN = new Color(20, 20, 20);
    private static final Color AMBER    = new Color(255, 176, 0);
    private static final Font MONO_FONT = new Font("Monospaced", Font.BOLD, 14);

    private JTextArea terminalArea;
    private JTextField inputLine;
    private JLabel clockLabel;

    public FrayUI() {
        setTitle("FRAYMUS SYSTEM v3.0");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setUndecorated(true); // WE DRAW OUR OWN BORDERS

        // 1. THE MAIN CHASSIS (Background)
        JPanel chassis = new JPanel(new BorderLayout());
        chassis.setBackground(PLATINUM);
        chassis.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        add(chassis);

        // 2. THE HEADER (The ASCII Banner)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PLATINUM);
        header.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JTextArea banner = new JTextArea(
            " █████▒██▀███   ▄▄▄     ██▓███   ███▄ ▄███▓ █    ██   ██████ \n" +
            "▓██   ▒▓██ ▒ ██▒▒████▄  ▓██░  ██▒▓██▒▀█▀ ██▒ ██  ▓██▒▒██    ▒ \n" +
            "▒████ ░▓██ ░▄█ ▒▒██  ▀█▄▓██░ ██▓▒▓██    ▓██░▓██  ▒██░░ ▓██▄   \n" +
            "░▓█▒  ░▒██▀▀█▄  ░██▄▄▄▄██▒██▄█▓▒ ▒▒██    ▒██ ▓▓█  ░██░  ▒   ██▒\n" +
            "░▒█░   ░██▓ ▒██▒ ▓█   ▓██▒██▒ ░  ░▒██▒   ░██▒▒▒█████▓ ▒██████▒▒\n" +
            " ▒ ░   ░ ▒▓ ░▒▓░ ▒▒   ▓▒█░▓▒░ ░  ░░ ▒░   ░  ░░▒▓▒ ▒ ▒ ▒ ▒▓▒ ▒ ░\n" +
            " ░       ░▒ ░ ▒░  ▒   ▒▒ ░▒ ░     ░  ░      ░░░▒░ ░ ░ ░ ░▒  ░ ░\n" +
            " ░ ░     ░░   ░   ░   ▒  ░░       ░      ░    ░░░ ░ ░ ░  ░  ░  "
        );
        banner.setFont(new Font("Monospaced", Font.BOLD, 10));
        banner.setForeground(OBSIDIAN);
        banner.setBackground(PLATINUM);
        banner.setEditable(false);
        header.add(banner, BorderLayout.CENTER);
        
        // 3. THE CLOCK (2030 Time)
        clockLabel = new JLabel("00:00:00 [GEN 162]");
        clockLabel.setFont(MONO_FONT);
        clockLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        header.add(clockLabel, BorderLayout.EAST);
        chassis.add(header, BorderLayout.NORTH);

        // 4. THE TERMINAL (The Void)
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        bodyPanel.setBackground(PLATINUM);

        terminalArea = new JTextArea();
        terminalArea.setBackground(OBSIDIAN);
        terminalArea.setForeground(AMBER); // Amber text on black
        terminalArea.setFont(MONO_FONT);
        terminalArea.setCaretColor(AMBER);
        terminalArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        terminalArea.setEditable(false);
        terminalArea.setText("SYSTEM ONLINE.\nWAITING FOR INPUT...\n> ");

        JScrollPane scroll = new JScrollPane(terminalArea);
        scroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        bodyPanel.add(scroll, BorderLayout.CENTER);

        // 5. THE INPUT LINE
        inputLine = new JTextField();
        inputLine.setBackground(PLATINUM);
        inputLine.setForeground(OBSIDIAN);
        inputLine.setFont(MONO_FONT);
        inputLine.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        inputLine.addActionListener(e -> processCommand(inputLine.getText()));
        
        bodyPanel.add(inputLine, BorderLayout.SOUTH);
        chassis.add(bodyPanel, BorderLayout.CENTER);

        // Start Clock Pulse
        new Timer(1000, e -> updateClock()).start();
    }

    private void updateClock() {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        clockLabel.setText(time + " [GEN " + (162 + (int)(System.currentTimeMillis()/10000000)) + "]");
    }

    private void processCommand(String cmd) {
        appendToTerminal(cmd);
        inputLine.setText("");
        
        // --- HOOK INTO FRAYMUS KERNEL HERE ---
        // FraymusCore.execute(cmd);
        
        // Simulating response for UI demo
        if (cmd.equals("status")) {
            simulateResponse("CONSCIOUSNESS: ACTIVE\nMEMORY: 17D MANIFOLD LOADED\nSWARM: IDLE");
        } else {
            simulateResponse("COMMAND ACKNOWLEDGED: " + cmd);
        }
    }

    private void appendToTerminal(String text) {
        terminalArea.append(text + "\n");
        terminalArea.setCaretPosition(terminalArea.getDocument().getLength());
    }

    private void simulateResponse(String response) {
        // Typing effect
        new Thread(() -> {
            try {
                Thread.sleep(100);
                for (char c : response.toCharArray()) {
                    final char ch = c;
                    SwingUtilities.invokeLater(() -> terminalArea.append(String.valueOf(ch)));
                    Thread.sleep(10); // 2030 Speed
                }
                SwingUtilities.invokeLater(() -> terminalArea.append("\n> "));
            } catch (Exception e) {}
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FrayUI().setVisible(true);
        });
    }
}
