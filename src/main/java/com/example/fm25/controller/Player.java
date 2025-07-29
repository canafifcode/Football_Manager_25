package com.example.fm25.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    private String name;
    private String position;
    private String league;
    private String team;
    private Map<String, Integer> stats;
    private int overall;

    private static final List<String> NON_GK_STATS = Arrays.asList("Pace", "Passing", "Shooting", "Dribbling", "Defending", "Physical");
    private static final List<String> GK_STATS = Arrays.asList("Reflexes", "Handling", "Positioning", "Diving", "Kicking");

    public Player(String name, String position, String league, String team, Map<String, Integer> stats, int overall) {
        this.name = name;
        this.position = position;
        this.league = league;
        this.team = team;
        this.stats = stats;
        this.overall = overall;
    }

    public String getName() {
        return name;
    }
    public String getPosition() {
        return position;
    }
    public String getLeague() {
        return league;
    }
    public String getTeam() {
        return team;
    }
    public int getOverall() {
        return overall;
    }
    public Map<String, Integer> getStats() {
        return stats;
    }

    @Override
    public String toString() {
        return name + " (" + position + ")";
    }

    public List<Player> loadPlayersForTeam(String teamName) {
        List<Player> players = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("players.txt"))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String league = parts[0].trim();
                    String team = parts[1].trim();
                    String playerName = parts[2].trim();
                    String position = parts[3].trim();
                    int overall;
                    try {
                        overall = Integer.parseInt(parts[4].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid overall value at line " + lineNumber + " for player: " + playerName);
                        continue;
                    }
                    Map<String, Integer> stats = new HashMap<>();
                    List<String> validStats = position.equalsIgnoreCase("GK") ? GK_STATS : NON_GK_STATS;

                    if (team.equalsIgnoreCase(teamName)) {
                        String statString = parts[5].trim();
                        for (int i = 6; i < parts.length; i++) {
                            statString += ";" + parts[i].trim();
                        }
                        String[] statPairs = statString.split(";");
                        for (String pair : statPairs) {
                            String[] statParts = pair.split(":");
                            if (statParts.length == 2) {
                                try {
                                    String statKey = statParts[0].trim();
                                    int statValue = Integer.parseInt(statParts[1].trim());
                                    if (validStats.contains(statKey)) {
                                        stats.put(statKey, statValue);
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid stat value at line " + lineNumber + " for player: " + playerName + ", stat: " + pair);
                                }
                            }
                        }
                        if (stats.isEmpty()) {
                            System.out.println("No valid stats loaded at line " + lineNumber + " for player: " + playerName);
                        }
                        players.add(new Player(playerName, position, league, team, stats, overall));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading players.txt: " + e.getMessage());
        }
        if (players.isEmpty()) {
            System.out.println("No players found for team: " + teamName);
        }
        return players;
    }


    public List<Player> loadPlayersForMySell(String username) {
        List<Player> players = new ArrayList<>();
        String fileName="sell_Request_of_"+username+".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String league = parts[0].trim();
                    String team = parts[1].trim();
                    String playerName = parts[2].trim();
                    String position = parts[3].trim();
                    int overall;
                    try {
                        overall = Integer.parseInt(parts[4].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid overall value at line " + lineNumber + " for player: " + playerName);
                        continue;
                    }
                    Map<String, Integer> stats = new HashMap<>();
                    List<String> validStats = position.equalsIgnoreCase("GK") ? GK_STATS : NON_GK_STATS;

                    String statString = parts[5].trim();
                    for (int i = 6; i < parts.length; i++) {
                        statString += ";" + parts[i].trim();
                    }
                    String[] statPairs = statString.split(";");
                    for (String pair : statPairs) {
                        String[] statParts = pair.split(":");
                        if (statParts.length == 2) {
                            try {
                                String statKey = statParts[0].trim();
                                int statValue = Integer.parseInt(statParts[1].trim());
                                if (validStats.contains(statKey)) {
                                    stats.put(statKey, statValue);
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid stat value at line " + lineNumber + " for player: " + playerName + ", stat: " + pair);
                            }
                        }
                    }
                    if (stats.isEmpty()) {
                        System.out.println("No valid stats loaded at line " + lineNumber + " for player: " + playerName);
                    }
                    players.add(new Player(playerName, position, league, team, stats, overall));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading players.txt: " + e.getMessage());
        }
        if (players.isEmpty()) {
            System.out.println("No players found for username: " + username);
        }
        return players;
    }

}