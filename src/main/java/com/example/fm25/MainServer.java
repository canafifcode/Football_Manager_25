package com.example.fm25;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainServer {
    private static final int PORT = 5000;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static PlayerDataManager playerDataManager = new PlayerDataManager();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                ClientHandler clientHandler = new ClientHandler(socket, playerDataManager);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}