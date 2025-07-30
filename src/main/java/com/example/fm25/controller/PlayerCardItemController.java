package com.example.fm25.controller;

import com.example.fm25.BuyRequestClient;
import com.example.fm25.Loader.BuySell;
import com.example.fm25.Loader.PlayerLoader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.InputStream;
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

    @FXML
    private ImageView playerView;

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

        if (playerView != null && player.getName() != null && !player.getName().isEmpty()) {
            String imagePath = "/card/dummy.png";
            if(player.getTeam().equals("Barcelona") || player.getTeam().equals("Real Madrid")){
                imagePath = "/card/" + player.getName().toLowerCase() + ".png";
            }
            else if( player.getName().equals("Ronaldo")){
                imagePath = "/card/" + player.getName().toLowerCase() + ".png";
            }
            else if( player.getName().equals("Messi")){
                imagePath = "/card/" + player.getName().toLowerCase() + ".png";
            }
            try {
                Image photo = new Image(getClass().getResourceAsStream(imagePath));
                playerView.setImage(photo);
                playerView.setStyle("-fx-background-color: transparent;");
                playerView.setSmooth(true);
                playerView.setCache(true);
            } catch (Exception e) {
                playerView.setImage(null);
            }
        }
    }

    @FXML
    public void sellPlayerAction(ActionEvent event) {
        if (player == null || username == null || userTeam == null || client == null) {
            System.err.println("Error: Player, username, userTeam, or client is null");
            showError("Cannot sell player: Invalid data");
            return;
        }
        String sellMessage = String.format("Request to sell %s from %s by %s", player.getName(), userTeam, username);
        System.out.println("Sending sell message: " + sellMessage);
        sellPlayerButton.setDisable(true);
        new Thread(() -> {
            boolean success = client.sendBuyRequest(sellMessage);
            Platform.runLater(() -> {
                if (success) {
                    sellPlayerButton.setText("Requested");
                    System.out.println("Sell request successful for " + player.getName());
                    refreshParentController();
                } else {
                    sellPlayerButton.setDisable(false);
                    System.out.println("Failed to sell " + player.getName());
                    showError("Failed to sell player: " + player.getName());
                }
            });
        }).start();
    }

    @FXML
    public void buyPlayerAction(ActionEvent event) {
        if (player == null || username == null || userTeam == null || client == null) {
            System.err.println("Error: Player, username, userTeam, or client is null");
            showError("Cannot buy player: Invalid data");
            return;
        }
        String buyMessage = String.format("Request to buy %s from %s by %s", player.getName(), player.getTeam(), username);
        System.out.println("Sending buy message: " + buyMessage);
        buyPlayerButton.setDisable(true);
        new Thread(() -> {
            boolean success = client.sendBuyRequest(buyMessage);
            Platform.runLater(() -> {
                if (success) {
                    buyPlayerButton.setText("Bought");
                    System.out.println("Buy request successful for " + player.getName());
                    addOwnedPlayer(player);
                    getAvailablePlayers().remove(player.getName());
                    refreshParentController();
                } else {
                    buyPlayerButton.setDisable(false);
                    System.out.println("Failed to buy " + player.getName());
                    showError("Failed to buy player: " + player.getName());
                }
            });
        }).start();
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Label errorLabel = new Label(message);
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            Button button = sellPlayerButton != null ? sellPlayerButton : buyPlayerButton;
            if (button != null && button.getScene() != null) {
                VBox container = (VBox) button.getScene().getRoot().lookup("#playerList");
                if (container != null) {
                    container.getChildren().add(errorLabel);
                } else {
                    System.err.println("Error: Could not find playerList to display error");
                }
            } else {
                System.err.println("Error: No valid button available to access scene for error display");
            }
        });
    }

    private void refreshParentController() {
        Object controller = sellPlayerButton != null ? sellPlayerButton.getScene().getRoot().getUserData() :
                buyPlayerButton != null ? buyPlayerButton.getScene().getRoot().getUserData() : null;
        if (controller instanceof SellPlayerController sellController) {
            sellController.loadOwnedPlayers();
            sellController.setBalanceLabel();
        } else if (controller instanceof BuyPlayerController buyController) {
            buyController.refreshPlayerList();
            buyController.setBalanceLabel();
        } else if (controller instanceof TransferMarketController transferController) {
            transferController.searchPlayers();
            transferController.updateBalanceLabel();
        } else if (controller instanceof TransferListController transferListController) {
            transferListController.loadOwnedSellrequestedPlayers();
            transferListController.loadOthersSellrequestedPlayers();
            transferListController.setBalanceLabel();
        } else {
            System.err.println("Error: Parent controller not recognized");
        }
    }

    public void setPlayerData(PlayerLoader player, String username, String userTeam) {

    }
}