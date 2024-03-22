package com.example.clientservermessenger;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MessageBubble extends HBox {
    public MessageBubble(String message, boolean isReceived) {
        Label label = new Label(message);
        label.setPadding(new Insets(10));
        label.setWrapText(true);

        VBox container = new VBox(label);
        container.setStyle("-fx-background-color: " + (isReceived ? "pink;" : "lightgreen;"));
        container.setMaxWidth(200);

        this.getChildren().add(container);
        this.setAlignment(isReceived ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
    }
}