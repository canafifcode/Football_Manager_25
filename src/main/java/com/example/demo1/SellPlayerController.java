package com.example.demo1;

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

public class SellPlayerController extends BuySell {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox playerList; // <-- Use VBox defined in FXML

    @FXML
    private Button backButton;

    @FXML
    private Label balanceLabel;

    private String username;
    private String userTeam;

    @FXML
    private ImageView teamLogoView;

    @FXML
    private Label userNameLabel;

    public void setUserTeam(String userTeam) {
        this.userTeam = userTeam;
        System.out.println("setUserTeam called - userTeam: " + userTeam);
    }

    public void setUserData(String username, String userTeam) {
        this.username = username;
        this.userTeam = userTeam;
        System.out.println("SellPlayerController.setUserData called - username: " + username + ", userTeam: " + userTeam);
        // After user data is set, load the players and update balance
        loadOwnedPlayers();
        setBalanceLabel();
    }

    // Call this after userTeam is set or upon page load
    public void loadOwnedPlayers() {
        if (userTeam == null || userTeam.trim().isEmpty()) {
            System.out.println("Error: userTeam is null or empty in loadOwnedPlayers");
            return;
        }
        Player playerLoader = new Player("", "", "", "", null, 0);
        List<Player> players = playerLoader.loadPlayersForTeam(userTeam);

        if (playerList == null) {
            System.out.println("Error: playerList VBox is null");
            return;
        }

        playerList.getChildren().clear();

        if (players.isEmpty()) {
            Label noPlayersLabel = new Label("No players found for team: " + userTeam);
            noPlayersLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            playerList.getChildren().add(noPlayersLabel);
            return;
        }

        for (Player player : players) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("sellPlayerCard.fxml"));
                AnchorPane card = loader.load();
                PlayerCardItemController itemController = loader.getController();
                itemController.setPlayerData(player, username, userTeam); // Pass user info if needed
                playerList.getChildren().add(card);
            } catch (IOException e) {
                System.out.println("Error loading sellPlayerCard.fxml for player: " + player.getName());
                e.printStackTrace();
            }
        }
    }

    private void setBalanceLabel() {
        if (balanceLabel == null) {
            System.out.println("Error: balanceLabel is null");
            return;
        }
        double balance = getAccountBalance();
        balanceLabel.setText(String.format("$%.2f", balance));
    }

    @FXML
    public void switchToTransferMarket() throws IOException {
        System.out.println("SellPlayerController.switchToTransferMarket called - username: " + username + ", userTeam: " + userTeam);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("transferMarket.fxml"));
        Parent root = loader.load();
        TransferMarketController controller = loader.getController();
        controller.setUserData(username, userTeam);
        scrollPane.getScene().setRoot(root);
    }
}