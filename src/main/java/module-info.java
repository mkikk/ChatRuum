module ChatRuum.main {

    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires io.netty.all;
    requires org.jetbrains.annotations;
    opens GUI to javafx.fxml;
    exports GUI;
}