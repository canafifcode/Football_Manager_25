package com.example.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class SellPlayerController extends BuySell {

    @FXML
    private AnchorPane playerContainer;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button backButton;

    @FXML
    private Label balanceLabel;

    private String username;
    private String userTeam;

    public void setUserData(String username, String userTeam) {
        this.username = username;
        this.userTeam = userTeam;
        System.out.println("SellPlayerController.setUserData called - username: " + username + ", userTeam: " + userTeam);
        if (userTeam != null && !userTeam.trim().isEmpty()) {
            loadPlayersFromFile(userTeam);
            setBalanceLabel();
        }
    }

    @FXML
    public void initialize() {
        System.out.println("SellPlayerController.initialize called - playerContainer: " + playerContainer + ", scene: " + (playerContainer != null ? playerContainer.getScene() : "null"));
        if (userTeam != null && !userTeam.trim().isEmpty()) {
            loadPlayersFromFile(userTeam);
            setBalanceLabel();
        } else {
            System.out.println("Warning: userTeam is null or empty during initialize");
        }
    }

    private void loadPlayersFromFile(String userTeam) {
        if (scrollPane == null) {
            System.out.println("Error: scrollPane is null");
            return;
        }
        ArrayList<Player> ownedPlayers = new ArrayList<>(getOwnedPlayers().values()); // Assuming getOwnedPlayers() is defined in BuySell
        VBox playerList = new VBox(5);
        scrollPane.setContent(playerList);

        if (ownedPlayers.isEmpty()) {
            Label noPlayersLabel = new Label("No players available to sell for " + userTeam);
            noPlayersLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            playerList.getChildren().add(noPlayersLabel);
            return;
        }

        for (Player player : ownedPlayers) {
            Label playerLabel = new Label(player.getName() + " (" + player.getTeam() + ")");
            playerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            playerList.getChildren().add(playerLabel);
        }
    }

    private void setBalanceLabel() {
        if (balanceLabel == null) {
            System.out.println("Error: balanceLabel is null");
            return;
        }
        double balance = getAccountBalance(); // Assuming getAccountBalance() is defined in BuySell
        balanceLabel.setText(String.format("$%.2f", balance));
    }

    @FXML
    public void switchToTransferMarket() throws IOException {
        System.out.println("SellPlayerController.switchToTransferMarket called - username: " + username + ", userTeam: " + userTeam);
        if (playerContainer == null || playerContainer.getScene() == null) {
            System.out.println("Error: playerContainer or its scene is null in switchToTransferMarket");
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("transferMarket.fxml"));
        Parent root = loader.load();
        TransferMarketController controller = loader.getController();
        controller.setUserData(username, userTeam);
        playerContainer.getScene().setRoot(root);
    }
}