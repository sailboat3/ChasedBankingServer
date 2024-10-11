module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires org.slf4j;


    opens com.example.demo1 to javafx.fxml;
    exports com.example.demo1;
}