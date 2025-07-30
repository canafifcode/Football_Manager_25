package com.example.fm25;

import com.example.fm25.Loader.BuySell;
import com.example.fm25.Loader.PlayerLoader;
import com.example.fm25.Server.info;
import com.example.fm25.controller.TransferListController;
import com.example.fm25.util.NetWorkUtil;
import javafx.application.Platform;
import javafx.scene.Scene;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class BuyRequestClient {
    private NetWorkUtil netUtil;
    private final BuySell buySell;
    private final String username;
    private final String userTeam;
    private volatile boolean running = true;
    private Socket socket;
    private static final int RECONNECT_DELAY_MS = 2000; // 2-second delay between reconnect attempts

    // Inside the BuyRequestClient constructor
    public BuyRequestClient(Socket socket, String username, String userTeam, BuySell buySell) throws Exception {
        this.buySell = buySell;
        this.username = username;
        this.userTeam = userTeam;
        this.socket = socket;
        this.netUtil = new NetWorkUtil(socket);
        // Send a simple info object without the NetWorkUtil instance.
        info clientInfo = new info(username, userTeam);
        System.out.println("Sending info for user: " + username);
        netUtil.write(clientInfo);
        new Thread(this::listenForUpdates).start();
    }

    // Inside BuyRequestClient.java

    public boolean sendBuyRequest(String message) {
        try {
            if (socket.isClosed()) {
                System.err.println("Socket is closed, cannot send request.");
                // Optionally, trigger reconnect logic here.
                return false;
            }
            // This method should ONLY write the request.
            // The response will be handled by the listenForUpdates thread.
            netUtil.write(message);

            // The original implementation had a fundamental flaw here.
            // We are returning true assuming the request was sent, but the actual success
            // depends on the server's response, which is now handled asynchronously.
            // For a quick fix, we return true. For a robust solution, use CompletableFuture.
            return true;
        } catch (IOException e) {
            System.err.println("Network error in sendBuyRequest: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    private void listenForUpdates() {
        while (running) {
            try {
                Object obj = netUtil.read(); // This is the single point of reading

                // Handle broadcast updates (List of players)
                if (obj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<PlayerLoader> updatedList = (List<PlayerLoader>) obj;
                    synchronized (buySell) {
                        buySell.getAvailablePlayers().clear();
                        for (PlayerLoader player : updatedList) {
                            buySell.addAvailablePlayer(player);
                        }
                        System.out.println("Received updated transfer list with " + updatedList.size() + " players");
                        // Refresh UI logic...
                    }
                }
                // Handle specific string responses from the server
                else if (obj instanceof String) {
                    String response = (String) obj;
                    System.out.println("Server Response: " + response);
                    // Here, you can add logic to update the UI based on success/error messages.
                    // For example, re-enabling a button if a buy/sell failed.
                    if (response.startsWith("Error:")) {
                        // Find the button and re-enable it on the UI thread.
                    } else if (response.startsWith("Success:")) {
                        // Update UI to reflect success.
                    }
                }
                else if (obj == null) {
                    System.out.println("Server disconnected");
                    if (running) reconnect();
                } else {
                    System.out.println("Unexpected data received: " + obj);
                }
            } catch (IOException | ClassNotFoundException e) {
                if (running) {
                    System.err.println("Error receiving updates: " + e.getMessage());
                    reconnect();
                }
            }
        }
    }

    private void reconnect() {
        try {
            Thread.sleep(RECONNECT_DELAY_MS); // Add delay to prevent rapid reconnect attempts
            socket = new Socket("localhost", 7564);
            netUtil = new NetWorkUtil(socket);
            info clientInfo = new info(username, userTeam, netUtil);
            netUtil.write(clientInfo);
            System.out.println("Reconnected to server for user: " + username);
            new Thread(this::listenForUpdates).start(); // Restart listener thread
        } catch (IOException e) {
            System.err.println("Failed to reconnect: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Reconnect interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }



    public void close() {
        running = false;
        if (netUtil != null) netUtil.closeNetwork();
    }

    public Scene getScene() {
        return buySell.getScene();
    }
}