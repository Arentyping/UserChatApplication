import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import org.glassfish.tyrus.server.Server;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint("/status")
public class WebSocketServer {
    private static Set<Session> clients = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);
        broadcastStatus();
        System.out.println("Client connected: " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        clients.remove(session);
        broadcastStatus();
        System.out.println("Client disconnected: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if (message.equals("ping")) {
            session.getAsyncRemote().sendText("pong");
        }
        System.out.println("Message from client (" + session.getId() + "): " + message);
    }

    private void broadcastStatus() {
        for (Session session : clients) {
            session.getAsyncRemote().sendText("Active Users: " + clients.size());
        }
    }

    // ✅ Main Method to Start the WebSocket Server
    public static void main(String[] args) {
        Server server = new Server("localhost", 8080, "/websocket", null, WebSocketServer.class);
        try {
            server.start();
            System.out.println("WebSocket Server started at ws://localhost:8080/websocket/status");
            System.out.println("Press Enter to stop the server...");
            System.in.read();
        } catch (Exception e) {
            Logger.getLogger(WebSocketServer.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            server.stop();
            System.out.println("WebSocket Server stopped.");
        }
    }
}
