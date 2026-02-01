module com.example.lab2 {
    requires transitive javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;


    opens com.example.lab2 to javafx.fxml;
    exports com.example.lab2;
}