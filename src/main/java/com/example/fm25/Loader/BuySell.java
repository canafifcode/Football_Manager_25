package com.example.fm25.Loader;

import com.example.fm25.NetworkContext;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class BuySell {
    protected BuySell buySell;
    private ConcurrentMap<String, PlayerLoader> availablePlayers = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, PlayerLoader> ownedPlayers = new ConcurrentHashMap<>();
    private double accountBalance = 100000.0; // Initial balance
    private static String userName;
    private String userTeam;
    protected Button buyPlayerButton;

    public BuySell() {
        loadPlayersFromFile();
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserTeam(String userTeam) {
        this.userTeam = userTeam;
    }

    public ConcurrentMap<String, PlayerLoader> getAvailablePlayers() {
        return availablePlayers;
    }

    public ConcurrentMap<String, PlayerLoader> getOwnedPlayers() {
        return ownedPlayers;
    }

    public void addAvailablePlayer(PlayerLoader player) {
        availablePlayers.put(player.getName(), player);
    }

    public void addOwnedPlayer(PlayerLoader player) {
        ownedPlayers.put(player.getName(), player);
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public Scene getScene() {
        return buyPlayerButton != null ? buyPlayerButton.getScene() : null;
    }

    void loadPlayersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("players.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String league = parts[0].trim();
                    String team = parts[1].trim();
                    String name = parts[2].trim();
                    String position = parts[3].trim();
                    int overall = Integer.parseInt(parts[4].trim());
                    Map<String, Integer> stats = new HashMap<>();
                    for (int i = 5; i < parts.length; i++) {
                        String[] statParts = parts[i].split(":");
                        if (statParts.length == 2) {
                            stats.put(statParts[0].trim(), Integer.parseInt(statParts[1].trim()));
                        }
                    }
                    PlayerLoader player = new PlayerLoader(name, position, league, team, stats, overall);
                    availablePlayers.put(name, player);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading players.txt: " + e.getMessage());
        }
    }

    public boolean buyPlayer(PlayerLoader player) {
        double price = calculatePrice(player.getOverall());
        if (availablePlayers.containsKey(player.getName()) && !ownedPlayers.containsKey(player.getName()) && accountBalance >= price) {
            accountBalance -= price;
            ownedPlayers.put(player.getName(), player);
            availablePlayers.remove(player.getName());
            updatePlayersFile(player, "YourTeam"); // Change team to "YourTeam"
            System.out.println(player.getName() + " bought for $" + price + "! New balance: $" + accountBalance);
            return true;
        }
        System.out.println("Cannot buy " + player.getName() + ": Insufficient funds or already owned. Balance: $" + accountBalance);
        return false;
    }

    public boolean sellPlayer(PlayerLoader player) {
        if (availablePlayers.containsKey(player.getName())) {
            String fileInsert = "sell_Req_of_" + NetworkContext.getUsername() + ".txt";
            String fileInsertTwo = "sellablePlayers.txt";
            String filetaken = "owned_players_" + NetworkContext.getUserTeam() + "_" + NetworkContext.getUsername()  + ".txt";

            // Ensure files exist
            ensureFileExists(fileInsert);
            ensureFileExists(fileInsertTwo);

            try (
                    BufferedReader reader = new BufferedReader(new FileReader(filetaken));
                    FileWriter writer = new FileWriter(fileInsert, true);
                    FileWriter writerTwo = new FileWriter(fileInsertTwo, true)
            ) {
                String line;
                boolean playerFound = false;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6 && parts[2].trim().equals(player.getName())) {
                        writer.write(line + System.lineSeparator());
                        writerTwo.write(line + System.lineSeparator());
                        playerFound = true;
                    }
                }

                if (playerFound) {
                    System.out.println(fileInsert + " is ready (updated with sold player).");
                    System.out.println(fileInsertTwo + " is ready (updated with sold player).");
                } else {
                    System.out.println("Player " + player.getName() + " not found in owned players file");
                }

            } catch (IOException e) {
                System.out.println("Error creating/updating " + fileInsert + "/" + fileInsertTwo + ": " + e.getMessage());
                return false;
            }

            // Process the sell
            availablePlayers.remove(player.getName());
            updatePlayersFile(player, player.getTeam());
            accountBalance += calculatePrice(player.getOverall()) * 0.8; // 80% refund
            System.out.println(player.getName() + " sold! New balance: $" + accountBalance);
            return true;
        } else {
            System.out.println("Cannot sell " + player.getName() + ": Not in available players.");
            return false;
        }
    }

    private void ensureFileExists(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Created file: " + fileName);
            } catch (IOException e) {
                System.out.println("Error creating file " + fileName + ": " + e.getMessage());
            }
        }
    }

    public double calculatePrice(int overall) {
        return overall * 150.0;
    }

    public void updatePlayersFile(PlayerLoader player, String newTeam) {
        File inputFile = new File("players.txt");
        File tempFile = new File("temp_players.txt");
        try (
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                FileWriter writer = new FileWriter(tempFile)
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[2].trim().equals(player.getName())) {
                    String playerData = String.format("%s,%s,%s,%s,%d,%s",
                            parts[0], newTeam, player.getName(), player.getPosition(), player.getOverall(),
                            player.getStats().entrySet().stream()
                                    .map(entry -> entry.getKey() + ":" + entry.getValue())
                                    .collect(Collectors.joining(",")));
                    writer.write(playerData + System.lineSeparator());
                } else {
                    writer.write(line + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.err.println("Error updating players.txt: " + e.getMessage());
            return;
        }
        if (!inputFile.delete()) {
            System.err.println("Error deleting players.txt");
            return;
        }
        if (!tempFile.renameTo(inputFile)) {
            System.err.println("Error renaming temp_players.txt to players.txt");
        }
    }

    public static void createOrResetOwnedPlayersFile(String username, String userTeam) {
        userName=username;
        //USERTEAM=userTeam;
        String fileName = "owned_players_" + userTeam +"_" +username+ ".txt";
        try (
                BufferedReader reader = new BufferedReader(new FileReader("players.txt"));
                FileWriter writer = new FileWriter(fileName, false) // 'false' = overwrite mode
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[1].trim().equals(userTeam)) {
                    writer.write(line + System.lineSeparator());
                }
            }
            System.out.println(fileName + " is ready (created/overwritten with owned players).");
        } catch (IOException e) {
            System.out.println("Error creating/overwriting " + fileName + ": " + e.getMessage());
        }
    }

    public static void loadOwnedPlayers(String username, String userTeam) {
        String fileName = "owned_players_" + userTeam.replace(" ", "_") + "_" + username + ".txt";
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("Owned players file does not exist: " + fileName);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[1].trim().equals(userTeam)) {
                    String name = parts[2].trim();
                    String position = parts[3].trim();
                    int overall = Integer.parseInt(parts[4].trim());
                    Map<String, Integer> stats = new HashMap<>();
                    for (int i = 5; i < parts.length; i++) {
                        String[] statParts = parts[i].split(":");
                        if (statParts.length == 2) {
                            stats.put(statParts[0].trim(), Integer.parseInt(statParts[1].trim()));
                        }
                    }
                    PlayerLoader player = new PlayerLoader(name, position, parts[0].trim(), userTeam, stats, overall);
                    ownedPlayers.put(name, player);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading owned players: " + e.getMessage());
        }
    }
}