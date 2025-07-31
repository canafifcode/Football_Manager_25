package com.example.fm25;

import java.io.BufferedReader;
import java.io.IOException;

// Note: No 'javax' imports are needed here.

public class ServerListener implements Runnable {

    // This now correctly refers to com.example.fm25.Refreshable
    private static Refreshable currentRefreshable;

    public static void setCurrentRefreshable(Refreshable refreshable) {
        currentRefreshable = refreshable;
    }

    @Override
    public void run() {
        BufferedReader reader = ServerCommunicator.getReader();
        if (reader == null) {
            System.out.println("ServerListener: Cannot run, reader is not initialized.");
            return;
        }

        String serverMessage;
        try {
            while ((serverMessage = reader.readLine()) != null) {
                if ("REFRESH".equals(serverMessage)) {
                    System.out.println("REFRESH signal received from server.");
                    if (currentRefreshable != null) {
                        // Our custom refresh() method does not throw exceptions
                        currentRefreshable.refresh();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server listener: " + e.getMessage());
        }
        // The catch block for RefreshFailedException is removed as it's not needed.
    }
}