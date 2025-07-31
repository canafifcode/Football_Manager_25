package com.example.fm25;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class BuyPlayerController extends BuySell{

    private String userTeam;
    private String username;

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
        // After user data is set, load the players and update balance
        loadOwnedPlayers(userTeam,username);
        setBalanceLabel();
    }

    public void initialize() {
        System.out.println("BuyPlayerController initialized");
    }

    private void setBalanceLabel() {
        if (balanceLabel == null) {
            System.out.println("Error: balanceLabel is null");
            return;
        }
        double balance = getAccountBalance();
        balanceLabel.setText(String.format("$%.2f", balance));
    }

    public void switchToTransferMarket() throws IOException {
        System.out.println("BuyPlayerController.switchToTransferMarket called - username: " + username + ", userTeam: " + userTeam);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("transferMarket.fxml"));
        Parent root = loader.load();
        TransferMarketController controller = loader.getController();
        controller.setUserData(username, userTeam);
        scrollPane.getScene().setRoot(root);
    }
}