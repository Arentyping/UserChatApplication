import javax.swing.*;
import java.awt.*;

public class ChatWindow extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private ChatClient client;

    public ChatWindow(String username, ChatClient client) {
        setTitle("Chat - @" + username);
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        try {

            // Handle incoming messages
            client.setOnMessageReceived(msg -> {
                SwingUtilities.invokeLater(() -> chatArea.append(msg + "\n"));
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to server");
            System.exit(0);
        }

        // Send message
        sendButton.addActionListener(e -> {
            String text = inputField.getText();
            if (!text.isEmpty()) {
                client.sendMessage(text);
                inputField.setText("");
            }
        });

        setVisible(true);
    }
}
