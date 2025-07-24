package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class BuyPlayerController {

    @FXML
    public VBox playerList; // This should match fx:id in FXML

    @FXML
    public ScrollPane scrollPane; // In case you want to further style or access it

    public void initialize() {
        // This method is called after the FXML file has been loaded
        // You can perform any initialization here if needed
        System.out.println("BuyPlayerController initialized");
    }

}