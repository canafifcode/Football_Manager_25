package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class PlayerCardController extends BuySell {

    @FXML
    private AnchorPane playerContainer;

    @FXML
    private ImageView teamLogoView;

    public void setTeamLogo(String teamName) {
        String imagePath = "/logos/" + teamName.toLowerCase() + ".png";
        Image logo = new Image(getClass().getResourceAsStream(imagePath));
        teamLogoView.setImage(logo);
        teamLogoView.setStyle("-fx-background-color: transparent;");
        teamLogoView.setSmooth(true);
        teamLogoView.setCache(true);

    }

    @FXML
    private Label userNameLabel;

    public void setUserName(String userName) {
        userNameLabel.setText(userName);
    }


    public void loadPlayersForTeam(String teamName) {
        Player playerLoader = new Player("", "", "", "", null, 0);
        List<Player> players = playerLoader.loadPlayersForTeam(teamName);

        if (playerContainer == null) {
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
        for (Player player : players) {
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
            }
        }
        playerContainer.setPrefHeight(yOffset + 5.0);
    }

    @FXML
    public void goBack(javafx.event.ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
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