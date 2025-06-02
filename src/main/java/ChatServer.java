import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private static final Set<String> connectedUsers = Collections.synchronizedSet(new HashSet<>());
    public static void main(String[] args) {
        System.out.println("Chat server started on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket);

                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    static void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Read the username
                username = in.readLine();

                synchronized (connectedUsers) {
                    if (connectedUsers.contains(username)) {
                        out.println("Username already connected. Connection closed.");
                        closeConnection();
                        return;
                    } else {
                        connectedUsers.add(username);
                    }
                }

                System.out.println("User connected: " + username);
                broadcast(username + " has joined the chat.", this);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(username + ": " + message);
                    broadcast(username + ": " + message, this);
                }

            } catch (IOException e) {
                System.out.println("Client disconnected: " + socket);
            } finally {
                closeConnection();
            }
        }


        void sendMessage(String message) {
            out.println(message);
        }

        void closeConnection() {
            try {
                if (username != null) {
                    synchronized (connectedUsers) {
                        connectedUsers.remove(username);
                    }
                    broadcast(username + " has left the chat.", this);
                }

                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException ignored) {}
            removeClient(this);
        }

    }
}
