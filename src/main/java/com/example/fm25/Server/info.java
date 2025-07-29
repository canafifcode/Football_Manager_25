package com.example.fm25.Server;

import com.example.fm25.util.NetWorkUtil;
import java.io.Serializable;

public class info implements Serializable {
    private String username;
    private String teamName;
    private transient NetWorkUtil netWorkUtil; // Marked as transient to prevent serialization

    public info(String username, String teamName, NetWorkUtil netWorkUtil) {
        this.username = username;
        this.teamName = teamName;
        this.netWorkUtil = netWorkUtil;
    }

    public String getUsername() {
        return username;
    }

    public String getTeamName() {
        return teamName;
    }

    public NetWorkUtil getNetWorkUtil() {
        return netWorkUtil;
    }
}