package com.example.fm25;

public class NetworkContext {
    private static String username;
    private static String userTeam;

    public static void setSession(String user, String team) {
        username = user;
        userTeam = team;
    }

    public static String getUsername() {
        return username;
    }

    public static String getUserTeam() {
        return userTeam;
    }

    public static void closeSession() {
        ServerCommunicator.disconnect();
        username = null;
        userTeam = null;
    }
}
