module com.congklakuas2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.congklakuas2 to javafx.fxml;
    exports com.congklakuas2;
}