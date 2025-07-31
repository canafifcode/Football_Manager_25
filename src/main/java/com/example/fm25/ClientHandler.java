package com.example.fm25;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private PlayerDataManager playerDataManager;
    private PrintWriter writer;

    public ClientHandler(Socket socket, PlayerDataManager playerDataManager) {
        this.socket = socket;
        this.playerDataManager = playerDataManager;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            String clientMessage;
            do {
                clientMessage = reader.readLine();
                if (clientMessage != null) {
                    System.out.println("Received from client: " + clientMessage);
                    String[] parts = clientMessage.split(":");
                    String command = parts[0];

                    switch (command) {
                        case "BUY":
                            // Format: BUY:playerName:username:userTeam
                            boolean success = playerDataManager.buyPlayer(parts[1], parts[3]);
                            writer.println(success ? "SUCCESS" : "FAILURE");
                            if (success) {
                                MainServer.broadcast("REFRESH");
                            }
                            break;
                        // Add cases for SELL, GET_PLAYERS etc. as needed
                    }
                }
            } while (clientMessage != null && !clientMessage.equalsIgnoreCase("exit"));

            socket.close();
            MainServer.removeClient(this);
            System.out.println("Client disconnected.");
        } catch (IOException ex) {
            System.out.println("Server exception in client handler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    void sendMessage(String message) {
        if (writer != null) {
            writer.println(message);
        }
    }
}