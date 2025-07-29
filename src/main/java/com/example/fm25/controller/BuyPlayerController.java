package com.example.fm25.controller;

import com.example.fm25.BuyRequestClient;
import com.example.fm25.Loader.BuySell;
import com.example.fm25.Loader.PlayerLoader;
import com.example.fm25.NetworkContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.Socket;

public class BuyPlayerController extends BuySell {

    private String userTeam;
    private String username;
    private BuyRequestClient client;

    @FXML
    private Label balanceLabel;

    @FXML
    public VBox playerList;

    @FXML
    public ScrollPane scrollPane;

    public void setUserTeam(String userTeam) {
        this.userTeam = userTeam;
        System.out.println("setUserTeam called - userTeam: " + userTeam);
    }

    public void setUserData(String username, String userTeam) {
        this.username = username;
        this.userTeam = userTeam;
        System.out.println("BuyPlayerController.setUserData called - username: " + username + ", userTeam: " + userTeam);
        this.client = NetworkContext.getClient();
        setBalanceLabel();
    }

    public void initialize() {
        System.out.println("BuyPlayerController initialized");
    }

    void setBalanceLabel() {
        if (balanceLabel == null) {
            System.out.println("Error: balanceLabel is null");
            return;
        }
        double balance = getAccountBalance();
        balanceLabel.setText(String.format("$%.2f", balance));
    }

    public void switchToTransferMarket() throws IOException {
        System.out.println("BuyPlayerController.switchToTransferMarket called - username: " + username + ", userTeam: " + userTeam);
        if (client != null) {
            //client.close();
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fm25/transferMarket.fxml"));
        Parent root = loader.load();
        TransferMarketController controller = loader.getController();
        controller.setUserData(username, userTeam);
        scrollPane.getScene().setRoot(root);
    }

    public void refreshPlayerList() {
        if (playerList == null) {
            System.out.println("Error: playerList is null");
            return;
        }
        playerList.getChildren().clear();
        for (PlayerLoader player : getAvailablePlayers().values()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fm25/buyplayercard.fxml"));
                Parent card = loader.load();
                PlayerCardItemController itemController = loader.getController();
                itemController.setPlayerData(player, username, userTeam, client);
                playerList.getChildren().add(card);
            } catch (IOException e) {
                System.out.println("Error loading buyplayercard.fxml: " + e.getMessage());
            }
        }
    }
}