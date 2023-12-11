package com.docparser.springboot.utils;

import org.apache.poi.xwpf.usermodel.*;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ParsingUtilsTest {
    @Test
    public void testGetParagraphsInTheDocument() {
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph paragraph1 = document.createParagraph();
        paragraph1.createRun().setText("Paragraph 1");
        XWPFParagraph paragraph2 = document.createParagraph();
        paragraph2.createRun().setText("Paragraph 2");
        List<String> paragraphs = ParsingUtils.getParagraphsInTheDocument(document);

        assertEquals(2, paragraphs.size());
        assertEquals("Paragraph 1", paragraphs.get(0));
        assertEquals("Paragraph 2", paragraphs.get(1));
    }
    @Test
    public void testCheckForFontParameterChange() {
        // Test when formatConfig is null
        assertFalse(ParsingUtils.checkForFontParameterChange.test(null));
        // Test when formatConfig is empty
        assertFalse(ParsingUtils.checkForFontParameterChange.test(""));
        // Test when formatConfig is not null and not empty
        assertTrue(ParsingUtils.checkForFontParameterChange.test("someValue"));
    }

    @Test
   public void testCheckForBooleanFontParameterChange() {
        // Test when parameter is null
        assertFalse(ParsingUtils.checkForBooleanFontParameterChange.test(null));
        // Test when parameter is not null
        assertTrue(ParsingUtils.checkForBooleanFontParameterChange.test(true));
    }
    @Test
   public void testCountLines() {
        // Test when input text is null
        assertArrayEquals(new String[]{}, ParsingUtils.countLines(null));

        // Test when input text is empty
        assertArrayEquals(new String[]{""}, ParsingUtils.countLines(""));

        // Test when there are no sentence-ending punctuation marks
        assertArrayEquals(new String[]{"This is a single line without punctuation."}, ParsingUtils.countLines("This is a single line without punctuation."));

        // Test when there are multiple lines separated by periods
        assertArrayEquals(new String[]{"This is line 1.", "This is line 2.", "This is line 3."},
                ParsingUtils.countLines("This is line 1. This is line 2. This is line 3."));

        // Test when there are multiple lines separated by different sentence-ending punctuation marks
        assertArrayEquals(new String[]{"Line 1?", "Line 2!", "Line 3."},
                ParsingUtils.countLines("Line 1? Line 2! Line 3."));
    }

}