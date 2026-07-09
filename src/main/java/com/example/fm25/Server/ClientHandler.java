package com.example.fm25.Server;

import com.example.fm25.Loader.BuySell;
import com.example.fm25.Loader.PlayerLoader;
import com.example.fm25.util.NetWorkUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientHandler implements Runnable {
    private final NetWorkUtil netWorkUtil;
    private final info clientInfo;
    private final HashMap<String, info> clientMap;
    private final List<PlayerLoader> transferPlayerList;
    private final BuySell buySell; // server-side player catalog (players.txt)

    public ClientHandler(NetWorkUtil netWorkUtil, info clientInfo, HashMap<String, info> clientMap,
                         List<PlayerLoader> transferPlayerList, BuySell buySell) {
        this.netWorkUtil = netWorkUtil;
        this.clientInfo = clientInfo;
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
                    System.out.println("Client disconnected: " + clientInfo.getUsername());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("ClientHandler error (" + clientInfo.getUsername() + "): " + e.getMessage());
        } finally {
            synchronized (clientMap) {
                clientMap.remove(clientInfo.getUsername());
            }
            netWorkUtil.closeNetwork();
        }
    }

    // Protocol: "SELL|playerName|team|username" or "BUY|playerName|team|username".
    // '|' as delimiter so multi-word player names (e.g. "Lamine Yamal") survive parsing.
    private void processMessage(String message) {
        try {
            String[] parts = message.split("\\|");
            if (parts.length < 4) {
                netWorkUtil.write("Error: Invalid request format");
                return;
            }

            String action = parts[0].trim().toUpperCase();
            String playerName = parts[1].trim();
            String username = parts[3].trim();

            info sender;
            synchronized (clientMap) {
                sender = clientMap.get(username);
            }
            if (sender == null) {
                netWorkUtil.write("Error: User not registered");
                return;
            }

            synchronized (transferPlayerList) {
                if (action.equals("SELL")) {
                    handleSell(playerName, sender);
                } else if (action.equals("BUY")) {
                    handleBuy(playerName, sender);
                } else {
                    netWorkUtil.write("Error: Invalid action " + action);
                }
            }
        } catch (IOException e) {
            System.out.println("Error sending response: " + e.getMessage());
        }
    }

    private void handleSell(String playerName, info sender) throws IOException {
        PlayerLoader player = buySell.getAvailablePlayers().get(playerName);
        if (player == null) {
            netWorkUtil.write("Error: Player " + playerName + " not found");
            return;
        }
        if (!player.getTeam().equalsIgnoreCase(sender.getTeamName())) {
            netWorkUtil.write("Error: You do not own " + playerName);
            return;
        }
        if (findListedPlayer(playerName) != null) {
            netWorkUtil.write("Error: " + playerName + " is already listed for sale");
            return;
        }
        transferPlayerList.add(player);
        netWorkUtil.write("Success: Listed " + playerName + " for sale for $"
                + (buySell.calculatePrice(player.getOverall()) * 0.8));
        broadcastTransferList();
    }

    private void handleBuy(String playerName, info sender) throws IOException {
        PlayerLoader player = findListedPlayer(playerName);
        if (player == null) {
            netWorkUtil.write("Error: " + playerName + " is not for sale");
            return;
        }
        if (player.getTeam().equalsIgnoreCase(sender.getTeamName())) {
            netWorkUtil.write("Error: You cannot buy your own player " + playerName);
            return;
        }
        transferPlayerList.remove(player);
        buySell.updatePlayersFile(player, sender.getTeamName()); // persist new owner in players.txt
        player.setTeam(sender.getTeamName());
        netWorkUtil.write("Success: Bought " + playerName + " for $"
                + buySell.calculatePrice(player.getOverall()));
        broadcastTransferList();
    }

    private PlayerLoader findListedPlayer(String playerName) {
        for (PlayerLoader p : transferPlayerList) {
            if (p.getName().equals(playerName)) {
                return p;
            }
        }
        return null;
    }

    // Push the current transfer list to every connected client so their UI
    // refreshes automatically — this replaces the old shared-txt-file exchange.
    private void broadcastTransferList() {
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
