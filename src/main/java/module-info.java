module com.example.fm25 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.fm25 to javafx.fxml;
    exports com.example.fm25;
}