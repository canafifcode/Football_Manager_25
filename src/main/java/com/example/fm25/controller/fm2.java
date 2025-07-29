package com.example.fm25.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class fm2 extends Application {
    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/fm25/home.fxml"));
            Scene scene = new Scene(root, 1215, 600, Color.NAVY);
            stage.setResizable(false);
            stage.setTitle("FM-25");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading home.fxml: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Start the server in a separate thread (optional)
        //new Thread(() -> new com.example.fm25.Server.Server(7564)).start();
        launch();
    }
}