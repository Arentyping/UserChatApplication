import javax.swing.*;
import javax.websocket.*;
import java.awt.*;
import java.net.URI;

@ClientEndpoint
public class WebSocketAdminGUI extends JFrame {
    private JTextArea statusArea = new JTextArea();
    private Session session;

    public WebSocketAdminGUI() {
        setTitle("Admin Control - User Status");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new JScrollPane(statusArea), BorderLayout.CENTER);
        setVisible(true);

        connectToServer();
    }

    private void connectToServer() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI("ws://localhost:8080/websocket/status"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to WebSocket Server");
    }

    @OnMessage
    public void onMessage(String message) {
        statusArea.append(message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WebSocketAdminGUI());
    }
}
