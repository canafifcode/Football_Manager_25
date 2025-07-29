package com.example.fm25;

import com.example.fm25.Loader.BuySell;
import com.example.fm25.Loader.PlayerLoader;
import com.example.fm25.Server.info;
import com.example.fm25.util.NetWorkUtil;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class BuyRequestClient {
    private final NetWorkUtil netUtil;
    private final BuySell buySell;
    private final String username;
    private final String userTeam;
    private volatile boolean running = true;

    // Uses the existing Socket (do NOT create a new socket here!)
    public BuyRequestClient(Socket socket, String username, String userTeam, BuySell buySell) throws Exception {
        this.buySell = buySell;
        this.username = username;
        this.userTeam = userTeam;
        this.netUtil = new NetWorkUtil(socket); // Use the existing socket!
        info clientInfo = new info(username, userTeam, netUtil);
        System.out.println("Sending info for user: " + username);
        netUtil.write(clientInfo);
        new Thread(this::listenForUpdates).start();
    }

    public boolean sendBuyRequest(String message) {
        try {
            netUtil.write(message);
            Object response = netUtil.read();
            if (response instanceof String) {
                String responseMessage = (String) response;
                System.out.println("Server response: " + responseMessage);
                return responseMessage.startsWith("Success");
            }
            return false;
        } catch (Exception e) {
            System.out.println("BuyRequestClient error: " + e.getMessage());
            return false;
        }
    }

    private void listenForUpdates() {
        try {
            while (running) {
                Object obj = netUtil.read();
                if (obj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<PlayerLoader> updatedList = (List<PlayerLoader>) obj;
                    synchronized (buySell) {
                        buySell.getAvailablePlayers().clear();
                        for (PlayerLoader player : updatedList) {
                            buySell.addAvailablePlayer(player);
                        }
                        System.out.println("Received updated transfer list with " + updatedList.size() + " players");
                    }
                } else if (obj == null) {
                    System.out.println("Server disconnected");
                    break;
                }
            }
        } catch (Exception e) {
            if (running) {
                System.out.println("Error receiving updates: " + e.getMessage());
            }
        }
    }

    // stop the thread and close the streams
    public void close() {
        running = false;
        netUtil.closeNetwork();
    }
}