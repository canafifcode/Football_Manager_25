package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

    private static final List<String> NON_GK_STATS = Arrays.asList("Pace", "Passing", "Shooting", "Dribbling", "Defending", "Physical");
    private static final List<String> GK_STATS = Arrays.asList("Reflexes", "Handling", "Positioning", "Diving", "Kicking");

    public void setPlayerData(Player player) {
        // Validate FXML bindings
        if (nameLabel == null || positionLabel == null || overallLabel == null || statsLabel == null) {
            return;
        }

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
    }
}