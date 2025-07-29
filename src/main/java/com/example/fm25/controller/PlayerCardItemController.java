package com.example.fm25.controller;

import com.example.fm25.BuyRequestClient;
import com.example.fm25.Loader.BuySell;
import com.example.fm25.Loader.PlayerLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PlayerCardItemController extends BuySell {

    @FXML
    private Label nameLabel;

    @FXML
    private Label positionLabel;

    @FXML
    private Label overallLabel;

    @FXML
    private Label statsLabel;

    @FXML
    private Button sellPlayerButton;

    @FXML
    private Button buyPlayerButton;

    @FXML
    private ImageView teamLogoView;

    private PlayerLoader player;
    private String username;
    private String userTeam;
    private BuyRequestClient client;

    private static final List<String> NON_GK_STATS = Arrays.asList("Pace", "Passing", "Shooting", "Dribbling", "Defending", "Physical");
    private static final List<String> GK_STATS = Arrays.asList("Reflexes", "Handling", "Positioning", "Diving", "Kicking");

    public void setPlayerData(PlayerLoader player, String username, String userTeam, BuyRequestClient client) {
        this.player = player;
        this.username = username;
        this.userTeam = userTeam;
        this.client = client;
        setPlayerData(player);
    }

    public void setPlayerData(PlayerLoader player) {
        this.player = player;

        if (nameLabel != null) nameLabel.setText(player.getName() != null ? player.getName() : "Unknown");
        if (positionLabel != null) positionLabel.setText(player.getPosition() != null ? player.getPosition() : "Unknown");
        if (overallLabel != null) overallLabel.setText("Overall: " + player.getOverall());

        Map<String, Integer> stats = player.getStats();
        if (statsLabel != null) {
            if (stats == null || stats.isEmpty()) {
                statsLabel.setText("Stats: None available");
            } else {
                StringBuilder statsText = new StringBuilder("Stats: ");
                List<String> relevantStats = player.getPosition() != null && player.getPosition().equalsIgnoreCase("GK") ? GK_STATS : NON_GK_STATS;
                boolean hasStats = false;
                for (String stat : relevantStats) {
                    if (stats.containsKey(stat)) {
                        statsText.append(stat).append(": ").append(stats.get(stat)).append(", ");
                        hasStats = true;
                    }
                }
                if (hasStats) {
                    statsText.setLength(statsText.length() - 2);
                    statsLabel.setText(statsText.toString());
                } else {
                    statsLabel.setText("Stats: None available");
                }
            }
        }

        if (teamLogoView != null && player.getTeam() != null && !player.getTeam().isEmpty()) {
            String imagePath = "/logos/" + player.getTeam().toLowerCase() + ".png";
            try {
                Image logo = new Image(getClass().getResourceAsStream(imagePath));
                teamLogoView.setImage(logo);
                teamLogoView.setStyle("-fx-background-color: transparent;");
                teamLogoView.setSmooth(true);
                teamLogoView.setCache(true);
            } catch (Exception e) {
                teamLogoView.setImage(null);
            }
        }
    }

    @FXML
    public void buyPlayerAction(ActionEvent event) {
        if (player == null || username == null || userTeam == null || client == null) {
            System.out.println("Error: Player, username, userTeam, or client is null");
            return;
        }

        String buyMessage = String.format("Request to buy %s from %s by %s", player.getName(), player.getTeam(), username);
        boolean success = client.sendBuyRequest(buyMessage);
        if (success) {
            buyPlayerButton.setDisable(true);
            buyPlayerButton.setText("Bought");
            System.out.println("Buy request successful for " + player.getName());
            refreshParentController();
        } else {
            System.out.println("Failed to buy " + player.getName());
        }
    }

    @FXML
    public void sellPlayerAction(ActionEvent event) {
        if (player == null || username == null || userTeam == null || client == null) {
            System.out.println("Error: Player, username, userTeam, or client is null");
            return;
        }

        String sellMessage = String.format("Request to sell %s from %s by %s", player.getName(), userTeam, username);
        boolean success = client.sendBuyRequest(sellMessage);
        if (success) {
            sellPlayerButton.setDisable(true);
            sellPlayerButton.setText("Sold");
            System.out.println("Sell request successful for " + player.getName());
            refreshParentController();
        } else {
            System.out.println("Failed to sell " + player.getName());
        }
    }

    private void refreshParentController() {
        Object controller = sellPlayerButton.getScene().getRoot().getUserData();
        if (controller instanceof SellPlayerController sellController) {
            sellController.loadOwnedPlayers();
            sellController.setBalanceLabel();
        } else if (controller instanceof BuyPlayerController buyController) {
            buyController.loadOwnedPlayers(username, userTeam);
            buyController.setBalanceLabel();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("transferMarket.fxml"));
                loader.load();
                TransferMarketController transferController = loader.getController();
                transferController.setUserData(username, userTeam);
                transferController.searchPlayers();
                transferController.updateBalanceLabel();
            } catch (Exception e) {
                System.out.println("Error refreshing parent controller: " + e.getMessage());
            }
        }
    }
}