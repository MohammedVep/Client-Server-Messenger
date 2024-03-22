package com.example.clientservermessenger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Application {
    private PrintWriter writer;
    private BufferedReader reader;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Socket socket = new Socket("localhost", 1234);

        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        VBox messagesArea = new VBox();
        messagesArea.setFillWidth(true);
        ScrollPane messagesScroll = new ScrollPane(messagesArea);
        messagesScroll.setFitToWidth(true);

        Label typingLabel = new Label();

        TextField inputField = new TextField();
        inputField.setPromptText("Enter a message");
        inputField.setOnKeyPressed(e -> writer.println("clientTyping:true"));
        inputField.setOnKeyReleased(e -> writer.println("clientTyping:false"));

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
        primaryStage.setTitle("Client");
        primaryStage.setScene(new Scene(layout, 300, 200));
        primaryStage.show();

        new Thread(() -> {
            String incomingMessage;
            try {
                while ((incomingMessage = reader.readLine()) != null) {
                    if (incomingMessage.equals("serverTyping:true")) {
                        Platform.runLater(() -> typingLabel.setText("Server is typing..."));
                    } else if (incomingMessage.equals("serverTyping:false")) {
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