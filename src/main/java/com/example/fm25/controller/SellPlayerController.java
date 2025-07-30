package com.example.fm25.controller;

import com.example.fm25.BuyRequestClient;
import com.example.fm25.Loader.BuySell;
import com.example.fm25.Loader.PlayerLoader;
import com.example.fm25.NetworkContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;

public class SellPlayerController extends BuySell {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    public VBox playerList;

    @FXML
    private Button backButton;

    @FXML
    private Label balanceLabel;

    @FXML
    private ImageView teamLogoView;

    private String username;
    private String userTeam;
    private BuyRequestClient client;

    public void setUserTeam(String userTeam) {
        this.userTeam = userTeam;
        this.client=NetworkContext.getClient();
        System.out.println("setUserTeam called - userTeam: " + userTeam);
    }

    public void setUserData(String username, String userTeam) {
        this.username = username;
        this.userTeam = userTeam;
        System.out.println("SellPlayerController.setUserData called - username: " + username + ", userTeam: " + userTeam);
        this.client = NetworkContext.getClient();
        loadOwnedPlayers();
        setBalanceLabel();
    }

    public void loadOwnedPlayers() {
        if (userTeam == null || userTeam.trim().isEmpty()) {
            System.out.println("Error: userTeam is null or empty in loadOwnedPlayers");
            displayError("No team selected. Please select a team.");
            return;
        }
        if (playerList == null) {
            System.out.println("Error: playerList VBox is null");
            displayError("UI initialization error: player list not found.");
            return;
        }

        PlayerLoader playerLoader = new PlayerLoader("", "", "", "", null, 0);
        List<PlayerLoader> players = playerLoader.loadPlayersForTeam(userTeam);
        playerList.getChildren().clear();

        if (players.isEmpty()) {
            Label noPlayersLabel = new Label("No players found for team: " + userTeam);
            noPlayersLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            playerList.getChildren().add(noPlayersLabel);
            return;
        }

        for (PlayerLoader player : players) {
            try {
                URL resource = getClass().getResource("/com/example/fm25/sellPlayerCard.fxml");
                if (resource == null) {
                    System.out.println("Error: sellPlayerCard.fxml not found in resources!");
                    displayError("Unable to load player card. Resource not found.");
                    return;
                }
                FXMLLoader loader = new FXMLLoader(resource);
                AnchorPane card = loader.load();
                PlayerCardItemController itemController = loader.getController();
                itemController.setPlayerData(player, username, userTeam, this.client, this);
                playerList.getChildren().add(card);
            } catch (IOException e) {
                System.out.println("Error loading sellPlayerCard.fxml for player: " + player.getName() + ": " + e.getMessage());
                displayError("Error loading player card: " + e.getMessage());
            }
        }
    }

    void setBalanceLabel() {
        if (balanceLabel == null) {
            System.out.println("Error: balanceLabel is null");
            return;
        }
        double balance = getAccountBalance();
        balanceLabel.setText(String.format("$%.2f", balance));
    }

    private void displayError(String message) {
        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        if (playerList != null) {
            playerList.getChildren().clear();
            playerList.getChildren().add(errorLabel);
        } else if (scrollPane != null) {
            scrollPane.setContent(errorLabel);
        } else {
            System.out.println("Error: playerList and scrollPane are null, cannot display error message");
        }
    }

    @FXML
    public void switchToTransferMarket() throws IOException {
        System.out.println("SellPlayerController.switchToTransferMarket called - username: " + username + ", userTeam: " + userTeam);
        if (client != null) {
            //client.close();
        }
        URL resource = getClass().getResource("/com/example/fm25/transferMarket.fxml");
        if (resource == null) {
            System.out.println("Error: transferMarket.fxml not found in resources!");
            displayError("Unable to navigate back. Resource not found.");
            return;
        }
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        TransferMarketController controller = loader.getController();
        controller.setUserData(username, userTeam);
        Stage stage = (Stage) scrollPane.getScene().getWindow();
        Scene scene = new Scene(root, 1215, 600, Color.NAVY);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public boolean sellPlayer(PlayerLoader player) {
        boolean success = super.sellPlayer(player);
        if (success) {
            setBalanceLabel();
            loadOwnedPlayers();
        }
        return success;
    }
}