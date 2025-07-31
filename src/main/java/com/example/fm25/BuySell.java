package com.example.fm25;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class BuySell {
    private Map<String, Player> availablePlayers;
    private Map<String, Player> ownedPlayers;
    private double accountBalance = 100000.0; // Initial balance
    private String userTeam = "YourTeam";
    private static String userName;
    private static String USERTEAM;

    public BuySell() {
        availablePlayers = new HashMap<>();
        ownedPlayers = new HashMap<>();
        loadPlayersFromFile(userTeam);
    }

    void loadPlayersFromFile(String userTeam) {
        try (BufferedReader reader = new BufferedReader(new FileReader("players.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String league = parts[0].trim();
                    String team = parts[1].trim();
                    this.userTeam = team;
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
                    Player player = new Player(name, position, league, team, stats, overall);
                    availablePlayers.put(name, player);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading players.txt: " + e.getMessage());
        }
    }

    public boolean buyPlayer(Player player) {
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

    public boolean sellPlayer(Player player) {
        if (availablePlayers.containsKey(player.getName())) {
            String fileInsert = "sell_Request_of_" +userName+ ".txt";
            String fileInsertTwo= "sellablePlayers.txt";
            String filetaken = "owned_players_"+USERTEAM+"_" +userName+ ".txt";
            try (
                    BufferedReader reader = new BufferedReader(new FileReader(filetaken));
                    FileWriter writer = new FileWriter(fileInsert, true); // 'false' = overwrite mode
                    FileWriter writerTwo = new FileWriter(fileInsertTwo, true)
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6 && parts[2].trim().equals(player.getName())) {
                        writer.write(line + System.lineSeparator());
                        writerTwo.write(line + System.lineSeparator());
                    }
                }
                System.out.println(fileInsert + " is ready (created/overwritten with owned players).");
                System.out.println(fileInsertTwo + " is ready (created/overwritten with owned players).");
            } catch (IOException e) {
                System.out.println("Error creating/overwriting " + fileInsert +"/"+fileInsertTwo+ ": " + e.getMessage());
            }

            //if(sellRequest(player))
            if(true){
                availablePlayers.remove(player.getName());
                //availablePlayers.put(player.getName(), player);
                updatePlayersFile(player, player.getTeam()); // Revert to original team
                accountBalance += calculatePrice(player.getOverall()) * 0.8; // 80% refund
                System.out.println(player.getName() + " sold! New balance: $" + accountBalance);
                return true;
            }
            else return false;
        }
        else{
            System.out.println("Cannot sell " + player.getName() + ": Not owned.");
            return false;
        }
    }

    double calculatePrice(int overall) {
        return overall * 150.0;
    }

    private void updatePlayersFile(Player player, String newTeam) {
        try {
            File inputFile = new File("players.txt");
            File tempFile = new File("temp.txt");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[2].trim().equals(player.getName())) {
                    parts[1] = newTeam; // Update team
                    line = String.join(",", parts);
                }
                writer.write(line + System.lineSeparator());
            }
            reader.close();
            writer.close();
            inputFile.delete();
            tempFile.renameTo(inputFile);
        } catch (IOException e) {
            System.out.println("Error updating players.txt: " + e.getMessage());
        }
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public Map<String, Player> getAvailablePlayers() {
        return availablePlayers;
    }

    public Map<String, Player> getOwnedPlayers() {
        return ownedPlayers;
    }

    public void addOwnedPlayer(Player player) {
        if (!ownedPlayers.containsKey(player.getName())) {
            ownedPlayers.put(player.getName(), player);
            System.out.println(player.getName() + " added to owned players.");
        }
    }


    public static void createOrResetOwnedPlayersFile(String username, String userTeam) {
        userName=username;
        USERTEAM=userTeam;
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


    public void loadOwnedPlayers(String username, String userTeam) {
        String fileName = "owned_players_" + userTeam +"_" +username+ ".txt";
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
                    Player player = new Player(name, position, "League", userTeam, stats, overall);
                    ownedPlayers.put(name, player);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading owned players: " + e.getMessage());
        }
    }

    public void addAvailablePlayer(Player player) {
        if (!availablePlayers.containsKey(player.getName())) {
            availablePlayers.put(player.getName(), player);
            System.out.println(player.getName() + " added to available players.");
        } else {
            System.out.println(player.getName() + " is already in the available players list.");
        }
    }

}