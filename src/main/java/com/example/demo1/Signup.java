package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Signup implements Initializable {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private ChoiceBox<String> teamChoiceBox;
    @FXML
    private ChoiceBox<String> leagueChoiceBox;

    private final Map<String, List<String>> leagueTeams = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        leagueTeams.put("La Liga", Arrays.asList("Real Madrid", "Barcelona", "Atlético Madrid", "Sevilla"));
        leagueTeams.put("Premier League", Arrays.asList("Liverpool", "Arsenal", "Manchester City", "Chelsea", "Manchester United", "Leicester City", "Tottenham"));
        leagueTeams.put("Bundesliga", Arrays.asList("Bayern Munich", "Borussia Dortmund", "Bayer Leverkusen"));
        leagueTeams.put("League 1", Arrays.asList("Paris Saint-Germain", "Olympique Lyonnais"));
        leagueTeams.put("Saudi Pro League", Arrays.asList("Al Hilal", "Al Nassr"));
        leagueTeams.put("MLS", Arrays.asList("Inter Miami", "Philadelphia"));

        leagueChoiceBox.getItems().addAll(leagueTeams.keySet());
        leagueChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldLeague, newLeague) -> {
            if (newLeague != null) {
                teamChoiceBox.getItems().clear();
                teamChoiceBox.getItems().addAll(leagueTeams.get(newLeague));
                teamChoiceBox.getSelectionModel().selectFirst();
            }
        });
        System.out.println("SignUp initialized");
    }

    @FXML
    public void handleSubmit(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String league = leagueChoiceBox.getValue();
        String team = teamChoiceBox.getValue();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || league == null || team == null) {
            System.out.println("Please fill out all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        User newUser = new User(username, password, league, team);
        saveUserToFile(newUser);
        System.out.println("User Signed Up: " + newUser);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1215, 600, Color.NAVY);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUserToFile(User newUser) {
        try (java.io.FileWriter fw = new java.io.FileWriter("users.txt", true)) {
            fw.write(newUser.toString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1215, 600, Color.NAVY);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}