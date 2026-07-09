package com.example.fm25;

import com.example.fm25.Loader.BuySell;
import com.example.fm25.Loader.PlayerLoader;
import com.example.fm25.Server.info;
import com.example.fm25.util.NetWorkUtil;
import javafx.application.Platform;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BuyRequestClient {
    private NetWorkUtil netUtil;
    private final BuySell buySell;
    private final String username;
    private final String userTeam;
    private volatile boolean running = true;
    private Socket socket;
    private static final int RECONNECT_DELAY_MS = 2000; // 2-second delay between reconnect attempts
    private static final int MAX_RECONNECT_ATTEMPTS = 5;

    // Latest transfer list pushed by the server; the UI reads this instead of txt files.
    private final List<PlayerLoader> transferList = new ArrayList<>();
    private volatile Consumer<List<PlayerLoader>> transferListListener;
    private volatile Consumer<String> responseListener;

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

    /** Snapshot of the server's current transfer list. */
    public List<PlayerLoader> getTransferList() {
        synchronized (transferList) {
            return new ArrayList<>(transferList);
        }
    }

    /** Called on the JavaFX thread whenever the server broadcasts a new transfer list. */
    public void setTransferListListener(Consumer<List<PlayerLoader>> listener) {
        this.transferListListener = listener;
    }

    /** Called on the JavaFX thread with every "Success:"/"Error:" response from the server. */
    public void setResponseListener(Consumer<String> listener) {
        this.responseListener = listener;
    }

    public boolean sendBuyRequest(String message) {
        try {
            if (socket.isClosed()) {
                System.err.println("Socket is closed, cannot send request.");
                return false;
            }
            // Only writes the request; the response arrives on the listener thread.
            netUtil.write(message);
            return true;
        } catch (IOException e) {
            System.err.println("Network error in sendBuyRequest: " + e.getMessage());
            return false;
        }
    }

    private void listenForUpdates() {
        while (running) {
            try {
                Object obj = netUtil.read();

                // Broadcast update: the server's current transfer list
                if (obj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<PlayerLoader> updatedList = (List<PlayerLoader>) obj;
                    synchronized (transferList) {
                        transferList.clear();
                        transferList.addAll(updatedList);
                    }
                    System.out.println("Received updated transfer list with " + updatedList.size() + " players");
                    notifyTransferListListener();
                }
                // Direct response to one of our requests
                else if (obj instanceof String) {
                    String response = (String) obj;
                    System.out.println("Server Response: " + response);
                    if (response.startsWith("Success: Bought")) {
                        // We now own a new player; refresh the local squad from players.txt.
                        BuySell.createOrResetOwnedPlayersFile(username, userTeam);
                        BuySell.loadOwnedPlayers(username, userTeam);
                    }
                    Consumer<String> listener = responseListener;
                    if (listener != null) {
                        Platform.runLater(() -> listener.accept(response));
                    }
                }
                else if (obj == null) {
                    System.out.println("Server disconnected");
                    if (running && !reconnect()) {
                        break;
                    }
                } else {
                    System.out.println("Unexpected data received: " + obj);
                }
            } catch (IOException | ClassNotFoundException e) {
                if (running) {
                    System.err.println("Error receiving updates: " + e.getMessage());
                    if (!reconnect()) {
                        break;
                    }
                }
            }
        }
    }

    private void notifyTransferListListener() {
        Consumer<List<PlayerLoader>> listener = transferListListener;
        if (listener != null) {
            List<PlayerLoader> snapshot = getTransferList();
            Platform.runLater(() -> listener.accept(snapshot));
        }
    }

    // Re-establish the connection and keep using the SAME listener loop
    // (spawning a new thread here would leave two threads reading one stream).
    private boolean reconnect() {
        for (int attempt = 1; attempt <= MAX_RECONNECT_ATTEMPTS && running; attempt++) {
            try {
                Thread.sleep(RECONNECT_DELAY_MS);
                socket = new Socket("localhost", 7564);
                netUtil = new NetWorkUtil(socket);
                netUtil.write(new info(username, userTeam));
                System.out.println("Reconnected to server for user: " + username);
                return true;
            } catch (IOException e) {
                System.err.println("Reconnect attempt " + attempt + " failed: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        System.err.println("Giving up reconnecting after " + MAX_RECONNECT_ATTEMPTS + " attempts");
        return false;
    }

    public void close() {
        running = false;
        if (netUtil != null) netUtil.closeNetwork();
    }

    public Scene getScene() {
        return buySell.getScene();
    }
}
