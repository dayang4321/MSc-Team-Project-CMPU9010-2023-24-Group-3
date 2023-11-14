package com.docparser.springboot.utils;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPrBase;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Component
public class ParsingUtils {


    public static List<XWPFParagraph> getParagraphsInTheDocument(XWPFDocument document) {
        return document.getParagraphs();
    }

    public String getTextFromParagraph(XWPFParagraph paragraph) {
        return paragraph.getParagraphText();
    }

    public static ParagraphAlignment mapStringToAlignment(String alignmentString) {
        return switch (alignmentString.toUpperCase()) {
            case "LEFT" -> ParagraphAlignment.LEFT;
            case "CENTER" -> ParagraphAlignment.CENTER;
            case "RIGHT" -> ParagraphAlignment.RIGHT;
            case "JUSTIFY" -> ParagraphAlignment.BOTH;
            case "DISTRIBUTE" -> ParagraphAlignment.DISTRIBUTE;
            default ->
                // Default to LEFT if the input string is not recognized
                    ParagraphAlignment.LEFT;
        };
    }

    public static BigInteger mapStringToLineSpacingValueInBigInt(String lineSpacing) {
        return switch (lineSpacing.toUpperCase()) {
            case "1" -> BigInteger.valueOf(240);
            case "1.5" -> BigInteger.valueOf(360);
            case "2" -> BigInteger.valueOf(480);
            default ->
                // Default to LEFT if the input string is not recognized
                    BigInteger.valueOf(360);
        };
    }

    public static BigInteger mapStringToCharacterSpacingValueInBigInt(String lineSpacing) {
        return switch (lineSpacing.toUpperCase()) {
            case "1" -> BigInteger.valueOf(20);
            case "1.5" -> BigInteger.valueOf(30);
            case "2" -> BigInteger.valueOf(40);
            case "2.5" -> BigInteger.valueOf(50);
            default ->
                // Default to LEFT if the input string is not recognized
                    BigInteger.valueOf(20);
        };
    }

    public static XWPFDocument createNewDocument() {
        return new XWPFDocument();
    }

    public static XWPFParagraph createNewParagraph(XWPFDocument document) {
        return document.createParagraph();
    }

    public static XWPFRun createNewRun(XWPFParagraph paragraph) {
        return paragraph.createRun();
    }

    public static XWPFDocument copyStylesAndContent(XWPFDocument source, XWPFDocument target) {
        for (XWPFParagraph sourceParagraph : source.getParagraphs()) {
            XWPFParagraph newParagraph = target.createParagraph();

            // Copy paragraph style
            if (sourceParagraph.getCTP().getPPr() != null)
                newParagraph.getCTP().setPPr((sourceParagraph.getCTP().getPPr()));

            // Concatenate text from all runs in the source paragraph
            StringBuilder paragraphText = new StringBuilder();
            for (XWPFRun sourceRun : sourceParagraph.getRuns()) {
                paragraphText.append(sourceRun.getText(0));
            }

            // Create a new run in the new paragraph and set its text
            XWPFRun newRun = newParagraph.createRun();
            newRun.setText(paragraphText.toString());

            // Copy run style
            if (sourceParagraph.getRuns()!= null&& !sourceParagraph.getRuns().isEmpty() &&sourceParagraph.getRuns().get(0).getCTR().getRPr() != null)
                newRun.getCTR().setRPr((sourceParagraph.getRuns().get(0).getCTR().getRPr()));
        }

        return target;
    }

    public  static Integer getHeadingSize(Integer fontSize){
        return fontSize+2;
    }

}