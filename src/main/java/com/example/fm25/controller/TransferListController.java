package com.example.fm25.controller;

import com.example.fm25.Loader.BuySell;
import com.example.fm25.Loader.PlayerLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.List;
import com.example.fm25.BuyRequestClient;
import com.example.fm25.NetworkContext;

public class TransferListController extends BuySell {

    private String username;
    private String userTeam;
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
        if (client != null) {
            // Server broadcasts arrive on the JavaFX thread via this listener,
            // so the page refreshes automatically when anyone sells or buys.
            client.setTransferListListener(this::renderTransferLists);
            renderTransferLists(client.getTransferList());
        } else {
            System.out.println("Error: no network client available; transfer list cannot be shown");
        }
        setBalanceLabel();
    }

    private void renderTransferLists(List<PlayerLoader> transferList) {
        renderList(playermytfList,
                transferList.stream().filter(p -> p.getTeam().equalsIgnoreCase(userTeam)).toList(),
                "No players listed for sale by you");
        renderList(playerothertfList,
                transferList.stream().filter(p -> !p.getTeam().equalsIgnoreCase(userTeam)).toList(),
                "No players from other teams for sale");
    }

    private void renderList(VBox container, List<PlayerLoader> players, String emptyMessage) {
        if (container == null) {
            System.out.println("Error: transfer list VBox is null");
            return;
        }
        container.getChildren().clear();

        if (players.isEmpty()) {
            Label noPlayersLabel = new Label(emptyMessage);
            noPlayersLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            container.getChildren().add(noPlayersLabel);
            return;
        }

        for (PlayerLoader player : players) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fm25/othersSellCard.fxml"));
                AnchorPane card = loader.load();
                PlayerCardItemController itemController = loader.getController();
                itemController.setPlayerData(player, username, userTeam, this.client, this);
                container.getChildren().add(card);
            } catch (IOException e) {
                System.out.println("Error loading othersSellCard.fxml for player: " + player.getName());
                e.printStackTrace();
            }
        }
    }

    public void loadOwnedSellrequestedPlayers() {
        if (client != null) {
            renderTransferLists(client.getTransferList());
        }
    }

    public void loadOthersSellrequestedPlayers() {
        // Both panes are rendered together from the server's transfer list.
        loadOwnedSellrequestedPlayers();
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
        if (client != null) {
            client.setTransferListListener(null); // this page is going away
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fm25/transferMarket.fxml"));
        Parent root = loader.load();
        TransferMarketController controller = loader.getController();
        controller.setUserData(username, userTeam);
        othersscrollPane.getScene().setRoot(root);
    }

}
