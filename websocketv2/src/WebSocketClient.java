import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WebSocketClient {
    private Session session;

    public WebSocketClient(String serverURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(serverURI));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to server");
        sendPing();
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Server: " + message);
    }

    public void sendPing() {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText("ping");
        }
    }

    public static void main(String[] args) {
        WebSocketClient client = new WebSocketClient("ws://localhost:8080/websocket/status");
        client.sendPing();
    }
}
