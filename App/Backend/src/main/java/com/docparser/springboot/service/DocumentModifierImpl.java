package com.docparser.springboot.service;

import com.docparser.springboot.errorhandler.FileParsingException;
import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.utils.ParsingUtils;
import lombok.AllArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@AllArgsConstructor
public class DocumentModifierImpl implements DocumentModifier {

    private final NLPService nlpService;

    public void modifyDocumentColor(XWPFDocument document, String color) {
        try {
            // Accessing private field ctSettings using reflection
            XWPFSettings settings = ParsingUtils.getSettings(document);
            java.lang.reflect.Field ctSetting = XWPFSettings.class.getDeclaredField("ctSettings");
            ctSetting.setAccessible(true);
            CTSettings ctSettings = (CTSettings) ctSetting.get(settings);

            // Enabling background shape display
            CTOnOff onOff = CTOnOff.Factory.newInstance();
            onOff.setVal(STOnOff.ON);
            ctSettings.setDisplayBackgroundShape(onOff);

            // Setting the background color
            CTBackground background = document.getDocument().addNewBackground();
            background.setColor(color);
        } catch (Exception e) {
            // Throwing an exception in case of failure
            throw new FileParsingException(e.getMessage());
        }
    }

    // Modifies headings in the document
    private void modifyEachHeading(String heading, XWPFParagraph paragraph) {
        XWPFRun run = ParsingUtils.createNewRun(paragraph);
        run.setText(heading);
        run.setFontFamily("Open Sans");
        run.setFontSize(16);
        run.setBold(true);
        run.addCarriageReturn();
    }

    // Adds a header to the document
    private void addHeader(XWPFDocument document, DocumentConfig formattingConfig) {
        List<String> docHeadings = new ArrayList<>();
        Set<String> stopWords = ParsingUtils.stopWords();

        // Looping through paragraphs to add headers
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (!paragraph.getRuns().isEmpty() && !paragraph.getParagraphText().isEmpty()) {
                if (ParsingUtils.checkIfHeadingStylePresent(paragraph)) {
                    docHeadings = new ArrayList<>();
                    docHeadings.add(paragraph.getParagraphText());
                    continue;
                }
                if (docHeadings.isEmpty()) {
                    XWPFRun run = paragraph.insertNewRun(0);
                    String headingText = nlpService.findMostCommonWord(paragraph.getParagraphText(), stopWords);
                    String fontType =ParsingUtils.checkForFontParameterChange.test(formattingConfig.getFontType()) ? formattingConfig.getFontType() : "Open Sans";
                    run.setFontFamily(fontType);
                    run.setText(headingText.toUpperCase());
                    run.addCarriageReturn();
                    run.setFontSize(16); // Set font size as needed
                    run.setBold(true);
                }
            }
        }

    }

    // Creates a Table of Contents for the document
    private XWPFParagraph createTableOfContents(List<String> headings, XWPFDocument document) {
        XWPFParagraph tocParagraph = ParsingUtils.createNewParagraph(document);
        tocParagraph.setPageBreak(true);
        tocParagraph.setAlignment(ParagraphAlignment.CENTER);
        tocParagraph.setStyle("Heading1");
        XWPFRun tocRun = tocParagraph.createRun();
        tocRun.setText("Table of Contents");
        tocRun.setFontFamily("Open Sans");
        tocRun.addBreak();
        tocRun.setFontSize(18);
        tocRun.setBold(true);

        // Adding each heading to the Table of Contents
        headings.stream().forEach(heading -> modifyEachHeading(heading, tocParagraph));
        return tocParagraph;
    }

    // Modifies the document to include a Table of Contents
    public void modifyDocumentToc(XWPFDocument document) {
        Optional<List<String>> headings = ParsingUtils.extractHeadings(document);
        if (headings.isPresent()) {
            XWPFParagraph newParagraph = createTableOfContents(headings.get(), document);
            // Move this paragraph to the beginning of the document
            CTBody body = document.getDocument().getBody();
            CTP newParagraphCtp = newParagraph.getCTP();
            body.insertNewP(0);
            CTP firstParagraph = body.getPArray(0);
            firstParagraph.set(newParagraphCtp);
            // Remove the duplicate (the original new paragraph at the end)
            body.removeP(body.sizeOfPArray() - 1);
        }
    }


    // Main method to modify the document
    @Override
    public XWPFDocument modify(XWPFDocument document, DocumentConfig formattingConfig) {

        // Applying various modifications based on formattingConfig
        if (ParsingUtils.checkForFontParameterChange.test(formattingConfig.getBackgroundColor())) {
            modifyDocumentColor(document, formattingConfig.getBackgroundColor());
        }
        if (ParsingUtils.checkForBooleanFontParameterChange.test(formattingConfig.getHeaderGeneration()) && formattingConfig.getHeaderGeneration().equals(Boolean.TRUE))
            addHeader(document,formattingConfig);
        if (ParsingUtils.checkForBooleanFontParameterChange.test(formattingConfig.getGenerateTOC())
                && formattingConfig.getGenerateTOC().equals(Boolean.TRUE)) {
            modifyDocumentToc(document);
        }
        return document;

    }
}
