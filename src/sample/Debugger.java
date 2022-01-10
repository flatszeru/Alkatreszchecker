package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Debugger {

    static List<String> debugLog = new ArrayList<>();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public void addToDebugList(String row) {
        debugLog.add(row);
    }

    public String getDate() {
        LocalDateTime now = LocalDateTime.now();
        return (dtf.format(now));
    }

    public void showDebugLog() {
        for (int i = 0; i < debugLog.size(); i++) {
            System.out.println(debugLog.get(i));
        }
    }

    public void clearDebugLog() {
        debugLog.clear();
    }

    public void showInformationMessage(String title, String message) {

        ButtonType ok = new ButtonType("OK!", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.INFORMATION,"", ok );
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

}
