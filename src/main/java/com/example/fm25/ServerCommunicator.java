package com.example.fm25;

import java.io.*;
import java.net.Socket;

public class ServerCommunicator {
    private static Socket socket;
    private static PrintWriter writer;
    private static BufferedReader reader;

    public static void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static String sendMessage(String message) {
        if (writer != null && reader != null) {
            try {
                writer.println(message);
                return reader.readLine(); // Wait for a single-line response
            } catch (IOException e) {
                e.printStackTrace();
                return "ERROR";
            }
        }
        return "NOT_CONNECTED";
    }

    public static BufferedReader getReader() {
        return reader;
    }

    public static void disconnect() {
        try {
            if (writer != null) writer.println("exit");
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean sendBuyRequest(String managerHost, int managerPort, String buyMessage) {
        try (Socket managerSocket = new Socket(managerHost, managerPort);
             PrintWriter managerWriter = new PrintWriter(managerSocket.getOutputStream(), true);
             BufferedReader managerReader = new BufferedReader(new InputStreamReader(managerSocket.getInputStream()))) {

            managerWriter.println(buyMessage);
            String response = managerReader.readLine();
            return "SUCCESS".equals(response);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}