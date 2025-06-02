import javax.swing.*;
import java.awt.*;

public class ChatWindow extends JFrame implements ChatClient.OnMessageReceived {
    private JTextArea chatArea;
    private JTextField inputField;
    private ChatClient chatClient;
    private String username;
    private DefaultListModel<String> userListModel;
    private JList<String> userList;


    public ChatWindow(String username, ChatClient chatClient) {
        this.username = username;
        this.chatClient = chatClient;
        this.chatClient.setOnMessageReceived(this); // Register this window as listener

        // Set up GUI
        setTitle("Chat - " + username);
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (chatClient != null) {
                    chatClient.sendMessage("/exit"); // Optional: server can use this
                    chatClient.closeEverything();    // Close socket and streams
                }
            }
        });
        setLocationRelativeTo(null);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setPreferredSize(new Dimension(24, 0));
        userList.setCellRenderer(new CustomListCellRenderer());
        JScrollPane userListScrollPane = new JScrollPane(userList);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, userListScrollPane);
        splitPane.setResizeWeight(0.6); // Chat area takes more space
        add(splitPane, BorderLayout.CENTER);


        inputField = new JTextField();
        inputField.addActionListener(e -> {
            String msg = inputField.getText();
            if (!msg.isEmpty()) {
                chatClient.sendMessage(msg);
                inputField.setText("");
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);
        add(splitPane, BorderLayout.EAST);

        setVisible(true);
        inputField.requestFocusInWindow();

        //checks if the textField is empty
        inputField.addActionListener(e -> {
            String msg = inputField.getText().trim();
            if (!msg.isEmpty()) {
                chatClient.sendMessage(msg);
                inputField.setText("");
            }
        });
    }

    // Receive messages from ChatClient
    @Override
    public void onMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(msg + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength()); // Auto-scroll
            System.out.println("onMessage called with: " + msg); // Optional: keep for debugging
        });
    }

    @Override
    public void onUserListUpdated(String[] users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            for (String user : users) {
                if (!user.isEmpty()) {
                    userListModel.addElement(" ⬤ " + user);
                }
            }
        });
    }

    static class CustomListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            // Call the superclass method to get the default rendering
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            // Set the text color to green
            label.setForeground(Color.GREEN);

            return label; // Return the customized label
        }


    }

}
