package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ErrorLogController implements Initializable {

    @FXML
    TextField textField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for (int i = 0; i < 150; i++) {
            textField.setText("pinaaaa");
        }
        }
    }

