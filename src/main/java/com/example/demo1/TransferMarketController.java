package com.example.demo1;

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
import java.net.URL;
import java.util.ArrayList;

public class TransferMarketController extends BuySell {
    private Stage stage;
    private Scene scene;
    private Parent root;

    String username;
    String userTeam;

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

    public void setUserData(String username, String userTeam) {
        this.username = username;
        this.userTeam = userTeam;
        System.out.println("TransferMarketController.setUserData called - username: " + username + ", userTeam: " + userTeam);
    }

    @FXML
    public void initialize() {
        System.out.println("TransferMarketController.initialize called - playerContainer: " + playerContainer);
        leagueComboBox.getItems().addAll("Premier League", "La Liga", "Bundesliga", "Serie A", "Ligue 1");
        positionComboBox.getItems().addAll("Goalkeeper", "Defender", "Midfielder", "Forward");
        balanceLabel.setText(String.format("$%.2f", getAccountBalance()));
    }

    @FXML
    public void searchPlayers() {
        String name = nameField.getText().trim().toLowerCase();
        String club = clubField.getText().trim().toLowerCase();
        String league = leagueComboBox.getValue();
        String position = positionComboBox.getValue();

        ArrayList<Player> filteredPlayers = new ArrayList<>();
        for (Player player : getAvailablePlayers().values()) {
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

    private void loadPlayers(ArrayList<Player> players) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("buyPlayerPage.fxml"));
            Parent buyPlayerRoot = loader.load();
            BuyPlayerController buyPlayerController = loader.getController();

            VBox playerList = buyPlayerController.playerList;
            playerList.getChildren().clear();

            for (Player player : players) {
                FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("buyplayercard.fxml"));
                AnchorPane card = cardLoader.load();
                PlayerCardItemController itemController = cardLoader.getController();
                itemController.setPlayerData(player);
                playerList.getChildren().add(card);
            }

            // Show the buyPlayerPage scene
            Stage stage = (Stage) playerContainer.getScene().getWindow();
            Scene scene = new Scene(buyPlayerRoot, 1215, 600, Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goBack(ActionEvent actionEvent) throws IOException {
        System.out.println("TransferMarketController.goBack called - username: " + username + ", userTeam: " + userTeam);
        System.out.println("playerContainer: " + playerContainer + ", scene: " + (playerContainer != null ? playerContainer.getScene() : "null"));
        if (playerContainer == null || playerContainer.getScene() == null) {
            System.out.println("Error: playerContainer or its scene is null");
            Label errorLabel = new Label("Error: Unable to navigate back due to UI initialization issue.");
            errorLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            AnchorPane errorPane = new AnchorPane(errorLabel);
            AnchorPane.setTopAnchor(errorLabel, 5.0);
            AnchorPane.setLeftAnchor(errorLabel, 5.0);
            Scene errorScene = new Scene(errorPane, 1215, 600, Color.NAVY);
            // Fallback to get the stage from the event source if possible
            Stage stage = (Stage) ((actionEvent.getSource() instanceof Button) ? ((Button) actionEvent.getSource()).getScene().getWindow() : new Stage());
            stage.setScene(errorScene);
            stage.setResizable(false);
            stage.show();
            return;
        }
        URL resource = getClass().getResource("players.fxml");
        if (resource == null) {
            System.out.println("Error: players.fxml not found in resources!");
            return;
        }
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        PlayerCardController controller = loader.getController();
        if (userTeam != null && !userTeam.trim().isEmpty()) {
            controller.setUserTeam(userTeam);
            controller.loadPlayersForTeam(userTeam);
            controller.setTeamLogo(userTeam);
        } else {
            System.out.println("Error: userTeam is null or empty, cannot load team players or logo");
            Label errorLabel = new Label("No team selected. Please select a team.");
            errorLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            AnchorPane errorPane = new AnchorPane(errorLabel);
            AnchorPane.setTopAnchor(errorLabel, 5.0);
            AnchorPane.setLeftAnchor(errorLabel, 5.0);
            Scene scene = new Scene(errorPane, 1215, 600, Color.NAVY);
            Stage stage = (Stage) playerContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            return;
        }
        controller.setUserName(username != null ? username : "Unknown");
        Stage stage = (Stage) playerContainer.getScene().getWindow();
        Scene scene = new Scene(root, 1215, 600, Color.NAVY);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public boolean buyPlayer(Player player) {
        boolean success = super.buyPlayer(player);
        if (success) {
            updateBalanceLabel();
            searchPlayers();
        }
        return success;
    }

    @FXML
    public void updateBalanceLabel() {
        balanceLabel.setText(String.format("$%.2f", getAccountBalance()));
    }

    @Override
    public boolean sellPlayer(Player player) {
        boolean success = super.sellPlayer(player);
        if (success) {
            updateBalanceLabel();
            searchPlayers();
        }
        return success;
    }

    @FXML
    public void goToSellPlayer(ActionEvent actionEvent) throws IOException {
        System.out.println("TransferMarketController.goToSellPlayer called - username: " + username + ", userTeam: " + userTeam);
        URL resource = getClass().getResource("sellPlayer.fxml");
        if (resource == null) {
            System.out.println("Error: sellPlayer.fxml not found in resources!");
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
}