package com.example.fm25;

import java.net.Socket;

public class NetworkContext {
    private static Socket socket;
    private static BuyRequestClient client;
    private static String username;
    private static String userTeam;

    public static void setSocket(Socket s) { socket = s; }
    public static Socket getSocket() { return socket; }

    public static void setClient(BuyRequestClient c) { client = c; }
    public static BuyRequestClient getClient() { return client; }

    public static void setSession(String user, String team) {
        username = user;
        userTeam = team;
    }
    public static String getUsername() { return username; }
    public static String getUserTeam() { return userTeam; }

    public static void closeSession() {
        try {
            if (client != null) client.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket = null;
        client = null;
        username = null;
        userTeam = null;
    }
}