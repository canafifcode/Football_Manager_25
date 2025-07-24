package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    private ImageView teamLogoView;

    private Player player;

    private static final List<String> NON_GK_STATS = Arrays.asList("Pace", "Passing", "Shooting", "Dribbling", "Defending", "Physical");
    private static final List<String> GK_STATS = Arrays.asList("Reflexes", "Handling", "Positioning", "Diving", "Kicking");

    public void setPlayerData(Player player) {
        // Validate FXML bindings
        if (nameLabel == null || positionLabel == null || overallLabel == null || statsLabel == null) {
            return;
        }

        this.player = player;

        // Set basic player data
        nameLabel.setText(player.getName() != null ? player.getName() : "Unknown");
        positionLabel.setText(player.getPosition() != null ? player.getPosition() : "Unknown");
        overallLabel.setText("Overall: " + player.getOverall());

        // Handle stats
        Map<String, Integer> stats = player.getStats();
        if (stats == null || stats.isEmpty()) {
            statsLabel.setText("Stats: None available");
        } else {
            StringBuilder statsText = new StringBuilder("Stats: ");
            List<String> relevantStats = player.getPosition().equalsIgnoreCase("GK") ? GK_STATS : NON_GK_STATS;
            boolean hasStats = false;
            for (String stat : relevantStats) {
                if (stats.containsKey(stat)) {
                    statsText.append(stat).append(": ").append(stats.get(stat)).append(", ");
                    hasStats = true;
                }
            }
            if (hasStats) {
                // Remove trailing comma and space
                statsText.setLength(statsText.length() - 2);
                statsLabel.setText(statsText.toString());
            } else {
                statsLabel.setText("Stats: None available");
            }
        }

        // Set team logo
        if (teamLogoView != null && player.getTeam() != null && !player.getTeam().isEmpty()) {
            String imagePath = "/logos/" + player.getTeam().toLowerCase() + ".png";
            try {
                Image logo = new Image(getClass().getResourceAsStream(imagePath));
                teamLogoView.setImage(logo);
                teamLogoView.setStyle("-fx-background-color: transparent;");
                teamLogoView.setSmooth(true);
                teamLogoView.setCache(true);
            } catch (Exception e) {
                System.out.println("Error loading logo for team: " + player.getTeam() + ", path: " + imagePath);
                teamLogoView.setImage(null);
            }
        }
    }


    @FXML
    public void sellPlayerAction(ActionEvent event) {
        if (player == null) {
            System.out.println("Error: No player selected for selling");
            return;
        }
        boolean success = sellPlayer(player);
        if (success) {
            System.out.println("Player " + player.getName() + " sold successfully");
            // Optionally refresh the UI by reloading players
            if (sellPlayerButton.getScene().getRoot().getUserData() instanceof SellPlayerController controller) {
                controller.loadOwnedPlayers();
            }
        } else {
            System.out.println("Failed to sell player " + player.getName());
        }
    }

    public void setPlayerData(Player player, String username, String userTeam) {
        this.player = player;
        setPlayerData(player);
        // Set user data if needed for further actions
    }

}