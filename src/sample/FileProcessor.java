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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileProcessor {




    //Buttonstate : green (valid) = true  and  white = false (non valid/empty)

    //   <Image url="@../sample/Assets/pdf.png" />

    private List<File> pdfFileList = new ArrayList<>();
    private List<File> xlsFileList = new ArrayList<>();
    private List<File> txtFileList = new ArrayList<>();
    private List pdfIdList = new ArrayList<>();
    private List xlsIdList = new ArrayList<>();
    private List<String> txtIdList = new ArrayList<>();
    public List<File> dragFileList = new ArrayList<>();

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

    public boolean isPdfFileListEmpty() {
        if (pdfFileList.isEmpty()) {
            return true;
        }
        else {
            return false;
        }
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
        clearDragNDropFilelist();
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

    private boolean isValidPdf(File file) {
        
            BufferedReader br;
            String temp="";
            PDFTextStripper stripper;
            
            try {
                br = new BufferedReader(new FileReader(file));
                while ((temp = br.readLine()) != null) {
                   if (temp.contains("%PDF")) {
                       PDDocument doc = PDDocument.load(file);
                       stripper = new PDFTextStripper();
                       String tempPdf = stripper.getText(doc);
                       if (tempPdf.contains("Artikel:")) {
                           doc.close();
                           
                           return true;
                       }
                       else {
                           doc.close();
                           return false;
                       }                   
                   }           
                }
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FileProcessor.class.getName())
                        .log(Level.SEVERE, null, ex);
                           return false;
            } catch (IOException ex) {
                Logger.getLogger(FileProcessor.class.getName())
                        .log(Level.SEVERE, null, ex);
                           return false;
            } 
            return false;
    }
    
    private boolean isValidXls(File file) {
        DataFormatter dataFormatter = new DataFormatter();
        FileInputStream fis; 
        FileReader freader; 
        BufferedReader br; 
        try {
            //
            fis = new FileInputStream(file);
            freader = new FileReader(file);
            br = new BufferedReader(freader);
            String tempString="";
            while ((tempString=br.readLine()) != null) {
                if(tempString.contains("[Content_Types].xml")) {
                    try {  
                        XSSFWorkbook wb = new XSSFWorkbook(fis);
                        XSSFSheet sheet = (XSSFSheet) wb.getSheetAt(0);
                        Row row = sheet.getRow(0);
                        org.apache.poi.ss.usermodel.Cell cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        String cellValue = dataFormatter.formatCellValue(cell);
                        System.out.println("cell ="+cellValue+" / "+cell.getCellType());
                            if(cell.getStringCellValue().equals("Db szám")) {
                                System.out.println("valid xls"); 
                                return true;
                            }
                
                            else {
                                System.out.println("invalid xls (else)");
                                return false;
                            }
                     
                    } 
                        catch (IOException e) {
                            System.out.println("invalid xls (ioEx)");
                            return false;
                        }     
                }
                else {
                    return false;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
            return false;
        }
    
    private boolean isValidTxt(File file) {
               
        FileInputStream fis;
        BufferedReader reader;
        FileReader fileReader;
        boolean resultBool=false;
        File resultFile = new File("x:/a.b");
        
        try {
            String temp="";           
            fis = new FileInputStream(file);
            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);
            while ((temp=reader.readLine()) != null) {
                if (temp.contains("Baustufe.:")) {
                    resultBool = true;
                    System.out.println("Valid txt");
                    break;                    
                }
                else {
                    resultBool = false;
                }
            }
        } catch (FileNotFoundException ex) {         
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return resultBool;
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
        // vvvvv

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
                        System.out.println("tempStringSubstring = "+tempStringForSubstring);
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







}
