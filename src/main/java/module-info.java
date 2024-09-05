module ctgraphdep.graphdep {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires static lombok;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.slf4j;
    requires io.reactivex.rxjava3;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.zaxxer.sparsebitset;
    requires java.sql;

    opens ctgraphdep to javafx.fxml;
    exports ctgraphdep;

    opens ctgraphdep.constants to javafx.fxml;
    exports ctgraphdep.constants;

    opens ctgraphdep.controllers to javafx.fxml;
    exports ctgraphdep.controllers;

    opens ctgraphdep.models to javafx.fxml;
    exports ctgraphdep.models;

    exports ctgraphdep.services;
    opens ctgraphdep.services to javafx.fxml;

    opens ctgraphdep.utils to javafx.fxml;
    exports ctgraphdep.utils;

    opens ctgraphdep.views to javafx.fxml;
    exports ctgraphdep.views;
}