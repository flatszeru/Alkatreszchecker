package sample.Controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Unfinished.MyLogger;
import sample.Utils.FileProcessor;
import sample.Model.MyTableModel;
import sample.Unfinished.PdfProcessor;


import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;

public class Controller implements Initializable {

    Parent root;
    Stage stage;

    FileProcessor f = new FileProcessor();
    PdfProcessor p = new PdfProcessor();
    Delta delta = new Delta();

    MyLogger logger = new MyLogger();


    private double xOffset=0;
    private double yOffset=0;

    private String pdfButtonState;
    private String xlsButtonState;
    private String txtButtonState;
    private String helpButtonState;

    Boolean isPaneExist=false;

    ObservableList<MyTableModel> ol = FXCollections.observableArrayList();

    //<editor-fold desc="GUI FXML">
    @FXML
    AnchorPane errorLogPopup;

    @FXML
    TextField textField;

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
    private Label secretButton;

    @FXML
    TableView<MyTableModel> table;

    @FXML
    TableColumn<MyTableModel, String> firstNo;

    @FXML
    TableColumn<MyTableModel, String> secondNo;

    @FXML
    TableColumn<MyTableModel, String> firstDb;

    @FXML
    TableColumn<MyTableModel, String> firstRajzszam;

    @FXML
    TableColumn<MyTableModel, String> firstMertekegyseg;

    @FXML
    TableColumn<MyTableModel, String> secondRajzszam;

    @FXML
    TableColumn<MyTableModel, String> secondDb;

    @FXML
    TableColumn<MyTableModel, String> secondMertekegyseg;

    @FXML
    TableColumn<MyTableModel, String> secondStatus;

    @FXML
    TableColumn<MyTableModel, String> secondInfo;

    @FXML
    Pane pane;
    //</editor-fold>

    //<editor-fold desc="Move by window border">
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
    //</editor-fold>

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setDefaultState();

        firstNo.setCellValueFactory(new PropertyValueFactory<MyTableModel, String>("col_no"));
        firstRajzszam.setCellValueFactory(new PropertyValueFactory<MyTableModel, String>("col_rsz_pdf"));
        firstDb.setCellValueFactory(new PropertyValueFactory<MyTableModel, String>("col_db_pdf"));
        firstMertekegyseg.setCellValueFactory(new PropertyValueFactory<MyTableModel, String>("col_mert_pdf"));
        secondRajzszam.setCellValueFactory(new PropertyValueFactory<MyTableModel, String>("col_rsz_txt"));
        secondNo.setCellValueFactory(new PropertyValueFactory<MyTableModel, String>("col_no_txt"));
        secondDb.setCellValueFactory(new PropertyValueFactory<MyTableModel, String>("col_db_txt"));
        secondMertekegyseg.setCellValueFactory(new PropertyValueFactory<MyTableModel, String>("col_mert_txt"));
        secondStatus.setCellValueFactory(new PropertyValueFactory<MyTableModel, String>("col_stat"));
        secondInfo.setCellValueFactory(new PropertyValueFactory<MyTableModel, String>("col_info"));
        table.setItems(ol);
        isPaneExist=false;

