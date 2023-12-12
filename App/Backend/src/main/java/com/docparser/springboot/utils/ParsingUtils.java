package com.docparser.springboot.utils;

import com.docparser.springboot.model.DocumentConfig;
import org.apache.commons.collections4.ListUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Component
public class ParsingUtils {

    // Extracts all paragraphs as a list of strings from a given XWPFDocument
    public static List<String> getParagraphsInTheDocument(XWPFDocument document) {
        List<String> paragraphs = new ArrayList<>();
        document.getParagraphs().forEach(paragraph -> paragraphs.add(paragraph.getParagraphText()));
        return paragraphs;
    }

    private ParsingUtils() {
        // Private constructor to prevent instantiation
    }
    // Functions to check if a font parameter or boolean font parameter has changed

    public static final Predicate<String> checkForFontParameterChange = formatConfig -> formatConfig != null
            && !formatConfig.isEmpty();
    public static final Predicate<Boolean> checkForBooleanFontParameterChange = Objects::nonNull;

    // Maps a string to a corresponding ParagraphAlignment enumeration
    public static ParagraphAlignment mapStringToAlignment(String alignmentString) {
        return switch (alignmentString.toUpperCase()) {
            case "LEFT" -> ParagraphAlignment.LEFT;
            case "CENTRE" -> ParagraphAlignment.CENTER;
            case "RIGHT" -> ParagraphAlignment.RIGHT;
            case "JUSTIFY" -> ParagraphAlignment.BOTH;
            case "DISTRIBUTE" -> ParagraphAlignment.DISTRIBUTE;
            default -> ParagraphAlignment.LEFT; // Defaults to LEFT if unrecognized
        };
    }

    // Maps a string representing line spacing to its corresponding BigInteger value
    public static BigInteger mapStringToLineSpacingValueInBigInt(String lineSpacing) {
        return switch (lineSpacing.toUpperCase()) {
            case "1" -> BigInteger.valueOf(240);
            case "1.25" -> BigInteger.valueOf(300);
            case "1.5" -> BigInteger.valueOf(360);
            case "1.75" -> BigInteger.valueOf(420);
            case "2" -> BigInteger.valueOf(480);
            case "2.25" -> BigInteger.valueOf(540);
            case "2.5" -> BigInteger.valueOf(600);
            case "2.75" -> BigInteger.valueOf(660);
            case "3" -> BigInteger.valueOf(720);
            default -> BigInteger.valueOf(360); // Defaults to 1.5 if unrecognized
        };
    }

    // Maps a string representing character spacing to its corresponding BigInteger
    // value
    public static BigInteger mapStringToCharacterSpacingValueInBigInt(String lineSpacing) {
        return switch (lineSpacing.toUpperCase()) {
            case "1" -> BigInteger.valueOf(20);
            case "1.5" -> BigInteger.valueOf(30);
            case "2" -> BigInteger.valueOf(40);
            case "2.5" -> BigInteger.valueOf(50);
            case "3" -> BigInteger.valueOf(60);
            case "3.5" -> BigInteger.valueOf(70);
            case "4" -> BigInteger.valueOf(80);
            case "4.5" -> BigInteger.valueOf(90);
            case "5" -> BigInteger.valueOf(100);
            case "5.5" -> BigInteger.valueOf(110);
            case "6" -> BigInteger.valueOf(120);
            case "6.5" -> BigInteger.valueOf(130);
            case "7" -> BigInteger.valueOf(140);
            case "7.5" -> BigInteger.valueOf(150);
            case "8" -> BigInteger.valueOf(160);
            case "8.5" -> BigInteger.valueOf(170);
            case "9" -> BigInteger.valueOf(180);
            case "9.5" -> BigInteger.valueOf(190);
            case "10" -> BigInteger.valueOf(200);

            default -> BigInteger.valueOf(20); // Defaults to 1 if unrecognized
        };
    }

    // Creates a new paragraph in a given XWPFDocument
    public static XWPFParagraph createNewParagraph(XWPFDocument document) {
        return document.createParagraph();
    }

    // Creates a new run (a chunk of text with uniform properties) in a paragraph
    public static XWPFRun createNewRun(XWPFParagraph paragraph) {
        return paragraph.createRun();
    }

    // Copies styles and content from a source XWPFDocument to a target XWPFDocument
    public static XWPFDocument copyStylesAndContent(XWPFDocument source, XWPFDocument target) {
        for (XWPFParagraph paragraph : source.getParagraphs()) {
            XWPFParagraph newParagraph = target.createParagraph();
            newParagraph.getCTP().set(paragraph.getCTP().copy());

            for (XWPFRun run : paragraph.getRuns()) {
                XWPFRun newRun = newParagraph.createRun();
                newRun.getCTR().set(run.getCTR().copy());
                // Additional properties could be copied here as needed
            }
            // Additional paragraph-level properties could be copied here
        }
        return target;
    }

    // Calculates the heading size based on the given font size
    public static Integer getHeadingSize(Integer fontSize) {
        return fontSize + 2;
    }

    // Maps a string to a font style name
    public static String mapStringToFontStyle(String fontStyle) {
        return switch (fontStyle) {
            case "openSans" -> "Open Sans";
            case "comicSans" -> "Comic Sans MS";
            case "dyslexie" -> "Dyslexie";
            case "openDyslexic" -> "OpenDyslexic Bold";
            case "lexend" -> "Lexend";
            case "arial" -> "Arial";
            case "helvetica" -> "Helvetica";
            default -> "Open Sans"; // Defaults to Open Sans if unrecognized
        };
    }

