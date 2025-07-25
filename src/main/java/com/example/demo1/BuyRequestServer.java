package com.example.demo1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class BuyRequestServer extends Thread {
    private int port;

    public BuyRequestServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("BuyRequestServer started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String request = in.readLine();
                System.out.println("Received buy request: " + request);
                // You can add logic to notify the manager in the UI here!
                clientSocket.close();
            }
        } catch (Exception e) {
            System.out.println("BuyRequestServer error: " + e.getMessage());
        }
    }
}