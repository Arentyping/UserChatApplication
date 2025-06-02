import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ChatClient {
    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;
    private String username;
    private OnMessageReceived listener;
    // Global at top of ChatServer class:
    private static final Set<String> connectedUsers = Collections.synchronizedSet(new HashSet<>());

    public ChatClient(String serverAddress, int port, String username) throws IOException {
        this.socket = new Socket(serverAddress, port);
        this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.username = username;

        // Send username
        output.write(username);
        output.newLine();
        output.flush();

        // Start listening thread
        new Thread(() -> {
            try {
                String msg;
                while ((msg = input.readLine()) != null) {
                    if (listener != null) {
                        listener.onMessage(msg);
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection closed.");
            }
        }).start();
    }

    public void sendMessage(String msg) {
        try {
            output.write(username + ": " + msg);
            output.newLine();
            output.flush();
        } catch (IOException e) {
            System.out.println("Failed to send message");
        }
    }

    public void setOnMessageReceived(OnMessageReceived listener) {
        this.listener = listener;
    }

    public interface OnMessageReceived {
        void onMessage(String msg);
    }

    public void closeEverything() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
