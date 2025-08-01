package com.example.fm25;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuySell {
    private Map<String, Player> availablePlayers;
    private Map<String, Player> ownedPlayers;
    private double accountBalance; // Initial balance
    private String userTeam = "YourTeam";
    private static String userName;
    private static String USERTEAM;
    private static String userLeague;

    public static void setUserTeamStatic(String userTeam){
        BuySell.USERTEAM=userTeam;
    }

    public static void setUserNameStatic(String userName){
        BuySell.userName=userName;
    }

    public  String getUserTeamStatic(){
        return USERTEAM;
    }

    public static void setUserLeagueStatic(String userLeague){
        BuySell.userLeague=userLeague;
    }

    public String getUserNameStatic(){
        return userName;
    }

    public String getUserLeagueStatic(){
        return userLeague;
    }

    public  void setUserAccountBalance() throws IOException {
        String line=getLineContaining("users.txt",userName);
        String[] parts = line.split(",");
        double balance=Double.parseDouble(parts[4]);
        accountBalance=balance;
    }

    public BuySell() throws IOException {
        availablePlayers = new HashMap<>();
        ownedPlayers = new HashMap<>();
        loadPlayersFromFile(userTeam);
        setUserAccountBalance();
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

    public boolean buyPlayer(Player player) throws IOException {
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

    public boolean sellPlayer(Player player) throws IOException {
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
                        writerTwo.write(line +","+userName+ System.lineSeparator());
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
                accountBalance += calculatePrice(player.getOverall()) ;
                //System.out.println(player.getName() + " sold! New balance: $" + accountBalance);
                return true;
            }
            else return false;
        }
        else{
            System.out.println("Cannot sell " + player.getName() + ": Not owned.");
            return false;
        }
    }

    public boolean buyothersPlayer(Player player) throws IOException {
        String line=getLineContaining("sellablePlayers.txt",player.getName());
        String[] parts = line.split(",");
        String owner=parts[6];
        String newline=getUserLeagueStatic()+","+getUserTeamStatic()+","+parts[2]+","+parts[3]+","+parts[4]+","+parts[5];
        //System.out.println(newline);
        String filePathOwnedSell="sell_Request_of_"+owner+".txt";
        String ownedPlayersPath="owned_players_"+player.getTeam()+"_"+owner+".txt";
        String keyWord=player.getName();
        deleteLinesContaining(filePathOwnedSell,keyWord);
        deleteLinesContaining("sellablePlayers.txt",keyWord);
        deleteLinesContaining(ownedPlayersPath,keyWord);
        String fileInsert = "owned_players_"+getUserTeamStatic()+"_"+getUserNameStatic()+".txt";
        try(FileWriter writer = new FileWriter(fileInsert, true)){
            writer.write(newline + System.lineSeparator());
        }catch (IOException e) {
            System.out.println("Error writing the changed player" );
        }

        Double overall= (double) player.getOverall()*150;
        String buyerLine=getLineContaining("users.txt",getUserNameStatic());
        String sellerLine=getLineContaining("users.txt",owner);
        String[] parts2 = buyerLine.split(",");
        String[] parts3 = sellerLine.split(",");
        Double buyerBalance=Double.parseDouble(parts2[4]);
        Double sellerBalance=Double.parseDouble(parts3[4]);
        buyerBalance-=overall;
        sellerBalance+=overall;
        String newBuyerLine=parts2[0]+","+parts2[1]+","+parts2[2]+","+parts2[3]+","+buyerBalance;
        String newSellerLine=parts3[0]+","+parts3[1]+","+parts3[2]+","+parts3[3]+","+sellerBalance;
        deleteLinesContaining("users.txt",getUserNameStatic());
        deleteLinesContaining("users.txt",owner);
        try(FileWriter writer2 = new FileWriter("users.txt", true)){
            writer2.write(newBuyerLine + System.lineSeparator());
            writer2.write(newSellerLine + System.lineSeparator());
        }catch (IOException e) {
            System.out.println("Error writing the balance" );
        }
        return true;
    }

    public  void deleteLinesContaining(String filePath, String keyword) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        List<String> filteredLines = new ArrayList<>();

        for (String line : lines) {
            if (!line.contains(keyword)) {
                filteredLines.add(line);
            }
        }

        Files.write(Paths.get(filePath), filteredLines);
    }

    public String getLineContaining(String filePath, String keyword) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            if (line.contains(keyword)) {
                return line; // return the first matching line
            }
        }
        return null; // or return "Not Found"
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

    public double getAccountBalance() throws IOException {
        setUserAccountBalance();
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