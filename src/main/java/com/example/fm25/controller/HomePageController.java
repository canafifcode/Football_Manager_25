package com.example.fm25.controller;

import com.example.fm25.Loader.*;
import com.example.fm25.Server.*;

import com.example.fm25.Loader.PlayerLoader;
import com.example.fm25.Loader.BuySell;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class HomePageController extends BuySell {

    @FXML
    private VBox playerContainer;

    @FXML
    private ImageView teamLogoView;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label statsLabel;

    private String userTeam; // Added to store userTeam

    public void setTeamLogo(String teamName) {
        if (teamName == null || teamName.trim().isEmpty()) {
            System.out.println("Error: teamName is null or empty in setTeamLogo");
            teamLogoView.setImage(null);
            return;
        }
        String imagePath = "/logos/" + teamName.toLowerCase() + ".png";
        try {
            Image logo = new Image(getClass().getResourceAsStream(imagePath));
            teamLogoView.setImage(logo);
            teamLogoView.setStyle("-fx-background-color: transparent;");
            teamLogoView.setSmooth(true);
            teamLogoView.setCache(true);
        } catch (Exception e) {
            System.out.println("Error loading logo for team: " + teamName + ", path: " + imagePath);
            e.printStackTrace();
        }
    }

    public void setUserName(String userName) {
        userNameLabel.setText(userName != null ? userName : "Unknown");
    }

    public void setUserTeam(String userTeam) {
        this.userTeam = userTeam;
        System.out.println("setUserTeam called - userTeam: " + userTeam);
    }

    public void loadPlayersForTeam(String teamName) {
        if (teamName == null || teamName.trim().isEmpty()) {
            System.out.println("Error: teamName is null or empty in loadPlayersForTeam");
            return;
        }
        PlayerLoader playerLoader = new PlayerLoader("", "", "", "", null, 0);
        List<PlayerLoader> players = playerLoader.loadPlayersForTeam(teamName);

        if (playerContainer == null) {
            System.out.println("Error: playerContainer is null");
            return;
        }

        playerContainer.getChildren().clear();

        if (players.isEmpty()) {
            Label noPlayersLabel = new Label("No players found for team: " + teamName);
            noPlayersLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            AnchorPane.setTopAnchor(noPlayersLabel, 5.0);
            AnchorPane.setLeftAnchor(noPlayersLabel, 5.0);
            playerContainer.getChildren().add(noPlayersLabel);
            return;
        }

        double yOffset = 5.0;
        for (PlayerLoader player : players) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("playerCard.fxml"));
                AnchorPane card = loader.load();
                PlayerCardItemController itemController = loader.getController();
                itemController.setPlayerData(player);
                AnchorPane.setTopAnchor(card, yOffset);
                AnchorPane.setLeftAnchor(card, 5.0);
                playerContainer.getChildren().add(card);
                yOffset += card.getPrefHeight() + 5.0;
            } catch (IOException e) {
                System.out.println("Error loading playerCard.fxml for player: " + player.getName());
                e.printStackTrace();
            }
        }
        playerContainer.setPrefHeight(yOffset + 5.0);
    }

    @FXML
    public void goBack(javafx.event.ActionEvent event) throws IOException {
        URL resource = getClass().getResource("home.fxml");
        if (resource == null) {
            System.out.println("Error: home.fxml not found in resources!");
            return;
        }
        Parent root = FXMLLoader.load(resource);
        Stage stage = (Stage) playerContainer.getScene().getWindow();
        Scene scene = new Scene(root, 1215, 600, Color.NAVY);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void goToTransferMarket(ActionEvent actionEvent) {
        try {
            URL resource = getClass().getResource("transferMarket.fxml");
            if (resource == null) {
                System.out.println("transferMarket.fxml not found in resources!");
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            TransferMarketController transferMarketController = loader.getController();
            transferMarketController.setUserData(userNameLabel.getText(), userTeam);
            Stage stage = (Stage) playerContainer.getScene().getWindow();
            Scene scene = new Scene(root, 1215, 600, Color.NAVY);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading transferMarket.fxml: ");
            e.printStackTrace();
        }
    }
}