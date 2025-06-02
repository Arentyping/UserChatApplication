import javax.swing.*;
import java.awt.*;

public class ChatWindow extends JFrame implements ChatClient.OnMessageReceived {
    private JTextArea chatArea;
    private JTextField inputField;
    private ChatClient chatClient;
    private String username;

    public ChatWindow(String username, ChatClient chatClient) {
        this.username = username;
        this.chatClient = chatClient;
        this.chatClient.setOnMessageReceived(this); // Register this window as listener

        // Set up GUI
        setTitle("Chat - " + username);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        chatArea = new JTextArea();
        chatArea.setText("Heehee");
        chatArea.setBackground(Color.YELLOW);
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        inputField.addActionListener(e -> {
            String msg = inputField.getText();
            if (!msg.isEmpty()) {
                chatClient.sendMessage(msg);
                inputField.setText("");
            }
        });

        add(scrollPane, "Center");
        add(inputField, "South");

        setVisible(true);
    }

    // Receive messages from ChatClient
    @Override
    public void onMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(msg + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength()); // Auto-scroll

            System.out.println("onMessage called with: " + msg); // 👈 debug print
            SwingUtilities.invokeLater(() -> {
                chatArea.append(msg + "\n");
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            });
        });
    }
}
