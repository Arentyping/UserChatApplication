import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatWindow extends Application {
    private String username;
    private ChatClient client;

    private VBox messagesBox;
    private TextField inputField;

    public ChatWindow(String username) {
        this.username = username;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chat - " + username);

        messagesBox = new VBox(10);
        messagesBox.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(messagesBox);
        scrollPane.setFitToWidth(true);

        inputField = new TextField();
        inputField.setPromptText("Type a message...");
        inputField.setOnAction(e -> sendMessage());

        BorderPane root = new BorderPane();
        root.setCenter(scrollPane);
        root.setBottom(inputField);

        Scene scene = new Scene(root, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            client = new ChatClient("localhost", 12345, username);
            client.setOnMessageReceived(this::displayMessage);
        } catch (IOException e) {
            displayMessage("⚠ Error connecting to server: " + e.getMessage());
        }

        primaryStage.setOnCloseRequest(e -> {
            client.closeEverything();
        });
    }

    private void sendMessage() {
        String msg = inputField.getText().trim();
        if (!msg.isEmpty()) {
            client.sendMessage(msg);
            inputField.clear();
        }
    }

    private void displayMessage(String msg) {
        Platform.runLater(() -> {
            Label messageLabel = new Label(msg);
            messageLabel.setWrapText(true);
            messagesBox.getChildren().add(messageLabel);
        });
    }
}
