package com.example.fm25.controller;

import com.example.fm25.BuyRequestClient;
import com.example.fm25.Loader.BuySell;
import com.example.fm25.Loader.PlayerLoader;
import com.example.fm25.NetworkContext;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

public class TransferMarketController extends BuySell {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private String username;
    private String userTeam;
    private BuyRequestClient client;

    @FXML
    private TextField nameField;
    @FXML
    private TextField clubField;
    @FXML
    private ComboBox<String> leagueComboBox;
    @FXML
    private ComboBox<String> positionComboBox;
    @FXML
    private Button searchButton;
    @FXML
    private Button sellButton;
    @FXML
    private Button backButton;
    @FXML
    private Label balanceLabel;
    @FXML
    private AnchorPane playerContainer;
    @FXML
    private Button TransferListButton;

    public void setUserData(String username, String userTeam) {
        this.username = username;
        this.userTeam = userTeam;
        System.out.println("TransferMarketController.setUserData called - username: " + username + ", userTeam: " + userTeam);
        this.client = NetworkContext.getClient();
        updateBalanceLabel();
    }

    @FXML
    public void initialize() {
        Socket socket = NetworkContext.getSocket();
        if (socket == null || !socket.isConnected()) {
            displayError("Socket connection is not established. Please try again later.");
            return;
        }
        System.out.println("TransferMarketController.initialize called - playerContainer: " + playerContainer);
        leagueComboBox.getItems().addAll("Premier League", "La Liga", "Bundesliga", "Saudi Pro League", "Ligue 1","MLS");
        positionComboBox.getItems().addAll("GK", "Defender", "Midfielder", "Forward");
        if (balanceLabel != null) {
            balanceLabel.setText(String.format("$%.2f", getAccountBalance()));
        }
    }

    @FXML
    public void searchPlayers() {
        String name = nameField.getText().trim().toLowerCase();
        String club = clubField.getText().trim().toLowerCase();
        String league = leagueComboBox.getValue();
        String position = positionComboBox.getValue();

        ArrayList<PlayerLoader> filteredPlayers = new ArrayList<>();

        for (PlayerLoader player : getAvailablePlayers().values()) {
            boolean matches = true;
            if (!name.isEmpty() && !player.getName().toLowerCase().contains(name)) {
                matches = false;
            }
            if (!club.isEmpty() && !player.getTeam().toLowerCase().contains(club)) {
                matches = false;
            }
            if (league != null && !league.equals(player.getLeague())) {
                matches = false;
            }
            if (position != null && !position.equals(player.getPosition())) {
                matches = false;
            }
            if (matches) {
                filteredPlayers.add(player);
            }
        }
        loadPlayers(filteredPlayers);
    }

    public void handleButtonClick(ActionEvent event) {
        System.out.println("Button clicked!");
    }

