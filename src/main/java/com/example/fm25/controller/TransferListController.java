package com.example.fm25.controller;

import com.example.fm25.Loader.BuySell;
import com.example.fm25.Loader.PlayerLoader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.List;
import com.example.fm25.BuyRequestClient; // Add this import
import com.example.fm25.NetworkContext;

public class TransferListController extends BuySell {

    private String username;
    private String userTeam;
    private BuySell buySell;
    private BuyRequestClient client;

    @FXML
    private Label balanceLabel;

    @FXML
    private ScrollPane myscrollPane;

    @FXML
    private ScrollPane othersscrollPane;

    @FXML
    private VBox playermytfList;

    @FXML
    private VBox playerothertfList;

    public void setUserData(String username, String userTeam) {
        this.username = username;
        this.userTeam = userTeam;
        this.client = NetworkContext.getClient();
        System.out.println("TransferListController.setUserData called - username: " + username + ", userTeam: " + userTeam);
        // After user data is set, load the players and update balance
        loadOwnedSellrequestedPlayers();
        loadOthersSellrequestedPlayers();
        setBalanceLabel();
    }

    public void loadOwnedSellrequestedPlayers(){
        if (userTeam == null || userTeam.trim().isEmpty()) {
            System.out.println("Error: userTeam is null or empty in loadOwnedSellRequestedPlayers");
            return;
        }
        PlayerLoader playerLoader = new PlayerLoader("", "", "", "", null, 0);
        List<PlayerLoader> players = playerLoader.loadPlayersForMySell(username);

        if (playermytfList == null) {
            System.out.println("Error: playermytfList VBox is null");
            return;
        }

        playermytfList.getChildren().clear();

        if (players.isEmpty()) {
            Label noPlayersLabel = new Label("No players found for sell: " + username);
            noPlayersLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            playermytfList.getChildren().add(noPlayersLabel);
            return;
        }

        for (PlayerLoader player : players) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fm25/othersSellCard.fxml"));
                AnchorPane card = loader.load();
                PlayerCardItemController itemController = loader.getController();
                itemController.setPlayerData(player, username, userTeam, this.client, this); // Pass user info if needed
                playermytfList.getChildren().add(card);
            } catch (IOException e) {
                System.out.println("Error loading mySellCards.fxml for player: " + player.getName());
                e.printStackTrace();
            }
        }

    }

    public void loadOthersSellrequestedPlayers(){
        if (userTeam == null || userTeam.trim().isEmpty()) {
            System.out.println("Error: userTeam is null or empty in loadOthersSellRequestedPlayers");
            return;
        }
        PlayerLoader playerLoader = new PlayerLoader("", "", "", "", null, 0);
        List<PlayerLoader> players = playerLoader.loadPlayersForOthersSell(username,userTeam);

        if (playerothertfList == null) {
            System.out.println("Error: playerothertfList VBox is null");
            return;
        }

        playerothertfList.getChildren().clear();

        if (players.isEmpty()) {
            Label noPlayersLabel = new Label("No players found to buy: " + username);
            noPlayersLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            playerothertfList.getChildren().add(noPlayersLabel);
            return;
        }

        for (PlayerLoader player : players) {
            try {
                // Correct
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fm25/othersSellCard.fxml"));
                AnchorPane card = loader.load();
                PlayerCardItemController itemController = loader.getController();
                itemController.setPlayerData(player, username, userTeam, this.client, this); // Pass user info if needed
                playerothertfList.getChildren().add(card);
            } catch (IOException e) {
                System.out.println("Error loading othersSellCard.fxml for player: " + player.getName());
                e.printStackTrace();
            }
        }

    }

    @FXML
    public void setBalanceLabel() {
        if (balanceLabel == null) {
            System.out.println("Error: balanceLabel is null");
            return;
        }
        double balance = getAccountBalance();
        balanceLabel.setText(String.format("$%.2f", balance));
    }

    @FXML
    public void switchToTransferMarket() throws IOException {
        System.out.println("TransferListController.switchToTransferMarket called - username: " + username + ", userTeam: " + userTeam);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fm25/transferMarket.fxml"));
        Parent root = loader.load();
        TransferMarketController controller = loader.getController();
        controller.setUserData(username, userTeam);
        othersscrollPane.getScene().setRoot(root);
    }

}
