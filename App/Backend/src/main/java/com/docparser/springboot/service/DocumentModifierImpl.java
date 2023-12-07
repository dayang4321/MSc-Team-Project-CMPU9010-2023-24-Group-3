package com.docparser.springboot.service;


import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.utils.ParsingUtils;
import lombok.AllArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@AllArgsConstructor
public class DocumentModifierImpl implements DocumentModifier {

    private final NLPService nlpService;

    public void modifyDocumentColor(XWPFDocument document, String color) {
        try {
            XWPFSettings settings = ParsingUtils.getSettings(document);
            java.lang.reflect.Field _ctSettings = XWPFSettings.class.getDeclaredField("ctSettings");
            _ctSettings.setAccessible(true);
            CTSettings ctSettings = (CTSettings) _ctSettings.get(settings);
            CTOnOff onOff = CTOnOff.Factory.newInstance();
            onOff.setVal(STOnOff.ON);
            ctSettings.setDisplayBackgroundShape(onOff);
            CTBackground background = document.getDocument().addNewBackground();
            background.setColor(color);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void modifyEachHeading(String heading, XWPFParagraph paragraph) {
        XWPFRun run = ParsingUtils.createNewRun(paragraph);
        run.setText(heading);
        run.setFontFamily("Open Sans");
        run.setFontSize(16);
        run.setBold(true);
        run.addCarriageReturn();
    }

    private void modifyImage(XWPFDocument document) {
        XWPFParagraph targetParagraph = null;
        XWPFRun imageRun = null;
        int runIndex = 0;
        for (XWPFParagraph p : document.getParagraphs()) {
            for (XWPFRun run : p.getRuns()) {
                runIndex = p.getRuns().indexOf(run);

                if (!run.getEmbeddedPictures().isEmpty()) {
                    targetParagraph = p;
                    imageRun = run;
                    break;
                }
            }
            if (targetParagraph != null) {
                break;
            }
        }
        if (targetParagraph != null && imageRun != null) {
            XWPFRun labelRun = targetParagraph.createRun();
            labelRun.setText("Figure 1: This is an image label.");
        }
    }

    private void addHeader(XWPFDocument document) {
        List<String> docHeadings = new ArrayList<>();
        Set<String> stopWords = ParsingUtils.stopWords();
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
                    run.setText(headingText.toUpperCase());
                    run.addCarriageReturn();
                    run.setFontSize(16); // Set font size as needed
                    run.setBold(true);
                }
            }
        }


    }

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
        headings.stream().forEach(heading -> modifyEachHeading(heading, tocParagraph));
        // modifyParagraph.accept(tocParagraph, formattingConfig);
        return tocParagraph;
    }

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

    private void addNewText(XWPFRun run, String para) {
        run.setText(para);
        run.addCarriageReturn();
    }


    private XWPFDocument modifyText(XWPFDocument document, XWPFDocument finalDoc) {
        finalDoc = ParsingUtils.copyStylesAndContent(document, finalDoc);
        document.getBodyElements();
        for (int i = 0; i < document.getParagraphs().size(); i++) {
            XWPFParagraph paragraph = document.getParagraphs().get(i);
            String text = paragraph.getParagraphText();
            if (ParsingUtils.countLines(text).length >= 3) {
                String[] paras = ParsingUtils.divideParagraph(text, 3);
                finalDoc.removeBodyElement(i);
                XmlCursor cursor = finalDoc.getParagraphArray(i).getCTP().newCursor();
                for (String para : paras) {
                    XWPFParagraph newParagraph = finalDoc.insertNewParagraph(cursor);
                    addNewText(newParagraph.createRun(), para);
                    cursor = newParagraph.getCTP().newCursor();
                }
            }
        }
        /*
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            String text = paragraph.getParagraphText();
            if (ParsingUtils.countLines(text).length >= 3) {
                String[] paras = ParsingUtils.divideParagraph(text, 3);
                for (String para : paras) {
                    XWPFParagraph newParagraph = finalDoc.createParagraph();

                    if (newParagraph != null) {
                        addNewText(newParagraph.createRun(), para);
                    }

                }
            }
        }*/
        return finalDoc;
    }


    @Override
    public XWPFDocument modify(XWPFDocument document, DocumentConfig formattingConfig) {

        if (ParsingUtils.checkForFontParameterChange.apply(formattingConfig.getBackgroundColor())) {
            modifyDocumentColor(document, formattingConfig.getBackgroundColor());
        }
        if (ParsingUtils.checkForBooleanFontParameterChange.apply(formattingConfig.getHeaderGeneration()) && formattingConfig.getHeaderGeneration())
            addHeader(document);
        if (ParsingUtils.checkForBooleanFontParameterChange.apply(formattingConfig.getGenerateTOC()) && formattingConfig.getGenerateTOC()) {
            modifyDocumentToc(document);
        }
        return document;

    }

}
