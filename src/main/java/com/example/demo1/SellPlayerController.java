package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class SellPlayerController extends TransferMarketController {
    @FXML
    private AnchorPane playerContainer;

    @FXML
    private AnchorPane playerListContainer;

    private String username;
    private String userTeam;

    public void setUserData(String username, String userTeam) {
        this.username = username;
        this.userTeam = userTeam;
    }

    @FXML
    public void initialize() {
        // Override to avoid TransferMarketController's initialize
        loadOwnedPlayers();
    }

    void loadOwnedPlayers() {
        if (playerListContainer == null) {
            System.out.println("Error: playerListContainer is null");
            return;
        }
        ArrayList<Player> ownedPlayers = new ArrayList<>(getOwnedPlayers().values());
        playerListContainer.getChildren().clear();
        if (ownedPlayers.isEmpty()) {
            Label noPlayersLabel = new Label("No players available to sell.");
            noPlayersLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            AnchorPane.setTopAnchor(noPlayersLabel, 5.0);
            AnchorPane.setLeftAnchor(noPlayersLabel, 5.0);
            playerListContainer.getChildren().add(noPlayersLabel);
            return;
        }

        double yOffset = 5.0;
        for (Player player : ownedPlayers) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("sellPlayerCard.fxml"));
                AnchorPane playerCard = loader.load();
                PlayerCardItemController itemController = loader.getController();
                itemController.setPlayerData(player, username, userTeam); // Pass user data
                playerListContainer.getChildren().add(playerCard);
                AnchorPane.setTopAnchor(playerCard, yOffset);
                AnchorPane.setLeftAnchor(playerCard, 5.0);
                yOffset += playerCard.getPrefHeight() + 5.0;
            } catch (IOException e) {
                System.out.println("Error loading playerCard.fxml for player: " + player.getName());
                e.printStackTrace();
            }
        }
        playerListContainer.setPrefHeight(yOffset + 5.0);
    }

    @FXML
    public void goBack(ActionEvent actionEvent) {
        try {
            URL resource = getClass().getResource("players.fxml");
            if (resource == null) {
                System.out.println("players.fxml not found in resources!");
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            PlayerCardController controller = loader.getController();
            controller.loadPlayersForTeam(userTeam);
            controller.setUserName(username);
            controller.setTeamLogo(userTeam);
            Stage stage = (Stage) playerContainer.getScene().getWindow();
            Scene scene = new Scene(root, 1215, 600, Color.NAVY);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading players.fxml: ");
            e.printStackTrace();
        }
    }
}