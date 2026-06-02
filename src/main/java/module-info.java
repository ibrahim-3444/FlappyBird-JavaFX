module com.example.demoflappy_usermng {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.demoflappy_usermng to javafx.fxml;
    exports com.example.demoflappy_usermng;
}