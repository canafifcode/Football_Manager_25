package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class TransferMarketController extends BuySell {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField nameField;

    @FXML
    private TextField clubField;

    @FXML
    private ComboBox<String> leagueComboBox;

    @FXML
    private ComboBox<String> positionComboBox;

    @FXML
    private Button searchButton;

    @FXML
    private Button backButton;

    @FXML
    private Label balanceLabel;

    @FXML
    private AnchorPane playerContainer;

    public void searchPlayers() {
        // Implement search functionality here
        // This could involve filtering players based on name, team, or position
        String name = nameField.getText().trim().toLowerCase();
        String club = clubField.getText().trim().toLowerCase();
        String league = leagueComboBox.getValue();
        String position = positionComboBox.getValue();

        ArrayList<Player> filteredPlayers = new ArrayList<>();
        for (Player player : getAvailablePlayers().values()) {
            boolean matches = true;
            if (!name.isEmpty() && !player.getName().toLowerCase().contains(name)) {
                matches = false;
            }
            if (!club.isEmpty() && !player.getTeam().toLowerCase().contains(club)) {
                matches = false;
            }
            if (league != null && !league.equals(player.getLeague())) {
                matches = false;
            }
            if (position != null && !position.equals(player.getPosition())) {
                matches = false;
            }
            if (matches) {
                filteredPlayers.add(player);
            }
        }

        loadPlayers(filteredPlayers);
    }

    private void loadPlayers(ArrayList<Player> Players) {
        playerContainer.getChildren().clear();

        if (Players.isEmpty()) {
            System.out.println("No Players found for the given criteria.");
        }
        double yOffset = 5.0;
        for (Player player : Players) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("playerCard.fxml"));
                AnchorPane playerCard = loader.load();
                PlayerCardItemController itemController = loader.getController();
                itemController.setPlayerData(player);
                playerContainer.getChildren().add(playerCard);
                AnchorPane.setTopAnchor(playerCard, yOffset);
                AnchorPane.setLeftAnchor(playerCard, 5.0);
                playerContainer.getChildren().add(playerCard);
                yOffset += playerCard.getPrefHeight() + 5.0;
            } catch (IOException e) {
                System.out.println("Error loading playerCard.fxml for player: " + player.getName());
                e.printStackTrace();
            }
        }
        playerContainer.setPrefHeight(yOffset + 5.0);
    }

    public void goBack(ActionEvent actionEvent) throws IOException {
        // Implement navigation back to the previous screen
        // This could involve closing the current window or switching scenes
        Parent root = FXMLLoader.load(getClass().getResource("players.fxml"));
        Stage stage = (Stage) playerContainer.getScene().getWindow();
        Scene scene = new Scene(root, 12150, 600, Color.NAVY);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    public void initialize(){
        leagueComboBox.getItems().addAll("Premier League", "La Liga", "Bundesliga", "Serie A", "Ligue 1");
        positionComboBox.getItems().addAll("Goalkeeper", "Defender", "Midfielder", "Forward");

        balanceLabel.setText("Balance: $.2f" + getAccountBalance());

        loadPlayers(new ArrayList<>(getAvailablePlayers().values()));
    }

    @Override
    public boolean buyPlayer(Player player) {
        boolean success = super.buyPlayer(player);
        if (success) {
            updateBalanceLabel();
            searchPlayers(); // Refresh player list
        }
        return success;
    }

    @FXML
    private void updateBalanceLabel() {
        balanceLabel.setText(String.format("Balance: $%.2f", getAccountBalance()));
    }
    @Override
    public boolean sellPlayer(Player player) {
        boolean success = super.sellPlayer(player);
        if (success) {
            updateBalanceLabel();
            searchPlayers(); // Refresh player list
        }
        return success;
    }
}
