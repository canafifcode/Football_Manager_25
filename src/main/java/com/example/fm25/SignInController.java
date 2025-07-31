package com.example.fm25;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

public class SignInController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    protected void handleSubmit() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return;
        }

        String userTeam = null;
        boolean matchFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String fileUsername = parts[0];
                    String filePassword = parts[1];
                    String team = parts[3];

                    if (fileUsername.equals(username) && filePassword.equals(password)) {
                        matchFound = true;
                        userTeam = team;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not read user file.");
            return;
        }
        if (matchFound) {
            System.out.println("Sign in successful!");
            logSignIn(username);
            BuySell.createOrResetOwnedPlayersFile(username, userTeam);
            
            //setUserData(username, userTeam);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("players.fxml"));
                root = loader.load();
                PlayerCardController controller = loader.getController();
                controller.loadPlayersForTeam(userTeam);
                controller.setTeamLogo(userTeam);
                controller.setUserName(username);
                controller.setUserTeam(userTeam);
                stage = (Stage) usernameField.getScene().getWindow();
                scene = new Scene(root, 1215, 600, Color.NAVY);
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    @FXML
    public void switchToSignUp(ActionEvent event) {
        try {
            URL fxmlLocation = getClass().getResource("signup.fxml");
            if (fxmlLocation == null) {
                System.out.println("signup.fxml not found!");
                return;
            }
            URL imageLocation = getClass().getResource("bg.jpg");
            if (imageLocation == null) {
                System.out.println("bg.jpg not found!");
                return;
            }
            root = FXMLLoader.load(fxmlLocation);
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root, 1215, 600, Color.NAVY);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logSignIn(String username) {
        try (FileWriter fw = new FileWriter("login_history.txt", true)) {
            String timestamp = java.time.LocalDateTime.now().toString().replace("T", " ");
            fw.write(username + " signed in at " + timestamp + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToHome(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("home.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1215, 600, Color.NAVY);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}