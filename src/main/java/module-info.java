module com.example.musilmaturita {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.musilmaturita to javafx.fxml;
    exports com.example.musilmaturita;
}