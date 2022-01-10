/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileProcessor {

    Debugger d =new Debugger();

    private List<File> pdfFileList = new ArrayList<>();
    private List<File> xlsFileList = new ArrayList<>();
    private List<File> txtFileList = new ArrayList<>();
    private List<Integer> differentLineNumbers = new ArrayList<>();
    private List<String> firstNo = new ArrayList<>();
    private List<String> firstRajzszam = new ArrayList<>();
    private List<String> firstDb = new ArrayList<>();
    private List<String> firstMertekegyseg = new ArrayList<>();
    private List<String> firstInfo = new ArrayList<>();
    private List<String> secondNo = new ArrayList<>();
    private List<String> secondRajzszam = new ArrayList<>();
    private List<String> secondDb = new ArrayList<>();
    private List<String> secondMertekegyseg = new ArrayList<>();
    private List<String> status = new ArrayList<>();
    private List<String> info = new ArrayList<>();
    public List<String> tempList = new LinkedList<>();
    private List<String> pdfIdList = new ArrayList<>();
    private List<String> xlsIdList = new ArrayList<>();
    private List<String> txtIdList = new ArrayList<>();
    public List<File> dragFileList = new ArrayList<>();
    private List startPositions = new ArrayList();
    private List endPositions = new ArrayList();

    File pdfFile;
    File xlsFile;
    File txtFile;
    File droppedFile;
    String fileType="UNKNOWN";


    //<editor-fold desc="file validator methods">
    public boolean isValidPdf(File file) {
        BufferedReader br;
        String temp="";
        PDFTextStripper stripper;
        boolean result=false;
        d.addToDebugList("start isValidPdf check.");
        if (isItaPdfFile(file)) {
            try {
                        PDDocument doc = PDDocument.load(file);
                        stripper = new PDFTextStripper();
                        String tempPdf = stripper.getText(doc);
                        if (tempPdf.contains("Artikel:")) {
                            doc.close();
                            d.addToDebugList("it is a VALID pdf file and 'Artikel:' found.");
                            result = true;

                        }
                        else {
                            doc.close();
                            d.addToDebugList("it is a NOT A VALID pdf file. 'Artikel:' not found.");
                            d.showInformationMessage("Figyelmeztetés!","Nem kompatibilis PDF fájl.");
                            result = false;
                        }


            } catch (FileNotFoundException ex) {
                Logger.getLogger(FileProcessor.class.getName())
                        .log(Level.SEVERE, null, ex);
                d.addToDebugList("FileProcessor/isItAPdfFile - file not found exception.");
                result = false;
            } catch (IOException ex) {
                Logger.getLogger(FileProcessor.class.getName())
                        .log(Level.SEVERE, null, ex);
                d.addToDebugList("FileProcessor/isItAPdfFile - Not a valid pdf.");
                result = false;
            }
        }
        return result;
    }

    public boolean isItaPdfFile(File file) {
        String temp = "";
        boolean result=false;
        BufferedReader br = null;
        PDFTextStripper stripper = null;
        try {
            br = new BufferedReader(new FileReader(file));
            while ((temp = br.readLine()) != null) {
                if (temp.contains("%PDF")) {
                    d.addToDebugList("FileProcessor/isItAPdfFile  -  %PDF not found. it's not a (compatible) pdf file.");
                    result = true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isValidXls(File file) {

        DataFormatter dataFormatter = new DataFormatter();
        FileInputStream fis;
        FileReader freader;
        BufferedReader br;
        boolean result=false;
        System.out.println(isItaXlsFile(file));

        if (isItaXlsFile(file)) {
            try {
                fis = new FileInputStream(file);
                        try {
                            //readXlsCell();
                            XSSFWorkbook wb = new XSSFWorkbook(fis);
                            XSSFSheet sheet = (XSSFSheet) wb.getSheetAt(0);
                            Row row = sheet.getRow(0);
                            org.apache.poi.ss.usermodel.Cell cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            String cellValue = dataFormatter.formatCellValue(cell);
                            System.out.println("cell ="+cellValue+" / "+cell.getCellType());
                            if(cell.getStringCellValue().equals("Db szám")) {
                                System.out.println("FileProcessor/isValidXls - valid xls.");
                                result = true;
                            }

                            else {
                                System.out.println("FileProcessor/isValidXls - A fájl excel,de nem kompatibilis.");
                                d.showInformationMessage("Figyelmeztetés!","Nem kompatibilis XLSX fájl.");
                                result = false;
                            }

                        }
                        catch (IOException e) {
                            System.out.println("FileProcessor/isValidXls - IO hiba.");
                            result = false;
                        }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return result;
    }

    public boolean isItaXlsFile(File file) {
        boolean result=false;
        String temp="";


        try {
            BufferedReader br=new BufferedReader(new FileReader(file));
            while((temp=br.readLine())!=null) {
                if(temp.contains("[Content_Types].xml")) {
                    result = true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isValidTxt(File file) {

        FileInputStream fis;
        BufferedReader reader;
        FileReader fileReader;
        boolean result = false;

        try {
            String temp="";
            fis = new FileInputStream(file);
            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);
            while ((temp=reader.readLine()) != null) {
                if (temp.contains("Baustufe.:")) {
                    result = true;
                    System.out.println("FileProcessor/isValidTxt - Valid txt."+result);
                    break;
                }
            }
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
            //result = false;
        }

        catch (IOException ex) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
            //result = false;
        }



        System.out.println("result = "+result);
        return result;
    }

    public boolean isItaTxtFile(File file) {
        boolean result = false;
        boolean tempRes=false;
        String temp = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((temp = reader.readLine()) != null) {
                if (temp.contains("Baustufe.:")) {
                    result = true;
                    break;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String whatsThisFile(File file) {

        if (isValidPdf(file)) {
            return "PDF";
        }
        else if(isValidXls(file)) {
            return "XLSX";
        }
        else if(isValidTxt(file)) {
            return "TXT";
        }
        else {
            return "UNKNOWN";
        }
    }
    //</editor-fold>

    //<editor-fold desc="add file methods">
    public void addPdf(File file) {

        fileType = whatsThisFile(file);

        if (fileType=="PDF") {
            System.out.println("FileProcessor/addPdf - filetype == pdf.");
            pdfFile = file;
        }

        else {
            System.out.println("FileProcessor/addPdf - filetype != pdf.");
        }
    }

    public void addXls(File file) {
        fileType = whatsThisFile(file);


        if (fileType=="XLSX") {
            System.out.println("FileProcessor/addPdf - filetype == xlsx.");
            xlsFile = file;

        }
        else {
            System.out.println("FileProcessor/addPdf - filetype != xlsx.");
        }
    }

    public void addTxt(File file) {
        fileType = whatsThisFile(file);
        System.out.println(fileType);

        if (fileType=="TXT") {
            System.out.println("FileProcessor/addPdf - filetype == txt.");
            txtFile = file;

        }
        else {
            System.out.println("FileProcessor/addPdf - filetype != txt.");

        }
    }
    //</editor-fold>

    //<editor-fold desc="Future version methods">
    public boolean isPdfFileListEmpty() {
        if (pdfFileList.isEmpty()) {
            return true;
        }
        else {
            return false;
        }
    }

    public void clearFileLists() {
        pdfFileList.clear();
        xlsFileList.clear();
        txtFileList.clear();
    }

    public void setDragFileList(List<File> fileList) {
        dragFileList = fileList;
    }

    public void clearDragNDropFilelist() {
        dragFileList.clear();
    }

    public void clearXlsList() {
        xlsFileList.clear();
    }

    public void clearPdfList() {
        pdfFileList.clear();
    }

    public void clearTxtList() {
        txtFileList.clear();
    }

    public void cleanTempList() {
        List<String> l = new ArrayList<>();
        findStartPositionInTableList(tempList.get(0));
        findEndPositionInTableList(tempList.get(0));

        for (int i = 0; i < startPositions.size(); i++) {
            //System.out.println("txtStartPos = "+txtStartPositions.get(i));
            for (int j = (Integer.parseInt(startPositions.get(i).toString())); j < (Integer.parseInt(endPositions.get(i).toString())); j++) {
                //System.out.println("J = "+j);
                String temp = tempList.get(j);

                System.out.println(""+j+"  "+temp);
                l.add(temp);
            }
        }
        tempList.clear();
        tempList.addAll(l);

    }



    public void resetFileLists() {
        pdfFile=null;
        xlsFile=null;
        txtFile=null;
    }

    public void resetPdfFile() {
        pdfFile=null;
    }

    public void resetXlsFile() {
        xlsFile=null;
    }

    public void resetTxtFile() {
        txtFile=null;
    }

    public void setPdfFileFromDragndrop(File file) {
        if (xlsFile!=null) {

            pdfFile = droppedFile;
        }
        System.out.println("pdf file = "+pdfFile);
    }

    public void setXlsFileFromDragndrop(File droppedFile) {
        if (pdfFile!=null) {
            xlsFile = droppedFile;
        }
        System.out.println("xls file = "+xlsFile);
    }

    public void setTxtFileFromDragndrop(File droppedFile) {
        txtFile = droppedFile;
    }

    public void setXlsFileFromDragndrop() {

    }

    public void setTxtFileFromDragndrop() {

    }

    public void addFilesToPdfList(List<File> files) {

        //add with pdf button

        pdfFileList.clear();
        pdfFileList.addAll(files);
    }

    public void addFilesToXlsList(List<File> files) {

        //add with xls button

        xlsFileList.clear();
        xlsFileList.addAll(files);
    }

    public void addFilesToTxtList(List<File> files) {

        //add with txt button

        txtFileList.clear();
        txtFileList.addAll(files);
    }

    public void checkTempList() {
        System.out.println("temlist length = "+tempList.size());
        for (int i = 0; i < tempList.size(); i++) {
            System.out.println(tempList.get(i));
        }
    }

    public void testTempList() {
        for (int i = 0; i < startPositions.size(); i++) {
            //System.out.println("txtStartPos = "+txtStartPositions.get(i));
            for (int j = (Integer.parseInt(startPositions.get(i).toString())); j < (Integer.parseInt(endPositions.get(i).toString())); j++) {
                System.out.println("No."+j+"  "+tempList.get(j));
            }
        }
    }

    public List<File> updatePdfFileListsByButton(List<File> fileList) {
        clearPdfList();
        if (fileList != null) {
            for (int i = 0; i < fileList.size(); i++) {
                if (isValidPdf(fileList.get(i)) == true) {
                    pdfFileList.add(fileList.get(i));
                }
                /* else if (isValidXls(fileList.get(i)) == true) {
                    xlsFileList.add(fileList.get(i));
                } else if (isValidTxt(fileList.get(i)) == true) {
                    txtFileList.add(fileList.get(i));
                }  */
            }
        }
        System.out.println("pdflist = "+pdfFileList);
        System.out.println("xlslist = "+xlsFileList);
        System.out.println("txtlist = "+txtFileList);
        return fileList;
    }

    public void updateXlsFileListsByButton(List<File> fileList) {
        clearXlsList();
        if (fileList != null) {
            for (int i = 0; i < fileList.size(); i++) {
                /*if (isValidPdf(fileList.get(i)) == true) {
                    pdfFileList.add(fileList.get(i));
                } */
                if (isValidXls(fileList.get(i)) == true) {
                    xlsFileList.add(fileList.get(i));
                }
                  /*
                  else if (isValidTxt(fileList.get(i)) == true) {
                    txtFileList.add(fileList.get(i));
                }  */
            }
        }
        System.out.println("pdflist (updateXlsLsit) = "+pdfFileList);
        System.out.println("xlslist (updateXlsLsit) = "+xlsFileList);
        System.out.println("txtlist (updateXlsLsit) = "+txtFileList);
    }

    public void updateTxtFileListsByButton(List<File> fileList) {
        clearTxtList();
        if (fileList != null) {
            for (int i = 0; i < fileList.size(); i++) {
                if (isValidTxt(fileList.get(i)) == true) {
                    txtFileList.add(fileList.get(i));
                }
                /* else if (isValidXls(fileList.get(i)) == true) {
                    xlsFileList.add(fileList.get(i));
                } else if (isValidTxt(fileList.get(i)) == true) {
                    txtFileList.add(fileList.get(i));
                }  */
            }
        }
        System.out.println("pdflist (updateTxt) = "+pdfFileList);
        System.out.println("xlslist (updateTxt) = "+xlsFileList);
        System.out.println("txtlist (updateTxt) = "+txtFileList);
    }

    public boolean isXlsFileListEmpty() {
        if (xlsFileList.isEmpty()) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isTxtFileListEmpty() {
        if (txtFileList.isEmpty()) {
            return true;
        }
        else {
            return false;
        }
    }

    public void fillNoColumnByData(List list) {

    }

    public void getPdfId() {
        pdfIdList.clear();
        BufferedReader br;
        String tempPdf="";
        String temp="";
        PDFTextStripper stripper;

        //  d:\abcdefgh\01.pdf

        for (int i = 0; i < pdfFileList.size(); i++) {
            try {
                br = new BufferedReader(new FileReader(pdfFileList.get(i)));
                PDDocument doc = PDDocument.load(pdfFileList.get(i));
                stripper = new PDFTextStripper();
                tempPdf = stripper.getText(doc);
                if (tempPdf.contains("Baukastenstückliste")) {
                    temp = tempPdf.substring(20, tempPdf.indexOf("\n"));
                    System.out.println("temp = "+temp);
                    pdfIdList.add(temp);

                }
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < pdfIdList.size(); i++) {
            System.out.println(pdfIdList.get(i));
        }


    }

    public void getXlsId() {
        DataFormatter dataFormatter = new DataFormatter();
        FileInputStream fis;
        FileReader freader;
        BufferedReader br;

        for (int i = 0; i < xlsFileList.size(); i++) {
            String temp = xlsFileList.get(0).getName().toString();
            System.out.println("filename = "+temp);
            temp = temp.replaceAll("\\D","");
            System.out.println("xls név substring = "+temp);
            xlsIdList.add(temp);
        }
    }

    public void getTxtId() {
        BufferedReader br;
        FileInputStream fis;
        FileReader fr;
        String temp = "";
        String subString="";
        String tempStringForSubstring="";

        List tempList = new ArrayList();
        for (int i = 0; i < txtFileList.size(); i++) {
            try {
                fr = new FileReader(txtFileList.get(i));
                br = new BufferedReader(fr);
                while ((temp = br.readLine()) != null) {
                    tempList.add(temp);
                    if (temp.contains("\f")) {
                        tempStringForSubstring = tempList.get(tempList.size()-4).toString();
                        subString = tempStringForSubstring.substring(94,113);
                        subString = subString.replaceAll("\\D","");
                        System.out.println("concrete substring = "+subString);
                        txtIdList.add(subString);
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (String s: txtIdList) {
                System.out.println(s);
            }

        }

        /*for (int i = 0; i < tempList.size(); i++) {
            System.out.println(tempList.get(i));

        }*/
    }

    private void sortFileLists() {

    }

    public void updateFileLists() {

        //First need to upload with setDragFileList
        clearFileLists();
        if (dragFileList != null) {
            for (int i = 0; i < dragFileList.size(); i++) {
                if (isValidPdf(dragFileList.get(i)) == true) {
                    pdfFileList.add(dragFileList.get(i));
                } else if (isValidXls(dragFileList.get(i)) == true) {
                    xlsFileList.add(dragFileList.get(i));
                } else if (isValidTxt(dragFileList.get(i)) == true) {
                    txtFileList.add(dragFileList.get(i));
                }
            }
        }
        System.out.println(pdfFileList);
        System.out.println(xlsFileList);
        System.out.println(txtFileList);
        clearDragNDropFilelist();
    }

    public void createPairedFileList() {
        List tempList = new ArrayList();

    }

    public void clearPairedFileList() {

    }

    //</editor-fold>

    public void fillTableList(File file, String type) {

        if (type.equals("TXT")) {
            BufferedReader br;
            String temp = "";
            PDFTextStripper stripper;
            tempList.clear();
            tempList.add("TXT");
            try {
                br = new BufferedReader(new FileReader(file));
                while ((temp = br.readLine()) != null) {
                    tempList.add(temp);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fillTxtRajzszamList();
            fillTxtDbList();
            fillTxtNoList();
            fillTxtMertekegysegList();
        }

        else if(type.equals("XLSX")) {
            tempList.clear();
            FileInputStream fis = null;
            XSSFWorkbook wb = null;
            XSSFSheet sheet = null;
            tempList.add("XLSX");

            try {
                fis = new FileInputStream(file);
                wb = new XSSFWorkbook(fis);
                sheet = wb.getSheetAt(0);
                Row row;
                Cell cell;

                for (int i = 0; i < sheet.getLastRowNum() ; i++) {
                    System.out.println(i);
                    String appendStr="";
                    row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                        for (int j = 0; j < 4 ; j++) {

                            if(row==null) {
                                continue;
                            }
                            cell=row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            if(cell.getCellType()==CellType.BLANK) {
                                appendStr = appendStr + "÷BLANK";
                            }
                            else if(cell.getCellType()==CellType._NONE) {
                                appendStr = appendStr + "÷NONE";
                            }
                            else if(cell.getCellType()==CellType.NUMERIC) {
                                appendStr = appendStr + "÷"+NumberToTextConverter.toText(cell.getNumericCellValue());
                            }
                            else if(cell.getCellType()==CellType.STRING) {
                                appendStr = appendStr + "÷"+cell.getStringCellValue();
                            }
                        }
                        tempList.add(appendStr);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fillXlsDbList();
            fillXlsRajzszamList();
        }

        else if(type.equals("PDF")) {
            BufferedReader br;
            String temp = "";
            PDFTextStripper stripper;
            PDDocument doc = null;
            tempList.clear();
            tempList.add("PDF");
            int index = 0;
            try {
                doc = PDDocument.load(file);
                stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                stripper.setWordSeparator("÷");
                temp = stripper.getText(doc);
                tempList = new ArrayList<String>(Arrays.asList(temp.split("\\n")));
                index++;

            } catch (IOException e) {
                e.printStackTrace();
            }

            fillPdfNoList();
            fillPdfRajzszamList();
            fillPdfDbList();
            fillPdfInfoList();
            fillPdfMertList();
        }

    }


    //<editor-fold desc="pdfList feltoltese">

    private void fillPdfInfoList() {
        firstInfo.clear();
        String temp="";
        String substring="";
        List<Integer> startIndex = findStartIndexInPdf();
        List<Integer> endIndex = findEndIndexInPdf();
        if (startIndex.size() != endIndex.size()) {
            d.showInformationMessage("Figyelmeztetés!","pdf parser hiba. startIndex és endIndex eltér");
        }

        for (int i = 0; i < startIndex.size(); i++) {
            for (int j = startIndex.get(i); j < endIndex.get(i); j++) {
                temp = tempList.get(j);
                substring = StringUtils.substring(temp, temp.length()-7, temp.length());
                substring = StringUtils.deleteWhitespace(substring);
                substring = substring.replaceAll("[÷]", "");
                if (!substring.isBlank() && !substring.isEmpty()) {
                    if (substring.contains("S") || substring.contains("I") || substring.contains("V") || substring.contains("E")) {
                        firstInfo.add(substring.replaceAll("-",""));
                    }
                    else {
                        firstInfo.add("");
                    }
                }
            }
        }

        System.out.println("pdf INFO list - " +firstInfo.size()+" "+firstInfo);
    }

    private void fillPdfMertList() {
        firstMertekegyseg.clear();

        String temp="";
        String substring="";
        List<Integer> startIndex = findStartIndexInPdf();
        List<Integer> endIndex = findEndIndexInPdf();
        if (startIndex.size() != endIndex.size()) {
            d.showInformationMessage("Figyelmeztetés!","pdf parser hiba. startIndex és endIndex eltér");
        }

        for (int i = 0; i < startIndex.size(); i++) {
            for (int j = startIndex.get(i); j < endIndex.get(i); j++) {
                temp = tempList.get(j);
                String[] tempStr = temp.split("÷");
                substring = tempStr[3];

                substring = StringUtils.deleteWhitespace(substring);
                //substring = substring.replaceAll("[^0-9.]", "");
                if (!substring.isBlank() && !substring.isEmpty()) {
                    firstMertekegyseg.add(substring);
                }
            }
        }
    }

    private void fillPdfNoList() {
        firstNo.clear();
        String temp="";
        String substring="";
        List<Integer> startIndex = findStartIndexInPdf();
        List<Integer> endIndex = findEndIndexInPdf();
        if (startIndex.size() != endIndex.size()) {
            d.showInformationMessage("Figyelmeztetés!","pdf parser hiba. startIndex és endIndex eltér");
        }

        for (int i = 0; i < startIndex.size(); i++) {
            for (int j = startIndex.get(i); j < endIndex.get(i); j++) {
                temp = tempList.get(j);
                String[] tempStr = temp.split("÷");
                substring = tempStr[0];
                substring = StringUtils.deleteWhitespace(substring);
                substring = substring.replaceAll("[^0-9.]", "");
                    if (!substring.isBlank() && !substring.isEmpty()) {
                        firstNo.add(substring);
                    }
                }
            }
    }

    private void fillPdfDbList() {
        firstDb.clear();
        String temp="";
        String substring="";
        List<Integer> startIndex = findStartIndexInPdf();
        List<Integer> endIndex = findEndIndexInPdf();
        if (startIndex.size() != endIndex.size()) {
            d.showInformationMessage("Figyelmeztetés!","pdf parser hiba. startIndex és endIndex eltér");
        }

        for (int i = 0; i < startIndex.size(); i++) {
            for (int j = startIndex.get(i); j < endIndex.get(i); j++) {
                temp = tempList.get(j);
                String[] tempStr = temp.split("÷");
                substring = tempStr[2];
                substring = StringUtils.deleteWhitespace(substring);
                String subFirstHalf = substring.split(",")[0];
                String subSecondHalf = substring.split(",")[1];
                if (subSecondHalf.equals("000")) {
                    substring = subFirstHalf;
                }
                else {
                    subSecondHalf = subSecondHalf.replaceAll("0","");
                    substring = subFirstHalf+","+subSecondHalf;
                }

                if (!substring.isBlank() && !substring.isEmpty()) {
                    firstDb.add(substring);
                }
            }
        }

    }

    public List<String> getSecondRajzszam() {
        return secondRajzszam;
    }

    private void fillPdfRajzszamList() {
        firstRajzszam.clear();
        String temp="";
        String substring="";
        List<Integer> startIndex = findStartIndexInPdf();
        List<Integer> endIndex = findEndIndexInPdf();

        if (startIndex.size() != endIndex.size()) {
            d.showInformationMessage("Figyelmeztetés!","pdf parser hiba. startIndex és endIndex eltér");
        }

        for (int i = 0; i < startIndex.size(); i++) {
            for (int j = startIndex.get(i); j < endIndex.get(i); j++) {
                temp = tempList.get(j);
                List<String> tempStr = Arrays.asList(temp.split("÷"));
                substring =String.valueOf(tempStr.get(1));
                substring = StringUtils.deleteWhitespace(substring);
                    if (!substring.isBlank() && !substring.isEmpty()) {
                        firstRajzszam.add(substring);
                    }
                }
        }
    }

    private List<Integer> findStartIndexInPdf() {
        List<Integer> pdfStartIndex = new ArrayList<>();
        for (int i = 0; i < tempList.size(); i++) {
            if(tempList.get(i).contains("Pos.Artikel")) {
                pdfStartIndex.add(i+1);
            }
        }
        return pdfStartIndex;
    }

    private List<Integer> findEndIndexInPdf() {
        List<Integer> pdfEndIndex = new ArrayList<>();
        for (int i = 0; i < tempList.size(); i++) {
            if(tempList.get(i).contains("Seite") && tempList.get(i).contains("schoen + sandt machinery")) {
                pdfEndIndex.add(i);
            }
        }
        return pdfEndIndex;
    }

    public List<String> getFirstNo() {
        return firstNo;
    }

    public List<String> getFirstRajzszam() {
        return firstRajzszam;
    }

    public List<String> getFirstDb() {
        return firstDb;
    }

    public List<String> getFirstMertekegyseg() {
        return firstMertekegyseg;
    }

    //</editor-fold>

    //<editor-fold desc="txtList feltoltese">
    public void fillTxtRajzszamList() {
        secondRajzszam.clear();
        String temp="";
        String substring="";
        List<Integer> startIndex = findStartIndexInTxt();
        List<Integer> endIndex = findEndIndexInTxt();
        if (startIndex.size() != endIndex.size()) {
            d.showInformationMessage("Figyelmeztetés!","txt parser hiba. startIndex és endIndex eltér");
        }

        for (int i = 0; i < startIndex.size(); i++) {
            for (int j = startIndex.get(i); j < endIndex.get(i); j++) {
                 temp = tempList.get(j);
                 if(temp.isEmpty() || temp.contains("\f") || temp.length()<58) {
                        continue;
                 }
                 else {
                     substring = temp.substring(30,51);
                     substring = StringUtils.deleteWhitespace(substring);
                     if (!substring.isBlank() && !substring.isEmpty()) {
                         secondRajzszam.add(substring);
                     }
                 }
            }
        }

    }

    public void fillTxtDbList() {
        secondDb.clear();
        String temp="";
        String substring="";
        List<Integer> startIndex = findStartIndexInTxt();
        List<Integer> endIndex = findEndIndexInTxt();
        if (startIndex.size() != endIndex.size()) {
            d.showInformationMessage("Figyelmeztetés!","txt parser hiba. startIndex és endIndex eltér");
        }

        for (int i = 0; i < startIndex.size(); i++) {
            for (int j = startIndex.get(i); j < endIndex.get(i); j++) {
                temp = tempList.get(j);
                if(temp.isBlank() || temp.isEmpty() || temp.contains("\f") || temp.length()<59 || temp.substring(0,4).equals("    ")) {

                    continue;
                }
                else {

                    if(!temp.substring(5,15).isBlank() || !temp.substring(5,15).isEmpty()) {
                        substring = temp.substring(50,57);
                        substring = StringUtils.deleteWhitespace(substring);
                        secondDb.add(substring);
                    }
                }
            }
        }

    }

    public void fillTxtMertekegysegList() {
        secondMertekegyseg.clear();
        String temp="";
        String substring="";
        List<Integer> startIndex = findStartIndexInTxt();
        List<Integer> endIndex = findEndIndexInTxt();
        if (startIndex.size() != endIndex.size()) {
            d.showInformationMessage("Figyelmeztetés!","txt parser hiba. startIndex és endIndex eltér");
        }



        for (int i = 0; i < startIndex.size(); i++) {
            for (int j = startIndex.get(i); j < endIndex.get(i); j++) {
                temp = tempList.get(j);
                if(temp.isBlank() || temp.isEmpty() || temp.contains("\f") || temp.length()<59 || temp.substring(0,4).equals("    ")) {

                    continue;
                }
                else {

                    if(!temp.substring(5,15).isBlank() || !temp.substring(5,15).isEmpty()) {
                        substring = temp.substring(58,62);
                        substring = StringUtils.deleteWhitespace(substring);
                        secondMertekegyseg.add(substring);
                    }
                }
            }
        }

    }

    public void fillTxtNoList() {
        secondNo.clear();
        String temp="";
        String substring="";
        for (int i = 0; i < tempList.size(); i++) {
            temp = tempList.get(i);
            if(temp.isEmpty() || temp.contains("\f") || temp.length()<5) {
                continue;
            }
            else {
                substring = temp.substring(0,4);
                substring = StringUtils.deleteWhitespace(substring);
                if (StringUtils.isNumeric(substring)) {
                    secondNo.add(substring);
                }
            }
        }

    }

    private List<Integer> findStartIndexInTxt() {
        List<Integer> startIndex = new ArrayList<>();
        for (int i = 0; i < tempList.size(); i++) {
            if(tempList.get(i).contains("Jk.Rf.")) {

                startIndex.add(i+2);
            }
        }
        return startIndex;
    }

    private List<Integer> findEndIndexInTxt() {
        List<Integer> endIndex = new ArrayList<>();
        for (int i = 0; i < tempList.size(); i++) {
            if(tempList.get(i).equals("\f")) {
                endIndex.add(i-5);
            }
        }
        return endIndex;
    }

    //</editor-fold>

    //<editor-fold desc="xlsList feltoltese">

    // Ha validáltuk a fájlokat, feltöltöttük a
    // tempList -et akkor ez a metódus csoport
    // tölti fel a MyTableModel elemeihez tartozó
    // listákat (first/second listákat)

    public void fillXlsRajzszamList() {
        String substring="";
        firstRajzszam.clear();
        int keraruStartIndex = findKeraruStartIndexInXls();
        int keraruEndIndex = findKeraruEndIndexInXls();
        List<String> tempArr=new ArrayList<String>();  //sima List unmodified wrapper >> kivétel

        for (int i = 2; i < keraruStartIndex; i++) {
            // index = 2 mivel mindig "0" a formátum, "1" a header
            String temp = tempList.get(i);
            if(temp.isEmpty() || temp.contains("÷BLANK÷BLANK") || temp.length()<5) {
                continue;
            }
            else {
                String[] t = temp.split(Pattern.quote("÷"));
                tempArr = Arrays.asList(t);
                firstRajzszam.add(tempArr.get(2));
            }
        }

        for (int i = keraruStartIndex+1; i < keraruEndIndex; i++) {
            // start index = 2 mivel mindig "0" a formátum, "1" a header
            String temp = tempList.get(i);
            if(temp.isEmpty() || temp.contains("÷BLANK÷BLANK") || temp.length()<5) {
                continue;
            }
            else {
                String[] t = temp.split(Pattern.quote("÷"));
                tempArr = Arrays.asList(t);
                firstRajzszam.add(tempArr.get(4));
            }
        }

    }

    public void fillXlsDbList() {
        String substring="";
        firstDb.clear();
        int keraruStartIndex = findKeraruStartIndexInXls();
        int keraruEndIndex = findKeraruEndIndexInXls();
        List<String> tempArr=new ArrayList<String>();  //sima List unmodified wrapper >> kivétel

        for (int i = 2; i < keraruStartIndex; i++) {
            // index = 2 mivel mindig "0" a formátum, "1" a header
            String temp = tempList.get(i);
            if(temp.isEmpty() || temp.contains("÷BLANK÷BLANK") || temp.length()<5) {
                continue;
            }
            else {
                String[] t = temp.split(Pattern.quote("÷"));
                tempArr = Arrays.asList(t);
                firstDb.add(tempArr.get(1));
            }
        }

        for (int i = keraruStartIndex+1; i < keraruEndIndex; i++) {
            // start index = 2 mivel mindig "0" a formátum, "1" a header
            String temp = tempList.get(i);
            if(temp.isEmpty() || temp.contains("÷BLANK÷BLANK") || temp.length()<5) {
                continue;
            }
            else {
                String[] t = temp.split(Pattern.quote("÷"));
                tempArr = Arrays.asList(t);
                firstDb.add(tempArr.get(1));
            }
        }

    }

    private int findKeraruStartIndexInXls() {
        int temp=-1;
        for (int i = 0; i < tempList.size(); i++) {
            if(tempList.get(i).contains("Megnevezés (ker. áru)")) {
                temp = i;
                break;
            }
        }
        return temp;
    }

    private int findKeraruEndIndexInXls() {
        int temp=-1;
        for (int i = 0; i < tempList.size(); i++) {
            if(tempList.get(i).contains("ALU PROFIL")) {
                temp = i;

                break;
            }
        }
        return temp;
    }

    private void showTemplist() {
        for (int i = 0; i < tempList.size(); i++) {
            System.out.println(tempList.get(i));
        }
    }
    //</editor-fold>

    //<editor-fold desc="tableChecker">

    public void clearFirstSecondList() {
        firstNo.clear();
        firstInfo.clear();
        firstDb.clear();
        firstRajzszam.clear();
        firstMertekegyseg.clear();
        secondMertekegyseg.clear();
        secondRajzszam.clear();
        secondDb.clear();
        secondNo.clear();
    }

    public List<MyTableModel> fillMyTableModel() {
        List rows = new ArrayList();
        String[] r = new String[10];

        int largestLength = findLargestList();

        fillEmptyHolesInLists(largestLength);

        for (int i = 0; i < largestLength ; i++) {
            r[0]=firstNo.get(i);
            r[1]=firstRajzszam.get(i);
            r[2]=firstDb.get(i);
            r[3]=firstMertekegyseg.get(i);
            r[4]=secondRajzszam.get(i);
            r[5]=secondNo.get(i);
            r[6]=secondDb.get(i);
            r[7]=secondMertekegyseg.get(i);
            r[8]=firstInfo.get(i);
            r[9]=status.get(i);
            rows.add(Arrays.asList(r));
        }


        return rows;

    }

    public int findLargestList() {
        int[] findMaxArray = {
                firstDb.size(),
                firstInfo.size(),
                firstRajzszam.size(),
                firstNo.size(),
                firstMertekegyseg.size(),
                secondDb.size(),
                secondNo.size(),
                secondMertekegyseg.size(),
                secondRajzszam.size()
        };
        return Arrays.stream(findMaxArray).max().getAsInt();
    }

    public void fillEmptyHolesInLists(int largestListSize) {
        for (int i = firstNo.size(); i < largestListSize; i++) {
            firstNo.add("x");
        }

        for (int i = firstRajzszam.size(); i < largestListSize; i++) {
            firstRajzszam.add("x");
        }

        for (int i = firstDb.size(); i < largestListSize; i++) {
            firstDb.add("x");
        }

        for (int i = firstMertekegyseg.size(); i < largestListSize; i++) {
            firstMertekegyseg.add("x");
        }

        for (int i = secondRajzszam.size(); i < largestListSize; i++) {
            secondRajzszam.add("x");
        }

        for (int i = secondNo.size(); i < largestListSize; i++) {
            secondNo.add("x");
        }

        for (int i = secondDb.size(); i < largestListSize; i++) {
            secondDb.add("x");
        }

        for (int i = secondMertekegyseg.size(); i < largestListSize; i++) {
            secondMertekegyseg.add("x");
        }

        for (int i = firstInfo.size(); i < largestListSize; i++) {
            firstInfo.add("");
        }

        for (int i = status.size(); i < largestListSize; i++) {
            status.add("");
        }
    }

    public void fillStatusList() {
        status.clear();
        for (int i = 0; i < findLargestList(); i++) {
            status.add("");
        }

        for (int i = 0; i < differentLineNumbers.size(); i++) {
            status.add(differentLineNumbers.get(i), "Error/Info");
        }
    }

    public void findErrorsInColumns() {
        differentLineNumbers.clear();

        int[] findMaxArray = {
                firstDb.size(),
                firstInfo.size(),
                firstRajzszam.size(),
                firstNo.size(),
                firstMertekegyseg.size(),
                secondDb.size(),
                secondNo.size(),
                secondMertekegyseg.size(),
                secondRajzszam.size()
        };

        int largestLength = Arrays.stream(findMaxArray).max().getAsInt();
        int maxDbLength = 0;
        int maxRajzszamLength = 0;

        //<editor-fold desc="findIteratorMax">
        if (firstDb.size()>secondDb.size()) {
            maxDbLength = firstDb.size()+1;
        }
        else {
            maxDbLength = secondDb.size()+1;
        }

        if (firstRajzszam.size()>secondRajzszam.size()) {
            maxRajzszamLength = firstRajzszam.size()+1;
        }
        else {
            maxRajzszamLength = secondRajzszam.size()+1;
        }
        //</editor-fold>
        //<editor-fold desc="findDbErrors">
        for (int i = 0; i < maxDbLength; i++) {
            if (firstDb.size()>i && secondDb.size()>i) {
                if (!firstDb.get(i).equals(secondDb.get(i))) {
                    differentLineNumbers.add(i);
                }
            } else if (firstDb.size()>i) {
                differentLineNumbers.add(i);
            } else if (secondDb.size()>i) {
                differentLineNumbers.add(i);
            }
        }
        //</editor-fold>
        //<editor-fold desc="findRajzszamErrors">
        for (int i = 0; i < maxDbLength; i++) {
            if (firstRajzszam.size()>i && secondRajzszam.size()>i) {
                if (!firstRajzszam.get(i).equals(secondRajzszam.get(i))) {
                    differentLineNumbers.add(i);
                }
            } else if (firstRajzszam.size()>i) {
                differentLineNumbers.add(i);
            } else if (secondRajzszam.size()>i) {
                differentLineNumbers.add(i);
            }
        }
        //</editor-fold>

        for (int i = 0; i < firstInfo.size(); i++) {
            if(firstInfo.get(i) == "I") {
                differentLineNumbers.add(i);
            }
        }

        Collections.sort(differentLineNumbers);
        List<Integer> tempList = new ArrayList();
        tempList = differentLineNumbers;
        differentLineNumbers = tempList.stream().distinct().collect(Collectors.toList());

    }

    public List<String> getFirstInfo() {
        return firstInfo;
    }

    public List<String> getSecondNo() {
        return secondNo;
    }

    public List<String> getSecondDb() {
        return secondDb;
    }

    public List<String> getSecondMertekegyseg() {
        return secondMertekegyseg;
    }

    public List<String> getStatus() {
        return status;
    }

    //</editor-fold>



    public void findStartPositionInTableList(String type) {
        if(type.equals("TXT")) {
            startPositions.clear();
            for (int i = 4; i < tempList.size(); i++) {
                if(tempList.get(i).toString().contains("Jk.Rf.")) {
                    startPositions.add(i+2);
                }
            }
        }
        else if(type.equals("XLSX")) {
            startPositions.clear();
            for (int i = 0; i < tempList.size() ; i++) {
                if(tempList.get(i).contains("Rajzszám (gyárott)") ||
                   tempList.get(i).contains("Rajzszám (gyártott)") ||
                        tempList.get(i).contains("Megnevezés (ker. áru)")) {
                }
            }
        }
    }

    public void findEndPositionInTableList(String s) {
        endPositions.clear();
        for (int i = 6; i < tempList.size(); i++) {
            if(tempList.get(i).toString().contains("\f")) {

                endPositions.add(i-4);
            }
        }
    }


}
