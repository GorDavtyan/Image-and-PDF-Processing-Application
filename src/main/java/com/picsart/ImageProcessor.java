package com.picsart;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class ImageProcessor {

    /**
     * Loads a PDF file from a specified path and returns a PDDocument object.
     *
     * @return The loaded PDDocument object.
     * @throws IOException If an IO error occurs during PDF loading.
     */
    public PDDocument createPDFFile() throws IOException {
        String path = "/home/gor/Desktop/javaProgramming/image-pdf-processor/src/main/resources/filePdf/c4611_sample_explain.pdf";
        return Loader.loadPDF(new File(path));
    }


    /**
     * Creates a PDImageXObject from a stamped image file based on user input.
     *
     * @param scanner   Scanner object for user input.
     * @param document  PDDocument object representing the PDF document.
     * @return          The created PDImageXObject.
     * @throws IOException If an IO error occurs during image loading.
     */
    public PDImageXObject createImage(Scanner scanner, PDDocument document) throws IOException {
        StringBuilder path = new StringBuilder("/home/gor/Desktop/javaProgramming/image-pdf-processor/src/main/resources/stamps/");
        Map<String, String> map = Map.of("1", "stamp1.png", "2", "stamp2.png");
        String str;
        do {
            System.out.println("please enter the number stamp you want");
            System.out.println("1. stamp1");
            System.out.println("2. stamp2");
            str = scanner.nextLine();
        } while (!map.containsKey(str));

        path.append(map.get(str));

        return PDImageXObject.createFromFile(path.toString(), document);
    }

    /**
     * Takes user input to create a short signature of 2-3 characters in uppercase.
     *
     * @param scanner The Scanner object for user input.
     * @return The user-input short signature.
     */
    public String inputShortSignature(Scanner scanner) {
        String inSignature;
        do {
            System.out.println("please input a short signature (2 - 3) characters");
            inSignature = scanner.nextLine().toUpperCase();
        } while (inSignature.length() < 2 || inSignature.length() > 3);

        return inSignature;
    }

    /**
     * Adds a stamped image with a short signature to each page of the provided PDF document.
     *
     * @param scanner   The Scanner object for user input.
     * @param document  The PDDocument object representing the PDF document.
     * @param image     The PDImageXObject representing the stamped image.
     * @return The modified PDDocument with stamped images and signatures.
     * @throws IOException If an IO error occurs during image processing.
     */
    public PDDocument addSnapForText(Scanner scanner, PDDocument document, PDImageXObject image) throws IOException {
        int count = 0;
        String signature = inputShortSignature(scanner);
        while (count != document.getNumberOfPages()) {
            PDPage page = document.getPage(count++);

            PDPageContentStream contentStream = new PDPageContentStream(
                    document,
                    page,
                    PDPageContentStream.AppendMode.APPEND,
                    true);

            float xPos = 10;
            float yPos = 10;

            contentStream.drawImage(image, xPos, yPos, image.getWidth(), image.getHeight());

            float textXPos = (float) image.getWidth() / 2f - 7f;
            float textYPos = (float) image.getHeight() / 2f + 5f;

            setFontAndDrawText(contentStream, signature, textXPos, textYPos);
        }
        return document;
    }

    /**
     * Sets the font and draws the provided signature text on the given PDPageContentStream.
     *
     * @param contentStream The PDPageContentStream object for drawing.
     * @param signature     The short signature text to be drawn.
     * @param textXPos      The X coordinate for drawing the text.
     * @param textYPos      The Y coordinate for drawing the text.
     * @throws IOException If an IO error occurs during text drawing.
     */
    public void setFontAndDrawText(PDPageContentStream contentStream, String signature, float textXPos, float textYPos) throws IOException {
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);

        contentStream.beginText();
        contentStream.setNonStrokingColor(new Color(227, 112, 78, 255));
        contentStream.newLineAtOffset(textXPos, textYPos);
        contentStream.showText(signature);
        contentStream.endText();

        contentStream.close();
    }

    /**
     * Saves the modified PDF document to a specified output file path.
     *
     * @param scanner  The Scanner object for user input.
     * @param document The PDDocument object representing the modified PDF document.
     * @throws IOException If an IO error occurs during PDF saving.
     */
    public void saveModifiedPDF(Scanner scanner, PDDocument document) throws IOException {
        String outputPath;

        do {
            outputPath = generateOutputPath(scanner);

            if (outputPath.equalsIgnoreCase("exit")) {
                System.out.println("PDF not saved. Exiting...");
                return;
            }

            File file = new File(outputPath);

            if (!file.exists()) {
                document.setAllSecurityToBeRemoved(true);
                document.save(outputPath);
                document.close();
                System.out.println("Signature added to PDF successfully.");
                return;
            }

            System.out.println("The given file exists. Please enter a different name or \"exit\" to exit.");
        } while (!outputPath.equalsIgnoreCase("exit"));
    }

    /**
     * Generates the output file path for saving the modified PDF.
     *
     * @param scanner The Scanner object for user input.
     * @return The generated output file path.
     */
    public String generateOutputPath(Scanner scanner) {
        StringBuilder path = new StringBuilder("/home/gor/Desktop/javaProgramming/image-pdf-processor/src/main/outputFiles/");
        String str;
        System.out.println("please enter file name for output file");
        str = scanner.nextLine();
        path.append(str).append(".pdf");
        return path.toString();
    }
}
