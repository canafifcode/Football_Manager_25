package com.example.fm25.controller;

import com.example.fm25.BuyRequestServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Football_Manager extends Application {
    @Override
    public void start(Stage stage) {
        try {
            BuyRequestServer server = new BuyRequestServer(5000); // Choose an unused port
            server.start();
            Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
            Scene scene = new Scene(root, 1215, 600, Color.NAVY);
            stage.setResizable(false);
            stage.setTitle("FM-25");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}