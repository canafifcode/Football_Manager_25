package com.example.fm25.Server;

import com.example.fm25.Loader.BuySell;
import com.example.fm25.Loader.PlayerLoader;
import com.example.fm25.util.NetWorkUtil;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private HashMap<String, info> clientMap;
    private List<PlayerLoader> transferPlayerList;
    private BuySell buySell;

    public Server(int port) {
        clientMap = new HashMap<>();
        transferPlayerList = new ArrayList<>();
        buySell = new BuySell();
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            serve();
        } catch (Exception e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    private void serve() {
        try {
            int count = 0;
            while (true) {
                Socket clientSocket = serverSocket.accept();
                count++;
                System.out.println("Client " + count + " connected successfully");
                NetWorkUtil netUtil = new NetWorkUtil(clientSocket);
                Object obj = netUtil.read();
                //Object obj = netUtil.read();
                if (obj instanceof info) {
                    info receivedInfo = (info) obj;
                    // Create a new info object on the server that includes the connection's NetWorkUtil
                    info clientInfoForServer = new info(receivedInfo.getUsername(), receivedInfo.getTeamName(), netUtil);
                    synchronized (clientMap) {
                        // Store the server-side info object in the map
                        clientMap.put(clientInfoForServer.getUsername(), clientInfoForServer);
                    }
                    ClientHandler clientHandler = new ClientHandler(netUtil, clientMap, transferPlayerList, buySell);
                    new Thread(clientHandler).start();
                } else {
                    System.out.println("Invalid client info received");
                    netUtil.closeNetwork();
                }
            }
        } catch (Exception e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Server(7564);
    }
}