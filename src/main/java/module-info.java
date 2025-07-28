module com.example.fm25 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.fm25 to javafx.fxml;
    exports com.example.fm25;
    exports com.example.fm25.controller;
    opens com.example.fm25.controller to javafx.fxml;
}