package com.docparser.springboot.utils;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

@Component
public class ParsingUtils {


    public static List<XWPFParagraph> getParagraphsInTheDocument(XWPFDocument document) {
        return document.getParagraphs();
    }

    public static Function<String, Boolean> checkForFontParameterChange = formatConfig -> formatConfig != null && !formatConfig.isEmpty();
    public static Function<Boolean, Boolean> checkForBooleanFontParameterChange = formatConfig -> formatConfig != null && formatConfig;


    public String getTextFromParagraph(XWPFParagraph paragraph) {
        return paragraph.getParagraphText();
    }

    public static ParagraphAlignment mapStringToAlignment(String alignmentString) {
        return switch (alignmentString.toUpperCase()) {
            case "LEFT" -> ParagraphAlignment.LEFT;
            case "CENTRE" -> ParagraphAlignment.CENTER;
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
            if (sourceParagraph.getRuns() != null && !sourceParagraph.getRuns().isEmpty() && sourceParagraph.getRuns().get(0).getCTR().getRPr() != null)
                newRun.getCTR().setRPr((sourceParagraph.getRuns().get(0).getCTR().getRPr()));
        }

        return target;
    }

    public static Integer getHeadingSize(Integer fontSize) {
        return fontSize + 2;
    }

    public static String mapStringToFontStyle(String fontStyle) {
        return switch (fontStyle) {
            case "openSans" -> "Open Sans";
            case "comicSans" -> "Comic Sans MS";
            case "dyslexie" -> "Dyslexie";
            case "openDyslexic" -> "OpenDyslexic Bold";
            case "lexend" -> "Lexend";
            case "arial" -> "Arial";
            case "helvetica" -> "Helvetica";
            default ->
                // Default to LEFT if the input string is not recognized
                    "Open Sans";
        };
    }

    public static String[] countLines(String text) {
        Pattern re = Pattern.compile("(?<=[.!?])\\s+(?=[a-zA-Z0-9])", Pattern.MULTILINE | Pattern.COMMENTS);
        return re.split(text);
    }

    public static void removeRuns(XWPFParagraph paragraph) {
        while (!paragraph.getRuns().isEmpty()) {
            paragraph.removeRun(0);
        }
    }

    public static String[] divideParagraph(String largeParagraph, int linesPerParagraph) {
        String[] lines = countLines(largeParagraph);
        int totalLines = lines.length;
        int paragraphsCount = (int) Math.ceil((double) totalLines / linesPerParagraph);
        String[] smallerParagraphs = new String[paragraphsCount];
        int start = 0;
        for (int i = 0; i < paragraphsCount; i++) {
            int end = Math.min(start + linesPerParagraph, totalLines);
            smallerParagraphs[i] = String.join(" ", Arrays.copyOfRange(lines, start, end));
            start = end;
        }
        return smallerParagraphs;
    }

    public static XWPFSettings getSettings(XWPFDocument document) throws Exception {
        java.lang.reflect.Field settings = XWPFDocument.class.getDeclaredField("settings");
        settings.setAccessible(true);
        return (XWPFSettings) settings.get(document);
    }

    public static boolean checkIfHeadingStylePresent(XWPFParagraph paragraph) {
        return paragraph.getStyleID() != null && paragraph.getStyleID().startsWith("Heading");
    }

    public static Optional<List<String>> extractHeadings(XWPFDocument document) {
        List<String> headings = new ArrayList<>();
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        paragraphs.stream().filter(ParsingUtils::checkIfHeadingStylePresent).forEach(paragraph -> headings.add(paragraph.getText()));
        return Optional.of(headings);
    }

    public static CTPPr getCTPPr(XWPFParagraph paragraph) {
        CTPPr ctpPr = paragraph.getCTP().isSetPPr() ? paragraph.getCTP().getPPr() : paragraph.getCTP().addNewPPr();
        return ctpPr;
    }

    public static void loadCustomFonts() throws IOException, FontFormatException {
        /*
        String dyslexieFontPath = "src/main/resources/fonts/OpenDyslexic-Bold.otf";
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(dyslexieFontPath)));
        Optional<Font> b = Arrays.stream(ge.getAllFonts()).filter(font -> font.getName().equals("OpenDyslexic")).findFirst();
        Font[] f = ge.getAllFonts();
         */

    }

}