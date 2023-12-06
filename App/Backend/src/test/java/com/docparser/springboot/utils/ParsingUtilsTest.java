package com.docparser.springboot.utils;

import com.docparser.springboot.Repository.UserRepository;
import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.model.UserAccount;
import org.apache.commons.collections4.ListUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.Test;
import org.mockito.Mockito;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.awt.*;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

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
        assertFalse(ParsingUtils.checkForFontParameterChange.apply(null));
        // Test when formatConfig is empty
        assertFalse(ParsingUtils.checkForFontParameterChange.apply(""));
        // Test when formatConfig is not null and not empty
        assertTrue(ParsingUtils.checkForFontParameterChange.apply("someValue"));
    }

    @Test
   public void testCheckForBooleanFontParameterChange() {
        // Test when parameter is null
        assertFalse(ParsingUtils.checkForBooleanFontParameterChange.apply(null));
        // Test when parameter is not null
        assertTrue(ParsingUtils.checkForBooleanFontParameterChange.apply(true));
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