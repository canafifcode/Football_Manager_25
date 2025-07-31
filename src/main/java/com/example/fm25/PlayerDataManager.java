package com.example.fm25;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDataManager {
    private final File playersFile = new File("players.txt");

    public synchronized boolean buyPlayer(String playerName, String newTeam) {
        File tempFile = new File("players_temp.txt");
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(playersFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] parts = currentLine.split(",");
                if (parts.length > 2 && parts[2].trim().equalsIgnoreCase(playerName)) {
                    // Assuming team is the second element
                    parts[1] = " " + newTeam; // Update the team
                    currentLine = String.join(",", parts);
                    updated = true;
                }
                writer.write(currentLine + System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (updated) {
            if (!playersFile.delete()) {
                System.out.println("Could not delete the original file");
                return false;
            }
            if (!tempFile.renameTo(playersFile)) {
                System.out.println("Could not rename the temp file");
                return false;
            }
        } else {
            // If no update occurred, delete the temp file
            tempFile.delete();
        }

        return updated;
    }
}