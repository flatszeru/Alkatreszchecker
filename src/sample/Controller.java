package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
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


import javax.swing.text.TableView;
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

    private String pdfButtonState;
    private String xlsButtonState;
    private String txtButtonState;
    private String helpButtonState;

    @FXML
    ImageView iv_reset;

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

    @FXML
    private Label firstFileLabel;

    @FXML
    private Label secondFileLabel;

    @FXML
    private TableView table1;





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
        iv_reset.setImage(new Image(getClass().getResource("/sample/Assets/reset.png").toExternalForm()));
        iv_reset.setDisable(false);
        iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/pdf.png").toExternalForm()));
        iv_pdf.setDisable(false);
        iv_xls.setImage(new Image(getClass().getResource("/sample/Assets/xls.png").toExternalForm()));
        iv_xls.setDisable(false);
        iv_txt.setImage(new Image(getClass().getResource("/sample/Assets/txt.png").toExternalForm()));
        iv_txt.setDisable(false);
        iv_help.setImage(new Image(getClass().getResource("/sample/Assets/help.png").toExternalForm()));
        iv_help.setDisable(false);
        xlsButtonState = "DEFAULT";
        txtButtonState = "DEFAULT";
        helpButtonState = "DEFAULT";
        pdfButtonState = "DEFAULT";

        // Button States:  DEFAULT,ON,DISABLED
    }

    public void setParentInController(Parent root) {
        this.root = root;
    }

    public void startExitDialog() {

    }

    public void pdfButtonClicked(MouseEvent event) {
        if (pdfButtonState!="DISABLED") {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                File chosedFile=null;
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("pdf files","*.pdf"));
                fc.setTitle("pdf fajl kivalasztasa");
                chosedFile = fc.showOpenDialog(stage);

                if (chosedFile!=null) {
                    f.addPdf(chosedFile);
                    if (f.pdfFile!=null) {
                        if (f.fileType=="PDF") {
                            pdfButtonToON();
                            xlsButtonToDISABLE();
                            firstFileLabel.setText(chosedFile.getPath());
                        }
                        else {
                            if (firstFileLabel.getText()!="") {
                                pdfButtonToON();
                            }
                            else {
                                pdfButtonToDEFAULT();
                                xlsButtonToDEFAULT();
                            }
                        }
                    }
                    else  {
                        pdfButtonToDEFAULT();
                        xlsButtonToDEFAULT();
                        firstFileLabel.setText("");
                    }
                }
            }

            if(event.getButton().equals(MouseButton.SECONDARY)) {
                f.pdfFile=null;
                pdfButtonToDEFAULT();
                xlsButtonToDEFAULT();
                firstFileLabel.setText("");
            }
        }
    }

    public void xlsButtonClicked(MouseEvent event) {
        if (xlsButtonState!="DISABLED") {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                File chosedFile=null;
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("xlsx files","*.xlsx"));
                fc.setTitle("xlsx fajl kivalasztasa");
                chosedFile = fc.showOpenDialog(stage);

                if (chosedFile!=null) {
                    f.addXls(chosedFile);
                    if (f.xlsFile!=null) {
                        if (f.fileType=="XLSX") {
                            xlsButtonToON();
                            pdfButtonToDISABLE();
                            firstFileLabel.setText(chosedFile.getPath());
                        }
                        else {
                            if (firstFileLabel.getText()!="") {
                                xlsButtonToON();
                            }
                            else {
                                xlsButtonToDEFAULT();
                                pdfButtonToDEFAULT();
                            }
                        }
                    }
                    else  {
                        xlsButtonToDEFAULT();
                        pdfButtonToDEFAULT();
                        firstFileLabel.setText("");
                    }
                }
            }

            if(event.getButton().equals(MouseButton.SECONDARY)) {
                f.xlsFile=null;
                xlsButtonToDEFAULT();
                pdfButtonToDEFAULT();
                firstFileLabel.setText("");
            }
        }
    }

    public void resetButtonClicked(MouseEvent event) {
        f.xlsFile=null;
        f.pdfFile=null;
        f.txtFile=null;
        f.fileType="UNKNOWN";
        pdfButtonToDEFAULT();
        xlsButtonToDEFAULT();
        txtButtonToDEFAULT();
        firstFileLabel.setText("");
        secondFileLabel.setText("");
    }

    private void pdfButtonToDISABLE() {
        pdfButtonState="DISABLED";
        iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/pdfGrey.png").toExternalForm()));
        iv_pdf.setDisable(true);
    }

    private void pdfButtonToDEFAULT() {
        pdfButtonState="DEFAULT";
        iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/pdf.png").toExternalForm()));
        iv_pdf.setDisable(false);
    }

    private void pdfButtonToON() {
        pdfButtonState="ON";
        iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/pdfGreen.png").toExternalForm()));
        iv_pdf.setDisable(false);
    }

    private void xlsButtonToDISABLE() {
        xlsButtonState="DISABLED";
        iv_xls.setImage(new Image(getClass().getResource("/sample/Assets/xlsGrey.png").toExternalForm()));
        iv_xls.setDisable(true);
    }

    private void xlsButtonToDEFAULT() {
        xlsButtonState="DEFAULT";
        iv_xls.setImage(new Image(getClass().getResource("/sample/Assets/xls.png").toExternalForm()));
        iv_xls.setDisable(false);
    }

    private void xlsButtonToON() {
        xlsButtonState="ON";
        iv_xls.setImage(new Image(getClass().getResource("/sample/Assets/xlsGreen.png").toExternalForm()));
        iv_xls.setDisable(false);
    }

    private void txtButtonToDISABLE() {
        txtButtonState="DISABLED";
        iv_txt.setImage(new Image(getClass().getResource("/sample/Assets/txtGrey.png").toExternalForm()));
        iv_txt.setDisable(true);
    }

    private void txtButtonToDEFAULT() {
        txtButtonState="DEFAULT";
        iv_txt.setImage(new Image(getClass().getResource("/sample/Assets/txt.png").toExternalForm()));
        iv_txt.setDisable(false);
    }

    private void txtButtonToON() {
        txtButtonState="ON";
        iv_txt.setImage(new Image(getClass().getResource("/sample/Assets/txtGreen.png").toExternalForm()));
        iv_txt.setDisable(false);
    }

    public void txtButtonClicked(MouseEvent event) {
        if (txtButtonState!="DISABLED") {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                File chosedFile=null;
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt files","*.txt"));
                fc.setTitle("txt fajl kivalasztasa");
                chosedFile = fc.showOpenDialog(stage);

                if (chosedFile!=null) {
                    f.addTxt(chosedFile);
                    if (f.txtFile!=null) {
                        if (f.fileType=="TXT") {
                            txtButtonToON();
                            secondFileLabel.setText(chosedFile.getPath());
                        }
                        else {
                            if (secondFileLabel.getText()!="") {
                                txtButtonToON();
                            }
                            else {
                                txtButtonToDEFAULT();
                            }
                        }
                    }
                    else  {
                        txtButtonToDEFAULT();
                        secondFileLabel.setText("");
                    }
                }
            }

            if(event.getButton().equals(MouseButton.SECONDARY)) {
                f.txtFile=null;
                txtButtonToDEFAULT();
                secondFileLabel.setText("");
            }
        }






    }

    public static void showInformationMessage(String title, String message) {

        ButtonType ok = new ButtonType("OK!", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.INFORMATION,"", ok );
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public void onDragDraggedHereImage(DragEvent event) {
        List<File> tempFileList = event.getDragboard().getFiles();
        if (tempFileList.size()>1) {
            showInformationMessage("Figyelmeztetés!","Egyszerre csak egy fájlt húzz be. Több fájl esetén csak az elsőt fogom használni.");
        }

        if (pdfButtonState!="DISABLED") {
            File draggedFile=null;
            draggedFile = tempFileList.get(0);
                if (draggedFile!=null) {
                    f.addPdf(draggedFile);
                    if (f.pdfFile!=null) {
                        if (f.fileType=="PDF") {
                            pdfButtonToON();
                            xlsButtonToDISABLE();
                            firstFileLabel.setText(draggedFile.getPath());
                        }
                        else {
                            if (firstFileLabel.getText()!="") {
                                pdfButtonToON();
                            }
                            else {
                                pdfButtonToDEFAULT();
                                xlsButtonToDEFAULT();
                                showInformationMessage("Figyelmeztetés!","Nem kompatibilis pdf fájl.");
                            }
                        }
                    }
                    else  {
                        pdfButtonToDEFAULT();
                        xlsButtonToDEFAULT();
                        firstFileLabel.setText("");
                    }
                }
            }
        else if(xlsButtonState!="DISABLED") {

        }
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
