package com.example.fm25.Server;

import com.example.fm25.Loader.BuySell;
import com.example.fm25.Loader.PlayerLoader;
import com.example.fm25.NetworkContext;
import com.example.fm25.util.NetWorkUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientHandler implements Runnable {
    private final NetWorkUtil netWorkUtil;
    private final HashMap<String, info> clientMap;
    private final List<PlayerLoader> transferPlayerList;
    private final BuySell buySell;

    public ClientHandler(NetWorkUtil netWorkUtil, HashMap<String, info> clientMap, List<PlayerLoader> transferPlayerList, BuySell buySell) {
        this.netWorkUtil = netWorkUtil;
        this.clientMap = clientMap;
        this.transferPlayerList = transferPlayerList;
        this.buySell = buySell;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object obj = netWorkUtil.read();
                if (obj instanceof String) {
                    String message = (String) obj;
                    processMessage(message);
                } else if (obj == null) {
                    System.out.println("Client disconnected");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("ClientHandler error: " + e.getMessage());
            e.printStackTrace(); // ADD THIS LINE
        } finally {
            netWorkUtil.closeNetwork();
        }
    }

    private void processMessage(String message) {
        try {
            // Expected message format: "Request to [buy/sell] [playerName] from [team] by [username]"
            String[] parts = message.split(" ");
            if (parts.length < 7 || !parts[0].equals("Request") || !parts[1].equals("to")) {
                netWorkUtil.write("Invalid request format");
                return;
            }

            String action = parts[2].toLowerCase();
            String playerName = parts[3];
            String username = parts[parts.length-1].trim();
            info clientInfo = clientMap.get(username);

            if (clientInfo == null) {
                netWorkUtil.write("Error: User not registered");
                return;
            }

            PlayerLoader player = buySell.getAvailablePlayers().get(playerName);
            if (player == null) {
                player = buySell.getOwnedPlayers().get(playerName);
                if (player == null) {
                    netWorkUtil.write("Error: Player " + playerName + " not found");
                    return;
                }
            }

            synchronized (transferPlayerList) {
                if (action.equals("buy")) {
                    boolean success = buySell.buyPlayer(player);
                    if (success) {
                        transferPlayerList.remove(player);
                        buySell.addOwnedPlayer(player);
                        buySell.createOrResetOwnedPlayersFile(username, clientInfo.getTeamName());
                        netWorkUtil.write("Success: Bought " + playerName + " for $" + (player.getOverall() * 10.0));
                        broadcastTransferList(); // Broadcast updated list
                    } else {
                        netWorkUtil.write("Error: Cannot buy " + playerName + ". Insufficient funds or already owned.");
                    }
                } else if (action.equals("sell")) {
                    if (!player.getTeam().equals(clientInfo.getTeamName())) {
                        netWorkUtil.write("Error: You do not own " + playerName);
                        return;
                    }
                    boolean success = buySell.sellPlayer(player);
                    if (success) {
                        transferPlayerList.add(player);
                        buySell.createOrResetOwnedPlayersFile(username, clientInfo.getTeamName());
                        netWorkUtil.write("Success: Sold " + playerName + " for $" + (player.getOverall() * 10.0 * 0.8));
                        broadcastTransferList(); // Broadcast updated list
                    } else {
                        netWorkUtil.write("Error: Cannot sell " + playerName + ". Not owned.");
                    }
                } else {
                    netWorkUtil.write("Error: Invalid action " + action);
                }
            }
        } catch (IOException e) {
            System.out.println("Error sending response: " + e.getMessage());
        }
    }

    private void broadcastTransferList() throws IOException {
        synchronized (clientMap) {
            for (info client : clientMap.values()) {
                try {
                    client.getNetWorkUtil().write(new ArrayList<>(transferPlayerList));
                } catch (IOException e) {
                    System.out.println("Error broadcasting to client " + client.getUsername() + ": " + e.getMessage());
                }
            }
        }
    }
}