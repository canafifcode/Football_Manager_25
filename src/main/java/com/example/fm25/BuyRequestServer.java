package com.example.fm25;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String request = in.readLine();
                System.out.println("Received buy request: " + request);

                // Always respond!
                out.println("approved"); // Or your full JSON response

                clientSocket.close();
            }
        } catch (Exception e) {
            System.out.println("BuyRequestServer error: " + e.getMessage());
        }
    }
}