    private void loadPlayers(ArrayList<PlayerLoader> players) {
        try {
            URL resource = getClass().getResource("/com/example/fm25/buyPlayerPage.fxml");
            if (resource == null) {
                System.out.println("Error: buyPlayerPage.fxml not found in resources!");
                displayError("Unable to load player list. Resource not found.");
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent buyPlayerRoot = loader.load();
            BuyPlayerController buyPlayerController = loader.getController();
            buyPlayerController.setUserData(username, userTeam);

            VBox playerList = buyPlayerController.playerList;
            playerList.getChildren().clear();

            for (PlayerLoader player : players) {
                FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("/com/example/fm25/buyplayercard.fxml"));
                if (cardLoader.getLocation() == null) {
                    System.out.println("Error: buyplayercard.fxml not found in resources!");
                    displayError("Unable to load player card. Resource not found.");
                    return;
                }
                AnchorPane card = cardLoader.load();
                PlayerCardItemController itemController = cardLoader.getController();
                itemController.setPlayerData(player, username, userTeam, client);
                playerList.getChildren().add(card);
            }

            Stage stage = (Stage) playerContainer.getScene().getWindow();
            Scene scene = new Scene(buyPlayerRoot, 1215, 600, Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            System.out.println("Error loading buyPlayerPage.fxml: " + e.getMessage());
            e.printStackTrace();
            displayError("Unable to load player list: " + e.getMessage());
        }
    }

    private void displayError(String message) {
        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        if (playerContainer != null) {
            playerContainer.getChildren().clear();
            playerContainer.getChildren().add(errorLabel);
        } else {
            System.out.println("Error: playerContainer is null, cannot display error message");
        }
    }

    @FXML
    public void goBack(ActionEvent actionEvent) throws IOException {
        System.out.println("TransferMarketController.goBack called by " + username);
        if (client != null) {
            //client.close();
        }
        if (playerContainer == null || playerContainer.getScene() == null) {
            System.out.println("Error: playerContainer or its scene is null");
            displayError("Unable to navigate back due to UI initialization issue.");
            return;
        }
        URL resource = getClass().getResource("/com/example/fm25/players.fxml");
        if (resource == null) {
            System.out.println("Error: players.fxml not found in resources!");
            displayError("Unable to navigate back. Resource not found.");
            return;
        }
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        HomePageController controller = loader.getController();
        if (userTeam != null && !userTeam.trim().isEmpty()) {
            controller.setUserTeam(userTeam);
            controller.loadPlayersForTeam(userTeam);
            controller.setTeamLogo(userTeam);
        } else {
            System.out.println("Error: userTeam is null or empty, cannot load team players or logo");
            displayError("No team selected. Please select a team.");
            return;
        }
        controller.setUserName(username != null ? username : "Unknown");
        Stage stage = (Stage) playerContainer.getScene().getWindow();
        Scene scene = new Scene(root, 1215, 600, Color.NAVY);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

//    @Override
//    public synchronized boolean buyPlayer(PlayerLoader player, String username, String teamName) {
//        boolean success = super.buyPlayer(player, username, teamName);
//        if (success) {
//            Platform.runLater(() -> {
//                updateBalanceLabel();
//                searchPlayers();
//            });
//        }
//        return success;
//    }

    @FXML
    public void updateBalanceLabel() {
        if (balanceLabel != null) {
            balanceLabel.setText(String.format("$%.2f", getAccountBalance()));
        }
    }

    @Override
    public boolean sellPlayer(PlayerLoader player) {
        boolean success = super.sellPlayer(player);
        if (success) {
            updateBalanceLabel();
            searchPlayers();
        }
        return success;
    }

    @FXML
    public void goToSellPlayer(ActionEvent actionEvent) throws IOException {
        System.out.println("TransferMarketController.goToSellPlayer called by " + username);
        // Do NOT close the socket here!
        // Just close the BuyRequestClient (thread/streams)
        if (client != null) {
           // client.close();
        }
        URL resource = getClass().getResource("/com/example/fm25/sellPlayer.fxml");
        if (resource == null) {
            System.out.println("Error: sellPlayer.fxml not found in resources!");
            displayError("Unable to load sell player page. Resource not found.");
            return;
        }
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        SellPlayerController controller = loader.getController();
        controller.setUserData(username, userTeam);
        Stage stage = (Stage) playerContainer.getScene().getWindow();
        Scene scene = new Scene(root, 1215, 600, Color.NAVY);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    public void goToTransferList(ActionEvent event) throws IOException {
        System.out.println("TransferMarketController.goToTransferList called by " + username);
        URL resource = getClass().getResource("/com/example/fm25/transfer_listed.fxml");
        if (resource == null) {
            System.out.println("Error: transfer_listed.fxml not found in resources!");
            return;
        }
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        TransferListController controller = loader.getController();
        controller.setUserData(username, userTeam);
        root.setUserData(controller); // Set userData
        Stage stage = (Stage) playerContainer.getScene().getWindow();
        Scene scene = new Scene(root, 1215, 600, Color.NAVY);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}