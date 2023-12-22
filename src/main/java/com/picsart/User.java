package com.picsart;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.util.Scanner;

public class User {

    public static void main(String[] args) {

        ImageProcessor imageProcessor = new ImageProcessor();
        try (Scanner scanner = new Scanner(System.in);
             PDDocument document = imageProcessor.createPDFFile();
             PDDocument modifiedDocument = imageProcessor.addSnapForText(
                     scanner,
                     document,
                     imageProcessor.createImage(scanner, document))) {
            imageProcessor.saveModifiedPDF(scanner, modifiedDocument);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
