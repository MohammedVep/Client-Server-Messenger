package com.example.clientservermessenger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class Server extends Application {
    private PrintWriter writer;
    private BufferedReader reader;

    @Override
    public void start(Stage primaryStage) throws IOException {
        ServerSocket server = new ServerSocket(1234);
        Socket clientSocket = server.accept();

        writer = new PrintWriter(clientSocket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        VBox messagesArea = new VBox();
        messagesArea.setFillWidth(true);
        ScrollPane messagesScroll = new ScrollPane(messagesArea);
        messagesScroll.setFitToWidth(true);

        Label typingLabel = new Label();

        TextField inputField = new TextField();
        inputField.setPromptText("Enter a message");
        inputField.setOnKeyPressed(e -> writer.println("serverTyping:true"));
        inputField.setOnKeyReleased(e -> writer.println("serverTyping:false"));

        Button sendButton = new Button("Send");

        // Action to be performed by pressing Enter on TextField or clicking sendButton
        Runnable sendMessage = () -> {
            String message = inputField.getText();
            messagesArea.getChildren().add(new MessageBubble("Sent: " + message, false));
            writer.println(message);
            inputField.clear();
        };

        inputField.setOnAction(e -> sendMessage.run());
        sendButton.setOnAction(e -> sendMessage.run());

        HBox inputArea = new HBox(inputField, sendButton);
        HBox.setHgrow(inputField, Priority.ALWAYS);  // let the TextField always grow horizontally

        VBox filler = new VBox();
        VBox.setVgrow(filler, Priority.ALWAYS);  // let the filler always grow vertically

        VBox layout = new VBox(messagesScroll, typingLabel, filler, inputArea);
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(layout, 300, 200));
        primaryStage.show();

        new Thread(() -> {
            String incomingMessage;
            try {
                while ((incomingMessage = reader.readLine()) != null) {
                    if (incomingMessage.equals("clientTyping:true")) {
                        Platform.runLater(() -> typingLabel.setText("Client is typing..."));
                    } else if (incomingMessage.equals("clientTyping:false")) {
                        Platform.runLater(() -> typingLabel.setText(""));
                    } else {
                        String message = incomingMessage;
                        Platform.runLater(() -> messagesArea.getChildren().add(new MessageBubble(message, true)));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}