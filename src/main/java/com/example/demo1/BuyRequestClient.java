package com.example.demo1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class BuyRequestClient {
    public static boolean sendBuyRequest(String host, int port, String jsonRequest) {
        try (
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(jsonRequest);

            // Wait for response
            String response = in.readLine();
            System.out.println("Received response: " + response);

            // Parse response (can use a JSON library for complex cases)
            if (response != null && response.contains("\"approved\"")) {
                return true; // Transaction approved
            }
            return false; // Transaction rejected
        } catch (Exception e) {
            System.out.println("BuyRequestClient error: " + e.getMessage());
            return false;
        }
    }
}