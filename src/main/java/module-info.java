module com.heine.dennis.dailyregime {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;
    requires com.sun.jna;

    opens com.heine.dennis.dailyregime to javafx.fxml;
    exports com.heine.dennis.dailyregime;
}