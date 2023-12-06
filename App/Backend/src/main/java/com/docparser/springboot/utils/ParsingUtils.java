package com.docparser.springboot.utils;

import com.docparser.springboot.model.DocumentConfig;
import org.apache.commons.collections4.ListUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

@Component
public class ParsingUtils {


    public static List<String> getParagraphsInTheDocument(XWPFDocument document) {
        List<String> paragraphs = new ArrayList<>();
        document.getParagraphs().forEach(paragraph -> paragraphs.add(paragraph.getParagraphText()));
        return paragraphs;
    }

    public static Function<String, Boolean> checkForFontParameterChange = formatConfig -> formatConfig != null && !formatConfig.isEmpty();
    public static Function<Boolean, Boolean> checkForBooleanFontParameterChange = Objects::nonNull;


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

    public static XWPFParagraph createNewParagraph(XWPFDocument document) {
        return document.createParagraph();
    }

    public static XWPFRun createNewRun(XWPFParagraph paragraph) {
        return paragraph.createRun();
    }

    public static XWPFDocument copyStylesAndContent(XWPFDocument source, XWPFDocument target) {

// Copy paragraphs (text, formatting, styles)
        for (XWPFParagraph paragraph : source.getParagraphs()) {
            XWPFParagraph newParagraph = target.createParagraph();
            newParagraph.getCTP().set(paragraph.getCTP().copy());

            // Copy runs (text, formatting, styles)
            for (XWPFRun run : paragraph.getRuns()) {
                XWPFRun newRun = newParagraph.createRun();
                newRun.getCTR().set(run.getCTR().copy());
                // Copy other run-level properties as needed
            }

            // Copy additional paragraph-level properties if needed
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
        if (text == null) return new String[]{};
        Pattern re = Pattern.compile("(?<=[.!?])\\s+(?=[a-zA-Z0-9])", Pattern.MULTILINE | Pattern.COMMENTS);
        return re.split(text);
    }

    public static void removeRuns(XWPFParagraph paragraph) {
        for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
            XWPFRun run = paragraph.getRuns().get(i);
            if (run.getEmbeddedPictures().isEmpty()) {
                paragraph.removeRun(i);
            }
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
        if (paragraph.getStyleID() != null && paragraph.getStyleID().startsWith("Heading"))
            return true;
        if (!paragraph.getRuns().isEmpty()) {
            return paragraph.getRuns().get(0).isBold();
        }
        return false;
    }

    public static Optional<List<String>> extractHeadings(XWPFDocument document) {
        List<String> headings = new ArrayList<>();
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        paragraphs.stream().filter(ParsingUtils::checkIfHeadingStylePresent).forEach(paragraph -> headings.add(paragraph.getRuns().get(0).toString()));
        return Optional.of(headings);
    }

    public static CTPPr getCTPPr(XWPFParagraph paragraph) {
        CTPPr ctpPr = paragraph.getCTP().isSetPPr() ? paragraph.getCTP().getPPr() : paragraph.getCTP().addNewPPr();
        return ctpPr;
    }

    public static List<List<String>> partitionList(List<String> documentIds) {
        List<List<String>> subSets = ListUtils.partition(documentIds, 25);
        return subSets;
    }

    /*
        public static void loadCustomFonts()  {
            String dyslexieFontPath = "src/main/resources/fonts/OpenDyslexic-Bold.otf";
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(dyslexieFontPath)));
            Optional<Font> b = Arrays.stream(ge.getAllFonts()).filter(font -> font.getName().equals("OpenDyslexic")).findFirst();
            Font[] f = ge.getAllFonts();
        }*/
    public static void copyDocumentConfig(DocumentConfig source, DocumentConfig target) {
        target.setFontColor(checkForFontParameterChange.apply(source.getFontColor())?source.getFontColor():target.getFontColor());
        target.setFontSize(checkForFontParameterChange.apply(source.getFontSize())?source.getFontSize():target.getFontSize());
        target.setFontType(checkForFontParameterChange.apply(source.getFontType())?source.getFontType():target.getFontType());
        target.setLineSpacing(checkForFontParameterChange.apply(source.getLineSpacing())?source.getLineSpacing():target.getLineSpacing());
        target.setAlignment(checkForFontParameterChange.apply(source.getAlignment())?source.getAlignment():target.getAlignment());
        target.setBackgroundColor(checkForFontParameterChange.apply(source.getBackgroundColor())?source.getBackgroundColor():target.getBackgroundColor());
        target.setFontColor(checkForFontParameterChange.apply(source.getFontColor())?source.getFontColor():target.getFontColor());
        target.setRemoveItalics(checkForBooleanFontParameterChange.apply(source.getRemoveItalics())?source.getRemoveItalics():target.getRemoveItalics());
        target.setBorderGeneration(checkForBooleanFontParameterChange.apply(source.getBorderGeneration())?source.getBorderGeneration():target.getBorderGeneration());
        target.setParagraphSplitting(checkForBooleanFontParameterChange.apply(source.getParagraphSplitting())?source.getParagraphSplitting():target.getParagraphSplitting());
        target.setHeaderGeneration(checkForBooleanFontParameterChange.apply(source.getHeaderGeneration())?source.getHeaderGeneration():target.getHeaderGeneration());
        target.setGenerateTOC(checkForBooleanFontParameterChange.apply(source.getGenerateTOC())?source.getGenerateTOC():target.getGenerateTOC());
        target.setSyllableSplitting(checkForBooleanFontParameterChange.apply(source.getSyllableSplitting())?source.getSyllableSplitting():target.getSyllableSplitting());
        target.setCharacterSpacing(checkForFontParameterChange.apply(source.getCharacterSpacing())?source.getCharacterSpacing():target.getCharacterSpacing());
    }

    public static Set<String> stopWords() {
        Set<String> stopWords = new HashSet<>();
        stopWords.add("the");
        stopWords.add("in");
        stopWords.add("that");
        stopWords.add("is");
        stopWords.add("a");
        stopWords.add("an");
        stopWords.add("to");
        stopWords.add("and");
        stopWords.add("of");
        stopWords.add("for");
        stopWords.add("with");
        stopWords.add("as");
        stopWords.add("by");
        stopWords.add("on");
        stopWords.add("at");
        stopWords.add("this");
        stopWords.add("from");
        stopWords.add("or");
        stopWords.add("you");
        stopWords.add("your");
        stopWords.add("we");
        stopWords.add("our");
        stopWords.add("us");
        stopWords.add("i");
        stopWords.add("me");
        stopWords.add("my");
        stopWords.add("mine");
        stopWords.add("he");
        stopWords.add("she");
        stopWords.add("him");
        stopWords.add("her");
        stopWords.add("his");
        stopWords.add("hers");
        stopWords.add("they");
        stopWords.add("them");
        stopWords.add("their");
        stopWords.add("theirs");
        stopWords.add("it");
        stopWords.add("its");
        stopWords.add("be");
        stopWords.add("been");
        stopWords.add("being");
        stopWords.add("have");
        stopWords.add("has");
        stopWords.add("had");
        stopWords.add("do");
        stopWords.add("does");
        stopWords.add("did");
        stopWords.add("will");
        stopWords.add("would");
        stopWords.add("shall");
        stopWords.add("should");
        stopWords.add("can");
        stopWords.add("could");
        stopWords.add("may");
        stopWords.add("might");
        stopWords.add("must");
        stopWords.add("am");
        stopWords.add("are");
        stopWords.add("was");
        stopWords.add("were");
        stopWords.add("not");
        stopWords.add("no");
        stopWords.add("nor");
        stopWords.add("so");
        stopWords.add("if");
        stopWords.add("but");
        stopWords.add("what");
        stopWords.add("when");
        stopWords.add("where");
        stopWords.add("why");
        stopWords.add("how");
        stopWords.add("which");
        stopWords.add("who");
        stopWords.add("whom");
        stopWords.add("whose");
        stopWords.add("more");

        return stopWords;
    }
}