    // Counts lines in a text based on punctuation and whitespace patterns
    public static String[] countLines(String text) {
        if (text == null)
            return new String[] {};
        Pattern re = Pattern.compile("(?<=[.!?])\\s+(?=[a-zA-Z0-9])", Pattern.MULTILINE | Pattern.COMMENTS);
        return re.split(text);
    }

    // Removes runs from a paragraph that don't contain embedded pictures
    public static void removeRuns(XWPFParagraph paragraph) {
        for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
            XWPFRun run = paragraph.getRuns().get(i);
            if (run.getEmbeddedPictures().isEmpty()) {
                paragraph.removeRun(i);
            }
        }
    }

    // Divides a large paragraph into smaller paragraphs based on the specified
    // number of lines
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

    // Retrieves the settings of a XWPFDocument through reflection
    public static XWPFSettings getSettings(XWPFDocument document) throws Exception {
        java.lang.reflect.Field settings = XWPFDocument.class.getDeclaredField("settings");
        settings.setAccessible(true);
        return (XWPFSettings) settings.get(document);
    }

    // Checks if a paragraph has a heading style
    public static boolean checkIfHeadingStylePresent(XWPFParagraph paragraph) {
        if (paragraph.getStyleID() != null && (paragraph.getStyleID().startsWith("Heading")))
            return true;
        if (paragraph.getStyleID() != null && (paragraph.getStyleID().startsWith("Title")))
            return true;
        if (!paragraph.getRuns().isEmpty()) {
            return paragraph.getRuns().get(0).isBold();
        }
        return false;
    }

    // Extracts headings from a document based on paragraph styles
    public static Optional<List<String>> extractHeadings(XWPFDocument document) {
        List<String> headings = new ArrayList<>();
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        paragraphs.stream().filter(ParsingUtils::checkIfHeadingStylePresent)
                .forEach(paragraph -> headings.add(paragraph.getRuns().get(0).toString()));
        return Optional.of(headings);
    }

    /*
     * Retrieves or creates a new CTPPr (paragraph properties) object for a given
     * paragraph
     */
    public static CTPPr getCTPPr(XWPFParagraph paragraph) {
        return paragraph.getCTP().isSetPPr() ? paragraph.getCTP().getPPr() : paragraph.getCTP().addNewPPr();
    }

    // Partitions a list of document IDs into sublists with a maximum size of 25
    public static List<List<String>> partitionList(List<String> documentIds) {
        return ListUtils.partition(documentIds, 25);

    }

    /*
     * public static void loadCustomFonts() {
     * String dyslexieFontPath = "src/main/resources/fonts/OpenDyslexic-Bold.otf";
     * GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
     * ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new
     * File(dyslexieFontPath)));
     * Optional<Font> b = Arrays.stream(ge.getAllFonts()).filter(font ->
     * font.getName().equals("OpenDyslexic")).findFirst();
     * Font[] f = ge.getAllFonts();
     * }
     */
    /*
     * Copies configuration settings from a source DocumentConfig to a target
     * DocumentConfig
     */
    public static void copyDocumentConfig(DocumentConfig source, DocumentConfig target) {
        // The following methods copy properties from source to target if they have
        // changed
        target.setFontColor(checkForFontParameterChange.test(source.getFontColor()) ? source.getFontColor()
                : target.getFontColor());
        target.setFontSize(
                checkForFontParameterChange.test(source.getFontSize()) ? source.getFontSize() : target.getFontSize());
        target.setFontType(
                checkForFontParameterChange.test(source.getFontType()) ? source.getFontType() : target.getFontType());
        target.setLineSpacing(checkForFontParameterChange.test(source.getLineSpacing()) ? source.getLineSpacing()
                : target.getLineSpacing());
        target.setAlignment(checkForFontParameterChange.test(source.getAlignment()) ? source.getAlignment()
                : target.getAlignment());
        target.setBackgroundColor(
                checkForFontParameterChange.test(source.getBackgroundColor()) ? source.getBackgroundColor()
                        : target.getBackgroundColor());
        target.setFontColor(checkForFontParameterChange.test(source.getFontColor()) ? source.getFontColor()
                : target.getFontColor());
        target.setRemoveItalics(
                checkForBooleanFontParameterChange.test(source.getRemoveItalics()) ? source.getRemoveItalics()
                        : target.getRemoveItalics());
        target.setBorderGeneration(
                checkForBooleanFontParameterChange.test(source.getBorderGeneration()) ? source.getBorderGeneration()
                        : target.getBorderGeneration());
        target.setParagraphSplitting(checkForBooleanFontParameterChange.test(source.getParagraphSplitting())
                ? source.getParagraphSplitting()
                : target.getParagraphSplitting());
        target.setHeaderGeneration(
                checkForBooleanFontParameterChange.test(source.getHeaderGeneration()) ? source.getHeaderGeneration()
                        : target.getHeaderGeneration());
        target.setGenerateTOC(
                checkForBooleanFontParameterChange.test(source.getGenerateTOC()) ? source.getGenerateTOC()
                        : target.getGenerateTOC());
        target.setSyllableSplitting(
                checkForBooleanFontParameterChange.test(source.getSyllableSplitting()) ? source.getSyllableSplitting()
                        : target.getSyllableSplitting());
        target.setHandlePunctuations(
                checkForBooleanFontParameterChange.test(source.getHandlePunctuations())
                        ? source.getHandlePunctuations()
                        : target.getHandlePunctuations());
        target.setCharacterSpacing(
                checkForFontParameterChange.test(source.getCharacterSpacing()) ? source.getCharacterSpacing()
                        : target.getCharacterSpacing());
    }

    public static Set<String> stopWords() {
        Set<String> stopWords = new HashSet<>();

        /*
         * Adding common English stop words to the set.
         * Stop words are commonly used words that are generally filtered out in natural
         * language processing
         */
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
