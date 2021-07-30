package sample;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PdfProcessor {
    File file;
    String nameOfThePdfFile="";
    List pdfNoList = new ArrayList();
    List pdfIdList = new ArrayList();
    List pdfPcsList = new ArrayList();
    List pdfInfoList = new ArrayList();
    List<String> extractedPdf = new ArrayList<>();

    public void setFile(File file) {
        this.file = file;
    }

    public void extractText() {
        BufferedReader br;
        PDDocument doc=null;
        String temp = "";
        PDFTextStripper stripper;
        String tempPdf="";

        try {
            br = new BufferedReader(new FileReader(file));
            doc = PDDocument.load(file);
            stripper = new PDFTextStripper();
            tempPdf = stripper.getText(doc);
            String lines[] = tempPdf.split("\\r?\\n");
            extractedPdf = Arrays.asList(lines);
            doc.close();

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void setPdfName() {
        for (int i = 0; i < extractedPdf.size() ; i++) {
            if (extractedPdf.get(i).contains("Baukastenstückliste")) {
                nameOfThePdfFile = extractedPdf.get(i).replaceAll("Baukastenstückliste ","");
            }
        }
    }

    public List getPdfNoList() {


        return pdfNoList;
    }

    public List getPdfIdList() {

        return pdfIdList;
    }

    public List getPdfPcsList() {

        return pdfPcsList;
    }

    public List getPdfInfoList() {

        return pdfInfoList;
    }

}
