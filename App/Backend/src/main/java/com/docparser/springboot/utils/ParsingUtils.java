package com.docparser.springboot.utils;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
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


}