        //d.addToDebugList("DEBUGGER STARTED - "+d.getDate());
    }

    @FXML
    public void handleMouseClickOnTable() {
        table.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!ol.isEmpty()) {
                    if(event.getButton().equals(MouseButton.PRIMARY)) {
                        Node node = ((Node) event.getTarget()).getParent();
                        TableRow row;
                        removePopupPane();
                        if (node instanceof TableRow) {
                            row = (TableRow) node;
                        } else {
                            row = (TableRow) node.getParent();
                        }
                        MyTableModel t = (MyTableModel) row.getItem();

                        popUpWindow(event, t.getCol_no(), t.getCol_no_txt(), t.getCol_rsz_pdf(),
                                t.getCol_rsz_txt(), t.getCol_db_pdf(), t.getCol_db_txt(), t.getCol_mert_pdf(),
                                t.getCol_mert_txt());
                    }
                }
            }
        });
    }

    private void removePopupPane() {
        rootView.getChildren().remove(pane);
        isPaneExist=false;
    }

    @FXML
    private void handleMouseMovedAction(MouseEvent event) {
        if(isPaneExist) {
            Point buttonClickLocation = MouseInfo.getPointerInfo().getLocation();
            rootView.setOnMouseMoved(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if ((Math.abs(buttonClickLocation.x - event.getScreenX()) > 30
                            || (Math.abs(buttonClickLocation.y - event.getScreenY()) > 30))) {
                        removePopupPane();
                    }
                }
            });
        }
    }

     public void popUpWindow(MouseEvent event, String no1, String no2, String rsz1,
                             String rsz2, String db1, String db2, String mert1, String mert2) {

         setPane(event);

         //<editor-fold desc="Label table">

         Label l1 = makeNewTableLabel("Pdf/Xlsx",   4,      37, Pos.CENTER);
         Label l2 = makeNewTableLabel("Txt",        22,     67, Pos.CENTER);
         Label l3 = makeNewTableLabel("No",         65,     7, Pos.CENTER);
         Label l4 = makeNewTableLabel("Rajzszám",   117,    7, Pos.CENTER);
         Label l5 = makeNewTableLabel("db",         210,    7, Pos.CENTER);
         Label l6 = makeNewTableLabel("Mért.",      236,    7, Pos.CENTER);
         Label l7= makeNewTableLabel(no1,               71,      37, Pos.CENTER);
         Label l8= makeNewTableLabel(no2,               71,      67, Pos.CENTER);
         Label l9= makeNewTableLabel(rsz1,              100,     37, Pos.CENTER);
         if (!rsz1.equals(rsz2)) {
             l9.setTextFill(Color.RED);
         }
         Label l9a= makeNewTableLabel(rsz2,               100,      67, Pos.CENTER);
         if (!rsz1.equals(rsz2)) {
             l9a.setTextFill(Color.RED);
         }
         Label l10= makeNewTableLabel(db1,               222,      37, Pos.CENTER);
         if (!db1.equals(db2)) {
             l10.setTextFill(Color.RED);
         }
         Label l11= makeNewTableLabel(db2,               222,      67, Pos.CENTER);
         if (!db1.equals(db2)) {
             l11.setTextFill(Color.RED);
         }
         Label l12= makeNewTableLabel(mert1,               246,      37, Pos.CENTER);
         Label l13= makeNewTableLabel(mert2,               246,      67, Pos.CENTER);

        //</editor-fold>

         pane.getChildren().addAll(l1,l2,l3,l4,l5,l6,l7,l8,l9,l9a,l10,l11,l12,l13);
         rootView.getChildren().addAll(pane);
         isPaneExist=true;
    }

    private Label makeNewTableLabel(String title, int x, int y, Pos pos) {
        Label label = new Label(title);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setAlignment(pos);
        return label;
    }

    private void setPane(MouseEvent event) {
        pane = new Pane();
        pane.setLayoutX(event.getSceneX()+20);
        pane.setLayoutY(event.getSceneY()-100);
        pane.setMinSize(285,100);
        pane.prefHeight(100);
        pane.prefWidth(285);
        pane.setStyle("-fx-background-color: white; -fx-border-color: grey;");
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
        iv_help.setDisable(true);
        iv_help.setVisible(false);
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

        //d.addToDebugList(d.getDate()+" pdfButtonClicked");

        if (pdfButtonState!="DISABLED") {

            if(event.getButton().equals(MouseButton.PRIMARY)) {
                File choosedFile = getFileWithFilechooser("pdf files","*.pdf", "pdf fajl kivalasztasa" );;
                if (choosedFile!=null) {
                    f.addPdf(choosedFile);
                    if (f.pdfFile!=null) {
                        if (f.fileType=="PDF") {
                            pdfButtonToON();
                            xlsButtonToDISABLE();
                            firstFileLabel.setText(choosedFile.getPath());
                            //d.addToDebugList(d.getDate()+" Choosed filename = "+choosedFile.getPath());
                            if(txtButtonState=="ON") {
                                helpButtonToON();
                            }
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
                        helpButtonToOFF();
                        firstFileLabel.setText("");
                        //d.addToDebugList(d.getDate()+ "pdf file chooser canceled.");
                    }
                }
            }

            if(event.getButton().equals(MouseButton.SECONDARY)) {
                f.pdfFile=null;
                pdfButtonToDEFAULT();
                xlsButtonToDEFAULT();
                helpButtonToOFF();
                ol.clear();
                firstFileLabel.setText("");
            }
        }
    }

    private File getFileWithFilechooser(String filterText1, String filtertext2, String title) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(filterText1, filtertext2));
        fc.setTitle(title);
        return fc.showOpenDialog(stage);
    }

    public void xlsButtonClicked(MouseEvent event) {

        if (xlsButtonState != "DISABLED") {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                File chosedFile = getFileWithFilechooser("xlsx files", "*.xlsx", "xlsx fajl kivalasztasa");

                if (chosedFile != null) {
                    f.addXls(chosedFile);
                    if (f.xlsFile != null) {
                        if (f.fileType == "XLSX") {
                            xlsButtonToON();
                            pdfButtonToDISABLE();
                            firstFileLabel.setText(chosedFile.getPath());
                            if(txtButtonState=="ON") {
                                helpButtonToON();
                            }
                        } else {
                            if (firstFileLabel.getText() != "") {
                                xlsButtonToON();
                            } else {
                                xlsButtonToDEFAULT();
                                pdfButtonToDEFAULT();
                                helpButtonToOFF();

                            }
                        }
                    } else {
                        xlsButtonToDEFAULT();
                        pdfButtonToDEFAULT();
                        helpButtonToOFF();
                        firstFileLabel.setText("");
                    }
                }
            }

            if (event.getButton().equals(MouseButton.SECONDARY)) {
                f.xlsFile = null;
                xlsButtonToDEFAULT();
                pdfButtonToDEFAULT();
                firstFileLabel.setText("");
                ol.clear();
            }
        }
    }

    public void txtButtonClicked(MouseEvent event) {

        if (txtButtonState!="DISABLED") {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                File chosedFile = getFileWithFilechooser("txt files", "*.txt", "txt fajl kivalasztasa" );

                if (chosedFile!=null) {
                    f.addTxt(chosedFile);
                    if (f.txtFile!=null) {
                        if (f.fileType=="TXT") {
                            txtButtonToON();
                            secondFileLabel.setText(chosedFile.getPath());
                            if(pdfButtonState=="ON" || xlsButtonState=="ON") {
                                helpButtonToON();
                            }
                        }
                        else {
                            if (secondFileLabel.getText()!="") {
                                txtButtonToON();
                            }
                            else {
                                txtButtonToDEFAULT();
                                helpButtonToOFF();
                            }
                        }
                    }
                    else  {
                        txtButtonToDEFAULT();
                        helpButtonToOFF();
                        secondFileLabel.setText("");
                        ol.clear();
                    }
                }
            }

            if(event.getButton().equals(MouseButton.SECONDARY)) {
                f.txtFile=null;
                txtButtonToDEFAULT();
                helpButtonToOFF();
                secondFileLabel.setText("");
            }
        }
    }

    public void resetButtonClicked(MouseEvent event) {
        //d.showDebugLog();
        f.xlsFile=null;
        f.pdfFile=null;
        f.txtFile=null;
        f.fileType="UNKNOWN";
        f.clearFirstSecondList();
        ol.clear();
        helpButtonToOFF();
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

    private void helpButtonToON() {
        helpButtonState="ON";
        iv_help.setImage(new Image(getClass().getResource("/sample/Assets/help.png").toExternalForm()));
        iv_help.setDisable(false);
        iv_help.setVisible(true);
    }

    private void helpButtonToOFF() {
        helpButtonState="OFF";
        iv_help.setImage(new Image(getClass().getResource("/sample/Assets/help.png").toExternalForm()));
        iv_help.setVisible(false);
        iv_help.setDisable(true);
    }

     public void onDragDraggedHereImage(DragEvent event) {
        List<File> tempFileList = event.getDragboard().getFiles();

        if (tempFileList.size()>1) {
            //e.showInformationMessage("Figyelmeztetés!","Egyszerre csak egy fájlt húzz be. Több fájl esetén csak az első kerül felhasználásra.");
            for (int i = 0; i < tempFileList.size(); i++) {
                if (i>0) {
                    tempFileList.remove(i);     //csak egy eleme legyen.
                }
            }
        }

        if (f.isItaPdfFile(tempFileList.get(0))) {
            if (f.whatsThisFile(tempFileList.get(0))=="PDF" && pdfButtonState!="DISABLED") {
                logger.addToLoggerList("Controller/onDragDraggedHereImage - pdf és nem disabled");
                File draggedFile=null;
                draggedFile = tempFileList.get(0);
                if (draggedFile!=null && firstFileLabel.getText()=="") {
                    logger.addToLoggerList("Controller/onDragDraggedHereImage - draggedfile nem null és a firstlabel == NoN ");
                    f.addPdf(draggedFile);
                    pdfButtonToON();
                    xlsButtonToDISABLE();
                    firstFileLabel.setText(draggedFile.getPath());
                }
                else if (draggedFile!=null && firstFileLabel.getText()!="") {
                    logger.addToLoggerList("Controller/onDragDraggedHereImage - draggedfile nem null és a firstlabel != NoN ");
                    f.addPdf(draggedFile);
                    pdfButtonToON();
                    xlsButtonToDISABLE();
                    firstFileLabel.setText(draggedFile.getPath());
                    if(xlsButtonState=="ON") {
                        helpButtonToON();
                    }
                }
                else if(draggedFile==null || f.whatsThisFile(tempFileList.get(0))=="UNKNOWN" ) {
                    logger.addToLoggerList("Controller/onDragDraggedHereImage - draggedfile nem null és a firstlabel == UNKNOWN ");
                    if (firstFileLabel.getText()!="") {
                        logger.addToLoggerList("Controller/onDragDraggedHereImage - draggedfile nem null és a firstlabel == UNKNOWN és firstLAbel != '' ");
                        pdfButtonToON();

                    }
                    else {
                        logger.addToLoggerList("Controller/onDragDraggedHereImage - draggedfile nem null és a firstlabel == UNKNOWN és a firstLAbel = ' ' ");
                        pdfButtonToDEFAULT();
                        xlsButtonToDEFAULT();
                        helpButtonToOFF();
                    }
                }
            }
        }

        else if (f.isItaXlsFile(tempFileList.get(0))) {

            if (f.whatsThisFile(tempFileList.get(0))=="XLSX" && xlsButtonState!="DISABLED") {
                logger.addToLoggerList("Controller/onDragDraggedHereImage - XLS és a nem DISABLED ");
                File draggedFile=null;
                draggedFile = tempFileList.get(0);

                if (draggedFile!=null && firstFileLabel.getText()=="") {
                    logger.addToLoggerList("Controller/onDragDraggedHereImage - draggedFile != null és firstLabel == ' ' ");
                    f.addXls(draggedFile);
                    xlsButtonToON();
                    pdfButtonToDISABLE();
                    firstFileLabel.setText(draggedFile.getPath());

                    if(pdfButtonState=="ON") {
                        helpButtonToON();
                    }
                }

                else if (draggedFile!=null && firstFileLabel.getText()!="") {
                    logger.addToLoggerList("Controller/onDragDraggedHereImage - draggedFile != null és firstLabel != ' ' ");
                    f.addXls(draggedFile);
                    xlsButtonToON();
                    pdfButtonToDISABLE();
                    firstFileLabel.setText(draggedFile.getPath());
                }

                else if(draggedFile==null || f.whatsThisFile(tempFileList.get(0))=="UNKNOWN" ) {
                    logger.addToLoggerList("Controller/onDragDraggedHereImage - draggedFile = null vagy fileTpye = UNKNOWN ");

                    if (firstFileLabel.getText()!="") {
                        xlsButtonToON();
                        logger.addToLoggerList("Controller/onDragDraggedHereImage - firstLAbel != ' ' ");

                    }

                    else {
                        xlsButtonToDEFAULT();
                        pdfButtonToDEFAULT();
                        helpButtonToOFF();
                        logger.addToLoggerList("Controller/onDragDraggedHereImage - firstLAbel = ' ' ");
                    }
                }
            }
        }

        else if(f.isItaTxtFile(tempFileList.get(0))) {

            if (f.whatsThisFile(tempFileList.get(0))=="TXT" ) {

                File draggedFile=null;
                draggedFile = tempFileList.get(0);

                if (draggedFile!=null && secondFileLabel.getText()=="") {

                    f.addTxt(draggedFile);
                    txtButtonToON();
                    secondFileLabel.setText(draggedFile.getPath());

                    if(xlsButtonState=="ON" || pdfButtonState=="ON") {
                        helpButtonToON();
                    }
                }

                else if (draggedFile!=null && secondFileLabel.getText()!="") {
                    f.addTxt(draggedFile);
                    txtButtonToON();
                    secondFileLabel.setText(draggedFile.getPath());
                }

                else if(draggedFile==null || f.whatsThisFile(tempFileList.get(0))=="UNKNOWN" ) {
                    if (secondFileLabel.getText()!="") {
                        txtButtonToON();
                    }

                    else {
                        txtButtonToDEFAULT();
                    }
                }
            }
        }

        else if(f.whatsThisFile(tempFileList.get(0))=="UNKNOWN" ) {
            logger.showInformationMessage("Figyelmeztetés!","Ismeretlen fájl formátum");
        }

    }

    public void onDragUpdateButtonStates(){
        if (f.isPdfFileListEmpty()==true) {
            iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/xls.png").toExternalForm()));
        }

        else if (f.isPdfFileListEmpty() != true) {
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

    public void helpButtonClicked(MouseEvent event) {

            if(event.getButton().equals(MouseButton.PRIMARY)) {

                if(!firstFileLabel.getText().equals("")) {

                    f.fillTableList(new File(firstFileLabel.getText()), f.whatsThisFile(new File(firstFileLabel.getText())));
                }

                if(!secondFileLabel.getText().equals("")) {

                    f.fillTableList(new File(secondFileLabel.getText()), f.whatsThisFile(new File(secondFileLabel.getText())));
                }

                f.findErrorsInColumns();
                f.fillStatusList();

                ol.clear();

                f.fillEmptyHolesInLists(f.findLargestList());

                for (int i = 0; i < f.findLargestList(); i++) {
                    ol.add(new MyTableModel(f.getFirstNo().get(i),
                            f.getFirstRajzszam().get(i),
                            f.getFirstDb().get(i),
                            f.getFirstMertekegyseg().get(i),
                            f.getSecondRajzszam().get(i),
                            f.getSecondNo().get(i),
                            f.getSecondDb().get(i),
                            f.getSecondMertekegyseg().get(i),
                            f.getStatus().get(i),
                            f.getFirstInfo().get(i)
                            ));
                }
                logger.showDebugLog();
            }
    }
}
