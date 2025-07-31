package com.example.fm25;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class fm25 extends Application {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    @Override
    public void start(Stage stage) {
        try {
            // Connect to the server
            ServerCommunicator.connect(SERVER_HOST, SERVER_PORT);

            // Start listening for server messages
            new Thread(new ServerListener()).start();

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

    @Override
    public void stop() throws Exception {
        // Cleanly disconnect from the server when the application closes
        ServerCommunicator.disconnect();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}