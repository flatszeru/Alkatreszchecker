package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    Controller c = new Controller();
    Pane pane;
    private double xOffset=0;
    private double yOffset=0;

    @FXML
    Pane titlePane;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Hello World");
        scene.getStylesheets().add("sample/CSS/style.css");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        c.setStage(primaryStage);
        c.setParentInController(root);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
