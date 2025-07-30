package com.example.fm25.Server;

import com.example.fm25.util.NetWorkUtil; // This import might be needed if you keep the server-side reference
import java.io.Serializable;

public class info implements Serializable {
    private String username;
    private String teamName;
    // Remove the NetWorkUtil field that is sent from the client
    // private NetWorkUtil netWorkUtil;

    private transient NetWorkUtil netWorkUtil; // Keep it on the server-side, but don't serialize it.

    // Constructor for the client to use
    public info(String username, String teamName) {
        this.username = username;
        this.teamName = teamName;
    }

    // Constructor for the server to use
    public info(String username, String teamName, NetWorkUtil netWorkUtil) {
        this.username = username;
        this.teamName = teamName;
        this.netWorkUtil = netWorkUtil;
    }

    // Getters
    public String getUsername() { return username; }
    public String getTeamName() { return teamName; }
    public NetWorkUtil getNetWorkUtil() { return netWorkUtil; }
}