package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.sun.javafx.scene.control.skin.Utils.getResource;

public class Controller implements Initializable {

    Parent root;
    FileProcessor f = new FileProcessor();
    PdfProcessor p = new PdfProcessor();
    Delta delta = new Delta();

    private double xOffset=0;
    private double yOffset=0;

    private int pdfButtonState;
    private int xlsButtonState;
    private int txtButtonState;
    private int helpButtonState;
    // Button States:  1. fedalut 2. green 3. grey

    @FXML
    ImageView iv_pdf;

    @FXML
    ImageView iv_xls;

    @FXML
    ImageView iv_txt;

    @FXML
    ImageView iv_help;

    @FXML
    private Circle exitCircle;

    @FXML
    private AnchorPane rootView;

    @FXML
    private Pane titlePane;

    Stage stage;

    @FXML
    public void paneMouseClicked(MouseEvent event) {
        stage = (Stage) rootView.getScene().getWindow();
        delta.x = stage.getX() - event.getScreenX();
        delta.y = stage.getY() - event.getScreenY();
    }

    public void paneMouseDragged(MouseEvent event) {
        stage.setX(delta.x + event.getScreenX());
        stage.setY(delta.y + event.getScreenY());
    }

    public class Delta{
        public double x,y;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDefaultState();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setDefaultState() {
        iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/pdf.png").toExternalForm()));
        iv_pdf.setDisable(false);
        iv_xls.setImage(new Image(getClass().getResource("/sample/Assets/xls.png").toExternalForm()));
        iv_xls.setDisable(false);
        iv_txt.setImage(new Image(getClass().getResource("/sample/Assets/txt.png").toExternalForm()));
        iv_txt.setDisable(false);
        iv_help.setImage(new Image(getClass().getResource("/sample/Assets/help.png").toExternalForm()));
        iv_help.setDisable(false);
        pdfButtonState = 1;
        xlsButtonState = 1;
        txtButtonState = 1;
        helpButtonState = 1;

        // Button States:  1. fedalut 2. green 3. grey
    }

    public void setParentInController(Parent root) {
        this.root = root;
    }

    public void startExitDialog() {

    }

    public void pdfButtonClicked(MouseEvent event) {

        if (pdfButtonState!=3) {

            if(event.getButton().equals(MouseButton.PRIMARY)) {
                System.out.println("Working Directory = " + System.getProperty("user.dir"));
                File file = new File("/Assets/pdfGreen.png");
                System.out.println(file);
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("pdf files","*.pdf"));
                fc.setTitle("pdf fajlok kivalasztasa");
                List<File> list = f.updatePdfFileListsByButton(fc.showOpenMultipleDialog(stage));
                if (f.isPdfFileListEmpty()==true) {
                    iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/pdf.png").toExternalForm()));
                    turnOnXlsButton();

                }
                else if (f.isPdfFileListEmpty() != true) {
                    iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/pdfGreen.png").toExternalForm()));
                    turnOfXlsButton();
                }
                f.getPdfId();

                if (list !=null) {
                    p.setFile(list.get(0));
                    p.extractText();
                    p.setPdfName();
                }

            }

            if(event.getButton().equals(MouseButton.SECONDARY)) {
                f.clearPdfList();
                iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/pdf.png").toExternalForm()));
                turnOnXlsButton();
            }
        }
    }



    public void xlsButtonClicked(MouseEvent event) {
        if (xlsButtonState!=3) {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("xlsx files","*.xlsx"));
                fc.setTitle("xlsx fajlok kivalasztasa");
                f.updateXlsFileListsByButton(fc.showOpenMultipleDialog(stage));

                if (f.isXlsFileListEmpty()==true) {
                    System.out.println("true");
                    iv_xls.setImage(new Image(getClass().getResource("/sample/Assets/xls.png").toExternalForm()));
                    xlsButtonState=1;
                }

                else if (f.isXlsFileListEmpty() != true) {
                    System.out.println("false");
                    iv_xls.setImage(new Image(getClass().getResource("/sample/Assets/xlsGreen.png").toExternalForm()));
                    xlsButtonState=2;
                    turnOfPdfButton();
                }
            }

            if (event.getButton().equals(MouseButton.SECONDARY)) {
                f.clearXlsList();
                iv_xls.setImage(new Image(getClass().getResource("/sample/Assets/xls.png").toExternalForm()));
                xlsButtonState=1;
                turnOnPdfButton();

            }
            f.getXlsId();
        }

    }

    private void turnOfPdfButton() {
        pdfButtonState=3;
        iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/pdfGrey.png").toExternalForm()));
        iv_pdf.setDisable(true);
    }

    private void turnOnPdfButton() {
        pdfButtonState=1;
        iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/pdf.png").toExternalForm()));
        iv_pdf.setDisable(false);
    }



    private void turnOnXlsButton() {
        xlsButtonState=1;
        iv_xls.setImage(new Image(getClass().getResource("/sample/Assets/xls.png").toExternalForm()));
        iv_xls.setDisable(false);
    }

    private void turnOfXlsButton() {
        xlsButtonState=3;
        iv_xls.setImage(new Image(getClass().getResource("/sample/Assets/xlsGrey.png").toExternalForm()));
        iv_xls.setDisable(true);
    }

    public void txtButtonClicked(MouseEvent event) {

        if (event.getButton().equals(MouseButton.PRIMARY)) {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt files","*.txt"));
            fc.setTitle("txt fajlok kivalasztasa");
            f.updateTxtFileListsByButton(fc.showOpenMultipleDialog(stage));

            if (f.isTxtFileListEmpty()==true) {
                iv_txt.setImage(new Image(getClass().getResource("/sample/Assets/txt.png").toExternalForm()));
                txtButtonState=1;
            }

            else if (f.isTxtFileListEmpty() != true) {
                iv_txt.setImage(new Image(getClass().getResource("/sample/Assets/txtGreen.png").toExternalForm()));
                txtButtonState=2;
            }
        }

        if (event.getButton().equals(MouseButton.SECONDARY)) {
            f.clearXlsList();
            iv_txt.setImage(new Image(getClass().getResource("/sample/Assets/txt.png").toExternalForm()));
        }

        f.getTxtId();





    }

    public void onDragDraggedHereImage(DragEvent event) {
        f.setDragFileList(event.getDragboard().getFiles());
        f.updateFileLists();

    }

    public void onDragUpdateButtonStates(){
        if (f.isPdfFileListEmpty()==true) {
            System.out.println("true");
            iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/xls.png").toExternalForm()));
        }

        else if (f.isPdfFileListEmpty() != true) {
            System.out.println("false");
            iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/pdfGreen.png").toExternalForm()));
            iv_xls.setImage(new Image(getClass().getResource("/sample/Assets/xlsGrey.png").toExternalForm()));
            iv_xls.setDisable(true);
        }
    }

    public void onDragOverDragHereImage(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void exitCircleOnDrag() {
        exitCircle.setFill(Color.INDIANRED);
    }

    public void exitCircleOnDragExited() {
        exitCircle.setFill(Color.DODGERBLUE);
    }

    public void exitLabelOnExited() {
        exitCircle.setFill(Color.DODGERBLUE);
    }

    public void exitLabelOnMoved() {
        exitCircle.setFill(Color.INDIANRED);
    }

    public void startExitDialog(MouseEvent event) {


            //System.exit(0);

        Alert dg = new Alert(Alert.AlertType.CONFIRMATION);
        dg.setTitle("Kilépés");
        dg.setContentText("Biztosan ki akarsz lépni az alkalmazásból?");

        Optional<ButtonType> result = dg.showAndWait();

        if (result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }


}
