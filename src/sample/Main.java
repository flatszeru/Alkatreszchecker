package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    Controller c = new Controller();
    Pane pane;
    Stage window;
    private double xOffset=0;
    private double yOffset=0;
    TableView<MyTableModel> t;

    ObservableList<MyTableModel> observableList = FXCollections.observableArrayList();

    @FXML
    Pane titlePane;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene scene = new Scene(root, 1100, 600);
        scene.getStylesheets().add("sample/CSS/style.css");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        c.setStage(primaryStage);
        c.setParentInController(root);
        primaryStage.show();
    }

    public ObservableList<MyTableModel> getData() {
        ObservableList<MyTableModel> p = FXCollections.observableArrayList();
        return p;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
