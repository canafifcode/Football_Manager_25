package com.example.fm25;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class BuyRequestClient {
    public static boolean sendBuyRequest(String host, int port, String message) {
        try (
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(message);

            // Wait for response
            String response = in.readLine();
            System.out.println("Received response: " + response);

            return response != null && response.contains("approved");
        } catch (Exception e) {
            System.out.println("BuyRequestClient error: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        // No resources to close in this simple client
        // If you had persistent connections or resources, you would close them here
        System.out.println("BuyRequestClient closed.");
    }
}